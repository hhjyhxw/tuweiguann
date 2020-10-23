$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallwasterecord/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '店铺', name: 'shop.shopName', index: 'shop_id', width: 80 },
            { label: '支付方式', name: 'payType', width: 60, formatter: function(value, options, row){
                    return value === '1' ?
                        '<span class="label label-danger">微信</span>' :
                        (value==='2'?'<span class="label label-success">支付宝</span>':'银行卡');
                }},
           /* { label: '操作类型', name: 'wasteFlag', width: 60, formatter: function(value, options, row){
                return value === '1' ?
                    '<span class="label label-danger">充值</span>' :
                    (value==='2'?'<span class="label label-success">提现</span>':'未知');
            }},*/
			// { label: '操作类型1充值 2提现', name: 'wasteFlag', index: 'waste_flag', width: 80 },

            { label: '金额', name: 'amount', index: 'amount', width: 80 },
			{ label: '创建时间', name: 'createTime', index: "create_time", width: 85, formatter: function(value, options, row){
                if(value!=null){
                    return getDateTime(value,"yyyyMMddHHmmss");
                }else{
                    return "";
                }
            }},
            { label: '提现状态', name: 'approveFlag', width: 60, formatter: function(value, options, row){
                    return value === '0' ? '<span class="label label-danger">提现申请</span>' :
                        (value==='1'?'<span class="label label-success">提现处理</span>':
                        (value==='2'?'<span class="label label-success">提现成功</span>':
                        (value==='3'?'<span class="label label-success">提现失败</span>':'未知操作')));
                }},
          /*  { label: '支付状态', name: 'wasteState', width: 60, formatter: function(value, options, row){
                    return value === '0' ?
                        '<span class="label label-danger">未支付</span>' :
                        (value==='1'?'<span class="label label-success">已支付</span>':'关闭');
                }},*/
			{ label: '本地订单号', name: 'orderNo', index: 'order_no', width: 80 },
			{ label: '第三方交易号', name: 'transactionId', index: 'transaction_id', width: 80 },
			{ label: '审核描述', name: 'msg', index: 'msg', width: 80 }, 			
			{ label: '审核时间', name: 'approveTime', index: "approve_time", width: 85, formatter: function(value, options, row){
                      if(value!=null){
                        return getDateTime(value,"yyyyMMddHHmmss");
                      }else{
                            return "";
                        }
            }},
			{ label: '更新时间', name: 'modifyTime', index: "modify_time", width: 85, formatter: function(value, options, row){
                      if(value!=null){
                        return getDateTime(value,"yyyyMMddHHmmss");
                      }else{
                            return "";
                        }
            }},
			{ label: '审核人', name: 'approveBy', index: 'approve_by', width: 80 }, 			
			{ label: '申请人', name: 'createBy', index: 'create_by', width: 80 },
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#icloudapp',
	data:{
		showList: true,
		title: null,
		smallWasteRecord: {},
		q:{

		}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.smallWasteRecord = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.smallWasteRecord.id == null ? "small/smallwasterecord/save" : "small/smallwasterecord/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallWasteRecord),
                    success: function(r){
                        if(r.code === 0){
                             layer.msg("操作成功", {icon: 1});
                             vm.reload();
                             $('#btnSaveOrUpdate').button('reset');
                             $('#btnSaveOrUpdate').dequeue();
                        }else{
                            layer.alert(r.msg);
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        }
                    }
                });
			});
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			var lock = false;
            layer.confirm('确定要删除选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
		            $.ajax({
                        type: "POST",
                        url: baseURL + "small/smallwasterecord/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
                                $("#jqGrid").trigger("reloadGrid");
                            }else{
                                layer.alert(r.msg);
                            }
                        }
				    });
			    }
             }, function(){
             });
		},
		getInfo: function(id){
			$.get(baseURL + "small/smallwasterecord/info/"+id, function(r){
                vm.smallWasteRecord = r.smallWasteRecord;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});