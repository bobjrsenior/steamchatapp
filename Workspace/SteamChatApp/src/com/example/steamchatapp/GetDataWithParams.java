package com.example.steamchatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.widget.EditText;

public class GetDataWithParams extends AsyncTask<RequestParams, Void, String>{

	
	EditText passView;
	String username;
	int what;
	
	public GetDataWithParams(EditText pass, int what, String username){
		passView = pass;
		this.what = what;
		this.username = username;
	}
	
	@Override
	protected String doInBackground(RequestParams... params) {
		BufferedReader reader = null;
			try {
				HttpURLConnection con = params[0].setupConnection();
				
				reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = "";
				while((line = reader.readLine()) != null){
					//Output what is recieved for debugging
					Log.d("demo", "aa " + line);
					sb.append(line);
				}
				return sb.toString();
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
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(what == 1){
			
			JSONObject obj;
			//byte[] password = (byte[]) ((Integer) (Integer.parseInt((passView.getText().toString())))).to;
			byte[] bytepass = EncodingUtils.getAsciiBytes((String) (passView.getText().toString()));
			
			try {
				obj = new JSONObject(result);
				//Get the RSA modulus and exponent from the returned JSON Object
				String auth_mod = ((String) obj.getString("publickey_mod"));
				String auth_exp = (String) obj.getString("publickey_exp");
				
				//Create RSA spec with returned data (16 = hex input)
				RSAPublicKeySpec rsa_params = new RSAPublicKeySpec(new BigInteger(auth_mod, 16), new BigInteger(auth_exp, 16));
				
				//RSA Encrypting
				KeyFactory factory = KeyFactory.getInstance("RSA");
				
				//Create a public key with params
				PublicKey pub = factory.generatePublic(rsa_params);
				
				//Create a RSA cipher
				Cipher cipher = Cipher.getInstance("RSA");
				
				//Set the cipher to encrypt using the public key
				cipher.init(Cipher.ENCRYPT_MODE, pub);
				
				//Encode the password
				byte[] pass_encrypted = cipher.doFinal((bytepass));
				//encode it into base 64 url safe bytes
				pass_encrypted = Base64.encode(pass_encrypted, Base64.URL_SAFE);
				//Turn it into a string
				String pass_encrypted_string = new String(pass_encrypted);
				
				Log.d("Demo2", pass_encrypted_string.toString());
				//Pass the params to the login url
				RequestParams params = new RequestParams("POST", "https://steamcommunity.com/login/dologin/");
				params.addParam("password", pass_encrypted_string);
				params.addParam("username", username);
				new GetDataWithParams(passView, 2, username).execute(params);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		else if (what == 2){
			
		}
	}
}

