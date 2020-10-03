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
import java.util.Calendar;
import pspcl.main_frame.*;

/**
 *
 * @author amandeep
 */
public class readandstoremainfile {
    Connection con;
    readandstoremainfile(Connection con)
    {
        this.con =con;
    }
    void readmainfile(String file_path)
    {
        int count=0;
        int batch_size=20;
        try(
                BufferedReader in = new BufferedReader(new FileReader(file_path))) {
                String truncate="truncate table pspcl.basic_main_table";
                Statement st = con.createStatement();
                st.execute(truncate);
                con.commit();
                System.out.println("basic main table truncated");
                String str;
                String sql = "INSERT INTO pspcl.basic_main_table (cycle,person_name,father_name,account_no,billing_date,category,due_amount,billing_group) VALUES (?, ?, ?, ? , ?, ?, ?, ?)";
                PreparedStatement statement = con.prepareStatement(sql);
                while ((str = in.readLine()) != null) {
//                    System.out.println(str);
                    String[] tokens = str.split("\"");
//                    for(int i=0;i<tokens.length;i++)
//                    {
//                        System.out.print(" "+i+"  "+tokens[i]);
//                    }
//                    System.out.println();

                  
                    int cycle =Integer.parseInt(trim(tokens[4])) ;
                    int billing_group = Integer.parseInt(trim(tokens[5]));
                    String person_name =trim(tokens[7]);
                    String father_name =trim(tokens[8]);
                    String account_no =trim(tokens[10]);
                    String billing_date=trim(tokens[12]);
                    String category=trim(tokens[21]);
                    int due_amount;
                    try{
                    due_amount = Integer.parseInt(trim(tokens[96]));
                    }
                    catch(Exception e)
                    {
                        due_amount=0;
                    }
//                    System.out.println("account "+ account_no+" cycle " + cycle+" BG " +billing_group+" name " +person_name+" father_name " +father_name+" amount " +due_amount+" billing_date "+ billing_date+" category "+category);
//                    System.out.print(billing_date+"\n");
                    SimpleDateFormat sdf;
                    java.util.Date parseddate;
                    
                    sdf= new SimpleDateFormat("dd-MMM-yyyy");
                    parseddate=sdf.parse(billing_date);
                                      
                    SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd");
//                    System.out.println(billing_date+ "      " +print.format(parseddate));
//                    
//                    System.out.println("values are -- "+ account_no + cycle+billing_group+person_name+father_name+due_amount+billing_date);
                    statement.setString(4, account_no);
                    statement.setString(6, category);
                    statement.setInt(1, cycle);
                    statement.setInt(8, billing_group);
                    statement.setString(2, person_name);
                    statement.setString(3, father_name);
                    statement.setInt(7, due_amount);
                    statement.setString(5,print.format(parseddate) );
                    count++;
                    statement.addBatch();
                    
                    if(count>batch_size)
                    {
                        statement.executeBatch();
                        count=0;
                    }
                } 
                statement.executeBatch(); // remaining if any
                con.commit();
                
                }
                catch (Exception e) {
                    System.out.println(e);
                }
        System.out.println("mainfile function completed");
    }
    
    
    boolean checkaccountno()
    {
        boolean p=false;
        try
        {
        String query="select account_no from pspcl.basic_main_table where left(account_no,3)!='T53'";
        Statement st = con.createStatement();
        ResultSet rs =st.executeQuery(query);
        con.commit();
        if(rs.next())
        {
            p=false;
        }
        else
        {
            p=true;
        }
       
        }
        catch(Exception e)
        {
                System.out.println(e);
        }
        System.out.println("account check function completed");
        return p;
    }
    
    
    
    String setdbdate()
    {
        //check the billing group and cycle from basic_main_table and delete that BG from agg main table and add new data to agg main table
        try
        {
            //check BG and cycle from basic main table
        String query="select billing_group,cycle from pspcl.basic_main_table limit 1";
        Statement st = con.createStatement();
        ResultSet rs =st.executeQuery(query);
        con.commit();
        String bg,cycle;
        
        if(rs.next())
        {
           System.out.println(rs.getString(1));
           System.out.println(rs.getString(2));
           bg=rs.getString(1);
           cycle=rs.getString(2);
        }
        else
        {
            return "\nincorrect data of upload file";
        }
        //check if data of stated BG and cycle is there in DB or not
        query="select count(*) from pspcl.agg_main_table where billing_group="+bg+" and cycle="+cycle;
        st = con.createStatement();
        rs =st.executeQuery(query);
        con.commit();
        rs.next();
        if(rs.getInt(1)==0)
        {
            System.out.println("data of stated BG and cycle is not there in DB");
        }
        else
        {
        //delete that BG from agg_table
        query="delete from pspcl.agg_main_table where billing_group="+bg+" and cycle="+cycle;
        st = con.createStatement();
        boolean p=st.execute(query);
        con.commit();
//        if(p==false)
//        {
//            return "\nerror: conflict in Billing Group while update data";
//        }
        System.out.println("BG deleted from agg_main_table");
        }
        //get max billing_date and insert data to agg_main_table
        query="select max(billing_date) from pspcl.basic_main_table";
        Statement st01 = con.createStatement();
        ResultSet rs01 =st01.executeQuery(query);
        con.commit();
        Calendar c = Calendar.getInstance();
        java.util.Date olddate=c.getTime();
        if(rs01.next())
        {
           System.out.println(rs01.getDate(1));
           olddate=rs01.getDate(1);
        }
        else
        {
            //p=true;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        c.setTime(olddate);
        c.add(Calendar.DAY_OF_MONTH, 15); 
        String newDate = sdf.format(c.getTime()); 
        System.out.println("newdate "+newDate);
        //new date fetched
        
        
        
        query="insert into pspcl.agg_main_table select cycle,person_name,father_name,account_no,category,due_amount as gross_amount,0,due_amount,'"+ newDate +"',billing_group,current_date from pspcl.basic_main_table ";
        Statement st2 = con.createStatement();
        st2.execute(query);
        con.commit();
        System.out.println("agg_main_table table insertion");
       
        }
        catch(Exception e)
        {
                System.out.println(e);
        }
        System.out.println("setdbdate function completed");
        return "\nData Inserted into DB";
    }
    
    
    
    void resultfile(int bg,int cycle)
    {
        
        try{
            String query="truncate table pspcl.result_table";
            Statement st1 = con.createStatement();
            boolean p=st1.execute(query);
            System.out.println(p);
            query="insert into pspcl.result_table " +
            "select * from "+
"(select cycle,billing_group,ac_no,substr(ac_no,4,4),substr(ac_no,8),person_name,father_name,category,gross_amount,added_amount+coalesce(amount_e,0)+coalesce(amount_c,0),coalesce(amount_e,0) as epayment_amount,coalesce(amount_c,0) as cashpayment_amount, "+
"(due_amount-coalesce(amount_e,0)-coalesce(amount_c,0)) as updated_amount from "+
"(select cycle,person_name,father_name,t1.account_no as ac_no,category,gross_amount,added_amount,due_amount,billing_group,t2.amount as amount_e,t3.amount as amount_c from pspcl.agg_main_table t1 "+
"left outer join (select account_no,amount from pspcl.final_payment where mode='e') t2 "+
"on t1.account_no=t2.account_no "+
" left outer join (select account_no,amount from pspcl.final_payment where mode='c') t3 "+
"on t1.account_no=t3.account_no "+
"where cycle="+cycle+" and billing_group="+bg+") t4 "+
") t5 where updated_amount>=200";
            st1 = con.createStatement();
            boolean p1 =st1.execute(query);
            System.out.println(p1);
            
           System.out.println("result table inserted") ;
            
            query="create temporary table pspcl.result_temp as " +
            "select cycle,person_name,father_name,t1.account_no as account_no ,category,gross_amount,added_amount+coalesce(t2.amount,0)+coalesce(t3.amount,0),(due_amount-coalesce(t2.amount,0)-coalesce(t3.amount,0)) as due_amount,start_date," +
            "billing_group,partition_date from pspcl.agg_main_table t1 " +
            "left outer join (select account_no,amount from pspcl.final_payment where mode='e') t2 on t1.account_no=t2.account_no" +
            "  left outer join (select account_no,amount from pspcl.final_payment where mode='c') t3 on t1.account_no=t3.account_no "
                    + " where cycle="+cycle+" and billing_group="+bg;
            st1 = con.createStatement();
            p1 =st1.execute(query);
            System.out.println("temporary result table  "+p1);
            
            
            query="delete from pspcl.agg_main_table where cycle="+cycle+" and billing_group="+bg;
            st1 = con.createStatement();
            p1 =st1.execute(query);
            System.out.println("delete from agg_main_table  "+p1);
            
            query="insert into pspcl.agg_main_table select * from pspcl.result_temp";
            st1 = con.createStatement();
            p1 =st1.execute(query);
            System.out.println("insert into agg_main_table  "+p1);
            
           
            
                
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
