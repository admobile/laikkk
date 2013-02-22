package com.lkk.mobile;

import java.io.File;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
	
	private static final String[] m={"促销","不促销"};   //spinner 填充数据
	 private ArrayAdapter<String> adapter;
	//控件
	 EditText e_title ;
	 EditText e_contentShort;
	 EditText e_content;
	 Spinner s_promotion ;
	 EditText e_pricePro ;
	 EditText e_price ;
	 Button   b_file ;
	 Button  submit;
	 //变量
	 String title ;
	 String contentShort ; 	
	 String content ;
	 String promotion;
	 String pricePro;
	 String price;
	  //图片功能 consts
	 private static final File PHOTO_DIR = new File(
				Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	 private static final int PIC_REQUEST_CODE_WITH_DATA = 1; // 获取图片数据
		private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 2; // 标识请求照相功能的activity
		private static final int PIC_Select_CODE_ImageFromLoacal = 3;// 标识请求相册取图功能的activity
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adform);	
		 e_title = (EditText)findViewById(R.id.textView1_2_title);      //标题
		 e_contentShort = (EditText)findViewById(R.id.textView2_2_contentShort);//广告简介
		 e_content = (EditText)findViewById(R.id.textView3_2_content);   //广告内容
		 s_promotion = (Spinner)findViewById(R.id.spiView4_2_promotion);   // 是否促销  0 促销  1 不促销
		 e_pricePro = (EditText)findViewById(R.id.textView5_2_pricePro);  //促销价格
		 e_price = (EditText)findViewById(R.id.textView6_2_price);       //原价格
		 b_file =  (Button)findViewById(R.id.button_file);               // 上传图片
	     submit = (Button) findViewById(R.id.buttom_submit);
		
	     //处理spinner
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_promotion.setAdapter(adapter);
        s_promotion.setOnItemSelectedListener(new SpinnerSelectedListener());
		s_promotion.setVisibility(View.VISIBLE);
		 
		    title = e_title.getText().toString();
		    contentShort = e_contentShort.getText().toString();
		    content = e_content.getText().toString();
		    pricePro = e_pricePro.getText().toString();
		    price = e_price.getText().toString();
		 
		 
           submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
           
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
								
								
								 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intent,
										PIC_REQUEST_CODE_SELECT_CAMERA);
							}
						})
				.setNegativeButton("手机相册",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent intent = new Intent();
								intent.setType("image/*");
								intent.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);
							}
						}).show();
	
			}
		});
           
           
           
           
		 }	
	// 使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
           promotion = m[arg2];   //促销
          /* if(m[arg2].equals("促销")){
        	   promotion = "0";
           } else {
        	   promotion = "1";
           }
           */
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		 }

	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			// 调用Gallery返回的
			case PIC_REQUEST_CODE_WITH_DATA: {
				final Bitmap photo = data.getParcelableExtra("data");
				

				break;
			}
			// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			case PIC_REQUEST_CODE_SELECT_CAMERA: {
				try {

					try {
						IntentFilter intentFilter = new IntentFilter(
								Intent.ACTION_MEDIA_SCANNER_STARTED);
						intentFilter
								.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
						intentFilter.addDataScheme("file");
						intentFilter
								.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					} catch (RuntimeException e) {

					}

				} catch (Exception e) {
					Toast.makeText(this, "获取图片异常，请重新尝试", Toast.LENGTH_LONG)
							.show();
					
				}
				break;
			}
			case PIC_Select_CODE_ImageFromLoacal:
				
	
				
				break;
			}
		}
	}

}

