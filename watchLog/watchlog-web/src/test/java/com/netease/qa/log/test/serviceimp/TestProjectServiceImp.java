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
//import com.netease.qa.log.meta.Project;
//import com.netease.qa.log.service.ProjectService;
//import com.netease.qa.log.service.impl.ProjectServiceImpl;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//// 一定要是applicationContext.xml,不能是其他的Spring 配置文件（spring-mvc.xml）
//@ContextConfiguration("/applicationContext.xml")
//@Transactional
//public class TestProjectServiceImp {
//
//	@Resource
//	private ProjectService projectService;
//
////	@Test
////	public void testAddProject() {
////		// 添加项目名不存在的项目，期望返回项目id
////		Project project = new Project();
////		project.setProjectName("hello");
////		project.setProjectEngName("hello_eng");
////		project.setTimeAccuracy(30);
////		
////		int project = projectService.createProject(project);
////		boolean key1 = false;
////		if (project >= 1)
////			key1 = true;
////		Assert.assertEquals("插入不成功", true, key1);
////
////		// 添加已存在的项目
////		int project1 = projectService.createProject("tset", "test", 30);
////		Assert.assertEquals("竟然添加已存在的项目", -2, project1);
////	}
////
////	@Test
////	public void testUpdateProject() {
////		// 更新已存在的项目，期望返回1
////		int project = projectService.updateProject(28, "123", "123_eng", 35);
////		Assert.assertEquals("更改成功", 1, project);
////
////		// 更新不存在的项目，期望返回0
////		int project1 = projectService.updateProject(36, "123", "123_eng", 30);
////		Assert.assertEquals("竟然更改不存在的项目成功", -1, project1);
////	}
////
////	@Test
////	public void testFindProject() {
////		// 找存在的项目
////		JSONObject project = projectService.findByProjectId(2);
////		// 略微判断下，JSON里面是否有我们需要的projectid和name_eng
////		boolean key1 = false;
////		if (project != null)
////			key1 = true;
////		Assert.assertEquals("查找不成功", true, key1);
////
////		JSONObject project1 = projectService.findByProjectId(100);
////		boolean key3 = false;
////		if (project1 != null)
////			key3 = true;
////		Assert.assertEquals("竟然查找到了不存在的项目", false, key3);
////	}
//}
