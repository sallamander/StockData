/* This file here will handle all of the table creation, deletion, etc. 
   that occurs in this program. 

   -Jones 05/30/2015
*/

import java.text.*;
import java.util.*;
import java.sql.*; 

public class TableManager {
    String month, year; 
    
    public TableManager() {        
        // Any time we are using the TableManager we are going to want to know what 
        // the month and year are. 
        Calendar cal = Calendar.getInstance(); 
        month  = new SimpleDateFormat("MMM").format(cal.getTime()); 
        year = new SimpleDateFormat("YYYY").format(cal.getTime());       
    }
    
    public void createMasterTable() {

        String datasource = "jdbc:mysql://localhost:3306/stocks"; 
         
         try( Connection conn = DriverManager.getConnection(datasource, "root", "healthy15")) {
             // Check if the table exists already. 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("show tables like '" + month + year + "'"); 
             if(!rs.next()) {
                // Now let's create the table. 
                PreparedStatement tableCreate = conn.prepareStatement("create table " + month + year + "(ticker varchar(10), name varchar(100),"
                   + "exchange varchar(10), price float, constraint primary key (ticker))");
                tableCreate.executeUpdate(); 
             } else {
                 
             }      
             conn.close(); 
         } catch (SQLException sqe) {
                System.out.println("SQL Error: " + sqe.getMessage());           
         }
    }
    
    public void createPennyTable() {
        // In this method we are going to create a table that will hold all the stocks that we are going to 
        // look up each day/during the day. At the time of writing, this will simply be all those that were 
        // under $5 at the start of the month. 
        String datasource = "jdbc:mysql://localhost:3306/stocks"; 
         
        try( Connection conn = DriverManager.getConnection(datasource, "root", "healthy15")) {
             // Check if the table exists already. 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("show tables like '" + month + year + "_penny_tickers'"); 
             if(rs.next()) {
                // If it exists, delete it so we can recreate it. This mainly for testing purposes, but 
                // I'll leave it in here so we don't get any errors in the future, or if I change my mind
                // and want to run the tickers code more often than once a month (but at that point I'll 
                // probably update the table name to indicate that, rather than delete the table). 
                PreparedStatement tableDelete = conn.prepareStatement("drop table " + month + year + "_penny_tickers"); 
                tableDelete.executeUpdate(); 
             }
             
             // Now let's create the table. 
             PreparedStatement tableCreate = conn.prepareStatement("create table " + month + year + "_penny_tickers (ticker varchar(10), price float,"
                     + " constraint primary key (ticker), constraint foreign key (ticker) references " + month + year + "(ticker))");
             tableCreate.executeUpdate(); 
             conn.close(); 
         } catch (SQLException sqe) {
                System.out.println("SQL Error: " + sqe.getMessage());           
         }
    }
    
    /*
    public void updatePennyTable() {
        String datasource = "jdbc:mysql://localhost:3306/stocks"; 
         
        try( Connection conn = DriverManager.getConnection(datasource, "root", "healthy15")) {
             // Populate the table. 
             PreparedStatement getPennyTickers = conn.prepareStatement("insert into " + month + year + "_penny_tickers (ticker) VALUES((select ticker from " + month + year + " where price < 5))"); 
             getPennyTickers.executeUpdate(); 
             conn.close(); 
         } catch (SQLException sqe) {
                System.out.println("SQL Error: " + sqe.getMessage());           
         }
    }
    */
}
