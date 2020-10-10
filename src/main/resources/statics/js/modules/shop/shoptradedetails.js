$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'shop/shoptradedetails/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
            { label: '所属店铺', name: 'shop.shopName', index: 'shop_id', width: 80 },
			{ label: '交易单号', name: 'tradeNo', index: 'trade_no', width: 80 }, 			
			{ label: '对应单号', name: 'orderNo', index: 'order_no', width: 80 }, 			
            { label: '交易类型', name: 'bizType', width: 60, formatter: function(value, options, row){
                    return value === 10 ?
                        '<span class="label label-success">订单收入</span>' :
                        (value===11?'<span class="label label-success">账号充值</span>':
                        (value===20?'<span class="label label-success">账号提现</span>':
                        (value===21?'<span class="label label-success">扣除订单手续费</span>':
                        (value===7?'<span class="label label-success">公共商品订单收入</span>':
                        (value===8?'<span class="label label-success">佣金收入</span>':
                        (value===9?'<span class="label label-success">公共订单(自营部分商品收入)</span>':
                        (value===10?'<span class="label label-success">自营订单收入</span>': '未知类型')
                        )
                        )
                        )
                        )
                        )
                        );
                }},
            { label: '收支方向', name: 'inOrOut', width: 60, formatter: function(value, options, row){
                    return value === 1 ?
                        '<span class="label label-danger">收入</span>' :
                        (value===2?'<span class="label label-success">支出</span>':'未写来源');
                }},
			{ label: '变更前余额', name: 'beforeBlance', index: 'before_blance', width: 80 },
			{ label: '变更金额', name: 'amount', index: 'amount', width: 80 }, 			
			{ label: '变更后余额', name: 'afterBlance', index: 'after_blance', width: 80 }, 			
			{ label: '创建人', name: 'createdBy', index: 'created_by', width: 80 }, 			
			{ label: '创建时间', name: 'createdTime', index: 'created_time', width: 80 }, 			
			{ label: '更新人', name: 'updatedBy', index: 'updated_by', width: 80 }, 			
			{ label: '更新时间', name: 'updatedTime', index: 'updated_time', width: 80 }			
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
		shopTradeDetails: {},
        q:{
            orderNo:'',
            shopName:'',
            bizType:null,
        }
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.shopTradeDetails = {};
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
                var url = vm.shopTradeDetails.id == null ? "shop/shoptradedetails/save" : "shop/shoptradedetails/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.shopTradeDetails),
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
                        url: baseURL + "shop/shoptradedetails/delete",
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
			$.get(baseURL + "shop/shoptradedetails/info/"+id, function(r){
                vm.shopTradeDetails = r.shopTradeDetails;
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