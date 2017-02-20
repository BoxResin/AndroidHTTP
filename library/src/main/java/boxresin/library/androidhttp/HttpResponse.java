package boxresin.library.androidhttp;

import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * A class representing HTTP response.
 * You can get information in response(ex. HTTP status code, body of a message etc) via this class.
 * @since v1.0.0
 */
public class HttpResponse
{
	private int statusCode;
	private String statusMessage;
	private ByteArrayOutputStream body;
	private final Map<String, List<String>> headerFields;

	/**
	 * Preventing from instantiation of HttpResponse with constructor
	 */
	HttpResponse(int statusCode, String statusMessage, ByteArrayOutputStream body, Map<String, List<String>> headerFields)
	{
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.body = body;
		this.headerFields = headerFields;
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
	 * Returns content of a response message. It can be an HTML document or JSON-formatted data.
	 * @return Body of a response message as String type
	 * @since v1.0.0
	 */
	public String getBody()
	{
		return body.toString();
	}

	/**
	 * Returns content of a response message. It can be an HTML document or JSON-formatted data.
	 * @param encoding Name of charset (ex. "UTF-8")
	 * @return Body of a response message as String type with specified encoding
	 * @since v1.0.0
	 */
	public String getBody(String encoding) throws UnsupportedEncodingException
	{
		return body.toString(encoding);
	}

	/**
	 * Returns a header of a response message by specified key. <b>The key is case-sensitive.</b>
	 * @return A header of a response message. It will be null if there's no such header.
	 * @since v1.0.0
	 */
	@Nullable
	public String getHeader(String key)
	{
		List<String> header = headerFields.get(key);
		if (header == null || header.size() == 0)
			return "";

		StringBuilder result = new StringBuilder();
		for (String str : header)
			result.append("; ").append(str);
		return result.substring(2);
	}

	/**
	 * Returns content of a response message. It can be an HTML document or JSON-formatted data.
	 * @return Body of a response message as byte array.
	 * @since v1.0.0
	 */
	public byte[] getBodyAsByteArray()
	{
		return body.toByteArray();
	}
}
