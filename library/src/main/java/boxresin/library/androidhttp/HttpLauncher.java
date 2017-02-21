package boxresin.library.androidhttp;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
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
		 * @see #cancel(HttpRequest, HttpCancelListener)
		 * @since v1.0.0
		 */
		@UiThread
		void onHttpCancel();
	}

	/**
	 * Interface for an observation to an HTTP request task.
	 * @since v1.0.0
	 */
	public interface HttpTaskListener
	{
		/**
		 * A callback method to be invoked periodically when an HTTP request is in progress
		 * @param response Intermediate response
		 * @param chunk    Partial of response message body newly read
		 * @param progress
		 */
		void onHttpProgress(@NonNull HttpResponse response, @NonNull String chunk, @IntRange(from = 0, to = 100) int progress);

		/**
		 * A callback method to be invoked when an HTTP request is finished <br><br>
		 * <b>NOTE: This method will be invoked on the UI thread.</b>
		 *
		 * @param response  An HTML response from the web server. <b>It will be null if the
		 *                  specified HttpRequest object is canceled by 'cancel()' method during
		 *                  request.</b>
		 *
		 * @param exception An exception occurred during request. If there were no exceptions, it
		 *                  will be null. <br>
		 *                  <b>SocketTimeoutException</b> would be thrown when timeout. <br>
		 *                  <b>IOException</b> would be thrown when a network error occurs.
		 *
		 * @see #launch(HttpRequest, HttpTaskListener)
		 * @since v1.0.0
		 */
		@UiThread
		void onHttpResult(@Nullable HttpResponse response, @Nullable Exception exception);
	}

	/**
	 * Sends HTTP request to a web server synchronously.
	 * @param request An HTTP request to send
	 * @return An HTML response from the web server <br><br>
	 *
	 * <b>NOTE: It will return null if the specified HttpRequest object can't be launched or is
	 * canceled by 'cancel' method during request. </b>
	 *
	 * @throws SocketTimeoutException Occurs when timeout.
	 * @see #launch(HttpRequest, HttpTaskListener)
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
	 * @param request An HTTP request to send
	 * @param listener Interface for a callback to be invoked when an HTTP request is finished
	 * @return true if the specified HttpRequest object can be launched, false otherwise.
	 * @see #launch(HttpRequest)
	 * @see HttpCancelListener
	 * @since v1.0.0
	 */
	@UiThread
	public static boolean launch(HttpRequest request, HttpTaskListener listener)
	{
		if (request.canBeLaunched())
		{
			new LaunchTask(request, listener).execute();
			return true;
		}
		else return false;
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
	 * @see HttpCancelListener
	 * @since v1.0.0
	 */
	public static boolean cancel(HttpRequest request, HttpLauncher.HttpCancelListener listener)
	{
		return requests.contains(request) && request.cancel(listener);
	}
}
