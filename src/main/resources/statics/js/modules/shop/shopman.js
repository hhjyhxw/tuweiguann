$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'shop/shopman/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
            { label: '所属店铺', name: 'shop.shopName', index: 'shop_id', width: 80 },
			{ label: '姓名', name: 'name', index: 'name', width: 80 }, 			
			{ label: '登录账号', name: 'accountNo', index: 'account_no', width: 80 }, 			
			{ label: '手机号', name: 'mobile', index: 'mobile', width: 80 }, 			
			{ label: '登录密码', name: 'pwd', index: 'pwd', width: 80 }, 			
            { label: '角色', name: 'role', width: 60, formatter: function(value, options, row){
                    return value === 0 ?
                        '<span class="label label-danger">管理员</span>' :
                        '<span class="label label-success">店员</span>';
                }},
            { label: '状态', name: 'role', width: 60, formatter: function(value, options, row){
                    return value === '0' ?
                        '<span class="label label-danger">关闭</span>' :
                        '<span class="label label-success">开启</span>';
                }},
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
		shopMan: {},
        shopList:[],
        shopName:'',
        q:{
            name:'',
            shopName:'',
            accountNo:'',
        },
        user: {
            userId:null
        },
        deptId:null,
        deptList:[],
        deptName:'',
	},
    created: function(){
        this.getUser();
        this.getDeptList();
    },
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.shopMan = {};

            vm.deptName = '',
            vm.deptId = null,
            vm.shopName = null;
            vm.getShopList();
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
                var url = vm.shopMan.id == null ? "shop/shopman/save" : "shop/shopman/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.shopMan),
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
                        url: baseURL + "shop/shopman/delete",
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
			$.get(baseURL + "shop/shopman/info/"+id, function(r){
                vm.shopMan = r.shopMan;
                vm.deptId = r.shopMan.deptId;
                vm.setDeptName(vm.deptId);
                vm.getShopList(r.shopMan.shopId);
               // vm.setShopName(r.shopMan.shopId);
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
		    console.log("id======"+id)
            $.ajaxSettings.async = false;
            $.get(baseURL + "shop/shop/selectlist?deptId="+vm.deptId, function(r){
                vm.shopList = r.list;
                if(id!=null && id!=''){
                    vm.setShopName(vm.shopMan.shopId);
                }
            });
            $.ajaxSettings.async = true;
        },
        //选择卡店铺
        selectShop: function (index) {
            vm.shopMan.shopId = vm.shopList[index].id;
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
        //加载企业列表
        getDeptList:function(){
            $.get(baseURL + "/sys/dept/selectlist", function(r){
                vm.deptList = r.deptList;
            });
        },
        //选择企业
        selectDept: function (index) {
            vm.shopMan.deptId = vm.deptList[index].deptId;
            vm.deptName = vm.deptList[index].name;
            vm.deptId = vm.deptList[index].deptId;
            vm.getShopList();
        },
        setDeptName:function(deptId){
            if(vm.deptList!=null && vm.deptList.length>0 && deptId!=null){
                vm.deptList.forEach(p=>{
                    if(p.deptId===deptId){
                        vm.deptName = p.name;
                    }
                });
            }
        },
        //获取用户信息
        getUser: function(){
            $.getJSON(baseURL+"sys/user/info?_"+$.now(), function(r){
                vm.user = r.user;
            });
        },
	}
});
vm.getShopList();