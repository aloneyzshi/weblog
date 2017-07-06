package com.netease.qa.log.util;


public class Const {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FILTER_KEYWORD_AND = "_AND_";
    public static final String FILTER_KEYWORD_OR = "_OR_";
    public static final String FILTER_KEYWORD_NONE = "NONE";
    
	public static final String UNKNOWN_TYPE = "unknown";


    public static final String ID_MUST_BE_NUM = "id must be a number";
    public static final String TYPE_MUST_BE_NUM="type must be a number of 0 or 1 ";
    public static final String STATUS_MUST_BE_NUM = "status must be 0 or 1";
    public static final String ACCURACY_MUST_BE_NUM = "accuracy must be a number";
    public static final String LIMIT_AND_OFFSET_MUST_BE_NUM = " limit or offset  must be a number";
    public static final String LOG_NAME_ALREADY_EXSIT = "logsource name already exist";
    public static final String LOG_PATH_ALREADY_EXSIT = "logsource path already exist";
    public static final String LOG_NOT_EXSIT = "logsource not exist";
    public static final String PROJECT_ALREADY_EXSIT = "project already exist";
    public static final String PROJECT_NOT_EXSIT = "project not exist";
    public static final String INNER_ERROR = "internal server error";
    public static final String INVALID_TIME_FORMAT = "invalid time format, please use: " + TIME_FORMAT;
    public static final String INVALID_NAME ="invalid name format";
    public static final String NULL_PARAM = "lose some paramater";
    public static final String DEFAULT = "default";
    public static final String RESPONSE_SUCCESSFUL = "response successfully";
    public static final String RESPONSE_NOTSUCCESSFUL = "response not successfully";
    public static final String TIME_TOO_SHORT = "time range is too short";
    
    public static final String FILITER_TYPE = "type_regex";
    
    
    public static final long RT_SHOW_TIME = 30; //实时分析页面前端显示间隔，s
    public static final long RT_SHOW_NUM = 15; //实时分析页面显示记录条数
    public static final int OFFLINE_POINT=300;//AB平台，离线曲线需要显示的点的个数
    
    public static final int MACHINE_ERROR_POINT=300;//AB平台，按机器聚合异常点数
    
    public static final String ORDER_FIELD_SAMPLE_TIME = "sample_time";
    public static final String ORDER_FIELD_SAMPLETIME = "sampleTime";
    public static final String ORDER_FIELD_TOTALCOUNT = "totalCount";
    public static final String ORDER_FIELD_EXCEPTIONCOUNT = "exceptionCount";
    public static final String ORDER_DESC = "desc";
    public static final String ORDER_ASC = "asc";
    
    
}
