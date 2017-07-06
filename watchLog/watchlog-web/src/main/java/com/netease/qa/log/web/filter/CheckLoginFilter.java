package com.netease.qa.log.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckLoginFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(CheckLoginFilter.class);

	private String exclusivePatter;
	private String redirectURL;
	private String sessionName;

	@Override
	public void init(FilterConfig filterConfig) {
		exclusivePatter = filterConfig.getInitParameter("exclude");
		redirectURL = filterConfig.getInitParameter("redirect");
		sessionName = filterConfig.getInitParameter("logined");
	}

	/**
	 * 验证目标URI是否有权限
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String target = req.getRequestURI();
		
		logger.info("target: " + target);
		logger.info(""+ !target.matches(redirectURL));
		logger.info(""+ !target.matches(this.exclusivePatter));

		if (!target.matches(redirectURL) && !target.matches(this.exclusivePatter)) {
			logger.info("target: " + target);
			Object userInfo = req.getSession().getAttribute(this.sessionName);

			if (userInfo == null) {
				StringBuffer buffer = req.getRequestURL();
				if (req.getQueryString() != null)
					buffer.append("?").append(req.getQueryString());
				String originUrl = res.encodeURL(buffer.toString());
				res.sendRedirect(redirectURL + "?cb=" + originUrl);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
