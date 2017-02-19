package boxresin.library.androidhttp;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to send HTTP requests.
 * @since v1.0.0
 */
public class HttpLauncher
{
	// HttpRequest list in progress. It will be removed from the list if the task is finished or canceled.
	private static List<HttpRequest> requests = new ArrayList<>();

	/**
	 * Interface for a callback to be invoked when an HTTP request is canceled
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
	 * Interface for a callback to be invoked when an HTTP request is finished
	 * @since v1.0.0
	 */
	public interface HttpResultListener
	{
		/**
		 * A callback method to be invoked when an HTTP request is finished <br><br>
		 * <b>NOTE: This method will be invoked on the UI thread.</b>
		 *
		 * @param response  An HTML response from the web server. <b>It will be null if the
		 *                  specified HttpRequest object can't be launched or is canceled by
		 *                  'cancel' method during request.</b>
		 *
		 * @param exception An exception occurred during request. If there were no exceptions, it
		 *                  will be null. <br>
		 *                  <b>SocketTimeoutException</b> occurs when timeout.
		 * @since v1.0.0
		 */
		@UiThread
		void onHttpResult(@Nullable HttpResponse response, @Nullable Exception exception);
	}

	/**
	 * Sends HTTP request to a web server synchronously.
	 * @param request An HTTP request to launch
	 * @return An HTML response from the web server <br><br>
	 *
	 * <b>NOTE: It will return null if the specified HttpRequest object can't be launched or is
	 * canceled by 'cancel' method during request. </b>
	 *
	 * @throws SocketTimeoutException Occurs when timeout.
	 * @see #launch(HttpRequest, HttpResultListener)
	 * @since v1.0.0
	 */
	@Nullable
	public static HttpResponse launch(HttpRequest request) throws IOException
	{
		if (request.canBeLaunched())
		{
			requests.add(request);
			HttpResponse response = request.request();
			requests.remove(request);
			return response;
		}
		else return null;
	}

	/**
	 * Sends HTTP request to a web server asynchronously. <br>
	 * <b>NOTE: This method must be called on the UI thread.</b>
	 *
	 * @param request An HTTP request to launch
	 * @param listener Interface for a callback to be invoked when an HTTP request is finished
	 * @see #launch(HttpRequest)
	 * @since v1.0.0
	 */
	@UiThread
	public static void launch(HttpRequest request, HttpResultListener listener)
	{
		new LaunchTask(request, listener).execute();
	}

	/**
	 * Cancels an HTTP request. <br><br>
	 * <b> NOTE: It doesn't terminate request immediately. If you want to know the time canceled,
	 * use {@link #cancel(HttpRequest, HttpCancelListener)}. If you called 'request' method several
	 * times previously, all requests would be canceled.</b>
	 *
	 * @param request An HTTP request to cancel
	 *
	 * @see #cancel(HttpRequest, HttpCancelListener)
	 * @since v1.0.0
	 */
	public static boolean cancel(HttpRequest request)
	{
		return cancel(request, null);
	}

	/**
	 * Cancels an HTTP request. <br><br>
	 * <b> NOTE: If you called 'request' method several times previously, all requests would be
	 * canceled.</b>
	 *
	 * @param request An HTTP request to cancel
	 * @param listener Interface for a callback to be invoked when an HTTP request is canceled
	 * @see #cancel(HttpRequest)
	 * @since v1.0.0
	 */
	public static boolean cancel(HttpRequest request, HttpLauncher.HttpCancelListener listener)
	{
		if (requests.contains(request))
			return request.cancel(listener);
		else return false;
	}
}