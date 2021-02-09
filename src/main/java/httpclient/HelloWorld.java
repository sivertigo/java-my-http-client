package httpclient;

import java.net.MalformedURLException;
import java.net.URL;

public class HelloWorld {

	public static void main(String[] args) throws MalformedURLException {
		String body = "{\"hoge\":\"joge\"}";

//		URL url = new URL("https://httpbin.org/post");
//		MyHttpResponse res = MyHttpClient.post(url, body).contentType("application/json; charset=UTF-8")
//				.accept("application/json").excecute();
		URL url = new URL("https://httpbin.org/get");
		MyHttpResponse res = MyHttpClient.get(url).contentType("application/json; charset=UTF-8")
				.accept("application/json").excecute();
	}

	public void get() throws MalformedURLException {
		URL url = new URL("https://httpbin.org/get");
		MyHttpResponse res = MyHttpClient.get(url).excecute();
	}

	public void postJson(String body) throws MalformedURLException {
		URL url = new URL("https://httpbin.org/post");

		MyHttpResponse res = MyHttpClient.post(url, body).contentType("application/json; charset=UTF-8")
				.accept("application/json").excecute();
	}

	public void postXml(String body) throws MalformedURLException {
		URL url = new URL("https://httpbin.org/post");

		MyHttpResponse res = MyHttpClient.post(url, body).contentType("application/xml; charset=UTF-8")
				.accept("application/xml").excecute();
	}
}
