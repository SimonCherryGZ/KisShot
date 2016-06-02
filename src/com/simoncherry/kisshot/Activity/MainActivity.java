package com.simoncherry.kisshot.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.simoncherry.kisshot.R;
import com.simoncherry.kisshot.Utils.ImageAddFrame;
import com.simoncherry.kisshot.Utils.Rotate3d;

/**
 * <p>
 * KisShot
 * <p/>
 *
 * @author SimonCherry 2016.01.11
 *
 */
//@SuppressWarnings("deprecation")
public class MainActivity extends Activity  {

	private static final String KISS_TAG = "KissLog";
	private boolean isPreviewOK = false ;
	private boolean isReady = false;
	private boolean isCameraOK = false;
	private boolean isRecordOK = false;
	private boolean isShotOK = false;
	private boolean isInitOK = false;
	private boolean isRotate = false;
	private boolean isSelectShot = false;
	private boolean isAvatarExists = false;
	
	final static int MSG_SHOW_VAL = 0x1;
	final static int MSG_INIT_OK = 0x2;
	final static int MSG_IS_VISIBLE = 0x3;
	final static int MSG_CLOSE_DIALOG = 0x4;
	final static int MSG_VIEWSTUB_OK = 0x5;
	final static int MSG_SHOT_OK = 0x6;
	final static int MSG_TRY_SHOT = 0x7;
	final static int MSG_ANIM_END = 0x8;
	
	static final int DIALOG_REQUEST = 0x7;  // The request code
	static final int RESULT_1 = 0x8; 
	static final int RESULT_2 = 0x9; 
	private static double val_power_show = 0.0;
	public static int val_power = 0;
	
	File directory;
	private Camera camera;
	private MediaRecorder mediaRecorder;
	private ImageView img_background;
	private AnimationDrawable anim;
	private TextView text_volume;
	private TextView text_title;
	private static Bitmap img_temp;
	private Bitmap img_avatar;
	private ImageView img_card;
	private ImageView img_card_white;
	private ImageView img_load;
	private ViewGroup mContainer;
	private ViewStub mViewStub_Dialog;
	private ImageView img_dialog;
	private TextView text_dialog;
	private Button btn_dialog1;
	private Button btn_dialog2;
	private ViewStub mViewStub_Main;
	
	private static final int IMAGE1 = R.drawable.contrary;
	private static final int IMAGE2 = R.drawable.forward;
	private static final int IMAGE3 = R.drawable.sample; 

	private Animation tween_alpha;
	private Animation img_load_anim;
	
	myThread mythread = new myThread();
	
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what)
			{
				case MSG_IS_VISIBLE:
					break;
				case MSG_INIT_OK:
					inflatedViewStub();
					text_title.setVisibility(View.VISIBLE);
					break;
				case MSG_VIEWSTUB_OK:
					img_background.setBackgroundResource(R.anim.frame2);
					anim = (AnimationDrawable) img_background.getBackground();
					mythread.start();
					MenuActivity.instance.finish();
					img_load.startAnimation(img_load_anim);
					img_load.setVisibility(View.GONE);
					break;
				case MSG_ANIM_END:
					img_dialog.setVisibility(View.VISIBLE);
					text_dialog.setVisibility(View.VISIBLE);
					btn_dialog1.setVisibility(View.VISIBLE);
					btn_dialog2.setVisibility(View.VISIBLE);
					//img_load.setVisibility(View.GONE);
					text_title.setText(R.string.title_text4);
					break;
				case MSG_CLOSE_DIALOG:
					img_background.setVisibility(View.VISIBLE);
					img_card.setVisibility(View.VISIBLE);
					text_volume.setVisibility(View.VISIBLE);
					mViewStub_Dialog.setVisibility(View.GONE);
					text_title.setText(R.string.title_text1);
					
					img_background.setBackgroundResource(R.anim.frame);
					anim = (AnimationDrawable) img_background.getBackground();
					anim.start();
					isReady = true;
					break;
				case MSG_TRY_SHOT:			
					new Thread(PhotoProcessRunnable).start();
					break;
				case MSG_SHOT_OK:
					isPreviewOK = false;
					Toast.makeText(getApplicationContext(), "Shot!", Toast.LENGTH_LONG).show();
					text_title.setText(R.string.title_text5);
					img_card.clearAnimation();
					img_card_white.setVisibility(View.GONE);
					break;
				case MSG_SHOW_VAL:
					text_volume.setText(String.valueOf(val_power));
					break;
			}
		}

	};
//=============================================================================
	public void findWidgt(){
		img_load = (ImageView)findViewById(R.id.img_load);
		mViewStub_Main = (ViewStub)this.findViewById(R.id.viewstub_main);
		mViewStub_Dialog = (ViewStub)this.findViewById(R.id.viewstub_dialog);
	}
	
	public void inflatedViewStub(){
		View mInflated_main = mViewStub_Main.inflate();
		img_background = (ImageView)mInflated_main.findViewById(R.id.img_background);
		mContainer = (ViewGroup)mInflated_main.findViewById(R.id.fl_card);
		img_card = (ImageView)mInflated_main.findViewById(R.id.img_card);
		img_card_white = (ImageView)mInflated_main.findViewById(R.id.img_card_white);
		text_volume = (TextView)mInflated_main.findViewById(R.id.text_volume);
		text_title = (TextView)mInflated_main.findViewById(R.id.text_title);
		mViewStub_Main.setVisibility(View.VISIBLE);
		
		View mInflated_dialog = mViewStub_Dialog.inflate();
		img_dialog = (ImageView)mInflated_dialog.findViewById(R.id.img_dialog);
		text_dialog = (TextView)mInflated_dialog.findViewById(R.id.text_dialog);
		btn_dialog1 = (Button)mInflated_dialog.findViewById(R.id.btn_dialog1);
		btn_dialog2 = (Button)mInflated_dialog.findViewById(R.id.btn_dialog2);
		mViewStub_Dialog.setVisibility(View.VISIBLE); 
		
		bindWidgt();
		
		myHandler.sendEmptyMessage(MSG_VIEWSTUB_OK);
	}
	
	public void bindWidgt(){
		img_card.setClickable(true);  
        img_card.setFocusable(true);  
		img_card.setOnClickListener(new OnClickListener() {	   	
			@Override
			public void onClick(View v) {
				if(!isRotate){
					applyRotation(0,0,-90);
				}
				else{
					applyRotation(0,0,90);
				}
			}
		});
		
		btn_dialog1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				text_dialog.setText(R.string.dialog_text2);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						text_title.setText(R.string.title_text2);
						isSelectShot = false;
						myHandler.sendEmptyMessage(MSG_CLOSE_DIALOG);
					}
				}, 1000);
			}
		});
		
		btn_dialog2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				text_dialog.setText(R.string.dialog_text2);  
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						text_title.setText(R.string.title_text3);
						isSelectShot = true;
						myHandler.sendEmptyMessage(MSG_CLOSE_DIALOG);
					}
				}, 1000);
			}
		});
	}
	
	public Bitmap getImageFromAssets(String fileName)
	{
		Bitmap imageAssets = null;
		AssetManager am = getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			imageAssets = BitmapFactory.decodeStream(is);
			is.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return imageAssets;
	}
	
	public void GetAvatar(){
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		filePath += "/KisShot/avatar/avatar.jpeg";
		File myAvatarFile = new File(filePath);
		if(myAvatarFile.exists())
		{
			img_avatar = BitmapFactory.decodeFile(filePath);
			isAvatarExists = true;
		}
	}
	
	public void InitSys(){	
		OpenRecorder();	
		openCamera();
		GetAvatar();
		
		tween_alpha = new AlphaAnimation(0.1f, 1.0f);
		tween_alpha.setDuration(500);
		tween_alpha.setRepeatMode(Animation.REVERSE);
		tween_alpha.setRepeatCount(Animation.INFINITE);
		
		img_load_anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_out);
		img_load_anim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				myHandler.sendEmptyMessage(MSG_ANIM_END);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	public class InitThread implements Runnable {
		@Override
		public void run() {
			try{
				InitSys();
				while(isInitOK == false)
				{
					if (isRecordOK && isCameraOK)
					{
						isInitOK = true;
						myHandler.sendEmptyMessage(MSG_INIT_OK);
					}
				}
			}catch (Exception e) {
				Log.e(KISS_TAG, "catch");
			}
		}
	}
	
	public void ReleaseResource(){
		if (mediaRecorder!=null)//(directory != null && directory.exists()) 
		{
		    mediaRecorder.stop();
		    mediaRecorder.release();
		    mediaRecorder = null;
		    
		}
		if(mythread != null)
		{
			isInitOK = false;
			mythread = null;
		}
		
		releaseCamera();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) { 
		super.onWindowFocusChanged(hasFocus);
		myHandler.sendEmptyMessage(MSG_IS_VISIBLE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == DIALOG_REQUEST) {
	        // Make sure the request was successful 	
	        if (resultCode == RESULT_1) {
	        	isSelectShot = false;
	        	myHandler.sendEmptyMessage(MSG_CLOSE_DIALOG);
	        }else if(resultCode == RESULT_2){
	        	isSelectShot = true;
	        	myHandler.sendEmptyMessage(MSG_CLOSE_DIALOG);
	        }
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findWidgt();
		//img_load_anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_out);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Thread(new InitThread()).start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		ReleaseResource();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
	    	startActivity(new Intent(MainActivity.this, MenuActivity.class));
			overridePendingTransition(R.anim.slide_down_in, R.anim.anim_nothing);
			MainActivity.this.finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}

//=============================================================================
	public void OpenRecorder()
	{
		mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		path += "/KisShot/record.amr";
        mediaRecorder.setOutputFile(path); 
        try {
	        mediaRecorder.prepare();
	        mediaRecorder.start();
	        
	        isRecordOK = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void openCamera(){

		Camera.CameraInfo cameraInfo = new CameraInfo() ;

		int count = Camera.getNumberOfCameras() ; 

		for (int i = 0; i < count; i++) {
			Camera.getCameraInfo(i, cameraInfo); 
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					camera = Camera.open(i) ;
				} catch (Exception e) {
					System.out.println("front camera open failed"+e.toString());
					e.printStackTrace();
				}
			}
		}

		if (camera == null) {
			for (int i = 0; i < count; i++) {
				Camera.getCameraInfo(i, cameraInfo); 
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					try {
						camera = Camera.open(i) ;
					} catch (Exception e) {
						System.out.println("back camera open failed"+e.toString());
						e.printStackTrace();
					}
				}
			}
		}

		try{
			if(camera != null){
				camera.startPreview();
				isCameraOK = true;
			} else {
				Toast.makeText(getApplicationContext(), "no camera", Toast.LENGTH_LONG).show();
			}	
		} catch (Exception e){ 
			e.printStackTrace();
		}
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			System.out.println("auto focus");

			if(success && camera!=null){

				camera.takePicture(null,null,new PictureCallback() {

					@SuppressLint("SdCardPath")
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						
						String name = "temp_pic.jpeg";
						File file = new File("/sdcard/KisShot/pic/");
						file.mkdirs();
						String filename=file.getPath()+File.separator+name;
						
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length) ;
						Matrix m = new Matrix();
						m.setRotate(270, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
						
						try {

							FileOutputStream fileOutputStream = new FileOutputStream(filename);
							boolean b = bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
							fileOutputStream.flush();
							fileOutputStream.close();

							if (b) {
								myHandler.sendEmptyMessage(MSG_TRY_SHOT);
							}else {
								Toast.makeText(getApplicationContext(), "take photo failed", Toast.LENGTH_LONG).show();
							}

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}finally{
							releaseCamera();	
						}
					}
				});
				
			}
		}
	};
	
	private void releaseCamera() {
		if (camera != null) {
			try {
				camera.setPreviewDisplay(null);
				camera.stopPreview();
				camera.release();
				camera = null;
				isPreviewOK = false;
				isReady = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	void setDialogImage(float f){
		if (f < 200.0) {
			val_power = 0;
		}else if (f > 200.0 && f < 400) {
			val_power = 1;
		}else if (f > 400.0 && f < 800) {
			val_power = 2;
		}else if (f > 800.0 && f < 1600) {
			val_power = 3;
		}else if (f > 1600.0 && f < 3200) {
			val_power = 4;
		}else if (f > 3200.0 && f < 5000) {
			val_power = 5;
		}else if (f > 5000.0 && f < 7000) {
			val_power = 6;
		}else if (f > 7000.0 && f < 10000.0) {
			val_power = 7;
		}else if (f > 10000.0 && f < 14000.0) {
			val_power = 8;
		}else if (f > 14000.0 && f < 17000.0) {
			val_power = 9;
		}else if (f > 17000.0 && f < 20000.0) {
			val_power = 10;
		}else if (f > 20000.0 && f < 24000.0) {
			val_power = 11;
		}else if (f > 24000.0 && f < 28000.0) {
			val_power = 12;
		}else if (f > 28000.0) {
			val_power = 13;
			if (isReady)
			{	
				isPreviewOK = true;
				
				if(camera != null)
				{
					camera.autoFocus(autoFocusCallback);
				}
				
				if(anim != null)
				{
					anim.stop();
				}
				img_background.setBackgroundResource(R.anim.frame2);
				anim = (AnimationDrawable) img_background.getBackground();
				anim.start();
				/*
				tween_alpha = new AlphaAnimation(0.1f, 1.0f);
				tween_alpha.setDuration(500);
				tween_alpha.setRepeatMode(Animation.REVERSE);
				tween_alpha.setRepeatCount(Animation.INFINITE);
				*/
				img_card_white.setVisibility(View.VISIBLE);
				img_card.startAnimation(tween_alpha);
				
				isReady = false;
			}
		}
	}
	
	public Runnable PhotoProcessRunnable = new Runnable() {
		public void run()
		{		
			String fileName = DateFormat.format("yyyy_MM_dd_hhmmss",Calendar.getInstance(Locale.CHINA))+ ".jpeg";
			String filePathName = Environment.getExternalStorageDirectory().getAbsolutePath();
			filePathName += "/KisShot/pic/";
			File filePath = new File(filePathName);
			if(!filePath.exists())
			{
				filePath.mkdirs();
			}
			File PhotoFile = new File(filePath, fileName);
			
			String tempFileName = "temp_pic.jpeg";
			File TempFile = new File(filePath, tempFileName);
			Bitmap bitmap = null;
			
			if(TempFile.exists()){
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inMutable = true;
				bitmap = BitmapFactory.decodeFile(filePathName + tempFileName, opts);
				Bitmap frameBitmap = getImageFromAssets("card_frame.png");
				ImageAddFrame imgAdd = new ImageAddFrame();
				bitmap = imgAdd.AddFrameToImage(bitmap, frameBitmap);
				img_temp = bitmap;
				
				TempFile.delete();
			}
			
			try{
					FileOutputStream fileOutputStream = new FileOutputStream(PhotoFile);
					boolean b = bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
					fileOutputStream.flush();
					fileOutputStream.close();
					
					if (b) {
						isShotOK = true;
						myHandler.sendEmptyMessage(MSG_SHOT_OK);
					}else {
						isShotOK = false;
					}	
				
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}

		}
	};
	
	class myThread extends Thread
	{
		public void run()
		{
			while(isInitOK)
			{
				try
				{
					sleep(200);
					
					myHandler.post(new Runnable()
					{
						public void run()
						{
							Log.v("i", String.valueOf(val_power_show));
							if(mediaRecorder !=null && isPreviewOK !=true)
							{
								setDialogImage(mediaRecorder.getMaxAmplitude());
							}
							
							myHandler.sendEmptyMessage(MSG_SHOW_VAL);
						}
					});
					
					
				}catch(Exception e)
				{
					Log.e("e", "e");
				}
			}
			
		}
	}
//==============================================================================
	 private void applyRotation(int position, float start, float end) {  
        // Find the center of the container  
        final float centerX = mContainer.getWidth() / 2.0f;  
        final float centerY = mContainer.getHeight() / 2.0f;  
        final Rotate3d rotation =  
                new Rotate3d(start, end, centerX, centerY, 310.0f, true);  
        rotation.setDuration(1000);
        rotation.setFillAfter(true);  
        rotation.setInterpolator(new AccelerateInterpolator());  
        rotation.setAnimationListener(new DisplayNextView());  
        mContainer.startAnimation(rotation);
	}      

	private final class DisplayNextView implements Animation.AnimationListener {  
        private DisplayNextView() {  
        }  
        public void onAnimationStart(Animation animation) {  
        }  
        public void onAnimationEnd(Animation animation) {  
        	if(!isRotate)
        	{
        		if (isShotOK == false)
        		{
        			img_card.setImageResource(IMAGE2);
        		}
        		else
        		{
        			if(isSelectShot == true)
        			{
        				img_card.setImageBitmap(img_temp);
        			}
        			else
        			{
        				if(isAvatarExists == true)
        				{
        					img_card.setImageBitmap(img_avatar);
        				}
        				else
        				{
        					img_card.setImageResource(IMAGE3);
        				}
        			}
        		}
        		 mContainer.post(new SwapViews(0));  
        	}
        	else
        	{
        		img_card.setImageResource(IMAGE1);
        		 mContainer.post(new SwapViews(1));  
        	} 
        }  
        public void onAnimationRepeat(Animation animation) {  
        }  
    }  

    private final class SwapViews implements Runnable {  
        private final int mdirection;
        public SwapViews(int direction) {  
            mdirection=direction;
        }  
        public void run() {  
            final float centerX = mContainer.getWidth() / 2.0f;  
            final float centerY = mContainer.getHeight() / 2.0f;  
            Rotate3d rotation;             
            if(mdirection == 0)
            {
            	rotation = new Rotate3d(90, 0, centerX, centerY, 310.0f, false);
            	isRotate = true;
            }
            else
            {
            	rotation = new Rotate3d(-90, 0, centerX, centerY, 310.0f, false);
            	isRotate = false;
            }
            rotation.setDuration(1000);  
            rotation.setFillAfter(true);  
            rotation.setInterpolator(new DecelerateInterpolator());  
            mContainer.startAnimation(rotation);
        }  
    }
	
}

