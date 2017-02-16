package boxresin.library.androidhttp;

import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class to send HTTP requests.
 */
public class HttpRequester
{
	private String url = ""; // URL to requst
	private String method = ""; // HTTP method
	private int connectTimeout; // Timeout when connecting to a web server, in milliseconds
	private int readTimeout; // Timeout when reading an HTTP response from a web server, in milliseconds
	private boolean canceled; // Whether request is canceled or not

	// Map that contains POST parameters
	private Map<String, String> params = new TreeMap<>();

	private HttpCancelListener cancelListener;
	private Handler handler;

	/**
	 * Interface deifinition for a callback to be invoked when an HTTP request is canceled
	 */
	public interface HttpCancelListener
	{
		/**
		 * A callback method to be invoked when an HTTP request is canceled <br><br>
		 * <b>NOTE: This method will be invoked in the UI thread.</b>
		 */
		void onHttpCancel();
	}

	/**
	 * <b>NOTE: You have to instantiate 'HttpRequester' only in the UI thread.</b>
	 */
	public HttpRequester()
	{
		handler = new Handler();
	}

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
	 * @return HTTP method as String type
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * Sets HTTP method.
	 * @param method HTTP method as String type (ex. "POST", "GET" etc) It's not case sensitive, so
	 *               you can use both "POST" and "post".
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
	 * Add a parameter for POST method.
	 * If specified HTTP method is not "POST", this parameter would be ignored.
	 */
	public HttpRequester addParameter(String key, String value)
	{
		params.put(key, value);
		return this;
	}

	/**
	 * Clear all of parameters for POST method.
	 */
	public void clearParameters()
	{
		params.clear();
	}

	/**
	 * Send HTTP request to a web server.
	 *
	 * @return An HTML response from the web server. <br><br>
	 * <b>NOTE: It will return null if 'cancel' method is called during request. </b>
	 *
	 * @throws SocketTimeoutException Occurs when timeout.
	 */
	public HttpResponse request() throws SocketTimeoutException, IOException
	{
		// Set options.
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod(method);
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);

		// Only for POST method
		if (method.equals("POST"))
		{
			connection.setDoOutput(true);

			// Add POST parameters.
			boolean isNotFirst = false;
			OutputStream out = connection.getOutputStream();
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();

				if (isNotFirst)
					out.write("&".getBytes());
				else isNotFirst = true;

				out.write(String.format("%s=%s", key, value).getBytes());
			}
		}

		// Read HTTP status.
		int statusCode = connection.getResponseCode();
		String statusMessage = connection.getResponseMessage();

		// Prepare buffer.
		ByteArrayOutputStream bufferStream = new ByteArrayOutputStream(10 * 1024);
		byte[] buffer = new byte[1024];

		// Read response's body little by little in order to be ready for cancelation.
		InputStream in = connection.getInputStream();
		while (true)
		{
			int length = in.read(buffer);
			if (length == -1)
				break;
			bufferStream.write(buffer, 0, length);

			// Check if canceled.
			if (canceled)
			{
				canceled = false;
				connection.disconnect();
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						if (cancelListener != null)
							cancelListener.onHttpCancel();
					}
				});
				return null;
			}
		}

		connection.disconnect();
		return new HttpResponse(statusCode, statusMessage, bufferStream);
	}

	/**
	 * Cancel the 'request' method. <br><br>
	 * <b> Note: It doesn't terminate request immediately. If you want to know the time canceled,
	 * use cancel(listener). </b>
	 *
	 * @see #cancel(HttpCancelListener)
	 */
	public void cancel()
	{
		cancel(null);
	}

	/**
	 * Cancel the 'request' method.
	 * @param listener Interface for a callback to be invoked when an HTTP request is canceled.
	 */
	public void cancel(HttpCancelListener listener)
	{
		cancelListener = listener;
		canceled = true;
	}
}
