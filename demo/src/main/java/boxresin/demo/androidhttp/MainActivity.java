package boxresin.demo.androidhttp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import boxresin.demo.androidhttp.databinding.ActivityMainBinding;
import boxresin.library.androidhttp.HttpRequester;
import boxresin.library.androidhttp.HttpResponse;

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

			httpRequester
					.setUrl(url)
					.setMethod(method)
					.request(new HttpRequester.HttpResultListener()
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
