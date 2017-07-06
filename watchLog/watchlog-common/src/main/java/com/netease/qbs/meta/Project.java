package com.netease.qbs.meta;

import java.util.Date;

/**
 * 需要下划线与驼峰之间转换
 * {@link com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy}
 * @author hzlaidonglin
 *
 */
public class Project extends BaseEntity{
	/**
	 *
	 */
	private static final long serialVersionUID = 3733419944771623629L;

	private Integer id;
	private String name;
	private Boolean isActive;
	private Integer parentId;
	private Integer authorizeId;
	private String typeName;
	private String url;
	private Date createdAt;
	private Date updatedAt;
	private Integer categoryId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getAuthorizeId() {
		return authorizeId;
	}
	public void setAuthorizeId(Integer authorizeId) {
		this.authorizeId = authorizeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
}
