/* This class is going to set up the phase 1 as described in the main class. It will 
   accomplish this in a couple of different steps. First, it will create an ArrayList 
   of all possible stock tickers that we want to grab information for (all combos of 1-4 
   letters). Then, it will break that ArrayList apart into a designated number of pieces 
   and feed those pieces to a TickerGrabber, which is just a separate thread that will 
   make calls to the Yahoo Finance API to grab ticker information. The part that is still 
   under "Beta" testing is how many pieces to split the ArrayList into. This class will 
   also create the mySQL data table where each of the TickerGrabbers will store the
   ticker data that they grab. 

   At the time of writing, this code was being prepped for a code portfolio, and as a result 
   simply looks for all stock tickers that are 1 letter and uses only 1 TickerGrabber. However, 
   the other code is left in for easy adjustment if anybody would like to play around with it. 

    -Sallamander 04/15/2015. 
*/

import java.util.*; 
import java.sql.*; 
import java.text.*; 

public class TickerLocater {; 
    ArrayList tickerList; 
    String month, year; 
    public TickerLocater() {
        // We'll create the list of tickers that we will feed into the TickerGrabbers. 
        tickerList = createTickerList(); 
        
        // Now we'll create the table where the TickerGrabbers will store all the information. 
        Calendar cal = Calendar.getInstance(); 
        month  = new SimpleDateFormat("MMM").format(cal.getTime()); 
        year = new SimpleDateFormat("YYYY").format(cal.getTime()); 
        
        createTable();
        
        // Now let's actually grab the information we want. We'll do this my creating 
        // multiple tickerGrabbers and letting them do the work. Generating this many tickers 
        // will create errors with ports/socket connections and such, but what we'll handle that 
        // within the actual tickerGrabbers themselves. Basically, we'll just have the tickerGrabbers 
        // keep accessing the URL until they succeed. 
        int num = 10; // This will be the number of tickerGrabbers we want to make. 
        int breakPoint = Math.round(tickerList.size() / num); 
        breakPoint = Math.max(1, breakPoint); 
        ArrayList tickerGrabbers = new ArrayList(); 
        int bPoint = 0; 
        while(bPoint * breakPoint <= tickerList.size()) {
            if(bPoint * breakPoint + breakPoint >= tickerList.size()) {
                tickerGrabbers.add(new TickerGrabber(tickerList.subList(bPoint * breakPoint, tickerList.size()) , month, year)); 
            } else if(bPoint * breakPoint >= tickerList.size()) {
            } else {
                tickerGrabbers.add(new TickerGrabber(tickerList.subList(bPoint * breakPoint, (bPoint + 1) * breakPoint) , month, year));
            }
            bPoint++; 
        }
    }
    
    private ArrayList createTickerList() {
        // This will hold all of the possible letters in the stock tickers, and 
        // then we'll simply cycle through them and see what we find. 
        ArrayList letters = new ArrayList();
        for(char letter = 'a'; letter <= 'z'; letter++) {
            String letter2 = "" + letter; 
            letters.add(letter2.toUpperCase());
        }
        
        // tickerList will hold the list of final tickers, and the 
        // individual ticker variables will hold the letters we will use to 
        // create our tickers. 
        ArrayList tickerList = new ArrayList(); 
        String ticker, ticker2, ticker3, ticker4;
        
        // Here we'll just cycle through the letters list, once for each individual 
        // letter position in the possible ticker (1-4). So our tickerList should end 
        // up with 26^4 possible tickers.
        for(Iterator letter1 = letters.iterator(); letter1.hasNext(); ) {
            ticker = (String)letter1.next(); 
            tickerList.add(ticker); 
            // Right below this is the part where we would add on 2, 3, or 4 letters and 
            // grab all stock tickers that are 1-4 letters. 
            for(Iterator letter2 = letters.iterator(); letter2.hasNext(); ) {
                ticker2 = (String)letter2.next(); 
                tickerList.add(ticker + ticker2);
                for(Iterator letter3 = letters.iterator(); letter3.hasNext(); ) {
                    ticker3 = (String)letter3.next(); 
                    tickerList.add(ticker + ticker2 + ticker3); 
                    /*
                    for(Iterator letter4 = letters.iterator(); letter4.hasNext(); ) {
                        ticker4 = (String)letter4.next(); 
                        tickerList.add(ticker + ticker2 + ticker3 + ticker4); 
                    }
                    */
                }
            }
        }        
        return tickerList; 
    }
    
    private void createTable() {   
         String datasource = "jdbc:mysql://localhost:3306/stocks"; 
         
         try( Connection conn = DriverManager.getConnection(datasource, "root", "healthy15")) {
             // Check if the table exists already. 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("show tables like '" + month + year + "'"); 
             if(rs.next()) {
                // If it exists, delete it so we can recreate it. This mainly for testing purposes, but 
                // I'll leave it in here so we don't get any errors in the future, or if I change my mind
                // and want to run the tickers code more often than once a month (but at that point I'll 
                // probably update the table name to indicate that, rather than delete the table). 
                PreparedStatement tableDelete = conn.prepareStatement("drop table " + month + year); 
                tableDelete.executeUpdate(); 
             }
             
             // Now let's create the table. 
             PreparedStatement tableCreate = conn.prepareStatement("create table " + month + year + "(ticker varchar(10), name varchar(100),"
                        + "exchange varchar(10), price float, constraint pk_all_tickers primary key (ticker))");
             tableCreate.executeUpdate(); 
             conn.close(); 
         } catch (SQLException sqe) {
                System.out.println("SQL Error: " + sqe.getMessage());
              
         }
    }
}