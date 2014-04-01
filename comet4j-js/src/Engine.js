/**
 * 消息引擎,
 * 负责将服务器推送的通道数据，以事件的方式触发，事件名称与通道名称相同。
 * @class JS.Engine
 * @singleton 
 * @extends JS.Observable
 * @author jinghai.xiao@gmail.com
 */
JS.ns("JS.Engine");
JS.Engine = (function(){
	var Engine = JS.extend(JS.Observable,{
		lStore : [],//用于存放没启动状态下用户增加的侦听
		/** 
		 * 引擎是否处于工作状态
		 * @property 
		 * @type Boolean
		 */ 
		running : false,
		/** 
		 * 引擎所使用的连接器
		 * @property 
		 * @type JS.Connector
		 */ 
		connector : null,
		constructor:function(){
			this.addEvents([
				/**
				 * @event start 开始事件
				 * @param {String} cId 连接ID
				 * @param {Array<String>} channels 通道列表
				 * @param {JS.Engine} engine 事件引擎
				 */   
				'start',
				/*** 
				 * @event stop 停止事件
				 * @param {String} cause 停止原因
				 * @param {String} cId 连接ID
				 * @param {String} url 连接地址
				 * @param {JS.Engine} engine 事件引擎
				 */
				'stop'
			]);
			Engine.superclass.constructor.apply(this,arguments);
			this.connector = new JS.Connector();
			this.initEvent();
		},
		/**
		 * 注册通道侦听
		 * @method on
		 * @param {String | Map<String,Function>} channelName 通道名称或多个通道名称和侦听函数的键值对
		 * @param {Function} fn 侦听函数
		 * @param {Object} scope 侦听函数作用域
		 */
		//重载addListener函数
		addListener : function(eventName, fn, scope, o){
			if(this.running){
				Engine.superclass.addListener.apply(this,arguments);
			}else{
				this.lStore.push({
					eventName : eventName,
					fn : fn,
					scope : scope,
					o : o
				});
			}
		},
		//private 
		initEvent : function(){
			var self = this;
			this.connector.on({
				connect : function(cId, aml, conn){
					//self.running = true;
					self.addEvents(aml);
					for(var i=0,len=self.lStore.length; i<len; i++){
						var e = self.lStore[i];
						self.addListener(e.eventName,e.fn,e.scope);
					}
					self.fireEvent('start', cId, aml, self);
				},
				stop : function(cause, cId, url, conn){
					self.running = false;
					self.fireEvent('stop',cause, cId,url, self);
					self.clearListeners();
				},
				message : function(amk, data, time){
					self.fireEvent(amk, data, time, self);
				}
			});
		},
		/**
		 * 开始连接
		 * @method 
		 * @param {String} url 连接地址
		 * @param {String} param 连接参数
		 */
		start : function(url,param){
			this.running = true;
			/*
			if(this.running){
				return;
			}*/
			for(var i=0,len=this.lStore.length; i<len; i++){
				var e = this.lStore[i];
				this.addListener(e.eventName,e.fn,e.scope);
			}
			this.connector.start(url,param);
		},
		/**
		 * 停止连接
		 * @method 
		 * @param {String} cause 停止原因
		 */
		stop : function(cause){
			if(!this.running){
				return;
			}
			this.connector.stop(cause);
		},
		/**
		 * 获得连接器实例
		 * @method 
		 * @return {JS.Connector} connector 连接器实例
		 */
		getConnector : function(){
			return this.connector;
		},
		/**
		 * 获得连接ID
		 * @method 
		 * @return {String} 连接ID
		 */
		getId : function(){
			return this.connector.cId;
		}
		
	});
	return new Engine();
}());