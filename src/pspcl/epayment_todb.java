/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pspcl;
import static com.sun.org.apache.xerces.internal.util.XMLChar.trim;
import java.io.FileInputStream;
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
                        
    void paymenttodb(Connection con,String file_path)
    {
        int count=0;
        int batchSize = 20;
 
        
 
        try {
            String truncate="truncate table pspcl.e_payment";
            Statement st = con.createStatement();
            st.execute(truncate);
            con.commit();
            System.out.println("table truncated");
            FileInputStream inputStream = new FileInputStream(file_path);
            
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
 
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.iterator();
 
            
  
            String sql = "INSERT INTO pspcl.e_payment (account_no, amount) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);    
             
            
             
            rowIterator.next(); // skip the header row
             
            while (rowIterator.hasNext()) {
                Row nextRow = rowIterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
 
                while (cellIterator.hasNext()) {
                    Cell nextCell = cellIterator.next();
 
                    int columnIndex = nextCell.getColumnIndex();
 
                    switch (columnIndex) {
                    case 2:
                        String account_no = nextCell.getStringCellValue();
                        System.out.print("account no  "+ account_no);
                        statement.setString(1, trim(account_no));
                        break;
                    case 3:
                        int amount = (int) nextCell.getNumericCellValue();
                        System.out.println("    amount  "+ amount);
                        statement.setInt(2,amount);
                        break;
                    }
 
                }
                 
                statement.addBatch();
                 
                if (count>batchSize) {
                    statement.executeBatch();
                    count=0;
                }              
 
            }
 
            workbook.close();
             
            // execute the remaining queries
            statement.executeBatch();
  
            con.commit();
            con.close();
             
        } catch (Exception ex1) {
            System.out.println("Error reading file");
            ex1.printStackTrace();
        }
    }
    
    
    void agg_table(Connection con)
    {
        try{
            String truncate="truncate table pspcl.intermediate_e_payment";
            Statement st = con.createStatement();
            st.execute(truncate);
            con.commit();
            System.out.println("agg_e_payment table truncated");
            
            String query="insert into pspcl.intermediate_e_payment select account_no,sum(amount) from pspcl.e_payment group by account_no";
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
