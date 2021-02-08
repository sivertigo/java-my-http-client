package httpclient;

import java.net.MalformedURLException;
import java.net.URL;

public class HelloWorld {

	public static void main(String[] args) throws MalformedURLException {
		String body = "{\"hoge\":\"joge\"}";
		URL url = new URL("https://httpbin.org/post");
		MyHttpResponse res = MyHttpClient.post(url, body).contentType("application/json; charset=UTF-8")
				.accept("application/json").excecute();
	}

}
