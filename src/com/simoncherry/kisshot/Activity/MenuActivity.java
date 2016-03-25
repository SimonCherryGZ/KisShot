package com.simoncherry.kisshot.Activity;

import com.simoncherry.kisshot.R;
import com.simoncherry.kisshot.Activity.MenuActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MenuActivity extends Activity {
	private Button LoginBtn;
	private Button OptionBtn;
	private Button GalleryBtn;
	private ImageView img_load;
	private Animation img_load_anim;
	
	public static MenuActivity instance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_menu);
		
		instance = this;
		
		LoginBtn = (Button) findViewById(R.id.login_btn);
		OptionBtn = (Button) findViewById(R.id.option_btn);
		GalleryBtn = (Button) findViewById(R.id.gallery_btn);
		img_load = (ImageView) findViewById(R.id.img_load);
		
		LoginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, MainActivity.class));
				overridePendingTransition(R.anim.slide_down_in, R.anim.anim_nothing);
			}
		});
		
		OptionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, GuideActivity.class));
				//overridePendingTransition(R.anim.slide_down_in, R.anim.anim_nothing);
			}
		});
		
		GalleryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, GalleryActivity.class));
				overridePendingTransition(R.anim.slide_down_in, R.anim.anim_nothing);
			}
		});
		
		img_load_anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_out);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				img_load.startAnimation(img_load_anim);
				img_load.setVisibility(View.GONE);
			}
		}, 2000);
	}

}
