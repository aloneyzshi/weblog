package com.netease.qbs.meta;

import java.util.Date;

/**
 * {@link com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy}
 * @author hzlaidonglin
 *
 */
public class User extends BaseEntity {
	/**
	 *
	 */
	private static final long serialVersionUID = -376802239420449040L;
	private Integer id;
	private String login;
	private String email;
	private String fullname;
	private String employeeId;
	private Boolean isActive;
	private Date createdAt;
	private Date updatedAt;
	private Integer divisionId;
	private Integer parentId;
	private String identityUrl;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getIdentityUrl() {
		return identityUrl;
	}
	public void setIdentityUrl(String identityUrl) {
		this.identityUrl = identityUrl;
	}
}
