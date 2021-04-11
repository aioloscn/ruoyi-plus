package com.ruoyi.cms.service;

import com.ruoyi.cms.domain.bo.BookBO;
import com.ruoyi.cms.domain.vo.BookVO;

/**
 * 文章管理Service接口
 * 
 * @author wujiyue
 * @date 2019-10-28
 */
public interface IBookService
{

    /**
     * 新增书籍管理
     * 
     * @param book 书籍管理
     * @return 结果
     */
    public int insertBook(BookVO book);

    BookBO selectBookById(Long id);

    int updateBook(BookVO book);

    /**
     * 逻辑删除书籍
     * @param ids
     * @return
     */
    int deleteBookByIds(String ids);
}
