package com.lkk.mobile;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.lkk.mobile.tools.NetUtil;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
	
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private static final int PHOTO_REQUEST_TAKEPHOTO_FORADD = 4;
	private static final int PHOTO_REQUEST_GALLERY_FORADD = 5;
	File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
	private static final String[] m={"不促销","促销"};   //spinner 填充数据
	 private ArrayAdapter<String> adapter;
	//控件
	 EditText e_title ;
	 EditText e_contentShort;
	 EditText e_content;
	 Spinner s_promotion ;
	 EditText e_pricePro ;
	 EditText e_price ;
	 Button   b_contentpic;
	 Button   b_file ;
	 Button  submit;
	 ImageView contentpic_show;
	 ImageView image_show;
	
	 //变量
	 String title ;
	 String contentShort ; 	
	 String content ;
	 String promotion;
	 String pricePro;
	 String price;
	  //图片功能 consts
	 
	 String fileName;         // 图片名称
	 File photo_file_addcontent = null; 
	 File photo_file = null;          //图片文件
	 ProgressDialog delLoadingDialog = null;   //进度条···
	 private Uri imageFilePath;   //照片路径	
	 private Date today;	
	 private byte[] image_Content;	
	 private Bitmap myBitmap;
	 
	  String add_statues = null ;
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adform);	
		 e_title = (EditText)findViewById(R.id.textView1_2_title);      //标题
		 e_contentShort = (EditText)findViewById(R.id.textView2_2_contentShort);//广告简介
		 e_content = (EditText)findViewById(R.id.textView3_2_content);   //广告内容
		 s_promotion = (Spinner)findViewById(R.id.spiView4_2_promotion);   // 是否促销  0 促销  1 不促销
		 e_pricePro = (EditText)findViewById(R.id.textView5_2_pricePro);  //促销价格
		 e_pricePro.setVisibility(View.INVISIBLE);      //默认不可见	
		 e_price = (EditText)findViewById(R.id.textView6_2_price);       //原价格
		 b_contentpic = (Button)findViewById(R.id.content_pic);         //广告内容图片 button
		 b_file =  (Button)findViewById(R.id.button_file);               // 上传图片
	     submit = (Button) findViewById(R.id.buttom_submit);            //提交
		 image_show = (ImageView) findViewById(R.id.Pic_show);
		 contentpic_show =(ImageView)findViewById(R.id.contentPic_show);   //广告内容图片展示
	  
		 //处理spinner
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_promotion.setAdapter(adapter);
        s_promotion.setOnItemSelectedListener(new SpinnerSelectedListener());
		s_promotion.setVisibility(View.VISIBLE);	 
		e_content.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					
					final String form[]={ "模板一", "模板二","模板三" };
					new AlertDialog.Builder(Main.this).setTitle("选择广告模板").setItems(form, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							e_content.setText(form[which].toString());
							
						}
					}).show();

					return true;
				}
				
	
			});
		 	 
           submit.setOnClickListener(new OnClickListener() {
        	   
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				title = e_title.getText().toString();
			    contentShort = e_contentShort.getText().toString();
			    content = e_content.getText().toString();
			    pricePro = e_pricePro.getText().toString();
			    price = e_price.getText().toString();
				if (title==null||"".equals(title)) {
					Toast.makeText(Main.this, "标题不能为空", Toast.LENGTH_SHORT)
							.show();
					e_title.setFocusable(true);
					e_title.setFocusableInTouchMode(true);
					e_title.requestFocus();
					e_title.requestFocusFromTouch();
				}else{
					// 还差一个图片路径
					// 多线程 提交广告
					URL url = null;
					try {
						url = new URL("");  
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} // 网络URL
					new PostAdTask().execute(url); // 启动线程
				
				}
			
	
			}
			
		}
           
     );
          b_contentpic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				new AlertDialog.Builder(Main.this)
				.setTitle("选择图片方式")
				.setMessage("选择")
				.setPositiveButton("相机拍照",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {							
								Intent take_photo_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								ContentValues values = new ContentValues(1);
								values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); 
								imageFilePath = Main.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
								Log.e("imagePath",imageFilePath.toString());
								take_photo_intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath); 
								
								startActivityForResult(take_photo_intent, PHOTO_REQUEST_TAKEPHOTO_FORADD);      //使用startActivityForReaule 方法								
							}
						})
				.setNegativeButton("手机相册",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {								
								Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);															
								startActivityForResult(intent, PHOTO_REQUEST_GALLERY_FORADD);
							}
						}).show();
				
			}
		}) ;
          
          b_file.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub								
				new AlertDialog.Builder(Main.this)
				.setTitle("选择图片方式")
				.setMessage("选择")
				.setPositiveButton("相机拍照",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {							
								Intent take_photo_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								// 指定调用相机拍照后照片的储存路径
								take_photo_intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
								startActivityForResult(take_photo_intent, PHOTO_REQUEST_TAKEPHOTO);      //使用startActivityForReaule 方法								
							}
						})
				.setNegativeButton("手机相册",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {								
								Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);															
								startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
							}
						}).show();
	
			}
		});
  
}	
	     
	 
	 /*
	  * 使用数组形式操作 spinner
	  */
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
           promotion = m[arg2];   //促销
         if(m[arg2].equals("促销")){
        	  // promotion = "0";
        	 e_pricePro.setVisibility(View.VISIBLE);
           } else{      //no promot
        	   e_pricePro.setVisibility(View.INVISIBLE);
           }
           
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		 }

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			
			startPhotoZoom(Uri.fromFile(tempFile), 150);
			break;
			
		case PHOTO_REQUEST_GALLERY :
			
             if (data != null)
 				startPhotoZoom(data.getData(), 150);
 			break;

		case PHOTO_REQUEST_CUT:
			if (data != null) 
				setPicToView(data);
			break;
		case PHOTO_REQUEST_TAKEPHOTO_FORADD:
			try
			{
				today = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
				fileName = sdf.format(today);  
				super.onActivityResult(requestCode, resultCode, data);	                            
				InputStream is=  this.getContentResolver().openInputStream(imageFilePath);  
                  image_Content=readStream(is);
                  myBitmap = getPicFromBytes(image_Content, null);                
                  contentpic_show.setImageBitmap(myBitmap);  // 预览功能
				
                  BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(String.format(
								"sdcard/Pic/upLoad/" + fileName + ".jpg",
								System.currentTimeMillis())));
                  
				 photo_file_addcontent = new File("sdcard/Pic/upLoad/" + fileName + ".jpg");  //上传的文件路径
			     myBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);  
				bos.flush();
				bos.close();	
			 } catch ( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 break;
			 
		case PHOTO_REQUEST_GALLERY_FORADD:
			 Uri selectedImage = data.getData();
	    	 String[] filePathColumn = { MediaStore.Images.Media.DATA };
	    	 Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
	    	 cursor.moveToFirst();
             int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	    	 String picturePath = cursor.getString(columnIndex);
	    	 cursor.close();    	 
	    	 contentpic_show.setImageBitmap(BitmapFactory.decodeFile(picturePath));
	    	 
              photo_file_addcontent = new File(picturePath); 
			break;
		default:
			break;
		}
				
	}
	    
	//将进行剪裁后的图片显示到UI界面上,并存储
		private void setPicToView(Intent picdata) {
			Bundle bundle = picdata.getExtras();
			if (bundle != null) {
				final Bitmap photo = bundle.getParcelable("data");
				image_show.setImageBitmap(photo);
				
				new Thread(){
					
						public void run() {
							try
							{
								today = new Date();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
								fileName = sdf.format(today);							
								BufferedOutputStream bos = new BufferedOutputStream(
										new FileOutputStream(
												String.format("sdcard/Pic/upLoad/" + fileName
														+ ".jpg", System.currentTimeMillis())));

								photo_file = new File("sdcard/Pic/upLoad/" + fileName + ".jpg"); // 上传的文件路径
								photo.compress(Bitmap.CompressFormat.JPEG, 80, bos);
								bos.flush();
								bos.close();

							  } catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							   }
						
						}
				}.start();
			}
		}           
	
	//裁剪照片
	 void startPhotoZoom(Uri uri, int size) {  
				    
		 Log.e("zoom", "begin");
	        Intent intent = new Intent("com.android.camera.action.CROP");  
	        intent.setData(uri); 
	        intent.setDataAndType(uri, "image/*");  
	        intent.putExtra("crop", "true");  	    
	        intent.putExtra("aspectX", 1);  
	        intent.putExtra("aspectY", 1);  	  	     
	        intent.putExtra("outputX", size);  
	        intent.putExtra("outputY", size);  
	        intent.putExtra("return-data", true);  
	        Log.e("zoom", "begin2");
	       startActivityForResult(intent, 3);
	    }  
	
	//读取图片
			public static byte[] readStream ( InputStream inStream ) throws Exception
			{
				byte[] buffer = new byte[1024];
				int len = -1;
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				while ((len = inStream.read(buffer)) != -1)
				{
					outStream.write(buffer, 0, len);
				}
				byte[] data = outStream.toByteArray();
				outStream.close();
				inStream.close();
				return data;

			}
	
			//转换成bitmap
			public static Bitmap getPicFromBytes ( byte[] bytes , BitmapFactory.Options opts )
			{
				if (bytes != null)
					if (opts != null)
						return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
					else
						return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				
				return null;
				
			}
	
			// 使用系统当前日期加以调整作为照片的名称
	  private String getPhotoFileName() {
		     Date date = new Date(System.currentTimeMillis());
				SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
				return"Pic/"+dateFormat.format(date) + ".jpg";
		}
	
	 
	
			 // 网络访问线程
			private class PostAdTask extends AsyncTask<URL, Integer, String> {
				    
					ProgressDialog progressDialog;
					
					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						Log.e("M2", "begin");
						super.onPreExecute();
						progressDialog = ProgressDialog.show(Main.this, "发送广告中", "请稍后····");
					}

					protected String doInBackground(URL... urls) {
						Log.e("M22", "begin");
			         
			             
					     String status = null ; // 是否提交成功
							
							 Map<String, String> addForm = new HashMap<String, String>();
							 
							 
							           addForm.put("title", title);
							           addForm.put("contentShort", contentShort);
							           addForm.put("content", content);
							           addForm.put("promotion", promotion);
							           addForm.put("pricePro", pricePro);
							           addForm.put("price", price);
							  Map <String,File> photo_f = new HashMap <String,File>();
							  Map <String,File> adcontentpic_f = new HashMap <String,File>();
							                photo_f.put("photo_file", photo_file);
							                adcontentpic_f.put("adcontentpic",photo_file_addcontent);
							                Log.e("file", photo_file.toString());
							                Log.e("file2", photo_file_addcontent.toString());
							  
							try {
								
								 status = NetUtil.postFile(urls[0].toString(), addForm, photo_f);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			              
			          
			            
			            
				         return status;
				     }

				
					 @Override
						protected void onProgressUpdate(Integer... values) {
							// TODO Auto-generated method stub
							super.onProgressUpdate(values);
							
						}

						protected void onPostExecute(String result) {
							Log.e("M222", "begin");
					     
						     add_statues = result;
						     progressDialog.dismiss();
						     
						     showResultDialog(result);
						     Log.e("result", result);
						}
				
			}

			/**
			 * 录入完成后，显示处理结果
			 * @param result
			 */
			private void showResultDialog(String result){
				// 创建提示
				new AlertDialog.Builder(Main.this).setTitle("发布广告结果")
						.setMessage(result).setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

									}
								}).setNegativeButton("继续录入",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										e_title.setText("");
										e_contentShort.setText("");
										e_content.setText("");
										e_price.setText("");
										e_pricePro.setText("");
										// 设置焦点
										e_title.setFocusable(true);
										e_title.setFocusableInTouchMode(true);
										e_title.requestFocus();
										e_title.requestFocusFromTouch();

									}
								}).show();
			}
			
			
			
	}
	
	
	

	
	
	
	
	

