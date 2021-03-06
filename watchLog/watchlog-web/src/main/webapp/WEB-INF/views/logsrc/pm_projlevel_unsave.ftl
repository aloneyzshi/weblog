<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>

<!-- plugin: datetime-picker-->
<link href="/res/css/plugin/datetimepicker/bootstrap-datetimepicker.min.css" rel="stylesheet" />	 
<script src="/res/js/plugin/datetimepicker/moment.min.js"></script>
 <script src="/res/js/plugin/datetimepicker/bootstrap-datetimepicker.min.js"></script>
 
 <!-- plugin: highcharts-->
  <script src="/res/js/plugin/highcharts/highcharts.js"></script>
<script src="/res/js/plugin/highcharts/no-data-to-display.js"></script>
		 
 <!-- self defined-->		 		 
<link rel="stylesheet" href="/res/css/self/logsrc_pm.css" />
 <script src="/res/js/self/common_pm.js"></script>
<script src="/res/js/self/pm_projlevel_unsave.js"></script>
 
  <#if RequestParameters.proj?exists  >
  		<#assign pid = RequestParameters.proj>	
<div class="container-fluid">
    	<div class="row">
   		<div class="col-sm-1"></div>
    	<div class="col-sm-10 "> 
  	
							<!-- 保存聚合分析表单form-->
							<form  id="pm_analyse_store_form"  onsubmit="return check_pm_report_name()" action="/logsrc/pm_analyse/store"  method="post"   class="form-horizontal" role="form"   accept-charset="UTF-8"   data-remote="true">     	  
								<!-- 模态框（Modal）-->
								<div class="modal fade" id="pm_analyse_store_modal" tabindex="-1" role="dialog"    aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
									   <div class="modal-dialog">
												      <div class="modal-content">
												      		<input type="hidden" id="proj" name="proj"  />
												      		<input type="hidden" id="start_time" name="start_time"  />
												      		<input type="hidden" id="end_time" name="end_time"  />
												      		 <!-- header  -->
													         <div class="modal-header">
														            <button type="button" class="close"    data-dismiss="modal" aria-hidden="true">   &times;  </button>
														            <h4 class="modal-title" id="myModalLabel"> 保存当前项目的聚合报告 </h4>
													         </div>
													         <!-- body  -->
													         <div class="modal-body"    style="height: 80px;">
																		<label for="host_name" class="col-sm-2 control-label"  style="text-align: center;color: gray;font-weight: 100;">报告名<font color="red">&#160;*</font></label>
																	      <div class="col-sm-10">
																	         		<input type="text" class="form-control"    id="report_title"     name="title"   placeholder="请输入报告名">
																	          		<div id="warn_report_comment"></div>
																	      </div>
																	      <div class="row" style="padding-left: 15px; padding-right: 15px; overflow: auto; ">
																			  			<div class="col-sm-10 pull-right"  id="title_notice"></div>
																			</div>
													         </div>
													         <!-- footer  -->
													         <div class="modal-footer"  style="border-top: 0;">
													           <button type="submit" class="btn btn-primary">  确定  </button>
													            <button type="button" class="btn btn-default"    data-dismiss="modal"> 取消  </button>
													         </div>
												      </div><!-- /.modal-content -->
										</div><!-- /.modal-dialog -->
								</div><!-- 模态框（Modal） -->
							</form>	  	
  	
					  			<!-- 聚合分析 导航条-->
								<div class="row"   style="border-bottom: solid 1px #ddd;height: 45px;" >
										<div class="col-sm-12"> 
														<table  class="table wratb  removebd">
															<tbody>
																		<tr>
																		<td class="col-sm-2"> <a href="/logsrc/pm_analyse?proj=${pid}"> 返回 &#62; </a>	</td>
																			<td class="col-sm-3">开始时间：${start_time}</td>
																			<td class="col-sm-3">结束时间：${end_time}</td>
																			<td class="col-sm-2"><button id="remove" class="btn btn-primary  btn-xs"  onclick="pm_analyse_store()" >  &#160;&#160; 保存 &#160;&#160;</button></td>
																		</tr>		
															</tbody>
														</table>
										</div><!-- /col-sm-12 -->			
								</div><!-- /row -->			
								
								<div class="row">
										<div class="col-sm-12"> <p style="font-size: 15px;font-weight: bold;">异常分布情况</p>	</div><!-- /col-sm-12 -->		
								</div>									
								<!-- 聚合分析 异常分布表格-->
								<div class="row"   >
							            <table id="pm_projlevel_unsave_etc_table"    data-toggle="toolbar"   data-side-pagination="server"  data-pagination="true" data-search="false">
									            <thead>
											            <tr>
											            			<th data-field="log_id"  data-visible="false" >id</th>
											              			<th data-field="logsrc_name" data-width="20%" data-formatter="pm_projlevel_unsave_etc_table_lognameFormatter">日志源</th>
													                <th data-field="error_tc"  data-width="70%" data-formatter="pm_projlevel_etc_table_disterrorFormatter"   > 异常类型和数量</th>
													                <th data-field="total_count"  data-width="10%" data-sortable="false"  > 异常总数 </th>
											            </tr>
									            </thead>
							        </table>			
							</div><!-- /row -->			
						
							<div class="row"    style="height:25px;   border-bottom: solid 1px #ddd; margin-bottom:30px;margin-top:30px;">
									<div class="col-sm-12"> <p style="font-size: 15px;font-weight: bold;">异常分布趋势图</p>	</div><!-- /col-sm-12 -->		
							</div>		
							<!-- loading-->						
                                <div  class='row'   style="display:none; "   id="rt_loading" >
                                            <div  class="col-sm-7 pull-right" >
                                                        <img src="/res/img/loading.gif" />
                                            </div>            
                                </div>		
                                <div id="pm_projlevel_notice"> </div>
                                					
							<!-- 聚合分析 异常分布图-->
							<div class="row"    id = "pm_projlevel_etc_chats">	</div><!-- /row -->													        				
			
			
			
		</div><!-- col-sm-10 -->			
		<div class="col-sm-1"></div>	        
	 </div><!-- row -->
</div><!-- container -->	
				
<#elseif  !RequestParameters.proj?exists >
	   <div class="container  alert alert-warning"> 请先选择右上角项目</div>
<#else>
	<div class="container  alert alert-warning">其他位置错误，请联系管理员</div>
</#if>
  

 
</@layout.myLayout>