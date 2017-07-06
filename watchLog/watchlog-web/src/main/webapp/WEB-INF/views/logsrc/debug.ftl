<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>

<!--self defined-->
<link rel="stylesheet" href="/res/css/self/logsrc_manage.css" />
 <script src="/res/js/self/common_logsrc.js"></script>
<script src="/res/js/self/debug.js"></script>


  <#if RequestParameters.proj?exists >
  	<#assign pid = RequestParameters.proj>
  	
		<div class="container">
		

		  				<!-- 返回+ 修改行-->
						<div class="row"   style="height: 40px;" >
								<div class="col-sm-12"> 
												<table  class="table wratb  removebd">
													<tbody>
																<tr>
																	<td class="col-sm-9"> <a href="/logsrc/manage?proj=${pid}"> 返回 &#62; </a>	&#160;&#160;<small>${logsrc_name}</small></td>
																	<td class="col-sm-1"> <a class="btn btn-primary btn-xs"  style="margin-right: 15px;" href="/logsrc/${id}/edit?proj=${pid}" role="button">修改日志源</a></td>
																</tr>		
													</tbody>
												</table>
								</div><!-- /col-sm-12 -->			
						</div><!-- /row -->	
						<!-- 日志详情页面 导航条-->
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
								<div class="row"   style="background-color: #eee;font-size: 0.9em;margin-bottom: 10px;" >
										<div class="col-sm-12"> 
												<table  class="table wratb  removebd">
													<tbody>
																<tr>
																	<td class="col-sm-1">起始地址：</td>
																	<td class="col-sm-11">${start_regex}</td>
																</tr>	
																<tr>
																	<td class="col-sm-1">过滤关键字：</td>
																	<td class="col-sm-11">
																			<#list filter_keyword_arr as f>
																					<#if f_has_next>
																							${f}&nbsp;  &nbsp;&nbsp;&nbsp; <b>${filter_keyword_flag} </b>&nbsp;  &nbsp;&nbsp;&nbsp;
																					<#else>
																							${f}
																					</#if>			  
																			</#list>
																	</td>
																</tr>			
																<tr>
																	<td class="col-sm-1">正则表达式：</td>
																	<td class="col-sm-11">
																			<#list reg_regex_arr as r>
																					<#if r_has_next>
																							${r}&nbsp;  &nbsp;&nbsp;&nbsp; <b>${reg_regex_flag} </b>&nbsp;  &nbsp;&nbsp;&nbsp;
																					<#else>
																							${r}
																					</#if>			  
																			</#list>
																	</td>
																</tr>																																
													</tbody>
												</table>
										</div><!-- /col-sm-12 -->										
						  		</div>					

				  					
					<!-- 验证/清空行 -->
						<div class="row"  >
												<table  class="table wratb  removebd" style="margin-bottom: 0px;margin-left: 10px;">
													<tbody>
																<tr>
																	<td class="col-sm-10" style="vertical-align: bottom;">  <label for="debug_log_content">请输入日志信息&#160; &#160; &#160; &#160; </label> <p id="warn_debug_info"></p><p id="warn_debug_notice"></p></td>
																	<td class="col-sm-1"> <a class="btn btn-default  btn-sm" onclick="clear_debug_validate()"  role="button">&#160; 清空 &#160;</a></td>
																	<td class="col-sm-1"> <button id="btn_debug_validate" class="btn btn-warning  btn-sm" onclick="start_debug_validate(${pid}, ${id})">&#160; 验证 &#160;</button></td>
																</tr>		
													</tbody>
												</table>
						</div><!-- /row -->
							
								<!-- loading 模态框 -->
								<div class="modal fade" id="debug_loader_div"  tabindex="-1" role="dialog"    aria-labelledby="myModalLabel" aria-hidden="true">
									   <div class="modal-dialog" >
										 <div  class="col-sm-7 pull-right" >
                                                        <img src="/res/img/loading.gif" />
                                            </div>            
										</div><!-- /.modal-dialog -->
								</div><!-- 模态框（Modal） -->				
						
						<!-- 调试日志输入框 -->				
						<div class="row"  >			
			   			 			<textarea class="form-control" id="debug_log_content"  rows="20"></textarea>
			   			 </div><!-- /row -->			
						   			 
						<!-- 验证结果 表格: type+count -->	   			 
						<div   class='container debug_tc_div ' style="margin-top: 30px; margin-left: -15px; background-color: #eee;display:none">
											<div  class='row' >
													<div class="col-sm-12 ">
															<table  class='table table-bordered' style=" background-color: #fff; margin-top: 12px; margin-bottom: 0px;">
																<tbody id='debug_tc_body'  >													 
																</tbody>
															</table>								
													</div>
											</div><!-- /row -->					
											<div class="row"  id="debug_tc_comment" style="color: gray; margin-left: 0px;margin-top: -15px; margin-bottom: 15px;"  > </div>
											<!-- 表格: unknowlist-->	   			 
											<div  class='  row' >
													<div class="col-sm-12 ">
															<table  class='table table-bordered' style=" background-color: #fff; margin-top: 12px;">
																<tbody id='debug_unknow_body' >																							 
																</tbody>
															</table>
													</div>		
											</div><!-- /row -->			
						</div>
						
					
						
									
							
						
		
						
						<!-- 调试失败信息-->
						<div id="debug_fail" class="row  alert alert-danger"   role="alert"   style="padding-left: 30px; padding-right: 30px; display:none">   </div>
						
</div><!--container-->
<#else>
     <div class="container  alert alert-warning"> 请先选择右上角项目</div>
</#if>

</@layout.myLayout>