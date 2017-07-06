<!-- 头部开始 -->
<div id="qadev-main-nav">
</div>

<script>
	<#if user ? exists>
	$("#qadev-main-nav").qadev_main_nav("WL", '${user.fullname ! "匿名"}');
	<#else>
	$("#qadev-main-nav").qadev_main_nav("WL", null);
	</#if>
</script>

<!-- 头部结束 -->