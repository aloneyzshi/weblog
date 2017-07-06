package com.netease.qa.log.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.netease.qa.log.web.filter.NeteaseOpenIdAuthService;
import com.netease.qbs.QbsService;
import com.netease.qbs.meta.Project;
import com.netease.qbs.meta.User;

@Controller
@RequestMapping(value = {"user", "/"})
public class UserController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private QbsService qbsService;

    private static NeteaseOpenIdAuthService openAuthService = new NeteaseOpenIdAuthService();

    private static final String LOCATION_NAME = "_NTES_LOCATION_";

    /**
     * OpenID登录页 .
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public RedirectView loginWithOpenId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String url = request.getRequestURL().toString();
        String realm = url.substring(0, url.indexOf(uri));
        String realUrl = request.getParameter("cb");
        String returnTo = realm + "/user/callback";
        if (realUrl != null && realUrl != "") {
            returnTo += "?cb=" + realUrl;
        }
        String redirectUrl = openAuthService.getRedirectUrl(returnTo, realm, request.getSession());
        logger.debug(request.getHeader("Refer"));
        return new RedirectView(redirectUrl);
    }

    /**
     * 登录成功回调页面 .
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("/callback")
    public Object openIdDoLogin(HttpServletRequest request, HttpSession session) {
        String assocHandle = request.getParameter("openid.assoc_handle");
        try {
            String macKey = (String) session.getAttribute(assocHandle);
            if (openAuthService.checkSignature(request.getQueryString(), assocHandle, macKey)) {
                String name = request.getParameter("openid.sreg.nickname");
                String fullname = request.getParameter("openid.sreg.fullname");
                String email = request.getParameter("openid.sreg.email");
                authSuccess(name, fullname, email, session);
                String redirectUrl = request.getParameter("cb");
                if (redirectUrl == null) {
                    redirectUrl = "/";
                } else {
                    try {
                        redirectUrl = URLDecoder.decode(redirectUrl, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error("redirect url decode error", e);
                    }
                }

                return new RedirectView(redirectUrl);
            }
        } catch (IOException e) {
            logger.error("认证失败", e);
        }
        return new RedirectView("login");
    }

    /**
     * 认证成功.
     * @param name
     * @param fullName
     * @param email
     * @param session
     * @return
     */
    public Object authSuccess(String name, String fullName, String email, HttpSession session) {
        User user = qbsService.getUserByLoginName(name);
        List<Project> projects;
        // 新用户
        if (user == null) {
            user = new User();
            user.setLogin(name);
            user.setFullname(fullName);
            user.setEmail(email);
            user.setIsActive(true);
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            qbsService.createUser(user);
            projects = new ArrayList<Project>();
        } else {
            projects = qbsService.getProjects(user.getId());
        }
        session.setAttribute("user", user);
        session.setAttribute("projects", projects);
        // 默认选择第1个项目
        if (projects.size() > 0) {
            session.setAttribute("project", projects.get(0));
        }
        return user;
    }

    @RequestMapping("/logout")
    public RedirectView logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return new RedirectView("/");
    }

    @RequestMapping("/{user_id}")
    public RedirectView detail(HttpServletRequest request) {
        return new RedirectView("/");
    }
}
