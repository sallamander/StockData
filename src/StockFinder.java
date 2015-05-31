/*  This class will be the main class from which all our methods will be called. The 
    end purpose of this program will be the following: 

    1.) Once a month, it will create an updated list of stock tickers that exist on Yahoo.
    This list will be limited to those that have 1-4 letters only (no numerics, and no 
    tickers that have more than 5 letters). In the end, I personally just want to study 
    a large number of penny stocks (it doesn't have to be all), and this will suffice to 
    get those. Everything else I'm doing is really just to be able to provide some things
    for other people who may want to use this. 
    2.) Every day once a day, it will get the stock prices for certain exchanges (popular ones)
    and store those in the data base. Then, a couple of times a day, it will get pricing 
    information for the penny stocks I'm looking at. In this way, I'll have more aggregate 
    information for end users of this product while at the same time getting detailed info. 
    on the penny stocks so I can study those. 

    At the time of writing this, I have not moved on to part (2) above. I am currently in a 
    "beta" phase where I am trying to figure out what the quickest way to get these stocks are, which
    primarly revolves around figuring out the optimal number of threads to run this program with 
    on my computer (and then later a server). 

    This class will prep our database and program to actually go into the Yahoo
    Finance API and grab stock tickers for us. There are two primary things that
    we will need to do in this method: 

    (1.) We need to create an arrayList of all the stock tickers that we will want
    to grab. This will just be all possible combinations of A-Z, for tickers that are
    1-4 letters. I could simply just do this in the actual TickerGrabber class, but 
    since I will want to create multiple TickerGrabbers in their own threads, I will
    want to be able to tell those TickerGrabbers exactly what tickers they are resonsible
    for. I'll need an arrayList to keep track of that. The beauty of this, though, is that
    it only needs to be created once. 

    (2.) Create the table that we will put all this information in. This will be a SQL 
    table that will be held in the stocks database, and will be titled the name of the month
    and year (i.e. mar2015). It will hold the ticker/symbol, name of the company, it's closing 
    price for the previous day, and the exchange the ticker is from.
*/

public class StockFinder {
 
    public static void main(String[] arguments) {   
        // Anytime we are using the TickerLocater we will be looking to see what tickers
        // there are and getting a master list. As such, we'll need to create an instance
        // of a TableManager and make sure that the 
        TableManager manager = new TableManager(); 
        manager.createMasterTable();
        TickerLocater tl = new TickerLocater(manager.month, manager.year); 
    }
}