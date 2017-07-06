package com.netease.qa.log.debugger;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.util.Const;


public class Debugger {
	
	private static final Logger logger = LoggerFactory.getLogger(Debugger.class);

	private EncodingSupportRAFReader fileReader;
	private LogSource logSource;
	private HashMap<String, Integer> exceptionCountCache;
	private ArrayList<String> unknownCache;
	private Pattern logHeadPattern;
	private ByteArrayOutputStream buff = null;
	private static final int LEN = 1024;
	private int lineCount = 0;
	private static final Pattern DEFAULT_HEAD_PATTERN = Pattern.compile("^\\S+");
	private static final int HEAD_LEN = 200;
	private boolean end = false;
    private final static int  MAX_LINE_NUM   = 100;
    
	public Debugger(LogSource logSource, String fileName){
		this.logSource = logSource;
	    this.logSource.convertParams();
		this.exceptionCountCache = new HashMap<String, Integer>();
		this.unknownCache = new ArrayList<String>();
		this.logHeadPattern = Pattern.compile(logSource.getLineStartRegex());
		try {
			fileReader =  new EncodingSupportRAFReader(new RandomAccessFile(fileName, "r"), "utf-8");
			logger.info("debug for file: " + fileName + ", logsource: " + logSource.getLogSourceName());
		}
		catch (FileNotFoundException e) {
			logger.error("file not exist");
		}
	}
	
	private String s;
	public String getS(){
		return s;
	}

//	public static void main(String []args){
//		LogSource logSource = new LogSource();
//		logSource.setLogSourceName("zwj_test");
//		logSource.setLineStartRegex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
//		logSource.setLineTypeRegex("(\\w+\\.)+(\\w)*Exception_OR_Forcing driver to exit uncleanly_OR_ThriftEventSink try connecto to ThriftServer fail! retrying...");
//		logSource.setLineFilterKeyword("ERROR_OR_Exception");
//		logSource.convertParams();
//		Debugger d = new Debugger(logSource, "a.txt");
//		logger.info(""+ d.getS().equals("asd"));
//
//	}
	
	/**
	 * 返回debug结果，类型和个数的map
	 * @return
	 */
	public HashMap<String, Integer> getExceptionCountMap(){
		return exceptionCountCache;
	}
	
	/**
	 * 返回debug结果，Unknown类型原始日志的List
	 * @return
	 */
	public ArrayList<String> getUnknownList(){
		return unknownCache;
	}
	
	public boolean doDebug(){
		boolean isFileEnd = false;
		while (!isFileEnd) {
			String line = null; 
			try {
				line = fileReader.readLine();
			}
			catch (IOException e) {
				return false;
			}
			if (line == null) {
				if (hasRemaining()) {
					setEnd(true);
					isFileEnd = true;
				}
				else {
					break;
				}
			}
			else if (line.length() <= 1 && line.trim().equals("")) {
				continue;
			}

			append(line, logHeadPattern);
			// current block end
			if (isEnd()) {
				byte[] rawMsg = getBytes();
				String input = new String(rawMsg);
				analyse(filter(input));
				reset();
				append(line, logHeadPattern);
			}
		}
		return true;
	}
	
	/**
	 * 如果行需要过滤，返回这行。 否则返回null.
	 * @param line
	 * @return
	 */
	private String filter(String line){
		String keywordStr = logSource.getLineFilterKeyword();
		// 未指定过滤关键字， 不需要过滤
		if (keywordStr.trim().equals(Const.FILTER_KEYWORD_NONE)) {
			return line;
		}
		// 需要过滤
		else {
			ArrayList<String> keywords = logSource.getLineFilterKeywords();
			String condition = logSource.getLineFilterKeywordsCondition();
			// OR 关键字过滤
			if (condition.equals(Const.FILTER_KEYWORD_OR)) {
				for (String keyword : keywords) {
					if (line.indexOf(keyword.trim()) != -1) {
						logger.info("or get! " + keyword + ", " + line);
						return line;
					}
				}
				return null;
			}
			// AND关键字过滤
			else {
				boolean flag = true;
				for (String keyword : keywords) {
					if (line.indexOf(keyword.trim()) == -1) {
						flag = false;
						break;
					}
				}
				if (flag) {
					logger.info("and get! " + line);
					return line;
				}
				else{
					logger.info("drop! " + line);
					return null;
				}
			}
		}
	}
	
	
	/**
	 * 计算类型和个数，增量缓存
	 * @param line
	 */
	private void analyse(String line){
		if(StringUtils.isEmpty(line)) return;
		ArrayList<String> lineTypeRegexs = logSource.getLineTypeRegexs();
		HashSet<String> exceptionTypes = new HashSet<String>(); //支持一条日志属于多个type
		for(String lineTypeRegex : lineTypeRegexs){
			try{
				Pattern p = Pattern.compile(lineTypeRegex); 
				Matcher m = p.matcher(line);  
				if(m.find()){
					logger.debug("match! " + m.group() + ", logSource: " + logSource.getLogSourceName());
					exceptionTypes.add(m.group());
				}
			}
			catch(PatternSyntaxException e){
				logger.error("Pattern compile error, regex: " + lineTypeRegex + ", logSource: " + logSource.getLogSourceName() + ", line: " + line, e);
			}
		}
		//日志没有匹配到任何异常类型，设置为unknown类型
		if(exceptionTypes.size() == 0){
			exceptionTypes.add(Const.UNKNOWN_TYPE);
			logger.debug("cant match! set as unknown, logSource: " + logSource.getLogSourceName() + ", line: " + line);
		}
			
		// 查询exception缓存，如果不存在则插入exception表
		for (String exceptionType : exceptionTypes) {
			if (exceptionCountCache.containsKey(exceptionType)) {
				exceptionCountCache.put(exceptionType, exceptionCountCache.get(exceptionType) + 1);
			}
			else {
				exceptionCountCache.put(exceptionType, 1);
			}
			
			// unknown类型异常记录原始日志
			if(exceptionType.equals(Const.UNKNOWN_TYPE)){
				if(line.length() < 500)
					unknownCache.add(line);
				else
					unknownCache.add(line.substring(0, 499));
			}
		}
	}
	
	private void append(String line, Pattern headPattern) {
		if (line == null || line.trim().length() == 0) {
			return;
		}
        if (lineCount > MAX_LINE_NUM) {
            logger.error("can't detect LogHead with pattern:" + headPattern.toString() + "' over " + MAX_LINE_NUM
                     + ", we auto end this log for you, check your header regex.");
            this.end = true;
            return;
        }
		try {
			if (buff == null) {
				// this is the first line of block, just add it anyway.
				buff = new ByteArrayOutputStream();
				buff.write(str2byte(line));
				lineCount++;
			}
			else {
				// this is not the first line of block
				// this is not exception style line, end current block
				if (this.isHeadLine(line, headPattern)) {
					setEnd(true);
					// send exception style line
				}
				else {
					if(line.startsWith("        ")){
						line = "\\t" + line.trim();
					}
					buff.write(str2byte(line));
					lineCount++;
				}
			}
		}
		catch (IOException e) {
			String msg = "send line to logwrapper fail. due to:" + e.getMessage();
			logger.error(msg);
		}
	}


	private byte[] str2byte(String line) {
		if (line == null) {
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			int count = (int) Math.ceil(((double) line.length()) / LEN);
			int end = 0;
			for (int i = 0; i < count; i++) {
				end = ((i + 1) * LEN > line.length()) ? line.length() : (i + 1) * LEN;
				String sub = line.substring(i * LEN, end);
				bos.write(sub.getBytes());
			}
			return bos.toByteArray();
			// return line.getBytes(SyslogUtil.CHAR_SET_DEFAULT);
		}
		catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
		catch (IOException e) {
			String msg = "Convert string to byte array  fail, due to:" + e.getMessage();
			logger.error(msg);
			return null;

		}
	}

	private boolean isHeadLine(String line, Pattern headPattern) {
		if (headPattern == null) {
			headPattern = DEFAULT_HEAD_PATTERN;
		}
		if (line == null || line.trim().length() == 0) {
			return false;
		}

		String theHead = line.substring(0, line.length() < HEAD_LEN ? line.length() : HEAD_LEN);
		Matcher m = headPattern.matcher(theHead);
		boolean result = m.find();
		return result;
	}

	protected void setEnd(boolean end) {
		this.end = end;
	}

	private boolean isEnd() {
		return end;
	}

	private void reset() {
		this.end = false;
		buff = null;
		lineCount = 0;
	}

	private byte[] getBytes() {
		return (buff == null ? null : buff.toByteArray());
	}

	private boolean hasRemaining() {
		return (buff != null && buff.size() > 0);
	}

}
