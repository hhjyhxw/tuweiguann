$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallpurorder/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '下单渠道', name: 'channel', index: 'channel', width: 80 }, 			
			{ label: '采购单号', name: 'purorderNo', index: 'purorder_no', width: 80 }, 			
			{ label: '关联订单id', name: 'orderId', index: 'order_id', width: 80 }, 			
			{ label: '店铺id', name: 'supplierId', index: 'supplier_id', width: 80 }, 			
			{ label: '采购单类型(0公共商品售卖产生 1其他渠道采购)', name: 'purorderType', index: 'purorder_type', width: 80 }, 			
			{ label: '0生成、处理中、已完成', name: 'orderStatus', index: 'order_status', width: 80 }, 			
			{ label: '0未支付、1支付中、2已支付', name: 'payStatus', index: 'pay_status', width: 80 }, 			
			{ label: '0生成、1、退款中、2已退款', name: 'refundStatus', index: 'refund_status', width: 80 }, 			
			{ label: '0未发货、1发货中、2已发货', name: 'shipStatus', index: 'ship_status', width: 80 }, 			
			{ label: '商品(sku)原始价总额', name: 'skuOriginalTotalPrice', index: 'sku_original_total_price', width: 80 }, 			
			{ label: '商品(sku)现价总额', name: 'skuTotalPrice', index: 'sku_total_price', width: 80 }, 			
			{ label: '运费', name: 'freightPrice', index: 'freight_price', width: 80 }, 			
			{ label: '实付订单金额', name: 'actualPrice', index: 'actual_price', width: 80 }, 			
			{ label: '支付金额', name: 'payPrice', index: 'pay_price', width: 80 }, 			
			{ label: '支付流水id(本地支付流水)', name: 'payId', index: 'pay_id', width: 80 }, 			
			{ label: '支付渠道名称', name: 'payChannel', index: 'pay_channel', width: 80 }, 			
			{ label: '支付时间(支付成功回调获取的支付时间)', name: 'payTime', index: 'pay_time', width: 80 }, 			
			{ label: '发货时间', name: 'shipTime', index: 'ship_time', width: 80 }, 			
			{ label: '确认收货时间', name: 'confirmTime', index: 'confirm_time', width: 80 }, 			
			{ label: '省', name: 'province', index: 'province', width: 80 }, 			
			{ label: '市', name: 'city', index: 'city', width: 80 }, 			
			{ label: '县', name: 'county', index: 'county', width: 80 }, 			
			{ label: '详细地址', name: 'address', index: 'address', width: 80 }, 			
			{ label: '', name: 'memo', index: 'memo', width: 80 }, 			
			{ label: '', name: 'consignee', index: 'consignee', width: 80 }, 			
			{ label: '', name: 'phone', index: 'phone', width: 80 }, 			
			{ label: '', name: 'createTime', index: 'create_time', width: 80 }, 			
			{ label: '', name: 'modifyTime', index: 'modify_time', width: 80 }			
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
		smallPurorder: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.smallPurorder = {};
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
                var url = vm.smallPurorder.id == null ? "small/smallpurorder/save" : "small/smallpurorder/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallPurorder),
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
                        url: baseURL + "small/smallpurorder/delete",
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
			$.get(baseURL + "small/smallpurorder/info/"+id, function(r){
                vm.smallPurorder = r.smallPurorder;
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