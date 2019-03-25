package com.datasoft.javaengineersassessment.solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.datasoft.javaengineersassessment.utils.IO;

public class Solution implements Runnable{
	
	
	/**
	 * Application entry to your solution
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		System.out.println("All set ...");
		try {
			readJSONData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Goodbye :)");
	
	}
	
	@SuppressWarnings("unchecked")
	public void readJSONData() throws IOException {		
        // read number of test cases, n
        int n = readn();
        int [] nD = new int[n];
        int [] nT = new int[n];
        
        LinkedList<ArrayList<ArrayList<Object>>> tests = new LinkedList<ArrayList<ArrayList<Object>>>();
		for (int i = 0; i < n; i++) {
			// read nT nD
			int [] nTnD = readnTnD();
			nT[i] = nTnD[0];
			nD[i] = nTnD[1];
			
			// read and find table names and orders
			String [][] tableNamesAndOrders = readTableNamesAndOrders(nT[i]);
			
			// create the tables
			ArrayList<ArrayList<Object>> currentTest = new ArrayList<>();
			for (int j = 0; j < tableNamesAndOrders.length; j++) {
				ArrayList<Object> table = new ArrayList<Object>();
				table.add(tableNamesAndOrders[j][0]);
				table.add(tableNamesAndOrders[j][1]);
				ArrayList<ArrayList<Object>> columnList = new ArrayList<ArrayList<Object>>();
				table.add(columnList);
				currentTest.add(table);
			}
			tests.add(currentTest);
			
			// read and store data for every JSONObject
			for (int j = 0; j < nD[i]; j++) {
				// to keep track and help insert missing columns
        		ArrayList<Object> prevColumn = null;
	        	while (true) {
	        		String input = IO.readLine().replaceAll("[\\,\"]", "");
	        		if (input.equals("{")) {
			        	// do nothing 
			        } else {
			        	ArrayList<Object> initialTable = tests.get(i).get(0);
		        		ArrayList<Object> initialTableColumnList = (ArrayList<Object>) initialTable.get(2);
		        		
		        		if (input.equals("}")) {
		        			// end of JSON Object so break while loop
		        			break;
		        		} else {
		        			String [] keyValuePair = input.split(":");
		        			// 
	        				int correctIndex = 0;
		        			if (keyValuePair[1].equals("{")) {
		        				// add inner JSON Object to initial table
		        				if (initialTableColumnList.isEmpty()) {
		        					// no column data yet so create new column
		        					ArrayList<Object> column = new ArrayList<Object>();
		        					column.add(keyValuePair[0]);
		        					HashMap<Integer, String> columnData = new HashMap<Integer, String>();
		        					column.add(columnData);
		        					prevColumn = column;
		        				} else {
		        					// check if current column already exists
		        					boolean columnExists = false;
		        					for (int k = 0; k < initialTableColumnList.size(); k ++) {
		        						ArrayList<Object> currentColumn = (ArrayList<Object>) initialTableColumnList.get(k);
		        						if (currentColumn.get(0).equals(keyValuePair[0])) {
		        							columnExists = true;
		        							prevColumn = currentColumn;
		        							correctIndex = k;
		        							break;
		        						} 
		        					}
		        					
		        					// column does not exist, so create new column
		        					if (!columnExists) {
		        						ArrayList<Object> column = new ArrayList<Object>();
			        					column.add(keyValuePair[0]);
			        					HashMap<Integer, String> columnData = new HashMap<Integer, String>();
			        					for (int k = 0; k < j ; k++) {
				        					columnData.put(k, "null");
			        					}
			        					
			        					// add new column in correct position in column list
			        					column.add(columnData);
			        					correctIndex = initialTableColumnList.indexOf(prevColumn) + 1;
			        					initialTableColumnList.add(correctIndex, column);
		        						prevColumn = column;
		        						columnExists = false;
		        					}
		        				}
		        				
		        				int innerTableIndex = 0;
		        				for (int k = 0; k < tests.get(i).size(); k++) {
		        					Object tableName = tests.get(i).get(k).get(0);
		        					if (tableName.equals(keyValuePair[0])) {
		        						innerTableIndex = k;
		        						break;
		        					}
		        				}
		        				
		        				ArrayList<Object> innerTable = tests.get(i).get(innerTableIndex);
				        		ArrayList<Object> innerTableColumnList = (ArrayList<Object>) innerTable.get(2);						        		
				        		ArrayList<Object> innerPrevColumn = null;
				        		
			        			int initialTableColumnValueForInnerTable = 0;
				        		while (true) {
				        			String innerObjectInput = IO.readLine().replaceAll("[\\,\"]", "");
				        			if (innerObjectInput.contains("}")) {
				        				break;
				        			}
				        			String [] innerKeyValuePair = innerObjectInput.split(":");
				        			if (innerTableColumnList.isEmpty()) {
			        					// no column data yet so create new column
			        					ArrayList<Object> column = new ArrayList<>();
			        					column.add(innerKeyValuePair[0]);
			        					HashMap<Integer, String> columnData = new HashMap<>();
			        					columnData.put(j, innerKeyValuePair[1]);
			        					column.add(columnData);
			        					innerTableColumnList.add(column);
			        					innerPrevColumn = column;
			        					initialTableColumnValueForInnerTable = 0;
			        					
			        				} else {
			        					// check if current column already exists
			        					boolean columnExists = false;
			        					for (int k = 0; k < innerTableColumnList.size(); k ++) {
			        						ArrayList<Object> currentColumn = (ArrayList<Object>) innerTableColumnList.get(k);
			        						if (currentColumn.get(0).equals(innerKeyValuePair[0])) {
			        							HashMap<Integer, String> currentColumnData = (HashMap<Integer, String>) currentColumn.get(1);
			        							currentColumnData.put(j, innerKeyValuePair[1]);
			        							columnExists = true;
			        							innerPrevColumn = currentColumn;
			        							initialTableColumnValueForInnerTable = currentColumnData.size() - 1;
			        						} 
			        					}
			        					
			        					// column does not exist, so create new column
			        					if (!columnExists) {
			        						ArrayList<Object> column = new ArrayList<>();
				        					column.add(innerKeyValuePair[0]);
				        					HashMap<Integer, String> columnData = new HashMap<>();
				        					for (int p = 0; p < j ; p++) {
					        					columnData.put(p, "null");
				        					}
				        					columnData.put(j, innerKeyValuePair[1]);
				        					
				        					// add new column in correct position in column list
				        					int correctIndexOfTable = innerTableColumnList.indexOf(innerPrevColumn);
				        					column.add(columnData);
				        					innerTableColumnList.add(correctIndexOfTable + 1, column);
			        						columnExists = false;
			        						innerPrevColumn = column;
		        							initialTableColumnValueForInnerTable = j;
			        					}
			        				}
				        		}
				        		HashMap<Integer, String> initialTableColumnDataForInnerTable = (HashMap<Integer, String>) ((ArrayList<Object>)initialTableColumnList.get(correctIndex)).get(1);
    							initialTableColumnDataForInnerTable.put(j, Integer.toString(initialTableColumnValueForInnerTable + 1));
		        			} else if (keyValuePair[1].equals("[")) {
		        				int innerTableIndex = 0;
		        				for (int r = 0; r < tests.get(i).size(); r++) {
		        					Object tableName = tests.get(i).get(r).get(0);
		        					if (tableName.equals(keyValuePair[0])) {
		        						innerTableIndex = r;
		        						break;
		        					}
		        				}
		        				
		        				ArrayList<Object> innerTable = tests.get(i).get(innerTableIndex);
				        		ArrayList<Object> innerTableColumnList = (ArrayList<Object>) innerTable.get(2);

		        				if (innerTableColumnList.isEmpty()) {
		        					// create and add two columns 
			        				ArrayList<Object> initialTableNameColumn = new ArrayList<>();
			        				initialTableNameColumn.add(initialTable.get(0));
		        					ArrayList<Object> initialTableNameColumnData = new ArrayList<Object>();
		        					initialTableNameColumn.add(initialTableNameColumnData);
		        					innerTableColumnList.add(initialTableNameColumn);
			        				ArrayList<Object> currentTableNameColumn = new ArrayList<>();
			        				currentTableNameColumn.add(keyValuePair[0]);
		        					ArrayList<Object> tableNameColumnData = new ArrayList<Object>();
		        					currentTableNameColumn.add(tableNameColumnData);
		        					innerTableColumnList.add(currentTableNameColumn);
		        					innerTable.add("JSONArray");
		        				}
		        				while (true) {
	        						String innerArrayInput = IO.readLine().replaceAll("[\\,\"]", "");
	        						if (innerArrayInput.contains("]")) {
	        							// JSONArray ends here so break
	        							break;
	        						} else {
	        							ArrayList<Object> initialTableNameColumn = (ArrayList<Object>) innerTableColumnList.get(0);
	        							ArrayList<Object> initialTableColumnData = (ArrayList<Object>) initialTableNameColumn.get(1);
	        							initialTableColumnData.add(Integer.toString(j+1));
	        							
	        							ArrayList<Object> tableNameColumn = (ArrayList<Object>) innerTableColumnList.get(1);
	        							ArrayList<Object> tableNameColumnData = (ArrayList<Object>) tableNameColumn.get(1);
	        							tableNameColumnData.add(innerArrayInput);
	        						}
	        					}
		        			} else {
		        				if (initialTableColumnList.isEmpty()) {
		        					// no column data yet so create new column
		        					ArrayList<Object> column = new ArrayList<>();
		        					column.add(keyValuePair[0]);
		        					HashMap<Integer, String> columnData = new HashMap<>();
		        					columnData.put(j, keyValuePair[1]);
		        					column.add(columnData);
		        					initialTableColumnList.add(column);
		        					prevColumn = column;
		        				} else {
		        					// check if current column already exists
		        					boolean columnExists = false;
		        					for (int k = 0; k < initialTableColumnList.size(); k ++) {
		        						ArrayList<Object> currentColumn = (ArrayList<Object>) initialTableColumnList.get(k);
		        						if (currentColumn.get(0).equals(keyValuePair[0])) {
		        							HashMap<Integer, String> currentColumnData = (HashMap<Integer, String>) currentColumn.get(1);
		        							currentColumnData.put(j, keyValuePair[1]);
		        							columnExists = true;
		        							prevColumn = currentColumn;
		        						} 
		        					}
		        					
		        					// column does not exist, so create new column
		        					if (!columnExists) {
		        						ArrayList<Object> column = new ArrayList<>();
			        					column.add(keyValuePair[0]);
			        					HashMap<Integer, String> columnData = new HashMap<>();
			        					for (int p = 0; p < j ; p++) {
				        					columnData.put(p, "null");
			        					}
			        					columnData.put(j, keyValuePair[1]);
			        					
			        					// add new column in correct position in column list
			        					correctIndex = initialTableColumnList.indexOf(prevColumn);
			        					column.add(columnData);
			        					initialTableColumnList.add(correctIndex + 1, column);
		        						columnExists = false;
		        						prevColumn = column;
		        					}
		        				}
		        			}
		        		}
		        	}
		        }	
			}
		} 
		// print the tables in desired format
		printAll(tests, nT, nD);
	}
	
	@SuppressWarnings("unchecked")
	public void printAll(LinkedList<ArrayList<ArrayList<Object>>> tests, int [] nT, int [] nD) {
		// for every test
		for(int i = 0; i < tests.size(); i++) {
			System.out.println("Test# " + (i + 1));
			// for every table
			for (int j = 0; j < nT[i]; j++) {
				// print table name
				System.out.println(tests.get(i).get(j).get(0));

				StringBuilder sb = new StringBuilder();
				sb.append("id ");
				
				// print column names
				ArrayList<Object> columnList = (ArrayList<Object>) tests.get(i).get(j).get(2);
				for (int k = 0; k < columnList.size(); k++) {
					sb.append(((ArrayList<Object>)columnList.get(k)).get(0));
					sb.append(" ");
				}
				System.out.println(sb.toString().trim());
				sb.setLength(0);
				
				// check order of table 
				boolean isDesc = false;
				if (tests.get(i).get(j).get(1).equals("desc")) {
					isDesc = true;
				}
				
				if (tests.get(i).get(j).size() == 4) {
					// print JSONArray
					int totalEntry = ((ArrayList<Object>)((ArrayList<Object>)columnList.get(1)).get(1)).size();
					if (isDesc) {
						for(int k = totalEntry - 1 ; k >= 0 ; k--) {
							printJSONArray(sb, columnList, k);
						}
					} else {
						for (int k = 0; k < totalEntry; k++) {
							printJSONArray(sb, columnList, k);
						}
					}
				} else {
					// print JSONObject
					if (isDesc) {
						for (int k = nD[i] - 1; k >= 0; k--) {
							printJSONObject(sb, columnList, k);
						}
					} else {
						for (int k = 0; k < nD[i]; k++) {
							printJSONObject(sb, columnList, k);
						}
					}
				}
				System.out.print("\n");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void printJSONObject(StringBuilder sb, ArrayList<Object> columnList, int index) {
		sb.append(index+1);
		sb.append(" ");
		for (int i = 0; i < columnList.size(); i++) {
			ArrayList<Object> column = (ArrayList<Object>) columnList.get(i);
			HashMap<Integer, Object> columnData = (HashMap<Integer, Object>) column.get(1);
			sb.append(columnData.get(index));
			sb.append(" ");
		}
		System.out.println(sb.toString().trim());
		sb.setLength(0);
	}
	
	@SuppressWarnings("unchecked")
	public void printJSONArray(StringBuilder sb, ArrayList<Object> columnList, int index) {
		sb.append(index + 1);
		sb.append(" ");
		for (int i = 0; i < columnList.size(); i++) {
			ArrayList<Object> column = (ArrayList<Object>) columnList.get(i);
			ArrayList<Object> columnData = (ArrayList<Object>) column.get(1);
			sb.append(columnData.get(index));
			sb.append(" ");
		}
		System.out.println(sb.toString().trim());
		sb.setLength(0);
	}
	
	public String [][] readTableNamesAndOrders(int nT) throws IOException {
		String [][] tableNamesAndOrder = new String[nT][2];
		try {
			String input = IO.readLine();
			String [] inputArray = input.split("\\s+");
		          
            if (inputArray.length != nT) {
            	throw new IOException("Expected "+ nT + " table names but got "+ tableNamesAndOrder.length);
            }
              
            String [] nameOrderSplit = new String[2];
            for (int i = 0; i < inputArray.length; i++) {
            	if (inputArray[i].contains("desc")) {
            		nameOrderSplit = inputArray[i].split("\\(");
            		tableNamesAndOrder[i][0] = nameOrderSplit[0];
            		tableNamesAndOrder[i][1] = "desc";
              	} else {
              		nameOrderSplit = inputArray[i].split("\\(");
              		tableNamesAndOrder[i][0] = nameOrderSplit[0];
              		tableNamesAndOrder[i][1] = "asc";
              	}
              }
            return tableNamesAndOrder;
		} catch (IOException e) {
			throw new IOException();
		}
	}
	
	public int[] readnTnD() throws NumberFormatException {
		
		String input = IO.readLine();
		String [] inputArray = input.split("\\s+");
		int [] values = new int[2];
		try {
			values[0] = Integer.parseInt(inputArray[0]);
			values[1] = Integer.parseInt(inputArray[1]);
			return values;
	    } catch (NumberFormatException e) {
	    	throw new NumberFormatException();
	    }
	}
	
	public int readn() throws NumberFormatException{
		try {
	      	return Integer.parseInt(IO.readLine());
	      } catch (NumberFormatException e) {
	    	  throw new NumberFormatException();
	      }
	}
}
