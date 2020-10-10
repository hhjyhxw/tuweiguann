$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallorder/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '下单渠道', name: 'channel', index: 'channel', width: 80 }, 			
			{ label: '订单号', name: 'orderNo', index: 'order_no', width: 80 }, 			
			{ label: '用户昵称', name: 'user.nickname', index: 'user_id', width: 80 },
			{ label: '店铺名称', name: 'shop.shopName', index: 'supplier_id', width: 80 },
            { label: '订单状态', name: 'orderStatus', width: 60, formatter: function(value, options, row){
                    return value === 0 ?
                        '<span class="label label-danger">未处理</span>' :
                        (value===1?'<span class="label label-success">处理中</span>':
                         (value===2?'<span class="label label-success">已完成</span>':'<span class="label label-success">已关闭</span>'));
                }},
            { label: '支付状态', name: 'payStatus', width: 60, formatter: function(value, options, row){
                    return value === 0 ?
                        '<span class="label label-danger">未支付</span>' :
                        (value===1?'<span class="label label-success">支付中</span>':'<span class="label label-success">已支付</span>');
                }},
            { label: '退款状态', name: 'refundStatus', width: 60, formatter: function(value, options, row){
                    return value === 0 ?
                        '<span class="label label-danger">未退款</span>' :
                        (value===1?'<span class="label label-success">退款中</span>':'<span class="label label-success">已退款</span>');
                }},
            { label: '发货状态', name: 'shipStatus', width: 60, formatter: function(value, options, row){
                    return value === 0 ?
                        '<span class="label label-danger">未发货</span>' :
                        (value===1?'<span class="label label-success">发货中</span>':'<span class="label label-success">已配送</span>');
                }},
			{ label: '运费(元)', name: 'freightPrice', index: 'freight_price', width: 80 },
			{ label: '代金券优惠价(元)', name: 'couponPrice', index: 'coupon_price', width: 80 },
			{ label: '实付订单金额(元)', name: 'actualPrice', index: 'actual_price', width: 80 },
			{ label: '支付金额(元)', name: 'payPrice', index: 'pay_price', width: 80 },
			{ label: '支付流水', name: 'payId', index: 'pay_id', width: 80 },
			{ label: '支付渠道名称', name: 'payChannel', index: 'pay_channel', width: 80 }, 			
			{ label: '支付时间', name: 'payTime', index: 'pay_time', width: 80 },
			{ label: '发货时间', name: 'shipTime', index: 'ship_time', width: 80 },
			{ label: '确认收货时间', name: 'confirmTime', index: 'confirm_time', width: 80 }

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
		smallOrder: {},
        q:{
            orderNo:'',
            nickname:'',
            shopName:'',
            startTime:null,
            endTime:null,
            payStatus:null,
            shipStatus:null,
        }
     },
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.smallOrder = {};
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
                var url = vm.smallOrder.id == null ? "small/smallorder/save" : "small/smallorder/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallOrder),
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
                        url: baseURL + "small/smallorder/delete",
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
			$.get(baseURL + "small/smallorder/info/"+id, function(r){
                vm.smallOrder = r.smallOrder;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:vm.q,
                page: 1
            }).trigger("reloadGrid");
		}
	}
});