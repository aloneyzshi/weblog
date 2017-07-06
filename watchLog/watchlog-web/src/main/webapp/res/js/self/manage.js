
  $(document).ready(function() {
	  // 表格分页设置
	  	$('#logtable').bootstrapTable({
	  		url : "manage/logtable",
	  		striped: true,
	  		sortName : "update_time",
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
	  	});//  表格end
    } );
  
  
  // 日志源名称显示   
 function logsrcnameFormatter(value, row, index) {
	  if(value!=undefined){
		     var maxwidth = 15; 
		     var value_show = value;
		     if (value.length > maxwidth) {
		    	 value_show = value.substring(0, maxwidth) + '...'
		     }
		  return  '<a class="like" title=' + value +' href="/logsrc/' + row.id + '?proj=' + pid + '" >' + value_show + '</a>';		  
	  }
 }
 
 // 服务器地址显示：限制长度截断+hover显示所有文字
 function hostnameFormatter(value, row, index){
	  if(value!=undefined){
			     var maxwidth = 40; 
			     var value_show = value;
			     if (value.length > maxwidth) {
			    	 value_show = value.substring(0, maxwidth) + '...'
			     }
				  return' <a class="table_text_limit" title='+value+'>'+value_show+'</a>';
		  }
	  

 }
 
 // 日志源地址显示：限制长度截断+hover显示所有文字
 function logsrcpathFormatter(value, row, index){
	  if(value!=undefined){
			     var maxwidth = 70; 
			     var value_show = value;
			     if (value.length > maxwidth) {
			    	 value_show = value.substring(0, maxwidth) + '...'
			     }
				  return' <a class="table_text_limit" title='+value+'>'+value_show+'</a>';		  
		  }
	  

 }
 
 function logsrcfileFormatter(value, row, index){
	  if(value!=undefined){
		     var maxwidth = 20; 
		     var value_show = value;
		     if (value.length > maxwidth) {
		    	 value_show = value.substring(0, maxwidth) + '...'
		     }
			  return' <a class="table_text_limit" title='+value+'>'+value_show+'</a>';
	  }
 }
 
 function statusFormatter(value, row, index){
	  if(value !=undefined){
			  if (value==0){
				  return '未开始'
			  }
			  else if(value == 1){
				  return '<font color="red">监控中</red>'
			  }
			  else if(value == 2){
				  return '监控结束'
			  }	  
			  else{
				  return '其他'
			  }
	  }
	  

 }

 
 // 操作
 function operateFormatter(value, row, index){

	  if(row.id==undefined){
			return "-";	  
		  }
	  else{
		  return [
		          	// 复制日志源功能-弹窗
//		            '<a class="copy" href="javascript:void(0)"  title="复制"  onclick=logsrc_copy('+row.id+')>',
//		            '<i class="glyphicon glyphicon-file"></i>',
//		            '</a>  ',	            
		            // 编辑日志源功能
		           '<a class="edit"   title="修改"   href="' + row.id + '/edit?proj=' + pid + '" >',
		            '<i class="glyphicon glyphicon-edit"></i>',
		            '</a>',
		            '&nbsp ',
		            // 复制日志源功能-链接
		           '<a class="copy"  title="复制"   href="' + row.id + '/copy?proj=' + pid + '" >',
		            '<i class="glyphicon glyphicon-copy"></i>',
		            '</a>'		            
		        ].join('');
	  }
 }

	
  $('#logtable').on('check.bs.table uncheck.bs.table ' +
            'check-all.bs.table uncheck-all.bs.table', function () {
        $remove.prop('disabled', !$table.bootstrapTable('getSelections').length);
        // save your data, here just save the current page
        selections = getIdSelections();
        // push or splice the selections if you want to save all data selections
    });
  
  
  
  // 获取表格选择的所有ids
  function getIdSelections() {
      return $.map($('#logtable').bootstrapTable('getSelections'), function (row) {
          return row.id
      });
  }
  
  // 表格：删除日志源+二次确认 (可能有多个)
	function delete_logsrc_table(){
	  	  var ids = getIdSelections(); //待删除id数组
		  var ids_str = ids.toString();  //待删除id字符串
		  // 用户没有勾选内容
		  if(ids_str == "" ){
			  $("#js_notice").html("<font color='red'>  请勾选需要删除的日志源</font></br>");
		  }
		  else{
			  $("#js_notice").html("");
				 $('#ids').val(ids_str);  //post请求参数 ids
				 $('#proj').val(pid);  //post请求参数 projj
				$('#destroy_logsrc_modal').modal('show');  //弹窗modal
		  }
	}

	
	
      
      // 开始监控
      function startMonitorLogsrc(){
    	  var ids = getIdSelections();
    	  var ids_str = ids.toString();
    	  if(ids_str==""){
    		  $("#js_notice").html("<font color='red'>  请勾选需要开始监控的日志源</font></br>");
    	  }else{
    		  // send ajax 
    	      	$.ajax({
    	      		type: 'POST',
    	    		url: '/logsrc/start_monitor',
    	    		data:{ ids: ids_str},
    	    		success :function(e){
    	    			 if(e['status'] == 0 ) { //开始监控成功
    	    				 location.reload() ;
    	    			 }
    	    			 else{
    	    				 $("#js_notice").html("<font color='color'> "+e['message']+"</font></br>");
    	    			 }
    	    		}
    	    	}) ;  //--ajax--  		  
    	  }	//--else--  		
      }
      
      // 停止监控
      function stopMonitorLogsrc(){
    	  var ids = getIdSelections();
    	  var ids_str = ids.toString();
    	  if(ids_str==""){
    		  $("#js_notice").html("<font color='red'>  请勾选需要停止监控的日志源</font></br>");
    	  }else{
    		  // send ajax 
    	      	$.ajax({
    	      		type: 'POST',
    	    		url: '/logsrc/stop_monitor',
    	    		data:{ ids: ids_str},
    	    		success :function(e){
    	    			 if(e['status'] == 0 ) { //开始监控成功
    	    				 location.reload() ;
    	    			 }
    	    			 else{
    	    				 $("#js_notice").html("<font color='color'> "+e['message']+"</font></br>");
    	    			 }
    	    		}
    	    	}) ;  //--ajax--  		  
    	  }	//--else--  		
      }      
      
           



