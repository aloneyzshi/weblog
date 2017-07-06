package com.netease.qa.log.meta;

public class NginxAccess {

	private int nid;
	private int logSourceId;
	private String url;
	//startTime以s为单位
	private long startTime;
	//totalCount 在离线时间区间很大的情况下，可能出现超出int范围
	private long totalCount;
	private long requestTimeTotal;
	private int requestTimeMax;
	private long upstreamResponseTimeTotal;
	private int upstreamResponseTimeMax;
	private long okCount;
	private long error4Count;
	private long error5Count;
	private long byteTotal;
	private int requestTime90;
	private int requestTime99;
	private int upstreamResponseTime90;
	private int upstreamResponseTime99;
	
	/**
	 * 返回ms单位的时间
	 * @return
	 */
	public long getStartTimeStamp(){
		return startTime * 1000;
	}
	
	public int getNid() {
		return nid;
	}

	public void setNid(int nid) {
		this.nid = nid;
	}

	public int getLogSourceId() {
		return logSourceId;
	}

	public void setLogSourceId(int logSourceId) {
		this.logSourceId = logSourceId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getRequestTimeTotal() {
		return requestTimeTotal;
	}

	public void setRequestTimeTotal(long requestTimeTotal) {
		this.requestTimeTotal = requestTimeTotal;
	}

	public int getRequestTimeMax() {
		return requestTimeMax;
	}

	public void setRequestTimeMax(int requestTimeMax) {
		this.requestTimeMax = requestTimeMax;
	}

	public long getUpstreamResponseTimeTotal() {
		return upstreamResponseTimeTotal;
	}

	public void setUpstreamResponseTimeTotal(long upstreamResponseTimeTotal) {
		this.upstreamResponseTimeTotal = upstreamResponseTimeTotal;
	}

	public int getUpstreamResponseTimeMax() {
		return upstreamResponseTimeMax;
	}

	public void setUpstreamResponseTimeMax(int upstreamResponseTimeMax) {
		this.upstreamResponseTimeMax = upstreamResponseTimeMax;
	}

	public long getOkCount() {
		return okCount;
	}

	public void setOkCount(long okCount) {
		this.okCount = okCount;
	}

	public long getError4Count() {
		return error4Count;
	}

	public void setError4Count(long error4Count) {
		this.error4Count = error4Count;
	}

	public long getError5Count() {
		return error5Count;
	}

	public void setError5Count(long error5Count) {
		this.error5Count = error5Count;
	}

	public long getByteTotal() {
		return byteTotal;
	}

	public void setByteTotal(long byteTotal) {
		this.byteTotal = byteTotal;
	}

	public int getRequestTime90() {
		return requestTime90;
	}

	public void setRequestTime90(int requestTime90) {
		this.requestTime90 = requestTime90;
	}

	public int getRequestTime99() {
		return requestTime99;
	}

	public void setRequestTime99(int requestTime99) {
		this.requestTime99 = requestTime99;
	}

	public int getUpstreamResponseTime90() {
		return upstreamResponseTime90;
	}

	public void setUpstreamResponseTime90(int upstreamResponseTime90) {
		this.upstreamResponseTime90 = upstreamResponseTime90;
	}

	public int getUpstreamResponseTime99() {
		return upstreamResponseTime99;
	}

	public void setUpstreamResponseTime99(int upstreamResponseTime99) {
		this.upstreamResponseTime99 = upstreamResponseTime99;
	}


	
	
	
	@Override
	public String toString() {
		return "NginxRecord [logSourceId=" + logSourceId + ", startTime=" + startTime + ", url=" + url
				+ ", totalCount=" + totalCount + ", requestTimeTotal=" + requestTimeTotal + ", requestTimeMax="
				+ requestTimeMax + ", upstreamResponseTimeTotal=" + upstreamResponseTimeTotal
				+ ", upstreamResponseTimeMax=" + upstreamResponseTimeMax + ", okCount=" + okCount + ", error4Count="
				+ error4Count + ", error5Count=" + error5Count + ", byteTotal=" + byteTotal + ", requestTime90="
				+ requestTime90 + ", requestTime99=" + requestTime99 + ", upstreamResponseTime90="
				+ upstreamResponseTime90 + ", upstreamResponseTime99=" + upstreamResponseTime99 + "]";
	}
}
