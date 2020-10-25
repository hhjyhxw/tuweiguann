$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'wx/wxuser/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: 'openid', name: 'openid', index: 'openid', width: 80 }, 			
			{ label: '昵称', name: 'nickname', index: 'nickname', width: 80 }, 			
			 { label: '性别', name: 'sex', width: 60, formatter: function(value, options, row){
                                return value === 0 ?
                                    '<span class="label label-danger">未知</span>' :
                                    (value===1?'<span class="label label-success">男性</span>':
                                    (value===2?'<span class="label label-success">女性</span>':'未知'
                                    ));
                            }},
			{ label: '省', name: 'province', index: 'province', width: 80 },
			{ label: '市', name: 'city', index: 'city', width: 80 }, 			
			{ label: '县', name: 'country', index: 'country', width: 80 }, 			
			 { label: '头像', name: 'headimgurl', width: 60, formatter: function(value, options, row){
                        return '<img style="height: 3rem;width: 3rem;" src="'+value+'"/>';
                    }},
			{ label: 'unionid', name: 'unionid', index: 'unionid', width: 80 },
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80 }, 			
			{ label: '更新时间', name: 'modifyTime', index: 'modify_time', width: 80 }, 			
			{ label: '联系电话', name: 'phone', index: 'phone', width: 80 }			
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
                   vm.wxUser = $(this).jqGrid("getRowData",id);
                   // vm.smallSku = $(this).dataGrid('getRowData', id);
               },
               /**
                *  双击选择
                */
               ondblClickRow: function (id) {
                   vm.wxUser = $(this).jqGrid("getRowData",id);
                   // vm.smallSku = $(this).dataGrid('getRowData', id);
                   vm.confirmSelected();
               },
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		wxUser: {},
		q:{
		    nickname:'',
		    xcxopenid:''
		}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.wxUser = {};
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
                var url = vm.wxUser.id == null ? "wx/wxuser/save" : "wx/wxuser/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.wxUser),
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
                        url: baseURL + "wx/wxuser/delete",
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
			$.get(baseURL + "wx/wxuser/info/"+id, function(r){
                vm.wxUser = r.wxUser;
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
                if ($.trim(vm.wxUser.id) == '') {
                    layer.msg("请选用户",{icon: 0,time: 1000});
                    return;
                }
                parent.vm.wxuserforgroupWinDblClick(vm.wxUser);
            }
	}
});