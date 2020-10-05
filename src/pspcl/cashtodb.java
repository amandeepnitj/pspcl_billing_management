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
import java.time.LocalDate;
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
    
    void cashfilereadtodb(String cash_file,int c_1)
    {
        
        int count=0;
        int batch_size=20;
        if(c_1==1)
        {try(
                BufferedReader in = new BufferedReader(new FileReader(cash_file))) {
                String str;
                String sql = "INSERT INTO pspcl.basic_cash_payment (account_no,amount,receiptid,receiptdate,partition_date) VALUES (?, ?, ?, ?,current_date)";
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
                                String amount_s="";
                                for(int i=n-1;i>=0;i--)
                                {
                                    char c1=tokens[i].charAt(0);
                                    if(s.indexOf(c1)!=-1)
                                    {
                                        amount_s=tokens[i];
                                        break;
                                    }
                                    
                                }
                                int amount = Integer.parseInt(amount_s);
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
    }
    
    int validatewithstoredpayment() throws Exception
    {
        boolean p;
//        String query="drop temporary table pspcl.cash_temp";
//        Statement st = con.createStatement();
//        boolean p =st.execute(query);
//        
//         query="drop temporary table pspcl.epayment_temp";
//         st = con.createStatement();
//         p =st.execute(query);
        boolean cash_check=false;
        boolean epayment_check=false;
        
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
        
        query="create temporary table pspcl.cash_temp as select t1.id as id from pspcl.basic_cash_payment t1 inner join pspcl.stored_payment t2 on t1.account_no=t2.account_no and t1.receiptid=t2.receiptid and t1.receiptdate=t2.receiptdate";
        st = con.createStatement();
        p =st.execute(query);
        query="delete from pspcl.basic_cash_payment  where id in (select id from pspcl.cash_temp)";
        st = con.createStatement();
        p =st.execute(query);
        
        
        query="create temporary table pspcl.epayment_temp as select t1.id as id from pspcl.basic_e_payment t1 inner join pspcl.stored_payment t2 on t1.account_no=t2.account_no and t1.receiptid=t2.receiptid and t1.receiptdate=t2.receiptdate";
        st = con.createStatement();
        p =st.execute(query);
        query="delete from pspcl.basic_e_payment  where id in (select id from pspcl.epayment_temp)";
        st = con.createStatement();
        p =st.execute(query);
        
        
        if(cash_check==true && epayment_check==true){
            return 0;}
        else if(cash_check==false && epayment_check==true){
            return 1;}
        else if(cash_check==true && epayment_check==false)
        {
            return 2;
        }
        else
        {
                return 3;
        }
            
        
        /*String query="insert into pspcl.stored_payment select receiptid,receiptdate,account_no,amount,current_date from pspcl.basic_cash_payment ";
        Statement st2 = con.createStatement();
        st2.execute(query);
        con.commit();
        query="insert into pspcl.stored_payment select receiptid,receiptdate,account_no,amount,current_date from pspcl.basic_e_payment ";
        Statement st3 = con.createStatement();
        st3.execute(query);
        con.commit();
        */
        
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
    //0-- both file exists 1-- e_payment exist 2-- cashpayment exists 3-- none files exists
    void agg_stored_payment(int n,int bg,int cycle) throws Exception
    {
        String query="truncate table pspcl.agg_stored_payment";
        Statement st2 = con.createStatement();
        st2.execute(query);
        con.commit();
        System.out.println("agg_stored_payment table truncated");        
        
        String start_date="";
        //get start date from main table
        try{
        
            
            
            query="select start_date from pspcl.agg_main_table where cycle="+cycle+" and billing_group = "+bg+" limit 1";
            Statement st1 = con.createStatement();
            ResultSet rs1 =st1.executeQuery(query);
            String date ="";
            con.commit();
            if(rs1.next())
            {
                date=rs1.getString("start_date");
            }
             LocalDate olddate = LocalDate.parse(date);
            LocalDate newdate=olddate.minusDays(50);
            start_date= newdate+"";
            System.out.println("startdate= "+start_date);
        
    }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        
        
       
        //insert epayment data to agg_stored_payment
        if(n==0 ||n==1)
        {query="insert into pspcl.agg_stored_payment SELECT p.account_no,p.amount,p.receiptid,p.receiptdate,'e' as mode from pspcl.basic_e_payment p,pspcl.agg_main_table m where m.account_no=p.account_no and p.receiptdate>= '"+start_date+"'";
        Statement st3 = con.createStatement();
        st3.execute(query);
        con.commit();
        System.out.println("agg_stored_payment table insertion -- epayment");        
        }
        
        //insert cashpayment data to agg_stored_payment
        if(n==0||n==2)
        {query="insert into pspcl.agg_stored_payment SELECT p.account_no,p.amount,p.receiptid,p.receiptdate,'c' as mode from pspcl.basic_cash_payment p,pspcl.agg_main_table m where m.account_no=p.account_no and p.receiptdate>='"+start_date+"'";
        Statement st4 = con.createStatement();
        st4.execute(query);
        con.commit();
        System.out.println("agg_stored_payment table insertion -- cash_payment");        
        }
        //data stored to stored_payment from agg_stored_payment
        if(n<=2)
        {query="insert into pspcl.stored_payment select receiptid,receiptdate,account_no,amount,current_date from pspcl.agg_stored_payment ";
        Statement st5 = con.createStatement();
        st5.execute(query);
        con.commit();
        System.out.println("data stored to stored_payment from agg_stored_payment");        
        }
        //retention period enabled
        query="delete from pspcl.stored_payment where partition_date<date_sub(current_date,INTERVAL 5 MONTH) ";
        Statement st6 = con.createStatement();
        st6.execute(query);
        con.commit();
        System.out.println("retention period enabled");        
        
        //final_payment table truncated
        query="truncate table pspcl.final_payment";
        Statement st7 = con.createStatement();
        st7.execute(query);
        con.commit();
        System.out.println("final_payment table truncated");        
        
        //final_payment table insertion
        if(n<=2)
        {query="insert into pspcl.final_payment select account_no,sum(amount),mode from pspcl.agg_stored_payment group by account_no,mode";
        Statement st8 = con.createStatement();
        st8.execute(query);
        con.commit();
        System.out.println("final_payment table insertion");        
        }
        
    }
    void con_close() throws Exception
    {
        con.close();
    }
}
