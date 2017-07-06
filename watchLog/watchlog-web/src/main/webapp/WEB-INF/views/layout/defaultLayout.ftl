<#macro myLayout>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <title>日志分析系统</title>
       <!--  jquery  -->    
     <script src="/res/js/jquery-1.11.3.min.js"></script>
         
     <!--  bootstrap  -->    
    <link rel="stylesheet" href="/res/css/bootstrap.min.css" />
	<script src="/res/js/bootstrap.min.js"></script>
	
	<!--  select2 plugin -->
     <link href="/res/css/plugin/select2/select2.min.css" rel="stylesheet" />
	<script src="/res/js/plugin/select2/select2.min.js"></script>
	
	<!-- table plugin -->
	     <link href="/res/css/plugin/table/bootstrap-table.min.css" rel="stylesheet" />
		<script src="/res/js/plugin/table/bootstrap-table.min.js"></script>	 
		
		<!-- 导航 -->
      <link rel="stylesheet" media="all" href="/res/css/qadev-main-nav.css" >
      <script src="/res/js/qadev-main-nav-platforms.js"></script>
      <script src="/res/js/qadev-main-nav.js"></script>
      		
     <!--  self defined  -->    
	 <link rel="stylesheet" href="/res/css/self/common.css" />
	 <script src="/res/js/self/common.js" ></script>
  </head>
  
  <body>
   	<div class="wrapper">
		<#include "header.ftl"/>
	     <#include "menu.ftl"/>
	     <#if message?exists>	 <#include "notice.ftl"/> </#if> 	
	     	<#nested>     	
    </div>
    <#include "footer.ftl"/>
  </body>
</html>
</#macro>