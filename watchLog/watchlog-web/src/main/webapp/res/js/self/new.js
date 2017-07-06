		 //  创建日志源非空校验
		function check_create_logsrc(){
			// 日志源名称验证
			$('#warn_new_logsrc_name').html("");
			var logsrc_name = $.trim($('#logsrc_name').val());
			if(logsrc_name == ""){
				$('#warn_new_logsrc_name').html("<font color='red'>日志名不能为空</font>");
				return false;
			}
			else if(/^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]{1,100}$/.test(logsrc_name) == false){
				// 具体区分不同的规则
						if(/^[\u4e00-\u9fa5_a-zA-Z0-9]+$/.test(logsrc_name) == false){
							$('#warn_new_logsrc_name').html("<font color='red'>日志源只能包含：中文/英文字母/数字/下划线</font>");
						}else if(/^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(logsrc_name) == false){
							$('#warn_new_logsrc_name').html("<font color='red'>日志源名称不能以下划线开始和结束</font>");
						}else{
							$('#warn_new_logsrc_name').html("<font color='red'>日志名长度不能超过100</font>");
						}
						return false
			}
			else{
				$('#warn_new_logsrc_name').html("");
			}
			// 服务器地址验证
			var host_name = $.trim($('#host_name').val());
			if(host_name == ""){
				$('#warn_new_host_name').html("<font color='red'>服务器地址不能为空</font>");
				return false;
			}
			else{
				$('#warn_new_host_name').html("");
			}
			// 日志文件路径验证
			var logsrc_path = $.trim($('#logsrc_path').val());
			if(logsrc_path == ""){
				$('#warn_new_logsrc_path').html("<font color='red'>日志文件路径不能为空</font>");
				return false;
			}
			else{
				$('#warn_new_logsrc_path').html("");
			}
			// 日志文件名验证
			var logsrc_file = $.trim($('#logsrc_file').val());
			if(logsrc_file == ""){
				$('#warn_new_logsrc_file').html("<font color='red'>日志文件名不能为空</font>");
				return false;
			}
			else{
				$('#warn_new_logsrc_file').html("");
			}
			//  起始标志验证
			var start_regex = $.trim($('#start_regex').val());
			if(start_regex == ""){
				$('#warn_new_start_regex').html("<font color='red'>起始标志不能为空</font>");
				return false;
			}
			else{
				$('#warn_new_start_regex').html("");
			}	
		}