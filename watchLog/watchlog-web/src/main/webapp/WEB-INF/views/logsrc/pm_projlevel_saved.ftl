<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>

<!-- plugin: datetime-picker-->
<link href="/res/css/plugin/datetimepicker/bootstrap-datetimepicker.min.css" rel="stylesheet" />	 
<script src="/res/js/plugin/datetimepicker/moment.min.js"></script>
 <script src="/res/js/plugin/datetimepicker/bootstrap-datetimepicker.min.js"></script>
 <!-- plugin: highcharts-->
  <script src="/res/js/plugin/highcharts/highcharts.js"></script>
<script src="/res/js/plugin/highcharts/no-data-to-display.js"></script>
		 
<!--self defined-->		 
<link rel="stylesheet" href="/res/css/self/logsrc_pm.css" />
<script src="/res/js/self/common_pm.js"></script>
 <script src="/res/js/self/pm_projlevel_saved.js"></script>

  
 
 
  <#if RequestParameters.proj?exists  >
  		<#assign pid = RequestParameters.proj>	
<div class="container-fluid">
    	<div class="row">
   		<div class="col-sm-1"></div>
    	<div class="col-sm-10 "> 
  	
						<!-- 删除当前聚合分析报告form-->
							<form  id="destroy_pm_analyse_single_form" action="/logsrc/pm_analyse/destroy"  method="post"   class="form-horizontal" role="form"   accept-charset="UTF-8"   data-remote="true">     	  
								<!-- 模态框（Modal） -->
								<div class="modal fade" id="destroy_pm_report_single_modal"  tabindex="-1" role="dialog"    aria-labelledby="myModalLabel" aria-hidden="true">
									   <div class="modal-dialog">
												      <div class="modal-content">
												      		<input type="hidden" id="report_id" name="report_id"  />
												      		<input type="hidden" id="proj" name="proj"  />
												      		 <!-- header  -->
													         <div class="modal-header">
														            <button type="button" class="close"    data-dismiss="modal" aria-hidden="true">   &times;  </button>
														            <h4 class="modal-title" id="myModalLabel">    确认删除    </h4>
													         </div>
													         <!-- body  -->
													         <div class="modal-body">
													            	您确定要删除选中当前聚合分析报告？
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
  	
						  	
				  			<!-- 保存聚合分析页面 导航条-->
							<div class="row"   style="border-bottom: solid 1px #ddd;height: 45px;" >
									<div class="col-sm-12"> 
													<table  class="table wratb  removebd">
														<tbody>
																	<tr>
																		<td class="col-sm-2"> <a href="/logsrc/pm_analyse?proj=${pid}"> 返回 &#62; </a>	&#160;&#160;<small>${title}</small></td>
																		<td class="col-sm-3">开始时间：${start_time}</td>
																		<td class="col-sm-3">结束时间：${end_time}</td>
																		<td class="col-sm-2"><button id="remove" class="btn btn-danger  btn-xs"  onclick="pm_analyse_single_destroy(${report_id}, ${pid})" > &#160;&#160;删除当前报告&#160;&#160;</button></td>
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
							            <table id="pm_projlevel_saved_etc_table"     data-toggle="toolbar"   data-side-pagination="server"  data-pagination="true" data-search="false">
									            <thead>
											            <tr>
											            			<th data-field="report_id"  data-visible="false" >id</th>
											              			<th data-field="logsrc_name" data-width="20%" data-formatter="pm_projlevel_saved_etc_table_lognameFormatter">日志源</th>
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