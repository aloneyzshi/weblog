package com.netease.qa.log.util;


public class ConstCN {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FILTER_KEYWORD_AND = "_AND_";
    public static final String FILTER_KEYWORD_OR = "_OR_";
    public static final String FILTER_KEYWORD_NONE = "NONE";
    public static final String TYPE_REGEX_CON = "__";
    
	public static final String UNKNOWN_TYPE = "unknown";


    public static final String ID_MUST_BE_NUM = "id 必须是数字";
    public static final String STATUS_MUST_BE_NUM = "status必须是0或1";
    public static final String ACCURACY_MUST_BE_NUM = "accuracy must be a number";
    public static final String LIMIT_AND_OFFSET_MUST_BE_NUM = " limit or offset  必须是数字";
    public static final String LOG_NAME_ALREADY_EXSIT = "日志源名称已经存在";
    public static final String LOG_PATH_ALREADY_EXSIT = "日志源路径已经存在";
    public static final String LOG_NOT_EXSIT = "该日志源不存在";
    public static final String PROJECT_ALREADY_EXSIT = "该项目已经存在";
    public static final String PROJECT_NOT_EXSIT = "该项目不存在";
    public static final String INNER_ERROR = "数据库内部错误";
    public static final String INVALID_TIME_FORMAT = "invalid time format, please use: " + TIME_FORMAT;
    public static final String INVALID_NAME ="名称非法";
    public static final String NULL_PARAM = "缺少参数";
    public static final String REPORT_EXSIT = "相同的报告已存在";
    
    public static final String RESPONSE_SUCCESSFUL = "响应成功";
    public static final String RESPONSE_NOTSUCCESSFUL = "响应不成功";
    
    public static final String UNKNOWN_MESSAGE = "部分日志无法解析类型，点击查看所有unknown类型原始日志";
}
