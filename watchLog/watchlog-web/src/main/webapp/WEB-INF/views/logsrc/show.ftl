<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>

<!--self defined-->
<link rel="stylesheet" href="/res/css/self/logsrc_manage.css" />
 <script src="/res/js/self/show.js"></script>
  
  <#if RequestParameters.proj?exists >
  	<#assign pid = RequestParameters.proj>
  	
			<div class="container">
							<!-- 删除日志源表单form-->
							<form  id="destroy_logsrc_single_form" action="/logsrc/destroy"  method="post"   class="form-horizontal" role="form"   accept-charset="UTF-8"   data-remote="true">     	  
								<!-- 模态框（Modal）for: 删除日志源 二次确认对话框 -->
								<div class="modal fade" id="destroy_logsrc_single_modal" tabindex="-1" role="dialog"    aria-labelledby="myModalLabel" aria-hidden="true">
									   <div class="modal-dialog">
												      <div class="modal-content">
												      		<input type="hidden" id="ids" name="ids"  />
												      		<input type="hidden" id="proj" name="proj"  />
												      		 <!-- header  -->
													         <div class="modal-header">
														            <button type="button" class="close"    data-dismiss="modal" aria-hidden="true">   &times;  </button>
														            <h4 class="modal-title" id="myModalLabel">    确认删除    </h4>
													         </div>
													         <!-- body  -->
													         <div class="modal-body">
													            	您确定要删除选中日志源？
													         </div>
													         <!-- footer  -->
													         <div class="modal-footer">
													           <button type="submit" class="btn btn-primary">  确定  </button>
													            <button type="button" class="btn btn-default"    data-dismiss="modal"> 取消  </button>
													         </div>
												      </div><!-- /.modal-content -->
										</div><!-- /.modal-dialog -->
								</div><!-- 模态框（Modal） -->
							</form>			
			
			  				<!-- 日志详情页面 导航条-->
							<div class="row"   style="border-bottom: solid 1px #ddd;height: 45px;" >
									<div class="col-sm-12"> 
													<table  class="table wratb  removebd">
														<tbody>
																	<tr>
																		<td class="col-sm-9"> <a href="/logsrc/manage?proj=${pid}"> 返回 &#62; </a>	&#160;&#160;<small>${logsrc_name}</small></td>
																		<td class="col-sm-1"> <a class="btn btn-primary btn-xs"  style="margin-right: 15px;" href="/logsrc/${id}/edit?proj=${pid}" role="button">修改日志源</a></td>
																		<td class="col-sm-1">  <button id="remove" class="btn btn-primary  btn-xs"  onclick="delete_logsrc_single(${id}, ${pid})" > 删除日志源 </button></td>
																		<td class="col-sm-1"> <a class="btn btn-primary btn-xs"  style="margin-right: 15px;" href="/logsrc/${id}/debug?proj=${pid}" role="button">调试日志源</a></td>
																	</tr>		
														</tbody>
													</table>
									</div><!-- /col-sm-12 -->			
							</div><!-- /row -->						
			
						
				<div class="row" style=" border-bottom: solid 1px #eee;">
						<div class="col-md-2">
							<h4> <small style="font-size: 15px;font-weight: bold;">日志源位置</small></h4>
						</div>
				</div>
				
					<div class="row" >
						<div class="col-md-2">
							<h4> <small  style="padding-left: 60px;">服务器地址：</small></h4>
						</div>
						<div class="col-md-10"> 
							<h4> <small>${host_name}</small></h4>
						</div>			
				</div>
				
				<div class="row" >
						<div class="col-md-2">
							<h4> <small  style="padding-left: 60px;">日志文件路径：</small></h4>
						</div>
						<div class="col-md-10"> 
							<h4> <small>${logsrc_path}</small></h4>
						</div>			
				</div>
				
				
				<div class="row" >
						<div class="col-md-2">
							<h4> <small  style="padding-left: 60px;">日志文件名：</small></h4>
						</div>
						<div class="col-md-10"> 
							<h4> <small>${logsrc_file}</small></h4>
						</div>			
				</div>
					
				<div class="row" style=" border-bottom: solid 1px #eee;">
						<div class="col-md-2">
							<h4> <small style="font-size: 15px;font-weight: bold;">匹配规则配置</small></h4>
						</div>
				</div>	
				
					<div class="row" >
						<div class="col-md-2">
							<h4> <small  style="padding-left: 60px;">起始标志：
								<span title='确定一行日志起始的正则表达式，如日志以"2015-03-09"格式的时间开头，则起始标志可设置为\d{4}\-\d{2}\-\d{2}。默认用"非空格"作为一行日志的起始标志，即^\\S+.' 
									class="glyphicon glyphicon-question-sign"  aria-hidden="true"></small></h4>
						</div>
						<div class="col-md-10"> 
							<h4> <small>${start_regex}</small></h4>
						</div>			
				</div>
					
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
				
				<div class="row" >
						<div class="col-md-2">
								<h4> <small  style="padding-left: 60px;">Step1：</small></h4>
						</div><!-- /col-md-2 -->
						<div class="col-md-6"> 
								<div class="row" >
										<h4> <small>过滤关键字	<span title='使用过滤关键字，可以从大量原始日志中过滤出有用的信息，交给分析模块处理。如"Error"，可以过滤出所有Error级别的异常日志。 多个关键字可以使用and连接，即日志同时包含这些关键字才能被筛选出。也可使用or连接。暂不支持复合条件'
										 class="glyphicon glyphicon-question-sign"  aria-hidden="true"></small></h4>
								</div><!-- /row -->
								<div  class="row">
										<table  class="table table-bordered wratb">
											<tbody>
													<#list filter_keyword_arr as f>
														<tr>
															<td class="col-md-6">${f}</td>
															<td class="col-md-1">${filter_keyword_flag}</td>
														</tr>											
													</#list>
											</tbody>
										</table>
								</div><!-- /row -->
						</div><!-- /col-md-10 -->			
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
					<div class="row" >
						<div class="col-md-2">
								<h4> <small  style="padding-left: 60px;">Step2：</small></h4>
						</div><!-- /col-md-2 -->
						<div class="col-md-6"> 
								<div class="row" >
										<h4> <small>正则表达式	<span title='使用正则表达式，可以从一行日志中，精确抓取到日志中的特征字段。如(\w+\.)+(\w)*Exception:.*?(($)|(\\t))可以匹配日志中任意的java异常，并提取出具体的异常类型。通过正则表达式，平台可以在分析报告中，统计出某个时间出现了哪些java异常，每种异常出现了多少次等信息。系统支持多个正则表达式，针对每一行日志，都会尝试匹配这些表达式。如果没有匹配到任何表达式，则该行日志的类型为“unknown”' 
										 class="glyphicon glyphicon-question-sign"  aria-hidden="true"></small></h4>
								</div><!-- /row -->
								<div  class="row">
										<table  class="table table-bordered">
											<tbody>
													<#list reg_regex_arr as r>
														<tr>
															<td class="col-md-6">${r}</td>
															<td class="col-md-1">${reg_regex_flag}</td>
														</tr>											
													</#list>
											</tbody>
										</table>
								</div><!-- /row -->
						</div><!-- /col-md-10 -->			
				</div><!-- /row -->
				
			</div>
<#else>
  		  <div class="container  alert alert-warning"> 请先选择右上角项目</div>
</#if>

</@layout.myLayout>