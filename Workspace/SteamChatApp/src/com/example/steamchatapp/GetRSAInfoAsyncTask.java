package com.example.steamchatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;
import android.util.Log;

public class GetRSAInfoAsyncTask extends AsyncTask<RequestParams, Void, String>{

	
	Login activity;
	
	public GetRSAInfoAsyncTask(Login activity) {
		this.activity = activity;
	}
	
	
	@Override
	protected String doInBackground(RequestParams... params) {
		BufferedReader reader = null;
		String result = null;
		try {
			HttpsURLConnection con = params[0].setupConnection();
			
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while((line = reader.readLine()) != null){
				//Output what is recieved for debugging
				Log.d("demo", "aa " + line);
				sb.append(line);
			}
			result = sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		activity.GetRSA(result);
	}
	
	

}
