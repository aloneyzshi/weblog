<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>

<!-- plugin: datetime-picker-->
<link href="/res/css/plugin/datetimepicker/bootstrap-datetimepicker.min.css" rel="stylesheet" />	 
<script src="/res/js/plugin/datetimepicker/moment.min.js"></script>
 <script src="/res/js/plugin/datetimepicker/bootstrap-datetimepicker.min.js"></script>
		 
<!-- self defined-->		 
<link rel="stylesheet" href="/res/css/self/logsrc_pm.css" />
<script src="/res/js/self/pm_analyse.js"></script>

  <#if RequestParameters.proj?exists  >
  	<#assign pid = RequestParameters.proj>
		        
	  <div class="container-fluid">
	    	<div class="row">
	   		<div class="col-sm-1"></div>
	    	<div class="col-sm-10 "> 

							<!-- 生成聚合报告导航表单-->
    						<form  id="get_pm_repost_single_form" action="/logsrc/pm_projlevel_unsave"     method="get"   class="form-horizontal" role="form"   accept-charset="UTF-8"   data-remote="true"  onsubmit="return check_pm_analyse_view()"> 
										<div class="row"  style="height: 50px;">
													  <div class="col-sm-1">
													  				 <h5><p class="text-left" style="font-size: 13px;font-weight: bold;">选择起始时间</p></h5>
													  	</div>				
													  	<div class="btn-toolbar col-sm-4"  style="padding-left: 0px;">			
													  			<#assign hour_minutes=60>
													  			<#assign three_minutes=3*60>
													  			<#assign six_minutes=6*60>
													  			<#assign one_day_minutes=24*60>
													  			<#assign three_day_minutes=3*24*60>
															<div class="btn-group"><button  class="btn btn-default  btn-sm"   type="button"  onclick="pm_time_select(${hour_minutes})">  最新1小时 </button> &nbsp; &nbsp; </div>  
															<div class="btn-group"><button  class="btn btn-default  btn-sm"   type="button"  onclick="pm_time_select(${three_minutes})">  最新3小时</button> &nbsp; &nbsp; </div>  
															<div class="btn-group"><button  class="btn btn-default  btn-sm"   type="button"  onclick="pm_time_select(${six_minutes})">  最新6小时 </button> &nbsp; &nbsp; </div>  
															<div class="btn-group"><button  class="btn btn-default  btn-sm"    type="button"  onclick="pm_time_select(${one_day_minutes})">  最新1天 </button> &nbsp; &nbsp; </div>  
															<div class="btn-group"><button  class="btn btn-default  btn-sm"     type="button" onclick="pm_time_select(${three_day_minutes})">  最新3天</button> &nbsp; &nbsp; </div>  
													  	</div>
				 										<div class="col-sm-2">									  				
													                <div class='input-group date' id='pm_start_time_datetimepicker'>
														                    <input type='text'   id="pm_start_time_id"   name="start_time" class="form-control  input-sm "   placeholder="开始时间"/>
														                    <span class="input-group-addon" onclick="clear_input_start_time()">
														                        <span class="glyphicon glyphicon-calendar"></span>
														                    </span>
													                </div>
													  </div>
													  <div class="col-sm-2">
													                <div class='input-group date' id='pm_end_time_datetimepicker'>
														                    <input type='text'    id="pm_end_time_id"   name="end_time" class="form-control    input-sm"   placeholder="结束时间" />
														                    <span class="input-group-addon" onclick="clear_input_end_time()">
														                        <span class="glyphicon glyphicon-calendar"></span>
														                    </span>
													                </div>									    			
													  </div>
													  <div class="col-sm-1">
													  				  <button  type="submit" class="btn btn-primary  btn-sm"  >  查看聚合报告 </button>
													  </div>  													  
								   </div> <!-- row end-->   
     						</form>	    
											</br>
											<div id="pm_notice" class="row"   style="padding-left: 15px; padding-right: 15px;" > </div></br>     						
		    
							<!-- 删除聚合报告表单form-->
							<form  id="destroy_pm_logsrc_single_form" action="/logsrc/pm_analyse/destroy"  method="post"   class="form-horizontal" role="form"   accept-charset="UTF-8"   data-remote="true">     	  
								<!-- 模态框（Modal）for: 删除日志源 二次确认对话框 -->
								<div class="modal fade" id="destroy_pm_table_single_modal" tabindex="-1" role="dialog"    aria-labelledby="myModalLabel" aria-hidden="true">
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
													            	您确定要删除选中历史报告吗？
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
						    
						            <table id="pmtable"    data-toggle="toolbar"    data-side-pagination="server"  data-pagination="true" data-search="false">
								            <thead>
										            <tr>
										              			<th data-field="title" data-sortable="true"  data-formatter="pmreportnameFormatter">报告名称</th>
												                <th data-field="report_id"  data-sortable="true"  data-visible="false">ID</th>
												                <th data-field="start_time" data-sortable="true"   >开始时间</th>
												                 <th data-field="end_time" data-sortable="true"  >结束时间 </th>
												                 <th data-field="create_time"    data-sortable="true" >创建时间</th>
												                  <th data-field="operate"    data-formatter="pmoperateFormatter" >操作</th>
										            </tr>
								            </thead>
						        </table>
					</div><!-- col-sm-10 -->			
					<div class="col-sm-1"></div>	        
				 </div><!-- row -->
    </div><!-- container -->		        
        
<#elseif  !RequestParameters.proj?exists >
	   <div class="container  alert alert-warning"> 请先选择右上角项目</div>
<#else>
	<div class="container  alert alert-warning">其他错误，请联系管理员</div>
</#if>
    

</@layout.myLayout>