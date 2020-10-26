$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/role/list',
        datatype: "json",
        colModel: [
            { label: '角色ID', name: 'roleId', index: "role_id", width: 45, key: true },
            { label: '角色名称', name: 'roleName', index: "role_name", width: 75 },
            { label: '所属部门', name: 'shopName', sortable: false, width: 75 },
            { label: '备注', name: 'remark', width: 100 },
            { label: '创建时间', name: 'createTime', index: "create_time", width: 85, formatter: function(value, options, row){
                return getDateTime(row.createTime,"yyyyMMddHHmmss");
            }},

            {header:'操作', name:'操作', width:90, sortable:false, title:false, align:'center', formatter: function(val, obj, row, act){
                var actions = [];
                  /*  actions.push('<a class="btn btn-primary" onclick="vm.add()" style="padding: 3px 8px;"><i class="fa fa-plus"></i>&nbsp;新增</a>&nbsp;');*/
                    actions.push('<a class="btn btn-primary" onclick="vm.update('+row.roleId+')" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;修改</a>&nbsp;');
                    actions.push('<a class="btn btn-primary" onclick="vm.del('+row.roleId+')" style="padding: 3px 8px;"><i class="fa fa-trash-o"></i>&nbsp;删除</a>&nbsp;');
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

//菜单树
var menu_ztree;
var menu_setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "menuId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    },
    check:{
        enable:true,
        nocheckInherit:true
    }
};

//部门结构树(在次更换成店铺树)
var shop_ztree;
var shop_setting = {
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

//数据树
var data_ztree;
var data_setting = {
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
    },
    check:{
        enable:true,
        nocheckInherit:true,
        chkboxType:{ "Y" : "", "N" : "" }
    }
};

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            roleName: null
        },
        showList: true,
        title:null,
        role:{
            id:null,
            shopId:null,
            shopName:''
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.role = {shopName:null, shopId:null};
            vm.getMenuTree(null);
            //加载店铺
            vm.shoplist();

            //vm.getDataTree();
        },
        update: function (roleId) {
            //var roleId = getSelectedRow();
            if(roleId == null){
                return ;
            }

            vm.showList = false;
            vm.title = "修改";
            //vm.getDataTree();
            vm.getMenuTree(roleId);
            vm.shoplist();
        },
        del: function (roleId) {
//            var roleIds = getSelectedRows();
            if(roleId == null){
                return ;
            }

            var roleIds = [];
            roleIds.push(roleId);
            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/role/delete",
                    contentType: "application/json",
                    data: JSON.stringify(roleIds),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        getRole: function(roleId){
            $.get(baseURL + "sys/role/info/"+roleId, function(r){
                vm.role = r.role;

                //勾选角色所拥有的菜单
                var menuIds = vm.role.menuIdList;
                for(var i=0; i<menuIds.length; i++) {
                    var node = menu_ztree.getNodeByParam("menuId", menuIds[i]);
                    menu_ztree.checkNode(node, true, false);
                }

                //勾选角色所拥有的部门数据权限
                // var deptIds = vm.role.deptIdList;
                var shopIds = vm.role.shopIdList;
                for(var i=0; i<shopIds.length; i++) {
                    var node = data_ztree.getNodeByParam("id", shopIds[i]);
                    data_ztree.checkNode(node, true, false);
                }

                vm.shoplist();
            });
        },
        saveOrUpdate: function () {
            //获取选择的菜单
            var nodes = menu_ztree.getCheckedNodes(true);
            var menuIdList = new Array();
            for(var i=0; i<nodes.length; i++) {
                menuIdList.push(nodes[i].menuId);
            }
            vm.role.menuIdList = menuIdList;

            //获取选择的数据
            var nodes = data_ztree.getCheckedNodes(true);
            // var deptIdList = new Array();
            var shopIdList = new Array();
            for(var i=0; i<nodes.length; i++) {
                shopIdList.push(nodes[i].id);
            }
            vm.role.shopIdList = shopIdList;

            var url = vm.role.roleId == null ? "sys/role/save" : "sys/role/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.role),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        getMenuTree: function(roleId) {
            //加载菜单树
            $.get(baseURL + "sys/menu/list", function(r){
                menu_ztree = $.fn.zTree.init($("#menuTree"), menu_setting, r);
                //展开所有节点
                menu_ztree.expandAll(true);

                if(roleId != null){
                    vm.getRole(roleId);
                }
            });
        },
        getDataTree: function(roleId) {
            //加载菜单树
            $.get(baseURL + "shop/shop/queryList", function(r){
                console.log("queryList====="+r.list);
                data_ztree = $.fn.zTree.init($("#dataTree"), data_setting, r.list);
                //展开所有节点
                data_ztree.expandAll(true);
            });
        },
        shoplist: function(){
            $.get(baseURL + "shop/shop/queryList", function(r){
                shop_ztree = $.fn.zTree.init($("#shopTree"), shop_setting, r.list);
                var node = shop_ztree.getNodeByParam("id", vm.role.shopId);
                if(node != null){
                    shop_ztree.selectNode(node);
                    vm.role.shopName = node.name;
                }
            })
        },
        shopTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#shopLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = shop_ztree.getSelectedNodes();
                    //选择上级部门
                    vm.role.shopId = node[0].id;
                    vm.role.shopName = node[0].name;

                    layer.close(index);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'roleName': vm.q.roleName},
                page:page
            }).trigger("reloadGrid");
        }
    }
});