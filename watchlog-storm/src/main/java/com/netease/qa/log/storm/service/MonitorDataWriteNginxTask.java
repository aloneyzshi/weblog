package com.netease.qa.log.storm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.qa.log.storm.util.Const;

public class MonitorDataWriteNginxTask implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(MonitorDataWriteTask.class);
	
	@Override
	public void run() {
		while(true){
			MonitorDataService.writeNginxAccessData();
			try {
				//每30s执行一次写入操作
				Thread.sleep(Const.NGINX_LOG_WRITE_DURATION);
			}
			catch (InterruptedException e) {
	        	logger.error("error", e);
			}
		}
	}

}
