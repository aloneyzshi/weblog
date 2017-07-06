// 复制日志源前端校验
function check_copy_logsrc(old_name, old_host_name, old_logsrc_path, old_logsrc_file ){
	var new_name =$.trim($('#logsrc_name').val()); 
	// 日志源名称不能重复校验 + 非空校验 + 规则校验
	if(old_name == new_name){
		$('#warn_copy_logsrc_name').html("<font color='red'>日志源名称不能重复</font>");
		return false;
	}	
	else if(new_name == ""){
		$('#warn_copy_logsrc_name').html("<font color='red'>日志名不能为空</font>");
		return false;		
	}
	else if(/^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]{1,100}$/.test(new_name) == false){
			// 具体区分不同的规则
		if(/^[\u4e00-\u9fa5_a-zA-Z0-9]+$/.test(new_name) == false){
			$('#warn_copy_logsrc_name').html("<font color='red'>日志源只能包含：中文/英文字母/数字/下划线</font>");
		}else if(/^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(new_name) == false){
			$('#warn_copy_logsrc_name').html("<font color='red'>日志源名称不能以下划线开始和结束</font>");
		}else{
			$('#warn_copy_logsrc_name').html("<font color='red'>日志名长度不能超过100</font>");
		}
		return false
}	
	else{
		$('#warn_copy_logsrc_name').html("");
	}	
	
	// 日志源位置：至少需要修改hostname、path、filepattern三者之一，否则复制无法完成。
	var new_host_name = $.trim($('#host_name').val());
	var new_logsrc_path = $.trim($('#logsrc_path').val());
	var new_logsrc_file =  $.trim($('#logsrc_file').val());
	if(old_host_name==new_host_name && old_logsrc_path==new_logsrc_path &&  old_logsrc_file==new_logsrc_file){
		$('#warn_copy_logsrc_location').html("<font color='red'>至少修改服务器地址,日志文件路径,日志文件名三者之一</font>");
		return false;		
	}
	else{
		$('#warn_copy_host_name').html("");
	}		
	// 服务器地址非空验证
	if(new_host_name == ""){
		$('#warn_copy_host_name').html("<font color='red'>服务器地址不能为空</font>");
		return false;			
	}else{
		$('#warn_copy_host_name').html("");
	}
	// 日志文件路径非空验证
	if(new_logsrc_path == ""){
		$('#warn_copy_logsrc_path').html("<font color='red'>日志文件路径不能为空</font>");
		return false;			
	}else{
		$('#warn_copy_logsrc_path').html("");
	}	
	// 日志文件名非空验证	
	if(new_logsrc_path == ""){
		$('#warn_copy_logsrc_file').html("<font color='red'>日志文件路径不能为空</font>");
		return false;			
	}else{
		$('#warn_copy_logsrc_file').html("");
	}	
}