package httpclient;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class MyHttpClientTest {
	@Test
	public void trySimpleGet() {
		URL url = null;
		try {
			url = new URL("https://httpbin.org/get");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		MyHttpResponse res = MyHttpClient.get(url).excecute();
	}

	@Test
	public void trySimpleJsonPost() throws MalformedURLException {
		String body = "{\"hoge\":\"joge\"}";

		URL url = new URL("https://httpbin.org/post");
		MyHttpResponse res = MyHttpClient.post(url, body).contentType("application/json; charset=UTF-8")
				.accept("application/json").excecute();

	}

	@Test
	public void trySimpleXmlPost()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// 1. DocumentBuilderFactoryのインスタンスを取得する
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// 2. DocumentBuilderのインスタンスを取得する
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 3. DocumentBuilderにXMLを読み込ませ、Documentを作る
		Document document = builder.parse(Paths
				.get("C:\\Users\\sgtak\\eclipse-workspace\\httpclient\\src\\main\\resources\\httpclient\\books.xml")
				.toFile());

		StringWriter writer = new StringWriter();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

		transformer.transform(new DOMSource(document), new StreamResult(writer));
		String body = writer.toString();
		URL url = new URL("https://httpbin.org/post");
		MyHttpResponse res = MyHttpClient.post(url, body).contentType("application/xml; charset=UTF-8")
				.accept("application/json").excecute();
	}
}
