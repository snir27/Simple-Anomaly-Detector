package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimeSeries {

	HashMap<String, List<String>> columns = new HashMap<String, List<String>>();
	List<String> columnNames = new ArrayList<String>();

	public TimeSeries(String csvFileName) {
		String[] CurrentLine;
		Boolean isFirstLine = true;
		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFileName));
			while ((CurrentLine = br.readLine().split(",")) != null) {
				if (isFirstLine) {
					isFirstLine = getColumnNames(CurrentLine, isFirstLine);
				} else {
					for (int i = 0; i < columnNames.size(); i++) {
						if (columns.get(columnNames.get(i)) == null) {
							List<String> tempList = new ArrayList<String>();
							tempList.add(CurrentLine[i]);
							columns.put(columnNames.get(i), tempList);
						} else {
							List<String> tempList = columns.get(columnNames.get(i));
							tempList.add(CurrentLine[i]);
							columns.put(columnNames.get(i), tempList);
						}

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public float[] getColumnByIndex(int index) {
		String columnName = this.columnNames.get(index);
		float[] floatColumnsArr = getColumn(columnName);
		return floatColumnsArr;
	}

	public float[] getColumnByName(String columnName) {
		float[] floatColumnsArr = getColumn(columnName);
		return floatColumnsArr;
	}

	private float[] getColumn(String columnName) {
		List<String> requestedColumns = columns.get(columnName);
		String[] columnsArr = new String[requestedColumns.size()];
		columnsArr = requestedColumns.toArray(columnsArr);
		float[] floatColumnsArr = new float[columnsArr.length];
		for (int i = 0; i < columnsArr.length; i++) {
			floatColumnsArr[i] = Float.parseFloat((columnsArr[i]));
		}
		return floatColumnsArr;
	}

	public int getNumberOfItemsInColumn() {
		return columns.get(columnNames.get(0)).size();
	}

	public List<String> getNames() {
		return this.columnNames;
	}

	public String getNameAtIndex(int i) {
		return this.columnNames.get(i);
	}

	public HashMap<String, List<String>> getColumns() {
		return this.columns;
	}
	
	private Boolean getColumnNames(String[] CurrentLine, Boolean isFirstLine) {
		if (isFirstLine) {
			for (String item : CurrentLine) {
				columnNames.add(item);
				columns.put(item, null);
				isFirstLine = false;
			}
		}
		return isFirstLine;
	}
}
