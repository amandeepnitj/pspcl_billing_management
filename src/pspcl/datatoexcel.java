/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pspcl;


import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomFilters;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilterColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFilterOperator;
/**
 *
 * @author amandeep
 */
public class datatoexcel {
    
    int i=2;
    int total_r=0;
    int bar=0;
    float f;
    void worksheet(int bg,int cycle,int v) throws Exception
    {
        
        OutputStream file = new FileOutputStream(System.getProperty("user.home")+"\\Desktop\\pspcl\\result\\cycle_"+cycle+"_billinggroup_"+bg+".xlsx");
        XSSFWorkbook wb = new XSSFWorkbook();   
        XSSFSheet sheet = wb.createSheet("pspcl");     //creating a Sheet object to retrieve object         
        Row rowhead= sheet.createRow(0);
//        Cell cell=rowhead.createCell(0);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 13);
        sheet.addMergedRegion(cellRangeAddress);
        CellStyle style =wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font=wb.createFont();
        font.setFontHeight(18);
        font.setBold(true);
        style.setFont(font);
        Cell cell= CellUtil.createCell(rowhead, 0, "PSPCL DEFAULTER LIST", style);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        
        //get last payment date from agg_main_table
        String e_max_date ="",cash_max_date="";
        
        Connection con = new jdbcconnect().initconn();
        String query;
        Statement st1; 
        ResultSet rs1 ;
        
        query="select max(receiptdate) as maxr from pspcl.basic_e_payment";
        st1 = con.createStatement();
        rs1 =st1.executeQuery(query);
        if(rs1.next())
        {
            e_max_date=rs1.getString("maxr");
        }
        
        query="select max(receiptdate) as maxr from pspcl.basic_cash_payment";
        st1 = con.createStatement();
        rs1 =st1.executeQuery(query);
        

        if(rs1.next())
        {
            cash_max_date=rs1.getString("maxr");
        }
        
        if(v==1)
        {
            cash_max_date="";
        }
        else if(v==2)
        {
            e_max_date="";
        }
        else if(v==3)
        {
            cash_max_date="";
            e_max_date="";
        }
       
    
        CellStyle style2 =wb.createCellStyle();
        XSSFFont font1=wb.createFont();
        font1.setBold(true);
        style2.setFont(font1);
        cell=rowhead.createCell(14);
        cell.setCellStyle(style2);
        cell.setCellValue("Last E-Payment Date:");
        cell= rowhead.createCell(15);
        cell.setCellStyle(style2);
        cell.setCellValue(e_max_date);
        
        //set headings
        Row row= sheet.createRow(1);
        Cell cell_first =row.createCell(0);
        cell_first.setCellStyle(style2);
        cell_first.setCellValue("Serial No.");
        cell =row.createCell(1);
        cell.setCellStyle(style2);
        cell.setCellValue("Cycle");
        cell =row.createCell(2);
        cell.setCellStyle(style2);
        cell.setCellValue("Billing Group");
        cell =row.createCell(3);
        cell.setCellStyle(style2);
        cell.setCellValue("Account No.");
        cell =row.createCell(4);
        cell.setCellStyle(style2);
        cell.setCellValue("Ledger");
        cell =row.createCell(5);
        cell.setCellStyle(style2);
        cell.setCellValue("Acc. No.");
        cell =row.createCell(6);
        cell.setCellStyle(style2);
        cell.setCellValue("Name");
        cell =row.createCell(7);
        cell.setCellStyle(style2);
        cell.setCellValue("Father's Name");
        cell =row.createCell(8);
        cell.setCellStyle(style2);
        cell.setCellValue("Category");
        cell =row.createCell(9);
        cell.setCellStyle(style2);
        cell.setCellValue("Gross Amount");
        cell =row.createCell(10);
        cell.setCellStyle(style2);
        cell.setCellValue("Net Collected Amount");
        cell =row.createCell(11);
        cell.setCellStyle(style2);
        cell.setCellValue("E-payment Amount");
        cell =row.createCell(12);
        cell.setCellStyle(style2);
        cell.setCellValue("Cash-payment Amount");
        Cell cell_last =row.createCell(13);
        cell_last.setCellStyle(style2);
        cell_last.setCellValue("Updated Defaulting Amount");
        cell= row.createCell(14);
        cell.setCellStyle(style2);
        cell.setCellValue("Last Cash-payment date:");
        cell= row.createCell(15);
        cell.setCellStyle(style2);
        cell.setCellValue(cash_max_date);
        
        //new JPanel
        
        con =new jdbcconnect().initconn();
        query="select * from pspcl.result_table";
        Statement st = con.createStatement();
        ResultSet rs =st.executeQuery(query);
        
        //set the values
        CellStyle sty =wb.createCellStyle();
        sty.setAlignment(HorizontalAlignment.CENTER);
       
        
        while(rs.next())
        {
           
            row=sheet.createRow(i);
            cell =row.createCell(0);
            cell.setCellValue(i-1);
            cell.setCellStyle(sty);
            cell =row.createCell(1);
            cell.setCellValue(rs.getInt("cycle"));
            cell.setCellStyle(sty);
            cell =row.createCell(2);
            cell.setCellValue(rs.getInt("billing_group"));
            cell.setCellStyle(sty);
            cell =row.createCell(3);
            cell.setCellValue(rs.getString("account_no"));
            cell =row.createCell(4);
            cell.setCellValue(rs.getString("ledger"));
            cell =row.createCell(5);
            cell.setCellValue(rs.getString("acc_no"));
            cell =row.createCell(6);
            cell.setCellValue(rs.getString("name"));
            cell =row.createCell(7);
            cell.setCellValue(rs.getString("father_name"));
            cell =row.createCell(8);
            cell.setCellValue(rs.getString("category"));
            cell =row.createCell(9);
            cell.setCellValue(rs.getInt("gross_amount"));
            cell =row.createCell(10);
            cell.setCellValue(rs.getInt("added_payment"));
            cell =row.createCell(11);
            cell.setCellValue(rs.getInt("epayment_amount"));
            cell =row.createCell(12);
            cell.setCellValue(rs.getInt("cashpayment_amount"));
            cell =row.createCell(13);
            cell.setCellValue(rs.getInt("updated_amount"));
            i++;
        }
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);
        sheet.autoSizeColumn(13);
        sheet.autoSizeColumn(14);
        sheet.autoSizeColumn(15);
        sheet.setAutoFilter(new CellRangeAddress(cell_first.getRowIndex(), cell_last.getRowIndex(), cell_first.getColumnIndex(),cell_last.getColumnIndex()));
        /* Step-1: Get the CTAutoFilter Object */
		CTAutoFilter sheetFilter=sheet.getCTWorksheet().getAutoFilter();
                CTFilterColumn  myFilterColumn=sheetFilter.insertNewFilterColumn(0);
                /* Step-3: Set Filter Column ID */
                myFilterColumn.setColId(13);
                                /* Add a Custom Filter */
                CTCustomFilters myCustomFilter=myFilterColumn.addNewCustomFilters();
                /* Specify that this is an AND filter */
//                myCustomFilter.setAnd(true);
                /* Add filters */
                CTCustomFilter myFilter1= myCustomFilter.addNewCustomFilter();  
//                CTCustomFilter myFilter2= myCustomFilter.addNewCustomFilter();  
                /* Does not Begin With Filter */                
                myFilter1.setOperator(STFilterOperator.GREATER_THAN_OR_EQUAL);
                myFilter1.setVal("200");  
                /* Does not End With Filter */
//                myFilter2.setOperator(STFilterOperator.NOT_EQUAL);
//                myFilter2.setVal("*1");

                
                XSSFRow r1;
                
                int j=2;
                /* Implement Multiple Custom Filter and Hide Rows that do not match */
                for(Row r:sheet ) {
                    
                        for (Cell c : r) {
                                if (c.getColumnIndex()==13) {
                                        r1=(XSSFRow) c.getRow();
                                        if (r1.getRowNum()>=2&&c.getNumericCellValue()<200) { /* Ignore top row */                                                                           
                                            r1.getCTRow().setHidden(true); }
                                }                               
                        }
        }
        wb.write(file);
       
    }   
}
