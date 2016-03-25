package com.simoncherry.kisshot.Activity;

import com.simoncherry.kisshot.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SplashActivity.this,
						MenuActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_down_in, R.anim.anim_nothing);
				SplashActivity.this.finish();
			}
		}, 1000);
	}

}
