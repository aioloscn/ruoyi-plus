package com.ruoyi.cms.service.impl;

import com.aiolos.common.enums.BookStatus;
import com.ruoyi.cms.domain.Category;
import com.ruoyi.cms.domain.bo.BookBO;
import com.ruoyi.cms.domain.vo.BookVO;
import com.ruoyi.cms.mapper.BookDao;
import com.ruoyi.cms.mapper.CategoryMapper;
import com.ruoyi.cms.service.BaseService;
import com.ruoyi.cms.service.IBookService;
import com.ruoyi.cms.util.IdGeneratorSnowflake;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 书籍管理Service业务层处理
 * 
 * @author wujiyue
 * @date 2019-10-28
 */
@Service
public class BookServiceImpl extends BaseService implements IBookService
{
    private final BookDao bookDao;
    private final CategoryMapper categoryMapper;
    private final IdGeneratorSnowflake idWorker;

    public BookServiceImpl(BookDao bookDao, CategoryMapper categoryMapper, IdGeneratorSnowflake idWorker) {
        this.bookDao = bookDao;
        this.categoryMapper = categoryMapper;
        this.idWorker = idWorker;
    }

    /**
     * 新增书籍管理
     * 
     * @param book 书籍管理
     * @return 结果
     */
    @Override
    @Transactional(propagation = Propagation.NESTED, rollbackFor = BusinessException.class)
    public int insertBook(BookVO book)
    {
        book.setId(idWorker.nextId());
        book.setSalesVolume(0);
        book.setExcerpt(StringUtils.EMPTY);

        // 可能是一级类目也可能是二级类目
        Integer categoryId = book.getCategory();
        Integer category = bookDao.selectCategoryById(categoryId);
        Integer classification = bookDao.selectClassificationByCategory(book.getCategory());
        if (classification == null) {
            book.setClassification(category);
            book.setCategory(0);
        } else {
            book.setClassification(classification);
            book.setCategory(category);
        }

        BigDecimal sellingPrice = book.getSellingPrice();// 售价
        BigDecimal originalPrice = book.getOriginalPrice();// 原价
        BigDecimal discount = sellingPrice.multiply(new BigDecimal(1000)).divide(originalPrice, BigDecimal.ROUND_DOWN);
        book.setDiscount(discount.intValue());
        book.setGmtCreate(new Date());
        book.setGmtModified(new Date());
        int resultCount = bookDao.insert(book);
        if (resultCount != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new BusinessException("更新失败");
            }
        }
        return resultCount;
    }

    @Override
    public BookBO selectBookById(Long id) {

        BookBO book = bookDao.selectBookById(id);

        // 根据classification和category到cms_category表查找对应的值
        Integer classification = book.getClassification();
        Integer category = book.getCategory();
        if (classification != null) {
            Category categoryPojo = categoryMapper.selectCategoryByValue(CATEGORY_ZSET, classification);
            book.setClassification(categoryPojo.getCategoryId().intValue());
        }
        if (category != null) {
            Category categoryPojo = categoryMapper.selectCategoryByValue(CATEGORY_ZSET + ":" + classification, category);
            book.setCategory(categoryPojo.getCategoryId().intValue());
            book.setCategoryName(categoryPojo.getCategoryName());
        }
        return book;
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = BusinessException.class)
    @Override
    public int updateBook(BookVO book) {

        if (StringUtils.isBlank(book.getExcerpt())) {
            book.setExcerpt(StringUtils.EMPTY);
        }

        // 可能是一级类目也可能是二级类目
        Integer categoryId = book.getCategory();
        Integer category = bookDao.selectCategoryById(categoryId);
        Integer classification = bookDao.selectClassificationByCategory(book.getCategory());
        if (classification == null) {
            book.setClassification(category);
            book.setCategory(0);
        } else {
            book.setClassification(classification);
            book.setCategory(category);
        }

        BigDecimal sellingPrice = book.getSellingPrice();// 售价
        BigDecimal originalPrice = book.getOriginalPrice();// 原价
        BigDecimal discount = sellingPrice.multiply(new BigDecimal(1000)).divide(originalPrice, BigDecimal.ROUND_DOWN);
        book.setDiscount(discount.intValue());
        book.setGmtModified(new Date());
        int resultCount = bookDao.updateById(book);
        if (resultCount != 1) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new BusinessException("更新失败");
            }
        }
        return resultCount;
    }

    @Transactional(propagation = Propagation.NESTED, rollbackFor = BusinessException.class)
    @Override
    public int deleteBookByIds(String ids) {
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(s -> {
            return Long.parseLong(s.toString());
        }).collect(Collectors.toList());
        List<Long> idResults = bookDao.selectBookByIds(idList);
        List<Long> notExistsIds = new LinkedList<>();
        idList.forEach(id -> {
            if (!idResults.contains(id)) {
                notExistsIds.add(id);
            }
        });

        int resultCount = bookDao.deleteBookByIds(idResults, BookStatus.DELETED.getType());
        if (resultCount != idResults.size()) {
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                throw new BusinessException("删除失败");
            }
        }

        if (notExistsIds.size() > 0) {
            throw new BusinessException(notExistsIds.toString() + "不存在");
        }
        return resultCount;
    }

}
