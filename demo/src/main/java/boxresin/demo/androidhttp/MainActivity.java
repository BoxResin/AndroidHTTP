package boxresin.demo.androidhttp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import boxresin.demo.androidhttp.databinding.ActivityMainBinding;
import boxresin.library.androidhttp.HttpLauncher;
import boxresin.library.androidhttp.HttpRequest;
import boxresin.library.androidhttp.HttpResponse;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;
	private HttpRequest httpRequest;
	private PipedOutputStream pos;
	private InputStreamReader isr;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		try
		{
			handler = new Handler();
			httpRequest = new HttpRequest();
			pos = new PipedOutputStream();
			isr = new InputStreamReader(new PipedInputStream(pos));
		}
		catch (IOException ignored)
		{
		}
	}

	public void onClick(View view)
	{
		if (binding.btnRequest.getText().equals("Cancel"))
		{
			HttpLauncher.cancel(httpRequest, new HttpLauncher.HttpCancelListener()
			{
				@Override
				public void onHttpCancel()
				{
					resetUI();
				}
			});
			binding.btnRequest.setEnabled(false);
			return;
		}

		binding.txtHttpStatus.setText("");
		binding.txtHtml.setText("");
		binding.btnRequest.setText("Cancel");
		binding.loadingBar.setVisibility(View.VISIBLE);

		if (view == binding.btnRequest)
		{
			final String url = binding.editUrl.getText().toString();
			final String method = (String) binding.spinnerHttpMethod.getSelectedItem();

			httpRequest.setUrl(url)
					.setMethod(method);

			final char[] buffer = new char[2048];
			HttpLauncher.launch(httpRequest, new HttpLauncher.HttpTaskListener()
			{

				@Override
				public void onHttpProgress(@NonNull HttpResponse response, @NonNull byte[] partialBody,
				                           int lengthRead, @IntRange(from = 0, to = 100) int progress)
				{
					try
					{
						Log.v("ASDF", "progress: " + progress);
						pos.write(partialBody, 0, lengthRead);
						int length = isr.read(buffer);
						if (length != -1)
						{
							binding.txtHtml.append(new String(buffer, 0, length));
							handler.postDelayed(new Runnable()
							{
								@Override
								public void run()
								{
									binding.scroll.fullScroll(View.FOCUS_DOWN);
								}
							}, 10);
						}
					}
					catch (IOException ignored)
					{
					}
				}

				@Override
				public void onHttpResult(@Nullable HttpResponse response, @Nullable Exception exception)
				{
					resetUI();

					if (response != null)
					{
						binding.txtHttpStatus.setText(String.format("%d %s",
								response.getStatusCode(), response.getStatusMessage()));
						binding.txtHtml.setText(response.getBody());
						binding.btnRequest.setText("Request");
						binding.loadingBar.setVisibility(View.GONE);
						binding.btnRequest.setEnabled(true);
					}

					if (exception != null)
						exception.printStackTrace();
				}
			});
		}
	}

	private void resetUI()
	{
		binding.txtHttpStatus.setText("");
		binding.btnRequest.setText("Request");
		binding.loadingBar.setVisibility(View.GONE);
		binding.btnRequest.setEnabled(true);
	}
}
