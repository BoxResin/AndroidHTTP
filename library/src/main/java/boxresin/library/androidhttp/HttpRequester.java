package boxresin.library.androidhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * A class to send HTTP requests.
 */
public class HttpRequester
{
	private String url = ""; // URL to requst
	private String method = ""; // HTTP method
	private int connectTimeout; // Timeout when connecting to a web server, in milliseconds
	private int readTimeout; // Timeout when reading an HTTP response from a web server, in milliseconds

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
	public HttpRequester setUrl(String url)
	{
		this.url = url;
		return this;
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
	 * Sets HTTP method.
	 * @param method HTTP method as String (ex. "POST", "GET" etc) It's not case sensitive, so you can use both "POST" and "post".
	 */
	public HttpRequester setMethod(String method)
	{
		this.method = method.toUpperCase();
		return this;
	}

	/**
	 * Return connect-timeout.
	 * @return Connect-timeout, in milliseconds
	 */
	public int getConnectTimeout()
	{
		return connectTimeout;
	}

	/**
	 * Sets timeout when connecting to a web server.
	 * @param timeout Connect-timeout, in milliseconds
	 */
	public HttpRequester setConnectTimeout(int timeout)
	{
		this.connectTimeout = timeout;
		return this;
	}

	/**
	 * Return read-timeout.
	 * @return Read-timeout, in milliseconds
	 */
	public int getReadTimeout()
	{
		return readTimeout;
	}

	/**
	 * Sets timeout when reading an HTTP response from a web server.
	 * @param timeout Read-timeout, in milliseconds
	 */
	public HttpRequester setReadTimeout(int timeout)
	{
		this.readTimeout = timeout;
		return this;
	}

	/**
	 * Send HTTP Request to a web server.
	 * @return An HTML response from the web server. It will return null if timeout occurs.
	 */
	public HttpResponse request() throws IOException
	{
		// Set options.
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod(method);
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);

		try
		{
			// Read HTTP status.
			int statusCode = connection.getResponseCode();
			String statusMessage = connection.getResponseMessage();

			// Prepare buffer.
			ByteArrayOutputStream out = new ByteArrayOutputStream(10 * 1024);
			byte[] buffer = new byte[1024];

			// Read response's body.
			InputStream in = connection.getInputStream();
			int length;
			while ((length = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, length);
			}

			return new HttpResponse(statusCode, statusMessage, out);
		}

		// An exception that occurs when timeout
		catch (SocketTimeoutException ignored)
		{
			return null;
		}

		finally
		{
			connection.disconnect();
		}
	}
}
