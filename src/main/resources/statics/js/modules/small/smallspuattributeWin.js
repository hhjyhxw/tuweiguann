$(function () {

});

var vm = new Vue({
	el:'#icloudapp',
	data:{
		showList: false,
		title: null,
		smallSpuAttribute: {
            id:(T.p('id')!=null && T.p('id')!='null')? T.p('id') : null,
            spuId:T.p('spuId') ? T.p('spuId') : null,
        }
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.smallSpuAttribute = {};
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
		    if(vm.smallSpuAttribute.id=='null'){
                vm.smallSpuAttribute.id = null;
            }
            console.log("smallSpuAttribute==="+JSON.stringify(vm.smallSpuAttribute));
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.smallSpuAttribute.id == null ? "small/smallspuattribute/save" : "small/smallspuattribute/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallSpuAttribute),
                    success: function(r){
                        console.log("result==="+JSON.stringify(r));
                        if(r.code === 0){
                             layer.msg("操作成功", {icon: 1});
                            vm.smallSpuAttribute = r.smallSpuAttribute;
                            parent.vm.getAttibutList(vm.smallSpuAttribute.spuId);
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
                        url: baseURL + "small/smallspuattribute/delete",
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
            if(id!=null && id!='null') {
                $.get(baseURL + "small/smallspuattribute/info/" + id, function (r) {
                    vm.smallSpuAttribute = r.smallSpuAttribute;
                });
            }
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
vm.getInfo(vm.smallSpuAttribute.id);