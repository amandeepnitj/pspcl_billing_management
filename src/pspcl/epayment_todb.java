/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pspcl;
import static com.sun.org.apache.xerces.internal.util.XMLChar.trim;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.*;
import java.text.*;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author amandeep
 */
public class epayment_todb {
                        
    Connection con;
    epayment_todb(Connection con)
    {
        this.con= con;
    }
    void agg_epayment_table()
    {
        try{
            String truncate="truncate table pspcl.agg_e_payment";
            Statement st = con.createStatement();
            st.execute(truncate);
            con.commit();
            System.out.println("agg_e_payment table truncated");
            
            String query="insert into pspcl.agg_e_payment select account_no,sum(amount) from pspcl.e_payment group by account_no";
            Statement st2 = con.createStatement();
            st2.execute(query);
            con.commit();
            System.out.println("agg_e_payment table updated");
            
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    void e_paymentbasictable(String file_path)
    {
        int count=0;
        int batch_size=20;
        try(
                BufferedReader in = new BufferedReader(new FileReader(file_path))) {
                String truncate="truncate table pspcl.basic_e_payment";
                Statement st = con.createStatement();
                st.execute(truncate);
                con.commit();
                System.out.println("basic_e_payment table truncated");
                String str;
                String sql = "INSERT INTO pspcl.basic_e_payment (account_no, amount,receiptid,receiptdate,partition_date) VALUES (?, ?, ? ,?,current_date)";
                PreparedStatement statement = con.prepareStatement(sql);
                boolean p=false;
                while ((str = in.readLine()) != null) {
//                    System.out.println(str);
                
                if(str.indexOf("<td>")!=-1&&p){
                    String[] tokens = str.split("</td><td>");
//                    for(int i=0;i<tokens.length;i++)
//                    {
//                        System.out.print(" "+i+"  "+tokens[i]);
//                    }
//                    System.out.println();


                  

                    String receiptid =trim(tokens[0]);
                    String date =trim(tokens[1]);
                    String account_no =trim(tokens[2]);
                    String amount=trim(tokens[3]);
                    //String  = "";
                    receiptid=receiptid.substring(4);
                    
                    
                    String receiptdate=  date;
//                            System.out.println("    receiptdate  "+ receiptdate);
                            SimpleDateFormat sdf;
                            java.util.Date parseddate;
                    
                            try{sdf= new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                            parseddate=sdf.parse(receiptdate);}
                            catch(Exception e)
                            {
                                sdf= new SimpleDateFormat("M/dd/yyyy hh:mm:ss a");
                                parseddate=sdf.parse(receiptdate);
                            }
                            
                            SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd");
                            receiptdate=print.format(parseddate);          
                            int amount1=Integer.parseInt(amount);
                            //System.out.println(amount1);
//                    System.out.println("receipt id "+ receiptid+" date " + date+" acc " +account_no+" amount " + amount);
          
                    statement.setString(1, account_no);
                    statement.setInt(2, amount1);
                    statement.setString(3, receiptid);
                    statement.setString(4, receiptdate);
                    
                    count++;
                    statement.addBatch();
                    
                    if(count>batch_size)
                    {
                        statement.executeBatch();
                        count=0;
                    }
                } 
                if(str.indexOf("<td>")!=-1)
                {
                    p=true;
                }
                }
                statement.executeBatch(); // remaining if any
                con.commit();
            
                }
                catch (Exception e) {
                    System.out.println(e);
                }
        System.out.println("basic_e_payment function completed");
        
    }
    void con_close() throws Exception
    {
        con.close();
    }
}
