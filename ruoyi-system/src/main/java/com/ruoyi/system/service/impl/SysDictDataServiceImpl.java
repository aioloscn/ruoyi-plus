package com.ruoyi.system.service.impl;

import java.util.*;

import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.system.service.BaseService;
import com.ruoyi.system.utils.RedisDBChangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.ISysDictDataService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字典 业务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysDictDataServiceImpl extends BaseService implements ISysDictDataService
{
    @Autowired
    private SysDictDataMapper sysDictDataMapper;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private RedisDBChangeUtil redisDBChangeUtil;

    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData)
    {
        return sysDictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType)
    {
        return sysDictDataMapper.selectDictDataByType(dictType);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue)
    {
        return sysDictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode)
    {
        return sysDictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 通过字典ID删除字典数据信息
     * 
     * @param dictCode 字典数据ID
     * @return 结果
     */
    @Override
    public int deleteDictDataById(Long dictCode)
    {
        return sysDictDataMapper.deleteDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据
     * 
     * @param ids 需要删除的数据
     * @return 结果
     */
    @Override
    public int deleteDictDataByIds(String ids)
    {
        return sysDictDataMapper.deleteDictDataByIds(Convert.toStrArray(ids));
    }

    /**
     * 新增保存字典数据信息
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData dictData)
    {
        return sysDictDataMapper.insertDictData(dictData);
    }

    /**
     * 修改保存字典数据信息
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData dictData)
    {
        return sysDictDataMapper.updateDictData(dictData);
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = BusinessException.class)
    @Override
    public void syncClassificationFromRedis(String loginName, String dictType) {

        redisDBChangeUtil.setDatabase(3);
        Set<ZSetOperations.TypedTuple<String>> classificationZSet = redis.opsForZSet().rangeByScoreWithScores(CATEGORY_ZSET, 0, MAXIMUM_RANGE_OF_CATEGORY_ZSET);
        Iterator<ZSetOperations.TypedTuple<String>> iterator = classificationZSet.iterator();

        if (!iterator.hasNext()) {
            throw new BusinessException("没有查询到字典数据");
        }

        List<SysDictData> sysDictDataList = new LinkedList<>();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<String> next = iterator.next();
            SysDictData sysDictData = new SysDictData();
            String value = next.getValue();
            Integer score = next.getScore().intValue();
            sysDictData.setDictSort(score.longValue());
            sysDictData.setDictLabel(value);
            sysDictData.setDictValue(String.valueOf(score));
            sysDictData.setDictType(dictType);
            sysDictData.setRemark(CATEGORY_ZSET);
            sysDictData.setCreateBy(loginName);
            sysDictData.setUpdateBy(loginName);
            sysDictData.setCreateTime(new Date());
            sysDictData.setUpdateTime(new Date());
            sysDictDataList.add(sysDictData);
        }
        int resultCount = sysDictDataMapper.insertAll(sysDictDataList);
        // insert会忽略掉已存在的，所以不一定返回值 > 0
    }

    @Override
    public void syncClassificationToRedis(String dictType) {
        redisDBChangeUtil.setDatabase(3);
        List<SysDictData> sysDictDataList = sysDictDataMapper.selectDictDataByType(dictType);
        sysDictDataList.forEach(v -> {
            // 如果已存在，更新成功依然返回false
            Boolean result = redis.opsForZSet().add(v.getRemark(), v.getDictLabel(), Double.valueOf(v.getDictValue()));
        });
    }

    @Override
    public void syncCategoryFromRedis(String loginName, String dictType) {
        redisDBChangeUtil.setDatabase(3);
        Set<ZSetOperations.TypedTuple<String>> classificationZSet = redis.opsForZSet().rangeByScoreWithScores(CATEGORY_ZSET, 0, MAXIMUM_RANGE_OF_CATEGORY_ZSET);
        Iterator<ZSetOperations.TypedTuple<String>> classificationIterator = classificationZSet.iterator();

        if (!classificationIterator.hasNext()) {
            throw new BusinessException("没有查询到字典数据");
        }

        List<SysDictData> sysDictDataList = new LinkedList<>();
        while (classificationIterator.hasNext()) {

            ZSetOperations.TypedTuple<String> classification = classificationIterator.next();
            Integer suffix = classification.getScore().intValue();
            Set<ZSetOperations.TypedTuple<String>> categoryZSet = redis.opsForZSet().rangeByScoreWithScores(CATEGORY_ZSET + ":" + suffix, 0, MAXIMUM_RANGE_OF_CATEGORY_ZSET);
            Iterator<ZSetOperations.TypedTuple<String>> categoryIterator = categoryZSet.iterator();

            while (categoryIterator.hasNext()) {

                ZSetOperations.TypedTuple<String> category = categoryIterator.next();
                SysDictData sysDictData = new SysDictData();
                String value = category.getValue();
                Integer score = category.getScore().intValue();
                sysDictData.setDictSort(score.longValue());
                sysDictData.setDictLabel(value);
                sysDictData.setDictValue(String.valueOf(score));
                sysDictData.setDictType(dictType);
                sysDictData.setRemark(CATEGORY_ZSET + ":" + suffix);
                sysDictData.setCreateBy(loginName);
                sysDictData.setUpdateBy(loginName);
                sysDictData.setCreateTime(new Date());
                sysDictData.setUpdateTime(new Date());
                sysDictDataList.add(sysDictData);
            }
        }

        int resultCount = sysDictDataMapper.insertAll(sysDictDataList);
    }
}
