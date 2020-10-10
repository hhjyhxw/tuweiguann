//数据字典组件
//下拉框 页面直接使用例子<dict-select v-model="xxx"  accesskey="字典key"></dict-select>
//如：<dict-select v-model="mystatus" accesskey="status" query="true"></dict-select>
var dictSelect = Vue.extend({
	name: 'dict-select',
	props:['value','accesskey','query'],
	template:[
	      '<select class="form-control" :accesskey="accesskey">',
			'<option disabled value="" v-if="!query">请选择</option>',
			'<option value="" v-if="query" selected>全部</option>',
	      	'<option :value="dict.value" v-for="dict in dictList">{{dict.label}}</option>',
	      '</select>'
	].join(''),
	data: function(){
         return {
             dictList: getDickByKey(this.accesskey)
         }
	},
	mounted: function () {
        var vm = this;
        $(this.$el)
            .val(this.value)
            .trigger('change')
            // emit event on change.
            .on('change', function () {
                vm.$emit('input', this.value)
            })
    },
	watch: {
        value: function (value) {
            // update value
            $(this.$el)
                .val(value)
                .trigger('change')
        },
        dictList: function (dictList) {
            // update dictList
            //$(this.$el).empty().select2({ data: dictList })
            var $select = $(this.$el);
            $select.find("option:first").nextAll().remove();
            $.each(options,function () {
                $select.append("<option value='"+ this.value +"'>"+ this.label +"</option>");
            });
            $(this.$el)
                .val(this.value)
                .trigger('change')
        }
    },
    destroyed: function () {
        $(this.$el).off().select2('destroy')
    }
});
//单选按钮  页面直接使用例子<dict-radio v-model="xxx" name="xxx"  accesskey="字典key"></dict-radio>
//如：<dict-radio v-model="mystatus" name="status" accesskey="status"></dict-radio>
var dictRadio = Vue.extend({
	name: 'dict-radio',
	props:['value','accesskey','name'],
	template:['<ul class="list-inline" style="line-height:28px;"><li v-for="dict in dictList">',
	         '<input type="radio" class="form-control" :name="name" :accesskey="accesskey" :value="dict.value" />{{dict.label}}',
	          '</li></ul>'].join(''),
	data:function(){
        return {
            dictList : getDickByKey(this.accesskey)
        }
	} ,
    mounted: function () {
        var vm = this;
        $(this.$el).find("input:radio")
            .on('ifChecked', function(event){
                vm.$emit('input', this.value)
            })
			.filter('[value='+ this.value +']')
			.iCheck('check');
    },
    watch: {
        value: function (value) {
            $(this.$el)
                .find("input:radio[value='"+value+"']")
                .iCheck('check');
        }
    },
    destroyed: function () {
        $(this.$el).off().iCheck('destroy')
    }
});

//复选框  页面直接使用例子<dict-checkbox name="xxx"  accesskey="字典key"></dict-checkbox>
//如：<dict-checkbox name="mystatus"  accesskey="status"></dict-checkbox>
var dictCheckbox = Vue.extend({
	name: 'dict-checkbox',
	props:['name','accesskey'],
	template:['<ul class="list-inline" style="line-height:28px;"><li v-for="dict in dictList">',
	         '<input type="checkbox" class="form-control" :name="name" :accesskey="accesskey" :value="dict.value" ></input>{{dict.label}}',
	          '</li></ul>'].join(''),
	data:function(){
		//alert(this.accesskey);
		var key = this.accesskey;
		var dictList =  getDickByKey(key);
		return {dictList:dictList};
	}
});

//注册菜单组件
Vue.component('dictSelect',dictSelect);
Vue.component('dictRadio',dictRadio);
Vue.component('dictCheckbox',dictCheckbox);

//根据key获取对应字典列表(json)
function getDickByKey(key){

	var dictList =  window.localStorage.getItem(key); //从缓存获取
	return JSON.parse(dictList);
}
//根据key和value获取对应的字典标签名
function getLabelByKeyValue(key,value){
	var label = window.localStorage.getItem(key+value);
	console.log("从缓存取字典值：%s",label);

	return label == null ? "" : label;
}

// Vue 整合 select2插件 下拉框
var select2 = Vue.extend({
    name: 'select2',
    props: ['options', 'value','delfirst'],
    template: '<select><slot></slot></select>',
    mounted: function () {
        var vm = this;
        $(this.$el)
            .select2({data: this.options})
            .val(this.value)
            .trigger('change')
            // emit event on change.
            .on('change', function () {
                vm.$emit('input', this.value)
            })
    },
    watch: {
        value: function (value) {
            // update value
            $(this.$el)
                .val(value)
                .trigger('change')
        },
        options: function (options) {
            // update options
            var $select = $(this.$el);
            if(this.delfirst) {
                $select.find("option").remove();
            }else{
                $select.find("option:first").nextAll().remove();
            }
            $.each(options,function () {
                $select.append("<option value='"+ this.id +"'>"+ this.text +"</option>");
            });
            $(this.$el)
                .val(this.value)
                .trigger('change')
        }
    },
    destroyed: function () {
        $(this.$el).off().select2('destroy')
    }
});
Vue.component('select2',select2);
