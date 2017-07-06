$(function () {
		$(document).ready(function() {
						// 生成表格
						render_table('#pm_projlevel_unsave_etc_table');

					// 项目级聚合分析趋势图
					if($("#pm_projlevel_etc_chats" ).length != 0) {
							// 检测时间范围
							var MAX_GRAPH_TIME_RANGE = 7*24*3600*1000; // 不能超过的7天，精确到毫秒
							if(end_time_ct !="" &&  start_time_ct!="" && datetime2timestamp(end_time_ct) -  datetime2timestamp(start_time_ct) > MAX_GRAPH_TIME_RANGE ){
									$('#pm_projlevel_notice').css('display','block'); // 失败提示文字
									$("#pm_projlevel_notice").html("<font color='red'>只支持查询7天内的数据</font>")
							}
							else{
									// ajax 请求获取chart数据
									$('#pm_projlevel_notice').css('display','none'); // 失败提示文字
									$('#rt_loading').css('display','block');
									$.ajax({
										type: 'GET',
										url : '/logsrc/pm_projlevel_etc_charts',
										data: {proj: pid, 
											report_id: report_id, 
											start_time: start_time_ct, 
											end_time: end_time_ct},
										dataType: "json",
										success : function(e){
											$('#rt_loading').css('display','none');
												if(e['status'] == 0){
													// 生成趋势图
													draw_charts('#pm_projlevel_etc_chats', e['results']);
												}
												else{
													$('#pm_projlevel_notice').css('display','block'); // 失败提示文字
													$("#pm_projlevel_notice").html("<font color='red'>"+e['message']+"</font>")
												}
										} // success
									}); // ajax							
							}// else
					}// 趋势图
		} );  //document
});




//聚合报告-项目级-异常分布    日志源显示格式   未保存
function pm_projlevel_unsave_etc_table_lognameFormatter(value, row, index){
	  if(value!=undefined){
		     var maxwidth = 28; 
		     var value_show = value;
		     if (value.length > maxwidth-3) {
		    	 value_show = value.substring(0, maxwidth-3) + '...'
		     }
		     var link_html =  '<a target="_blank" title=' + value +' href="/logsrc/pm_analyse_unsave?proj=' + pid + '&log_id=' + row.log_id +'&start_time=' + start_time_ct + '&end_time=' + end_time_ct + '" >&nbsp;&gt;&gt;查看详情</a>';	
		     var display_html = '<font size=2px>'+value_show+ link_html + '</font>';
		  return  display_html;		  
	  }
}


//校验保存聚合报告名称
function check_pm_report_name(){
	// 报告名
	var report_title = $.trim($('#report_title').val());
	if(report_title == "" ){
		 $("#title_notice").html("<font color='red'> 聚合报告名称不能为空</font></br>");
		 return false;
	}
	else if(/^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]{1,255}$/.test(report_title) == false){
		$('#title_notice').html("<font color='red'>不能以下划线开始和结尾;  <br>" +
				"只能包含英文字母，数字，汉字; <br>" +
				"长度不能超过255;</font>");
		return false
	}	
	else{
		 $("#pm_notice").html("");
	}		
}

//点击保存聚合分析，弹出对话框
function pm_analyse_store(){
	 $('#proj').val(pid);  //post请求参数 proj
	 $('#start_time').val(start_time_ct);  //post请求参数 start_time
	 $('#end_time').val(end_time_ct);  //post请求参数 end_time
	$('#pm_analyse_store_modal').modal('show');  //弹窗modal			
}
