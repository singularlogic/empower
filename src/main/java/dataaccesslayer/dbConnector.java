/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

import java.sql.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author eleni
 */
public class dbConnector {
    
    private Statement stmt;
	private String stmtStr;
	private Connection dbConnection;

	public void dbOpen()
	throws ClassNotFoundException, SQLException,
        InstantiationException, IllegalAccessException
	{                   
                String url = "jdbc:mysql://localhost:3306/";
                String dbName = "empower_database";
                String driver = "com.mysql.jdbc.Driver";
                String userName = "root"; 
                String password = "ego";
  
                Class.forName(driver).newInstance();
                dbConnection = DriverManager.getConnection(url+dbName,userName,password);
                stmt = dbConnection.createStatement();		
	}

	public ResultSet dbQuery(String sqlQuery)
	throws SQLException
	{
		ResultSet rs;
		clearRequestFields();
		this.stmtStr = new String(sqlQuery);
		rs = stmt.executeQuery(stmtStr);
		return(rs);
	}

	public int dbUpdate(String updateString)
	throws SQLException
	{
		//int updatesMade;
                int key=1;
		clearRequestFields();
		this.stmtStr = new String(updateString);
		//updatesMade = stmt.executeUpdate(stmtStr);
		//return(updatesMade);
                
                stmt.executeUpdate(updateString,Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = stmt.getGeneratedKeys();
                if ( rs.next() ) {
                // Retrieve the auto generated key(s).
                key = rs.getInt(1);
                }   
                return key;
                
	}

	public void dbClose()
	throws SQLException
	{
		clearRequestFields();
		dbConnection.close();
	}

	private void clearRequestFields()
	{
		stmtStr = null;
	}

    
}
