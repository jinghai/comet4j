var statebar, toolbar, logbox, inputbox, lastTalkId, totalMemoryDom, freeMemoryDom, maxMemoryDom, usedMemoryDom, connectorCountDom, startupDom, workStyleDom, maxLogCount = 100,loginDom;
var isFocus = true,unreadCount = 0,dTitle="ChatDemo";
function windowOnFocus(){
	isFocus = true;
	unreadCount = 0;
	document.title = dTitle;
}
function windowOnBlur(){
	isFocus = false;
}
function windowResize() {
	var offset = 2;
	var other = statebar.offsetHeight + toolbar.offsetHeight + offset;
	logbox.style.height = document.documentElement.clientHeight - other + 'px';
}
function loginEnter(event){
	if (event.keyCode == 13) {
		login();
		return false;
	}
}
function showLogin(){
	statebar.style.display = 'none';
	toolbar.style.display = 'none';
	logbox.style.display = 'none';
	loginDom.style.display = 'block';
	loginDom.style.height = document.documentElement.clientHeight + 'px';
	document.getElementById("loginName").focus();
}
function login(callback){
	var userName = document.getElementById("loginName").value;
	userName = userName ? userName.trim() : '' ;
	if(!userName){
		alert('非法昵称，请重新输入');
		document.getElementById("loginName").fucos();
		return;
	}
	setCookie('userName',userName, 365);
	loginDom.style.display = 'none';
	statebar.style.display = 'block';
	toolbar.style.display = 'block';
	logbox.style.display = 'block';
	start();
}
function init() {
	statebar = document.getElementById("statebar");
	toolbar = document.getElementById("toolbar");
	logbox = document.getElementById("logbox");
	inputbox = document.getElementById("inputbox");
	totalMemoryDom = document.getElementById("totalMemory");
	freeMemoryDom = document.getElementById("freeMemory");
	maxMemoryDom = document.getElementById("maxMemory");
	usedMemoryDom = document.getElementById("usedMemory");
	connectorCountDom = document.getElementById("connectorCount");
	workStyleDom = document.getElementById("workStyle");
	startupDom = document.getElementById("startup");
	loginDom = document.getElementById("login");
	windowResize();
	JS.on(window,'resize',windowResize);
	JS.on(window,'focus',windowOnFocus);
	JS.on(window,'blur',windowOnBlur);
	
	// 引擎事件绑定
	JS.Engine.on({
		start : function(cId, channels, engine) {
			var style = engine.getConnector().workStyle;
			style = style === 'stream'?'长连接':'长轮询';
			workStyleDom.innerHTML = style;
		},
		stop : function(cause, url, cId, engine) {
			workStyleDom.innerHTML = '<span class="warning">已停止<a href="javascript:start();" >(重连)</a></span>';
		},
		talker : function(data, timespan, engine) {
			switch (data.type) {
			case 'rename': // 改名
				onRename(data, timespan);
				break;
			case 'talk': // 收到聊天消息
				onMessage(data, timespan);
				break;
			case 'up': // 上线
				onJoin(data, timespan);
				break;
			case 'down': // 下线
				onLeft(data, timespan);
				break;
			case 'health':
				onHealthMessage(data, timespan);
				break;
			default:
			}
		}
	});
	
	var userName = getCookie('userName') || '';
	userName = userName ? userName.trim() : '' ;
	if(userName){
		start();
	}else{
		showLogin();
	}
}
//开启连接
function start(){
	JS.Engine.start('conn');
	inputbox.focus();
}
// 用户改名通知
function onRename(data, timespan) {
	var id = data.id;
	var newName = data.newName || '';
	newName = newName.HTMLEncode();
	var oldName = data.oldName || '';
	oldName = oldName.HTMLEncode();
	var t = data.transtime;
	var str = [ '<div class="sysmessage">', t, '【', oldName, '】改名为【',
			newName, '】</div>' ];
	checkLogCount();
	logbox.innerHTML += str.join('');
	lastTalkId = null;
	moveScroll();
}
// 用户聊天通知
function onMessage(data, timespan) {
	if(!isFocus){
		unreadCount++;
		document.title = '('+unreadCount+')'+dTitle;
	}
	var id = data.id;
	var name = data.name || '';
	name = name.HTMLEncode();
	var text = data.text || '';
	text = text.HTMLEncode();
	var t = data.transtime;
	var str;
	if (lastTalkId == id) {
		str = [ '<div class="usermessage">', '<blockquote>', text,
				'</blockquote>', '</div>' ];
	} else {
		str = [ '<div class="usermessage">', t, '<span class="user">【',
				name, '】</span><blockquote>', text, '</blockquote>', '</div>' ];
	}
	checkLogCount();
	logbox.innerHTML += str.join('');
	lastTalkId = id;
	moveScroll();
}
// 用户上线通知
function onJoin(data, timespan) {
	if(!isFocus){
		unreadCount++;
		document.title = '('+unreadCount+')'+dTitle;
	}
	var id = data.id;
	var name = data.name || id;
	name = name.HTMLEncode();
	var t = data.transtime;
	var str = [
			'<div class="sysmessage">',
			t,
			'【',
			name,
			'】来了，欢迎体验 <a href="http://code.google.com/p/comet4j/" target="_new">Comet For Java</a>',
			'</div>' ];
	checkLogCount();
	logbox.innerHTML += str.join('');
	lastTalkId = null;
	moveScroll();
}
// 用户下线通知
function onLeft(data, timespan) {
	var id = data.id;
	var name = data.name || id;
	name = name.HTMLEncode();
	var t = data.transtime;
	var str = [ '<div class="sysmessage">', t, '【', name, '】离开了',
			'</div>' ];
	checkLogCount();
	logbox.innerHTML += str.join('');
	lastTalkId = null;
	moveScroll();
}
// 系统健康信息
function onHealthMessage(data, timespan) {
	var totalMemory = data.totalMemory;
	var freeMemory = data.freeMemory;
	var maxMemory = data.maxMemory;
	var usedMemory = data.usedMemory;
	var startup = data.startup;
	var connectorCount = data.connectorCount + '个';
	totalMemoryDom.innerHTML = totalMemory + 'M';
	freeMemoryDom.innerHTML = freeMemory + 'M';
	maxMemoryDom.innerHTML = maxMemory + 'M';
	usedMemoryDom.innerHTML = usedMemory + 'M';
	connectorCountDom.innerHTML = connectorCount;
	startupDom.innerHTML = startup;
}

// 检测输出长度
function checkLogCount() {
	var count = logbox.childNodes.length;
	if (count > maxLogCount) {
		var c = count - maxLogCount;
		for ( var i = 0; i < c; i++) {
			// logbox.removeChild(logbox.children[0]);
			logbox.removeChild(logbox.firstChild);
		}

	}
}
// 移动滚动条
function moveScroll() {
	logbox.scrollTop = logbox.scrollHeight;
	inputbox.focus();
}
// 回车事件
function onSendBoxEnter(event) {
	if (event.keyCode == 13) {
		var text = inputbox.value;
		send(text);
		return false;
	}
}
// 发送聊天信息动作
function send(text) {
	if (!JS.Engine.running)
		return;
	text = text.trim();
	if (!text)
		return;
	var id = JS.Engine.getId();
	var param = "id=" + id + '&text=' + encodeURIComponent(text);
	JS.AJAX.post('talk.do?cmd=talk', param, function() {
		inputbox.value = '';
	});
}
// 改名动作
function rename() {
	if (!JS.Engine.running)
		return;
	var oldName = getCookie('userName') || '';
	oldName = oldName.trim();
	var userName = prompt("请输入姓名", oldName);
	userName = userName ? userName.trim() : '';
	var id = JS.Engine.getId();
	if (!id || !userName || oldName == userName)
		return;
	var param = "id=" + id + '&newName=' + encodeURIComponent(userName)
			+ '&oldName=' + encodeURIComponent(oldName);
	setCookie('userName', userName, 365);
	JS.AJAX.post('talk.do?cmd=rename', param);
}

// 设置Cookie
function setCookie(name, value, expireDay) {
	var exp = new Date();
	exp.setTime(exp.getTime() + expireDay * 24 * 60 * 60 * 1000);
	document.cookie = name + "=" + encodeURIComponent(value) + ";expires="
			+ exp.toGMTString();
}
// 获得Cookie
function getCookie(name) {
	var arr = document.cookie
			.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
	if (arr != null)
		return decodeURIComponent(arr[2]);
	return null;
}
// 删除Cookie
function delCookie(name) {
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval = getCookie(name);
	if (cval != null)
		document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}
// HTML编码
String.prototype.HTMLEncode = function() {
	var temp = document.createElement("div");
	(temp.textContent != null) ? (temp.textContent = this)
			: (temp.innerText = this);
	var output = temp.innerHTML;
	temp = null;
	return output;
};
// HTML解码
String.prototype.HTMLDecode = function() {
	var temp = document.createElement("div");
	temp.innerHTML = this;
	var output = temp.innerText || temp.textContent;
	temp = null;
	return output;
};
String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g, '');
};