package com.facebook.campaigns.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.api.services.bigquery.model.TableRow;

public class CampaignUpdate {

	private static final String URL  = "https://graph.facebook.com";
	private static final String VERSION = "v2.9";
	private static final String SESSION_TOKEN = "CAAWXmQeQZAmcBANADF6ew1ZBXAAifj7REIcHmbTVjkAR5q6GAnRjrpcuVhhV435LHMXpb8HzUKzQaUU4uwkxIl5xpYSgzUNog43JX4qxe0pqVBvjHZCsPfgIpRRGY7xfFC2hb1Hi1s9EH0IhQu4KlnTGcsdgIq5FN2ufeNHOeEB9YGck36aah1rPHrdi10ZD";
	
	public static Boolean updateCampaign(TableRow row){
	
		try{
				
			String custom_url = URL + "/" + VERSION + "/" + String.valueOf(row.getF().get(1).getV());
			
			HttpClient reqclient = new DefaultHttpClient();
			HttpPost reqpost = new HttpPost(custom_url);
			
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("access_token",SESSION_TOKEN));
			parameters.add(new BasicNameValuePair("name", String.valueOf(row.getF().get(2).getV())));
			//parameters.add(new BasicNameValuePair("promoted_object", "{\"product_catalog_id\":\""+ client.getCatalog_id() +"\"}"));
			parameters.add(new BasicNameValuePair("status",String.valueOf(row.getF().get(3).getV())));
			
			reqpost.setEntity(new UrlEncodedFormEntity(parameters));
			
			System.out.println("Sending POST request to URL : " + custom_url);
			System.out.println("POST Parameters : " + parameters);
			
			HttpResponse response = reqclient.execute(reqpost);
			
			System.out.println("Response Code : "+ response.getStatusLine().getStatusCode());

			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			String line = "";
			
			while((line = reader.readLine()) != null){
				buffer.append(line);
			}
			
			System.out.println(buffer.toString());
			
			if(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300){
				
				JSONObject object = new JSONObject(buffer.toString());
				
				if(object.has("id")){
					return true;
				}
				else{
					System.out.println("Response Message : Request Failed for the Campaign as there is no ID returned by Facebook.");
					return false;
				}
				
			}
			else{
				System.out.println("Response Message : Request Failed for Campaign Creation for Campaign with ID.");
				return false;
			}
	
		}
		catch(Exception e){
			System.out.println(e);
			return false;
		}
			
	}
	
}