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
       
        Connection con_main =new jdbcconnect().initconn();
        Connection con_cashpayment =new jdbcconnect().initconn();
        Connection con_epayment =new jdbcconnect().initconn();
        con_main.setAutoCommit(false);
        con_cashpayment.setAutoCommit(false);
        con_epayment.setAutoCommit(false);
        readandstoremainfile main_obj = new readandstoremainfile(con_main);
        epayment_todb epayment_obj = new epayment_todb(con_epayment);
        cashtodb cashpayment_obj = new cashtodb(con_cashpayment);
        
       //main --- readmainfile        
        String filepath="C:\\java lib\\upload data bg4 cy-2\\web-T53024.txt";
        main_obj.readmainfile(filepath);
    
    
    
        //main --- check account no from main file
        boolean p= main_obj.checkaccountno();
        System.out.println(p);
    
    
    
    
    //main --- get max date and insert to main table
        main_obj.setdbdate();

                  

        //epay -- fetch e-payment data
        
        String excel_dir = "C:\\Users\\amandeep\\Desktop\\payment_1.xls";
        epayment_obj.e_paymentbasictable(excel_dir);
        
    
        //epay -- udpated agg e-payment table
        epayment_obj.agg_epayment_table();
      




        //cash -- fetch the cash file
        String cashfile = "C:\\Users\\amandeep\\Desktop\\cashfile.txt";
        
        cashpayment_obj.cashfilereadtodb(cashfile);
        
        
        //cash --  validate and store payments
        
        boolean p_1= cashpayment_obj.validateandstoredpayment();
        System.out.println(p_1);
        
        
        //cash --  add agg_cash_data
        
        cashpayment_obj.agg_cash_table();
              

       main_obj.con_close();
       cashpayment_obj.con_close();
       epayment_obj.con_close();
       
          
}  
}  
    
    
