/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yelpPacket;

/**
 *
 * @author Tom
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnect {
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        
        String host = "localhost";//Needs change base on your database address
        String port = "1521";//Needs change base on your database address
        String dbName = "orcl";//Needs change base on your database address
        String userName = "scott";//Needs change base on your database address
        String password = "tiger";//Needs change base on your database address
        
        
        String dbURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName; 
        return DriverManager.getConnection(dbURL, userName, password); 
    }
    
    public void closeConnection(Connection con) {
        try {
         con.close();
        } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
        }
    }
}
