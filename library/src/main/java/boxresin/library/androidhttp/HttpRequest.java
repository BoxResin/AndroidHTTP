package boxresin.library.androidhttp;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * A class representing HTTP request.
 * @since v1.0.0
 */
public class HttpRequest
{
	// @formatter:off
	String  url    = "";    // URL to requst
	String  method = "";    // HTTP method
	int     connectTimeout; // Timeout when connecting to a web server, in milliseconds
	int     readTimeout;    // Timeout when reading an HTTP response from a web server, in milliseconds
	boolean canceled;       // Whether request is canceled or not
	// @formatter:on

	// Map that contains POST parameters
	Map<String, String> params;

	/**
	 * Returns the URL to request.
	 * @return The URL to request
	 * @since v1.0.0
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Sets the URL to request.
	 * @param url The URL to request
	 * @since v1.0.0
	 */
	public HttpRequest setUrl(@NonNull String url)
	{
		this.url = url;
		return this;
	}

	/**
	 * Returns HTTP method to request.
	 * @return HTTP method as String type (ex. "POST", "GET" etc)
	 * @since v1.0.0
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * Sets HTTP method.
	 *
	 * @param method HTTP method as String type (ex. "POST", "GET" etc)<br>
	 *               <b>It's not case sensitive, so you can use both "POST" and "post".</b>
	 * @since v1.0.0
	 */
	public HttpRequest setMethod(@NonNull String method)
	{
		this.method = method.toUpperCase();
		return this;
	}

	/**
	 * Returns connect-timeout.
	 * @return Connect-timeout, in milliseconds
	 * @since v1.0.0
	 */
	public int getConnectTimeout()
	{
		return connectTimeout;
	}

	/**
	 * Sets timeout when connecting to a web server.
	 * @param timeout Connect-timeout, in milliseconds
	 * @since v1.0.0
	 */
	public HttpRequest setConnectTimeout(int timeout)
	{
		this.connectTimeout = timeout;
		return this;
	}

	/**
	 * Returns read-timeout.
	 * @return Read-timeout, in milliseconds
	 * @since v1.0.0
	 */
	public int getReadTimeout()
	{
		return readTimeout;
	}

	/**
	 * Sets timeout when reading an HTTP response from a web server.
	 * @param timeout Read-timeout, in milliseconds
	 * @since v1.0.0
	 */
	public HttpRequest setReadTimeout(int timeout)
	{
		this.readTimeout = timeout;
		return this;
	}

	/**
	 * Adds a parameter for POST method.
	 * If specified HTTP method is not "POST", this parameter would be ignored.
	 * @since v1.0.0
	 */
	public HttpRequest addParameter(@NonNull String key, @NonNull String value)
	{
		params.put(key, value);
		return this;
	}

	/**
	 * Clears all of parameters for POST method.
	 * @since v1.0.0
	 */
	public void clearParameters()
	{
		params.clear();
	}
}
