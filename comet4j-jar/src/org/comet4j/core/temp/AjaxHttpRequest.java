/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.temp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * AjaxHttpRequest　，用java 模拟 浏览器的 XMLHttpRequest 对象. 目的是 用 操作浏览器中的XHR对象的
 * 方式来处理java端的 http请求.
 * @author fins 本类的实现借鉴了 cobra 组件的 org.lobobrowser.html.test.SimpleHttpRequest
 *         类. 可以看作是对 SimpleHttpRequest 类的一个完善和补充. cobra 组件是一个 Java HTML Renderer
 *         & Parser, 官方网站 :　http://lobobrowser.org/cobra.jsp
 */

public class AjaxHttpRequest {

	// 对应 XMLHttpRequest 的5种状态.
	public static final int STATE_UNINITIALIZED = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_LOADED = 2;
	public static final int STATE_INTERACTIVE = 3;
	public static final int STATE_COMPLETE = 4;

	// 默认的 userAgent
	public static final String DEFAULT_USERAGENT = "Mozilla/4.0 (compatible; MSIE 6.0;) JavaAjax/1.0";

	// 默认的 编码
	public static final String DEFAULT_AJAX_CHARSET = "UTF-8";
	public static final String DEFAULT_HTTP_CHARSET = "ISO-8859-1";

	// 默认的 HTTP method
	public static final String DEFAULT_REQUEST_METHOD = "POST";

	private int readyState;
	private int status;
	private String statusText;
	private String responseHeaders;
	private byte[] responseBytes;
	@SuppressWarnings("rawtypes")
	private Map responseHeadersMap;
	@SuppressWarnings("rawtypes")
	private final Map requestHeadersMap;
	private ReadyStateChangeListener readyStateChangeListener;

	private boolean async;
	private boolean sent;
	private URLConnection connection;
	private String userAgent = DEFAULT_USERAGENT;
	private String postCharset = DEFAULT_AJAX_CHARSET;
	private Proxy proxy;

	private URL requestURL;
	protected String requestMethod;
	protected String requestUserName;
	protected String requestPassword;

	// ///////////////////////////////
	// ///////////////////////////////

	/**
	 * 构造方法. 自动添加 XMLHttpRequest 的一些缺省头信息. 如果不需要这些头信息,可在创建 AjaxHttpRequest 实例后,
	 * 通过 setRequestHeader/removeRequestHeader/removeAllRequestHeaders 方法
	 * 进行修改或移除.
	 */
	@SuppressWarnings("rawtypes")
	public AjaxHttpRequest() {
		requestHeadersMap = new LinkedHashMap();
		setRequestHeader("X-Requested-With", "XMLHttpRequest");
		setRequestHeader("Accept", "text/javascript, text/html, application/xml, application/json, text/xml, */*");
	}

	/**
	 * 类似 XMLHttpRequest 中的 readyState 属性.
	 */
	public synchronized int getReadyState() {
		return this.readyState;
	}

	/**
	 * 类似 XMLHttpRequest 中的 status 属性.
	 */
	public synchronized int getStatus() {
		return this.status;
	}

	/**
	 * 类似 XMLHttpRequest 中的 statusText 属性.
	 */
	public synchronized String getStatusText() {
		return this.statusText;
	}

	/**
	 * 类似 XMLHttpRequest 中的 setRequestHeader 方法.
	 */
	@SuppressWarnings("unchecked")
	public void setRequestHeader(String key, String value) {
		this.requestHeadersMap.put(key, value);
	}

	/**
	 * 类似 XMLHttpRequest 中的 open 方法.
	 */
	public void open(String method, String url, boolean async, String userName, String password) throws IOException {
		URL urlObj = createURL(null, url);
		open(method, urlObj, async, userName, password);
	}

	/**
	 * 类似 XMLHttpRequest 中的 open 方法.
	 */
	public void open(final String method, final URL url, boolean async, final String userName, final String password)
			throws IOException {
		this.abort();
		Proxy proxy = this.proxy;
		URLConnection c = proxy == null || proxy == Proxy.NO_PROXY ? url.openConnection() : url.openConnection(proxy);
		synchronized (this) {
			this.connection = c;
			this.async = async;
			this.requestMethod = method;
			this.requestURL = url;
			this.requestUserName = userName;
			this.requestPassword = password;
		}
		this.changeState(AjaxHttpRequest.STATE_LOADING, 0, null, null);
	}

	/**
	 * 类似 XMLHttpRequest 中的 open 方法. 省略部分参数的形式.
	 */
	public void open(String url, boolean async) throws IOException {
		open(DEFAULT_REQUEST_METHOD, url, async, null, null);
	}

	/**
	 * 类似 XMLHttpRequest 中的 open 方法. 省略部分参数的形式.
	 */
	public void open(String method, String url, boolean async) throws IOException {
		open(method, url, async, null, null);
	}

	/**
	 * 类似 XMLHttpRequest 中的 send 方法. 支持发送 key-value 形式的数据集合(Map). 传入map参数,
	 * 自动转换成string形式 并调用 send(String) 方法发送.
	 */
	@SuppressWarnings("rawtypes")
	public void send(Map parameters) throws IOException {
		Iterator keyItr = parameters.keySet().iterator();
		StringBuffer strb = new StringBuffer();
		while (keyItr.hasNext()) {
			Object key = keyItr.next();
			String keyStr = encode(key);
			String valueStr = encode(parameters.get(key));
			strb.append(keyStr).append("=").append(valueStr);
			strb.append("&");
		}
		send(strb.toString());
	}

	/**
	 * 类似 XMLHttpRequest 中的 send 方法.
	 */
	public void send(final String content) throws IOException {
		final URL url = this.requestURL;
		if (url == null) {
			throw new IOException("No URL has been provided.");
		}
		if (this.isAsync()) {
			new Thread("AjaxHttpRequest-" + url.getHost()) {

				@Override
				public void run() {
					try {
						sendSync(content);
					} catch (Throwable thrown) {
						log(Level.WARNING, "send(): Error in asynchronous request on " + url, thrown);
					}
				}
			}.start();
		} else {
			sendSync(content);
		}
	}

	/**
	 * 类似 XMLHttpRequest 中的 getResponseHeader 方法.
	 */
	public synchronized String getResponseHeader(String headerName) {
		return this.responseHeadersMap == null ? null : (String) this.responseHeadersMap.get(headerName);
	}

	/**
	 * 类似 XMLHttpRequest 中的 getAllResponseHeaders 方法.
	 */
	public synchronized String getAllResponseHeaders() {
		return this.responseHeaders;
	}

	/**
	 * 类似 XMLHttpRequest 中的 responseText 属性.
	 */
	public synchronized String getResponseText() {
		byte[] bytes = this.responseBytes;
		String encoding = getCharset(this.connection);
		if (encoding == null) {
			encoding = getPostCharset();
		}
		if (encoding == null) {
			encoding = DEFAULT_HTTP_CHARSET;
		}
		try {
			return bytes == null ? null : new String(bytes, encoding);
		} catch (UnsupportedEncodingException uee) {
			log(Level.WARNING, "getResponseText(): Charset '" + encoding + "' did not work. Retrying with "
					+ DEFAULT_HTTP_CHARSET + ".", uee);
			try {
				return new String(bytes, DEFAULT_HTTP_CHARSET);
			} catch (UnsupportedEncodingException uee2) {
				// Ignore this time
				return null;
			}
		}
	}

	/**
	 * 类似 XMLHttpRequest 中的 responseXML 属性. TODO : 此方法在java中 并不常使用,
	 * 而且需要两个额外的包,两个包不是所有的java环境都有的. 所以我(fins)把此方法注释条了. 如果确实需要此方法
	 * 请自行取消该方法的注释,并引入下面列出的类/接口. javax.xml.parsers.DocumentBuilderFactory;
	 * org.w3c.dom.Document;
	 */
	// public synchronized Document getResponseXML() {
	// byte[] bytes = this.responseBytes;
	// if (bytes == null) {
	// return null;
	// }
	// InputStream in = new ByteArrayInputStream(bytes);
	// try {
	// return DocumentBuilderFactory.newInstance().newDocumentBuilder()
	// .parse(in);
	// }catch (Exception err) {
	// log(Level.WARNING, "Unable to parse response as XML.", err); return null;
	// }
	// }

	/**
	 * 类似 XMLHttpRequest 中的 responseBody 属性.
	 * @deprecated 这个方法命名源自XMLHttpRequest中的responseBody属性. 不过这个名字并不是好名字.建议使用
	 *             getResponseBytes 方法代替之.
	 */
	@Deprecated
	public synchronized byte[] getResponseBody() {
		return this.getResponseBytes();
	}

	/**
	 * 类似 XMLHttpRequest 中的 responseBody 属性. 建议使用此方法代替 getResponseBody 方法.
	 */
	public synchronized byte[] getResponseBytes() {
		return this.responseBytes;
	}

	/**
	 * 与 XMLHttpRequest 中的 responseStream 属性对应的方法 暂不提供. 因为1 不常用 2 通常可用
	 * getResponseBytes 方法代替
	 */

	/**
	 * 类似 XMLHttpRequest 中的 onreadystatechange 属性. 设置一个监听器,用来跟踪HttpRequest状态变化.
	 * 参数是一个 ReadyStateChangeListener 对象. ReadyStateChangeListener 是一个抽象类. 只需 实现
	 * onReadyStateChange方法即可.
	 */

	public void setReadyStateChangeListener(ReadyStateChangeListener listener) {
		this.readyStateChangeListener = listener;
	}

	/**
	 * 中断 Request 请求. 类似 XMLHttpRequest 中的 abort.
	 */
	public void abort() {
		URLConnection c = null;
		synchronized (this) {
			c = this.getConnection();
		}
		if (c instanceof HttpURLConnection) {
			((HttpURLConnection) c).disconnect();
		} else if (c != null) {
			try {
				c.getInputStream().close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	// /////////////////////////////////////////////
	// // 以上 为 模拟 XMLHttpRequest 对象 相关的方法 ////
	// /////////////////////////////////////////////
	// /////////////////////////////////////////////

	/**
	 * 返回 此次HttpRequest是否是"异步"的.
	 */
	public boolean isAsync() {
		return async;
	}

	/**
	 * 返回 此次HttpRequest是否已经发送. 已经发送 且还没有完全处理完此次发送的返回信息时,是不能再次发送的. 如果需要联系发送请求,
	 * 请再另行创建一个 AjaxHttpRequest对象.
	 */
	public boolean hasSent() {
		return sent;
	}

	protected void setSent(boolean sent) {
		this.sent = sent;
	}

	/**
	 * 设置/取得 伪造的 userAgent 信息.通常不用理会. 很少有http服务会对此做严格的判断.
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getUserAgent() {
		return this.userAgent;
	}

	/**
	 * 取得/设置 默认的 AJAX编码.AJAX请求都是UTF-8编码 此属性通常无需改变.
	 */
	public String getPostCharset() {
		return this.postCharset;
	}

	public void setPostCharset(String postCharset) {
		this.postCharset = postCharset;
	}

	/**
	 * 实现发送数据功能的方法,是整个类的核心. 我(fins)借鉴了 cobra 组件的
	 * org.lobobrowser.html.test.SimpleHttpRequest 类中的同名方法. 略作改动.
	 */
	protected void sendSync(String content) throws IOException {
		if (hasSent()) {
			log(Level.WARNING, "This AjaxHttpRequest Object has sent", null);
			return;
		}
		try {
			URLConnection c;
			synchronized (this) {
				c = this.connection;
			}
			if (c == null) {
				log(Level.WARNING, "Please open AjaxHttpRequest first.", null);
				return;
			}
			setSent(true);
			initConnectionRequestHeader(c);
			int istatus;
			String istatusText;
			InputStream err;
			if (c instanceof HttpURLConnection) {
				HttpURLConnection hc = (HttpURLConnection) c;
				String method = this.requestMethod == null ? DEFAULT_REQUEST_METHOD : this.requestMethod;

				method = method.toUpperCase();
				hc.setRequestMethod(method);
				if ("POST".equals(method) && content != null) {
					hc.setDoOutput(true);
					byte[] contentBytes = content.getBytes(this.getPostCharset());
					hc.setFixedLengthStreamingMode(contentBytes.length);
					OutputStream out = hc.getOutputStream();
					try {
						out.write(contentBytes);
					} finally {
						out.flush();
					}
				}
				istatus = hc.getResponseCode();
				istatusText = hc.getResponseMessage();
				err = hc.getErrorStream();
			} else {
				istatus = 0;
				istatusText = "";
				err = null;
			}
			synchronized (this) {
				this.responseHeaders = getConnectionResponseHeaders(c);
				this.responseHeadersMap = c.getHeaderFields();
			}
			this.changeState(AjaxHttpRequest.STATE_LOADED, istatus, istatusText, null);
			InputStream in = err == null ? c.getInputStream() : err;
			int contentLength = c.getContentLength();

			this.changeState(AjaxHttpRequest.STATE_INTERACTIVE, istatus, istatusText, null);
			byte[] bytes = loadStream(in, contentLength == -1 ? 4096 : contentLength);
			this.changeState(AjaxHttpRequest.STATE_COMPLETE, istatus, istatusText, bytes);
		} finally {
			synchronized (this) {
				this.connection = null;
				setSent(false);
			}
		}
	}

	/**
	 * 当状态变化时 重新设置各种状态值,并触发状态变化监听器.
	 */
	protected void changeState(int readyState, int status, String statusMessage, byte[] bytes) {
		synchronized (this) {
			this.readyState = readyState;
			this.status = status;
			this.statusText = statusMessage;
			this.responseBytes = bytes;
		}
		if (this.readyStateChangeListener != null) {
			this.readyStateChangeListener.onReadyStateChange();
		}
	}

	/**
	 * 对字符串进行 URLEncoder 编码.
	 */
	protected String encode(Object str) {
		try {
			return URLEncoder.encode(String.valueOf(str), getPostCharset());
		} catch (UnsupportedEncodingException e) {
			return String.valueOf(str);
		}
	}

	/**
	 * 将设置的 RequestHeader 真正的设置到链接请求中.
	 */
	@SuppressWarnings("rawtypes")
	protected void initConnectionRequestHeader(URLConnection c) {
		c.setRequestProperty("User-Agent", this.getUserAgent());
		Iterator keyItor = this.requestHeadersMap.keySet().iterator();
		while (keyItor.hasNext()) {
			String key = (String) keyItor.next();
			String value = (String) this.requestHeadersMap.get(key);
			c.setRequestProperty(key, value);
		}
	}

	/**
	 * 以下 4个 方法 负责处理 requestHeader信息.
	 */
	public String getRequestHeader(String key) {
		return (String) this.requestHeadersMap.get(key);
	}

	public String removeRequestHeader(String key) {
		return (String) this.requestHeadersMap.remove(key);
	}

	public void removeAllRequestHeaders() {
		this.requestHeadersMap.clear();
	}

	@SuppressWarnings("rawtypes")
	public Map getAllRequestHeaders() {
		return this.requestHeadersMap;
	}

	public URLConnection getConnection() {
		return connection;
	}

	public void setConnection(URLConnection connection) {
		this.connection = connection;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	// ///////////////////////////////////////////////////////////////////
	// 以下是 Static Method //////////////////////////////////////////////
	// 这些静态方法其实可以(应该)单独提取出去的, ///////////////////////////////
	// 不过为了让这个程序结构简单些 , 我(fins)就全部 all in one 了.///////////////////
	// 这些方法也不都是我(fins)自己写的 很多是copy 借鉴来的 功能都挺简单的 就不详细说明了 //
	// ///////////////////////////////////////////////////////////////////

	public static void log(Level level, String msg, Throwable thrown) {
		System.out.println(level.getName() + " : " + thrown.getMessage() + " ----- " + msg);
	}

	public static String getConnectionResponseHeaders(URLConnection c) {
		int idx = 0;
		String value;
		StringBuffer buf = new StringBuffer();
		while ((value = c.getHeaderField(idx)) != null) {
			String key = c.getHeaderFieldKey(idx);
			buf.append(key);
			buf.append(": ");
			buf.append(value);
			idx++;
		}
		return buf.toString();
	}

	public static String getCharset(URLConnection connection) {
		String contentType = connection == null ? null : connection.getContentType();
		if (contentType != null) {
			StringTokenizer tok = new StringTokenizer(contentType, ";");
			if (tok.hasMoreTokens()) {
				tok.nextToken();
				while (tok.hasMoreTokens()) {
					String assignment = tok.nextToken().trim();
					int eqIdx = assignment.indexOf('=');
					if (eqIdx != -1) {
						String varName = assignment.substring(0, eqIdx).trim();
						if ("charset".equalsIgnoreCase(varName)) {
							String varValue = assignment.substring(eqIdx + 1);
							return unquote(varValue.trim());
						}
					}
				}
			}
		}
		return null;
	}

	public static String unquote(String text) {
		if (text.startsWith("\"") && text.endsWith("\"")) {
			return text.substring(1, text.length() - 2);
		}
		return text;
	}

	protected static URL createURL(URL baseUrl, String relativeUrl) throws MalformedURLException {
		return new URL(baseUrl, relativeUrl);
	}

	protected static byte[] loadStream(InputStream in, int initialBufferSize) throws IOException {
		if (initialBufferSize == 0) {
			initialBufferSize = 1;
		}
		byte[] buffer = new byte[initialBufferSize];
		int offset = 0;
		for (;;) {
			int remain = buffer.length - offset;
			if (remain <= 0) {
				int newSize = buffer.length * 2;
				byte[] newBuffer = new byte[newSize];
				System.arraycopy(buffer, 0, newBuffer, 0, offset);
				buffer = newBuffer;
				remain = buffer.length - offset;
			}
			int numRead = in.read(buffer, offset, remain);
			if (numRead == -1) {
				break;
			}
			offset += numRead;
		}
		if (offset < buffer.length) {
			byte[] newBuffer = new byte[offset];
			System.arraycopy(buffer, 0, newBuffer, 0, offset);
			buffer = newBuffer;
		}
		return buffer;
	}

	// /////////////////////////////////////////////////////////
	// Listener Class /////////////////////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * 一个用来监听 HttpRequest状态 的监听器. 是一个内部静态抽象类. 可以根据实际情况来自行重构(如 增加方法、变为独立的外部类等).
	 */
	public static abstract class ReadyStateChangeListener {

		public abstract void onReadyStateChange();
	}

	// /////////////////////////////////////////////////////////
	// Test Method ////////////////////////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * 利用这个AjaxHttpReuqest类来实现 对google translate服务的访问 . 只演示了 "英-->汉"的翻译.
	 * 返回的是JSON字符串,需要使用Json工具类进行转换,这个不难 就不详细举例了.
	 */
	@SuppressWarnings("rawtypes")
	public static void testGoogleTranslate(String words, boolean async) throws IOException {
		Map params = new HashMap();
		/*
		 * params.put("q",words); params.put("v","1.0");
		 * params.put("langpair","en|zh-CN"); String
		 * url="http://ajax.googleapis.com/ajax/services/language/translate";
		 */
		String url = "http://localhost:8080/long";

		// 以上为 调用google翻译服务所需要的参数.
		// 下面 是用java 来调用这个 翻译服务.
		// 在 AjaxHttpRequest 类的 帮助下 我们可以使用 类似操作浏览器XHR对象的方式来 实现该功能.

		// 创建 AjaxHttpRequest对象 相当于 创建 XHR对象.
		// 这里的final 主要是为了"使监听器内部能够调用ajax对象"而加的.
		final AjaxHttpRequest ajax = new AjaxHttpRequest();

		// 设置状态监听器,类似 XHR对象的 onreadystatechange 属性.
		// 由于js 和java的本质区别 导致这里可能和 xhr有点不一样,但是应该不难理解.
		ajax.setReadyStateChangeListener(
		// 监听器, 根据需求 实现onReadyStateChange方法即可.
		new AjaxHttpRequest.ReadyStateChangeListener() {

			@Override
			public void onReadyStateChange() {
				int readyState = ajax.getReadyState();
				System.out.println(ajax.getStatus());
				System.out.println(ajax.getReadyState());
				// System.out.println( ajax.getResponseText() );
				// 判断状态 然后做出相应处理.
				if (// readyState==AjaxHttpRequest.STATE_INTERACTIVE ||
				readyState == AjaxHttpRequest.STATE_COMPLETE) {
					System.out.println(ajax.getResponseText());
				}
			}
		});

		// 这里就和 xhr 几乎完全一样了
		ajax.open("POST", url, true);

		// 这里这个send方法接受的参数是一个map ,当然AjaxHttpRequest类也提供了string的方法
		ajax.send(params);

	}

	public static void main(String[] args) throws IOException {

		testGoogleTranslate("Hello world!", false);

	}

}
