$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallwasterecord/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '店铺id', name: 'shopId', index: 'shop_id', width: 80 }, 			
			{ label: '支付方式1微信 2支付包 3银行卡', name: 'payType', index: 'pay_type', width: 80 }, 			
			{ label: '操作类型1充值 2提现', name: 'wasteFlag', index: 'waste_flag', width: 80 }, 			
			{ label: '金额', name: 'amount', index: 'amount', width: 80 }, 			
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80 }, 			
			{ label: '审批状态0未审核 1通过 2不通过', name: 'approveFlag', index: 'approve_flag', width: 80 }, 			
			{ label: '支付状态0未支付 1已支付 2关闭', name: 'wasteState', index: 'waste_state', width: 80 }, 			
			{ label: '第三方交易号', name: 'transactionId', index: 'transaction_id', width: 80 }, 			
			{ label: '本地订单号', name: 'orderNo', index: 'order_no', width: 80 }, 			
			{ label: '审核描述', name: 'msg', index: 'msg', width: 80 }, 			
			{ label: '审核时间', name: 'approveTime', index: 'approve_time', width: 80 }, 			
			{ label: '修改时间', name: 'modifyTime', index: 'modify_time', width: 80 }, 			
			{ label: '审核人', name: 'approveBy', index: 'approve_by', width: 80 }, 			
			{ label: '申请人', name: 'createBy', index: 'create_by', width: 80 }			
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
		smallWasteRecord: {}
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