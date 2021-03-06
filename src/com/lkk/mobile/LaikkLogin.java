package com.lkk.mobile;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.ls.LSInput;


import com.lkk.mobile.tools.NetUtil;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.Toast;

public class LaikkLogin extends Activity {
   
	EditText username_View;
	EditText password_View;
	Button  submit_button;
	String username;
	String password;
	String user_state;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_laikkmain);
		  checkPhoneStage();   //  SD  NetWork
		  username_View =(EditText)findViewById(R.id.username);
		  password_View =(EditText)findViewById(R.id.password);
		  submit_button =(Button)findViewById(R.id.submit);
		
		  //aaaaaaa
			
		  submit_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		       
				username = username_View.getText().toString();   
				password = password_View.getText().toString();
				
				//多线程访问 网络	
				 URL url = null;
				try {
					url = new URL("http://www.laikankan.cn/AdInput!checkUsr");
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  //网络URL
				 new checkUserTask().execute(url);    //启动线程    
				
				Log.e(username, password);
				
				//if(username.equals("admin")&& password.equals("admin")){
				
				
				 if("ok".equals(user_state)){
				    Intent intent = new Intent();  
                    //验证通过，启动下一个acticity
                    intent.setClass(LaikkLogin.this, Main.class);  
                    //启动Activity  
                    startActivity(intent); 		
				}else{
					Toast.makeText(LaikkLogin.this, "用户名或密码错误",Toast.LENGTH_SHORT ).show();
					
				}
			}
		});
	}
	
	
	
	//获得远程数据库的用户名和密码
	public String getUP(){
		
		return null;
	}
	/*
	 * menu 的实现
	 * */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_laikkmain, menu);
		return true;
	}
	
	

	/**
	 * 检查一下手机设备各项硬件的开启状态
	 */
	public void checkPhoneStage() {
		/*
		 * 先看手机是否已插入sd卡 然后判断sd卡里是不是已经创建了文件夹用来存储本程序拍下来的照片
		 * 如果没有创建的话就重新在sdcard里创建文件夹
		 */
		if (existSDcard()) { // 判断手机SD卡是否存在
			File sdcardDir =Environment.getExternalStorageDirectory();
			if (new File(sdcardDir.getPath()).canRead()) {
				 
				File file = new File(sdcardDir.getPath()+"PicLaikk/upLoadPic/");
				if (!file.exists()) {
					file.mkdirs();
				}
			}
		} else {
			new AlertDialog.Builder(this)
					.setMessage("检查到没有存储卡,请插入手机存储卡再开启本应用")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									finish();
								}
							}).show();
	      	}
		
		checkNetWorkStatus();
		
	}
	
    
	/**
	 * check sd card
	 * 
	 * @return
	 */
	public boolean existSDcard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}
	
	/**
	 * check network  Status 
	 * @return boolean
	 */
    
	public boolean checkNetWorkStatus() {
		boolean result;
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) { // 当前网络可用
			
			result = true;
		} else {         //不可用
			new AlertDialog.Builder(LaikkLogin.this)
			.setMessage("检查到没有可用的网络连接,请打开网络连接")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
							ComponentName cn = new ComponentName(
									"com.android.settings",
									"com.android.settings.Settings");
							Intent intent = new Intent();
							intent.setComponent(cn);
							intent.setAction("android.intent.action.VIEW");
							startActivity(intent);
							// finish();
						}
					}).show();
			result=false;
		}
		return result;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
	            AlertDialog.Builder alertbBuilder=new AlertDialog.Builder(this);
	            alertbBuilder.setTitle("真的要离开？").setMessage("你确定要离开？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                    
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                            //结束这个Activity
	                    	int nPid = android.os.Process.myPid();
	                    	android.os.Process.killProcess(nPid);
	                    }
	            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
	                    
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                            dialog.cancel();
	                            
	                    }
	            }).create();
	            alertbBuilder.show();
	            
	    }
	return true;
	}
    
   // 网络访问线程
private class checkUserTask extends AsyncTask<URL, Integer, String> {
	
	ProgressDialog progerDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Log.e("M2", "begin");
			super.onPreExecute();
			progerDialog = ProgressDialog.show(LaikkLogin.this, "登陆中", "请稍后···");
		}

		protected String doInBackground(URL... urls) {
			Log.e("M22", "begin");
         
		    
				
				 Map<String, String> checkUser = new HashMap<String, String>();
				           checkUser.put("username", username);
				           checkUser.put("password", password);
				          Log.e(username, password);
				   String status = NetUtil.getStringByPost(urls[0].toString(), checkUser);
              
          
            Log.e("userStatus", status);
            
	         return status;
	     }

	
		 @Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
				
			}

			protected void onPostExecute(String result) {
				Log.e("M222", "begin");
				progerDialog.dismiss();
			     user_state = result;
			     Log.e("us", user_state);
			     
			     
			}
	
}

}
