/**
 * @class JS.Observable
 * 事件模型
 * @author jinghai.xiao@gmail.com
 */
JS.ns("JS.Observable");
JS.Observable = function(o){
	JS.apply(this,o || JS.toArray(arguments)[0]);
	if(this.events){
		this.addEvents(this.events);
	}
    if(this.listeners){
        this.on(this.listeners);
        delete this.listeners;
    }
};
JS.Observable.prototype = {
	/**
	 * 添加侦听
	 * @method 
	 * @param {String | Map<String|Function>} channelName 事件名称或多个事件名称和侦听函数的键值对
	 * @param {Function} fn 侦听函数
	 * @param {Object} scope 侦听函数作用域
	 */
	on : function(eventName, fn, scope, o){
		if(JS.isString(eventName)){
			this.addListener(eventName, fn, scope, o);
		}else if(JS.isObject(eventName)){
			this.addListeners(eventName,scope, o);
		}
	},
	/**
	 * 触发事件
	 * @method 
	 * @param {String} eventName 事件名称
	 * @param {[AnyType0~n...]} eventParam 事件参数，可以是0到N个
	 * @return {Boolean}
	 */
	fireEvent : function(){
		var arg = JS.toArray(arguments),
		    eventName = arg[0].toLowerCase(),
			e = this.events[eventName];
		if(e && !JS.isBoolean(e)){
			return e.fire.apply(e,arg.slice(1));
		}
	},
	/**
	 * 注册事件类型
	 * @method 
	 * @param {String} eventName 事件名称
	 */
	addEvent : function(eventName){
		if(!JS.isObject(this.events)){
			this.events = {};
		}
		if(this.events[eventName]){
			return;
		}
		if(JS.isString(eventName)){
			this.events[eventName.toLowerCase()] = true;
		}else if(eventName instanceof JS.Event){
			this.events[eventName.name.toLowerCase()] = eventName;
		}
	},
	/**
	 * 批量注册事件类型
	 * @method 
	 * @param {Array<String>} eventNames 事件名称
	 */
	addEvents : function(arr){
		if(JS.isArray(arr)){
			for(var i = 0,len = arr.length; i < len; i++){
				this.addEvent(arr[i]);
			}
		}
	},
	/**
	 * 注册事件侦听
	 * @method 
	 * @param {String} eventName 事件名称
	 * @param {Function} fn 侦听函数
	 * @param {Object} scope 侦听函数作用域
	 */
	addListener : function(eventName, fn, scope, o){//o配置项尚未实现
		eventName = eventName.toLowerCase();
		this.addEvent(eventName);
		var e = this.events[eventName];
		if(e){
			if(JS.isBoolean(e)){
				e = this.events[eventName] = new JS.Event(eventName,this);
			}
			e.addListener(fn, scope , o);
		}
	},
	/**
	 * 批量注册事件侦听
	 * @method 
	 * @param {Map<String,Function>} eventMap 事件名称与侦听函数的键值对
	 * @param {Function} fn 侦听函数
	 * @param {Object} scope 侦听函数作用域
	 */
	addListeners : function(obj,scope, o){
		if(JS.isObject(obj)){
			for(var p in obj){
				this.addListener(p,obj[p],scope, o);
			}
		}
	},
	/**
	 * 移除事件侦听
	 * @method 
	 * @param {String} eventName 事件名称
	 * @param {Function} fn 侦听函数
	 * @param {Object} scope 侦听函数作用域
	 */
	removeListener : function(eventName, fn, scope){
		eventName = eventName.toLowerCase();
		var e = this.events[eventName];
		if(e && !JS.isBoolean(e)){
			e.removeListener(fn, scope);
		}
	},
	/**
	 * 移除所有事件侦听
	 * @method 
	 */
	clearListeners : function(){
		var events = this.events,
			e;
		for(var p in events){
			e = events[p];
			if(!JS.isBoolean(e)){
				e.clearListeners();
			}
		}
	},
	/**
	 * 移除所有事件类型及事件侦听
	 * @method 
	 */
	clearEvents : function(){
		var events = this.events;
		this.clearListeners();
		for(var p in events){
			this.removeEvent(p);
		}
	},
	/**
	 * 移除事件类型
	 * @method 
	 * @param {String} eventName 事件类型名称
	 */
	removeEvent : function(eventName){
		var events = this.events,
			e;
		if(events[eventName]){
			e = events[eventName];
			if(!JS.isBoolean(e)){
				e.clearListeners();
			}
			delete events[eventName];
		}
		
	},
	/**
	 * 批量移除事件类型
	 * @method 
	 * @param {Array<String>} 事件类型名称列表
	 */
	removeEvents : function(eventName){
		if(JS.isString(eventName)){
			this.removeEvent(eventName);
		}else if(JS.isArray(eventName) && eventName.length > 0){
			for(var i=0, len=eventName.length; i<len ;i++){
				this.removeEvent(eventName[i]);
			}
		}
	},
	/**
	 * 检测是否具有指定的事件类型
	 * @method 
	 * @param {String} 事件类型名称
	 */
	hasEvent : function(eventName){
		return this.events[eventName.toLowerCase()]?true:false;
	},
	/**
	 * 检测是否具有指定的事件侦听
	 * @method 
	 * @param {String} 事件类型名称
	 * @param {Function} fn 侦听函数
	 * @param {Object} scope 侦听函数作用域
	 */
	hasListener : function(eventName,fn,scope){
		var events = this.events,
			e = events[eventName];
		if(!JS.isBoolean(e)){
			return e.hasListener(fn,scope);
		}
		return false;
	},
	suspendEvents : function(){
		//TODO:
	},
	resumeEvents : function(){
		//TODO:
	}
};
/**
 * 事件源，代表一类事件，负责管理事件侦听
 * @class JS.Event
 * @author jinghai.xiao@gmail.com
 */
JS.Event = function(name,caller){
	this.name = name.toLowerCase();
	this.caller = caller;
	this.listeners = [];
};
JS.Event.prototype = {
	/**
	 * @method
	 * @return {Boolean}
	 */
	fire : function(){
		var 
			listeners = this.listeners,
			//len = listeners.length,
			i = listeners.length-1;
		for(; i > -1; i--){//TODO:fix 倒序
			if(listeners[i].execute.apply(listeners[i],arguments) === false){
				return false;
			}
		}
		return true;
	},
	/**
	 * @method
	 * @param {Function} fn
	 * @param {Object} scope
	 */
	addListener : function(fn, scope,o){
        scope = scope || this.caller;
        if(this.hasListener(fn, scope) == -1){
            this.listeners.push(new JS.Listener(fn, scope ,o));
        }
    },
    /**
     * 
     * @method
     * @param {Function} fn
     * @param {Object} scope
     */
	removeListener : function(fn, scope){
        var index = this.hasListener(fn, scope);
		if(index!=-1){
			this.listeners.splice(index, 1);
		}
    },
    /**
     * 
     * @method
     * @param {Function} fn
     * @param {Object} scope
     */
	hasListener : function(fn, scope){
		var i = 0,
			listeners = this.listeners,
			len = listeners.length;
		for(; i<len; i++){
			if(listeners[i].equal(fn, scope)){
				return i;
			}
		}
		return -1;
	},
	 /**
     * 
     * @method
     */
	clearListeners : function(){
		var i = 0,
			listeners = this.listeners,
			len = listeners.length;
		for(; i<len; i++){
			listeners[i].clear();
		}
		this.listeners.splice(0);
	}

};
/**
 * 事件侦听器，包装并统一侦听的调用方式
 * @class JS.Listener
 * @author jinghai.xiao@gmail.com
 */
JS.Listener = function(fn, scope,o){
	this.handler = fn;
	this.scope = scope;
	this.o = o;//配置项，delay,buffer,once,
};
JS.Listener.prototype = {
	/**
     * 
     * @method
     * @return {Boolean}
     */
	execute : function(){
		return JS.callBack(this.handler,this.scope,arguments);
	},
	/**
     * 
     * @method
     * @return {Boolean}
     */
	equal : function(fn, scope){
		return this.handler === fn /*&& this.scope === scope*/ ? true : false;
	},
	/**
     * 
     * @method
     */
	clear : function(){
		delete this.handler;
		delete this.scope ;
		delete this.o ;
	}
};