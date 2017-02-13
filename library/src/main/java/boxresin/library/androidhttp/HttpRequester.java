package boxresin.library.androidhttp;

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
	 * Send HTTP Request to web server.
	 */
	public HttpResponse request()
	{
		return null;
	}
}
