package com.simoncherry.kisshot.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.simoncherry.kisshot.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class GalleryActivity extends Activity implements OnScrollListener, OnGestureListener{
	GestureDetector detector;
	String DEBUG_TAG = "TEST";
	private ViewFlipper viewflipper;
	private GridView gridView;
	private TextView tv_gallery_title;
	private TextView tv_gallery_empty;
	private ImageView img_load;
	private Animation img_load_anim;
	private List<String>mList = null;
	public static Map<String,Bitmap>gridViewBitmapCaches = new HashMap<String,Bitmap>();
	private MyGridViewAdapter adapter = null;
	private int ListSize = 0;
	private int currentPos = 0;
	final int View_A = 0;
	final int View_B = 1;
	final int View_C = 2;
	private int View_Del_L = View_C;
	private int View_Del_R = View_A; 
	private int View_Show = View_B;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gallery);
		
		detector = new GestureDetector(this);
		
		viewflipper = (ViewFlipper)findViewById(R.id.viewflipper);
		gridView = (GridView)findViewById(R.id.gridview_gallery);
		tv_gallery_title = (TextView)findViewById(R.id.tv_gallery_title);
		tv_gallery_empty = (TextView)findViewById(R.id.tv_gallery_empty);
		img_load = (ImageView)findViewById(R.id.img_load);
		
		initData();
		setAdapter();
		
		img_load_anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_out);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				img_load.startAnimation(img_load_anim);
				img_load.setVisibility(View.GONE);
				MenuActivity.instance.finish();
			}
		}, 2000);
	}
	
	private View getImageView(Bitmap bitmap){
	     ImageView imgView = new ImageView(this);
	     imgView.setImageBitmap(bitmap);
	     return imgView;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			if(viewflipper.getVisibility() == View.VISIBLE){
				
				//viewflipper.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_exit));
				viewflipper.setVisibility(View.INVISIBLE);
				gridView.setVisibility(View.VISIBLE);
				viewflipper.removeAllViews();
				tv_gallery_title.setText(R.string.tv_gallery_title);
				return true;
			}
			else{
				startActivity(new Intent(GalleryActivity.this, MenuActivity.class));
				overridePendingTransition(R.anim.slide_down_in, R.anim.anim_nothing);
				GalleryActivity.this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean checkIsImageFiles(String fName){
		boolean isImageFile = false;
		String fileEnd = fName.substring(fName.lastIndexOf(".")+1, fName.length()).toLowerCase(Locale.getDefault());
		if(fileEnd.equals("jpg")||fileEnd.equals("png")||fileEnd.equals("gif")||fileEnd.equals("bmp")||fileEnd.equals("jpeg")){
			isImageFile = true;
		}else{
			isImageFile = false;
		}
		return isImageFile;
	}
	
	private void initData(){
		mList = new ArrayList<String>();
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		filePath += "/KisShot/pic";
		File picFileDir = new File(filePath);
		if(!picFileDir.exists()){
			picFileDir.mkdirs();
		}
		File[] picFiles = picFileDir.listFiles();
		
		for(int i=0; i<picFiles.length; i++){
			File file = picFiles[i];
			if(checkIsImageFiles(file.getPath())){
				mList.add(file.getPath()+ "/");
			}
		}
		
		if(mList.size() > 0){
			tv_gallery_empty.setVisibility(View.INVISIBLE);
		}else{
			tv_gallery_empty.setVisibility(View.VISIBLE);
		}
	}
	
	private void setAdapter(){
		adapter = new MyGridViewAdapter(this,mList);
		gridView.setAdapter(adapter);
		ListSize = mList.size();
		gridView.setOnScrollListener((OnScrollListener)this);
		gridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Log.v(DEBUG_TAG, (mList.get(position).toString()));
				//Log.v(DEBUG_TAG, String.valueOf(position));
				currentPos = position;
				
				if((position > 0) && (position < (ListSize-1))){
					viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position-1))),View_A);
					viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position))),View_B);
					viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position+1))),View_C);
				}else if(position == 0){
					viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position))),View_A);
					viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position))),View_B);
					if(ListSize > 1){
						viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position+1))),View_C);
					}
				}else if(position == (ListSize-1)){
					if(ListSize > 1){
						viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position-1))),View_A);
					}
					viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position))),View_B);
					viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(position))),View_C);
				}
				
				viewflipper.setInAnimation(AnimationUtils.loadAnimation(GalleryActivity.this, R.anim.zoom_enter));
				viewflipper.setDisplayedChild(View_B);
				View_Del_L = View_C;
				View_Del_R = View_A;
				
				gridView.setVisibility(View.INVISIBLE);
				viewflipper.setVisibility(View.VISIBLE);
				tv_gallery_title.setText("No." + String.valueOf(currentPos+1));
			};
		});	
	}
	
	private void recycleBitmapCaches(int fromPosition, int toPosition){
		Bitmap delBitmap = null;
		for(int del=fromPosition; del<toPosition; del++){
			delBitmap = gridViewBitmapCaches.get(mList.get(del));

			if(delBitmap != null){
				gridViewBitmapCaches.remove(mList.get(del));
				delBitmap.recycle();
				delBitmap = null;
			}
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		recycleBitmapCaches(0, firstVisibleItem);
		recycleBitmapCaches(firstVisibleItem+visibleItemCount, totalItemCount);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){
		// TODO Auto-generated method stub
	}
	
	

	public class MyGridViewAdapter extends BaseAdapter{

		private LayoutInflater mLayoutInflater = null;
		private List<String>mList = null;
		private int width = 105;
		private int height = 140;
		
		public class MyGridViewHolder{
			public ImageView imageview_thumbnail;
			public TextView textview_test;
		}
		
		public MyGridViewAdapter(Context context, List<String>list){
			this.mList = list;
			mLayoutInflater = LayoutInflater.from(context);		
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyGridViewHolder viewHolder = null;
			if(convertView == null)
			{
				viewHolder = new MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.layout_gallery_item, null);
				viewHolder.imageview_thumbnail = (ImageView)convertView.findViewById(R.id.imageview_thumbnail);
				viewHolder.textview_test = (TextView)convertView.findViewById(R.id.textview_test);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (MyGridViewHolder)convertView.getTag();
			}
			
			String url = mList.get(position);

			if(cancelPotentialLoad(url, viewHolder.imageview_thumbnail)){
				AsyncLoadImageTask task = new AsyncLoadImageTask(viewHolder.imageview_thumbnail);
				LoadedDrawable loadedDrawable = new LoadedDrawable(task);
				
				viewHolder.imageview_thumbnail.setImageDrawable(loadedDrawable);
				task.execute(position);
			}
			//viewHolder.textview_test.setText((position+1)+"");
			return convertView;
		}
		
		private Bitmap getBitmapFromUrl(String url){
			Bitmap bitmap = null;
			bitmap = GalleryActivity.gridViewBitmapCaches.get(url);
			if(bitmap != null){
				return bitmap;
			}

			url = url.substring(0, url.lastIndexOf("/"));
			try{
				FileInputStream is = new FileInputStream(url);
				bitmap = BitmapFactory.decodeFileDescriptor(is.getFD());
				//added by simon
				is.close(); 
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			
			bitmap = BitmapUtilities.getBitmapThumbnail(bitmap, width, height);
			return bitmap;
		}

		private class AsyncLoadImageTask extends AsyncTask<Integer,Void,Bitmap>{
			private String url = null;
			private final WeakReference<ImageView>imageViewReference;
			
			public AsyncLoadImageTask(ImageView imageview){
				super();
				imageViewReference = new WeakReference<ImageView>(imageview);
			}
			
			protected Bitmap doInBackground(Integer...params){
				Bitmap bitmap = null;
				this.url = mList.get(params[0]);
				bitmap = getBitmapFromUrl(url);
				
				GalleryActivity.gridViewBitmapCaches.put(mList.get(params[0]), bitmap);
				return bitmap;
			}
			
			protected void onPostExecute(Bitmap resultBitmap){
				if(isCancelled()){
					resultBitmap = null;
				}
				
				if(imageViewReference != null){
					ImageView imageview = imageViewReference.get();
					AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
					if(this == loadImageTask)
					{
						imageview.setImageBitmap(resultBitmap);
						//imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
						imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
						//imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
					}
				}
				super.onPostExecute(resultBitmap);
			}
		}
		
		private boolean cancelPotentialLoad(String url, ImageView imageview){
			AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
			if(loadImageTask != null){
				String bitmapUrl = loadImageTask.url;
				if((bitmapUrl == null)||(!bitmapUrl.equals(url))){
					loadImageTask.cancel(true);
				}else{
					return false;
				}
			}
			return true;
		}

		private AsyncLoadImageTask getAsyncLoadImageTask(ImageView imageview){
			if(imageview != null){
				Drawable drawable = imageview.getDrawable();
				if(drawable instanceof LoadedDrawable){
					LoadedDrawable loadedDrawable = (LoadedDrawable)drawable;
					return loadedDrawable.getLoadImageTask();
				}
			}
			return null;
		}

		public class LoadedDrawable extends ColorDrawable{
			private final WeakReference<AsyncLoadImageTask>loadImageTaskReference;
			public LoadedDrawable(AsyncLoadImageTask loadImageTask){
				super(Color.TRANSPARENT);
				loadImageTaskReference = new WeakReference<AsyncLoadImageTask>(loadImageTask);
			}
			
			public AsyncLoadImageTask getLoadImageTask(){
				return loadImageTaskReference.get();
			}
		}
	}

	public static class BitmapUtilities{
		
		public BitmapUtilities(){
		}
		
		public static Bitmap getBitmapThumbnail(String path, int width, int height){
			Bitmap bitmap = null;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			opts.inSampleSize = Math.max((int)(opts.outHeight/(float)height), (int)(opts.outWidth/(float)width));
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(path, opts);
			return bitmap;
		}
		
		public static Bitmap getBitmapThumbnail(Bitmap bmp, int width, int height){
			Bitmap bitmap = null;
			if(bmp != null){
				int bmpWidth = bmp.getWidth();
				int bmpHeight = bmp.getHeight();
				if(width!=0 && height!=0){
					Matrix matrix = new Matrix();
					float scaleWidth = ((float)width/bmpWidth);
					float scaleHeight = ((float)height/bmpHeight);
					matrix.postScale(scaleWidth, scaleHeight);
					bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
				}else{
					bitmap = bmp;
				}
			}
			return bitmap;
		}
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
     return detector.onTouchEvent(event);
    }
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if(e2.getX() - e1.getX() > 120){
			
			if(currentPos > 0){
				
			   viewflipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
			   viewflipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
			   viewflipper.showPrevious();
			   
			   currentPos -= 1;
			   tv_gallery_title.setText("No." + String.valueOf(currentPos+1));
			   
			   View_Del_R = View_Del_L;
			   
			   if(currentPos > 0){

				   viewflipper.removeViewAt(View_Del_L);
				   viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(currentPos-1))),View_Del_L);

				   View_Show = View_Del_L + 1;  
				   if(View_Show > View_C){
					   View_Show = View_A;
				   }
				   viewflipper.setDisplayedChild(View_Show);

				   View_Del_L--;
				   if(View_Del_L < View_A){
					   View_Del_L = View_C;
				   }
			   }

			}
			
		}else if(e1.getX() - e2.getX() > 120){
			if(currentPos < (ListSize-1)){
				   
			   viewflipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
			   viewflipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
			   viewflipper.showNext();
			   
			   currentPos += 1;
			   tv_gallery_title.setText("No." + String.valueOf(currentPos+1));
			   
			   View_Del_L = View_Del_R;
			   
			   if(currentPos < (ListSize-1)){
				   
				   viewflipper.removeViewAt(View_Del_R);
				   viewflipper.addView(getImageView(BitmapFactory.decodeFile(mList.get(currentPos+1))),View_Del_R);
				   
				   View_Del_R++;
				   if(View_Del_R > View_C){
					   View_Del_R = View_A;
				   }
			   }   
			   
		   }
		}
		return false;
	}
}
