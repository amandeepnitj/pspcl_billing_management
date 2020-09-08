/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pspcl;
import java.sql.*;
import java.io.File;  
import java.io.FileInputStream;  
import java.util.Iterator;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author amandeep
 */
public class Pspcl {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
       
     /*   //readmainfile
        Connection con0 =new jdbcconnect().initconn();
        con0.setAutoCommit(false);
        String filepath="C:\\java lib\\upload data bg4 cy-2\\web-T53024.txt";
        new readandstoremainfile().readmainfile(con0,filepath);
       */ 
        
        //check account no
        Connection con1 =new jdbcconnect().initconn();
        con1.setAutoCommit(false);
        boolean p= new readandstoremainfile().checkaccountno(con1);
        System.out.print(p);
        
    /*    
        //fetch e-payment data
        Connection con =new jdbcconnect().initconn();
        con.setAutoCommit(false);
        String excel_dir = "C:\\Users\\amandeep\\Desktop\\e_payment.xlsx";
        con.setAutoCommit(false);
        new epayment_todb().paymenttodb(con,excel_dir);
        
        //udpated agg e-payment table
        Connection con1 =new jdbcconnect().initconn();
        con1.setAutoCommit(false);
        new epayment_todb().agg_table(con1);
      
      
      
        //fetch the cash file
        String cashfile = "C:\\Users\\amandeep\\Desktop\\cashfile.txt";
        Connection con2 =new jdbcconnect().initconn();
        con2.setAutoCommit(false);
        new cashtodb().cashfilereadtodb(con2,cashfile);
        
        
        // add agg_cash_data
        Connection con3 =new jdbcconnect().initconn();
        con3.setAutoCommit(false);
        new cashtodb().agg_cash_table(con3);
        
      */  
          
}  
}  
    
    
