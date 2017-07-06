package com.netease.qa.log.test.serviceimp;
package com.netease.qa.log.user.serviceimp;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.netease.qa.log.service.LogSourceService;



@RunWith(SpringJUnit4ClassRunner.class)
//一定要是applicationContext.xml,不能是其他的Spring 配置文件（spring-mvc.xml）
@ContextConfiguration("/applicationContext.xml")
@Transactional
public class TestLogsourceServiceImp {

	@Resource
	LogSourceService logsourceService;

	/*
	 * importent information // 之所以LogsourceServiceImp
	 * 可以被注册，是因为它在定义的时候已经被注解为@Service了，被Spring注册为bean // 而LogSource
	 * 只是一个model，不需要为某一业务服务，它在整个程序中持续存在，所以不需要注册bean,只要在方法中 // 调用该model即可 //
	 * @Resource // LogSource logSource;
	 */

//	@Test
//	public void testAddLogsource() {
//		// 测试添加不同路径的日志源，期望返回json{logsourceid:**}
//		int logsource = logsourceService.createLogSource("log", 2, "localhost://qg", "test1/helloworld1",
//				"helloWorld1.txt", "\\d{4}\\-\\d{2}\\-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}", "error",
//				"java.net.NoRouteToHostException: this is a NoRouteToHostException", "lilei");
//		boolean key1 = false;
//		if (logsource >= 1)
//			key1 = true;
//		Assert.assertEquals("插入不存在的日志源，竟然插入失败", true, key1);
//
//		// 插入已存在的日志源
//		int logsource1 = logsourceService.createLogSource("log4", 4, "localhost://", "test1/helloworld1",
//				"helloWorld1.txt", "\\d{4}\\-\\d{2}\\-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}", "error",
//				"java.net.NoRouteToHostException: this is a NoRouteToHostException", "lilei");
//		Assert.assertEquals("插入已存在的日资源，竟然插入成功了！", -2, logsource1);
//
//		// 插入不存在的项目
//		int logsource2 = logsourceService.createLogSource("log4", 900, "localhost://qgq", "test1/helloworld1",
//				"helloWorld1.txt", "\\d{4}\\-\\d{2}\\-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}", "error",
//				"java.net.NoRouteToHostException: this is a NoRouteToHostException", "lilei");
//		Assert.assertEquals("插入不存在的项目，竟然插入成功！", -1, logsource2);
//	}
//
//	@Test
//	public void testUpdateLogsource() {
//		// 更新已存在的日志源，期望返回json{logsourceid:**}
//		int logsource = logsourceService.updateLogSource(5, "log_new", "localhost://qgq", "test1/helloworld1",
//				"helloWorld1.txt", "\\d{4}\\-\\d{2}\\-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}", "error",
//				"java.net.NoRouteToHostException: this is a NoRouteToHostException");
//		Assert.assertEquals("更新不成功", 1, logsource);
//
//		// 更新不存在的日志源，期望返回空json
//		int logsource1 = logsourceService.updateLogSource(100, "log_new", "localhost://qgq", "test1/helloworld1",
//				"helloWorld1.txt", "\\d{4}\\-\\d{2}\\-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}", "error",
//				"java.net.NoRouteToHostException: this is a NoRouteToHostException");
//		Assert.assertEquals("更新不成功", -1, logsource1);
//	}
//
//	@Test
//	public void testFindLogsource() {
//		// find 已存在的日志
//		JSONObject logsource = logsourceService.getDetailByLogSourceId(5);
//		boolean key1 = false;
//		if (logsource != null)
//			key1 = true;
//		Assert.assertEquals("找错了", true, key1);
//
//		// find 不存在的日志
//		JSONObject logsource1 = logsourceService.getDetailByLogSourceId(100);
//		boolean key2 = false;
//		if (logsource1 != null)
//			key2 = true;
//		Assert.assertEquals("竟然把不存在的找出来了", false, key2);
//	}
//
//	@Test
//	public void testDeleteLogsource() {
//		// delete已存在的日志源，期望返回json{deleteOk:1}
//		int logsource = logsourceService.deleteLogSource(5);
//		Assert.assertEquals("没有删除成功", 1, logsource);
//
//		// delete不存在的日志源,期望返回空的json{}
//		int logsource1 = logsourceService.deleteLogSource(100);
//		Assert.assertEquals("竟然把不存在的日志删除了", -1, logsource1);
//	}
//
//	@Test
//	public void testChangeLogsourceStatus() {
//		// change已存在的日志源，期望返回json{logsourceid:**}
//		int logsource = logsourceService.changeLogSourceStatus(5, 1);
//		Assert.assertEquals("修改不成功", 1, logsource);
//
//		// change不存在的日志源，期望返回空json
//		int logsource1 = logsourceService.changeLogSourceStatus(100, 1);
//		Assert.assertEquals("竟然修改了不存在的日志源状态", -1, logsource1);
//	}

}
