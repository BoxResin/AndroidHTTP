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
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class representing HTTP request.
 * @since v1.0.0
 */
public class HttpRequest
{
	// @formatter:off
	private String  url    = "";    // URL to requst
	private String  method = "";    // HTTP method
	private int     connectTimeout; // Timeout when connecting to a web server, in milliseconds
	private int     readTimeout;    // Timeout when reading an HTTP response from a web server, in milliseconds
	private boolean canceled;       // Whether request is canceled or not
	private boolean launching;      // Whether request is in progress or not
	private boolean waiting;        // Whether some thread is blocked by 'waitUntilLaunchable()' method

	private Map<String, String> params  = new TreeMap<>(); // Map that contains POST parameters
	private Map<String, String> headers = new TreeMap<>(); // Map that contains request headers
	// @formatter:on

	private HttpLauncher.HttpCancelListener cancelListener;
	private Handler handler;

	/**
	 * <b>NOTE: This constructor must only be called on the UI thread.</b>
	 * @since v1.0.0
	 */
	@UiThread
	public HttpRequest()
	{
		handler = new Handler();
	}

	/**
	 * Returns the URL to request.
	 * @return The URL to request
	 * @since v1.0.0
	 */
	@NonNull
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
	@NonNull
	public String getMethod()
	{
		return method;
	}

	/**
	 * Sets HTTP method.
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

	/**
	 * Adds a header for request.
	 * @since v1.0.0
	 */
	public HttpRequest addHeader(@NonNull String key, @NonNull String value)
	{
		headers.put(key, value);
		return this;
	}

	/**
	 * Clears all of headers for request.
	 */
	public void clearHeaders()
	{
		headers.clear();
	}

	/**
	 * Returns whether HttpRequest is ready to be launched. If it returns false, you should not
	 * launch that HttpRequest object.
	 *
	 * @return Whether HttpRequest is ready to be launched
	 * @see #waitUntilLaunchable()
	 * @since v1.0.0
	 */
	public boolean canBeLaunched()
	{
		return !launching;
	}

	/**
	 * Blocks current thread until this HttpRequest object can be launched. <br>
	 *
	 * <b>NOTE: Do not use this method on a thread where {@link HttpLauncher#launch(HttpRequest)}
	 * called. <br>Do not invoke it on the UI thread.</b>
	 *
	 * @see #canBeLaunched()
	 * @since v1.0.0
	 */
	@WorkerThread
	public void waitUntilLaunchable() throws InterruptedException
	{
		if (launching)
		{
			waiting = true;
			wait();
		}
	}

	@Nullable
	HttpResponse request() throws IOException
	{
		launching = true;

		// Set options.
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod(method);
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);

		// Add headers.
		for (Map.Entry<String, String> entry : headers.entrySet())
			connection.setRequestProperty(entry.getKey(), entry.getValue());

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

		connection.connect();

		// Read HTTP status.
		int statusCode = connection.getResponseCode();
		String statusMessage = connection.getResponseMessage();

		// Read response headers.
		Map<String, List<String>> headerFields = connection.getHeaderFields();

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
				connection.disconnect();
				canceled = false;
				launching = false;
				if (waiting)
				{
					waiting = false;
					notifyAll();
				}

				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						if (cancelListener != null)
							cancelListener.onHttpCancel();
						cancelListener = null;
					}
				});
				return null;
			}
		}

		connection.disconnect();
		launching = false;
		if (waiting)
		{
			waiting = false;
			notifyAll();
		}
		return new HttpResponse(statusCode, statusMessage, bufferStream, connection);
	}

	boolean cancel(@Nullable HttpLauncher.HttpCancelListener cancelListener)
	{
		// If it hasn't been launched, 'cancel()' method will be failed always.
		if (!launching)
			return false;

		// Otherwise, 'cancel()' succeeds.
		this.cancelListener = cancelListener;
		canceled = true;
		return true;
	}
}
