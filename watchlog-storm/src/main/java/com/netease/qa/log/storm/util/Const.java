package com.netease.qa.log.storm.util;

public class Const {
	
	//日志统计数据的入库间隔。 
	public static final int EXCEPTION_LOG_WRITE_DURATION = 30000; //每隔30秒聚合一次，写入数据库
	public static final int NGINX_LOG_WRITE_DURATION = 30000;
	public static final int CONFIG_LOAD_DURATION = 15000;//15s更新logsourceCache
	public static final int STORM_TIME_INTERVAL = 30;//nginx 日志storm处理的聚合时间,30s

	//日志类型
	public static final String NGINX_LOG = "nginx";
	public static final String EXCEPTION_LOG = "exception";
	//分布式常量
	public static final String MQ_QUEUE = "queue.Name";
	public static final String MQ_HOST = "mq.host";
	public static final String MQ_PORT = "mq.port";
	public static final String MYBATIS_EVN = "mybatis.env";
	
	public static final String REMOTE_ADDR = "remote_addr";
	public static final String REMOTE_USER = "remote_user";
	public static final String TIME_LOCAL = "time_local";
	public static final String REQUEST = "request";
	public static final String STATUS = "status";
	public static final String BODY_BYTES_SENT = "body_bytes_sent";
	public static final String HTTP_REFERER = "http_referer";
	public static final String HTTP_SUER_AGENT = "http_user_agent";
	public static final String HTTP_X_FORWARDED_FOR = "http_x_forwarded_for";
	public static final String REQUEST_TIME = "request_time";
	public static final String HOST = "host";
	public static final String UPSTREAM_ADDR = "upstream_addr";
	public static final String UPSTREAM_STATUS = "upstream_status";
	public static final String UPSTREAM_RESPONSE_TIME = "upstream_response_time";
	public static final String SSL_PROTOCOL = "ssl_protocol";
	public static final String SSL_CIPHER = "ssl_cipher";
	//当upstream_status = "-",upstream_reponse_time="-",特殊处理
	//upstream_status，可能出现"504,200",upstream_reponse_time可能出现"0.32,0.06"
	public static final String REPLACE_MESSAGE = "-";
	
	//每隔50ms，发送的数量，作为限流标准
	public static final String  NGINX_LIMIT_NUM = "nginx_limit_num";
	public static final String NGINX_SLEEP_TIME = "nginx_sleep_time";
	public static final String  EXCEPTION_LIMIT_NUM = "exception_limit_num";
	public static final String  EXCEPTION_SLEEP_TIME = "exception_sleep_time";
	
	
}
