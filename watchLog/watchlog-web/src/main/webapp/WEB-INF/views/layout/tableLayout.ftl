  <#macro tbLayout title>
  <!DOCTYPE html>
<html>
		  <head>
			    <meta charset="utf-8" />
			    <title>${title}</title>
			       <!--  jquery  -->    
			     <script src="/res/js/jquery-1.11.3.min.js"></script>
			         
			     <!--  bootstrap  -->    
			    <link rel="stylesheet" href="/res/css/bootstrap.min.css" />
				<script src="/res/js/bootstrap.min.js"></script>
				
				<!-- table plugin -->
			     <link href="/res/css/plugin/table/bootstrap-table.min.css" rel="stylesheet" />
				<script src="/res/js/plugin/table/bootstrap-table.min.js"></script>
					
			     <!--  self defined  -->    
			     <link href="/res/css/self/pm_more_table.css" rel="stylesheet" />
				  <script src="/res/js/self/pm_more_table.js" ></script>
		  </head>
  <body>
	   	<div class="wrapper">
		     <#if message?exists>	 <#include "notice.ftl"/> </#if> 	
		     	<#nested>     	
	    </div>
  </body>
</html>		  
</#macro>