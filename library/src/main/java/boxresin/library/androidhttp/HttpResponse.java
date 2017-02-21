package boxresin.library.androidhttp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.HttpURLConnection;

/**
 * A class representing HTTP response.
 * You can get information in response(ex. HTTP status code, body of a message etc) via this class.
 * @since v1.0.0
 */
public class HttpResponse
{
	private int statusCode;
	private String statusMessage;
	private String bodyString;
	private HttpURLConnection connection;

	/**
	 * Preventing from instantiation of HttpResponse with constructor
	 */
	HttpResponse(int statusCode, String statusMessage, String bodyString, HttpURLConnection connection)
	{
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.bodyString = bodyString;
		this.connection = connection;
	}

	/**
	 * Returns HTTP status code of a response message.
	 * @return HTTP status code as int type (ex. 404)
	 * @since v1.0.0
	 */
	public int getStatusCode()
	{
		return statusCode;
	}

	/**
	 * Returns a description of HTTP status code.
	 * @return Description of HTTP status code as String type (ex. "Not Found")
	 * @since v1.0.0
	 */
	public String getStatusMessage()
	{
		return statusMessage;
	}

	/**
	 * Returns a header of a response message by specified key. <b>The key is case-sensitive.</b>
	 * @return A header of a response message. It will be null if there's no such header.
	 * @since v1.0.0
	 */
	@Nullable
	public String getHeader(String key)
	{
		return connection.getHeaderField(key);
	}

	/**
	 * Returns content of a response message. It can be an HTML document or JSON-formatted data.
	 * @return Content of a response message as String type. (<b>Content encoding is detected
	 *         automatically</b>)
	 * @since v1.0.0
	 */
	@NonNull
	public String getBody()
	{
		return bodyString;
	}
}
