package boxresin.library.androidhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A class to send HTTP requests.
 */
public class HttpRequester
{
	private String url; // URL to requst
	private String method; // HTTP method

	/**
	 * Return the URL to request.
	 * @return The URL to request
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Sets the URL to request.
	 * @param url The URL to request
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * Return HTTP method to request.
	 * @return HTTP method as String
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * Sets HTTP method
	 * @param method HTTP method as String
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}

	/**
	 * Send HTTP Request to a web server.
	 * @return An HTML response from the web server. It will return null if timeout occurs.
	 */
	public HttpResponse request() throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod(method);

		int statusCode = connection.getResponseCode();
		String statusMessage = connection.getResponseMessage();

		InputStream in = connection.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream(10 * 1024); // prepare 10 KB buffer.
		byte[] buf = new byte[1024];

		int length;
		while ((length = in.read(buf)) != -1)
		{
			out.write(buf, 0, length);
		}

		connection.disconnect();
		return new HttpResponse(statusCode, statusMessage, out);
	}
}
