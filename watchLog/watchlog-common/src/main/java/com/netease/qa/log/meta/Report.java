package com.netease.qa.log.meta;

import java.sql.Timestamp;

public class Report {

	private int reportId;
	private int logSourceId;
	private int projectId;
	private Timestamp startTime;
	private Timestamp endTime;
	private Timestamp createTime;
	private int creatorId;
	private String title;
	private String comment;
	
	
	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getLogSourceId() {
		return logSourceId;
	}

	public void setLogSourceId(int logSourceId) {
		this.logSourceId = logSourceId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Timestamp getCreatTime() {
		return createTime;
	}

	public void setCreatTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	



	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("reportId:").append(reportId).append(";logSourceId:").append(logSourceId).append(";projectId:")
				.append(projectId).append(";startTime:").append(startTime).append(";endTime:").append(endTime)
				.append(";creatorId:").append(creatorId).append(";creatTime:").append(createTime).append(";title:")
				.append(title).append(";comment:").append(comment);
		return sb.toString();
	}
}
