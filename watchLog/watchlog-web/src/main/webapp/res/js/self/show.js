	
	// 详情：删除日志源 + 二次确认 ( 单个删除)
	function delete_logsrc_single(id, proj){
		console.log(id);
		console.log(pid);
		 $('#ids').val(id);  //post请求参数 ids
		 $('#proj').val(proj);  //post请求参数 projj
		$('#destroy_logsrc_single_modal').modal('show');  //弹窗modal		
	}
