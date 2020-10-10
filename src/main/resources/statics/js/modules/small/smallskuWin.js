$(function () {
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
				// vm.optionSucai.localUrls = baseURL + r.url;
				// vm.sucai.list[vm.selectIndex].localUrls = baseURL + r.url;
				vm.smallSku.img = r.url;
				console.log("vm.smallSku.img=="+ vm.smallSku.img);
				//vm.reload();
			}else{
				alert(r.msg);
			}
		}
	});
});


var vm = new Vue({
	el:'#icloudapp',
	data:{
		showList: false,
		title: null,
		smallSku: {
			id:T.p('id') ? T.p('id') : null,
			spuId:T.p('spuId') ? T.p('spuId') : null,
			addStock:0,
		},
		addStock:0,
		caculatRemainStock:0,
	},
	watch: {
		addStock(newV,oldV) {
			// do something
			console.log(newV,oldV)
			var remainStock = vm.caculatRemainStock;//获取原来剩余库存，用于实时计算
			var pnewV = parseInt(newV);
			if(pnewV<0){
				vm.smallSku.remainStock =  remainStock+pnewV>0?remainStock+pnewV:0;
			}else{
				vm.smallSku.remainStock =  remainStock+pnewV;
			}
			vm.smallSku.addStock = pnewV;
		}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.smallSku = {

			};
			vm.smallSku.addStock = null;
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
			console.log("smallSku======"+JSON.stringify(vm.smallSku))
			//参数校验，
			if(vm.smallSku.title==null || vm.smallSku.title==''){
				layer.msg("商品名称不能为空", {icon: 2});
				return;
			}
			if(vm.smallSku.spuId==null || vm.smallSku.spuId==''){
				layer.msg("没关联上商品，请联想管理员", {icon: 2});
				return;
			}
			if(!priceCheck(vm.smallSku.originalPrice)){
				layer.msg("商品原价不能为空,且为数字,最多保留两位小数", {icon: 2});
			}
			if(!priceCheck(vm.smallSku.originalPrice)){
				layer.msg("商品现价不能为空,且为数字,最多保留两位小数", {icon: 2});
				return;
			}
			if(vm.smallSku.id=='null'){
				vm.smallSku.id = null;
			}
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
							vm.smallSku = r.smallSku;
							vm.caculatRemainStock = vm.smallSku.remainStock;//设置剩余库存:只用于临时计算
							// window.parent.location.reload();
							// var index = parent.layer.getFrameIndex(window.name);
							// console.log("window.name====="+window.name);
							// console.log("index====="+index)
							// parent.layer.close(index);
							parent.vm.getSkuList(vm.smallSku.spuId);
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
			if(id!=null && id!='null') {
				$.get(baseURL + "small/smallsku/info/"+id, function(r){
					vm.smallSku = r.smallSku;
					vm.caculatRemainStock = vm.smallSku.remainStock;//设置剩余库存:只用于临时计算
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

vm.getInfo(vm.smallSku.id);