/*  This class will be the main class from which all our methods will be called. The 
    end purpose of this program will be the following: 

    1.) Every 28 days, it will create an updated list of stock tickers that exist on Yahoo.
    This list will be limited to those that have 1-4 letters only (no numerics, and no 
    tickers that have more than 5 letters for now). In the end, I personally just want to study 
    a large number of penny stocks (it doesn't have to be all), and this will suffice to 
    get those. Everything else I'm doing is really just to be able to provide some things
    for other people who may want to use this. 
    2.) A couple of times a day, it will get pricing information for the penny stocks 
    I'm looking at. In this way, I'll have more aggregate information for end users 
    of this product while at the same time getting detailed info. on the penny stocks 
    so I can study those. 

    This class will just be the main method from which everything else is called. 

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
        manager.createDailyDetailedTable(); 
        manager.createMasterTable();
        manager.createPennyTable(); 
        manager.createDailyStocksTable(); 
        TickerLocater tl = new TickerLocater(manager.month, manager.year); 
    }
}