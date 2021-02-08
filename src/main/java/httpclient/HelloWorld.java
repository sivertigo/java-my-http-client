package httpclient;

import java.net.MalformedURLException;
import java.net.URL;

public class HelloWorld {

	public static void main(String[] args) throws MalformedURLException {
//		String token = "1d8a62fb7ea42c16fe5dd25f56ac105065625368";
//		new URL("https://qiita.com/api/v2/items/8f61f02f7c84b786697a/likes")
		String body = "{\"hoge\":\"joge\"}";
		URL url = new URL("https://httpbin.org/post");
		MyHttpResponse res = MyHttpClient.post(url, body).contentType("application/json; charset=UTF-8")
				.accept("application/json")
//				.get(url)
				// .authorization("Bearer " + token)
				.excecute();
	}

}
