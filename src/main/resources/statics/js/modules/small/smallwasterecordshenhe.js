$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallwasterecord/shenhelist',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '店铺', name: 'shop.shopName', index: 'shop_id', width: 80 },
            { label: '金额', name: 'amount', index: 'amount', width: 80 },
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80 }, 			
            { label: '审核', name: 'approveFlag', width: 60, formatter: function(value, options, row){
                    return value === '0' ?
                        '<span class="label label-danger">未审核</span>' :
                        (value==='1'?'<span class="label label-success">审核通过</span>':'不通过');
                }},
			{ label: '审核描述', name: 'msg', index: 'msg', width: 80 },
			{ label: '审核时间', name: 'approveTime', index: 'approve_time', width: 80 }, 			
			{ label: '审核人', name: 'approveBy', index: 'approve_by', width: 80 },
			{ label: '申请人', name: 'createBy', index: 'create_by', width: 80 },
            {header:'操作', name:'操作', width:90, sortable:false, title:false, align:'center', formatter: function(val, obj, row, act){
                    var actions = [];
                    if(row.approveFlag!='1'){
                        actions.push('<a title="审核通过" onclick="vm.updatepass('+row.id+')"><i class="fa fa-pencil">审核通过</i></a>&nbsp;');
                        actions.push('<a title="审核不通过" onclick="vm.updateUnpass('+row.id+')"><i class="fa fa-pencil">审核不通过</i></a>&nbsp;');
                    }

                    /*  actions.push('<a title="提现记录" onclick="vm.update('+row.id+',0)"><i class="fa fa-trash-o">提现记录</i></a>&nbsp;');*/
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
		smallWasteRecord: {
		    id:null,
            approveFlag:'2',
            msg:null
        }
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.smallWasteRecord = {};
		},
        updatepass : function (id) {
			// var id = getSelectedRow();
			if(id == null){
				return ;
			}
			var smallWasteRecord = {
			    id:id,
                approveFlag:'1'
            }
            var lock = false;
            layer.confirm('确定审核通过？', {
                btn: ['确定','取消'] //按钮
            }, function(){
                if(!lock) {
                    lock = true;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "small/smallwasterecord/shenhe",
                        contentType: "application/json",
                        data: JSON.stringify(smallWasteRecord),
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
            vm.getInfo(id)
		},
        updateUnpass : function (id) {
            if(id == null){
                return ;
            }
            vm.showList = false;
            vm.title = "审核不通过";
            vm.smallWasteRecord.id = id;
            // vm.getInfo(id)
        },
		saveOrUpdate: function (event) {
		    if(vm.smallWasteRecord.id==null){
		        return;
            }
            if(vm.smallWasteRecord.msg==null || vm.smallWasteRecord.msg==''){
                layer.msg("不通过原因不能为空", {icon: 2});
                return;
            }
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = "small/smallwasterecord/shenhe";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallWasteRecord),
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
                        url: baseURL + "small/smallwasterecord/delete",
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
			$.get(baseURL + "small/smallwasterecord/shenheinfo/"+id, function(r){
                vm.smallWasteRecord = r.smallWasteRecord;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});