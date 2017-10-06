package com.facebook.campaigns.main;

import java.util.ArrayList;
import java.util.List;

import com.facebook.campaigns.common.CampaignUpdate;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.TableDataList;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest.Rows;
import com.google.bigquery.main.BQOperations;
import com.google.bigquery.main.GAuthenticate;

public class App{
	
	public static ArrayList<Rows> logChunk = new ArrayList<Rows>();
    
	public static void main(String[] args)
	{

		Bigquery bigquery = GAuthenticate.getAuthenticated();
		
		System.out.println(bigquery);
		
		if(null != bigquery){
		
			if(BQOperations.StructureValidate(bigquery, "campaign_update")){
		    	
				System.out.println("Response Message : Validated the Structure of Table : campaign_update in the Big Query.");
				
				//Commented as in Version 1 of the Code.
				//TableDataList list = BQOperations.GetUpdateRows(bigquery, "campaign_create");
				
				List<TableRow> list = BQOperations.GetUpdateRows(bigquery, "campaign_update");
				
				if(null != list){
					// Commented as in Version 1 of the Code.
					//for(TableRow row : list.getRows()){
					
					for(int arr_i = 0; arr_i < list.size(); arr_i++){

						TableRow row = list.get(arr_i);
						
						if(CampaignUpdate.updateCampaign(row)){
							
							System.out.println("Response Message : The Adset is created Successfully with ID : " + row.getF().get(0).getV());
							
						}
						else{
					
							System.out.println("Response Message : Error while creating Adset with ID : " + row.getF().get(0).getV());
						
						}
						
					}
					
					if(null != logChunk && logChunk.size() > 0){
			    		
			    		if(BQOperations.insertDataRows(bigquery, logChunk)){
			    			System.out.println("Response Message : Logs Added Successfully.");
			    		}else{
			    			System.out.println("Response Message : Error while saving Logs.");
			    		}
			    		
			    	}
				
				}
				
				else{
				
					System.out.println("Response Message : No Updation is requested in Adset Create Table.");
				
				}
				
			}
					
			else{
				
				System.out.println("Response Message : Couldn't validate the Structure of Table : adset_create in the Big Query.");
				System.exit(0);	
			
			}
				    			
		}
		
		else{
			
			System.out.println("Response Message : Couldn't Establish connection with Big Query.");
			System.exit(0);
		
		}
		 
	}
	
}