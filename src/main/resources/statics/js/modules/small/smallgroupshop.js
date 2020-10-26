$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallgroupshop/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			/*{ label: 'spuId', name: 'spuId', index: 'spu_id', width: 80 },
            { label: 'skuId', name: 'skuId', index: 'spu_id', width: 80 },*/
            { label: '商品图片', name: 'sku.img', width: 60, formatter: function(value, options, row){
                    return '<img style="height: 3rem;width: 3rem;" src="'+value+'"/>';
                }},
            { label: '商品名称', name: 'sku.title', index: 'title', width: 80 },
            { label: '商品类型', name: 'commonFlag', width: 50, formatter: function(value, options, row){
                    return value == '0' ?
                        '<span class="label label-danger">自营商品</span>' :
                        (value=='1'?'<span class="label label-success">公共商品</span>':'其他');

                }},
            { label: '现价(元)', name: 'minPrice', index: 'minPrice', width: 50 },
			{ label: '原价(元)', name: 'maxPrice', index: 'maxPrice', width: 50 },
			{ label: '剩余库存', name: 'sku.remainStock', index: 'remainStock', width: 50 },
            { label: '店铺名称', name: 'shop.shopName', index: 'shopName', width: 80 },
            { label: '所属分类', name: 'smallCategory.title', index: 'category_id', width: 80 },
            { label: '公共商品所在店铺', name: 'sysShop.shopName', index: 'shopName', width: 80 },
            { label: '状态', name: 'status', width: 60, formatter: function(value, options, row){
                    return value === 0 ?
                        '<span class="label label-danger">下架</span>' :
                        '<span class="label label-success">上架</span>';
                }},
          /*  { label: '所属零售户', name: 'shop.shopName', index: 'shop_id', width: 80 },*/
            { label: '创建时间', name: 'createTime', index: "create_time", width: 85, formatter: function(value, options, row){
                if(value!=null){
                    return getDateTime(value,"yyyyMMddHHmmss");
                }else{
                    return "";
                }
            }},
            { label: '更新时间', name: 'modifyTime', index: "modify_time", width: 85, formatter: function(value, options, row){
                      if(value!=null){
                        return getDateTime(value,"yyyyMMddHHmmss");
                      }else{
                            return "";
                        }
            }},
            {header:'操作', name:'操作', width:90, sortable:false, title:false, align:'center', formatter: function(val, obj, row, act){
                var actions = [];
                    actions.push('<a class="btn btn-primary" onclick="vm.update('+row.id+')" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;修改</a>&nbsp;');
                    actions.push('<a class="btn btn-primary" onclick="vm.del('+row.id+')" style="padding: 3px 8px;"><i class="fa fa-trash-o"></i>&nbsp;删除</a>&nbsp;');
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
		smallGroupShop: {
            commonFlag:0
        },
        goodName:'',
        shopName:'',
        sysFlag:'0',//1系统店铺 0非系统店铺
        shopList:[],//店铺列表
        skuList :[],//对应店铺商品

        q:{
            title:'',
            shopName:'',
            sysShopName:'',
            categoryTitle:'',
            startTime:null,
            endTime:null,
            status:null,
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
			vm.smallGroupShop = {
                gmtStart:null,
                gmtEnd:null,
                commonFlag:0
            };
            vm.goodName='',
            vm.getShopList('');
		},
		update: function (id) {
//			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.smallGroupShop.id == null ? "small/smallgroupshop/save" : "small/smallgroupshop/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallGroupShop),
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
		del: function (id) {
//			var ids = getSelectedRows();
			if(id == null){
				return ;
			}
			var ids = [];
            ids.push(id);
			var lock = false;
            layer.confirm('确定要删除选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
		            $.ajax({
                        type: "POST",
                        url: baseURL + "small/smallgroupshop/delete",
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
			$.get(baseURL + "small/smallgroupshop/info/"+id, function(r){
                vm.smallGroupShop = r.smallGroupShop;
                vm.shopName = r.smallGroupShop.shop.shopName;
                vm.goodName = r.smallGroupShop.sku.title;
                vm.getShopList(r.smallGroupShop.shopId);
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
        //加载AttibutList
        getShopList:function(id){
            $.get(baseURL + "shop/shop/selfshoplist", function(r){
                vm.shopList = r.list;

                if(id!=null && id!=''){
                    vm.setShopName(vm.smallGroupShop.shopId);
                }else{
                    console.log("selectlist=="+JSON.stringify(r))
                    console.log("id=="+id)
                    if(r.list!=null && r.list.length>0){
                        vm.smallGroupShop.shopId = r.list[0].id;
                        vm.shopName = r.list[0].shopName;
                    }
                }
            });
        },
        //选择店铺
        selectShop: function (index) {
            vm.smallGroupShop.shopId = vm.shopList[index].id;
            vm.shopName = vm.shopList[index].shopName;
            // vm.getGoodsList(vm.shopList[index].id);//加载店铺sku列表
            vm.goodName = '';
            vm.sysFlag = vm.shopList[index].sysFlag;
            vm.smallGroupShop.spuId = null;
            vm.smallGroupShop.skuId = null;

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
        //选择自营或者公共商品
        selectCommonFlag: function (commonFlag) {
		    if(commonFlag==0){
		        vm.smallGroupShop.commonFlag = 0;
                //vm.getGoodsList(vm.smallGroupShop.shopId,false);//加载店铺sku列表
            }else {
                vm.smallGroupShop.commonFlag = 1;
                //vm.getGoodsList(vm.smallGroupShop.shopId,true);//加载店铺sku列表
            }
            vm.goodName = '';
            vm.smallGroupShop.spuId = null;
            vm.smallGroupShop.skuId = null;

        },
        //加载商品列表
        // getGoodsList:function(shopId,sysFlag){
        //     $.get(baseURL + "small/smallsku/skulistForGroup?shopId="+shopId+"&sysFlag="+sysFlag, function(r){
        //         vm.skuList = r.list;
        //     });
        // },
        //选择sku
        selectSku_bak: function (index) {
		    var goods = vm.skuList[index];
            vm.goodName = goods.title;
            vm.smallGroupShop.spuId = goods.spuId;
            vm.smallGroupShop.skuId = goods.id;
            vm.smallGroupShop.minPrice = goods.price;//团购价
            vm.smallGroupShop.maxPrice = goods.originalPrice;//原价
        },
        //打开添加sku弹窗 选择需要上团购 是航拍
        selectSku: function () {
            if(vm.smallGroupShop.commonFlag==0 || vm.smallGroupShop.commonFlag=='0'){//自营商品
                sysFlag = false;
            }else {
                sysFlag = true;
            }
            this.skuWinIndex = layer.open({
                title: '选择sku',
                type: 2,
                maxmin: true,
                move:true,
                shadeClose: true,
                area: ['65%', '65%'],
                btn: ['<i class="fa fa-check"></i> 确定', '<i class="fa fa-close"></i> 关闭'],
                content: baseURL + "modules/small/smallskuForGroup.html?shopId="+vm.smallGroupShop.shopId+"&sysFlag="+sysFlag,
                yes: function (index, layero) {
                    var iframeWin = window[layero.find('iframe')[0]['name']];
                    var smallSku = iframeWin.vm.smallSku;
                    console.log("smallSku====="+JSON.stringify(smallSku));
                    if($.trim(smallSku.id) == '') {
                        layer.msg("请选择sku",{icon: 0,time: 1000});return;
                    }

                    vm.goodName = smallSku.title;
                    vm.smallGroupShop.spuId = smallSku.spuId;
                    vm.smallGroupShop.skuId = smallSku.id;
                    vm.smallGroupShop.minPrice = smallSku.price;//团购价
                    vm.smallGroupShop.maxPrice = smallSku.originalPrice;//原价
                    console.log("vm.smallGroupShop====="+JSON.stringify(vm.smallGroupShop));
                    console.log("vm.goodName====="+JSON.stringify(vm.goodName));
                    layer.close(index);
                },
                success: function (layero, index) {
                    /*var info = '<font color="red" class="pull-left mt10">提示：双击可快速选择。</font>';
                    layero.find('.layui-layer-btn').append(info);*/
                }
            });
        },
        //选择商品弹出双击选中
        skuforgroupWinDblClick: function (smallSku) {
            vm.goodName = smallSku.title;
            vm.smallGroupShop.spuId = smallSku.spuId;
            vm.smallGroupShop.skuId = smallSku.id;
            vm.smallGroupShop.minPrice = smallSku.price;//团购价
            vm.smallGroupShop.maxPrice = smallSku.originalPrice;//原价
            console.log("vm.smallGroupShop====="+JSON.stringify(vm.smallGroupShop));
            console.log("vm.goodName====="+JSON.stringify(vm.goodName));
            layer.close(vm.skuWinIndex);
        },
	}
});
vm.getShopList('');