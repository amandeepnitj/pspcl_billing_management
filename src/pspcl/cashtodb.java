/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pspcl;
import static com.sun.org.apache.xerces.internal.util.XMLChar.trim;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.*;
import java.text.*;
import java.util.Locale;


/**
 *
 * @author amandeep
 */
public class cashtodb {
    
    
    
    void cashfilereadtodb(Connection con,String cash_file)
    {
        try{
            String truncate="truncate table pspcl.cash_payment";
        
        Statement st = con.createStatement();
        st.execute(truncate);
        con.commit();
        System.out.println("cash table table truncated");
        } catch(Exception e){
            System.out.println(e);
        }
        int count=0;
        int batch_size=20;
        try(
                BufferedReader in = new BufferedReader(new FileReader(cash_file))) {
                String str;
                String sql = "INSERT INTO pspcl.cash_payment (account_no,amount) VALUES (?, ?)";
                PreparedStatement statement = con.prepareStatement(sql);
                    String s= "0123456789";
                    while ((str = in.readLine()) != null) {
                    
                    try
                        {
                            char c=str.charAt(0);
                            if(s.indexOf(c)!=-1)
                            {
                                String[] tokens = str.split("\\s+");
                                String account_no = trim(tokens[3]);
                                int amount = Integer.parseInt(tokens[7]);
                                statement.setString(1, account_no);
                                statement.setInt(2, amount);
                                System.out.println("account no = "+account_no +"  amount = "+amount);
                                count++;
                                statement.addBatch();
                                if(count>batch_size)
                                {
                                    statement.executeBatch();
                                    count=0;
                                }
                            }
                        }catch(Exception e)
                        {
                            System.out.println(e);
                            

                        }

                } 
                statement.executeBatch(); // remaining if any
                con.commit();
                con.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
    void agg_cash_table(Connection con)
    {
        try{
            String truncate="truncate table pspcl.agg_cash_payment";
            Statement st = con.createStatement();
            st.execute(truncate);
            con.commit();
            System.out.println("agg_cash_payment table truncated");
            
            String query="insert into pspcl.agg_cash_payment select account_no,sum(amount) from pspcl.cash_payment group by account_no";
            Statement st2 = con.createStatement();
            st2.execute(query);
            con.commit();
            System.out.println("agg_e_payment table updated");
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        
    }
}
