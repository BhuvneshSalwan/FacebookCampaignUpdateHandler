package com.google.bigquery.main;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.TableDataList;
import com.google.api.services.bigquery.model.TableList;
import com.google.api.services.bigquery.model.TableList.Tables;

public class BQOperations {
	
	private static final String PROJECT_ID = "stellar-display-145814";
	private static final String DATASET_ID = "table_output";

	public static Boolean StructureValidate(Bigquery bigquery, String TABLE_ID){
		try {
			TableList tables = bigquery.tables().list(PROJECT_ID, DATASET_ID).execute();	
			System.out.println(tables);
			if(null != tables && null != tables.getTables()){
				for(Tables table : tables.getTables()){
					if(table.getId().equalsIgnoreCase(PROJECT_ID + ":" + DATASET_ID + "." + TABLE_ID)){
						return true;
					}
				}
			}	
			return false;
		} catch (Exception e) {
			System.out.println(e);
			return false;	
		}
	}
	
	public static TableDataList GetUpdateRows(Bigquery bigquery, String TABLE_ID){
		try {
			TableDataList rows = bigquery.tabledata().list(PROJECT_ID, DATASET_ID, TABLE_ID).execute();
			if(rows.getTotalRows().intValue() != 0){
				return rows;
			}
			else{
				return null;
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
}