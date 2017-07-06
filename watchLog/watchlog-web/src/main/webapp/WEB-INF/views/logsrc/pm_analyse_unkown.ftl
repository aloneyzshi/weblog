<#import "../layout/tableLayout.ftl" as layout>
<@layout.tbLayout title="unknown详情">

    <div class="container-fluid">
    	<div class="row">
	   		<div class="col-sm-1"></div>
	    	<div class="col-sm-10 "> 
		
						<div class="row"    style="height:25px;   border-bottom: solid 1px #ddd; margin-bottom:30px;margin-top:30px;">
								<div class="col-sm-12"> <p style="font-size: 15px;font-weight: bold;">unknow类型原始日志</p>	</div><!-- /col-sm-12 -->		
						</div>		
							
				           <table id="pm_unknow_list_table"    data-toggle="toolbar"        data-side-pagination="server"            data-pagination="true"      data-search="false">
						            <thead>
								            <tr>
								                <th data-field="sample"  data-formatter="pmunknownExampleFormatter" > 原始日志实例</th>
								            </tr>
						            </thead>
				        </table>
			</div><!-- col-sm-10 -->			
			<div class="col-sm-1"></div>	        
	 </div><!-- row -->					        
		        		 			
	</div>


</@layout.tbLayout>
