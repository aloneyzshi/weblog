package com.netease.qa.log.storm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.qa.log.storm.util.Const;



public class ConfigDataLoadTask implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(ConfigDataLoadTask.class);

	@Override
	public void run() {
		while(true){
			ConfigDataService.loadConfig();
			try {
				Thread.sleep(Const.CONFIG_LOAD_DURATION);
			}
			catch (InterruptedException e) {
	        	logger.error("error", e);
			}
		}
	}

}
	
