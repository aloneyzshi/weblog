

// 获取URL参数方法
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
  

// 获取全局js变量
var pid = getParam( 'proj' );


//  项目下拉列表
$(document).ready(function() {
	getProjects();
});

// 获取project
function getProjects(){
  	$.ajax({
  		type: 'GET',
		url: '/projects',
		success :function(e){
			var data = e['data'];
			var i=0, html = "";
			if(typeof(pid)=='string' && pid.length==0){
				html  =  '<option  selected="true" value="#"> 请选择项目</option>';
			}
			for(i=0; i<data.length; i++){
				var id = data[i]['id'],  name=data[i]['name'];
				if(data[i]['id'] == pid){
					html  +=  '<option  selected="true" value="/logsrc/manage?proj=' +id  + '"> ' + name + '</option>';
				}
				else{
					html  +=  '<option value="/logsrc/manage?proj=' +id  + '"> ' + name + '</option>';
				}
			}
			$("#project_select").empty().append(html);
			$('#project_select').select2();
		}
	})
}


// 启动和设置popover
function popover_setting(){
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
}



//  手动转换datetime 为 timestamp        "2015-06-17 10:12:37" => 1434507157000
function datetime2timestamp(datetime_str){        
        var dt_arr=  datetime_str.split(' ');
        var date_part_arr = dt_arr[0].split('-');
        var time_part_arr = dt_arr[1].split(':');
        var date = new Date(date_part_arr[0],  parseInt(date_part_arr[1], 10) - 1,  date_part_arr[2], time_part_arr[0],  time_part_arr[1], time_part_arr[2] );
        return date.getTime(); 
}

//手动转换timestamp 为 datetime    1437047489404 = > ""
function timestamp2datetime(timestamp_str){
	var year = timestamp_str.getFullYear();  // 年： 4位
	var month = timestamp_str.getMonth()+1;  // 月： (0-11, 0代表1月)
	if(month<10){	month = '0' + month;	}
	var date = timestamp_str.getDate() ;  // 日：(1-31)
	if(date<10){	date = '0' + date;	}
	var hour = timestamp_str.getHours();  // 时：(0-23)
	if(hour<10){	hour = '0' + hour;	}	
	var minute = timestamp_str.getMinutes();  // 分：(0-59)
	if(minute<10){	minute = '0' + minute;	}		
	var second = timestamp_str.getSeconds();  // 秒：(0-59)
	if(second<10){second = '0' + second;}		
	return year+ "-" +month + "-" + date  + " " +  hour + ":" + minute + ":" + second;
}

