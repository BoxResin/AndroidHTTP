package boxresin.library.androidhttp;

/**
 * A class representing HTTP response.
 * You can get information in response(ex. HTTP version of a web server, HTTP status code, body of a message etc) via this class.
 */
public class HttpResponse
{
	/**
	 * Preventing instantiation of HttpResponse with constructor.
	 */
	HttpResponse()
	{

	}

	/**
	 * Return HTTP version of the web server.
	 * @return HTTP version as String (ex. "HTTP/1.1")
	 */
	public String getHttpVersion()
	{
		return null;
	}

	/**
	 * Return HTTP status code of a response message.
	 * @return HTTP status code as int. (ex. 404)
	 */
	public int getStatusCode()
	{
		return 0;
	}

	/**
	 * Return a description of HTTP status code.
	 * @return description of HTTP status code as String. (ex. "Not Found")
	 */
	public String getStatusMessage()
	{
		return null;
	}

	/**
	 * Return content of a response message. It can be an HTML document or JSON-formatted data.
	 * @return body of a response message as String.
	 */
	public String getBody()
	{
		return null;
	}
}
