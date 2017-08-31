package com.facebook.campaigns.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.facebook.campaigns.main.App;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest.Rows;

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
			
			Rows logsRow = new Rows();
			
			HashMap<String, Object> logsMap = new HashMap<String, Object>();
			
			logsMap.put("account_id", String.valueOf(row.getF().get(0).getV()));
			logsMap.put("operation", "UPDATE");
			logsMap.put("table_name", "CAMPAIGN_UPDATE");
			logsMap.put("campaign_id", String.valueOf(row.getF().get(1).getV()));
			logsMap.put("campaign_name", String.valueOf(row.getF().get(2).getV()));
			logsMap.put("status_code", response.getStatusLine().getStatusCode());
			logsMap.put("response_message", buffer.toString());
			logsMap.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
			
			logsRow.setJson(logsMap);
			App.logChunk.add(logsRow);
			
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