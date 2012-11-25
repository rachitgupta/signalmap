package com.hackerspace.signalHeatMap;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SignalGeneratorActivity extends Activity {
	/** Called when the activity is first created. */
	private TelephonyManager Tel;
	private MyPhoneStateListener MyListener;
	private DataBaseAdapter dbAdapter;
	private ArrayAdapter<SignalData> arrayAdapter;
	private ListView listView;
	private ArrayList<SignalData> signalDatas;
	private static String m_BaseUri = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dbAdapter = new DataBaseAdapter(this);
		dbAdapter.open();

		TelephonyManager manager = (TelephonyManager) SignalGeneratorActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
		final String carrierName = manager.getNetworkOperatorName();

		listView = (ListView) findViewById(R.id.listView);

		signalDatas = dbAdapter.getAllSignalDatas();

		arrayAdapter = new ArrayAdapter<SignalData>(SignalGeneratorActivity.this, android.R.layout.simple_list_item_1, signalDatas);
		listView.setAdapter(arrayAdapter);

		MyListener = new MyPhoneStateListener();
		Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				int strength = MyListener.signalStrengthNormalized;

				double lat = location.getLatitude();

				double lon = location.getLongitude();

				int netType = Tel.getNetworkType();

				char firstCharacter = carrierName.toLowerCase().charAt(0);

				int operatorType = 0;

				switch (firstCharacter)
				{
				case 'a':
					operatorType = 0;
				case 'b':
					operatorType = 1;
				case 'i':
					operatorType = 2;
				case 'r':
					operatorType = 3;
				case 't':
					operatorType = 4;
				case 'v':
					operatorType = 5;
				}

				SignalData signalData = new SignalData(lat, lon, netType, operatorType, strength, getTimeStamp() + "");

				dbAdapter.insertSignalData(signalData);

				signalDatas = dbAdapter.getAllSignalDatas();

				arrayAdapter.notifyDataSetChanged();

				postData(signalData);

			}

			public long getTimeStamp() {
				Calendar calendar = Calendar.getInstance();
				java.util.Date now = calendar.getTime();
				java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
				return currentTimestamp.getTime();
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		// Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
	}

	public void postData(SignalData signalData)
	{

		AsyncPostData postData = new AsyncPostData(this);
		postData.execute(signalData);

	}

	/* Called when the application resumes */
	@Override
	protected void onResume()
	{
		super.onResume();
		Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	private class MyPhoneStateListener extends PhoneStateListener
	{
		private static final String tag = "MyPhoneStateListener";
		public int signalStrengthNormalized;

		/*
		 * Get the Signal strength from the provider, each tiome there is an
		 * update
		 */
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength)
		{

			signalStrengthNormalized = (signalStrength.getGsmSignalStrength() * 2) + 10;
			super.onSignalStrengthsChanged(signalStrength);
		}

	}

}