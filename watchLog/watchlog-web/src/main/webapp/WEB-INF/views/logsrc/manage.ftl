<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>

<!--self defined-->
<link rel="stylesheet" href="/res/css/self/logsrc_manage.css" />
 <script src="/res/js/self/manage.js"></script>
		  
  <#if RequestParameters.proj?exists >
  	<#assign pid = RequestParameters.proj>
    <div class="container-fluid">
    	<div class="row">
   		<div class="col-sm-1"></div>
    	<div class="col-sm-10 "> 
						<form  id="destroy_logsrc_form" action="/logsrc/destroy"  method="post"   class="form-horizontal" role="form"   accept-charset="UTF-8"   data-remote="true">     	  
							<!-- 模态框（Modal）for: 删除日志源 二次确认对话框 -->
							<div class="modal fade" id="destroy_logsrc_modal" tabindex="-1" role="dialog"    aria-labelledby="myModalLabel" aria-hidden="true">
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

					    <div id="toolbar">
						      <a class="btn btn-primary btn-sm margin-right-10" href="/logsrc/new?proj=${pid}" role="button"  >创建日志源</a>
						      <button id="remove" class="btn btn-primary btn-sm margin-right-10"  onclick="delete_logsrc_table()"   > 删除日志源 </button>
					         	<button  class="btn btn-primary btn-sm margin-right-10"  onclick="startMonitorLogsrc()"    > 开始监控 </button>
					         	<button  class="btn btn-primary btn-sm margin-right-10"  onclick="stopMonitorLogsrc()"     > 停止监控 </button>
					    </div>
				 
 						<div id="js_notice" class="row"   style="padding-left: 15px; padding-right: 15px;" > </div></br>
 
				        <table id="logtable"    data-toggle="toolbar"     data-side-pagination="server"   data-pagination="true"   data-search="false">
				            <thead>
						            <tr>
						                <th data-field="state" data-checkbox="true" ></th>
						                <th data-field="id"  data-sortable="true"  data-visible="false">ID</th>
						                <th data-field="logsrc_name"   data-sortable="true"   data-formatter="logsrcnameFormatter"  >日志源名称</th>
						                <th data-field="host_name" data-sortable="true"    data-formatter="hostnameFormatter">服务器地址</th>
						                 <th data-field="logsrc_path" data-sortable="true"   data-formatter="logsrcpathFormatter" >日志源地址 </th>
						                 <th data-field="logsrc_file" data-sortable="true"  data-formatter="logsrcfileFormatter" >日志文件名称</th>
						                 <th data-field="status"   data-formatter="statusFormatter"  data-sortable="true" >监控状态</th>
						                 <th data-field="update_time" data-sortable="true" >更新时间</th>
						                  <th data-field="operate"    data-formatter="operateFormatter">操作</th>
						            </tr>
				            </thead>
				        </table>
						</div><!-- col-sm-10 -->			
						<div class="col-sm-1"></div>	        
				 </div><!-- row -->
    </div><!-- container -->
        
<#else>
     <div class="container  alert alert-warning"> 请先选择右上角项目</div>
</#if>
    

    
</@layout.myLayout>