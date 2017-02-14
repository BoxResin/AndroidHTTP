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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
	}

	public void onClick(View view)
	{
		binding.txtHttpStatus.setText("");
		binding.txtHtml.setText("");
		binding.btnRequest.setText("Cancel");

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
						final HttpResponse response = new HttpRequester()
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
