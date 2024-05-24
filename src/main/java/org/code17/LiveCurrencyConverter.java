package org.code17;

import java.io.IOException;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class LiveCurrencyConverter {

    public static final String ACCESS_KEY = "fca_live_xt0FFh760d9RDZzKF9999K61D1eu7PSQ6Y7du9BK";
    public static final String BASE_URL = "https://api.freecurrencyapi.com/v1/latest";

    // This object is used for executing requests to the (REST) API
    static CloseableHttpClient httpClient = HttpClients.createDefault();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        System.out.print("Enter the base currency code: ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Enter the target currency code: ");
        String targetCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Enter the amount: ");
        double amount = scanner.nextDouble();

        LiveCurrencyConverter(baseCurrency, targetCurrency, amount);

    }

    public static void LiveCurrencyConverter(String baseCurrency, String targetCurrency, double amount) throws IOException {

        HttpGet get = new HttpGet(BASE_URL + "?apikey=" + ACCESS_KEY + "&currency=" + baseCurrency);
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            try {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);
                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.has("data")) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    if (data.has(targetCurrency)) {
                        double conversionRate = data.getDouble(targetCurrency);
                        double conversionResult = amount * conversionRate;

                        System.out.println("\nConversion Result:");
                        System.out.printf(String.format("%.2f %s = %.3f %s", amount, baseCurrency, conversionResult, targetCurrency));
                    } else {
                        System.out.println("Error: Target currency not found in the JSON response.");
                    }
                } else {
                    System.out.println("Error: 'data' key not found in the JSON response.");
                }
            } finally {
                response.close();
            }
        } catch (IOException | ParseException | JSONException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            scanner.close();
            httpClient.close();
        }
    }
}
