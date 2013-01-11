package com.glutamatt.velibgo.storage;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import models.Station;

public class DaoStation extends AbstractDao implements IDaoDb<Station>{
	
	public static final int DATABASE_VERSION = 3;

	private static final String TABLE_NAME = "station";
	private static final String KEY_ID = "id";
	private static final String KEY_NOM = "nom";
	private static final String KEY_ADRESSE = "adresse";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	
	private static final String KEY_VELOS_DISPO = "velos_dispo";
	private static final String KEY_PLACES_DISPO = "places_dispo" ;
	private static final String KEY_PLACES_TOTAL = "places_total" ;
	private static final String KEY_PLACES_LOCKED = "places_locked" ;
	
	private static DaoStation _instance;

	private String[] fieldsString = new String[]{
			KEY_ID,
			KEY_NOM,
			KEY_ADRESSE,
			KEY_LATITUDE,
			KEY_LONGITUDE,
			KEY_VELOS_DISPO,
			KEY_PLACES_DISPO,
			KEY_PLACES_TOTAL,
			KEY_PLACES_LOCKED 
		};

	private DaoStation(Context context) {
		super(context);
	}
	
	public static DaoStation getInstance(Context context)
	{
		if(null == _instance) _instance = new DaoStation(context);
		return _instance;
	}

	@Override
	public String getCreateSql() {
		return "CREATE TABLE " + TABLE_NAME + "(" +
				KEY_ID + " INTEGER PRIMARY KEY , " +
				KEY_NOM + " TEXT, " + 
				KEY_LATITUDE + " TEXT, " + 
				KEY_LONGITUDE + " TEXT, " + 
				KEY_ADRESSE + " TEXT, " + 
				KEY_VELOS_DISPO + " TEXT, " + 
				KEY_PLACES_DISPO + " TEXT, " + 
				KEY_PLACES_TOTAL + " TEXT, " + 
				KEY_PLACES_LOCKED + " TEXT " + 
				" )";
	}

	@Override
	public String getUpgradeSql() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

	@Override
	public void save(Station model) {
		SQLiteDatabase db = getHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, model.getId());
		values.put(KEY_ADRESSE, model.getAdresse());
		values.put(KEY_NOM, model.getNom());
		values.put(KEY_LATITUDE, model.getLatitude());
		values.put(KEY_LONGITUDE, model.getLongitude());
		values.put(KEY_VELOS_DISPO, model.getVelosDispo());
		values.put(KEY_PLACES_DISPO, model.getPlacesDispo());
		values.put(KEY_PLACES_LOCKED, model.getPlacesLocked());
		values.put(KEY_PLACES_TOTAL, model.getPlacesTotal());
		if(null == find(model.getId()))
			db.insert(TABLE_NAME, null, values);
		else
			db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(model.getId())});
	}

	@Override
	public List<Station> getAll() {
		List<Station> stations = new ArrayList<Station>();
		SQLiteDatabase db = getHelper().getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, fieldsString, null, null, null, null, null);	
		if(cursor.moveToFirst())
		{
			do {
				stations.add(cursorToStation(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return stations;
	}
	
	private Station cursorToStation(Cursor cursor) {
		Station station = new Station();
		station.setId(cursor.getInt(0));
		station.setNom(cursor.getString(1));
		station.setAdresse(cursor.getString(2));
		station.setLatitude(cursor.getDouble(3));
		station.setLongitude(cursor.getDouble(4));
		station.setVelosDispo(cursor.getInt(5));
		station.setPlacesDispo(cursor.getInt(6));
		station.setPlacesTotal(cursor.getInt(7));
		station.setPlacesLocked(cursor.getInt(8));
		return station;
	}


	@Override
	public void delete(Station model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Station find(int id) {
		SQLiteDatabase db = getHelper().getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, fieldsString,
				KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
		
		if(!cursor.moveToFirst())
		{
			cursor.close();
			return null;
		}
		Station station = cursorToStation(cursor);
		cursor.close();
		return station;
	}

	@Override
	public int getVersion() {
		return DATABASE_VERSION;
	}
}
