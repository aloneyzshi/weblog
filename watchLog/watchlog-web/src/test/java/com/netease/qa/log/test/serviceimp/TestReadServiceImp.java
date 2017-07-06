package com.netease.qa.log.test.serviceimp;
//package com.netease.qa.log.user.serviceimp;
//
//import javax.annotation.Resource;
//
//import junit.framework.Assert;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.alibaba.fastjson.JSONObject;
//import com.netease.qa.log.service.impl.ReadServiceImpl;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//// 一定要是applicationContext.xml,不能是其他的Spring 配置文件（spring-mvc.xml）
//@ContextConfiguration("/applicationContext.xml")
//@Transactional
//public class TestReadServiceImp {
//	@Resource
//	private ReadServiceImpl readService;
//
////	@Test
////	public void testQueryTimeRecords() {
////		JSONObject records = readService.queryTimeRecords(1, 1427618062, 1427634829, 100, 1);
////		boolean key1 = false;
////		if (records != null)
////			key1 = true;
////		Assert.assertEquals("查找不成功", true, key1);
////
////		JSONObject records1 = readService.queryTimeRecords(1, 1427618060, 1427618061, 100, 1);
////		boolean key2 = false;
////		if (records1 != null)
////			key2 = true;
////		Assert.assertEquals("在没有的起止时间里，竟然查找成功了", false, key2);
////
////	}
////
////	@Test
////	public void testQueryErrorRecords() {
////		JSONObject errors = readService.queryErrorRecords(1, 1427618062, 1427634829, 100, 1);
////		boolean key1 = false;
////		if (errors != null)
////			key1 = true;
////		Assert.assertEquals("查找不成功", true, key1);
////
////		JSONObject errors1 = readService.queryErrorRecords(1, 1427618060, 1427618061, 100, 1);
////		boolean key2 = false;
////		if (errors1 != null)
////			key2 = true;
////		Assert.assertEquals("在没有的起止时间里，竟然查找成功了", false, key2);
////	}
////
////	@Test
////	public void testQueryUnknownExceptions() {
////		JSONObject unknown = readService.queryUnknownExceptions(1, 1427611078, 1427634829, 100, 1);
////		boolean key1 = false;
////		if (unknown != null)
////			key1 = true;
////		Assert.assertEquals("查找不成功", true, key1);
////
////		JSONObject unknown1 = readService.queryUnknownExceptions(1, 1427618060, 1427618061, 100, 1);
////		boolean key2 = false;
////		if (unknown1 != null)
////			key2 = true;
////		Assert.assertEquals("在没有的起止时间里，竟然查找成功了", false, key2);
////	}
//}
