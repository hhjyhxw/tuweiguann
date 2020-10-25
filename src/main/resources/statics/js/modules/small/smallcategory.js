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

/*
$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'small/smallcategory/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '分类名称', name: 'title', index: 'title', width: 80 }, 			
			{ label: '父类id', name: 'parentId', index: 'parent_id', width: 80 }, 			
			{ label: '分类图标地址', name: 'picUrl', index: 'pic_url', width: 80 }, 			
			{ label: '分类级别', name: 'level', index: 'level', width: 80 }, 			
			{ label: '状态', name: 'status', index: 'status', width: 80 }, 			
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
        }
    });
});
*/

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
                vm.smallCategory.picUrl = r.url;
                console.log("vm.smallCategory.img=="+ vm.smallCategory.picUrl);
                //vm.reload();
            }else{
                alert(r.msg);
            }
        }
    });
});
//列表树结构
var treetable_url = baseURL + "small/smallcategory/list"

var vm = new Vue({
	el:'#icloudapp',
	data:{
		showList: true,
		title: null,
		smallCategory: {
		    id:null,
            name: null,
            parentName:null,
            parentId:0,
            sortNum:999,
            picUrl:'',
            deptId:null,
        },
        q:{
            title:'',
            parentName:'',
            startTime:'',
            endTime:'',
        }

	},
    created: function(){

    },
	methods: {
        getCategory: function(){
            //加载分类树
            $.get(baseURL + "small/smallcategory/select", function(r){
                // console.info("r==="+JSON.stringify(r))
                ztree = $.fn.zTree.init($("#deptTree"), setting, r.categoryList);
                // console.log("ztree====="+JSON.stringify(ztree))
                var node = ztree.getNodeByParam("id", vm.smallCategory.parentId);
                console.log("加载node====="+JSON.stringify(node))
                if(node!=null){
                    ztree.selectNode(node);
                    vm.smallCategory.parentName = node.name;
                }
            })
        },
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.smallCategory = {
			    id:null,
                name: null,
                parentName:null,
                parentId:0,
                sortNum:999,
            };

		},
		update: function (id) {
//            var id = getCategoryId();
            if(id == null){
                return ;
            }
            vm.smallCategory ={};
            $.get(baseURL + "small/smallcategory/info/"+id, function(r){
                vm.showList = false;
                vm.title = "修改";
                vm.smallCategory = r.smallCategory;
                vm.getCategory();
            });
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.smallCategory.id == null ? "small/smallcategory/save" : "small/smallcategory/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.smallCategory),
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
                        url: baseURL + "small/smallcategory/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
                                  vm.reload();
                                //$("#jqGrid").trigger("reloadGrid");
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
			$.get(baseURL + "small/smallcategory/info/"+id, function(r){
                vm.smallCategory = r.smallCategory;
            });
		},
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择上级分类",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级部门
                    console.log("node====="+JSON.stringify(node))
                    vm.smallCategory.parentId = node[0].id;
                    vm.smallCategory.parentName = node[0].name;

                    layer.close(index);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            vm.getData();
            //Dept.table.refresh();
        },

        getData:function(){
             var treetable_url = baseURL + "small/smallcategory/list?title="+vm.q.title+"&parentName="+vm.q.parentName+"&startTime="+vm.q.startTime+"&endTime="+vm.q.endTime;
             $.get(baseURL + 'small/smallcategory/info', function(r){
                    // console.info("r==="+JSON.stringify(r))
                    var colunms = Dept.initColumn();
                    var table = new TreeTable(Dept.id, treetable_url, colunms);
                    //console.info("table==="+JSON.stringify(table))
                    table.setRootCodeValue(r.id);
                    table.setExpandColumn(2);
                    table.setIdField("id");
                    table.setCodeField("id");
                    table.setParentCodeField("parentId");
                    table.setExpandAll(false);
                    table.init();
                    Dept.table = table;
                });
        }

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
       /* {title: '分类名称', field: 'title',visible: false, align: 'center', valign: 'middle', sortable: true, width: '180px'},*/
        {title: '分类名称', field: 'name', align: 'center', valign: 'middle', sortable: true, width: '80px'},
        { title: '分类图标', field: 'picUrl', width: '60px', formatter: function(item, index){
           return '<img style="height: 3rem;width: 3rem;" src="'+item.picUrl+'"/>';
         }},
        {title: '上级分类', field: 'parentName', align: 'center', valign: 'middle', false: true, width: '50px'},
        { title: '状态', field: 'status', width: '60px', formatter: function(item, index){
                return item.status === 0 ?
                    '<span class="label label-danger">停用</span>' :
                    '<span class="label label-success">启用</span>';
            }},
        { title: '创建时间', field: 'createTime', align: 'center',  valign: 'middle', false: true, width: '80px', formatter: function(item, index){
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
        {title: '排序号', field: 'sortNum', align: 'center', valign: 'middle', sortable: false, width: '50px'},
        {title:'操作', field:'操作', width: '80px', sortable:false, title:"操作", align:'center', formatter: function(item, index){
                        var actions = [];
                            actions.push('<a class="btn btn-primary" onclick="vm.update('+item.id+')" style="padding: 3px 8px;"><i class="fa fa-pencil-square-o"></i>&nbsp;修改</a>&nbsp;');
                            actions.push('<a class="btn btn-primary" onclick="vm.del('+item.id+')" style="padding: 3px 8px;"><i class="fa fa-trash-o"></i>&nbsp;删除</a>&nbsp;');
                        return actions.join('');
                    }}
        ]
    return columns;
};


function getCategoryId () {
    var selected = $('#deptTable').bootstrapTreeTable('getSelections');
    if (selected.length == 0) {
        alert("请选择一条记录");
        return null;
    } else {
        return selected[0].id;
    }
}



/*$(function () {
    $.get(baseURL + 'small/smallcategory/info', function(r){
        // console.info("r==="+JSON.stringify(r))
        var colunms = Dept.initColumn();
        var table = new TreeTable(Dept.id, treetable_url, colunms);
        //console.info("table==="+JSON.stringify(table))
        table.setRootCodeValue(r.id);
        table.setExpandColumn(2);
        table.setIdField("id");
        table.setCodeField("id");
        table.setParentCodeField("parentId");
        table.setExpandAll(false);
        table.init();
        Dept.table = table;
    });
});*/
vm.getData();
vm.getCategory();

