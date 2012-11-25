package com.hackerspace.signalHeatMap;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncPostData extends AsyncTask<SignalData, Void, Void> {

	private Context c;
	private static String m_BaseUri="http://192.168.1.176/signalmap/strengthdata/strengthdata";

	public String doPost(HashMap<String, Object> params) throws Exception {

		try {
			return postRequest(m_BaseUri, encodeUrl(params), "application/json");
		} catch (NetworkErrorException ioe) {
			throw new NetworkErrorException(ioe.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public String postRequest(String url, String params, String contentType)
			throws ClientProtocolException, IOException, NetworkErrorException, Exception {

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 60000);

		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost(url);
		post.setHeader(HTTP.CONTENT_TYPE, contentType);
		post.setHeader( "accept","application/json");

		Log.i("tag", "Request URL : " + url);
		Log.i("tag", "Request params : " + params);

		if (params != null) {
			post.setEntity(new StringEntity(params));
		}

		try {
			HttpResponse hhtpResponse = client.execute(post);
			if (hhtpResponse.getStatusLine().getStatusCode() == 200) {
				String response = EntityUtils.toString(hhtpResponse.getEntity());
				Log.i("tag", "Response : " + response);
				return response;
			} else {
				Log.i("tag", "Response : " + hhtpResponse.getStatusLine().getStatusCode() + " = " + hhtpResponse.getStatusLine().getReasonPhrase());
			}
			// Log.i(tag,String.valueOf(hhtpResponse.getStatusLine().getStatusCode()));
			// Log.i(tag, hhtpResponse.getStatusLine().getReasonPhrase());
		} catch (SocketException e) {
			throw new NetworkErrorException(e.getMessage());
		} catch (SocketTimeoutException e) {
			throw new NetworkErrorException(e.getMessage());
		} catch (ConnectTimeoutException e) {
			throw new NetworkErrorException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		return null;
		// throw new IOException(response.getStatusLine().getReasonPhrase());
	}

	public String encodeUrl(HashMap<String, Object> parameters) {
		if (parameters == null) {
			Log.i("tag", "Encoded string : ");
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(key) + "=" +
					URLEncoder.encode(parameters.get(key).toString()));
		}
		Log.i("tag", "Encoded string : " + sb.toString());
		return sb.toString();
	}

	ProgressDialog pDialog;
	
	public AsyncPostData(Context context) {
		
		c=context;
		
		
	}
	
	@Override
	protected void onPreExecute() {
	
		pDialog=new ProgressDialog(c);
		pDialog.show();
		
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(SignalData... params) {

		SignalData signalData = params[0];

		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("created_time", Long.parseLong(signalData.timestamp));
		hashMap.put("operator_id", signalData.operatorId);
		hashMap.put("latitude", signalData.latitude);
		hashMap.put("longitude", signalData.longitude);
		hashMap.put("service_type_id", signalData.serviceType);
		hashMap.put("signal_strength", signalData.strength);
		hashMap.put("id", UUID.randomUUID().toString());

		try {
			doPost(hashMap);
		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		
		pDialog.dismiss();
		
		super.onPostExecute(result);
	}
}
