package com.netease.qa.log.storm.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

	public static void createFile(String fileName){
		File file;
		try {
			file = new File(fileName);
			if (!file.exists()) {
				System.out.println("file is not exists");
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void appendContent(String fileName, String content){
		try {
			//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件 
			FileWriter fw = new FileWriter(fileName, true);
			fw.write(content + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean fileExist(String fileName){
		File file = new File(fileName);
		if(file.exists()){
			return true;
		}
		return false;
	}
}
