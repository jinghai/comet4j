/**
 * @class JS.Connector
 * @extends JS.Observable
 * 连接器,负责建立并维持连接，接收服务器端推送的数据。
 * @author jinghai.xiao@gmail.com
 */
JS.ns("JS.Connector");
JS.Connector = JS.extend(JS.Observable,{
   /** 
    * 版本
    * @property 
    * @type String
    */ 
	version : JS.version,
	SYSCHANNEL:'c4j', //协议常量
	/** 
	 * 长轮询工作模式常量
	 * @property 
	 * @type String
	 */ 
	LLOOPSTYLE : 'lpool',//协议常量
	/** 
	 * 长连接工作模式常量
	 * @property 
	 * @type String
	 */ 
	STREAMSTYLE : 'stream',//协议常量
	CMDTAG : 'cmd',
	/**
	 * @cfg {String} retryCount 
	 * 重试次数，连续重试指定次数仍无法正常工作则stop.
	 */
	retryCount : 3,
	/**
	 * @cfg {String} retryCount 
	 * 重试延迟，重试时间隔指定时间执行
	 */
	retryDelay : 1000,
	currRetry : 0,
	/**
	 * @cfg {String} url 
	 * 连接地址，若使用start方法中带有url，则此配置项将被覆盖。
	 */
	url : '',
	/**
	 * @cfg {Object} param 
	 * 连接参数，若使用start方法中带有param，则此配置项将被覆盖。
	 */
	param : '',
	/**
	 * @cfg {Object} revivalDelay
	 * 复活请求延时毫秒数,默认为100
	 */
	revivalDelay : 100,
    /** 
	 * 连接ID，连接后有效
	 * @property 
	 * @type String
	 */ 
	cId : '',
	/** 
	 * 通道列表，连接后有效
	 * @property 
	 * @type Array
	 */ 
	channels : [],
	/** 
	 * 连接工作模式，连接后有效。值为：LLOOPSTYLE或STREAMSTYLE
	 * @property 
	 * @type String
	 */ 
	workStyle : '',
	emptyUrlError : 'URL为空',
	runningError : '连接正在运行',
	dataFormatError : '数据格式有误',
	/** 
	 * 连接器是否处于运行状态
	 * @property 
	 * @type String
	 */ 
	running : false,
	_xhr : null,
	lastReceiveMessage : '',
	constructor:function(){
		JS.Connector.superclass.constructor.apply(this,arguments);
		this.addEvents([
			/**
			 * @event beforeConnect 即将连接
			 * @param {String} url 连接地址
			 * @param {Connector} conn 连接器
			 */
			'beforeConnect',
			/**
			 * @event connect 已连接
			 * @param {String} cId 连接ID
			 * @param {Array} channels 通道列表
			 * @param {String} workStyle 工作模式
			 * @param {Number} timeout 连接超时时间
			 * @param {Connector} conn 连接器
			 */
			'connect',
			/**
			 * @event beforeStop 即将停止
			 * @param {String} cause 停止原因
			 * @param {String} cId 连接ID
			 * @param {String} url 连接地址
			 * @param {Connector} conn 连接器
			 */
			'beforeStop',
			/**
			 * @event stop 已停止
			 * @param {String} cause 停止原因
			 * @param {String} cId 连接ID
			 * @param {String} url 连接地址
			 * @param {Connector} conn 连接器
			 */
			'stop',
			/**
			 * @event message 接收到推送数据
			 * @param {String} channel 通道标识
			 * @param {Object} data 数据对象
			 * @param {Number} time 发送时间，距1970-01-01 00:00:00毫秒数
			 * @param {Connector} conn 连接器
			 */
			'message',
			/**
			 * @event revival 请求复活，用于保持连接。
			 * @param {String} url 连接地址
			 * @param {String} cId 连接ID
			 * @param {Connector} conn 连接器
			 */
			'revival'
		]);
		if(JS.isIE7){
			this._xhr = new JS.XMLHttpRequest({
				specialXHR : 'Msxml2.XMLHTTP.6.0'
			});
		}else{
			this._xhr = new JS.XMLHttpRequest();
		}
		//this._xhr.addListener('readyStateChange',this.onReadyStateChange,this);
		
		this._xhr.addListener('progress',this.doOnProgress,this);
		this._xhr.addListener('load',this.doOnLoad,this);
		this._xhr.addListener('error',this.doOnError,this);
		this._xhr.addListener('timeout',this.doOnTimeout,this);
		
		this.addListener('beforeStop',this.doDrop,this);
		JS.on(window,'beforeunload',this.doDrop,this);

	},
	//private
	doDrop : function(url,cId,conn,xhr){
		if(!this.running || !this.cId){
			return;
		}
		try {
			var xhr = new JS.XMLHttpRequest();
			var param = this.perfectParam(this.param);
			var url = this.url + '?'+this.CMDTAG+'=drop&cid=' + this.cId + param;
			xhr.open('GET', url, true);
			xhr.send(null);
			xhr = null;
		}catch(e){};
	},
	//private distributed 派发服务器消息
	dispatchServerEvent : function(msg){
		this.fireEvent('message', msg.channel, msg.data, msg.time, this);
	},
	//private 长连接信息转换
	translateStreamData : function(responseText){
		var str = responseText;
		if(this.lastReceiveMessage && str){//剥离出接收到的数据
			str = str.split(this.lastReceiveMessage);
			str = str.length ? str[str.length-1] : "";
		}
		this.lastReceiveMessage = responseText;
		return str;
	},
	//private 消息解码
	decodeMessage : function(msg){
		var json = null;
		if(JS.isString(msg) && msg!=""){
			//解析数据格式
			if(msg.charAt(0)=="<" ){
				msg = msg.substring(1,msg.length);
			}
			if(msg.charAt(msg.length-1)==">"){
				msg = msg.substring(0,msg.length-1);
			}
			msg = decodeURIComponent(msg);
			try{
				json = eval("("+msg+")");
			}catch(e){
				this.stop('JSON转换异常');
			}			
		}
		return json;
	},
	//private
	doOnProgress : function(xhr){
		if(this.workStyle === this.STREAMSTYLE){				
			var str = this.translateStreamData(xhr.responseText);
			var msglist = str.split(">");
			if(msglist.length > 0){
				for(var i=0, len=msglist.length; i<len; i++){
					if(!msglist[i] && i!=0){
						return;
					}
					var json = this.decodeMessage(msglist[i]);
					if(json){
						this.currRetry = 0;
						this.dispatchServerEvent(json);
						if(json.channel == this.SYSCHANNEL){
							this.revivalConnect();
						}
					}else{//非正常情况，状态为3,200并且还没有收到任何数据
						this.currRetry++;
						if(this.currRetry > this.retryCount){
							this.stop('服务器异常');
						}else{
							this.retryRevivalConnect();
						}
					}
				}
			}
		}
	},
	//private
	doOnLoad : function(xhr){
		if(this.workStyle === this.LLOOPSTYLE){
			var json = this.decodeMessage(xhr.responseText);
			if(json){
				this.currRetry = 0;
				this.dispatchServerEvent(json);
				this.revivalConnect();
			}else{//非正常情况，状态为4,200并且还没有收到任何数据
				this.currRetry++;
				if(this.currRetry > this.retryCount){
					this.stop('服务器异常');
				}else{
					this.retryRevivalConnect();
				}
			}
		}
	},
	//private
	doOnError : function(xhr){
		this.currRetry++;
		if(this.currRetry > this.retryCount){
			this.stop('服务器异常');
		}else{
			this.retryRevivalConnect();
		}
		
	},
	//private
	doOnTimeout : function(xhr){
		this.currRetry++;
		if(this.currRetry > this.retryCount){
			this.stop('请求超时');
		}else{
			this.retryRevivalConnect();
		}
	},
	//private
	startConnect : function(url){
		if(this.running){
			var connXhr = new JS.XMLHttpRequest();
			connXhr.addListener('error',function(xhr){
				this.stop("连接时发生错误");
			},this);
			connXhr.addListener('timeout',function(xhr){
				this.stop("连接超时");
			},this);
			connXhr.addListener('load',function(xhr){
				var msg = this.decodeMessage(xhr.responseText);
				if(!msg){
					this.stop('连接失败');
					return;
				}
				var data = msg.data;
				this.cId = data.cId;
				this.channels = data.channels;
				this.workStyle = data.ws;
				this._xhr.setTimeout(data.timeout + 1000/*网络延迟误差*/);
				this.fireEvent('connect', data.cId, data.channels, data.ws, data.timeout, this);
				this.revivalConnect();
			},this);
			connXhr.open('GET', url, true);
			connXhr.send(null);
			/*
			JS.AJAX.get(url,'',function(xhr){
				var msg = this.decodeMessage(xhr.responseText);
				if(!msg){
					this.stop('连接失败');
					return;
				}
				var data = msg.data;
				this.cId = data.cId;
				this.channels = data.channels;
				this.workStyle = data.ws;
				this._xhr.setTimeout(data.timeout + 1000);
				this.fireEvent('connect', data.cId, data.channels, data.ws, data.timeout, this);
				this.revivalConnect();
			},this);*/
		}
	},

	//private 重试复活连接
	retryRevivalConnect : function(){
		var self = this;
		if(this.running){
			setTimeout(function(){
				self.revivalConnect();
			},this.retryDelay);
		}
	},
	
	//private
	revivalConnect : function(){
		var self = this;
		if(this.running){
			setTimeout(revival,this.revivalDelay);
		}
		function revival(){
			var xhr = self._xhr;
			var param = self.perfectParam(self.param);
			var url = self.url + '?'+self.CMDTAG+'=revival&cid=' + self.cId + param;
			xhr.open('GET', url, true);
			xhr.send(null);
			self.fireEvent('revival',self.url, self.cId, self);
		}
		
	},
	/**
	 * 开始连接
	 * @method 
	 * @param {String} url 连接地址
	 * @param {String|DOM} param 连接参数
	 */
	start : function(url,param){
		if(this.running){
			return;
		}
		
		this.url = url || this.url;
		if(!this.url){
			throw new Error(this.emptyUrlError);
		}
		
		if(this.fireEvent('beforeConnect', this.url, this) === false){
			return;
		}
		
		this.param = param || this.param;
		param = this.perfectParam(this.param);
		var url = this.url+'?'+this.CMDTAG+'=conn&cv='+this.version+param;
		
		this.running = true;
		this.currRetry = 0;
		var self = this;
		setTimeout(function(){
			self.startConnect(url);
		},1000);
	},
	// 完善参数
	perfectParam : function(param){
		if(param && JS.isString(param)){
			if(param.charAt(0) != '&'){
				param = '&'+param;
			}
		}
		return param;
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
		if(this.fireEvent('beforeStop',cause,this.cId, this.url,  this) === false){
			return;
		}
		this.running = false;
		var cId = this.cId;
		this.cId = '';
		this.channels = [];
		this.workStyle = '';
		try{
			this._xhr.abort();
		}catch(e){};
		this.fireEvent('stop',cause, cId, this.url, this);
	},
	/**
	 * 获得连接ID
	 * @method 
	 * @return {String} 连接ID
	 */
	getId : function(){
		return this.cId;
	}
});