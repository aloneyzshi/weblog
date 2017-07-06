var pid = getParam( 'proj' );
var report_id = getParam( 'report_id' );
var log_id = getParam( 'log_id' );
var start_time = getParam( 'start_time' );
var end_time = getParam( 'end_time' );
var start_time_ct = decodeURIComponent(start_time).replace(/\+/g, ' ');
var end_time_ct = decodeURIComponent(end_time).replace(/\+/g, ' ');

//pm_projlevel_saved, pm_projlevel_unsave:   table表格封装
function render_table(div_table_id){
	  	$(div_table_id).bootstrapTable({
	  		url : "/logsrc/pm_projlevel_etc_table",
	  		striped: true,
	  		onPostBody: function () {
	  			popover_setting();
	  	    },	  		
	  		pageList: "[10, 25, 50, 100, All]",
	  		queryParams: function(p){
	  			return {
	  				proj : pid,
	  				report_id: report_id,
	  				start_time : start_time_ct,
	  				end_time : end_time_ct,
	  				limit: p.limit,
	  				offset : p.offset,
	  			}
	  		}
	  	});
}// 表格封装



//聚合报告-项目级-异常分布  日志源显示格式
function pm_projlevel_etc_table_disterrorFormatter(value,row,index){
		  var each_val = "";
		  var content_arr = [];
		  // 遍历数字，拼接type和count到超链接
		  var i;
		  for(i=0; i<value.length; i++){
			  each_val = "<a   class='pointer_a'  data-toggle='popover' data-placement='top'    title='异常类型' data-content='"+value[i]['type']+ "'>"+value[i]['count']+" </a>";
			  content_arr.push(each_val);
		  }
		  var  content_str = content_arr.join(' , ');
		  return content_str		  
}

//pm_projlevel_saved, pm_projlevel_unsave:  highcharts 趋势图封装
function draw_charts(div_charts_id, results){
	// highcharts 画图
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
    $(div_charts_id).highcharts({
    	credits: {enabled: false}, 
        chart: {
            type: 'spline',
            height: 500,
            animation: Highcharts.svg, // don't animate in old IE
        },
        // 设置调用颜色顺序，暂时设定50种
        colors:[
                '#0000FF', // 蓝色
                '#00FFFF', //  草青色
                '#32CD32',
                '#FF0000', //红色
                '#FFFF00', //  黄色
                '#FF00FF', 
                '#FF8C00',
                '#708090',
                '#7B68EE',
                ],
        title: { text: null},
        plotOptions: {
            series: {
              lineWidth: '1px',   //曲线粗细，默认是2
              marker: {   // 鼠标悬浮设置
                enabled: false,  // 是否显示point点
                states: {
                  hover: {
                    enabled: true,
                  },
                  select: {
                    enabled: true,
                  },
                },
              },
          }},
        xAxis: {
            type: 'datetime',
//            dateTimeLabelFormats: {
//            	second: '%Y-%m-%d   %H:%M:%S',
//            	minute: '%Y-%m-%d   %H:%M:00',
//            	hour: '%Y-%m-%d  %H:00:00',
//            	day: '%Y-%m-%d 00:00:00 ',
//            	month: '%Y-%m-01 00:00:00',
//            },		   
//            tickPixelInterval : 50,
//            labels: {
//                rotation: -60
//            }
        },
        yAxis: {
            title: {
                text: '异常总数'
            },
            min:0
        },
        legend: {  
            enabled: true
        },
        tooltip:{
        	shared: true,
        	xDateFormat: "%Y-%m-%d %H:%M:%S",
        },
        series: (function(){
        	var series_list = [], i;
        	for(i=0; i<results.length; i ++){
        		series_list.push({name: results[i]['logsrc_name'], data: results[i]['data']});
        	}
        	return series_list;
        }()),
        lang: {
            noData: "趋势图没有数据，请联系管理员"
        },		        
    });// highcharts
}

