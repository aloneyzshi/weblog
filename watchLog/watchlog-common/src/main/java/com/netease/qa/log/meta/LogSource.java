package com.netease.qa.log.meta;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.springframework.util.StringUtils;

import com.netease.qa.log.util.Const;


public class LogSource {
	
	private int logSourceId;
	private String logSourceName;
	private int logType;
	private int projectId;
	private Timestamp  createTime;
	private Timestamp  modifyTime;
	private String hostname;
	private String path;
	private String filePattern;
	private String lineStartRegex;
	private String lineFilterKeyword;
	private String lineTypeRegex;
	private String logFormat;
	private int logSourceCreatorId;
	private int logSourceStatus;
	
	private ArrayList<String> lineTypeRegexs;
	private ArrayList<String> lineFilterKeywords;
	private String lineFilterKeywordsCondition;
	
	
	/**
	 * lineFilterKeyword  lineTypeRegex格式转化
	 */
	public void convertParams(){
		convertFilterKeywords();
		convertTypeRegexs();
	}

	
	private void convertTypeRegexs() {
		String[] typeRegexs = lineTypeRegex.split(Const.FILTER_KEYWORD_OR, -1);
		lineTypeRegexs = new ArrayList<String>();
		for (String tmp : typeRegexs) {
			if (!StringUtils.isEmpty(tmp)) {
				lineTypeRegexs.add(tmp);
			}
		}
	}

	
	private void convertFilterKeywords(){
		if(lineFilterKeyword.trim().equals(Const.FILTER_KEYWORD_NONE)) { 
			return;
		}
		String[] keywords = lineFilterKeyword.split(Const.FILTER_KEYWORD_OR, -1);
		if(keywords.length > 1){
			lineFilterKeywords = new ArrayList<String>();
			for(String tmp : keywords){
				if(!StringUtils.isEmpty(tmp)){
					lineFilterKeywords.add(tmp);
				}
			}
			this.setLineFilterKeywordsCondition(Const.FILTER_KEYWORD_OR);
		}
		else{
			keywords = lineFilterKeyword.split(Const.FILTER_KEYWORD_AND, -1);
			lineFilterKeywords = new ArrayList<String>();
			for(String tmp : keywords){
				if(!StringUtils.isEmpty(tmp)){
					lineFilterKeywords.add(tmp);
				}
			}
			this.setLineFilterKeywordsCondition(Const.FILTER_KEYWORD_AND);
		}
	}
	
	
	
	public static void main(String []d){
		LogSource l = new LogSource();
		l.setLineFilterKeyword("ERROR_AND");
		l.setLineTypeRegex("(\\w)*Exception:.*?__as1111__");
		l.convertParams();
		for(String tmp : l.getLineFilterKeywords()){
			System.out.println("LL: " + tmp);
		}
		System.out.println(l.getLineFilterKeywordsCondition());
		
		for(String tmp : l.getLineTypeRegexs()){
			System.out.println("LL: " + tmp);
		}
		
	}
	
	public ArrayList<String> getLineTypeRegexs() {
		return lineTypeRegexs;
	}

	public void setLineTypeRegexs(ArrayList<String> lineTypeRegexs) {
		this.lineTypeRegexs = lineTypeRegexs;
	}
	
	public ArrayList<String> getLineFilterKeywords() {
		return lineFilterKeywords;
	}

	public void setLineFilterKeywords(ArrayList<String> lineFilterKeywords) {
		this.lineFilterKeywords = lineFilterKeywords;
	}
	
	public String getLineFilterKeywordsCondition() {
		return lineFilterKeywordsCondition;
	}
	
	public void setLineFilterKeywordsCondition(String lineFilterKeywordsCondition) {
		this.lineFilterKeywordsCondition = lineFilterKeywordsCondition;
	}

	public int getLogSourceId() {
		return logSourceId;
	}
	
	public void setLogSourceId(int logSourceId) {
		this.logSourceId = logSourceId;
	}
	
	public String getLogSourceName() {
		return logSourceName;
	}

	public void setLogSourceName(String logSourceName) {
		this.logSourceName = logSourceName;
	}
	
	public int getLogType() {
		return logType;
	}

	public void setLogType(int logType) {
		this.logType = logType;
	}
	public int getProjectId() {
		return projectId;
	}
	
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	
	public Timestamp getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	public Timestamp getModifyTime() {
		return modifyTime;
	}
	
	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getFilePattern() {
		return filePattern;
	}
	
	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}
	
	public String getLineStartRegex() {
		return lineStartRegex;
	}
	
	public void setLineStartRegex(String lineStartRegex) {
		this.lineStartRegex = lineStartRegex;
	}
	
	public String getLineFilterKeyword() {
		return lineFilterKeyword;
	}
	
	public void setLineFilterKeyword(String lineFilterKeyword) {
		this.lineFilterKeyword = lineFilterKeyword;
	}
	
	public String getLineTypeRegex() {
		return lineTypeRegex;
	}
	
	public void setLineTypeRegex(String lineTypeRegex) {
		this.lineTypeRegex = lineTypeRegex;
	}
	
	public int getLogSourceStatus() {
		return logSourceStatus;
	}

	public void setLogSourceStatus(int logSourceStatus) {
		this.logSourceStatus = logSourceStatus;
	}


	public int getLogSourceCreatorId() {
		return logSourceCreatorId;
	}

	public void setLogSourceCreatorId(int logSourceCreatorId) {
		this.logSourceCreatorId = logSourceCreatorId;
	}
	

	public String getLogFormat() {
		return logFormat;
	}

	public void setLogFormat(String logFormat) {
		this.logFormat = logFormat;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{logSourceId:").append(logSourceId).append("{projectId:").append(projectId).append(",hostname:")
			.append(hostname).append("{path:").append(path).append(",filePattern:").append(filePattern);
		return sb.toString();
	}
	

}
