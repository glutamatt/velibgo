package com.glutamatt.velibgo.storage;

import android.content.Context;

public abstract class AbstractDao {

	protected Context context;
	private static DatabaseOpenHelper helper;

	public AbstractDao(Context pContext) {
		context = pContext ;
	}
	abstract public String getCreateSql();
	abstract public String getUpgradeSql();
	
	protected DatabaseOpenHelper getHelper()
	{
		if(null == helper) helper = DatabaseOpenHelper.getInstance(context);
		return helper;
	}
	
	abstract public int getVersion() ;
}
