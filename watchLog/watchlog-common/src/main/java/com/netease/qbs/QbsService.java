package com.netease.qbs;

import java.util.List;

import com.netease.qbs.meta.Member;
import com.netease.qbs.meta.Project;
import com.netease.qbs.meta.User;
import com.netease.qbs.meta.Version;

public interface QbsService {

    /**
     * 根据用户 Id 获取用户资料.
     *
     * @param id
     * @return
     */
    public User getUserById(int id);

    /**
     * 根据用户名获取用户资料.
     *
     * @param loginName 登录名称，例如hzsomebody
     * @return
     */
    public User getUserByLoginName(String loginName);

    /**
     * 根据邮箱获取用户资料.
     *
     * @param email 登录邮箱
     * @return
     */
    public User getUserByEmail(String email);

    /**
     * 批量获取用户资料.
     *
     * @param idList id列表
     * @return
     */
    public List<User> getBatchUsersById(List<Integer> idList);

    /**
     * 创建用户，并更新Id.
     *
     * @param user
     * @return
     */
    public boolean createUser(User user);

    /**
     * 获取所有项目列表.
     *
     * @return
     */
    public List<Project> getProjects();

    /**
     * 获取项目的所有版本.
     *
     * @param projectId 项目Id
     * @return
     */
    public List<Version> getVersions(int projectId);

    /**
     * 添加版本.
     *
     * @param projectId
     * @param version
     * @return
     */
    public boolean addVersion(int projectId, Version version);

    /**
     * 获取项目列表.
     *
     * @param userId 用户Id
     * @return
     */
    public List<Project> getProjects(int userId);

    /**
     * 根据项目 Id 获取项目资料.
     *
     * @param id 项目Id
     * @return
     */
    public Project getProjectById(int id);

    /**
     * 根据项目名称获取项目资料.
     *
     * @param name 项目名
     * @return
     */
    public Project getProjectByName(String name);

    /**
     * 批量获取用户资料.
     *
     * @param idList id列表
     * @return
     */
    public List<Project> getBatchProjectsById(List<Integer> idList);

    /**
     * 获取成员.
     *
     * @param projectId
     * @return
     */
    public List<Member> getMembers(int projectId);

    /**
     * 项目添加成员.
     *
     * @param project
     * @return
     */
    public boolean addMember(Project project, Member member);

    /**
     * 批量添加项目成员.
     *
     * @param project
     * @return
     */
    public boolean addMember(Project project, List<Member> members);

}
