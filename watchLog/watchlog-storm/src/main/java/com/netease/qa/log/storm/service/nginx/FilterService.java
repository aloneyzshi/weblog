package com.netease.qa.log.storm.service.nginx;

/**
 * 过滤请求静态资源的url
 * @author hzquguoqing
 *
 */
public class FilterService {
	public boolean filterStaticUrl(String url){
		String a = "(.*?)(\\.)(css|js|jpg)$";
		boolean isTrue = url.matches(a);
		return !isTrue;
	}
}
