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
    
    void remove_duplicate()
    {
         try{
            String truncate="create temporary table pspcl.cashpayment_temp_1 as SELECT distinct account_no, amount,receiptid,receiptdate FROM pspcl.basic_cash_payment";
            Statement st = con.createStatement();
            st.execute(truncate);
            con.commit();
            truncate="truncate table pspcl.basic_cash_payment";
            st = con.createStatement();
            st.execute(truncate);
            con.commit();
            truncate="insert into pspcl.basic_cash_payment (account_no, amount,receiptid,receiptdate,partition_date) select account_no, amount,receiptid,receiptdate,current_date as partition_date from pspcl.cashpayment_temp_1";
            st = con.createStatement();
            st.execute(truncate);
            con.commit();
            
            truncate="drop temporary table pspcl.cashpayment_temp_1";
            st = con.createStatement();
            st.execute(truncate);
            con.commit();
            
            System.out.println("remove cash duplicate done");
            } catch(Exception e){
                System.out.println(e);
            }
         
         
    }
    
    String cashfilereadtodb(String cash_file)
    {
        
        try{
            String truncate="create temporary table pspcl.cashpayment_temp( "+
            "account_no varchar(50), "+
            "amount int, "+
            "receiptid varchar(50), "+
            "receiptdate date)";
        
        Statement st = con.createStatement();
        st.execute(truncate);
        con.commit();
        System.out.println("pspcl.cashpayment_temp table created");
        } catch(Exception e){
            System.out.println(e);
            return e+"";
        }
        
        int count=0;
        int batch_size=20;
        
        try(
                BufferedReader in = new BufferedReader(new FileReader(cash_file))) {
                String str;
                String sql = "INSERT INTO pspcl.cashpayment_temp (account_no,amount,receiptid,receiptdate) VALUES (?, ?, ?, ?)";
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
//                            return e+"";

                        }

                } 
                statement.executeBatch(); // remaining if any
                con.commit();
                
        }
        catch (Exception e) {
            System.out.println(e);
            return e+"";
        }
        
        //insert data to basic cashpayment
        try{
            String truncate="insert into pspcl.basic_cash_payment (account_no, amount,receiptid,receiptdate,partition_date) select account_no, amount,receiptid,receiptdate,current_date as partition_date from pspcl.cashpayment_temp";
        
            Statement st = con.createStatement();
            st.execute(truncate);
            con.commit();
            System.out.println("inserted data into basic_cash_payment table");
            } catch(Exception e){
                System.out.println(e);
                return e+"";
            }
        
        //drop temp table
        try{
            String truncate="drop temporary table pspcl.cashpayment_temp";
        
        Statement st = con.createStatement();
        st.execute(truncate);
        con.commit();
        System.out.println("temporary table dropped");
        } catch(Exception e){
            System.out.println(e);
            return e+"";
        }
        
        return "true";
        
    }
    
    void validatewithstoredpayment() throws Exception
    {
                
        String query="create temporary table pspcl.cash_temp as select t1.id as id from pspcl.basic_cash_payment t1 inner join pspcl.stored_payment t2 on t1.account_no=t2.account_no and t1.receiptid=t2.receiptid and t1.receiptdate=t2.receiptdate";
        Statement st = con.createStatement();
        boolean p =st.execute(query);
        con.commit();
        query="delete from pspcl.basic_cash_payment  where id in (select id from pspcl.cash_temp)";
        st = con.createStatement();
        p =st.execute(query);
        con.commit();
        query="drop table pspcl.cash_temp";
        st = con.createStatement();
        p =st.execute(query);
        con.commit();
        
        query="create temporary table pspcl.epayment_temp as select t1.id as id from pspcl.basic_e_payment t1 inner join pspcl.stored_payment t2 on t1.account_no=t2.account_no and t1.receiptid=t2.receiptid and t1.receiptdate=t2.receiptdate";
        st = con.createStatement();
        p =st.execute(query);
        con.commit();
        query="delete from pspcl.basic_e_payment  where id in (select id from pspcl.epayment_temp)";
        st = con.createStatement();
        p =st.execute(query);
        con.commit();
        query="drop table pspcl.epayment_temp";
        st = con.createStatement();
        p =st.execute(query);
        con.commit();
        
            
        
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
        {query="insert into pspcl.agg_stored_payment SELECT p.account_no,p.amount,p.receiptid,p.receiptdate,'e' as mode from pspcl.basic_e_payment p,pspcl.agg_main_table m where m.account_no=p.account_no and p.receiptdate>= '"+start_date+"' and m.billing_group="+bg+" and m.cycle = "+cycle;
        Statement st3 = con.createStatement();
        st3.execute(query);
        con.commit();
        System.out.println("agg_stored_payment table insertion -- epayment");        
        }
        
        //insert cashpayment data to agg_stored_payment
        if(n==0||n==2)
        {query="insert into pspcl.agg_stored_payment SELECT p.account_no,p.amount,p.receiptid,p.receiptdate,'c' as mode from pspcl.basic_cash_payment p,pspcl.agg_main_table m where m.account_no=p.account_no and p.receiptdate>='"+start_date+"' and m.billing_group="+bg+" and m.cycle = "+cycle;
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
        void payment_update(int bg,int cycle,String cash_update,String e_update) throws Exception
    {
        String query="select * from  pspcl.payment_update where billing_group="+bg+" and cycle="+cycle;
        Statement st = con.createStatement();
        ResultSet rs =st.executeQuery(query);    
        con.commit();
        int p;
        if(rs.next())
        {
              if(cash_update.isEmpty()==false && e_update.isEmpty()==false)
              {
                query="update pspcl.payment_update set cash_date='"+cash_update+"',e_date='"+e_update+"' where billing_group="+bg+" and cycle="+cycle;
                st = con.createStatement();
                p=st.executeUpdate(query);    
                con.commit();
                  
              }
              else if (cash_update.isEmpty()==true && e_update.isEmpty()==false)
              {
                query="update pspcl.payment_update set e_date='"+e_update+"' where billing_group="+bg+" and cycle="+cycle;
                st = con.createStatement();
                p =st.executeUpdate(query);    
                con.commit();
                      
              }
              else if(cash_update.isEmpty()==false && e_update.isEmpty()==true)
              {
                query="update pspcl.payment_update set cash_date='"+cash_update+"' where billing_group="+bg+" and cycle="+cycle;
                st = con.createStatement();
                p =st.executeUpdate(query);    
                con.commit();
                      
              }        
        }
        else
        {
            if(cash_update.isEmpty()==false && e_update.isEmpty()==false)
              {
                query="insert into pspcl.payment_update (billing_group,cycle,cash_date,e_date) values ("+bg+","+cycle+",'"+cash_update+"','"+e_update+"')";
                st = con.createStatement();
                boolean n =st.execute(query);    
                con.commit();
                  
              }
              else if (cash_update.isEmpty()==true && e_update.isEmpty()==false)
              {
                query="insert into pspcl.payment_update (billing_group,cycle,cash_date,e_date) values ("+bg+","+cycle+",null,'"+e_update+"')";
                st = con.createStatement();
                boolean n =st.execute(query);    
                con.commit();
                      
              }
              else if(cash_update.isEmpty()==false && e_update.isEmpty()==true)
              {
                query="insert into pspcl.payment_update (billing_group,cycle,cash_date,e_date) values ("+bg+","+cycle+",'"+cash_update+"',null)";
                st = con.createStatement();
                boolean n =st.execute(query);    
                con.commit();      
              }
            else
              {
                query="insert into pspcl.payment_update (billing_group,cycle,cash_date,e_date) values ("+bg+","+cycle+",null,null)";
                st = con.createStatement();
                boolean n =st.execute(query);    
                con.commit();      
              }
            
            
            
        }
        System.out.println("payment date updated");
    }
    void con_close() throws Exception
    {
        con.close();
    }
}
