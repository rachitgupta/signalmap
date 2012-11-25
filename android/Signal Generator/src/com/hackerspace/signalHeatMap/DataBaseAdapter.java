package com.hackerspace.signalHeatMap;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataBaseAdapter {

	private static final String tag = "DataBaseAdapter";
	private Context context;
	private static final String DATABASE_NAME = "gkbhitech";
	private static final int DATABASE_VERSION = 1;
	private static SQLiteDatabase db;
	private static DataBaseHelper dataBaseHelper;

	// ---------------------------- Comppile stmt ------------------------
	private static SQLiteStatement insertSignalData;

	/*
	 * private static SQLiteStatement updateLogin; private static
	 * SQLiteStatement updateLens; private static SQLiteStatement
	 * updateLensCoat; private static SQLiteStatement updateCoating; private
	 * static SQLiteStatement updatePlant; private static SQLiteStatement
	 * updateProductBrand; private static SQLiteStatement updateStatus; private
	 * static SQLiteStatement updateService; private static SQLiteStatement
	 * updateCustomer; private static SQLiteStatement updateInhouseBank;
	 */

	// ---------------------------- Table ---------------------------------
	private static final String SIGNAL_DATA = "SignalData";

	private static final String _ID = "_id";

	private static final int INDEX_ID = 1; // change index of all table column

	// -----------------------------Colomns of Signal Data
	// table-----------------
	private static final String SIGNAL_DATA_LATITUDE = "latitude";
	private static final String SIGNAL_DATA_LONGITUDE = "longitude";
	private static final String SIGNAL_DATA_SERVICE_TYPE = "serviceType";
	private static final String SIGNAL_DATA_OPERATOR_ID = "operatorId";
	private static final String SIGNAL_DATA_STRENGTH = "strength";
	private static final String SIGNAL_DATA_TIMESTAMP = "timeStamp";

	private static String query;

	private static Cursor cursor;


	public DataBaseAdapter(Context context) {
		this.context = context;
	}

	public void open() {
		try {
			dataBaseHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
			db = dataBaseHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (db.isOpen()) {
			try {
				db.close();
				Log.i(tag, "database closed successfully");
			} catch (Exception e) {
				Log.i(tag, "error during database close");
			}
		}
	}

	public Boolean isDbOpen() {
		return db.isOpen();
	}

	public void compileStatement() {
		// if(Constant.log) Log.i(tag,
		// "........compile stmt method..............");
		// sqLiteStatement.executeInsert();
	}

	public void insertSignalData(SignalData signalData) {

		String sql = "insert into SignalData (latitude , longitude  , serviceType  , operatorId  , strength  , " +
				"timestamp ) values (" + signalData.latitude + "," + signalData.longitude + "," + signalData.serviceType + "," + signalData.operatorId + "," + signalData.strength + ",'" + signalData.timestamp + "')";

		db.execSQL(sql);
		
		
	}

	public ArrayList<SignalData> getAllSignalDatas()
	{
		String sql = " Select * from SignalData";

		Cursor c = db.rawQuery(sql, null);

		c.moveToFirst();

		ArrayList<SignalData> signalDatas = new ArrayList<SignalData>();

		while (!c.isAfterLast())
		{
			SignalData tempSignalData = new SignalData(c.getDouble(c.getColumnIndex("latitude")), c.getDouble(c.getColumnIndex("longitude")), c.getInt(c.getColumnIndex("serviceType")), c.getInt(c.getColumnIndex("operatorId")), c.getFloat(c.getColumnIndex("strength")), c.getString(c.getColumnIndex("timestamp")));

			signalDatas.add(tempSignalData);

			c.moveToNext();
		}

		c.close();
		return signalDatas;
	}

}