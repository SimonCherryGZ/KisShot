package com.simoncherry.kisshot.Activity;

import com.simoncherry.kisshot.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GuideActivity extends Activity {
	private ImageButton Btn_Left;
	private ImageButton Btn_Right;
	private ImageView Img_Tutorial;
	private TextView Text_Tutorial;
	private TextView Text_Index;
	
	int show_index = 1;
	
	private void LoadTutorial(int index)
	{
		switch(index)
		{
			case 1:
				Img_Tutorial.setImageResource(R.drawable.tutorial_1);
				Text_Tutorial.setText(R.string.tutorial_text1);
				Text_Index.setText(R.string.tutorial_index1);
				break;
			case 2:
				Img_Tutorial.setImageResource(R.drawable.tutorial_2);
				Text_Tutorial.setText(R.string.tutorial_text2);
				Text_Index.setText(R.string.tutorial_index2);break;
			case 3:
				Img_Tutorial.setImageResource(R.drawable.tutorial_3);
				Text_Tutorial.setText(R.string.tutorial_text3);
				Text_Index.setText(R.string.tutorial_index3);break;
			case 4:
				Img_Tutorial.setImageResource(R.drawable.tutorial_4);
				Text_Tutorial.setText(R.string.tutorial_text4);
				Text_Index.setText(R.string.tutorial_index4);break;
			case 5:
				Img_Tutorial.setImageResource(R.drawable.tutorial_5);
				Text_Tutorial.setText(R.string.tutorial_text5);
				Text_Index.setText(R.string.tutorial_index5);break;
			case 6:
				Img_Tutorial.setImageResource(R.drawable.tutorial_6);
				Text_Tutorial.setText(R.string.tutorial_text6);
				Text_Index.setText(R.string.tutorial_index6);break;
			case 7:
				Img_Tutorial.setImageResource(R.drawable.tutorial_7);
				Text_Tutorial.setText(R.string.tutorial_text7);
				Text_Index.setText(R.string.tutorial_index7);break;
			case 8:
				Img_Tutorial.setImageResource(R.drawable.tutorial_8);
				Text_Tutorial.setText(R.string.tutorial_text8);
				Text_Index.setText(R.string.tutorial_index8);break;
			case 9:
				Img_Tutorial.setImageResource(R.drawable.tutorial_9);
				Text_Tutorial.setText(R.string.tutorial_text9);
				Text_Index.setText(R.string.tutorial_index9);break;
			default:break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);
		
		Btn_Left = (ImageButton) findViewById(R.id.btn_left);
		Btn_Right = (ImageButton) findViewById(R.id.btn_right);
		Img_Tutorial = (ImageView)findViewById(R.id.img_tutorial);
		Text_Tutorial = (TextView)findViewById(R.id.text_tutorial);
		Text_Index = (TextView)findViewById(R.id.text_index);
		
		Btn_Left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(show_index > 1)
				{
					show_index--;
				}
				else
				{
					show_index = 9;
				}
				LoadTutorial(show_index);
			}
		});
		
		Btn_Right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(show_index < 9)
				{
					show_index++;
				}
				else
				{
					show_index = 1;
				}
				LoadTutorial(show_index);
			}
		});
		
		Img_Tutorial.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(show_index == 9)
				{
					//Text_Tutorial.setText(R.string.tutorial_text6);
					startActivity(new Intent(GuideActivity.this, AvatarActivity.class));
					overridePendingTransition(R.anim.slide_down_in, R.anim.anim_nothing);
					GuideActivity.this.finish();
				}
			}
			
		});
	}
}
