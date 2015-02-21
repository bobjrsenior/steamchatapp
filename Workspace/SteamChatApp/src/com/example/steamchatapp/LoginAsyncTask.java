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
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

public class LoginAsyncTask extends AsyncTask<String, Boolean, ArrayList<String>>{

	
	Login activity;
	
	String password;
	String username;
	
	Bitmap captcha_image;
	String captcha_answer;
	String steamguard_answer;
	
	
	public LoginAsyncTask(String pass, String username, Login activity){
		this.password = pass;
		this.username = username;
		this.activity = activity;
	}
	

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		//Handle Captcha and steam guard
		ArrayList<String> results = new ArrayList<String>();
		JSONObject obj;
		//byte[] password = (byte[]) ((Integer) (Integer.parseInt((passView.getText().toString())))).to;
		byte[] bytepass = EncodingUtils.getAsciiBytes((String) (password));
		
		try {
			obj = new JSONObject(params[0]);
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
			//pass_encrypted = ;
			pass_encrypted = Base64.encode(pass_encrypted, Base64.URL_SAFE);
			//StringBuilder b;
			//Turn it into a string
			String pass_encrypted_string = new String(pass_encrypted);
			
			Log.d("Demo2", pass_encrypted_string.toString());
			//Pass the params to the login url
			RequestParams params2 = new RequestParams("POST", "https://steamcommunity.com/mobilelogin/dologin/");
			params2.addParam("password", pass_encrypted_string);
			params2.addParam("username", username);
			params2.addParam("rsatimestamp", (String) obj.getString("timestamp"));
			params2.addParam("token_gid", obj.getString("token_gid"));
			if(params.length > 1){
				if(params[1].equals("1")){
					params2.addParam("captcha_gid", params[2]);
					params2.addParam("captcha_text", params[3]);
					Log.d("captcha Stuff", "G: " + params[2] + " : " + params[3]);
				}
				else if(params[1].equals("2")){
					params2.addParam("emailauth", params[2]);
					params2.addParam("emailsteamid", params[3]);
				}
				else if(params[1].equals("3")){
					params2.addParam("captcha_gid", params[2]);
					params2.addParam("captcha_text", params[3]);
					params2.addParam("emailauth", params[4]);
					params2.addParam("emailsteamid", params[5]);
				}
			}
			
			
			String result = GetData(params2);
			
			obj = new JSONObject(result);
			
			//boolean[] check = new boolean[2];
			
			if(obj.getString("success").equals("false")){
				results.add("0");
				if(obj.getString("captcha_needed").equals("true")){
					String captcha_gid = obj.getString("captcha_gid");
					//params2 = new RequestParams("GET", "https://steamcommunity.com/public/captcha.php?gid=" + captcha_gid);
					//captcha_image = GetBitMap(params2);
					//check[0] = true;
					results.add(captcha_gid);
					
				}
				else{
					results.add("1");
				}
				
				if(obj.getString("requires_twofactor").equals("true")){
					//check[1] = true;
					results.add("0");
				}
				else{
					results.add("1");
				}
			}
			else{
				results.add("1");
			}
			return results;
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
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Boolean... values) {
		super.onProgressUpdate(values);
		
		if(values[0]){
			
		}
		
		
	}
	
	
	//Returns data from a url
	public String GetData(RequestParams params){
		BufferedReader reader = null;
		String result = null;
		try {
			HttpsURLConnection con = params.setupConnection();
			
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
	protected void onPostExecute(ArrayList<String> result) {
		activity.StatusCheck(result);
	}
}