package de.dicke.education.calculation.trainer.datahandling;

import java.sql.SQLException;
import java.util.Map;

public interface DbAccess {
	
	public String queryData (String dataName) throws SQLException; 
	public Map<String, String> queryAllData () throws SQLException; 
	public void setData (String dataName, String data) throws SQLException;
	public void setData (String dataName, int data) throws SQLException;
	public void setData (String dataName, long data) throws SQLException;
	public void setData(String dataName, boolean data) throws SQLException;

}
