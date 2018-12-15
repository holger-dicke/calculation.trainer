package de.dicke.education.calculation.trainer.datahandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// H2 In-Memory Database Example shows about storing the database contents into memory. 
class H2DBAccess implements DbAccess {

	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_CONNECTION = "jdbc:h2:mem:calc;DB_CLOSE_DELAY=-1";

	private static final String DB_USER = "dummyusr";
	private static final String DB_PASSWORD = "dummypwd";

	private String createTableQuery = "CREATE TABLE DATA(id int primary key AUTO_INCREMENT, dataName varchar(255), data varchar(255))";
	private String InsertQuery = "INSERT INTO DATA" + "(dataName, data) values" + "(?,?)";
	private String SelectAllDataQuery = "select * from DATA";
	private String SelectSingleDataQuery = "select * from DATA where dataName is " + "(?)";
	private String DeleteDataRecordQuery = "DELETE FROM DATA WHERE dataName is " + "(?)";

	private static Connection dbConnection;

	public H2DBAccess() throws SQLException {
		dbConnection = getDBConnection();
		createDB();
	}

	@Override
	public void setData(String dataName, String data) throws SQLException {
		dbConnection = getDBConnection();
		insertWithPreparedStatement(dataName, data);
	}
	
	@Override
	public void setData(String dataName, int data) throws SQLException {
		if (data >= Integer.MAX_VALUE) {
			throw new SQLException("MAX int for integer");
		}
		setData(dataName, String.valueOf(data));
	}
	
	@Override
	public void setData(String dataName, long data) throws SQLException {
		setData(dataName, String.valueOf(data));
	}

	@Override
	public void setData(String dataName, boolean data) throws SQLException {
		setData(dataName, String.valueOf(data));
	}
	
	@Override
	public String queryData(String dataName) throws SQLException {
		String retValue = "";
		dbConnection = getDBConnection();

		PreparedStatement selectPreparedStatement = dbConnection.prepareStatement(SelectSingleDataQuery);
		selectPreparedStatement.setString(1, dataName);
		ResultSet rs = selectPreparedStatement.executeQuery();
		if(!rs.isBeforeFirst()){
            System.out.println("No Data Found"); //data not exist
        }
		
		int cnt = 0;
		while (rs.next()) {
			retValue = rs.getString("data");
			cnt++;
		}
		selectPreparedStatement.close();
		
		if (cnt != 1) {
			throw new SQLException("Multiple occurences found: (" + cnt + ") " + dataName);
		} 
		return retValue;
	}

	@Override
	public Map<String, String> queryAllData() throws SQLException {
		Map<String, String> retMap = new HashMap<String, String>();
		dbConnection = getDBConnection();
		PreparedStatement selectPreparedStatement = null;

		selectPreparedStatement = dbConnection.prepareStatement(SelectAllDataQuery);
		ResultSet rs = selectPreparedStatement.executeQuery();
		while (rs.next()) {
			retMap.put(rs.getString("dataName"), rs.getString("data"));
		}
		selectPreparedStatement.close();
		return retMap;
	}

	protected void createDB() throws SQLException {
		try {
			dbConnection.setAutoCommit(false);
			PreparedStatement createPreparedStatement = null;
			createPreparedStatement = dbConnection.prepareStatement(createTableQuery);
			createPreparedStatement.executeUpdate();
			createPreparedStatement.close();
		} catch (SQLException e) {
			if (e.getErrorCode() != 42101) { // code # 42101 is "DB Table does already exist
				throw e;
			} else {
				// System.out.println("DB table does already exist");
			}
		}
	}

	void deleteWithPreparedStatement (String dataName) throws SQLException {
		dbConnection = getDBConnection();
		PreparedStatement delete = null;
		try {
			dbConnection.setAutoCommit(false);
			delete = dbConnection.prepareStatement(DeleteDataRecordQuery);
			delete.setString(1, dataName);
			delete.executeUpdate();
			delete.close();
		} catch (SQLException e) {
			System.out.println("Exception Message " + e.getLocalizedMessage());
			throw (e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insertWithPreparedStatement(String dataName, String data) throws SQLException {
		deleteWithPreparedStatement(dataName);
		dbConnection = getDBConnection();
		PreparedStatement insertPreparedStatement = null;
		try {
			dbConnection.setAutoCommit(false);
			insertPreparedStatement = dbConnection.prepareStatement(InsertQuery);
			insertPreparedStatement.setString(1, dataName);
			insertPreparedStatement.setString(2, data);
			insertPreparedStatement.executeUpdate();
			insertPreparedStatement.close();

		} catch (SQLException e) {
			System.out.println("Exception Message " + e.getLocalizedMessage());
			throw (e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized Connection getDBConnection() {

		if (dbConnection == null) {
			System.out.println("DB connection is null ....");
			try {
				Class.forName(DB_DRIVER);
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
			try {
				dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
				System.out.println("Transaction mode = " + dbConnection.getTransactionIsolation());
				return dbConnection;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		return dbConnection;

	}





}
