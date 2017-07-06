
// popover 生效设置
$(function () {
	popover_setting();
}); // funciton



// 获取指定url和指定名字参数
function getParam( name, href_str )
  {
	   name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	   var regexS = "[\\?&]"+name+"=([^&#]*)";
	   var regex = new RegExp( regexS );
	 //  var results = regex.exec( window.location.href );
	   var results = regex.exec(href_str );
	   if( results == null )
	    return "";
	  else
	   return results[1];
  }
  

//var log_id = getParam( 'log_id',"/logsrc/rt_analyse?proj=38&amp;log_id=2" );


//  实时分析 手动刷新
function rt_analyse_refresh()
{
	// 获取当前选中日志源的log_id和proj
	var select_url = $("#logsrc_list").children(".active").attr('href');
	var pid = getParam( 'proj',select_url );
	var log_id =  getParam( 'log_id', select_url);
	$("#rt_refresh_body").html("");

	// ajax 获取最新数据
	var refresh_url =  '/logsrc/rt_analyse/refresh?proj=' + pid + '&log_id=' + log_id;
	$.ajax({
  		type: 'GET',
		url: refresh_url,
		success :function(e){
					// status为0： 表格有更新
					if(e['status'] == 0){
						var rt_refresh_data = e['data'];  // rt_refresh_data是array
						var i=0;
						var rt_refresh_td_html = "<tr><th class='col-sm-4'>采样时间</th><th class='col-sm-6'>异常类型和数量</th><th class='col-sm-2'>异常总数</th></tr>";
						for(i=0; i<rt_refresh_data.length; i++){
							// 表格第一列
							rt_refresh_td_html = rt_refresh_td_html  +  "<tr><td class='col-sm-4'> " + rt_refresh_data[i]['date_time']  + "</td><td class='col-sm-6'>";
							// 表格第二列 
							var j=0, popover_loction ;
							var col2_data = rt_refresh_data[i]['error_tc'];
							for(j=0; j<(col2_data.length-1); j++){
								popover_loction = (j%2==0)? "top" : "bottom";
								rt_refresh_td_html = rt_refresh_td_html  + "<a class='pointer_a'  data-toggle='popover'  title='异常类型'   data-placement=" +popover_loction+ "  data-content="  + col2_data[j]['type']  + "   href ='#' >&#160;"  + col2_data[j]['count']  + "&#160;</a>,";
							}
							// 最后一个数字后面没有逗号
							popover_loction = (j%2==0)? "top" : "bottom";
							if(j<col2_data.length){
								rt_refresh_td_html = rt_refresh_td_html  + "<a class='pointer_a'  data-toggle='popover'  title='异常类型'  data-placement=" +popover_loction+ "  data-content="   + col2_data[j]['type']  + "   href ='#' >&#160;"  + col2_data[j]['count']  + "</a>";
							}
							// 表格第三列
							rt_refresh_td_html = rt_refresh_td_html  + "</td><td class='col-sm-2'>"  + rt_refresh_data[i]['total_count']; +  "</td></tr>";
						}
						// 更新表格内容
						$("#rt_refresh_body").html(rt_refresh_td_html);
					}
					popover_setting();	  
			}// end success
	}); // end ajax
	
}