package com.ruoyi.cms.domain.bo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BookBO implements Serializable {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private Long id;

    /**
     * 书名
     */
    private String name;

    /**
     * 封面
     */
    @Column(name = "pic_url")
    private String picUrl;

    /**
     * 进货价格
     */
    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    /**
     * 进货数量
     */
    @Column(name = "purchase_num")
    private Integer purchaseNum;

    /**
     * 售价
     */
    @Column(name = "selling_price")
    private BigDecimal sellingPrice;

    /**
     * 销售量
     */
    @Column(name = "sales_volume")
    private Integer salesVolume;

    /**
     * 折扣: discount / 1000为实际折扣，discount / 100为显示的折扣
     */
    private Integer discount;

    /**
     * 原价
     */
    @Column(name = "original_price")
    private BigDecimal originalPrice;

    /**
     * 目录
     */
    private String catalog;

    /**
     * 作者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 其他信息
     */
    @Column(name = "other_infor")
    private String otherInfor;

    /**
     * 书籍大分类，可能不使用全部放在小分类里
     */
    private Integer classification;

    /**
     * 书籍小分类
     */
    private Integer category;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 状态: 0: 已删除, 1: 已上架: 2: 未上架
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 内容简介
     */
    private String brief;

    /**
     * 前言
     */
    private String preface;

    /**
     * 节选
     */
    private String excerpt;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取书名
     *
     * @return name - 书名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置书名
     *
     * @param name 书名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取封面
     *
     * @return pic_url - 封面
     */
    public String getPicUrl() {
        return picUrl;
    }

    /**
     * 设置封面
     *
     * @param picUrl 封面
     */
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl == null ? null : picUrl.trim();
    }

    /**
     * 获取进货价格
     *
     * @return purchase_price - 进货价格
     */
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * 设置进货价格
     *
     * @param purchasePrice 进货价格
     */
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /**
     * 获取进货数量
     *
     * @return purchase_num - 进货数量
     */
    public Integer getPurchaseNum() {
        return purchaseNum;
    }

    /**
     * 设置进货数量
     *
     * @param purchaseNum 进货数量
     */
    public void setPurchaseNum(Integer purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    /**
     * 获取售价
     *
     * @return selling_price - 售价
     */
    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    /**
     * 设置售价
     *
     * @param sellingPrice 售价
     */
    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    /**
     * 获取销售量
     *
     * @return sales_volume - 销售量
     */
    public Integer getSalesVolume() {
        return salesVolume;
    }

    /**
     * 设置销售量
     *
     * @param salesVolume 销售量
     */
    public void setSalesVolume(Integer salesVolume) {
        this.salesVolume = salesVolume;
    }

    /**
     * 获取折扣: discount / 1000为实际折扣，discount / 100为显示的折扣
     *
     * @return discount - 折扣: discount / 1000为实际折扣，discount / 100为显示的折扣
     */
    public Integer getDiscount() {
        return discount;
    }

    /**
     * 设置折扣: discount / 1000为实际折扣，discount / 100为显示的折扣
     *
     * @param discount 折扣: discount / 1000为实际折扣，discount / 100为显示的折扣
     */
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    /**
     * 获取原价
     *
     * @return original_price - 原价
     */
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    /**
     * 设置原价
     *
     * @param originalPrice 原价
     */
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    /**
     * 获取目录
     *
     * @return catalog - 目录
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * 设置目录
     *
     * @param catalog 目录
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog == null ? null : catalog.trim();
    }

    /**
     * 获取作者
     *
     * @return author - 作者
     */
    public String getAuthor() {
        return author;
    }

    /**
     * 设置作者
     *
     * @param author 作者
     */
    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    /**
     * 获取出版社
     *
     * @return publisher - 出版社
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * 设置出版社
     *
     * @param publisher 出版社
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher == null ? null : publisher.trim();
    }

    /**
     * 获取其他信息
     *
     * @return other_infor - 其他信息
     */
    public String getOtherInfor() {
        return otherInfor;
    }

    /**
     * 设置其他信息
     *
     * @param otherInfor 其他信息
     */
    public void setOtherInfor(String otherInfor) {
        this.otherInfor = otherInfor == null ? null : otherInfor.trim();
    }

    /**
     * 获取书籍大分类，可能不使用全部放在小分类里
     *
     * @return classification - 书籍大分类，可能不使用全部放在小分类里
     */
    public Integer getClassification() {
        return classification;
    }

    /**
     * 设置书籍大分类，可能不使用全部放在小分类里
     *
     * @param classification 书籍大分类，可能不使用全部放在小分类里
     */
    public void setClassification(Integer classification) {
        this.classification = classification;
    }

    /**
     * 获取书籍小分类
     *
     * @return category - 书籍小分类
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * 设置书籍小分类
     *
     * @param category 书籍小分类
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 获取状态: 0: 已删除, 1: 已上架: 2: 未上架
     *
     * @return status - 状态: 0: 已删除, 1: 已上架: 2: 未上架
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态: 0: 已删除, 1: 已上架: 2: 未上架
     *
     * @param status 状态: 0: 已删除, 1: 已上架: 2: 未上架
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取创建时间
     *
     * @return gmt_create - 创建时间
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 设置创建时间
     *
     * @param gmtCreate 创建时间
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 获取修改时间
     *
     * @return gmt_modified - 修改时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置修改时间
     *
     * @param gmtModified 修改时间
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * 获取内容简介
     *
     * @return brief - 内容简介
     */
    public String getBrief() {
        return brief;
    }

    /**
     * 设置内容简介
     *
     * @param brief 内容简介
     */
    public void setBrief(String brief) {
        this.brief = brief == null ? null : brief.trim();
    }

    /**
     * 获取前言
     *
     * @return preface - 前言
     */
    public String getPreface() {
        return preface;
    }

    /**
     * 设置前言
     *
     * @param preface 前言
     */
    public void setPreface(String preface) {
        this.preface = preface == null ? null : preface.trim();
    }

    /**
     * 获取节选
     *
     * @return excerpt - 节选
     */
    public String getExcerpt() {
        return excerpt;
    }

    /**
     * 设置节选
     *
     * @param excerpt 节选
     */
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt == null ? null : excerpt.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", picUrl=").append(picUrl);
        sb.append(", purchasePrice=").append(purchasePrice);
        sb.append(", purchaseNum=").append(purchaseNum);
        sb.append(", sellingPrice=").append(sellingPrice);
        sb.append(", salesVolume=").append(salesVolume);
        sb.append(", discount=").append(discount);
        sb.append(", originalPrice=").append(originalPrice);
        sb.append(", catalog=").append(catalog);
        sb.append(", author=").append(author);
        sb.append(", publisher=").append(publisher);
        sb.append(", otherInfor=").append(otherInfor);
        sb.append(", classification=").append(classification);
        sb.append(", category=").append(category);
        sb.append(", status=").append(status);
        sb.append(", sort=").append(sort);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", brief=").append(brief);
        sb.append(", preface=").append(preface);
        sb.append(", excerpt=").append(excerpt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}