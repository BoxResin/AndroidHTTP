package boxresin.library.androidhttp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

import boxresin.library.androidhttp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;
	private HttpRequester httpRequester;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		httpRequester = new HttpRequester();
	}

	public void onClick(View view)
	{
		if (binding.btnRequest.getText().equals("Cancel"))
		{
			httpRequester.cancel(new HttpRequester.HttpCancelListener()
			{
				@Override
				public void onHttpCancel()
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							binding.btnRequest.setText("Request");
							binding.loadingBar.setVisibility(View.GONE);
							binding.btnRequest.setEnabled(true);
						}
					});
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

			new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						final HttpResponse response = httpRequester
								.setUrl(url)
								.setMethod(method)
								.request();

						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if (response != null)
								{
									binding.txtHttpStatus.setText(String.format("%d %s",
											response.getStatusCode(), response.getStatusMessage()));
									binding.txtHtml.setText(response.getBody());
									binding.btnRequest.setText("Request");
									binding.loadingBar.setVisibility(View.GONE);
									binding.btnRequest.setEnabled(true);
								}
							}
						});
					}
					catch (IOException ignored)
					{
						ignored.printStackTrace();
					}
				}
			}.start();
		}
	}
}
