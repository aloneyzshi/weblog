package com.netease.qa.log.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.netease.qa.log.meta.UkExceptionData;
import com.netease.qa.log.meta.dao.UkExceptionDataDao;
import com.netease.qa.log.service.UnknowService;

@Service
public class UnknowServiceImpl implements UnknowService{

	@Resource
	private UkExceptionDataDao unExceptionDataDao;
	
	@Override
	public int getTotalCount(int logSourceId, long startTime, long endTime) {
		return unExceptionDataDao.getTotalCount(logSourceId, startTime, endTime);
	}

	@Override
	public List<UkExceptionData> findByLogSourceIdAndTime(int logSourceId, long startTime, long endTime, int limit,
			int offset) {
		List<UkExceptionData> unknows = new ArrayList<UkExceptionData>();
		unknows = unExceptionDataDao.findByLogSourceIdAndTime(logSourceId, startTime, endTime, limit, offset);
		return unknows;
	}

}
