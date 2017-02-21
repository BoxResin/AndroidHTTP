package boxresin.library.androidhttp;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * An AsyncTask to send HTTP request asynchronously
 */
final class LaunchTask extends AsyncTask<Object, Object, HttpResponse>
{
	private HttpRequest request;
	private Exception exception;
	private HttpLauncher.HttpTaskListener listener;

	LaunchTask(@NonNull HttpRequest request, @Nullable HttpLauncher.HttpTaskListener listener)
	{
		this.request = request;
		this.listener = listener;
	}

	@Override
	protected HttpResponse doInBackground(Object[] params)
	{
		HttpResponse response = null;
		try
		{
			HttpLauncher.requests.add(request);
			response = request.request(listener);
			HttpLauncher.requests.remove(request);
		}
		catch (IOException e)
		{
			exception = e;
		}

		return response;
	}

	@Override
	protected void onPostExecute(HttpResponse response)
	{
		if (listener != null)
			listener.onHttpResult(response, exception);
	}
}
