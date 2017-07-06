  $(document).ready(function() {
        // unsave页面popover
	  	popover_setting();
    } );  //document


//点击 unsave 聚合报告 异常类型 total 弹窗
function get_unsave_error_type_total(log_id, exp_id, start_time, end_time){
	$("#unsave_error_type_total_modal").modal('show');
	  // 表格分页设置
  	$('#unsave_error_type_total_table').bootstrapTable('destroy').bootstrapTable({
  		url : "/logsrc/pm_analyse/error_type_total_table",
  		sortName : "date_time",
  		sortOrder: "desc",
  		pageList: "[10, 25, 50, 100, All]",
  		queryParams: function(p){
  			return {
  				log_id : log_id,
  				start_time: start_time,
  				end_time : end_time,
  				exp_id: exp_id,
  				limit: p.limit,
  				offset : p.offset,
  				sort: p.sort,
  				order: p.order
  			}
  		}
  	});//  表格end
}