package boxresin.library.androidhttp;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * A class representing HTTP response.
 * You can get information in response(ex. HTTP status code, body of a message etc) via this class.
 */
public class HttpResponse
{
	private int statusCode;
	private String statusMessage;
	private ByteArrayOutputStream out;

	/**
	 * Preventing from instantiation of HttpResponse with constructor
	 */
	HttpResponse(int statusCode, String statusMessage, ByteArrayOutputStream out)
	{
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.out = out;
	}

	/**
	 * Return HTTP status code of a response message.
	 * @return HTTP status code as int type (ex. 404)
	 */
	public int getStatusCode()
	{
		return statusCode;
	}

	/**
	 * Return a description of HTTP status code.
	 * @return Description of HTTP status code as String type (ex. "Not Found")
	 */
	public String getStatusMessage()
	{
		return statusMessage;
	}

	/**
	 * Return content of a response message. It can be an HTML document or JSON-formatted data.
	 * @return Body of a response message as String type
	 */
	public String getBody()
	{
		return out.toString();
	}

	/**
	 * Return content of a response message. It can be an HTML document or JSON-formatted data.
	 * @param encoding Name of charset (ex. "UTF-8")
	 * @return Body of a response message as String type with specified encoding
	 */
	public String getBody(String encoding) throws UnsupportedEncodingException
	{
		return out.toString(encoding);
	}
}
