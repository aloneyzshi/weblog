<#import "../layout/tableLayout.ftl" as layout>
<@layout.tbLayout title="更多-异常类型">

    <div class="container-fluid">
	
    	<div class="row">
	   		<div class="col-sm-1"></div>
	    	<div class="col-sm-10 "> 
					<div class="row"    style="height:25px;   border-bottom: solid 1px #ddd; margin-bottom:30px;margin-top:30px;">
							<div class="col-sm-12"> <p style="font-size: 15px;font-weight: bold;">异常类型详情</p>	</div><!-- /col-sm-12 -->		
					</div>		
					
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="error_type_total_more_modal"  tabindex="-1" role="dialog"    aria-labelledby="myModalLabel" aria-hidden="true"   data-backdrop="static">
										   <div class="modal-dialog">
													      <div class="modal-content">
													      		 <!-- header  -->
														         <div class="modal-header">
															            <button type="button" class="close"    data-dismiss="modal" aria-hidden="true"   >   &times;  </button>
															            <h4 class="modal-title" id="myModalLabel">    异常类型详情 </h4>
														         </div>												      		
														         <!-- body  -->
														         <div class="modal-body">
																	           <table id="error_type_total_more_table"    data-toggle="toolbar"     data-height="500"     data-side-pagination="server"    data-pagination="true"      data-search="false" >
																	            <thead>
																	            <tr>
																	                <th data-field="date_time"   data-sortable="true"    > 采样时间</th>
																	                 <th data-field="total_count" data-sortable="true"  > 异常总数</th>
																	            </tr>
																	            </thead>
																	        </table>													            	
														         </div>
													      </div><!-- /.modal-content -->
											</div><!-- /.modal-dialog -->
									</div><!-- 模态框（Modal） -->					
						
							           <table id="pm_error_type_table"    class="wratb" data-toggle="toolbar"        data-side-pagination="server"            data-pagination="true"      data-search="false">
									            <thead>
											            <tr>
											                <th data-field="error_type"     data-width="42%" data-formatter="pmtypeTypeFormatter"  > 异常类型 </th>
											                <th data-field="error_example"   data-width="50%"  data-formatter="pmtypeExampleFormatter"  > 原始日志实例</th>
											                 <th data-field="total_count" data-width="8%"  data-sortable="true"  data-formatter="pmtypeTotalFormatter" > 异常总数 </th>
											            </tr>
									            </thead>
							        </table>
					</div><!-- col-sm-10 -->			
					<div class="col-sm-1"></div>	        
			 </div><!-- row -->					        
	</div>


</@layout.tbLayout>
