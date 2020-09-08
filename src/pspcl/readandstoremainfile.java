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
public class readandstoremainfile {
        
    void readmainfile(Connection con,String file_path)
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
                con.close();
                }
                catch (Exception e) {
                    System.out.println(e);
                }
    }
    
    
    boolean checkaccountno(Connection con)
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
        return p;
    }
    
    
    void setdbdate(Connection con)
    {
        
    }
    
}
