var editor1;
KindEditor.ready(function(K) {
    editor1 = K.create('textarea[name="detail"]',{
            //参数配置
            width : '95%',
            filePostName: "file",
            uploadJson:  baseURL + "sys/oss/uploadFrontFoylay",
            minHeight: '450',
            resizeType : 0,//禁止拉伸，1可以上下拉伸，2上下左右拉伸
            filterMode: false,//true时过滤HTML代码，false时允许输入任何代码。
            itmes:  [
                'source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'code', 'cut', 'copy', 'paste',
                'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
                'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
                'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
                'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
                'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
                'flash', 'media', 'insertfile', 'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
                'anchor', 'link', 'unlink', '|', 'about'
            ]
        }

    );
    //  prettyPrint();
});

/**
 * 商品分类选择树
 * @type {{data: {simpleData: {idKey: string, enable: boolean, pIdKey: string, rootPId: number}, key: {url: string}}}}
 */
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
        url: baseURL + 'small/smallcoupon/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '优惠券名称', name: 'title', index: 'title', width: 80 },
			/*{ label: '使用类型，如满减', name: 'coupType', index: 'coup_type', width: 80 }, 	*/
			/*{ label: '描述', name: 'description', index: 'description', width: 80 },*/
			{ label: '发行总数', name: 'total', index: 'total', width: 80 },
            { label: '已领取数', name: 'total', index: 'total', width: 80 },
            { label: '优惠券类型', name: 'orderStatus', width: 60, formatter: function(value, options, row){
                    return value === 0 ?
                        '<span class="label label-danger">默认类型</span>' :
                        (value===1?'<span class="label label-success">新用户专用</span>':
                            '其他');
                }},
			{ label: '每人限领', name: 'limits', index: 'limits', width: 80 },
			{ label: '满多少（元）', name: 'min', index: 'min', width: 80 },
            { label: '减多少（元）', name: 'discount', index: 'discount', width: 80 },
			{ label: '是否可用 0不用 1可用', name: 'status', index: 'status', width: 80 }, 			
			{ label: '可用分类', name: 'categoryId', index: 'category_id', width: 80 },
		/*	{ label: '过期天数', name: 'days', index: 'days', width: 80 },
			{ label: '领取开始时间', name: 'startTime', index: 'start_time', width: 80 }, 			
			{ label: '领取/使用结束时间', name: 'endTime', index: 'end_time', width: 80 }, 	*/
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80 }, 			
			{ label: '修改时间', name: 'modifyTime', index: 'modify_time', width: 80 }, 			
			/*{ label: '商户id', name: 'shopId', index: 'supplier_id', width: 80 },
			{ label: '企业Id', name: 'deptId', index: 'dept_id', width: 80 }			*/
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
		smallCoupon: {
            coupType:1,
            surplus:0,
            startTime:null,
            endTime:null,
            validateType:1
        },
        smallCategoryName:'',
        shopList:[],
        shopName:'',
        q:{
            name:'',
        },

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
			vm.smallCoupon = {
                coupType:1,
                surplus:0,
                startTime:null,
                endTime:null,
                validateType:1
            };
			vm.getShopList('');
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
            if(!priceCheck(vm.smallCoupon.min)){
                layer.msg("最低消费金额不能为空,且为数字,最多保留两位小数", {icon: 2});
            }
            if(!priceCheck(vm.smallCoupon.discount)){
                layer.msg("优惠金额不能为空,且为数字,最多保留两位小数", {icon: 2});
                return;
            }
            if(vm.smallCoupon.surplus==0 && (vm.smallCoupon.startTime==null ||  vm.smallCoupon.startTime=='')){
                layer.msg("有效开始时间不能为空", {icon: 2});
                return;
            }
            if(vm.smallCoupon.surplus==0 && (vm.smallCoupon.endTime==null ||  vm.smallCoupon.endTime=='')){
                layer.msg("有效结束时间不能为空", {icon: 2});
                return;
            }
            if(vm.smallCoupon.surplus==1 && (vm.smallCoupon.days==null ||  vm.smallCoupon.days=='' || vm.smallCoupon.days<=0)){
                layer.msg("有效天天数不能空，且大于0", {icon: 2});
                return;
            }
            vm.smallCoupon.description=editor1.html();
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.smallCoupon.id == null ? "small/smallcoupon/save" : "small/smallcoupon/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallCoupon),
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
                        url: baseURL + "small/smallcoupon/delete",
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
			$.get(baseURL + "small/smallcoupon/info/"+id, function(r){
                vm.smallCoupon = r.smallCoupon;
                editor1.html(vm.smallCoupon.description);
                vm.getShopList(r.smallCoupon.shopId);
                //加载商品分类
                vm.getCategory();
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		},
        //加载分类树
        getCategory: function(){
            //加载分类树
            $.get(baseURL + "small/smallcategory/select", function(r){
                ztree = $.fn.zTree.init($("#categroyTree"), setting, r.categoryList);
                // console.log("ztree====="+JSON.stringify(ztree))
                var node = ztree.getNodeByParam("id", vm.smallCoupon.categoryId);
                // console.log("加载node====="+JSON.stringify(node))
                if(node!=null){
                    ztree.selectNode(node);
                    vm.smallCategoryName = node.name;
                }
            })
        },
        categroyTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择分类",
                area: ['300px', '300px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#categroyLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择分类
                    // console.log("node====="+JSON.stringify(node))
                    if(node!=null) {
                        if(node[0].id===0){
                            return;
                        }
                        vm.smallCoupon.categoryId = node[0].id;
                        vm.smallCategoryName = node[0].name;
                    }
                    layer.close(index);
                }
            });
        },

        //加载getShopList
        getShopList:function(id){
            console.log("id======"+id)
            $.ajaxSettings.async = false;
            $.get(baseURL + "shop/shop/selectlist", function(r){
                vm.shopList = r.list;
                if(id!=null && id!=''){
                    vm.setShopName(vm.smallCoupon.shopId);
                }else{
                    if(r.list!=null && r.list.length>0){
                        vm.smallCoupon.shopId = r.list[0].id;
                        vm.shopName = r.list[0].shopName;
                    }
                }
            });
            $.ajaxSettings.async = true;
        },
        //选择卡店铺
        selectShop: function (index) {
            vm.smallCoupon.shopId = vm.shopList[index].id;
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