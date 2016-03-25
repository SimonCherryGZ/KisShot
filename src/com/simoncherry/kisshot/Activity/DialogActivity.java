package com.simoncherry.kisshot.Activity;

import com.simoncherry.kisshot.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogActivity extends Activity {
	private Button btn_select1;
	private Button btn_select2;
	private TextView text_dialog;
	static final int DIALOG_REQUEST = 7;  // The request code
	static final int RESULT_1 = 8; 
	static final int RESULT_2 = 9; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_dialog);
		
		btn_select1 = (Button)findViewById(R.id.btn_dialog1);
		btn_select2 = (Button)findViewById(R.id.btn_dialog2);
		text_dialog = (TextView)findViewById(R.id.textView1);
		
		btn_select1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				text_dialog.setText(R.string.dialog_text2);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						DialogActivity.this.setResult(RESULT_1);
						DialogActivity.this.finish();
					}
				}, 2000);
			}
		});
		
		btn_select2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				text_dialog.setText(R.string.dialog_text2);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						DialogActivity.this.setResult(RESULT_2);
						DialogActivity.this.finish();
					}
				}, 2000);
			}
		});
	}
}
