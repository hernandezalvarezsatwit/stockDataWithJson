import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class Main {

    public static void main(String args []) {
        String teslaStock = getStockData("TSLA");
        System.out.println(teslaStock);
    }

    private static String getStockData(String company) {
        String stockData = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+company+"&interval=5min&outputsize=full&apikey=X5N8KPH4CUJ9C5YP";
        Object obj = null;
        try {
            obj = new JSONParser().parse(getJsonFrom(stockData));
        } catch (ParseException e) {
            System.out.println("---> Problem parsing JSON of stockData");
            //Log.v("myApp", "Problem parsing JSON of stockData");
            e.printStackTrace();
        }
        JSONObject jo = (JSONObject) obj;
        Map stockHeader = (HashMap) jo.get("Meta Data");
        String symbol = stockHeader.get("2. Symbol").toString();
        String last = stockHeader.get("3. Last Refreshed").toString();

        Map stockPrices = (HashMap) jo.get("Time Series (5min)");
        String lastData = stockPrices.get(last).toString();
        Object obj2 = null;
        try {
            obj2 = new JSONParser().parse(lastData);
        } catch (ParseException e) {
            System.out.println("---> Problem parsing JSON of last prices of stock");
            //Log.v("myApp", "Problem parsing JSON of last prices of stock");
            e.printStackTrace();
        }
        JSONObject refreshedData = (JSONObject) obj2;
        String open = "Not Available";
        String temp = refreshedData.get("1. open").toString();
        if(temp != null)
            open = temp;

        String close = "Not Available";
        String temp2 = refreshedData.get("4. close").toString();
        if(temp2 != null)
            close = temp2;

        StringBuilder data = new StringBuilder("Symbol: ");
        data.append(symbol+"\n"); data.append("Open: "+open+"\n"); data.append("Close: "+close);
        return data.toString();
    }

    private static String getJsonFrom(String link) {
        //Retrieve info from URL
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            System.out.println("---> URL not retrieved correctly. URL: "+link);
            //Log.v("myApp", "URL not retrieved correctly. URL: "+link);
            e.printStackTrace();
        }

        //HTTP Connect
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            System.out.println("---> HTTP Connection Problem");
            //Log.v("myApp", "HTTP Connection Problem");
            e.printStackTrace();
        }

        //Get Input stream
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            System.out.println("---> Input Stream Problem");
            //Log.v("myApp", "Input Stream Problem");
            e.printStackTrace();
        }
        if(inputStream == null) {
            System.out.println("---> Data was not retrieved");
            //Log.v("myApp", "Data was not retrieved"");
            return "Data was not retrieved";
        }

        //Get information into string
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            //Log.v("myApp", "Buffer Reader Problem");
            System.out.println("---> Buffer Reader Problem");
            e.printStackTrace();
        }
        while(line != null)
            sb.append(line);

        return sb.toString();
    }
}
