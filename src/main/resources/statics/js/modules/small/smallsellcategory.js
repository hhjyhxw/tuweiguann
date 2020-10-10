$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallsellcategory/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '分类名称', name: 'title', index: 'title', width: 80 }, 			
			{ label: '父类id', name: 'parentId', index: 'parent_id', width: 80 }, 			
			{ label: '分类图标地址', name: 'picUrl', index: 'pic_url', width: 80 }, 			
			{ label: '分类级别', name: 'level', index: 'level', width: 80 }, 			
			{ label: '状态', name: 'status', index: 'status', width: 80 }, 			
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80 }, 			
			{ label: '修改时间', name: 'modifyTime', index: 'modify_time', width: 80 }, 			
			{ label: '排序', name: 'sortNum', index: 'sort_num', width: 80 }, 			
			{ label: '店铺id', name: 'shopId', index: 'shop_id', width: 80 }
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

var settingretail = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};
var retialztree;

var vm = new Vue({
	el:'#icloudapp',
	data:{
		showList: true,
		title: null,
		smallSellCategory: {
            smallRetail:{
                supplierName:null
            }
        },
        user: {
            userId:null
        },//当前登陆用户
        deptId:null,//部门id(企业id)
        deptList:[],//部门列表（企业列表）
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
			vm.smallSellCategory = {
                shopId:null,
                smallRetail:{
                    supplierName:null
                }
            };
            vm.deptId=null,//部门id(企业id)
            vm.deptName='',

            vm.getRetailList();
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            vm.deptId=null,//部门id(企业id)
            vm.deptName='',
            vm.getInfo(id);
            // vm.getRetailList();
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.smallSellCategory.id == null ? "small/smallsellcategory/save" : "small/smallsellcategory/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallSellCategory),
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
                        url: baseURL + "small/smallsellcategory/delete",
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
			$.get(baseURL + "small/smallsellcategory/info/"+id, function(r){
                vm.smallSellCategory = r.smallSellCategory;
                vm.smallSellCategory.smallRetail = {
                    supplierName:null
                };
                //设置部门信息
                vm.deptId = r.smallSellCategory.deptId;
                vm.setDeptName(vm.deptId);
                vm.getRetailList();
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		},
        //加载零售户
        getRetailList: function(){
            //加载
            $.get(baseURL + "shop/shop/select?deptId="+vm.deptId, function(r){
                console.log("r====="+JSON.stringify(r))
                retialztree = $.fn.zTree.init($("#retailTree"), settingretail, r.retailList);
                var node = retialztree.getNodeByParam("id", vm.smallSellCategory.shopId);
                // console.log("加载node====="+JSON.stringify(node))
                if(node!=null){
                    retialztree.selectNode(node);
                    vm.smallSellCategory.smallRetail.supplierName = node.name;
                }
            })
        },
        //加载零售户
        retailTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择零售户",
                area: ['300px', '300px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#retailLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = retialztree.getSelectedNodes();
                    if(node!=null) {
                        if (node[0].parentId === -1) {
                            return;
                        }
                        //选择
                        // console.log("node====="+JSON.stringify(node))
                        vm.smallSellCategory.shopId = node[0].id;
                        vm.smallSellCategory.smallRetail.supplierName = node[0].name;
                    }
                    layer.close(index);
                }
            });
        },
        setShopName:function(shopId){
            if(vm.deptList!=null && vm.deptList.length>0 && deptId!=null){
                vm.deptList.forEach(p=>{
                    if(p.deptId===deptId){
                        vm.deptName = p.name;
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
            vm.smallSellCategory.deptId = vm.deptList[index].deptId;
            vm.deptName = vm.deptList[index].name;
            vm.deptId = vm.deptList[index].deptId;
            vm.getRetailList();
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