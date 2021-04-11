package com.ruoyi.cms.controller;

import com.aiolos.common.utils.PagedResult;
import com.ruoyi.cms.domain.Tags;
import com.ruoyi.cms.domain.bo.BookBO;
import com.ruoyi.cms.domain.vo.BookVO;
import com.ruoyi.cms.service.IBookService;
import com.ruoyi.cms.service.ITagsService;
import com.ruoyi.cms.util.CmsConstants;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 文章管理Controller
 * 
 * @author wujiyue
 * @date 2019-10-28
 */
@Controller
@RequestMapping("/cms/book")
public class BookController extends BaseController
{
    private String prefix = "cms/book";

    @Autowired
    private IBookService bookService;
    @Autowired
    private ITagsService tagsService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RestTemplate restTemplate;

    private String getEditorType(){
        return configService.selectConfigByKey(CmsConstants.KEY_EDITOR_TYPE);
    }

    @RequiresPermissions("cms:book:view")
    @GetMapping()
    public String book()
    {
        return prefix + "/book";
    }

    /**
     * 查询书籍管理列表
     */
    @RequiresPermissions("cms:book:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BookVO book)
    {
        PagedResult bodyResult;
        try {
            PageDomain pageDomain = TableSupport.buildPageRequest();
            Integer pageNum = pageDomain.getPageNum();
            Integer pageSize = pageDomain.getPageSize();
            String bookServerUrlExecute = "http://127.0.0.1:7070/library-book/book/getAllBooks?page=" + pageNum + "&pageSize=" + pageSize;

            if (StringUtils.isNotBlank(book.getName())) {
                bookServerUrlExecute += "&keyword=" + book.getName();
            }
            if (StringUtils.isNotNull(book.getCategory())) {
                bookServerUrlExecute += "&category=" + book.getCategory();
            }
            if (StringUtils.isNotNull(book.getStatus())) {
                bookServerUrlExecute += "&status=" + book.getStatus();
            }
            ResponseEntity<PagedResult> responseEntity = restTemplate.getForEntity(bookServerUrlExecute, PagedResult.class);
            bodyResult = responseEntity.getBody();
        } catch (ResourceAccessException e) {
            throw new BusinessException("图书管理系统未启动");
        }
        return new TableDataInfo(bodyResult.getRecords(), Long.valueOf(bodyResult.getTotal()).intValue());
    }

    /**
     * 新增书籍管理
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        List<Tags> tags=tagsService.selectTagsAll();
        mmap.put("tags",tags);

        String editorType = getEditorType();
        if(CmsConstants.EDITOR_TYPE_EDITORMD.equals(editorType)){
            return prefix + "/add_editormd";
        }else{
            return prefix + "/add";
        }
    }

    /**
     * 新增保存书籍管理
     */
    @RequiresPermissions("cms:book:add")
    @Log(title = "新增书籍", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BookVO book)
    {
        return toAjax(bookService.insertBook(book));
    }

    /**
     * 修改书籍管理
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BookBO book = bookService.selectBookById(id);
        mmap.put("book", book);
        return prefix + "/edit";
    }

    /**
     * 修改保存文章管理
     */
    @RequiresPermissions("cms:book:edit")
    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BookVO book)
    {
        return toAjax(bookService.updateBook(book));
    }

    /**
     * 删除书籍
     */
    @RequiresPermissions("cms:book:remove")
    @Log(title = "书籍管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(bookService.deleteBookByIds(ids));
    }
}
