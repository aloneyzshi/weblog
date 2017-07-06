package com.netease.qbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.netease.qa.log.util.HttpUtils;
import com.netease.qa.log.util.HttpUtils.Response;
import com.netease.qbs.meta.Member;
import com.netease.qbs.meta.Project;
import com.netease.qbs.meta.Role;
import com.netease.qbs.meta.User;
import com.netease.qbs.meta.Version;

/**
 * Qbs API 封装.
 * @author hzlaidonglin
 * @version 0.1
 */
public class QbsServiceBean implements QbsService {
    private static final Logger logger = LoggerFactory.getLogger(QbsServiceBean.class);

    private String url;
    private String token;
    private final ObjectMapper objectMapper;
    private final Map<String, String> authorizeHeader;
    private final Cache<String, Object> innerCache;

    /**
     * Qbs 服务.
     *
     * @param url qbs api地址，形如http://10.10.10.1:3001/api/
     * @param token qbs token
     */
    public QbsServiceBean(String url, String token) {
        if (url == null || token == null) {
            throw new IllegalArgumentException("url or token is null");
        }
        if (!url.endsWith("/api/")) {
            if (url.endsWith("/")) {
                url = url + "api/";
            } else if (url.endsWith("api")) {
                url = url + "/";
            } else
                url = url + "/api/";
        }
        this.url = url;
        this.token = token;
        objectMapper = new ObjectMapper();
        objectMapper
                .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        authorizeHeader = new HashMap<String, String>();
        authorizeHeader.put("Authorization", "Token " + token);
        authorizeHeader.put("Accept", "application/json; charset=utf-8");
        authorizeHeader.put("Content-Type", "application/json; charset=utf-8");

        innerCache =
                CacheBuilder.newBuilder().maximumSize(2000).expireAfterWrite(15, TimeUnit.MINUTES)
                        .build(new CacheLoader<String, Object>() {
                            @Override
                            public Object load(String key) throws Exception {
                                return innerCache.getIfPresent(key);
                            }
                        });
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getUserById(int)
     */
    @Override
    public User getUserById(int id) {
        User user = getFirstOrNull("users.json?id=" + id, User.class);
        return user;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getUserByLoginName(java.lang.String)
     */
    @Override
    public User getUserByLoginName(String loginName) {
        User user = getFirstOrNull("users.json?login=" + loginName, User.class);
        return user;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getUserByEmail(java.lang.String)
     */
    @Override
    public User getUserByEmail(String email) {
        User user = getFirstOrNull("users.json?email=" + email, User.class);
        return user;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getBatchUsersById(java.util.List)
     */
    @Override
    public List<User> getBatchUsersById(List<Integer> idList) {
        StringBuffer urlBuffer = new StringBuffer("users.json?");
        urlBuffer.append(concreteUrl(idList, "id"));
        List<User> users = getAll(urlBuffer.toString(), User.class);
        return users;
    }


    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#createUser(com.netease.qbs.meta.User)
     */
    @Override
    public boolean createUser(User user) {
        user.setId(null); // Clear id
        int result = postHelper("users.json", user);
        if (result > 0) {
            user.setId(result);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getProjects()
     */
    @Override
    public List<Project> getProjects() {
        List<Project> projects = getAll("projects.json", Project.class);
        return projects;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getVersions(int)
     */
    @Override
    public List<Version> getVersions(int projectId) {
        List<Version> versions = getAll("projects/" + projectId + "/versions.json", Version.class);
        return versions;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#addVersion(int, com.netease.qbs.meta.Version)
     */
    @Override
    public boolean addVersion(int projectId, Version version) {
        version.setId(null);
        version.setProjectId(projectId);
        int versionId = postHelper("projects/" + projectId + "/versions.json", version);
        if (versionId > 0) {
            version.setId(versionId);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getProjects(int)
     */
    @Override
    public List<Project> getProjects(int userId) {
        List<Project> projects = getAll("users/" + userId + "/get_projects.json", Project.class);
        return projects;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getProjectById(int)
     */
    @Override
    public Project getProjectById(int id) {
        Project project = getFirstOrNull("projects.json?id=" + id, Project.class);
        return project;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getProjectByName(java.lang.String)
     */
    @Override
    public Project getProjectByName(String name) {
        Project project = getFirstOrNull("projects.json?name=" + name, Project.class);
        return project;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getBatchProjectsById(java.util.List)
     */
    @Override
    public List<Project> getBatchProjectsById(List<Integer> idList) {
        StringBuffer urlBuffer = new StringBuffer("projects.json?");
        urlBuffer.append(concreteUrl(idList, "id"));
        List<Project> projects = getAll(urlBuffer.toString(), Project.class);
        return projects;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#getMembers(int)
     */
    @Override
    public List<Member> getMembers(int projectId) {
        List<ArrayNode> array =
                getAll("projects/" + projectId + "/member_with_roles.json", ArrayNode.class);
        List<Member> users = new ArrayList<Member>();
        for (ArrayNode a : array) {
            try {
                User user = objectMapper.readValue(a.get(0).toString(), User.class);
                JavaType javaType =
                        objectMapper.getTypeFactory().constructParametricType(List.class,
                                Role.class);
                List<Role> roles = objectMapper.readValue(a.get(1).toString(), javaType);
                Member member = new Member();
                member.setRoles(roles);
                member.setUser(user);
                users.add(member);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return users;
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#addMember(com.netease.qbs.meta.Project, com.netease.qbs.meta.Member)
     */
    @Override
    public boolean addMember(Project project, Member member) {
        List<Member> members = new ArrayList<Member>();
        members.add(member);
        return addMember(project, members);
    }

    /* (non-Javadoc)
     * @see com.netease.qbs.QbsService2#addMember(com.netease.qbs.meta.Project, java.util.List)
     */
    @Override
    public boolean addMember(Project project, List<Member> members) {
        List<MemberRequest> entities = new ArrayList<MemberRequest>();
        for (Member m : members) {
            MemberRequest entity = new MemberRequest();
            entity.projectId = project.getId();
            entity.userId = m.getUser().getId();
            entity.roleId = new ArrayList<Integer>();
            for (Role role : m.getRoles()) {
                entity.roleId.add(role.getId());
            }
            entities.add(entity);
        }

        int result = postHelper("members/create_members", entities);
        if (result < 0)
            return false;
        return true;
    }

    /**
     * 获取第一个或者 null（若为空）记录.
     *
     * @param command 对象API
     * @param clazz 记录类型
     * @return
     */
    protected <T> T getFirstOrNull(String command, Class<T> clazz) {
        T cached = getFromCache(command);
        if (cached == null) {
            List<T> result = getAll(command, clazz);
            if (result != null && result.size() > 0) {
                cached = result.get(0);
                saveToCache(command, cached);
            }
        }
        return cached;
    }

    /**
     * 获取所有记录，但是不缓存.
     *
     * @param command
     * @param clazz 记录的类型
     * @return
     */
    protected <T> List<T> getAll(String command, Class<T> clazz) {
        return getAll(command, clazz, false);
    }

    /**
     * 获取所有记录，并且决定是否缓存.
     *
     * @param command
     * @param clazz 记录类型
     * @param needCached 是否缓存
     * @return 记录列表
     */
    protected <T> List<T> getAll(String command, Class<T> clazz, boolean needCached) {
        String url = this.url + command;
        if (needCached) {
            List<T> cached = getFromCache(command);
            if (cached != null)
                return cached;
        }

        Response response = HttpUtils.httpGet(url, authorizeHeader);
        if (response.getCode() == 200) {
            try {
                JavaType javaType =
                        objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
                List<T> results = objectMapper.readValue(response.getContent(), javaType);
                if (needCached)
                    saveToCache(command, results);
                return results;
            } catch (IOException e) {
                logger.error("Parse error: {}", response.getContent());
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.error("Qbs reading error: {}", response.toString());
        }
        return null;
    }

    /**
     * 需要改进.
     *
     * @param command
     * @param object
     * @return
     */
    private <T> int postHelper(String command, T object) {
        String url = this.url + command;
        if (object == null)
            throw new IllegalArgumentException("Nothing to post");

        try {
            String json = objectMapper.writeValueAsString(object);
            Response response = HttpUtils.httpPost(url, json, authorizeHeader);
            // TODO: 根据响应码，决定返回值
            if (response.getCode() < 500) {
                if (response.getContent().length() == 0) {
                    return 0;
                }
                JavaType javaType =
                        objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class,
                                JsonNode.class);
                try {
                    Map<String, JsonNode> result =
                            objectMapper.readValue(response.getContent(), javaType);
                    if (result.containsKey("errors")) {
                        logger.error("Qbs return error: {}", result.get("errors").toString());
                    } else if (result.containsKey("id")) {
                        return result.get("id").asInt();
                    }
                } catch (IOException e) {
                    logger.error("Get response but parse error:{}", response.getContent());
                    logger.error(e.getMessage(), e);
                }
            } else {
                logger.error("Write failed: {}", json);
                logger.error("Qbs reading error: {}", response);
            }
        } catch (JsonProcessingException e) {
            logger.error("Process error: {}", object);
            logger.error(e.getMessage(), e);
        }

        return -1;
    }

    @SuppressWarnings("unchecked")
    private <T> T getFromCache(String key) {
        T t = (T) innerCache.getIfPresent(key);
        return t;
    }

    private void saveToCache(String key, Object object) {
        innerCache.put(key, object);
    }

    private String concreteUrl(List<Integer> idList, String keyName) {
        StringBuffer buffer = new StringBuffer();
        for (int id : idList) {
            buffer.append(keyName);
            buffer.append("%5B%5D=");
            buffer.append(id);
            buffer.append("&");
        }
        return buffer.toString();
    }

    private static class MemberRequest {
        @SuppressWarnings("unused")
        public int projectId;
        @SuppressWarnings("unused")
        public int userId;
        public List<Integer> roleId;
    }
}
