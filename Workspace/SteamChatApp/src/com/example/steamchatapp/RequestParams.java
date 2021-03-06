package com.example.steamchatapp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

public class RequestParams {
	String method, baseUrl;
	HashMap<String, String> params = new HashMap<String, String>();

	public RequestParams(String method, String baseUrl) {
		super();
		this.method = method;
		this.baseUrl = baseUrl;
	}

	public void addParam(String key, String value) {
		params.put(key, value);
	}

	public String getEncodedParams() {

		// param1=value1%20value1&param2=value2&param3=value3

		StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			try {
				String value = URLEncoder.encode(params.get(key), "UTF-8");
				if (sb.length() > 0) {
					sb.append("&");
				}
				sb.append(key + "=" + value);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (sb.length() != 0) {
			return sb.toString();
		}
		return "";
	}

	public String getEncodedUrl() {
		return this.baseUrl + "?" + getEncodedParams();
	}

	public HttpsURLConnection setupConnection() throws IOException {
		if (method.equals("GET")) {
			URL url = new URL(getEncodedUrl());
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			return con;

		} else {// POST

			URL url = new URL(this.baseUrl);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(
					con.getOutputStream());
			writer.write(getEncodedParams());
			Log.d("demo", getEncodedParams());
			writer.flush();
			con.getResponseCode();
			return con;

		}
	}
}
