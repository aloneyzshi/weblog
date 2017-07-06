package com.netease.qbs.meta;

import java.util.List;


public class Member extends BaseEntity {
	/**
	 *
	 */
	private static final long serialVersionUID = -6573641805794825880L;

	private User user;
	private List<Role> roles;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
