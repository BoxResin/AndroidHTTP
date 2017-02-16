package boxresin.library.androidhttp;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

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
 * @since v1.0.0
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
	 * @since v1.0.0
	 */
	public interface HttpCancelListener
	{
		/**
		 * A callback method to be invoked when an HTTP request is canceled <br><br>
		 * <b>NOTE: This method will be invoked on the UI thread.</b>
		 *
		 * @since v1.0.0
		 */
		@UiThread
		void onHttpCancel();
	}

	/**
	 * <b>NOTE: This constructor must only be called on the UI thread.</b>
	 * @since v1.0.0
	 */
	@UiThread
	public HttpRequester()
	{
		handler = new Handler();
	}

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
	public HttpRequester setUrl(@NonNull String url)
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
	public HttpRequester setMethod(@NonNull String method)
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
	public HttpRequester setConnectTimeout(int timeout)
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
	public HttpRequester setReadTimeout(int timeout)
	{
		this.readTimeout = timeout;
		return this;
	}

	/**
	 * Adds a parameter for POST method.
	 * If specified HTTP method is not "POST", this parameter would be ignored.
	 * @since v1.0.0
	 */
	public HttpRequester addParameter(@NonNull String key, @NonNull String value)
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

	/**
	 * Sends HTTP request to a web server synchronously.
	 *
	 * @return An HTML response from the web server. <br><br>
	 * <b>NOTE: It will return null if 'cancel' method is called during request. </b>
	 *
	 * @throws SocketTimeoutException Occurs when timeout.
	 * @since v1.0.0
	 */
	@Nullable @WorkerThread
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
	 * Cancels the 'request' method. <br><br>
	 * <b> Note: It doesn't terminate request immediately. If you want to know the time canceled,
	 * use cancel(listener). </b>
	 *
	 * @see #cancel(HttpCancelListener)
	 * @since v1.0.0
	 */
	public void cancel()
	{
		cancel(null);
	}

	/**
	 * Cancels the 'request' method.
	 * @param listener Interface for a callback to be invoked when an HTTP request is canceled.
	 * @since v1.0.0
	 */
	public void cancel(@Nullable HttpCancelListener listener)
	{
		cancelListener = listener;
		canceled = true;
	}
}
