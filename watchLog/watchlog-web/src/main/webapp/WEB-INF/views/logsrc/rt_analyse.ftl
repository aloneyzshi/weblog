<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>


<link rel="stylesheet" href="/res/css/self/logsrc_rt.css" />
<script src="/res/js/self/rt_crud.js"></script>
	  	
	  	
  <#if RequestParameters.proj?exists  && logs?has_content >
  	<#assign pid = RequestParameters.proj>	
  	
    <div class="container-fluid">
    	<div class="row">
   		<div class="col-sm-1"></div>
    	<div class="col-sm-10 "> 
  	 	
						<!-- 左侧： 日志源名称列表-->
						<div class="col-sm-2"  style="padding-left:0px; padding-right:0px;">
								<div id="logsrc_list"  class=" btn-group-vertical  col-sm-12 " role="group" >
										<#assign i=0>
										<#list logs as log_str>
														<#assign lg_arr=log_str?split("#")>
														<#if lg_arr[0]?exists && lg_arr[1]?exists >
																<#if RequestParameters.log_id?exists && RequestParameters.log_id == lg_arr[0]>
																			<a class="btn btn-default  col-lg-12 active"	href="/logsrc/rt_analyse?proj=${pid}&log_id=${lg_arr[0]}">   ${lg_arr[1]}	</a>
																<#elseif !RequestParameters.log_id?exists && i == 0>			
																			<a class="btn btn-default  col-lg-12 active"	href="/logsrc/rt_analyse?proj=${pid}&log_id=${lg_arr[0]}">   ${lg_arr[1]}	</a>
																<#else>
																			<a class="btn btn-default  col-lg-12 "	href="/logsrc/rt_analyse?proj=${pid}&log_id=${lg_arr[0]}">   ${lg_arr[1]}	</a>
																</#if>		
												  		</#if>
												  <#assign i=1>		
										</#list>
								</div>
						</div><!--  col-sm-2 -->
						
						
						<!-- 中间： 实时表格-->
						<div class="col-sm-5" >
							<div class="row"   style=" border-bottom: solid 1px #eee; margin-bottom:10px; padding-bottom: 8px;">
									<div class="col-sm-9 pull-left">  
												<p><font  style="font-size: 15px;font-weight: bold;">实时分析结果&#160;&#160;</font> <font style="font-size: 12px;">采样间隔30s</font></p>
									</div>
									<div class="col-sm-3 pull-right">  
											  <button id="remove" class="btn btn-warning  btn-sm"  onclick="rt_analyse_refresh()" ><i class="glyphicon glyphicon-refresh"></i>刷新 </button>
									</div>
							</div>										
								
										<div  class='row'>
												<table  class='table table-bordered'>
													<tbody id='rt_refresh_body'>
																<tr>
																	<th class='col-sm-4'>采样时间</th>
																	<th class='col-sm-6'>异常类型和数量</th>
																	<th class='col-sm-2'>异常总数</th>
																</tr>		
																<#list rt_table as data>
																	<tr  >
																		<td class='col-sm-4'>${data['date_time']}</td>
																		<td class='col-sm-6'>
																				<#list data['error_tc'] as dt>																
																						<#if dt_has_next>
																								<a  class='pointer_a'  data-toggle='popover' data-placement='top'   title='异常类型'  data-content="${dt['type']}"   href ="#" >&#160;${dt['count']}&#160;</a>,
																						<#else>
																								<a  class='pointer_a'  data-toggle='popover' data-placement='top'   title='异常类型'  data-content="${dt['type']}"   href ="#" >&#160;${dt['count']}</a>
																						</#if>
																				 </#list>																			 
																		 </td>																	
																		<td class='col-sm-2'>${data['total_count']}</td>
																	</tr>																 
															</#list>																	
													</tbody>
												</table>
										</div><!-- /row -->				
						</div><!--  col-sm-6 -->
							
							
						<!-- 右侧： 日志源详情-->
						<div class="col-sm-4 log_info_border">
										<div class="row" style=" border-bottom: solid 1px #eee; margin-bottom:10px;">
													<p style="font-size: 15px;font-weight: bold;">日志源配置</p>
										</div>
												<div class="row" >
														<div class="col-sm-12"> 
																		<table  class="table wratb  removebd">
																			<tbody>
																						<tr>
																							<td class="col-sm-4">服务器地址：</td>
																							<td class="col-sm-8">${host_name}</td>
																						</tr>		
																						<tr>
																							<td class="col-sm-4">文件路径：</td>
																							<td class="col-sm-8">${logsrc_path}</td>
																						</tr>		
																						<tr>
																							<td class="col-sm-4">日志文件名：</td>
																							<td class="col-sm-8">${logsrc_file}</td>
																						</tr>																																																											
																						<tr>
																							<td class="col-sm-4">起始地址：</td>
																							<td class="col-sm-8">${start_regex}</td>
																						</tr>											
																			</tbody>
																		</table>
														</div><!-- /col-sm-12 -->			
												</div><!-- /row -->						
										
								<#if  filter_keyword?contains("_AND_")>
										<#assign filter_keyword_flag = "AND">
										<#assign filter_keyword_arr=filter_keyword?split("_AND_")>
								<#elseif  filter_keyword?contains("_OR_")>				
										<#assign filter_keyword_flag = "OR">
										<#assign filter_keyword_arr=filter_keyword?split("_OR_")>
								<#else>
										<#assign filter_keyword_flag = "">
										<#assign filter_keyword_arr=[filter_keyword]>
								</#if>
								
										<div class="row"    style="height:25px;">
												<div class="col-sm-4">
													<p>Step 1: </p>
												</div>
												<div class="col-sm-8"> 
													 <p>过滤关键字</p>
												</div>			
										</div>						
								
								<div class="row" >
										<div class="col-sm-12"> 
														<table  class="table table-bordered wratb">
															<tbody>
																	<#list filter_keyword_arr as f>
																		<tr>
																			<td class="col-sm-9">${f}</td>
																			<td class="col-sm-3">${filter_keyword_flag}</td>
																		</tr>											
																	</#list>
															</tbody>
														</table>
										</div><!-- /col-sm-12 -->			
								</div><!-- /row -->
						
								<#if  reg_regex?contains("_AND_")>
										<#assign reg_regex_flag = "AND">
										<#assign reg_regex_arr=reg_regex?split("_AND_")>
								<#elseif  reg_regex?contains("_OR_")>				
										<#assign reg_regex_flag = "OR">
										<#assign reg_regex_arr=reg_regex?split("_OR_")>
								<#else>
										<#assign reg_regex_flag = "">
										<#assign reg_regex_arr=[reg_regex]>
								</#if>				
								
										<div class="row"    style="height:25px;">
												<div class="col-sm-3">
													<p>Step 2: </p>
												</div>
												<div class="col-sm-9"> 
													 <p>正则表达式</p>
												</div>			
										</div>						
						
								<div class="row" >
										<div class="col-sm-12"> 
														<table  class="table table-bordered wratb">
															<tbody>
																	<#list reg_regex_arr as r>
																		<tr>
																			<td class="col-sm-9">${r}</td>
																			<td class="col-sm-3">${reg_regex_flag}</td>
																		</tr>											
																	</#list>
															</tbody>
														</table>
										</div><!-- /col-sm-12 -->			
								</div><!-- /row -->				
																														
							</div><!--  col-sm-4 -->
					</div><!-- col-sm-10 -->			
					<div class="col-sm-1"></div>	        
				 </div><!-- row -->
    </div><!-- container -->				
				
<#elseif  !RequestParameters.proj?exists >
	   <div class="container  alert alert-warning"> 请先选择右上角项目</div>
<#elseif !logs?has_content>
	<div class="container  alert alert-warning">该项目没有日志源</div>
<#else>
	<div class="container  alert alert-warning">其他位置错误，请联系管理员</div>
</#if>
    

</@layout.myLayout>