package com.lkk.mobile.tools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

public class NetUtil {
	private static final String CHARSET = "UTF-8"; // 设置编码
	public static final String SERVER_IP = "http://www.laikankan.cn/";
//	public static final String SERVER_IP = "http://123.169.147.11:8080/laikk/";
	public static final String SDCARD_ADDR = "sdcard/";
	public static final String UPLOAD_ADDR = "PicLaikk/";
	public static final String IMG_UPLOAD_ADDR = SDCARD_ADDR+UPLOAD_ADDR+"upLoadPic/";
	public static String checkUser(String urlPath, String username,
			String Password) {

		return null;
	};

	/**
	 * 
	 * 
	 * @param urlPath
	 * @return
	 */
	public static String getStringByGet(String urlPath) {

		String strResult = "-1";

		HttpGet httpRequest = new HttpGet(urlPath);
		try {
			/* 创建HTTP request */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 检查ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				strResult = (EntityUtils.toString(httpResponse.getEntity()));

			} else {
				strResult = "-1";
			}
		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		} catch (Exception e) {

		}
		return strResult;
	}

	// 带参数的请求
	public static String getStringByPost(String urlPath,
			Map<String, String> parmas) {
		HttpPost httpRequest = new HttpPost(urlPath);
		String strResult = "";
		try {

			ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
			if (parmas != null) {
				Set<String> keys = parmas.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					pairs.add(new BasicNameValuePair(key, parmas.get(key)));
				}
			}
			httpRequest.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));

			HttpClient client = new DefaultHttpClient();
			client.getParams().setIntParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 29000);
			HttpResponse httpResponse = client.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, CHARSET);
					try {
						JSONObject jsonObject = new JSONObject(out);
						strResult = jsonObject.getString("msg");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				strResult = "网络故障";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strResult;
	}

	/*
	 * 发布广告方法 请求的URL string 图片文件
	 */
	public static String postFile(String actionUrl, Map<String, String> params,
			Map<String, File> files) throws Exception {
		String strResult="";  
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(35 * 1000); // 
		conn.setDoOutput(true);// 
		conn.setUseCaches(false); // 
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", CHARSET);
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(conn
				.getOutputStream());
		outStream.write(sb.toString().getBytes());
		InputStream in = null;
		// 
		if (files != null && !files.isEmpty()) {
			for (Map.Entry<String, File> file : files.entrySet()) {
				File f = file.getValue();
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				
				Log.e("file.getKey()", file.getKey());
				Log.e("f.getName()", f.getName());
				sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\"; filename=\""
								+ f.getName() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(f);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}
		}
			// 
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 
			
			byte[] b = new byte[1024];   
			int res = conn.getResponseCode();
			if (res == 200) {
				in = conn.getInputStream();
				int ch = 0;
				while ((ch = in.read(b)) != -1) {
					 strResult+=new String(b,CHARSET);    
					  b = new byte[1024];    
				}
			}
			outStream.close();
			conn.disconnect();
		
		
		if(strResult!=null&&!"".equals(strResult)){
			try {
				Log.e("strResult", strResult);
				JSONObject jsonObject = new JSONObject(strResult);
				strResult = jsonObject.getString("msg");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return strResult;
	}

//	private static final String TAG = "uploadFile";
//	private static final int TIME_OUT = 10 * 1000; // 超时时间
//
//	public static String uploadFile(File file, String RequestURL) {
//		String result = null;
//		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
//		String PREFIX = "--", LINE_END = "\r\n";
//		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
//
//		try {
//			URL url = new URL(RequestURL);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setReadTimeout(TIME_OUT);
//			conn.setConnectTimeout(TIME_OUT);
//			conn.setDoInput(true); // 允许输入流
//			conn.setDoOutput(true); // 允许输出流
//			conn.setUseCaches(false); // 不允许使用缓存
//			conn.setRequestMethod("POST"); // 请求方式
//			conn.setRequestProperty("Charset", CHARSET); // 设置编码
//			conn.setRequestProperty("connection", "keep-alive");
//			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
//					+ BOUNDARY);
//			Log.e(TAG, "RequestURL : " + RequestURL);
//			if (file != null) {
//				/**
//				 * 当文件不为空，把文件包装并且上传
//				 */
//				DataOutputStream dos = new DataOutputStream(conn
//						.getOutputStream());
//				StringBuffer sb = new StringBuffer();
//				sb.append(PREFIX);
//				sb.append(BOUNDARY);
//				sb.append(LINE_END);
//				/**
//				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
//				 * filename是文件的名字，包含后缀名的 比如:abc.png
//				 */
//				sb.append("Content-Disposition: form-data; file=\"img\"; fileFilename=\""
//								+ file.getName() + "\"" + LINE_END);
//				sb.append("Content-Type: application/octet-stream; charset="
//						+ CHARSET + LINE_END);
//				sb.append(LINE_END);
//				dos.write(sb.toString().getBytes());
//				InputStream is = new FileInputStream(file);
//				byte[] bytes = new byte[1024];
//				int len = 0;
//				while ((len = is.read(bytes)) != -1) {
//					dos.write(bytes, 0, len);
//				}
//				is.close();
//				dos.write(LINE_END.getBytes());
//				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
//						.getBytes();
//				dos.write(end_data);
//				dos.flush();
//				/**
//				 * 获取响应码 200=成功 当响应成功，获取响应的流
//				 */
//				int res = conn.getResponseCode();
//				Log.e(TAG, "response code:" + res);
//				if (res == 200) {
//					Log.e(TAG, "request success");
//					InputStream input = conn.getInputStream();
//					StringBuffer sb1 = new StringBuffer();
//					int ss;
//					while ((ss = input.read()) != -1) {
//						sb1.append((char) ss);
//					}
//					result = sb1.toString();
//					Log.e(TAG, "result : " + result);
//				} else {
//					Log.e(TAG, "request error");
//				}
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}

}