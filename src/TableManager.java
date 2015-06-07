/* This file here will handle all of the table creation and deletion that occurs 
   in the program. 
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
    
    public void createDailyStocksTable() {
        // In this method we are going to create a table that will hold all the stocks that we are going to 
        // look up each day/during the day. This is exactly the same as the table above, except it will only 
        // hold the tickers to look up (and not prices), and will change each month. The reason for doing the 
        // above and this table is that the tables above will hold the month name so I can keep historical data.
        String datasource = "jdbc:mysql://localhost:3306/stocks"; 
         
        try( Connection conn = DriverManager.getConnection(datasource, "root", "healthy15")) {
             // Check if the table exists already. 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("show tables like 'daily_tickers'"); 
             if(rs.next()) {
                // If it exists, delete it so we can recreate it. This mainly for testing purposes, but 
                // I'll leave it in here so we don't get any errors in the future, or if I change my mind
                // and want to run the tickers code more often than once a month (but at that point I'll 
                // probably update the table name to indicate that, rather than delete the table). 
                PreparedStatement tableDelete = conn.prepareStatement("drop table daily_tickers"); 
                tableDelete.executeUpdate(); 
             }
             
             // Now let's create the table. 
             PreparedStatement tableCreate = conn.prepareStatement("create table daily_tickers_list (ticker varchar(10), "
                     + " constraint primary key (ticker), constraint foreign key (ticker) references " + month + year + "(ticker))");
             tableCreate.executeUpdate(); 
             conn.close(); 
         } catch (SQLException sqe) {
                System.out.println("SQL Error: " + sqe.getMessage());           
         }
    }
    
    public void createDailyDetailedTable() {
        // This is the table that will hold all of the actual stock information that
        // we grab every day. 
        String datasource = "jdbc:mysql://localhost:3306/stocks"; 
        
        try( Connection conn = DriverManager.getConnection(datasource, "root", "healthy15")) {             
             PreparedStatement tableCreate = conn.prepareStatement("create table daily_tickers_data (ticker varchar(10), date_time datetime, "
                     + "previous_close float, opening_price float, ask float, bid float, days_low float, days_high float, moving_average_50 "
                     + "float, moving_average_200 float, volume float, dailiy_average_volume float, dividend_yield float, "
                     + "dividend_per_share float, earnings_per_share float, p_e_ratio float, p_e_g_ratio float, book_value float, "
                     + "revenue float, holdings_value float, market_cap float, shares_owned float, shares_outstanding float, "
                     + "float_shares float, short_ratio float, constraint primary key (ticker, date_time))"); 
             tableCreate.executeUpdate(); 
             conn.close(); 
         } catch (SQLException sqe) {
                System.out.println("SQL Error: " + sqe.getMessage());           
         }
    }
}
