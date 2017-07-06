<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>

<!--self defined-->
<link rel="stylesheet" href="/res/css/self/logsrc_manage.css" />
 <script src="/res/js/self/common_logsrc.js"></script>
 <script src="/res/js/self/copy.js"></script>
  
  
  <#if RequestParameters.proj?exists >
  	<#assign pid = RequestParameters.proj>
  	
		<div class="container">

				<form  id="logsrc_edit_form" class="form-horizontal" role="form"   accept-charset="UTF-8" action="/logsrc/repeat" data-remote="true" method="post" onsubmit="return check_copy_logsrc('${logsrc_name}', '${host_name}', '${logsrc_path}', '${logsrc_file}' )">
						
						<div class="form-group"  >
								<label for="logsrc_name" class="col-sm-2 control-label"  style="text-align: left; font-size: medium; color: gray;">日志源名称
				<a title="不能以下划线_开始和结尾;  
只能包含英文字母，数字，汉字;  
长度最长为100;"  style="color: gray;"><span class="glyphicon glyphicon-question-sign"  aria-hidden="true"><a>		</label>		
						      <div class="col-sm-6">
						         <input type="text" class="form-control log-item-input"   id="logsrc_name"      name="logsrc_name"  value=${logsrc_name}  placeholder="请输入名字">
						         <p  style="font-size: 12px;color: #699;"> 命名规则：不能以下划线开始和结尾;  只能包含英文字母/汉字/数字;  长度最长为100;</p>
						         <div id="warn_copy_logsrc_name"></div>
						      </div>
						</div>
					
						<div class="row" style=" border-bottom: solid 1px #eee; margin-bottom: 10px;">
									<label  class="col-sm-2 control-label"  style="text-align: left; font-size: medium; color: gray; ">日志源位置</label>
						</div>
						
						
									<div class="form-group" >
											<label for="host_name" class="col-sm-2 control-label log-item-show" >服务器地址</label>
									      <div class="col-sm-6">
									         <input type="text" class="form-control log-item-input"    id="host_name"     name="host_name"   value=${host_name} placeholder="请输入服务器地址">
									         <div id="warn_copy_host_name"></div>
									      </div>
									</div>		
						
									<div class="form-group" >
											<label for="logsrc_path" class="col-sm-2 control-label log-item-show" >日志文件路径</label>
									      <div class="col-sm-6">
									         <input type="text" class="form-control log-item-input"     id="logsrc_path"       name="logsrc_path"  value=${logsrc_path}   placeholder="请输入日志文件路径">
									          <div id="warn_copy_logsrc_path"></div>
									      </div>
									</div>		
						
				
									<div class="form-group" >
											<label for="logsrc_file" class="col-sm-2 control-label log-item-show" >日志文件名</label>
									      <div class="col-sm-6">
									         <input type="text" class="form-control log-item-input"     id="logsrc_file"  name="logsrc_file"      value=${logsrc_file} placeholder="请输入日志文件名">
									           <div id="warn_copy_logsrc_file"></div>
									          <div id="warn_copy_logsrc_location"></div>
									      </div>
									</div>		
									
					<#if  filter_keyword?contains("_AND_")>
							<#assign filter_keyword_flag = "AND">
							<#assign filter_keyword_arr=filter_keyword?split("_AND_")>
					<#elseif  filter_keyword?contains("_OR_")>				
							<#assign filter_keyword_flag = "OR">
							<#assign filter_keyword_arr=filter_keyword?split("_OR_")>
					<#else>
							<#assign filter_keyword_flag = "none">
							<#assign filter_keyword_arr=[filter_keyword]>
					</#if>
							
						<div class="row" style=" border-bottom: solid 1px #eee; margin-bottom: 10px;">
									<label  class="col-sm-2 control-label"  style="text-align: left; font-size: medium; color: gray;">匹配规则配置</label>
						</div>	
						
						
									<div class="form-group" >
											<label for="start_regex" class="col-sm-2 control-label log-item-show"  >
												起始标志
												<span title='确定一行日志起始的正则表达式，如日志以"2015-03-09"格式的时间开头，则起始标志可设置为\d{4}\-\d{2}\-\d{2}。默认用"非空格"作为一行日志的起始标志，即^\S+.' 
												 class="glyphicon glyphicon-question-sign"  aria-hidden="true">
											</label>
									      <div class="col-sm-6">
									         <input type="text" class="form-control log-item-input"   id="start_regex"  name="start_regex"   value=${start_regex}   placeholder="请输入起始标志">
									          <p  style="font-size: 12px;color: #699;"> 一般以非空格作为日志行起始标志，即  ^\S+ </p>
									         <div id="warn_edit_start_regex"></div>
									      </div>
									</div>		
									
															
								<div class="row" >
										<div class="col-md-2">
												<h4> <small  style="padding-left: 60px;">Step1：</small></h4>
										</div><!-- /col-md-2 -->
										<div class="col-md-6"> 
												<div class="row" >
														<div class="col-md-3"> <h4 style="margin-left: 15px;"> <small>过滤关键字	
															<span title='使用过滤关键字，可以从大量原始日志中过滤出有用的信息，交给分析模块处理。如"Error"，可以过滤出所有Error级别的异常日志。 多个关键字可以使用and连接，即日志同时包含这些关键字才能被筛选出。也可使用or连接。暂不支持复合条件'
										 class="glyphicon glyphicon-question-sign"  aria-hidden="true"></small></h4></div>
														<div class="col-md-2 pull-right">  <a class="btn btn-default btn-sm" href="javascript:void(0);" onclick="add_row_filter_keyword()" role="button">+ 添加行</a></div>
												</div><!-- /row -->					
											<div class="form-group"  style="margin: 0px;" >
											        <table  id="filter_keyword_table" class="table table-bordered">
															<tbody>
																	<#assign i=0>
																	<#list filter_keyword_arr as f>
																				<tr>
																						<td class="col-md-5" style="padding:0px">
																									<input type="text" class="form-control log-item-regular-input"  id="filter_keyword_input_id "   name="filter_keyword_arr[]"   value="${f}"   >	
																						</td>
																					<#if i==0> 
																						<td class="col-md-1"  style="padding:0px"> 
																						    	  <select class=" form-control  log-item-regular-input"   id="filter_keyword_select_id"   name="filter_keyword_con"    onchange="select_onchange_filter_words(this)">
																							    	  		<#if filter_keyword_flag=="AND">
																									      		<option value="AND"   selected="true" >AND</option>
																									      		<option value="OR"   >OR</option>																									    	  		
																									      	<#else>
																									      		<option value="OR"   selected="true" >OR</option>
																									      		<option value="AND"   >AND</option>		
																							    	  		</#if>
																							      </select>	<!-- /select -->															
																					</td>	
																						<td class="col-md-1" ><a class="filter_delete" href="javascript:void(0)"  title="删除"  onclick=filter_item_delete(this)><i class="glyphicon glyphicon-trash  filter_delete_icon"></i></a>	</td>
																					<#else>
																						<td class="col-md-1 filter_keyword_td_select"  style="padding:0px">${filter_keyword_flag}</td>
																						<td class="col-md-1" ><a class="filter_delete" href="javascript:void(0)"  title="删除"  onclick=filter_item_delete(this)><i class="glyphicon glyphicon-trash  filter_delete_icon"></i></a>	</td>
																					</#if>
																				</tr><!-- /tr -->
																				<#assign i=1>
																		</#list>																																					
															</tbody>
														</table><!-- /table -->			   
											</div><!--/ form-group -->
									</div><!-- /col-md-6 -->			
								</div><!-- /row -->
								
					<#if  reg_regex?contains("_AND_")>
							<#assign reg_regex_flag = "AND">
							<#assign reg_regex_arr=reg_regex?split("_AND_")>
					<#elseif  reg_regex?contains("_OR_")>				
							<#assign reg_regex_flag = "OR">
							<#assign reg_regex_arr=reg_regex?split("_OR_")>
					<#else>
							<#assign reg_regex_flag = "none">
							<#assign reg_regex_arr=[reg_regex]>
					</#if>					
							
									<div class="row" >
										<div class="col-md-2">
												<h4> <small  style="padding-left: 60px;">Step2：</small></h4>
										</div><!-- /col-md-2 -->
										<div class="col-md-6"> 
												<div class="row" >
														<div class="col-md-3"> <h4 style="margin-left: 15px;">
																 <small>正则表达式		<span  title='使用正则表达式，可以从一行日志中，精确抓取到日志中的特征字段。如(\w+\.)+(\w)*Exception:.*?(($)|(\\t))可以匹配日志中任意的java异常，并提取出具体的异常类型。通过正则表达式，平台可以在分析报告中，统计出某个时间出现了哪些java异常，每种异常出现了多少次等信息。系统支持多个正则表达式，针对每一行日志，都会尝试匹配这些表达式。如果没有匹配到任何表达式，则该行日志的类型为“unknown”' 
																	class="glyphicon glyphicon-question-sign"  aria-hidden="true"></small></h4></div>
														<div class="col-md-2 pull-right">  <a class="btn btn-default btn-sm" href="javascript:void(0);" onclick="add_row_reg_regex()" role="button">+ 添加行</a></div>										
												</div><!-- /row -->
												<div class="form-group"  style="margin: 0px;" >
														<table  id="reg_regex_table" class="table table-bordered"   style="margin-bottom: 0px;">
															<tbody>
																	<#assign j=0>
																	<#list reg_regex_arr as r>
																			<tr>
																					<td class="col-md-5" style="padding:0px">
																								<input type="text" class="form-control log-item-regular-input"  id="reg_regex_input_id"   name="reg_regex_arr[]"    value="${r}"  >	
																					</td>
																					<#if j==0> 
																						<td class="col-md-1"  style="padding:0px"> 
																						    	  <select class=" form-control log-item-regular-input"   id="reg_regex_select_id"   name="reg_regex_con"  onchange="select_onchange_reg_regex(this)" >
																						    	  			<option value="OR"   selected="true" >OR</option>
																							      </select>	<!-- /select -->															
																					</td>				
																						<td class="col-md-1" ><a class="filter_delete" href="javascript:void(0)"  title="删除"  onclick=filter_item_delete(this)><i class="glyphicon glyphicon-trash  filter_delete_icon"></i></a>	</td>
																					<#else>
																						<td class="col-md-1 reg_regex_td_select"  style="padding:0px">${reg_regex_flag}</td>
																						<td class="col-md-1" ><a class="filter_delete" href="javascript:void(0)"  title="删除"  onclick=filter_item_delete(this)><i class="glyphicon glyphicon-trash  filter_delete_icon"></i></a>	</td>
																					</#if>
																			</tr><!-- /tr -->		
																			<#assign j=1>
																	</#list>		
															</tbody>
														</table>
														<p  style="font-size: 12px;color: #699;"> 如果是java应用，可以使用   (\w+\.)+(\w)*Exception:.*?(($)|(\\t))    匹配典型的java异常信息 </p>
												</div><!-- /row -->
										</div><!-- /col-md-10 -->			
								</div><!-- /row -->
						
						<input type="hidden" name="filter_keyword_arr[]"   value=""  />	
						<input type="hidden" name="reg_regex_arr[]"   value=""  />									
						<input type="hidden" name="id"   value=${id} />		
						<input type="hidden"  name="proj"   value="${pid}"  />
						
				   <div class="form-group">
				      <div class="col-sm-offset-2 col-sm-10" style="margin-top: 40px;">
				         <button type="submit" class="btn btn-primary">保存</button>
				          <a class="btn btn-default" href="/logsrc/manage?proj=${pid}" role="button">取消</a>
				      </div>
				   </div>   
					 
			</form>
	
</div>
<#else>
     <div class="container  alert alert-warning"> 请先选择右上角项目</div>
</#if>

</@layout.myLayout>