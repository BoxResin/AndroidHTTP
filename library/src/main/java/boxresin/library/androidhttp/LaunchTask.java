package boxresin.library.androidhttp;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * A Task that sends HTTP request asynchronously
 */
final class LaunchTask extends AsyncTask<Object, Object, HttpResponse>
{
	private HttpRequest request;
	private Exception exception;
	private HttpLauncher.HttpResultListener listener;

	LaunchTask(@NonNull HttpRequest request, @Nullable HttpLauncher.HttpResultListener listener)
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
			response = HttpLauncher.launch(request);
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
