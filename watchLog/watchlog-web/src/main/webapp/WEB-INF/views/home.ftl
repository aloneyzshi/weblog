<#import "layout/defaultLayout.ftl" as layout>
<@layout.myLayout>
		 <div class="container">
				    <div class="row"  style="margin-left: 30px;">
				 					<a class="brand" href="#"><img src="/res/img/page.jpg" /></a>
				 	</div>		    	
				 	
				    <div class="row"  style="margin-left: 0px;margin-top: 30px;">
		    				<div class="col-sm-12"> 
												<table  class="table wratb  removebd">
													<tbody>
																<tr>
																	<td class="col-sm-2">
																						<p class="head-text">创建日志源</p>
																						
																						<ul class="home-stl">
																								<li class="font-13-gray">添加日志监控</li>
																								<li class="font-13-gray">自定义分析规则</li>
																								<li class="font-13-gray">关键字，正则支持</li>
																								<li class="font-13-gray">Datestream实时收集日志</li>
																								<li class="font-13-gray">基于Storm集群实时计算</li>
																						</ul>
																	</td>
																	<td class="col-sm-1 ">
																					<div style="margin-top: 100px;  padding-left: 20px;">
																							<h1><i class="glyphicon glyphicon-circle-arrow-right "></i></h1>
																					</div>			
																	</td>
																			
																	
																	<td class="col-sm-2">
																						<p class="head-text">调试日志源</p>
																						<ul class="home-stl">
																								<li class="font-13-gray">输入调试样本日志</li>
																								<li class="font-13-gray">在线日志规则</li>
																								<li class="font-13-gray">保障首次分析成功</li>
																						</ul>
																	</td>
																	<td class="col-sm-1 ">
																					<div style="margin-top: 100px;  padding-left: 20px;">
																							<h1><i class="glyphicon glyphicon-circle-arrow-right "></i></h1>
																					</div>		
																	</td>
																	
																											
																	<td class="col-sm-2">
																						<p class="head-text">实时分析</p>
																						<ul class="home-stl">
																									<li class="font-13-gray">实时展示最新结果</li>
																									<li class="font-13-gray">延迟时间不超过30s</li>
																						</ul>				
																	</td>
																	<td class="col-sm-1 ">
																					<div style="margin-top: 100px;  padding-left: 20px;">
																							<h1><i class="glyphicon glyphicon-circle-arrow-right "></i></h1>
																					</div>		
																	</td>
																	
																	<td class="col-sm-2">
																						<p class="head-text">聚合分析</p>
																						<ul class="home-stl">
																								<li class="font-13-gray">支持自定义时间段选择</li>
																								<li  class="font-13-gray">全面日志详情展示</li>
																								<li class="font-13-gray">异常日志分布趋势图</li>
																								<li class="font-13-gray">异常日志分布表</li>
																								<li class="font-13-gray">异常信息详情</li>
																						</ul>		
																	</td>
																																																																	
																</tr>								
													</tbody>
												</table>
								</div><!-- /col-sm-12 -->			
				 	</div><!-- /row -->					    					 	
				 	
				    <div class="row"  style="margin-left: 30px; margin-top: 40px;">
				    			<div  class="col-sm-7 pull-right" >
				    			      <#if RequestParameters.proj?exists >
				 							  <a class="btn  btn-lg"  style="color: #699;background-color: #fff;border-color: #08D355;" href="/logsrc/manage?proj=${RequestParameters.proj}" role="button"  style="margin-right: 10px;">开始使用</a>
				    			        <#else>
				 							  <a class="btn  btn-lg"  style="color: #699;background-color: #fff;border-color: #08D355;" href="/logsrc/manage" role="button"  style="margin-right: 10px;">开始使用</a>
				    			        </#if>
  								</div>
				 	</div>		    					 	
				 	
		</div>
 
</@layout.myLayout>