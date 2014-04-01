/**
 * @class JS.AJAX
 * AJAX常用方法封装
 * @singleton 
 * @author jinghai.xiao@gmail.com
 */
JS.ns("JS.AJAX");

JS.AJAX = (function(){
	var xhr = new JS.XMLHttpRequest();
	return {
		dataFormatError : '服务器返回的数据格式有误',
		urlError : '未指定url',
		/**
		 * 以POST方式向服务器发送请求，并得到服务返回的xhr对象。<br>
		 * <pre>如：JS.Ajax.post('/someurl.do','keyword=xxx',function(xhr){
			alert(xhr.responseText);
		   });</pre>
		 * @method 
		 * @param {String} url 网址
		 * @param {String|DOM} param 参数
		 * @param {Function} callback 回调函数 function(xhr){alert(xhr.responseText)}
		 * @param {Object} scope 作用域
		 * @param {Boolean} asyn 是否异步调用，默认true
		 */
		post : function(url,param,callback,scope,asyn){
			if(typeof url!=='string'){
				throw new Error(this.urlError);
			}
			//默认为异步请求
			var asynchronous = true;
			if(asyn===false){
				asynchronous = false;
			}
			xhr.onreadystatechange = function(){
				if(xhr.readyState==4 && asynchronous){
					JS.callBack(callback,scope,[xhr]);
				}
			};
			xhr.open('POST', url, asynchronous);
			xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF8");
			xhr.send(param || null);
			if(!asynchronous){
				JS.callBack(callback,scope,[xhr]);
			}
			
		},
		/**
		 * 以GET方式向服务器发送请求，并得到服务返回的xhr对象。<br>
		 * <pre>如：JS.Ajax.get('/someurl.do','keyword=xxx',function(xhr){
			alert(xhr.responseText);
		   });</pre>
		 * @method 
		 * @param {String} url 网址
		 * @param {String|DOM} param 参数
		 * @param {Function} callback 回调函数 function(xhr){alert(xhr.responseText)}
		 * @param {Object} scope 作用域
		 * @param {Boolean} asyn 是否异步调用，默认true
		 */
		get : function(url,param,callback,scope,asyn){
			if(typeof url!=='string'){
				throw new Error(this.urlError);
			}
			//默认为异步请求
			var asynchronous = true;
			if(asyn===false){
				asynchronous = false;
			}
			xhr.onreadystatechange = function(){
				if(xhr.readyState==4 && asynchronous){
					JS.callBack(callback,scope,[xhr]);
				}
			};
			xhr.open('GET', url, asynchronous);
			xhr.setRequestHeader("Content-Type","html/text;charset=UTF8");
			xhr.send(param || null);
			if(!asynchronous){
				JS.callBack(callback,scope,[xhr]);
			}
			
		},
		/**
		 * 以GET方式向服务器发送请求，并得到服务返回的文本信息。<br>
		 * <pre>如：JS.Ajax.getText('/someurl.do','keyword=xxx',function(text){
			alert(text);
		   });</pre>
		 * @method 
		 * @param {String} url 网址
		 * @param {String|DOM} param 参数
		 * @param {Function} callback 回调函数 function(text){alert(text)}
		 * @param {Object} asyn 作用域
		 * @param {Boolean} asyn 是否异步调用，默认true
		 */
		getText : function(url,jsonData,callback,scope,asyn){
			this.get(url,jsonData,function(xhr){
				if(scope){
					callback.call(scope,xhr.responseText);
				}else{
					callback(xhr.responseText);
				}
			},this,asyn);
		},
		/**
		 * 以GET方式向服务器发送请求，并得到服务返回的JSON对象。<br>
		 * <pre>如：JS.Ajax.getJson('/someurl.do','keyword=xxx',function(obj){
			alert(obj.someField);
		   });</pre>
		 * @method 
		 * @param {String} url 网址
		 * @param {String|DOM} param 参数
		 * @param {Function} callback 回调函数 function(obj){alert(alert(obj.someField);)}
		 * @param {Object} scope 作用域
		 * @param {Boolean} asyn 是否异步调用，默认true
		 */
		getJson : function(url,jsonData,callback,scope,asyn){
			this.get(url,jsonData,function(xhr){
				var json = null;
				try{
					json = eval("("+xhr.responseText+")");
				}catch(e){
					throw new Error(this.dataFormatError);
				}
				JS.callBack(callback,scope,[json]);
			},this,asyn);
		}
	};
})();