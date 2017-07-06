

  $(document).ready(function() {
	  //  聚合分析首页 开始时间datatimepicker
      $('#pm_start_time_datetimepicker').datetimepicker({
		   // format: 'YYYY-MM-DD HH:mm'
       	  format: 'YYYY-MM-DD HH:mm:ss'   
       });
       
       //   聚合分析首页结束时间datatimepicker
       $('#pm_end_time_datetimepicker').datetimepicker({
		    format: 'YYYY-MM-DD HH:mm:ss'   
       });   	  
       
	// 聚合分析首页表格
	  if($("#pmtable" ).length != 0) {
			  	$('#pmtable').bootstrapTable({
			  		url : "/logsrc/pm_analyse/pmtable",
			  		striped: true,
			  		sortName : "create_time",
			  		sortOrder: "desc",
			  		pageList: "[10, 25, 50, 100, All]",
			  		queryParams: function(p){
			  			return {
			  				proj : pid,
			  				limit: p.limit,
			  				offset : p.offset,
			  				sort: p.sort,
			  				order: p.order
			  			}
			  		}
			  	});
	  }//  表格end
	  
        // unsave页面popover
        popover_setting();
        
} );  //document
  
  
  
//点击时间插件之前先清理输入框中可能被填充的内容，否则datetimepicker无法正常工作
  function clear_input_start_time(){
  	$('#pm_start_time_id').val(''); 
  }
  
  function clear_input_end_time(){
  	$('#pm_end_time_id').val(''); 
  }
    
  
  //聚合报告名称显示   
  function pmreportnameFormatter(value, row, index) {
 	  if(row.report_id==undefined){
 		return "-";	  
 	  }
 	     var maxwidth = 40; 
 	     var value_show = value;
 	     if (value.length > maxwidth) {
 	    	 value_show = value.substring(0, maxwidth) + '...'
 	     }
 	  return  '<a title=' + value +' href="/logsrc/pm_projlevel_save?report_id=' + row.report_id + '&proj=' + pid + '" >' + value_show + '</a>';
  }

  // 操作
  function pmoperateFormatter(value, row, index){
	  if(row.report_id==undefined){
			return "-";	  
		  }
	  else{
		  return [
		          	 // 删除功能
		            '<a class="copy" href="javascript:void(0)"  title="删除"  onclick=pm_report_destroy('+row.report_id+','+ pid + ')>',
		            '<i class=" glyphicon glyphicon-trash"></i>',
		            '</a>  ',	            
		            // 查看详情
		           '<a class="pmshow" title="详情" href="/logsrc/pm_projlevel_save?report_id=' + row.report_id + '&proj=' + pid + '" >',
		            '<i class="  glyphicon glyphicon-align-left"></i>',
		            '</a>'
		        ].join('');
	  }
 }  
  
	// 详情：删除日志报告 + 二次确认 ( 单个删除)
	function pm_report_destroy(report_id, proj){
		console.log(report_id);
		console.log(pid);
		 $('#report_id').val(report_id);  //post请求参数 ids
		 $('#proj').val(proj);  //post请求参数 projj
		$('#destroy_pm_table_single_modal').modal('show');  //弹窗modal		
	}
  
  
//选择时间段
  function pm_time_select(duration){
  	// 先清空之前的内容
  	$('#pm_start_time_id').val(''); 
  	$('#pm_end_time_id').val(''); 
  	// 获取时间段的开始和结束时间
  	var duration_ms = duration * 60 * 1000; // 将分钟转换为毫秒
  	var current_time = new Date();   // 当前时间对象
  	var passed_time = new Date(current_time.getTime() - duration_ms);   // duration之前时间点对象
  	 //时间对象转换为可读string格式
  	var current_time_str = timestamp2datetime(current_time);    // 结束时间
  	var passed_time_str  = timestamp2datetime(passed_time);  // 开始时间
  	// 填充input输入框
  	$('#pm_start_time_id').val(passed_time_str); 
  	$('#pm_end_time_id').val(current_time_str); 
  }
  
  
//查看聚合报告校验
  function check_pm_analyse_view(){
  			// 开始时间
  			var start_time = $('#pm_start_time_id').val(); 
  			if(start_time == "" ){
  				 $("#pm_notice").html("<font color='red'> 开始时间不能为空</font></br>");
  				 return false;
  			}
  			else{
  				 $("#pm_notice").html("");
  			}
  			// 结束时间
  			var end_time =  $('#pm_end_time_id').val(); 
  			if(end_time == "" ){
  				 $("#pm_notice").html("<font color='red'> 结束时间不能为空</font></br>");
  				 return false;
  			}
  			else{
  				 $("#pm_notice").html("");
  			}
  			// 开始时间不能大于结束时间
  			if(  datetime2timestamp(start_time)  >   datetime2timestamp (end_time) ) {
  				$("#pm_notice").html("<font color='red'>  开始时间不能大于结束时间 </font></br>");
  				 return false;
  			}
  			else{
  				 $("#pm_notice").html("");
  			}					
  			// 提交 查看聚合报告
  			$('#get_pm_repost_single_form').append('<input type="hidden" name="proj" value='+pid+' /> ');   
  		   return true;
  }
  
