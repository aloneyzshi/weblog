<nav class="navbar navbar-default mid-menu">
  <div class="container">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
     </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav" id="log_menu">
		      <!-- 获取project.id -->
		      <#if RequestParameters.proj?exists >
		      	<#assign proj=RequestParameters.proj>  		      	
			      	    <#if controller == "WlogHome">
			      			<li class="active"><a   class="text-menu"   href="/?proj=${proj}">首页</a></li>
			      		<#else>
			      			<li><a class="text-menu"   href="/?proj=${proj}">首页</a></li>
			      		</#if>  
			      	   <#if controller == "WlogManage">
							<li class="active"><a   class="text-menu"   href="/logsrc/manage?proj=${proj}">日志源管理</a></li>		      	   
			      		<#else>
			      		   <li><a class="text-menu"   href="/logsrc/manage?proj=${proj}">日志源管理</a></li>
			      		</#if>  	
			      	   <#if controller == "WlogRT">
			      	       <li class="active"><a    class="text-menu"   href="/logsrc/rt_analyse?proj=${proj}">实时分析</a></li>
			      		<#else>
			      		   <li><a class="text-menu"   href="/logsrc/rt_analyse?proj=${proj}">实时分析</a></li>
			      		</#if>  	
			      	   <#if controller == "WlogPM">
			      	       <li class="active"><a    class="text-menu"  href="/logsrc/pm_analyse?proj=${proj}">聚合分析</a></li>        
			      		<#else>
			      			<li><a class="text-menu"  href="/logsrc/pm_analyse?proj=${proj}">聚合分析</a></li>
			      		</#if>  	
			      	   <#if controller == "WlogAlarm">
			      	        <li class="active"><a     class="text-menu"  href="/logsrc/alarm?proj=${proj}" >报警管理</a></li>     
			      		<#else>
			      		    <li><a class="text-menu"  href="/logsrc/alarm?proj=${proj}" >报警管理</a></li>     
			      		</#if>  		 				      		
		      	<#else>
			      	    <#if controller == "WlogHome">
			      			<li class="active"><a   class="text-menu"   href="/">首页</a></li>
			      		<#else>
			      			<li><a class="text-menu"   href="/">首页</a></li>
			      		</#if>  
			      	   <#if controller == "WlogManage">
							<li class="active"><a   class="text-menu"   href="/logsrc/manage">日志源管理</a></li>		      	   
			      		<#else>
			      		   <li><a class="text-menu"   href="/logsrc/manage">日志源管理</a></li>
			      		</#if>  	
			      	   <#if controller == "WlogRT">
			      	       <li class="active"><a    class="text-menu"   href="/logsrc/rt_analyse">实时分析</a></li>
			      		<#else>
			      		   <li><a class="text-menu"   href="/logsrc/rt_analyse">实时分析</a></li>
			      		</#if>  	
			      	   <#if controller == "WlogPM">
			      	       <li class="active"><a    class="text-menu"  href="/logsrc/pm_analyse">聚合分析</a></li>        
			      		<#else>
			      			<li><a class="text-menu"  href="/logsrc/pm_analyse">聚合分析</a></li>
			      		</#if>  	
			      	   <#if controller == "WlogAlarm">
			      	        <li class="active"><a     class="text-menu"  href="/logsrc/alarm" >报警管理</a></li>     
			      		<#else>
			      		    <li><a class="text-menu"  href="/logsrc/alarm" >报警管理</a></li>     
			      		</#if>  		    	
		      	</#if>
      </ul>
      
	       <div class="pull-right  col-md-2" style="padding-top: 10px;">
	          		<select   id="project_select"  style="width:100%" onchange="window.location.href=this.options[selectedIndex].value" >
	    		</select>
	    	</div>
    			
      </div><!-- /.navbar-collapse -->
  </div><!-- /.container -->
</nav>

