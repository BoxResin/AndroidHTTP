package boxresin.library.androidhttp;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * A Task that sends HTTP request asynchronously
 */
final class RequestTask extends AsyncTask<Object, Object, HttpResponse>
{
	private HttpRequester requester;
	private Exception exception;
	private HttpRequester.HttpResultListener listener;

	RequestTask(@NonNull HttpRequester requester, @Nullable HttpRequester.HttpResultListener listener)
	{
		this.requester = requester;
		this.listener = listener;
	}

	@Override
	protected HttpResponse doInBackground(Object[] params)
	{
		HttpResponse response = null;
		try
		{
			response = requester.request();
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
