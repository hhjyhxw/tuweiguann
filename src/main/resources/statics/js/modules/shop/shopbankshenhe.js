$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'shop/shopbank/shenhelist',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
            { label: '所属店铺', name: 'shop.shopName', index: 'shop_id', width: 80 },
			{ label: '银行名称', name: 'bankName', index: 'bank_name', width: 80 }, 			
			{ label: '支行名称', name: 'subBranch', index: 'sub_branch', width: 80 }, 			
			{ label: '银行卡号', name: 'cardNo', index: 'card_no', width: 80 }, 			
			{ label: '用户姓名', name: 'userName', index: 'user_name', width: 80 }, 			
			{ label: '手机号', name: 'mobile', index: 'mobile', width: 80 }, 			
            { label: '状态', name: 'status', width: 60, formatter: function(value, options, row){
                    return value === '0' ?
                        '<span class="label label-danger">禁用</span>' :
                        '<span class="label label-success">正常</span>';
                }},
            { label: '审核状态', name: 'approveFlag', width: 60, formatter: function(value, options, row){
                    return value === '0' ?
                        '<span class="label label-danger">未审核</span>' :
                        (value==='1'?'<span class="label label-success">待审核</span>':
                        (value==='2'?'<span class="label label-success">审核通过</span>':
                        (value==='3'?'<span class="label label-success">审核失败</span>':'未审核')));
                }},
            { label: '审核不通过原因', name: 'msg', index: 'msg', width: 80 },
            { label: '创建人', name: 'createdBy', index: 'created_by', width: 80 },
			{ label: '创建时间', name: 'createdTime', index: "created_time", width: 85, formatter: function(value, options, row){
                if(value!=null){
                    return getDateTime(value,"yyyyMMddHHmmss");
                }else{
                    return "";
                }
            }},
			{ label: '更新人', name: 'updatedBy', index: 'updated_by', width: 80 }, 			
			{ label: '更新时间', name: 'updatedTime', index: "updated_time", width: 85, formatter: function(value, options, row){
              if(value!=null){
                return getDateTime(value,"yyyyMMddHHmmss");
              }else{
                    return "";
                }
            }},
            {header:'操作', name:'操作', width:139, sortable:false, title:false, align:'center', formatter: function(val, obj, row, act){
                    var actions = [];
                    if(shop_shopbank_shenhe===1 && shop_shopbank_shenheinfo===1 && row.approveFlag=='1'){
                        actions.push('<a class="btn btn-primary" onclick="vm.updatepass('+row.id+')" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;审核通过</a>&nbsp;');
                         actions.push('<a class="btn btn-primary" onclick="vm.updateUnpass('+row.id+')" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;审核不通过</a>&nbsp;');
//                        actions.push('<a title="审核通过" onclick="vm.updatepass('+row.id+')"><i class="fa fa-pencil">审核通过</i></a>&nbsp;');
//                        actions.push('<a title="审核不通过" onclick="vm.updateUnpass('+row.id+')"><i class="fa fa-pencil">审核不通过</i></a>&nbsp;');
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
		shopBank: {},
        shopList:[],
        shopName:'',
         q:{
            bankName:'',
            shopName:'',
            subBranch:'',
            cardNo:'',
            userName:'',
            mobile:'',
            status:'',
            approveFlag:''
        },

	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.shopBank = {};

            vm.getShopList();
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            vm.getInfo(id);

		},
        updatepass : function (id) {
            // var id = getSelectedRow();
            console.log("id==="+id);
            if(id == null){
                return ;
            }
            var shopBank = {
                id:id,
                status:'1',
                approveFlag:'2'
            }
            var lock = false;
            layer.confirm('确定审核通过？', {
                btn: ['确定','取消'] //按钮
            }, function(){
                if(!lock) {
                    lock = true;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "shop/shopbank/shenhe",
                        contentType: "application/json",
                        data: JSON.stringify(shopBank),
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
            vm.getInfo(id)
        },
        updateUnpass : function (id) {
            if(id == null){
                return ;
            }
            vm.showList = false;
            vm.title = "审核不通过";
            vm.shopBank.status = '0';
            vm.approveFlag='3',
            vm.msg='',
            vm.shopBank.id = id;
            // vm.getInfo(id)
        },
		saveOrUpdate: function (event) {
            if(vm.shopBank.id==null){
                return;
            }
            if(vm.shopBank.msg==null || vm.shopBank.msg==''){
                layer.msg("不通过原因不能为空", {icon: 2});
                return;
            }
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = "shop/shopbank/shenhe";

                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.shopBank),
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
			$.get(baseURL + "shop/shopbank/shenheinfo/"+id, function(r){
                vm.shopBank = r.shopBank;
                vm.getShopList(r.shopBank.shopId);
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
        //加载getShopList
        getShopList:function(id){
            $.ajaxSettings.async = false;
            $.get(baseURL + "shop/shop/selectlist", function(r){
                vm.shopList = r.list;
                if(id!=null && id!=''){
                    vm.setShopName(vm.shopBank.shopId);
                }
            });
            $.ajaxSettings.async = true;
        },
        //选择卡店铺
        selectShop: function (index) {
            vm.shopBank.shopId = vm.shopList[index].id;
            vm.shopName = vm.shopList[index].shopName;
        },
        setShopName:function(shopId){
            if(vm.shopList!=null && vm.shopList.length>0 && shopId!=null){
                vm.shopList.forEach(p=>{
                    if(p.id===shopId){
                        vm.shopName = p.shopName;
                    }
                });
            }
        },

	}
});
