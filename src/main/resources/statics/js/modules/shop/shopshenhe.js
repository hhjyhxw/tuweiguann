
var setting = {
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
var ztree;

$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'shop/shop/shenhelist',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true,align:"center" },
			{ label: '名称', name: 'shopName', index: 'shop_name', width: 130 },
            { label: '系统店铺', name: 'sysFlag', width: 100, formatter: function(value, options, row){
                    return value ==='0' ?
                        '<span class="label label-danger">不是</span>' :
                        '<span class="label label-success">是</span>';
                }},
			{ label: '级别', name: 'shopLevel', index: 'shop_level', width: 60 },
			{ label: '覆盖范围(米)', name: 'coverScope', index: 'cover_scope', width: 100 },
			{ label: '状态', name: 'status', width: 100, formatter: function(value, options, row){
					return value === '0' ?
						'<span class="label label-danger">关闭</span>' :
						'<span class="label label-success">开启</span>';
				}},
			 { label: '审核状态', name: 'review', width: 100, formatter: function(value, options, row){
                return value === '0' ?
                    '<span class="label label-danger">未审核</span>' :
                    (value==='1'?'<span class="label label-success">审核中</span>':
                    (value==='2'?'<span class="label label-success">审核通过</span>':
                    (value==='3'?'<span class="label label-success">审核失败</span>':'未审核')));
            }},
			{ label: '创建人', name: 'createdBy', index: 'created_by', width: 80 }, 			
			{ label: '创建时间', name: 'createTime', index: "create_time", width: 130, formatter: function(value, options, row){
			    if(value!=null){
			        return getDateTime(value,"yyyyMMddHHmmss");
			    }else{
			        return "";
			    }
            }},
			{ label: '更新人', name: 'updatedBy', index: 'updated_by', width: 80 }, 			
            { label: '更新时间', name: 'updatedTime', index: "updated_time", width: 130, formatter: function(value, options, row){
                      if(value!=null){
                        return getDateTime(value,"yyyyMMddHHmmss");
                      }else{
                            return "";
                        }
            }},
            {header:'操作', name:'操作', width:200, sortable:false, title:false, align:'center', formatter: function(val, obj, row, act){
           var actions = [];
                    if(shop_shop_shenhe===1 && row.review=='1'){
                        actions.push('<a class="btn btn-primary" onclick="vm.updatepass('+row.id+')" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;审核通过</a>&nbsp;');
                         actions.push('<a class="btn btn-primary" onclick="vm.updateUnpass('+row.id+')" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;审核不通过</a>&nbsp;');
                    }
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
        shrinkToFit:false,
        autoScroll: true,
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
        	//$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        	 $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "scroll" });
        }
    });
 });

var vm = new Vue({
	el:'#icloudapp',
	data:{
		showList: true,
		title: null,
		shop: {
            parentName:'',
            parentId:null,
            status:0,
            sysFlag:0,
            review:0,
            shopName:'',
            commissionRate:0,
        },
        user: {
		    userId:null
        },
        q:{
         shopName:'',
        }
	},
    created: function(){
        this.getUser();
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
		},
		 updatepass : function (id) {
            console.log("id==="+id);
            if(id == null){
                return ;
            }
            var shop = {
                id:id,
//                status:'1',
                review:'2'
            }
            var lock = false;
            layer.confirm('确定审核通过？', {
                btn: ['确定','取消'] //按钮
            }, function(){
                if(!lock) {
                    lock = true;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "shop/shop/shenhe",
                        contentType: "application/json",
                        data: JSON.stringify(shop),
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
            //vm.getInfo(id)
        },
        updateUnpass : function (id) {
            if(id == null){
                return ;
            }
            vm.showList = false;
            vm.title = "审核不通过";
            vm.shop.review='3',
            vm.msg='',
            vm.shop.id = id;
        },
        saveOrUpdate: function (event) {
            if(vm.shop.id==null){
                return;
            }
            if(vm.shop.msg==null || vm.shop.msg==''){
                layer.msg("不通过原因不能为空", {icon: 2});
                return;
            }
            $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = "shop/shop/shenhe";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.shop),
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
            $.get(baseURL + "shop/shop/shenheinfo/"+id, function(r){
                vm.shop = r.shop;
//                vm.getShopList(r.shopBank.shopId);
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
        //加载店铺树
        getShopTree: function(){
            //加载分类树
            $.get(baseURL + "shop/shop/select", function(r){
                // console.info("r==="+JSON.stringify(r))
                ztree = $.fn.zTree.init($("#deptTree"), setting, r.list);
                // console.log("ztree====="+JSON.stringify(ztree))
                var node = ztree.getNodeByParam("id", vm.shop.parentId);
                console.log("加载node====="+JSON.stringify(node))
                if(node!=null){
                    ztree.selectNode(node);
                    vm.shop.parentName = node.name;
                }
            })
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择父类",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级部门
                    console.log("node====="+JSON.stringify(node))
                    if(node!=null) {
                        //系统管理员可以添加一级店铺
                        vm.shop.parentId = node[0].id;
                        vm.shop.parentName = node[0].name;
                    }

                    layer.close(index);
                }
            });
        },
        //获取用户信息
        getUser: function(){
            $.getJSON(baseURL+"sys/user/info?_"+$.now(), function(r){
                vm.user = r.user;
            });
        },
	}
});
