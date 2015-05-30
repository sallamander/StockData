/*  This will be the class that goes onto Yahoo's website and actually finds the 
    names of all the stocks - so it will find their stock tickers, and store 
    some basic information that we'll want to make farther decisions off of. 

    Basically, what I want to know is (1.) The name of the company, (2.) It's ticker/symbol, 
    (3.) What stock exchange it is on, and (4.) It's most recent closing price. The
    first two of these characteristics will be used simply as descriptive information, but
    the second two will be used as filter characteristics for further study (i.e. I can 
    look at certain exchanges, or stocks within a certain price range). 

    The TickerGrabber class will be set up to run in it's own thread, such that I can 
    create multiple TickerGrabbers and have them all running asynchronously in their own 
    threads. Furtherrmore, the TickerGrabbers will be set up to send calls to the Yahoo
    Finance API in batches, so that I can grab the stats for multiple tickers at once. Note 
    that I will test this batch method and see if it is truly faster - if it is not I will 
    scrap it (but leave this note in here to let the reader know I did consider and test it). 

    Finally, since I will be creating multiple ticker grabbers and trying to maximize the use of
    ports to get this done as quickly as possible, the error handling that will result will basically  
    just take any tickers that there are errors for and stick them in an ArrayList. Then, since 
    Yahoo Finance has a limit on their calls per hour, I'll put the thread to sleep for an hour or so, 
    and then start the ticker grabber over using the list of tickers that had errors as the new input. 
    Note that the thread will only be put to sleep after it has gone through the entire intial list 
    (or error list in subsequent operations), no matter how many errors. But this sleeping will only 
    take place when I'm doing large numbers of tickers (i.e. not right now). 
*/

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class TickerGrabber implements Runnable {
    List stockTickers; 
    String month, year; 
    private Thread runner; 
    ArrayList endList, errorTickers, finalTickers; 
    
    public TickerGrabber(List stockTickers, String month, String year) {
        this.stockTickers = stockTickers; // This will hold the specfic stockTickers this particular Grabber will look up. 
        this.month = month; 
        this.year = year; 
        endList = new ArrayList(); 
        errorTickers = new ArrayList(); 
        finalTickers = new ArrayList(); 
        
        if (runner == null) {
            runner = new Thread(this); 
            runner.start(); 
        }
    }
    
    public void run() {
        errorTickers = getStockTickers(this.stockTickers); 
        while(errorTickers.size() > 0) {
            errorTickers = getStockTickers(errorTickers); 
        }
        
        if(errorTickers.size() == 0) {
            storeTickers(endList); 
        }
    }
    
    private ArrayList getStockTickers(List stockTickers) {
        String ticker = null, line;  
        ArrayList errorTickers = new ArrayList(); 
        String[] lineSplit = new String[4]; 
        
        for(Iterator tickers = stockTickers.iterator(); tickers.hasNext(); ) {
            // First we'll do one letter. 
           ticker = (String)tickers.next(); 
           // Now we'll append all of the the other letters in this ticker to that. 
           while(tickers.hasNext()) {
               ticker += '+' + (String)tickers.next(); 
           }
        }   
           
        try {
            System.out.println(ticker); 
            URL tickerLookup = new URL("http://quote.yahoo.com/d/quotes.csv?s=" + ticker + "&f=nsxp&e=.csv"); 
            URLConnection conn = tickerLookup.openConnection(); 
            try (InputStreamReader in = new InputStreamReader(conn.getInputStream()); 
                BufferedReader data = new BufferedReader(in); ) {
                while((line = data.readLine()) != null) {
                    // If the line contains N/A, then we don't want to store it. 
                    if(!line.contains("N/A")) {
                        lineSplit = line.split("\","); 
                        System.out.println(lineSplit[0] + ' ' + lineSplit[1] + ' ' + lineSplit[2] + ' ' + lineSplit[3]); 
                        endList.add(lineSplit[0].replace("\"", "")); 
                        endList.add(lineSplit[1].replace("\"", "")); 
                        endList.add(lineSplit[2].replace("\"", "")); 
                        endList.add(lineSplit[3].replace("\"", "")); 
                    }
                }
                in.close();               
                finalTickers.add(ticker);
            }
        } catch(MalformedURLException mue) {
            System.out.println("Bad URL: " + ticker + " " + mue.getMessage()); 
        } catch(IOException ioe) {
            System.out.println(ticker); 
            errorTickers.add(ticker); 
            System.out.println("IO Error: " + ioe.getMessage()); 
        } 
    return errorTickers; 
    }
    
    private void storeTickers(ArrayList endList) {
        int count = 0; 
        String data1, data2, data3, data4; 
        for(Iterator i = endList.iterator(); i.hasNext(); ) {
                data1 = i.next().toString(); 
                data2 = i.next().toString(); 
                data3 = i.next().toString(); 
                data4 = i.next().toString(); 
                String datasource = "jdbc:mysql://localhost:3306/stocks"; 
                try( Connection conn = DriverManager.getConnection(datasource, "root", "healthy15")) {
                    Class.forName("com.mysql.jdbc.Driver");
                    PreparedStatement prep = conn.prepareStatement("INSERT INTO " + month + year + "(name, "
                            + "ticker, exchange, price) VALUES(?, ?, ?, ?)"); 
                    prep.setString(1, data1); 
                    prep.setString(2, data2); 
                    prep.setString(3, data3); 
                    prep.setString(4, data4); 
                    prep.executeUpdate();
                    conn.close(); 
                } catch (SQLException sqe) {
                    System.out.println("SQL Error: " + sqe.getMessage());
                } catch (ClassNotFoundException cnfe) {
                    System.out.println(cnfe.getMessage()); 
                } 
            
        }
    }
}