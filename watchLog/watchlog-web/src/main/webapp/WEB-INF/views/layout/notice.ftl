   <div class="container">
   		<#if status?exists && status == 0>
   				<div id="notice" class="row  alert alert-success"   role="alert"  style="padding-left: 30px; padding-right: 30px;">   ${message} </div>
   		<#elseif status?exists && status == -1>
   				<div id="notice" class="row  alert alert-danger"   role="alert"   style="padding-left: 30px; padding-right: 30px;">   ${message} </div>
   		<#else>
   				<div id="notice" class="row  alert alert-info"    role="alert"  style="padding-left: 30px; padding-right: 30px;">   ${message} </div>	
   		</#if>
 	</div>

	