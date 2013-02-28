package com.lkk.mobile.tools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	private static final String CHARSET = "UTF-8"; // 设置编码
	
    public static String checkUser(String urlPath, String username,String Password){
    	
    	
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
	public static String getStringByPost(String urlPath,Map<String, String> parmas
			) {
		HttpPost httpRequest = new HttpPost(urlPath);
	
		String strResult = "";
		try {
			
			ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		     if(parmas != null){
		         Set<String> keys = parmas.keySet();
		         for(Iterator<String> i = keys.iterator(); i.hasNext();) {
		              String key = (String)i.next();
		              pairs.add(new BasicNameValuePair(key, parmas.get(key)));
		         }
		    }
			httpRequest.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		
			HttpClient client = new DefaultHttpClient();
			client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 29000);
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
	 * 发布广告方法
	 * 请求的URL 
	 * string 
	 * 图片文件
	 */
	public static String postFile(String actionUrl, Map<String, String> params,
			Map<String, File> files) throws Exception {
		StringBuilder sb2 = new StringBuilder();
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(35 * 1000); // 
		conn.setDoOutput(true);// 
		conn.setUseCaches(false); // 
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
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
		if (files != null){
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1
						.append("Content-Disposition: form-data; name=\"myUpload\"; filename=\""
								+ file.getKey() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

		// 
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// 
		int res = conn.getResponseCode();
		if (res == 200) {
			in = conn.getInputStream();
			int ch;
			sb2 = new StringBuilder();
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
		}
		outStream.close();
		conn.disconnect();
		}
		return sb2.toString();
	}
	
}