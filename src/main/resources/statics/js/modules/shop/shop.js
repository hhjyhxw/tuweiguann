
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
   /* $("#jqGrid").jqGrid({
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
			{ label: '创建时间', name: 'createTime', index: "create_time", width: 85, formatter: function(value, options, row){
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
    });*/


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
            shopName:'',
            commissionRate:0,
            user:{
                username:'',
                password:'',
                email:'',
                mobile:'',
            }
        },
        nickname:'',
        user: {
		    userId:null
        },
        q:{
            shopName:'',
            city:'',
            county:'',
            address:'',
            province:'',
            description:'',
            status:'',
            review:'',
            shopTel:'',
            sysFlag:'',
            shopLevel:'',
            startTime:'',
            endTime:'',

        }
	},
    created: function(){
        this.getUser();
    },
	methods: {
	    inputUserFunction: function(){
	        if(vm.shop.user!=null && vm.shop.user.userId!=null){
                return;
	        }
	        $.ajax({
                type: "POST",
                url: baseURL + "shop/shop/getUsernameByShopname",
                contentType: "application/json",
                 data: JSON.stringify(vm.shop),
                success: function(r){
                    if(r.code == 0){
                        layer.msg("操作成功", {icon: 1});
                        //$("#jqGrid").trigger("reloadGrid");
                       vm.shop.user.username= r.username;
                    }else{
                       // layer.alert(r.msg);
                    }
                }
             });
	    },
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
                user:{
                    username:'',
                    password:'',
                    email:'',
                    mobile:'',
                }
            };
            vm.getShopTree();
		},
		update: function (id) {
//			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id);

		},
		updateStatus:function(id,status){
            if(id == null){
                return ;
            }
            var shop = {
                id:id,
                status:status
            };
            var text = status=='1'?'开启':'关闭';
            var lock = false;
            layer.confirm('确定要'+text+'店铺？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "shop/shop/updateStatus",
                        contentType: "application/json",
                         data: JSON.stringify(shop),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
                                //$("#jqGrid").trigger("reloadGrid");
                                vm.reload();
                            }else{
                                layer.alert(r.msg);
                            }
                        }
                   	    });
                    }
            }, function(){
            });
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
		del: function (id) {
//			var ids = getSelectedRows();
//			if(ids == null){
//				return ;
//			}
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
                        url: baseURL + "shop/shop/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
//                                $("#jqGrid").trigger("reloadGrid");
                                vm.reload();
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
                vm.getShopTree();
            });
		},
		reload: function (event) {
			vm.showList = true;
//			var page = $("#jqGrid").jqGrid('getGridParam','page');
//			$("#jqGrid").jqGrid('setGridParam',{
//                 postData:vm.q,
//                 page: 1
//            }).trigger("reloadGrid");
            vm.getData();
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
        getData:function(){
             var treetable_url = baseURL + "shop/shop/list?shopName="+vm.q.shopName+"&province="+vm.q.province+"&city="+vm.q.city+"&county="+vm.q.county
                +"&address="+vm.q.address+"&description="+vm.q.description+"&status="+vm.q.status
                +"&review="+vm.q.review+"&shopTel="+vm.q.shopTel+"&sysFlag="+vm.q.sysFlag
                +"&shopLevel="+vm.q.shopLevel+"&startTime="+vm.q.startTime+"&endTime="+vm.q.endTime;
                var colunms = Dept.initColumn();
                var table = new TreeTable(Dept.id, treetable_url, colunms);
                //console.info("table==="+JSON.stringify(table))
//                table.setRootCodeValue(r.id);
                table.setExpandColumn(2);
                table.setIdField("id");
                table.setCodeField("id");
                table.setParentCodeField("parentId");
                table.setExpandAll(false);
                table.init();
                Dept.table = table;
        },
        selectWxUser:function(){
            this.wxUserWinIndex = layer.open({
                title: '选择sku',
                type: 2,
                maxmin: true,
                move:true,
                shadeClose: true,
                area: ['65%', '65%'],
                btn: ['<i class="fa fa-check"></i> 确定', '<i class="fa fa-close"></i> 关闭'],
                content: baseURL + "modules/wx/wxuserWin.html",
                yes: function (index, layero) {
                    var iframeWin = window[layero.find('iframe')[0]['name']];
                    var wxUser = iframeWin.vm.wxUser;
                    console.log("wxUser====="+JSON.stringify(wxUser));
                    if($.trim(wxUser.id) == '') {
                        layer.msg("请选择微信用户",{icon: 0,time: 1000});return;
                    }
                    vm.nickname = wxUser.nickname;
                    vm.shop.userId = wxUser.id;
                    console.log("vm.shop====="+JSON.stringify(vm.shop));
                    console.log("vm.nickname====="+JSON.stringify(vm.nickname));
                    layer.close(index);
                },
                success: function (layero, index) {
                    /*var info = '<font color="red" class="pull-left mt10">提示：双击可快速选择。</font>';
                    layero.find('.layui-layer-btn').append(info);*/
                }
            });
        },
         //双击选中用户
        wxuserforgroupWinDblClick: function (wxUser) {
            vm.nickname = wxUser.nickname;
            vm.shop.userId = wxUser.id;
            layer.close(vm.wxUserWinIndex);
        },
        //获取用户信息
        getUser: function(){
            $.getJSON(baseURL+"sys/user/info?_"+$.now(), function(r){
                vm.user = r.user;
            });
        },
	}
});



var Dept = {
    id: "deptTable",
    table: null,
    layerIndex: -1
};


/**
 * 初始化表格的列
 */
Dept.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle', width: '50px'},
        {title: '名称', field: 'shopName', align: 'center', valign: 'middle', sortable: true, width: '80px'},
        { title: '图标', field: 'shopImg', width: '60px', formatter: function(item, index){
           return '<img style="height: 3rem;width: 3rem;" src="'+item.shopImg+'"/>';
         }},
         { title: '系统标志', field: 'sysFlag', width: '60px', formatter: function(item, index){
             return item.status == '1' ? '<span class="label label-danger">是</span>' :
                 '<span class="label label-success">否</span>';
         }},
         {title: '省份', field: 'province', align: 'center', valign: 'middle', sortable: true, width: '80px'},
          {title: '城市', field: 'city', align: 'center', valign: 'middle', sortable: true, width: '60px'},
         {title: '地区', field: 'county', align: 'center', valign: 'middle', sortable: true, width: '60px'},
       /* {title: '上级店铺', field: 'parentName', align: 'center', valign: 'middle', false: true, width: '50px'},*/
        { title: '状态', field: 'status', width: '60px', formatter: function(item, index){
                return item.status =='1' ?
                    '<span class="label label-danger">启用</span>' :
                    '<span class="label label-success">停用</span>';
         }},
        { label: '审核状态', name: 'review', width: '60px', formatter: function(item, index){
             return item.review == '0' ?
                 '<span class="label label-danger">未审核</span>' :
                 (item.review=='1'?'<span class="label label-success">审核通过</span>':
                  (item.review=='2'?'<span class="label label-success">审核失败</span>':'<span class="label label-success">未审核</span>'));
         }},
      /*   { title: '创建时间', field: 'createTime', align: 'center',  valign: 'middle', false: true, width: '80px', formatter: function(item, index){
        	if(item.createTime!=null){
                return getDateTime(item.createTime,"yyyyMMddHHmmss");
            }else{
                return "";
            }
        }},
         { title: '修改时间', field: 'modifyTime',align: 'center', width: '80px', valign: 'middle', false: true, formatter: function(item, index){
             if(item.createTime!=null){
                return getDateTime(item.createTime,"yyyyMMddHHmmss");
            }else{
                return "";
            }
        }},
        {title: '排序号', field: 'sortNum', align: 'center', valign: 'middle', sortable: false, width: '50px'},*/
        {title:'操作', field:'操作', width: '80px', sortable:false, title:"操作", align:'center', formatter: function(item, index){
                        var actions = [];
                            actions.push('<a class="btn btn-primary" onclick="vm.update('+item.id+')" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;修改</a>&nbsp;');
                            if(item.status=='0'){
                                 actions.push('<a class="btn btn-primary" onclick="vm.updateStatus('+item.id+',1)" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;开启</a>&nbsp;');
                            }
                             if(item.status=='1'){
                                 actions.push('<a class="btn btn-primary" onclick="vm.updateStatus('+item.id+',0)" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;关闭</a>&nbsp;');
                            }
                            actions.push('<a class="btn btn-primary" onclick="vm.del('+item.id+')" style="padding: 3px 8px;"><i class="fa fa-trash-o"></i>&nbsp;删除</a>&nbsp;');
                        return actions.join('');
                    }}
        ]
    return columns;
};
vm.getData();

//function getShopId () {
//    var selected = $('#deptTable').bootstrapTreeTable('getSelections');
//    if (selected.length == 0) {
//        alert("请选择一条记录");
//        return null;
//    } else {
//        return selected[0].id;
//    }
//}





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

