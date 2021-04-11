package com.ruoyi.cms.service.impl;

import java.util.*;

import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.BaseService;
import com.ruoyi.system.utils.RedisDBChangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.ruoyi.cms.mapper.CategoryMapper;
import com.ruoyi.cms.domain.Category;
import com.ruoyi.cms.service.ICategoryService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 栏目分类Service业务层处理
 * 
 * @author wujiyue
 * @date 2019-10-28
 */
@Service
public class CategoryServiceImpl extends BaseService implements ICategoryService
{
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private RedisDBChangeUtil redisDBChangeUtil;

    /**
     * 查询栏目分类
     * 
     * @param categoryId 栏目分类ID
     * @return 栏目分类
     */
    @Override
    public Category selectCategoryById(Long categoryId)
    {
        return categoryMapper.selectCategoryById(categoryId);
    }

    /**
     * 查询栏目分类列表
     * 
     * @param category 栏目分类
     * @return 栏目分类
     */
    @Override
    public List<Category> selectCategoryList(Category category)
    {
        return categoryMapper.selectCategoryList(category);
    }

    /**
     * 新增栏目分类
     * 
     * @param category 栏目分类
     * @return 结果
     */
    @Override
    @Transactional
    public int insertCategory(Category category)
    {
        category.setCreateTime(DateUtils.getNowDate());
        SysUser user=ShiroUtils.getSysUser();
        category.setCreateBy(user.getUserId().toString());
        int n=categoryMapper.insertCategory(category);
        //更新parentids
        Long pid=category.getParentId();
        Category p=categoryMapper.selectCategoryById(pid);
        String ancestors="";
        if(p!=null){
            ancestors = p.getAncestors();
            ancestors += category.getCategoryId()+",";
        }else{
            ancestors=category.getCategoryId()+",";
        }
        category.setAncestors(ancestors);
        n=categoryMapper.updateCategory(category);
        return n;
    }

    /**
     * 修改栏目分类
     * 
     * @param category 栏目分类
     * @return 结果
     */
    @Override
    public int updateCategory(Category category)
    {
        category.setUpdateTime(DateUtils.getNowDate());
        SysUser user=ShiroUtils.getSysUser();
        category.setUpdateBy(user.getUserId().toString());
        return categoryMapper.updateCategory(category);
    }

    /**
     * 删除栏目分类对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteCategoryByIds(String ids)
    {
        return categoryMapper.deleteCategoryByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除栏目分类信息
     * 
     * @param categoryId 栏目分类ID
     * @return 结果
     */
    @Override
    public int deleteCategoryById(String categoryId)
    {
        return categoryMapper.deleteCategoryById(categoryId);
    }

    /**
     * 查询栏目分类树列表
     * 
     * @return 所有栏目分类信息
     */
    @Override
    public List<Ztree> selectCategoryTree()
    {
        List<Category> categoryList = categoryMapper.selectCategoryList(new Category());
        List<Ztree> ztrees = new ArrayList<Ztree>();
        for (Category category : categoryList)
        {
            Ztree ztree = new Ztree();
            ztree.setId(category.getCategoryId());
            ztree.setpId(category.getParentId());
            ztree.setName(category.getCategoryName());
            ztree.setTitle(category.getCategoryName());
            ztrees.add(ztree);
        }
        return ztrees;
    }

    @Override
    public List<Category> selectNavCategories(Category category) {
        return categoryMapper.selectNavCategories(category);
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = BusinessException.class)
    @Override
    public void syncCategoryFromRedis(String loginName) {
        redisDBChangeUtil.setDatabase(3);
        Set<ZSetOperations.TypedTuple<String>> classificationZSet = redis.opsForZSet().rangeByScoreWithScores(CATEGORY_ZSET, 0, MAXIMUM_RANGE_OF_CATEGORY_ZSET);
        Iterator<ZSetOperations.TypedTuple<String>> classificationIterator = classificationZSet.iterator();

        if (!classificationIterator.hasNext()) {
            throw new BusinessException("没有查询到字典数据");
        }

        List<Category> categories = new LinkedList<>();
        while (classificationIterator.hasNext()) {

            ZSetOperations.TypedTuple<String> nextClassification = classificationIterator.next();
            Category classification = new Category();
            String classificationValue = nextClassification.getValue();
            Integer classificationScore = nextClassification.getScore().intValue();
            classification.setCategoryName(classificationValue);
            classification.setParent(null);
            classification.setValue(classificationScore);
            classification.setSort(classificationScore);
            classification.setDescription(CATEGORY_ZSET);
            classification.setCreateBy(loginName);
            classification.setUpdateBy(loginName);
            classification.setCreateTime(new Date());
            classification.setUpdateTime(new Date());
            // 插入大类数据并返回主键
            int result = categoryMapper.insertCategory(classification);
            if (result != 1) {
                try {
                    throw new RuntimeException();
                } catch (Exception e) {
                    throw new BusinessException("同步书籍类目失败");
                }
            }

            Integer suffix = nextClassification.getScore().intValue();
            Set<ZSetOperations.TypedTuple<String>> categoryZSet = redis.opsForZSet().rangeByScoreWithScores(CATEGORY_ZSET + ":" + suffix, 0, MAXIMUM_RANGE_OF_CATEGORY_ZSET);
            Iterator<ZSetOperations.TypedTuple<String>> categoryIterator = categoryZSet.iterator();

            while (categoryIterator.hasNext()) {

                ZSetOperations.TypedTuple<String> nextCategory = categoryIterator.next();
                Category category = new Category();
                String categoryValue = nextCategory.getValue();
                Integer categoryScore = nextCategory.getScore().intValue();
                category.setCategoryName(categoryValue);
                category.setParentId(classification.getCategoryId());
                category.setValue(categoryScore);
                category.setSort(categoryScore);
                category.setDescription(CATEGORY_ZSET + ":" + suffix);
                category.setCreateBy(loginName);
                category.setUpdateBy(loginName);
                category.setCreateTime(new Date());
                category.setUpdateTime(new Date());
                categories.add(category);
            }
        }

        int resultCount = categoryMapper.insertAll(categories);
    }

    @Override
    public void syncCategoryToRedis() {
        redisDBChangeUtil.setDatabase(3);
        Category category = new Category();
        category.setDescription("sys_book_category");
        List<Category> sysDictDataList = categoryMapper.selectCategoryList(category);
        sysDictDataList.forEach(v -> {
            Boolean result = redis.opsForZSet().add(v.getDescription(), v.getCategoryName(), v.getSort());
        });
    }
}
