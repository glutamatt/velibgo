package com.glutamatt.velibgo.storage;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
private static final String DATABASE_NAME = "glutamatt.velibgo";
	
	private static DatabaseOpenHelper _instance;
	private static ArrayList<AbstractDao> daoList = new ArrayList<AbstractDao>();

	private DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, getVersion());
	}
	
	private static int getVersion() {
		int version = 0 ;
		for(AbstractDao dao : daoList)
			version += dao.getVersion();
		return version;
	}

	public static DatabaseOpenHelper getInstance(Context context)
	{
		if(null == _instance)
		{
			daoList.add(DaoStation.getInstance(context));
			_instance = new DatabaseOpenHelper(context);
		}
		return _instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for(AbstractDao dao : daoList)
			db.execSQL(dao.getCreateSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for(AbstractDao dao : daoList)
			db.execSQL(dao.getUpgradeSql());
		onCreate(db);
	}
}
