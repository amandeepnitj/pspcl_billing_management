/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pspcl;
import java.sql.*;


/**
 *
 * @author amandeep
 */
public class jdbcconnect {
    Connection conn;
    jdbcconnect()
    {
        
    }
    
    Connection initconn() throws SQLException, ClassNotFoundException
    {
        
            Class.forName("com.mysql.jdbc.Driver");  
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306","root","iamthebest");  
            
        return conn;
    }
    
}
