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
    
    Connection con;
    
    cashtodb(Connection con)
    {
        this.con=con;
    }
    
    void cashfilereadtodb(String cash_file)
    {
        try{
            String truncate="truncate table pspcl.basic_cash_payment";
        
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
                String sql = "INSERT INTO pspcl.basic_cash_payment (account_no,amount,receiptid,receiptdate) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = con.prepareStatement(sql);
                    String s= "0123456789";
                    while ((str = in.readLine()) != null) {
                    
                    try
                        {
                            char c=str.charAt(0);
                            if(s.indexOf(c)!=-1)
                            {
                                String[] tokens = str.split("\\s+");
                                int n=tokens.length;
//                                for(int i=0;i<tokens.length;i++){
//                                    System.out.print(i+"   "+tokens[i]+" ");
//                                }
//                                System.out.print("  "+tokens[n-2]);
//                                System.out.println();
                                
                                String account_no = trim(tokens[3]);
//                                System.out.println(trim(tokens[n-1]));
                                int amount = Integer.parseInt(tokens[n-2]);
                                String date=trim(tokens[0]);
                                String receiptid=trim(tokens[2]);
                                 String receiptdate=  date;
//                            System.out.println("    receiptdate  "+ receiptdate);
                            SimpleDateFormat sdf;
                            java.util.Date parseddate;
                    
                            sdf= new SimpleDateFormat("dd/MM/yy");
                            parseddate=sdf.parse(receiptdate);
                            
                            
                            SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd");
                            receiptdate=print.format(parseddate); 
                           

//                            System.out.println("account no = "+account_no +"  amount = "+amount+"    receiptdate =  "+receiptdate+"    receiptid =  "+receiptid+"  ");
                                statement.setString(1, account_no);
                                statement.setInt(2, amount);
                                statement.setString(3, receiptid);
                                statement.setString(4, receiptdate);
//                              System.out.println(date);  
                                
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
                
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    boolean validateandstoredpayment() throws Exception
    {
        boolean cash_check=false;
        boolean epayment_check=false;
        try
        {
        String query="select c1.account_no from pspcl.basic_cash_payment c1 inner join pspcl.stored_payment c2 on c1.account_no=c2.account_no and c1.receiptid=c2.receiptid and c1.receiptdate=c2.receiptdate";
        Statement st = con.createStatement();
        ResultSet rs =st.executeQuery(query);
        con.commit();
        if(rs.next())
        {
            cash_check=false;
        }
        else
        {
            cash_check=true;
        }
        
        
        query="select c1.account_no from pspcl.basic_e_payment c1 inner join pspcl.stored_payment c2 on c1.account_no=c2.account_no and c1.receiptid=c2.receiptid and c1.receiptdate=c2.receiptdate";
        Statement st1 = con.createStatement();
        ResultSet rs1 =st1.executeQuery(query);
        con.commit();
        if(rs1.next())
        {
            epayment_check=false;
        }
        else
        {
            epayment_check=true;
        }
        
        }
        
    
        catch(Exception e)
        {
            System.out.println(e);
        }
        if(!(cash_check&&epayment_check))
            return false;
        String query="insert into pspcl.stored_payment select receiptid,receiptdate,account_no,amount,current_date from pspcl.basic_cash_payment ";
        Statement st2 = con.createStatement();
        st2.execute(query);
        con.commit();
        query="insert into pspcl.stored_payment select receiptid,receiptdate,account_no,amount,current_date from pspcl.basic_e_payment ";
        Statement st3 = con.createStatement();
        st3.execute(query);
        con.commit();
        
        return true;
    }
    void agg_cash_table()
    {
        try{
            String truncate="truncate table pspcl.agg_cash_payment";
            Statement st = con.createStatement();
            st.execute(truncate);
            con.commit();
            System.out.println("agg_cash_payment table truncated");
            
            String query="insert into pspcl.agg_cash_payment select account_no,sum(amount) from pspcl.basic_cash_payment group by account_no";
            Statement st2 = con.createStatement();
            st2.execute(query);
            con.commit();
            System.out.println("agg_cash_payment table updated");
        
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        
    }
    void con_close() throws Exception
    {
        con.close();
    }
}
