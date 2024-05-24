package org.code17;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
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

public class LiveCurrencyGUI extends JFrame {
    // Essential URL structure is built using constants
    public static final String ACCESS_KEY = "fca_live_xt0FFh760d9RDZzKF9999K61D1eu7PSQ6Y7du9BK";
    public static final String BASE_URL = "https://api.freecurrencyapi.com/v1/latest";

    // This object is used for executing requests to the (REST) API
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public LiveCurrencyGUI() {
        setTitle("Live Currency Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel baseCurrencyLabel = new JLabel("Base Currency:");
        JTextField baseCurrencyField = new JTextField();
        JLabel targetCurrencyLabel = new JLabel("Target Currency:");
        JTextField targetCurrencyField = new JTextField();
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        JButton convertButton = new JButton("Convert");

        JLabel resultLabel = new JLabel("Conversion Result:");
        JTextArea resultArea = new JTextArea(2, 20);
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        panel.add(baseCurrencyLabel);
        panel.add(baseCurrencyField);
        panel.add(targetCurrencyLabel);
        panel.add(targetCurrencyField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(convertButton);
        panel.add(resultLabel);
        panel.add(resultScrollPane);

        convertButton.addActionListener(e -> {
            String baseCurrency = baseCurrencyField.getText().toUpperCase();
            String targetCurrency = targetCurrencyField.getText().toUpperCase();
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException ex) {
                resultArea.setText("Invalid amount entered.");
                return;
            }

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

                            resultArea.setText(String.format("%.2f %s = %.3f %s", amount, baseCurrency, conversionResult, targetCurrency));
                        } else {
                            resultArea.setText("Error: Target currency not found in the JSON response.");
                        }
                    } else {
                        resultArea.setText("Error: 'data' key not found in the JSON response.");
                    }
                } finally {
                    response.close();
                }
            } catch (IOException | ParseException | JSONException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        });

        add(panel);
    }

    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() -> {
            LiveCurrencyGUI converter = new LiveCurrencyGUI();
            converter.setVisible(true);
        });
    }
}