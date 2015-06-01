/* This class is going to set up the phase 1 as described in the main class. It will 
   accomplish this in a couple of different steps. First, it will create an ArrayList 
   of all possible stock tickers that we want to grab information for (all combos of 1-4 
   letters). Then, it will break that ArrayList apart into a designated number of pieces 
   and feed those pieces to a TickerGrabber, which is just a separate thread that will 
   make calls to the Yahoo Finance API to grab ticker information. The part that is still 
   under "Beta" testing is how many pieces to split the ArrayList into. 

    -Sallamander 04/15/2015. 
*/

import java.util.*; 

public class TickerLocater {; 
    ArrayList tickerList; 
    String month, year; 
    
    public TickerLocater(String month, String year) {
        this.month = month; 
        this.year = year; 
        
        // We'll create the list of tickers that we will feed into the TickerGrabbers. 
        tickerList = createTickerList(); 
        
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
                    for(Iterator letter4 = letters.iterator(); letter4.hasNext(); ) {
                        ticker4 = (String)letter4.next(); 
                        tickerList.add(ticker + ticker2 + ticker3 + ticker4); 
                    }
                }
            }
        }        
        return tickerList; 
    }
}