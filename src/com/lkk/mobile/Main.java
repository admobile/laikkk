package com.lkk.mobile;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.lkk.mobile.tools.NetUtil;

public class Main extends Activity {

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 鎷嶇収
	private static final int PHOTO_REQUEST_GALLERY = 2;// 浠庣浉鍐屼腑閫夋嫨
	private static final int PHOTO_REQUEST_CUT = 3;// 缁撴灉
	private static final int PHOTO_REQUEST_TAKEPHOTO_FORADD = 4;
	private static final int PHOTO_REQUEST_GALLERY_FORADD = 5;
	private File tempFile ;
	private File tempFileContent;
	private static final String IMAGE_FILE_LOCATION = "file:///"+NetUtil.IMG_UPLOAD_ADDR+"temp.jpg";
	private Uri tempImageUri;
	private Uri tempContentImageUri;
	private static final String[] m = { "涓嶄績閿�, "淇冮攢" }; // spinner 濉厖鏁版嵁
	private ArrayAdapter<String> adapter;
	// 鎺т欢
	private EditText e_title;
	private EditText e_contentShort;
	private EditText e_content;
	private Spinner s_promotion;
	private EditText e_pricePro;
	private EditText e_price;
	private Button b_contentpic;
	private Button b_file;
	private Button submit;
	private ImageView contentpic_show;
	private ImageView image_show;

	// 鍙橀噺
	private String title;
	private String contentShort;
	private String content;
	private String promotion;
	private String pricePro;
	private String price;
	// 鍥剧墖鍔熻兘 consts

	private String fileName; // 鍥剧墖鍚嶇О
	private File photo_file_addcontent = null;
	private File photo_file = null; // 鍥剧墖鏂囦欢
	private ProgressDialog delLoadingDialog = null; // 杩涘害鏉÷仿仿�
//	private Uri imageFilePath; // 鐓х墖璺緞
	private Date today;
	private byte[] image_Content;
//	private Bitmap myBitmap;

	private String add_statues = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("onCreate", "start");
		setContentView(R.layout.adform);
		//instantiate
		if(tempFile!=null&&tempFile.exists())
			tempFile.delete();
		tempFile = new File(Environment.getExternalStorageDirectory()+"/"+NetUtil.UPLOAD_ADDR,
		"img_temp.jpg");
		if(tempFileContent!=null&&tempFileContent.exists())
			tempFileContent.delete();
        tempFileContent= new File(Environment.getExternalStorageDirectory()+"/"+NetUtil.UPLOAD_ADDR,"img_content_temp.jpg");
		tempImageUri = Uri.fromFile(tempFile);
		tempContentImageUri = Uri.fromFile(tempFileContent);
		Log.e("tempImageUri", String.valueOf(tempImageUri));
		Log.e("tempContentImageUri", String.valueOf(tempContentImageUri));
		
		e_title = (EditText) findViewById(R.id.textView1_2_title); // 鏍囬
		e_contentShort = (EditText) findViewById(R.id.textView2_2_contentShort);// 骞垮憡绠�粙
		e_content = (EditText) findViewById(R.id.textView3_2_content); // 骞垮憡鍐呭
		s_promotion = (Spinner) findViewById(R.id.spiView4_2_promotion); // 鏄惁淇冮攢
		e_pricePro = (EditText) findViewById(R.id.textView5_2_pricePro); // 淇冮攢浠锋牸
		e_pricePro.setVisibility(View.INVISIBLE); // 榛樿涓嶅彲瑙�
		e_price = (EditText) findViewById(R.id.textView6_2_price); // 鍘熶环鏍�
		b_contentpic = (Button) findViewById(R.id.content_pic); // 骞垮憡鍐呭鍥剧墖 button
		b_file = (Button) findViewById(R.id.button_file); // 涓婁紶鍥剧墖
		submit = (Button) findViewById(R.id.buttom_submit); // 鎻愪氦
		image_show = (ImageView) findViewById(R.id.Pic_show);
		contentpic_show = (ImageView) findViewById(R.id.contentPic_show); // 骞垮憡鍐呭鍥剧墖灞曠ず
		// 澶勭悊spinner
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, m);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_promotion.setAdapter(adapter);
		s_promotion.setOnItemSelectedListener(new SpinnerSelectedListener());
		s_promotion.setVisibility(View.VISIBLE);

		e_content.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				final String form[] = { "妯℃澘涓�, "妯℃澘浜�, "妯℃澘涓� };
				new AlertDialog.Builder(Main.this).setTitle("閫夋嫨骞垮憡妯℃澘").setItems(
						form, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
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
				if (title == null || "".equals(title)) {
					Toast.makeText(Main.this, "鏍囬涓嶈兘涓虹┖", Toast.LENGTH_SHORT)
							.show();
					e_title.setFocusable(true);
					e_title.setFocusableInTouchMode(true);
					e_title.requestFocus();
					e_title.requestFocusFromTouch();
				} else {
					// 杩樺樊涓�釜鍥剧墖璺緞
					// 澶氱嚎绋�鎻愪氦骞垮憡
					URL url = null;
					try {
						url = new URL(NetUtil.SERVER_IP + "AdInput!addAd");
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} // 缃戠粶URL
					new PostAdTask().execute(url); // 鍚姩绾跨▼

				}

			}

		}

		);

		b_contentpic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Main.this).setTitle("閫夋嫨鍥剧墖鏂瑰紡")
						.setMessage("閫夋嫨").setPositiveButton("鐩告満鎷嶇収",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent take_photo_intent = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										ContentValues values = new ContentValues(
												1);
										values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
										Log.e("tempContentImageUri", tempContentImageUri
												.toString());
										take_photo_intent.putExtra(
												MediaStore.EXTRA_OUTPUT,
												tempContentImageUri);

										startActivityForResult(
												take_photo_intent,
												PHOTO_REQUEST_TAKEPHOTO_FORADD); // 浣跨敤startActivityForReaule
																					
									}
								}).setNegativeButton("鎵嬫満鐩稿唽",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent intent = new Intent(
												Intent.ACTION_PICK,
												android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
										startActivityForResult(intent,
												PHOTO_REQUEST_GALLERY_FORADD);
									}
								}).show();

			}
		});

		b_file.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new AlertDialog.Builder(Main.this).setTitle("閫夋嫨鍥剧墖鏂瑰紡")
						.setMessage("閫夋嫨").setPositiveButton("鐩告満鎷嶇収",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent take_photo_intent = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										// 鎸囧畾璋冪敤鐩告満鎷嶇収鍚庣収鐗囩殑鍌ㄥ瓨璺緞
										take_photo_intent.putExtra(
												MediaStore.EXTRA_OUTPUT, tempImageUri);
										startActivityForResult(
												take_photo_intent,
												PHOTO_REQUEST_TAKEPHOTO);
									}
								}).setNegativeButton("鎵嬫満鐩稿唽",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

										Intent intent = new Intent(
												Intent.ACTION_PICK,
												android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

										startActivityForResult(intent,
												PHOTO_REQUEST_GALLERY);
									}
								}).show();

			}
		});

	}

	/*
	 * 浣跨敤鏁扮粍褰㈠紡鎿嶄綔 spinner
	 */
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			promotion = m[arg2]; // 淇冮攢
			if (m[arg2].equals("淇冮攢")) {
				promotion = "1";
				e_pricePro.setVisibility(View.VISIBLE);
			} else { // no promot
				promotion = "0";
				e_pricePro.setVisibility(View.INVISIBLE);
			}

		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(this);
			alertbBuilder.setTitle("绯荤粺淇℃伅").setMessage("纭畾瑕侀�鍑虹▼搴忥紵")
					.setPositiveButton("纭畾",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 缁撴潫杩欎釜Activity
									int nPid = android.os.Process.myPid();
									android.os.Process.killProcess(nPid);
								}
							}).setNegativeButton("鍙栨秷",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

								}
							}).create();
			alertbBuilder.show();

		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(tempImageUri, 150);
			break;

		case PHOTO_REQUEST_GALLERY:

			if (data != null)
				startPhotoZoom(data.getData(), 150);
			break;

		case PHOTO_REQUEST_CUT:
			Log.e("PHOTO_REQUEST_CUT", String.valueOf(data));
			if (data != null)
				setPicToView(data);
			break;
		case PHOTO_REQUEST_TAKEPHOTO_FORADD:
			Log.e("data", String.valueOf(data));
			try {
				today = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
				fileName = sdf.format(today);
//				super.onActivityResult(requestCode, resultCode, data);
//				InputStream is = this.getContentResolver().openInputStream(
//						tempContentImageUri);
//				image_Content = readStream(is);
//				myBitmap = getPicFromBytes(image_Content, null);
				Bitmap bitmap = decodeUriAsBitmap(tempContentImageUri);
				Log.e("bitmap",String.valueOf(bitmap));
				//contentpic_show.setImageBitmap(myBitmap); // 棰勮鍔熻兘
				contentpic_show.setImageBitmap(bitmap);
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(String.format(
								NetUtil.IMG_UPLOAD_ADDR + fileName + ".jpg",
								System.currentTimeMillis())));
				photo_file_addcontent = new File(NetUtil.IMG_UPLOAD_ADDR
						+ fileName + ".jpg"); // 涓婁紶鐨勬枃浠惰矾寰�
				Log.e("photo_file_addcontent",photo_file_addcontent.toString());
				bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);
				bos.flush();
				bos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case PHOTO_REQUEST_GALLERY_FORADD:
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			contentpic_show.setImageBitmap(BitmapFactory
					.decodeFile(picturePath));

			photo_file_addcontent = new File(picturePath);
			break;

		default:
			break;
		}

	}

	// 灏嗚繘琛屽壀瑁佸悗鐨勫浘鐗囨樉绀哄埌UI鐣岄潰涓�骞跺瓨鍌�
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			final Bitmap photo = bundle.getParcelable("data");
			image_show.setImageBitmap(photo);

			new Thread() {

				public void run() {
					try {
						today = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyyMMddhhmm");
						fileName = sdf.format(today);
						BufferedOutputStream bos = new BufferedOutputStream(
								new FileOutputStream(String.format(
										NetUtil.IMG_UPLOAD_ADDR + fileName
												+ ".jpg", System
												.currentTimeMillis())));
						photo.compress(Bitmap.CompressFormat.JPEG, 80, bos);
						bos.flush();
						bos.close();
						photo_file = new File(NetUtil.IMG_UPLOAD_ADDR
								+ fileName + ".jpg"); // 涓婁紶鐨勬枃浠惰矾寰�
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();
		}
	}

	// 瑁佸壀鐓х墖
	void startPhotoZoom(Uri uri, int size) {
		Log.e("zoom", "begin");
		Log.e("uri", String.valueOf(uri));
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
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
		
		
	}

	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	// 璇诲彇鍥剧墖
	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	// 杞崲鎴恇itmap
	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null){
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
			

		return null;

	}

	// 浣跨敤绯荤粺褰撳墠鏃ユ湡鍔犱互璋冩暣浣滀负鐓х墖鐨勫悕绉�
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return  dateFormat.format(date) + ".jpg";
	}

	// 缃戠粶璁块棶绾跨▼
	private class PostAdTask extends AsyncTask<URL, Integer, String> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Log.e("M2", "begin");
			super.onPreExecute();
			progressDialog = ProgressDialog.show(Main.this, "鎻愪氦涓�, "璇风◢鍚幝仿仿仿�);
		}

		protected String doInBackground(URL... urls) {
			Log.e("M22", "begin");

			String status = null; // 鏄惁鎻愪氦鎴愬姛

			Map<String, String> addForm = new HashMap<String, String>();

			addForm.put("title", title);
			addForm.put("contentShort", contentShort);
			addForm.put("content", content);
			addForm.put("promotion", promotion);
			addForm.put("pricePro", pricePro);
			addForm.put("price", price);
			Map<String, File> photo_f = new HashMap<String, File>();
			if (photo_file != null) {
				Log.e("photo_file", photo_file.toString());
				photo_f.put("file", photo_file);
			}
			if (photo_file_addcontent != null) {
				Log
						.e("photo_file_addcontent", photo_file_addcontent
								.toString());
				photo_f.put("fileForContent", photo_file_addcontent);
			}
			// get username
			// Restore preferences
			SharedPreferences sharedPreferences = getSharedPreferences(
					"user_pref", 0);
			String username = sharedPreferences.getString("username", "");
			addForm.put("username", username);

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
	 * 褰曞叆瀹屾垚鍚庯紝鏄剧ず澶勭悊缁撴灉
	 * 
	 * @param result
	 */
	private void showResultDialog(String result) {
		// 鍒涘缓鎻愮ず
		new AlertDialog.Builder(Main.this).setTitle("鍙戝竷骞垮憡缁撴灉")
				.setMessage(result).setPositiveButton("纭畾",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).setNegativeButton("缁х画褰曞叆",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								e_title.setText("");
								e_contentShort.setText("");
								e_content.setText("");
								e_price.setText("");
								e_pricePro.setText("");
								// 璁剧疆鐒︾偣
								e_title.setFocusable(true);
								e_title.setFocusableInTouchMode(true);
								e_title.requestFocus();
								e_title.requestFocusFromTouch();

							}
						}).show();
	}

	public void test(){
		
		// test GIT
	}
	
	public void test2(){
	    //the second mondify	
	}
	}
}
