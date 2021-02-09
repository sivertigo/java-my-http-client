package httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Http Request HTTP/1.1対応
 * 
 * @author t.sugawara
 * @version 2021/02/05 新規作成
 * 
 */
public class MyHttpClient {

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String ACCEPT = "Accept";
	public static final String COOKIE = "Cookie";
	public static final String AUTHORIZATION = "Authorization";

	// 必須パラメータ
	private URL url = null;
	private String contentType = null;
	// 任意パラメータ
	private String accept = null;
	private String method = "GET";
	private int connectTimeOut = 10000;
	private int readTimeOut = 30000;
	private String cookie = null;
	private Boolean useCache = false;
	private String requestBody = null;

	/* valid HTTP methods */
	protected static final String[] METHODS = { "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE" };

	public static class HttpClientBuilder {
		// These parameters are used for sending.
		/* required parameters */
		/** HTTP Method */
		private String method = "GET";
		/** URL of the destination host */
		private URL url;
		/** The media type of the request body */
		private String contentType;

		/* optional parameters */
		/** A map of HTTP header property */
		private Map<String, String> headerProperty = new HashMap<String, String>();
		/** The type of media the client allow to recieve */
		private String accept = null;
		/** the timeout in making the initial connection */
		private int connectTimeOut = 10000;
		/** the timeout on waiting to read data */
		private int readTimeOut = 30000;
		/** The Cookie to set header */
		private String cookie = null;
		/** query parameter */
		private String queryString = "";
		/** Information of proxy server to use */
		private Proxy proxyServer = null;
		/** The flag to indicate proxy is set */
		private Boolean isProxySet = false;
		/** The content of Http request */
		private String requestBody = null;
		/** The flag to indicate wheter to use cache */
		private Boolean isUseCache = false;

		/**
		 * Set GET as HTTP Request Method.
		 * 
		 * @return MyHttpRequestBuilder
		 */
		private HttpClientBuilder setGet() {
			this.method = "GET";
			return this;
		}

		/**
		 * Set POST as HTTP Request Method.
		 * 
		 * @return MyHttpRequestBuilder
		 */
		private HttpClientBuilder setPost() {
			this.method = "POST";
			return this;
		};

		/**
		 * URLを設定する.
		 * 
		 * @param url
		 * @return HttpClientBuilder
		 */
		private HttpClientBuilder url(URL urlPrm) {
			this.url = urlPrm;
			return this;
		};

		/**
		 * Acceptを設定する.
		 * 
		 * @param acceptPrm
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder accept(String acceptPrm) {
			this.accept = acceptPrm;
			this.headerProperty.put(ACCEPT, this.accept);
			return this;
		};

		/**
		 * Cookieを設定する.
		 * 
		 * @param cookie
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder cookie(String cookie) {
			this.cookie = cookie;
			this.headerProperty.put(COOKIE, this.cookie);
			return this;
		};

		/**
		 * useCacheを設定する.
		 * 
		 * @param isUseCache
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder useCache(Boolean isUseCache) {
			this.isUseCache = isUseCache;
			return this;
		};

		/**
		 * リクエストボディを設定する.
		 * 
		 * @param reqBody
		 * @return HttpClientBuilder
		 */
		private HttpClientBuilder StrBody(String reqBody) {
			this.requestBody = reqBody;
			return this;
		}

		/**
		 * 指定されたフィールド名と値をヘッダーに設定する.
		 * 
		 * @param fieldName
		 * @param value
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder header(String fieldName, String value) {
			if ("Content-Length".equals(fieldName)) {
				throw new IllegalArgumentException();
			}
			this.headerProperty.put(fieldName, value);
			return this;
		}

		/**
		 * Content-Typeを設定する.
		 * 
		 * @param value Content-Type
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder contentType(String value) {
			this.contentType = value;
			this.headerProperty.put(CONTENT_TYPE, this.contentType);
			return this;
		}

		/**
		 * Authorizationを設定する.
		 * 
		 * @param value 設定値
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder authorization(String value) {
			this.headerProperty.put(AUTHORIZATION, value);
			return this;
		}

		/**
		 * Proxyサーバを設定する.
		 * 
		 * @param proxyServer
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder proxy(Proxy proxyServer) {
			this.proxyServer = proxyServer;
			this.isProxySet = true;
			return this;
		}

		/**
		 * クエリ文字列を構築する.
		 * 
		 * @param queryKey
		 * @param value
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder queryString(String queryKey, String value) {
			if (this.queryString.isEmpty()) {
				this.queryString += "?" + queryKey + "=" + value;
			} else {
				this.queryString += "&" + queryKey + "=" + value;
			}
			return this;
		}

		/**
		 * コネクションタイムアウトの時間を設定する.
		 * 
		 * @param timeoutMSec
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder connectTimeout(Integer timeoutMSec) {
			this.connectTimeOut = timeoutMSec;
			return this;
		}

		/**
		 * 読み込みタイムアウトの時間を設定する.
		 * 
		 * @param timeoutMSec
		 * @return HttpClientBuilder
		 */
		public HttpClientBuilder readTimeout(Integer timeoutMSec) {
			this.readTimeOut = timeoutMSec;
			return this;
		}

		/**
		 * MyHttpRequestを作成して返す.
		 * 
		 * @return MyHttpRequest
		 */
		public MyHttpClient build() {
			if (method == null) {
				throw new NullPointerException("HTTPリクエストのMethodを設定してください.");
			}
			return new MyHttpClient(this);
		}

		/**
		 * HTTP通信を実行する.
		 * 
		 * @return
		 * @throws IOException
		 * @throws ParserConfigurationException
		 * @throws TransformerException
		 */
		public MyHttpResponse excecute() {
			if (!this.queryString.isEmpty()) {
				try {
					this.url = new URL(this.url.toString() + this.queryString);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			HttpURLConnection con = null;
			BufferedReader reader = null;
			OutputStream out = null;
			try {
				// コネクションを取得する.
				if (this.isProxySet) {
					con = (HttpURLConnection) this.url.openConnection(this.proxyServer);
				} else {
					con = (HttpURLConnection) this.url.openConnection();
				}
				con.setRequestMethod(this.method);

				if (this.method.equals("GET")) {
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setUseCaches(this.isUseCache);
					// Keep-Aliveを利用せず強制的に切る設定
					con.setRequestProperty("Connection", "close");

					// false: 接続先で3XXが返されてもリダイレクトを行わず、明示的な接続先URLのみにアクセスする。
					con.setInstanceFollowRedirects(false);
					con.setConnectTimeout(connectTimeOut);
					con.setReadTimeout(readTimeOut);

					for (String key : this.headerProperty.keySet()) {
						con.setRequestProperty(key, this.headerProperty.get(key));
					}
					System.out.println(con.getHeaderField("Content-Type"));
					System.out.println(con.getResponseCode());
					// ボディ部
					StringBuilder sb = new StringBuilder();
					String line = null;
					reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					System.out.println(sb.toString());
				} else if (this.method.equals("POST")) {
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setUseCaches(this.isUseCache);
					// Keep-Aliveを利用せず強制的に切る設定
					con.setRequestProperty("Connection", "close");

					// false: 接続先で3XXが返されてもリダイレクトを行わず、明示的な接続先URLのみにアクセスする。
					con.setInstanceFollowRedirects(false);
					con.setConnectTimeout(connectTimeOut);
					con.setReadTimeout(readTimeOut);

					for (String key : this.headerProperty.keySet()) {
						con.setRequestProperty(key, this.headerProperty.get(key));
					}

					byte[] val = this.requestBody.getBytes("UTF-8");

					con.setRequestProperty("Content-Length", String.valueOf(val.length));

					OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
					writer.write(this.requestBody);
					writer.flush();
					writer.close();

					System.out.println(con.getHeaderField("Content-Type"));
					System.out.println(con.getResponseCode());
					if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
						throw new RuntimeException("失敗");
					}

					// HTTPレスポンス受信
					StringBuilder ssb = new StringBuilder();
					String sline = null;
					reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
					while ((sline = reader.readLine()) != null) {
						ssb.append(sline).append("\n");
					}

					System.out.println(ssb.toString());

				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (con != null) {
					con.disconnect();
				}
			}
			return null;
		}

	}

	/**
	 * @param builder
	 */
	public MyHttpClient(HttpClientBuilder builder) {
		this.method = builder.method;
		this.url = builder.url;

	}

	/**
	 * 新しくPOST用Builderを作成して返却.
	 * 
	 * @return
	 */
	public static HttpClientBuilder post(URL url, String body) {
		HttpClientBuilder builder = new HttpClientBuilder();
		builder.setPost();
		builder.url(url);
		builder.StrBody(body);
		return builder;
	}

	/**
	 * 新しくGET用Builderを作成して返却.
	 * 
	 * @return
	 */
	public static HttpClientBuilder get(URL url) {
		HttpClientBuilder builder = new HttpClientBuilder();
		builder.setGet();
		builder.url(url);
		return builder;
	}
}