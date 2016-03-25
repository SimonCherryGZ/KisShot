package com.simoncherry.kisshot.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.simoncherry.kisshot.R;
import com.simoncherry.kisshot.Utils.ImageAddFrame;

public class AvatarActivity extends Activity {
	private ImageView img_avatar;
	private Bitmap bitmap_avatar;

	private Uri photoUri;

	private final static int PIC_FROM_CAMERA = 1;
	private final static int PIC_FROM_LOCALPHOTO = 0;
	
	private ImageView img_load;
	private Animation img_load_anim;
	
	private Bitmap getImageFromAssets(String fileName)
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
			bitmap_avatar = BitmapFactory.decodeFile(filePath);
			img_avatar.setImageBitmap(bitmap_avatar);
		}else{
			img_avatar.setImageResource(R.drawable.sample);
		}
	}
	
	private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.dialog_avatar_select_title))
				.setPositiveButton(
						getString(R.string.dialog_avatar_btn_camera),
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						doHandlerPhoto(PIC_FROM_CAMERA);
					}
				})
				.setNegativeButton(getString(R.string.dialog_avatar_btn_local),
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						doHandlerPhoto(PIC_FROM_LOCALPHOTO);
					}
				}).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_avatar);
		//MenuActivity.instance.finish();
		
		img_load = (ImageView) findViewById(R.id.img_load);
		img_avatar = (ImageView) findViewById(R.id.img_avatar);
		img_avatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		
		GetAvatar();
		
		img_load_anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_out);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				img_load.startAnimation(img_load_anim);
				img_load.setVisibility(View.GONE);
				MenuActivity.instance.finish();
			}
		}, 3000);
	}
	
	@Override 
	public void onWindowFocusChanged(boolean hasFocus) { 
	    super.onWindowFocusChanged(hasFocus);
	    if(hasFocus){
	    	//MenuActivity.instance.finish();
	    }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {

	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
	    	startActivity(new Intent(AvatarActivity.this, MenuActivity.class));
			overridePendingTransition(R.anim.slide_down_in, R.anim.anim_nothing);
			AvatarActivity.this.finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	private void doHandlerPhoto(int type)
	{
		try
		{
			File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/KisShot/avatar");
			if (!pictureFileDir.exists()) {
				pictureFileDir.mkdirs();
			}
			File picFile = new File(pictureFileDir, "avatar.jpeg");
			if (!picFile.exists()) {
				picFile.createNewFile();
			}
			photoUri = Uri.fromFile(picFile);
			
			if (type==PIC_FROM_LOCALPHOTO)
			{
				Intent intent = getCropImageIntent();
				startActivityForResult(intent, PIC_FROM_LOCALPHOTO);
			}else
			{
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
			}

		} catch (Exception e)
		{
			Log.i("HandlerPicError", "handlerPicError");
		}
	}
	
	public Intent getCropImageIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		setIntentParams(intent);
		return intent;
	}

	private void cropImageUriByTakePhoto() {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		setIntentParams(intent);
		startActivityForResult(intent, PIC_FROM_LOCALPHOTO);
	}

	private void setIntentParams(Intent intent)
	{
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 3);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 450);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case PIC_FROM_CAMERA:
			try 
			{
				cropImageUriByTakePhoto();
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			break;
		case PIC_FROM_LOCALPHOTO:
			try
			{
				if (photoUri != null) 
				{
					Bitmap bitmap = decodeUriAsBitmap(photoUri);
					Bitmap frameBitmap = getImageFromAssets("card_frame.png");
			
					ImageAddFrame imgAdd = new ImageAddFrame();
					Bitmap saveBitmap = imgAdd.AddFrameToImage(bitmap, frameBitmap);
					img_avatar.setImageBitmap(saveBitmap);
					
					String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
					filePath += "/KisShot/avatar/avatar.jpeg";
					FileOutputStream fileOutputStream = new FileOutputStream(filePath);
					saveBitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
					fileOutputStream.flush();
					fileOutputStream.close();
				}
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			break;
		}
	}

	private Bitmap decodeUriAsBitmap(Uri uri)
	{
		Bitmap bitmap = null;
		try 
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inMutable = true;
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null,opts);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
