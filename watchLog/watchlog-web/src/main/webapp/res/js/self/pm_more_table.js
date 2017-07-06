
// 获取url上特定字段值js片段
function getParam( name )
  {
	   name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	   var regexS = "[\\?&]"+name+"=([^&#]*)";
	   var regex = new RegExp( regexS );
	   var results = regex.exec( window.location.href );
	   if( results == null )
	    return "";
	  else
	   return results[1];
  }

var pid = getParam( 'proj' );
var report_id = getParam( 'report_id' );
var log_id = getParam( 'log_id' );
var start_time = getParam( 'start_time' );
var end_time = getParam( 'end_time' );
var start_time_ct = start_time.replace(/\%20/g, ' ');
var end_time_ct = end_time.replace(/\%20/g, ' ');


  $(document).ready(function() {
	  // 异常分布情况 - 更多 表格
	  if($("#pm_error_dist_table" ).length != 0) {
					  // 聚合分析 - 异常分布情况 表格
					  	$('#pm_error_dist_table').bootstrapTable({
					  		url : "/logsrc/pm_analyse/error_dist_table",
					  		striped: true,
					  		onPostBody: function () {
					  				// 启动popover 
						  			$("[data-toggle='popover']").popover(); 	  		
						  			// 每次点击只展示当前点击的popover
						  			$("[data-toggle='popover']").on('click', function (e) {
						  			    $("[data-toggle='popover']").not(this).popover('hide');
						  			});				
						  			// 点击空白处所有popover消失
						  			$('body').on('click', function (e) {
						  			    //did not click a popover toggle or popover
						  			    if ($(e.target).data('toggle') !== 'popover'
						  			        && $(e.target).parents('.popover.in').length === 0) { 
						  			        $('[data-toggle="popover"]').popover('hide');
						  			    }
						  			});						  			
					  		  },	  						  		
					  		sortName : "date_time",
					  		sortOrder: "desc",
					  		pageList: "[20, 40, 80, 100]",  
					  		pageSize: "20",  // 默认展示条目个数
					  		queryParams: function(p){
					  			return {
					  				proj : pid,
					  				report_id: report_id,
					  				log_id : log_id,
					  				start_time : start_time_ct,
					  				end_time : end_time_ct,
					  				limit: p.limit,
					  				offset : p.offset,
					  				sort: p.sort,
					  				order: p.order
					  			}
					  		}
					  	});//  表格end		  
		}
	  
	  // 异常类型详情 - 更多 表格
	  if($("#pm_error_type_table" ).length != 0){
				  // 聚合分析 - 异常分布情况 表格
				  	$('#pm_error_type_table').bootstrapTable({
				  		url : "/logsrc/pm_analyse/error_type_table",
				  		striped: true,
				  		sortName : "total_count",
				  		sortOrder: "desc",
				  		pageList: "[20, 40, 80, 100]",
				  		pageSize: "20",
				  		queryParams: function(p){
				  			return {
				  				proj : pid,
				  				report_id: report_id,
				  				log_id : log_id,
				  				start_time : start_time_ct,
				  				end_time : end_time_ct,
				  				limit: p.limit,
				  				offset : p.offset,
				  				sort: p.sort,
				  				order: p.order
				  			}
				  		}
				  	});//  表格end		    
	  }
	  
	  // unknown - 更多 表格
	  if($("#pm_unknow_list_table" ).length != 0){
				  // 聚合分析 - 异常分布情况 表格
				  	$('#pm_unknow_list_table').bootstrapTable({
				  		url : "/logsrc/pm_analyse/unknown_table",
				  		striped: true,
				  		pageList: "[50, 80, 100]",
				  		pageSize: "50",
				  		queryParams: function(p){
				  			return {
				  				proj : pid,
				  				report_id: report_id,
				  				log_id : log_id,
				  				start_time : start_time_ct,
				  				end_time : end_time_ct,
				  				limit: p.limit,
				  				offset : p.offset,
				  			}
				  		}
				  	});   
	  }	  //  表格end		 
	  
 } );  //document
  
  // 异常分布情况 (Error type & count) 显示格式 :  数字+hover信息
  function pmdisterrorFormatter(value, row, index){
	  var each_val = "";
	  var content_arr = [];

	  // 遍历数字，拼接type和count到超链接
	  var i;
	  for(i=0; i<value.length; i++){
//		  if((i%2) == 0 ){
			  	each_val = "<a class='pointer_a'  data-toggle='popover' data-placement='top'  title='异常类型' data-content='"+value[i]['type']+ "'>"+value[i]['count']+" </a>";
//		  }else{
//			  each_val = "<a class='pointer_a'  data-toggle='popover' data-placement='bottom'  title='异常类型'  data-content='"+value[i]['type']+ "'>"+value[i]['count']+" </a>";
//		  }
		  content_arr.push(each_val);
	  }
	  var  content_str = content_arr.join(' , ');
	  return content_str
  }
  
  // 异常类型详情 更多表格 异常类型 显示格式
  function pmtypeTypeFormatter(value, row, index){
	  // \t 转换为换行
 	  if(row.exp_id==undefined){
			return "-";	  
 	  }else{
 		 return value.replace(/\\t+/g, "<br /> &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;");
 	  }	  
  }
  
  // 异常类型详情 更多表格  异常实例 显示格式
  function pmtypeExampleFormatter(value, row, index){
 	  if(row.exp_id==undefined){
			return "-";	  
 	  }
 	  // 如果是unknow类型
 	  if(value == "unknown"){
 		  return [
 		          '部分日志无法解析类型，',
 		          '<a class="custom_a"  target="_blank"  ',
 		         ' href="/logsrc/pm_analyse/unknown?proj='+pid+'&report_id='+report_id+'&log_id='+log_id+'&start_time='+start_time_ct+'&end_time='+end_time_ct+'">',
 		          '点击查看所有unknown类型原始日志',
 		          '</a>'
 		          ].join('');
 	  }else{
 	 	  // \t 转换为换行
 		 var value_ct =  value.replace(/\\t+/g, "<br/> &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;");
 		 return value_ct;
 	  }

  }
  
  // 异常类型详情 更多表格 异常总数的显示
  function pmtypeTotalFormatter(value, row, index){
 	  if(row.exp_id==undefined){
 			return "-";	  
 		  }
 	  else{
 		  // unsave  : log_id, start_time, end_time
 		  if(report_id == ""){
 			  var start_time_str = "'"+start_time_ct+"'";
 			  var end_time_str = "'"+end_time_ct+"'";
 	 		  return    '<a href="javascript:void(0)"   onclick="get_unsave_error_type_more_total('+row.exp_id+','+log_id+','+start_time_str+ ',' + end_time_str +')" >' + row.total_count + '</a>' ;
 		  }
 		  // saved : report_id
 		  else{
 	 		  return    '<a href="javascript:void(0)"   onclick="get_saved_error_type_more_total('+row.exp_id+','+report_id + ')" >' + row.total_count + '</a>' ;
 		  }
 	  }//if
  }
  
  
  // 异常类型详情 更多表格 点击total弹窗 unsave
  function get_unsave_error_type_more_total(exp_id, log_id, start_time_str, end_time_str){
		$("#error_type_total_more_modal").modal('show');
		  // 表格分页设置
	  	$('#error_type_total_more_table').bootstrapTable('destroy').bootstrapTable( {
	  		url : "/logsrc/pm_analyse/error_type_total_table",
	  		sortName : "date_time",
	  		sortOrder: "desc",
	  		pageList: "[10, 25, 50, 100, All]",
	  		queryParams: function(p){
	  			return {
	  				exp_id: exp_id,
	  				log_id : log_id,
	  				start_time: start_time_str,
	  				end_time: end_time_str,
	  				limit: p.limit,
	  				offset : p.offset,
	  				sort: p.sort,
	  				order: p.order
	  			}
	  		}
	  	});//  表格end
  }
  
  
  // 异常类型详情 更多表格 点击total弹窗 saved
  function get_saved_error_type_more_total(exp_id, report_id){
		$("#error_type_total_more_modal").modal('show');
		  // 表格分页设置
		// 每次弹窗需要refresh表格，但是refresh方法只能修改url，无法修改queryParams
		// 先destroy，然后在重新load
	  	$('#error_type_total_more_table').bootstrapTable('destroy').bootstrapTable( {
	  		url : "/logsrc/pm_analyse/error_type_total_table",
	  		sortName : "date_time",
	  		sortOrder: "desc",
	  		pageList: "[10, 25, 50, 100, All]",
	  		queryParams: function(p){
	  			return {
	  				exp_id: exp_id,
	  				report_id : report_id,
	  				limit: p.limit,
	  				offset : p.offset,
	  				sort: p.sort,
	  				order: p.order
	  			}
	  		}
	  	});//  表格end
  }  
  
  
  
  // unknow 详情表格 原始日志实例 显示格式
  function pmunknownExampleFormatter(value, row, index){
	  // \t 转换为换行
 	  if(row.uknow_id==undefined){
			return "-";	  
 	  }else{
 		 return value.replace(/\\t+/g, "<br />  &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;");
 	  }	  
  }
  
  
  
