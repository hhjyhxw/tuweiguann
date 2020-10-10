$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'shop/shopstore/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
            { label: '所属店铺', name: 'shop.shopName', index: 'shop_id', width: 80 },
			{ label: '仓库名称', name: 'titile', index: 'titile', width: 80 }, 			
			{ label: '省', name: 'province', index: 'province', width: 80 }, 			
			{ label: '市', name: 'city', index: 'city', width: 80 }, 			
			{ label: '县', name: 'county', index: 'county', width: 80 }, 			
			{ label: '详细地址', name: 'address', index: 'address', width: 80 }, 			
			{ label: '联系电话', name: 'phone', index: 'phone', width: 80 }, 			
			{ label: '经度', name: 'lnt', index: 'lnt', width: 80 }, 			
			{ label: '纬度', name: 'lat', index: 'lat', width: 80 }, 			
			{ label: '配送范围(米)', name: 'distributionScope', index: 'distribution_scope', width: 80 }, 			
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
                vm.shopStore.lnt = longitudeBaiDu;
                vm.shopStore.lat = latitudeBaiDu;

                myGeo.getLocation(point, function(rs){
                    console.log("rs=="+JSON.stringify(rs))
                    var addComp = rs.addressComponents;
                    var addressBaiDu =  addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;
                    <!--var addressBaiDu = addComp.province + addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;-->
                    vm.shopStore.province = addComp.province;
                    vm.shopStore.city = addComp.city;
                    vm.shopStore.county = addComp.district;
                    vm.shopStore.address = addComp.street + addComp.streetNumber;
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
                vm.shopStore.lnt = longitudeBaiDu;
                vm.shopStore.lat = latitudeBaiDu;

                myGeo.getLocation(point, function(rs){
                    console.log("rs=="+JSON.stringify(rs))
                    var addComp = rs.addressComponents;
                    var addressBaiDu =  addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;
                    <!--var addressBaiDu = addComp.province + addComp.city+ addComp.district+ addComp.street+ addComp.streetNumber;-->
                    vm.shopStore.province = addComp.province;
                    vm.shopStore.city = addComp.city;
                    vm.shopStore.county = addComp.district;
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
		shopStore: {},
        shopList:[],
        shopName:'',
        q:{
            titile:'',
            shopName:'',
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
			vm.shopStore = {
                status:0,
                lnt:null,
                lat:null,
                province:'',
                city:'',
                county:'',
                address:'',
            };
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
                var url = vm.shopStore.id == null ? "shop/shopstore/save" : "shop/shopstore/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.shopStore),
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
                        url: baseURL + "shop/shopstore/delete",
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
			$.get(baseURL + "shop/shopstore/info/"+id, function(r){
                vm.shopStore = r.shopStore;
                vm.deptId = r.shopStore.deptId;
                vm.setDeptName(vm.deptId);
                vm.getShopList(r.shopStore.shopId);
                // vm.setShopName(r.shopStore.shopId);
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
            $.get(baseURL + "shop/shop/selectlist?deptId="+vm.deptId, function(r){
                vm.shopList = r.list;
                if(id!=null && id!=''){
                    vm.setShopName(vm.shopStore.shopId);
                }
            });
        },
        //选择卡店铺
        selectShop: function (index) {
            vm.shopStore.shopId = vm.shopList[index].id;
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
            vm.shopStore.deptId = vm.deptList[index].deptId;
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
        vm.shopStore.province = addComp.province;
        vm.shopStore.city = addComp.city;
        vm.shopStore.county = addComp.district;
        vm.shopStore.address = addComp.street + addComp.streetNumber;
    });
    latitudeBaiDu = e.point.lat;
    longitudeBaiDu = e.point.lng;
    vm.shopStore.lnt = longitudeBaiDu;
    vm.shopStore.lat = latitudeBaiDu;

});

