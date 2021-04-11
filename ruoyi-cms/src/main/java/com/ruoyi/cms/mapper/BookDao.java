package com.ruoyi.cms.mapper;

import com.ruoyi.cms.domain.bo.BookBO;
import com.ruoyi.cms.domain.vo.BookVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BookDao {
    void insertBook(Map<String, Object> map);

    int insert(BookVO book);

    Integer selectClassificationByCategory(Integer category);

    Integer selectCategoryById(Integer categoryId);

    BookBO selectBookById(Long id);

    int updateById(BookVO book);

    List<Long> selectBookByIds(List<Long> ids);

    int deleteBookByIds(@Param("ids") List<Long> ids, @Param("status") Integer status);
}