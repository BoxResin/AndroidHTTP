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
						HttpRequester requester = new HttpRequester();
						requester.setUrl(url);
						requester.setMethod(method);
						final HttpResponse response = requester.request();

						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								binding.txtHttpStatus.setText(String.format("%d %s",
										response.getStatusCode(), response.getStatusMessage()));
								binding.txtHtml.setText(response.getBody());
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
