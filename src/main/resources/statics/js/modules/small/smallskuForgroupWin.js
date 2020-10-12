$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallsku/listForgroup',
        datatype: "json",
        postData: {"sysFlag":T.p('sysFlag'),"shopId":T.p('shopId')},
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '商品spuid', name: 'spuId', index: 'spu_id', width: 80 },
			/*{ label: 'sku条码', name: 'barCode', index: 'bar_code', width: 80 }, */
			{ label: 'sku名称', name: 'title', index: 'title', width: 80 }, 			
			{ label: '图片', name: 'img', index: 'img', width: 80 }, 			
			{ label: '原始价', name: 'originalPrice', index: 'original_price', width: 80 }, 			
			{ label: '现价', name: 'price', index: 'price', width: 80 }, 			
			/*{ label: 'vip价', name: 'vipPrice', index: 'vip_price', width: 80 }, 			*/
            { label: '店铺名称', name: 'shop.shopName', index: 'shop.shopName', width: 80 },
			{ label: '库存', name: 'stock', index: 'stock', width: 80 },
			{ label: '冻结库存', name: 'freezeStock', index: 'freeze_stock', width: 80 }, 			
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80 }, 			
			{ label: '修改时间', name: 'modifyTime', index: 'modify_time', width: 80 }			
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
        },
        /**
         *  单击选中
         */
        onSelectRow: function (id) {
            vm.smallSku = $(this).jqGrid("getRowData",id);
            // vm.smallSku = $(this).dataGrid('getRowData', id);
        },
        /**
         *  双击选择
         */
        ondblClickRow: function (id) {
            vm.smallSku = $(this).jqGrid("getRowData",id);
            // vm.smallSku = $(this).dataGrid('getRowData', id);
            vm.confirmSelected();
        },
        // gridComplete:function(){
        //     $("#cb_dataGrid").hide();
        // }
    });
});

var vm = new Vue({
	el:'#icloudapp',
	data:{
		showList: true,
		title: null,
		smallSku: {},
        q: {
            sysFlag:T.p('sysFlag'),
            shopId: T.p('shopId'),
            title:''
        }
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.smallSku = {};
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
                var url = vm.smallSku.id == null ? "small/smallsku/save" : "small/smallsku/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallSku),
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
                        url: baseURL + "small/smallsku/delete",
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
			$.get(baseURL + "small/smallsku/info/"+id, function(r){
                vm.smallSku = r.smallSku;
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
        confirmSelected: function () {
            if ($.trim(vm.smallSku.id) == '') {
                layer.msg("请选择商品sku",{icon: 0,time: 1000});
                return;
            }
            parent.vm.skuforgroupWinDblClick(vm.smallSku);
        }
	}
});