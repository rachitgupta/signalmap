package com.hackerspace.signalHeatMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{

	private static final String tag = "DataBaseHelper";
	
	private static final String TABLE_SIGNAL_DATA = "CREATE TABLE if not exists SignalData( " +
			"latitude NUMERIC , longitude NUMERIC , serviceType INTEGER , operatorId INTEGER , strength NUMERIC , " +
			"timestamp STRING)";
			
	
	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	
		Log.d(tag, "before create");
		db.execSQL(TABLE_SIGNAL_DATA);
		Log.d(tag, "after create");
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
		
	}

}
