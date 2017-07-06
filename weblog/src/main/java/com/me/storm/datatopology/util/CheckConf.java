package com.me.storm.datatopology.util;

import java.io.File;
import com.me.storm.datatopology.bolt.FilterBolt;
import com.me.storm.datatopology.util.GlobalDef;

public class CheckConf extends Thread {
	private String xmlpath = "xxx.xml";
	private int heartbeat = 1000;
	private String type = "type";

	public CheckConf(String XmlPath, int HeartBeat, String type) {
		this.xmlpath = XmlPath;
		this.heartbeat = HeartBeat;
		this.type = type;
	}

	public void run() {

		// hash初始值
		long init_time = 0;

		for (int i = 0;; i++) {

			try {

				File file = new File(this.xmlpath);
				// 检查时间戳是否一致
				long lasttime = file.lastModified();

				if (i == 0) {
					init_time = lasttime;
				} else {
					if (init_time != lasttime) {

						init_time = lasttime;

						if (this.type.equals(GlobalDef.Thread_type_filterbolt)) {
							FilterBolt.isload();
						}
					}
				}

				Thread.sleep(this.heartbeat);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new CheckConf("MysqlBolt.xml", 1000, "test").start();
		Thread.sleep(100000);
	}
}
