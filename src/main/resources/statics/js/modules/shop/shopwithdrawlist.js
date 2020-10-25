


$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'shop/shop/withdrawlist',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '名称', name: 'shopName', index: 'shop_name', width: 80 },
			{ label: '店铺余额', name: 'balance', index: 'balance', width: 80 },
            {header:'操作', name:'操作', width:50, sortable:false, title:false, align:'center', formatter: function(val, obj, row, act){
                    var actions = [];
                        if(row.balance>0){
                             actions.push('<a title="提现" onclick="vm.update('+row.id+')"><i class="fa fa-pencil">提现</i></a>&nbsp;');
                        }
                      /*  actions.push('<a title="提现记录" onclick="vm.update('+row.id+',0)"><i class="fa fa-trash-o">提现记录</i></a>&nbsp;');*/
                    return actions.join('');
                }}
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
        smallWasteRecord:{
            shopId:null,
            payType:3,
            wasteFlag:'2',
            amount:0,
            approveFlag:'0',
            wasteState:'0',
            bankId:null
        },//提现记录
		shop: {
            parentName:'',
            parentId:null,
            status:0,
            sysFlag:0,
            review:0,
            commissionRate:0,
        },
        banklist:[],//所属店铺 审核通过的银行卡
        q:{
            shopName:''
        }

	},
    created: function(){

    },
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.shop = {
                parentName:'',
                parentId:null,
                status:0,
                sysFlag:0,
                review:0,
                lnt:null,
                lat:null,
                province:'',
                city:'',
                county:'',
                address:'',
                commissionRate:0,
            };
            vm.deptName = '',
            vm.deptId = null,
            vm.getShopTree();
		},
		update: function (id) {
			// var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id);

		},
		saveOrUpdate: function (event) {
            if(!priceCheck(vm.smallWasteRecord.amount)){
                layer.msg("提现金额不能为空,且为数字,最多保留两位小数", {icon: 2});
                return;
            }
            if(parseFloat(vm.shop.balance)<parseFloat(vm.smallWasteRecord.amount)){
                layer.msg("提现金额不能大于:"+vm.shop.balance, {icon: 2});
                return;
            }
            if(vm.smallWasteRecord.bankId==null || vm.smallWasteRecord.bankId==''){
                layer.msg("请选择提现银行卡", {icon: 2});
                return;
            }
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = "shop/shop/withdraw";
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

		getInfo: function(id){
			$.get(baseURL + "shop/shop/withdrawinfo/"+id, function(r){
                vm.shop = r.shop;
                vm.smallWasteRecord.shopId = r.shop.id;
                vm.getShopBankList(r.shop.id);
            });
		},
        //获取店铺审核通过的银行卡列表
		getShopBankList: function(shopId){
            $.get(baseURL + "shop/shopbank/bankList?shopId="+shopId, function(r){
                console.log("list===="+JSON.stringify(r));
                vm.banklist = r.list;
            });
        },
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:vm.q,
                page: 1
            }).trigger("reloadGrid");
		},

	}
});

