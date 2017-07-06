// edit, copy: 过滤关键字 AND/OR切换  
function select_onchange_filter_words(e){
	var p1=$(e).children('option:selected').val();  //selected值 : AND OR
	$('.filter_keyword_td_select').html(p1);  //修改table第二个元素
}

//edit, copy:  正则表达式  AND/OR切换
function select_onchange_reg_regex(e){
	var p1=$(e).children('option:selected').val();  //selected值 
	$('.reg_regex_td_select').html(p1);  //修改table第二个元素
}


//edit, copy: 删除1行： 过滤关键字 + 正则表达式
function filter_item_delete(e){
	$(e).closest("tr").remove();
}


//edit, copy,new: 过滤关键字 添加1行
function add_row_filter_keyword(){
	// 表格第1列
	var cell_1_html = '<td class="col-md-5" style="padding:0px"><input type="text" class="form-control log-item-regular-input"  id="filter_keyword_input_id "   name="filter_keyword_arr[]"     ></td>';
	var select_val = $("#filter_keyword_select_id option:selected").val();   //selected值 : AND OR
	// 表格第2列
	if(select_val == undefined){
		// 第1行，新建select
		var cell_2_html = '<td class="col-md-1"  style="padding:0px"><select class=" form-control log-item-regular-input" id="filter_keyword_select_id" name="filter_keyword_con"  onchange="select_onchange_filter_words(this)"><option value="AND" selected="true" >AND</option><option value="OR">OR</option></select></td>';
	}else{
		// 非第1行，不需要新建select
		var cell_2_html = '<td class="col-md-1 filter_keyword_td_select">'+select_val+'</td>';
	}
	// 表格第3列
	var cell_3_html = '<td class="col-md-1" ><a class="filter_delete" href="javascript:void(0)"  title="删除"  onclick=filter_item_delete(this)><i class="glyphicon glyphicon-trash  filter_delete_icon"></i></a>	</td>';
	var tr = '<tr>' + cell_1_html  + cell_2_html + cell_3_html  + '</tr>';	
	$('#filter_keyword_table > tbody:last-child').append(tr);
}


//edit, copy,new: 正则表达式 添加1行
function add_row_reg_regex(){
	// 表格第1列
	var cell_1_html = '<td class="col-md-5" style="padding:0px"><input type="text" class="form-control log-item-regular-input"  id="reg_regex_input_id "   name="reg_regex_arr[]"  ></td>';
	var select_val = $("#reg_regex_select_id option:selected").val();    //selected值 : AND OR
	// 表格第2列
	if(select_val == undefined){
		// 第1行，新建select
		var cell_2_html = '<td class="col-md-1"  style="padding:0px"><select class=" form-control log-item-regular-input" id="reg_regex_select_id" name="reg_regex_con" onchange="select_onchange_reg_regex(this)" ><option value="OR" selected="true">OR</option></select></td>	';
	}else{
		// 非第1行，不需要新建select
		var cell_2_html = '<td class="col-md-1 reg_regex_td_select">'+select_val+'</td>';
	}	
	// 表格第3列
	var cell_3_html = '<td class="col-md-1" ><a class="filter_delete" href="javascript:void(0)"  title="删除"  onclick=filter_item_delete(this)><i class="glyphicon glyphicon-trash  filter_delete_icon"></i></a>	</td>';
	var tr = '<tr>' + cell_1_html  + cell_2_html  + cell_3_html  +'</tr>';	
	$('#reg_regex_table > tbody:last-child').append(tr);
}
