package com.netease.qbs.meta;

import java.util.Date;


public class Version extends BaseEntity {
	/**
	 *
	 */
	private static final long serialVersionUID = -6812063709603893683L;
	private Integer id;
	private String name;
	private Integer projectId;
	private Boolean isValid;
	private Boolean isActive;
	private Date startAt;
	private Date endAt;
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
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public Boolean getIsValid() {
		return isValid;
	}
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Date getStartAt() {
		return startAt;
	}
	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}
	public Date getEndAt() {
		return endAt;
	}
	public void setEndAt(Date endAt) {
		this.endAt = endAt;
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
