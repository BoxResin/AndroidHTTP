package boxresin.demo.androidhttp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import boxresin.demo.androidhttp.databinding.ActivityMainBinding;
import boxresin.library.androidhttp.HttpLauncher;
import boxresin.library.androidhttp.HttpRequest;
import boxresin.library.androidhttp.HttpResponse;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;
	private HttpRequest httpRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		httpRequest = new HttpRequest();
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

			HttpLauncher.launch(httpRequest, new HttpLauncher.HttpTaskListener()
			{
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
