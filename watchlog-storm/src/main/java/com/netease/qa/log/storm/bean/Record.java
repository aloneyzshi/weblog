package com.netease.qa.log.storm.bean;

public class Record {

	private int log_source_id;
	private String remote_addr;
	private String remote_port;
	private String remote_user;
	private long time_local;
	private String request;
	private int status;
	private int body_bytes_sent;
	private String http_referer;
	private String http_user_agent;
	private String http_x_forwarded_for;
	private double request_time;
	private String host;
	private String upstream_addr;
	private int upstream_status;
	private double upstream_response_time;
	private String ssl_protocol;
	private String ssl_cipher;
	private String server_name;
	
	public int getLog_source_id() {
		return log_source_id;
	}
	public void setLog_source_id(int log_source_id) {
		this.log_source_id = log_source_id;
	}
	
	public String getRemote_addr() {
		return remote_addr;
	}
	public void setRemote_addr(String remote_addr) {
		this.remote_addr = remote_addr;
	}
	public String getRemote_port() {
		return remote_port;
	}
	public void setRemote_port(String remote_port) {
		this.remote_port = remote_port;
	}
	public String getRemote_user() {
		return remote_user;
	}
	public void setRemote_user(String remote_user) {
		this.remote_user = remote_user;
	}
	public long getTime_local() {
		return time_local;
	}
	public void setTime_local(long time_local) {
		this.time_local = time_local;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getBody_bytes_sent() {
		return body_bytes_sent;
	}
	public void setBody_bytes_sent(int body_bytes_sent) {
		this.body_bytes_sent = body_bytes_sent;
	}
	public String getHttp_referer() {
		return http_referer;
	}
	public void setHttp_referer(String http_referer) {
		this.http_referer = http_referer;
	}
	public String getHttp_user_agent() {
		return http_user_agent;
	}
	public void setHttp_user_agent(String http_user_agent) {
		this.http_user_agent = http_user_agent;
	}
	public String getHttp_x_forwarded_for() {
		return http_x_forwarded_for;
	}
	public void setHttp_x_forwarded_for(String http_x_forwarded_for) {
		this.http_x_forwarded_for = http_x_forwarded_for;
	}
	public double getRequest_time() {
		return request_time;
	}
	public void setRequest_time(double request_time) {
		this.request_time = request_time;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUpstream_addr() {
		return upstream_addr;
	}
	public void setUpstream_addr(String upstream_addr) {
		this.upstream_addr = upstream_addr;
	}
	public int getUpstream_status() {
		return upstream_status;
	}
	public void setUpstream_status(int upstream_status) {
		this.upstream_status = upstream_status;
	}
	public double getUpstream_response_time() {
		return upstream_response_time;
	}
	public void setUpstream_response_time(double upstream_response_time) {
		this.upstream_response_time = upstream_response_time;
	}
	public String getSsl_protocol() {
		return ssl_protocol;
	}
	public void setSsl_protocol(String ssl_protocol) {
		this.ssl_protocol = ssl_protocol;
	}
	public String getSsl_cipher() {
		return ssl_cipher;
	}
	public void setSsl_cipher(String ssl_cipher) {
		this.ssl_cipher = ssl_cipher;
	}

	public String toString(){
		String str = remote_addr + "," + time_local + "," + request ;
		return str;
	}
	public String getServer_name() {
		return server_name;
	}
	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}
}
