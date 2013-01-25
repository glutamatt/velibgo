package com.glutamatt.velibgo.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {
	
	private Context context = null ;
	
	public Network(Context c)
	{
		super();
		context = c;
	}
	
	public boolean checkNetwork()
	{
		ConnectivityManager connMgr = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE) ;
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())
			return true ;
		return false ;
	}
	
	public String downloadUrl(String stringurl) {
		InputStream is = getInputStreamFromUrl(stringurl);
		String contentAsString = null;
		try {
			contentAsString = readIt(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentAsString;
	}
	
	public InputStream getInputStreamFromUrl(String stringurl)
	{
		InputStream is = null;
		URL url;
		try {
			url = new URL(stringurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			conn.setReadTimeout(10000 /*milliseconds*/) ;
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			is  = conn.getInputStream();
		} catch (IOException e) {
		}
		return is;
	}
	
	public String readIt(InputStream is) throws IOException {
		if(is == null) return null;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		return total.toString();
	}
}
