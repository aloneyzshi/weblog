// 浏览器缓存key
var LOCAL_STORAGE_KEY = "debug_info";
// 浏览器缓存大小
var LOCAL_STORAGE_SIZE = 2*1024*1024;

// 缓存调试输入内容
$(function (){	
		// 检测浏览器是否支持localStorage  
		function supportsLocalStorage() {
			// return "function" : support => true
			// return "undefined" : not support => false
		  return typeof(Storage)!== 'undefined';
		}
	
		// 实时监控和存储调试日志信息
		if (!supportsLocalStorage()) {
		  // No HTML5 localStorage Support
			alert("Your browser not support html5 local storage");
		} else {
		  // HTML5 localStorage Support
					// 页面刷新，展示上次内容
					var newest_debug_info = localStorage.getItem(LOCAL_STORAGE_KEY);
					$("#debug_log_content").val(newest_debug_info);
					// 实时监控和实时缓存
					try {
							debug_info_check(); 
					} catch (e) 	{
					  		 localStorage.clear(); 
							 alert('localstorage exception happens!');
					}// catch		
		}	// else
});



// 调试日志信息的实时存储
function debug_info_update(){
	  var intl_store = setInterval(function() {
			// 每隔1s获取用户输入
			  var user_input_textarea = $('#debug_log_content').val();
			  var count = $("#debug_log_content").val().length;
			  if(count > LOCAL_STORAGE_SIZE){
				// 如果当前输入内容超过LOCAL_STORAGE_SIZE，停止实时存储
					clearInterval(intl_store);
			  }else{
				// 如果当前输入内容不超过LOCAL_STORAGE_SIZE，则实时存储
				  localStorage.setItem(LOCAL_STORAGE_KEY, user_input_textarea);
			  }
		  }, 1000);
}

// 调试日志新的实时监控
function debug_info_check(){
		  var intl_check = setInterval(function(){
		  var count = $("#debug_log_content").val().length;
		  //console.log(count);
			if(count <=  LOCAL_STORAGE_SIZE){
				// 如果当前输入内容小于等于LOCAL_STORAGE_SIZE，启动实时存储
				//console.log("small");
				$('#warn_debug_info').html("");
				$('#btn_debug_validate').removeAttr('disabled');
				debug_info_update();
			}else{
				// 如果当前输入内容大于LOCAL_STORAGE_SIZE，不启动实时存储，并提示用户
				//console.log("big");
				$('#warn_debug_notice').html("");
				$('#warn_debug_info').html("<font color='red'>日志信息大小不能超过2M</font>");
				$('#btn_debug_validate').attr('disabled','disabled');
			}				  
	  }, 1000);	
}

//调试日志源验证功能
function start_debug_validate(proj, log_id)
{
		// 校验用户输入 大小
		  var user_input_textarea = $('#debug_log_content').val();
		  var count = $("#debug_log_content").val().length;
		  if(count == 0 ){
			  $('#warn_debug_notice').html("<font color='red'>日志信息不能为空</font>");
		  }else if (count >= LOCAL_STORAGE_SIZE ){
			  $('#warn_debug_info').html("<font color='red'>日志信息大小不能超过2M</font>");
		  } else{
			  $('#warn_debug_notice').html("");
				// loading 等待
				$("#debug_tc_comment").html("");
				$("#debug_fail").css("display",  "none");
				$(".debug_tc_div").css("display",  "none");
				$('#debug_loader_div').modal('show');	  
				console.log(count);
				// ajax 请求后端
			  	$.ajax({
			  		type: 'POST',
					url: '/logsrc/debugvalidate',
					data: {proj: proj,  log_id: log_id, debug_info: user_input_textarea},
					dataType: "json",
					success :function(e){
								// loading消失
								$('#debug_loader_div').modal('hide');
								// 调试成功
								if(e['status'] == 0)
								{
									$(".debug_tc_div").css("display",  "block");
										// 渲染debug的2个表格
										var error_tc_list = e['error_tc'];
										var unknow_list = e['unknow_list'];
										var i=0,error_tc_item, unknow_item;
										// error_tc表格
										if(error_tc_list.length>0){
											// 表格第一行
											var debug_tc_body_html = "<tr><th class='col-sm-10' style='text-align: center;'>异常类型</th><th class='col-sm-2' style='text-align: center;'>异常总数</th></tr>";
											for(i=0; i<error_tc_list.length; i++){
												// 表格其他行
												error_tc_item = error_tc_list[i]['type'].replace(/\\t+/g, "<br /> &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;"); // \\t 用换行和8个space替换
												if(error_tc_list[i]['type'] == "unknown"){
													debug_tc_body_html = debug_tc_body_html + "<tr><td class='col-sm-10' style='color:blue'>" +error_tc_item + "</td>" + "<td class='col-sm-2' style='text-align: center;'>"+error_tc_list[i]['count']+"</td></tr>";
												}
												else{
													debug_tc_body_html = debug_tc_body_html + "<tr><td class='col-sm-10'>" + error_tc_item + "</td>" + "<td class='col-sm-2' style='text-align: center;'>"+error_tc_list[i]['count']+"</td></tr>";
												}
											}				
											// 表格更新
											$("#debug_tc_body").html(debug_tc_body_html);		
											// 备注
											$("#debug_tc_comment").html("备注： 显示为unknown说明没有分析出异常类型，请修改正则表达式进一步区分.");				
										}
				
										// unknown表格
										if(unknow_list.length > 0){
											// 表格第一行
											var debug_unknow_body_html = "<tr><th class='col-sm-12' style='text-align: center;'>unknow日志信息</th></tr>";
											for(i=0; i<unknow_list.length; i++){
												// 表格其他行
												unknow_item = unknow_list[i].replace(/\\t+/g, "<br /> &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;"); // \\t 用换行和8个space替换
												debug_unknow_body_html = debug_unknow_body_html + "<tr><td class='col-sm-12'>"+unknow_item+"</td></tr>";
											}			
											// 表格更新
											$("#debug_unknow_body").html(debug_unknow_body_html);					
										}
										
										// 无返回结果
										if(error_tc_list.length==0 && unknow_list.length==0 ){
											$("#debug_tc_comment").html("<br>无");				
										}
										
								}
								// 调试失败
								else{
									$("#debug_fail").css("display",  "block");
									$("#debug_fail").html(e['message']);
								}
					},
					error: function(){
						$('#debug_loader_div').modal('hide');
						//请求出错处理
					}
				});			  
		  }
}


	// 清空调试日志内容
	function clear_debug_validate(){
		$("#debug_log_content").val('');  //jquery
		localStorage.clear(); // 清空缓存内容
	}