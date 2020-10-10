



//直接输入地址，光标离开的时候，地址解析
// $("#village").change(function(){
//     debugger;
//     // 创建地址解析器实例
//     var myGeo = new BMap.Geocoder();
//     //获取到地址
//     var address = $(this).val();
//     // 将地址解析结果显示在地图上,并调整地图视野
//     myGeo.getPoint(address, function(point){
//         if (point) {
//             map.centerAndZoom(point, 19);
//             map.addOverlay(new BMap.Marker(point));
//             //alert(point.lng+"和"+point.lat);
//             latitudeBaiDu = point.lat;
//             longitudeBaiDu = point.lng;
//             $("#latitude").attr("value",latitudeBaiDu);
//             $("#longitude").attr("value",longitudeBaiDu);
//             $("#address").attr("value",address);
//         }else{
//             alert("您选择地址没有解析到结果!");
//         }
//     }, "深圳市");
//
// });




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
        url: baseURL + 'shop/shop/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '上级店铺名称', name: 'parentName', index: 'parentName', width: 80 },
			{ label: '名称', name: 'shopName', index: 'shop_name', width: 80 },
            { label: '系统店铺', name: 'sysFlag', width: 60, formatter: function(value, options, row){
                    return value ==='0' ?
                        '<span class="label label-danger">不是</span>' :
                        '<span class="label label-success">是</span>';
                }},
			{ label: '级别', name: 'shopLevel', index: 'shop_level', width: 80 }, 			
			{ label: '覆盖范围(米)', name: 'coverScope', index: 'cover_scope', width: 80 },
			{ label: '状态', name: 'status', width: 60, formatter: function(value, options, row){
					return value === '0' ?
						'<span class="label label-danger">关闭</span>' :
						'<span class="label label-success">开启</span>';
				}},
			{ label: '审核', name: 'review', width: 60, formatter: function(value, options, row){
					return value === '0' ?
						'<span class="label label-danger">未审核</span>' :
						(value==='1'?'<span class="label label-success">审核通过</span>':'审核失败');
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


    new AjaxUpload('#upload', {
        action: baseURL + "sys/oss/uploadFront",
        name: 'file',
        autoSubmit:true,
        responseType:"json",
        onSubmit:function(file, extension){
            if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))){
                alert('只支持jpg、png、gif格式的图片！');
                return false;
            }
        },
        onComplete : function(file, r){
            console.log("r=="+JSON.stringify(r));
            console.log("file=="+file);
            if(r.code == 0){
                alert("上传成功!");
                vm.shop.shopImg = r.url;
                console.log("vm.shop.shopImg=="+vm.shop.shopImg);
                //vm.reload();
            }else{
                alert(r.msg);
            }
        }
    });


    $("#sssssssssss").click(function(){
        //获取到地址
        // 创建地址解析器实例
        var myGeo = new BMap.Geocoder();
        var address = $("#detailAddress").val();
        if(address==null || address==''){
            return;
        }
        // 将地址解析结果显示在地图上,并调整地图视野
        myGeo.getPoint(address, function(point){
            if (point) {
                map.centerAndZoom(point, 19);
                map.addOverlay(new BMap.Marker(point));
                //alert(point.lng+"和"+point.lat);
                console.log("point==="+JSON.stringify(point))
                latitudeBaiDu = point.lat;
                longitudeBaiDu = point.lng;
                vm.shop.lnt = longitudeBaiDu;
                vm.shop.lat = latitudeBaiDu;

                myGeo.getLocation(point, function(rs){
                    console.log("rs=="+JSON.stringify(rs))
                    var addComp = rs.addressComponents;
                    var addressBaiDu =  addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;
                    <!--var addressBaiDu = addComp.province + addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;-->
                    vm.shop.province = addComp.province;
                    vm.shop.city = addComp.city;
                    vm.shop.county = addComp.district;
                    vm.shop.address = addComp.street + addComp.streetNumber;
                });
            }else{
                alert("您选择地址没有解析到结果!");
            }
        }, "深圳市");

    });

    //直接输入地址，光标离开的时候，地址解析
    $("#address").blur(function(){
        // 创建地址解析器实例
        var myGeo = new BMap.Geocoder();
        //获取到地址
        var address = $("#address").val();
        // 将地址解析结果显示在地图上,并调整地图视野
        myGeo.getPoint(address, function(point){
            if (point) {
                map.centerAndZoom(point, 19);
                map.addOverlay(new BMap.Marker(point));
                //alert(point.lng+"和"+point.lat);
                console.log("point==="+JSON.stringify(point))
                latitudeBaiDu = point.lat;
                longitudeBaiDu = point.lng;
                vm.shop.lnt = longitudeBaiDu;
                vm.shop.lat = latitudeBaiDu;

                myGeo.getLocation(point, function(rs){
                    console.log("rs=="+JSON.stringify(rs))
                    var addComp = rs.addressComponents;
                    var addressBaiDu =  addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;
                    <!--var addressBaiDu = addComp.province + addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;-->
                    vm.shop.province = addComp.province;
                    vm.shop.city = addComp.city;
                    vm.shop.county = addComp.district;
                    // vm.shop.address = addComp.street + addComp.streetNumber;
                });
            }else{
                alert("您选择地址没有解析到结果!");
            }
        }, "深圳市");
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
            commissionRate:0,
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
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id);

		},
		saveOrUpdate: function (event) {
            if(!priceCheck(vm.shop.commissionRate)){
                layer.msg("佣金率不能为空,且为数字,最多保留两位小数", {icon: 2});
                return;
            }
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.shop.id == null ? "shop/shop/save" : "shop/shop/update";
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
                        url: baseURL + "shop/shop/delete",
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
			$.get(baseURL + "shop/shop/info/"+id, function(r){
                vm.shop = r.shop;
                vm.deptId = r.shop.deptId;
                vm.setDeptName(vm.deptId);
                vm.getShopTree();
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		},
        //加载店铺树
        getShopTree: function(){
            //加载分类树
            $.get(baseURL + "shop/shop/select?deptId="+vm.deptId, function(r){
                // console.info("r==="+JSON.stringify(r))
                ztree = $.fn.zTree.init($("#deptTree"), setting, r.retailList);
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
                        if(vm.user.userId!=1){
                            if (node[0].parentId === -1) {
                                return;
                            }
                        }
                        vm.shop.parentId = node[0].id;
                        vm.shop.parentName = node[0].name;
                    }

                    layer.close(index);
                }
            });
        },
        //加载企业列表
        getDeptList:function(){
            $.get(baseURL + "/sys/dept/selectlist", function(r){
                vm.deptList = r.deptList;
            });
        },
        //选择企业
        selectDept: function (index) {
            vm.shop.deptId = vm.deptList[index].deptId;
            vm.deptName = vm.deptList[index].name;
            vm.deptId = vm.deptList[index].deptId;
            vm.getShopTree();
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

var latitudeBaiDu ;
var longitudeBaiDu ;
// 百度地图初始化
var map = new BMap.Map("allmap");    // 创建Map实例
console.log("map==="+map)
var point = new BMap.Point(114.05, 22.55); //深圳市
map.centerAndZoom(point, 13);  // 初始化地图,设置中心点坐标和地图级别
map.addControl(new BMap.MapTypeControl());   //添加地图类型控件
map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
var marker = new BMap.Marker(point);
map.addOverlay(marker);            //添加标注



var geoc = new BMap.Geocoder();
//直接点击地图获取地址和经纬度
map.addEventListener("click", function(e){
    var pt = e.point;
    geoc.getLocation(pt, function(rs){
        console.log("rs=="+JSON.stringify(rs))
        var addComp = rs.addressComponents;
        var addressBaiDu =  addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;
        <!--var addressBaiDu = addComp.province + addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;-->
        vm.shop.province = addComp.province;
        vm.shop.city = addComp.city;
        vm.shop.county = addComp.district;
        vm.shop.address = addComp.street + addComp.streetNumber;
    });
    latitudeBaiDu = e.point.lat;
    longitudeBaiDu = e.point.lng;
    vm.shop.lnt = longitudeBaiDu;
    vm.shop.lat = latitudeBaiDu;

});

