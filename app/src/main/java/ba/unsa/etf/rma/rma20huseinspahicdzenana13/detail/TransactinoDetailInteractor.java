package ba.unsa.etf.rma.rma20huseinspahicdzenana13.detail;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.Transaction;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.TransactionListDBOpenHelper;

public class TransactinoDetailInteractor extends AsyncTask<HashMap<String,Transaction>, Integer, Void> implements ITransactionDetailInteractor {
    Transaction transaction;
    private OnActionDone caller;

    public interface OnActionDone{
        void onDone(Transaction transaction);
    }

    public TransactinoDetailInteractor() {
    }

    public TransactinoDetailInteractor(OnActionDone caller) {
        this.caller = caller;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Void doInBackground(HashMap<String,Transaction>... map) {
        if (map[0].get("all") != null) {
            addTransaction(map[0]);
            updateTransaction(map[0]);
            deleteTransaction(map[0]);
        }
        if (map[0].get("add") != null) addTransaction(map[0]);
        else if (map[0].get("delete") != null) deleteTransaction(map[0]);
        else if (map[0].get("update") != null) updateTransaction(map[0]);
        return null;
    }

    private void addTransaction(HashMap<String, Transaction> map) {
        String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/9dd38c33-17d8-4a9b-b3a8-a3fe14bbff6b/transactions";
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            int i=-1;
            for(Map.Entry m : map.entrySet()) {
                JSONObject jsonObject = new JSONObject();
                Transaction transaction = null;
                if (m.getKey().equals("add"+i) || m.getKey().equals("add")) transaction = (Transaction) m.getValue();
                else {
                    i++;
                    continue;
                }
                jsonObject.put("date", transaction.getDate().toString());
                jsonObject.put("title", transaction.getTitle());
                if (transaction.getAmount() > 0) jsonObject.put("amount", transaction.getAmount());
                else jsonObject.put("amount", -transaction.getAmount());
                if (transaction.getEndDate() != null)
                    jsonObject.put("endDate", transaction.getEndDate().toString());
                if (transaction.getItemDescription() != null)
                    jsonObject.put("itemDescription", transaction.getItemDescription());
                if (transaction.getTransactionInterval() != 0)
                    jsonObject.put("transactionInterval", transaction.getTransactionInterval());
                jsonObject.put("typeId", transaction.getTypeId());
                String result = String.valueOf(jsonObject);
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = result.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
                i++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteTransaction(HashMap<String, Transaction> map) {
        try {
            int i=-1;
            for (Map.Entry m: map.entrySet()) {
                if (!m.getKey().equals("delete"+i) && !m.getKey().equals("delete")) {
                    i++;
                    continue;
                }
                Transaction transaction = (Transaction) m.getValue();
                String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/9dd38c33-17d8-4a9b-b3a8-a3fe14bbff6b" +
                        "/transactions/" + transaction.getId();
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);

                System.out.println(urlConnection.getResponseMessage());
                i++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTransaction(HashMap<String, Transaction> map) {
        try {
            int i=-1;
            for (Map.Entry m: map.entrySet()) {
                if (!m.getKey().equals("update"+i) && !m.getKey().equals("update")) {
                    i++;
                    continue;
                }
                Transaction transaction = (Transaction) m.getValue();
                String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/9dd38c33-17d8-4a9b-b3a8-a3fe14bbff6b" +
                        "/transactions/" + transaction.getId();
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", transaction.getDate().toString());
                jsonObject.put("title", transaction.getTitle());
                if (transaction.getAmount() > 0) jsonObject.put("amount", transaction.getAmount());
                else jsonObject.put("amount", -transaction.getAmount());
                if (transaction.getEndDate() != null)
                    jsonObject.put("endDate", transaction.getEndDate().toString());
                if (transaction.getItemDescription() != null)
                    jsonObject.put("itemDescription", transaction.getItemDescription());
                if (transaction.getTransactionInterval() != 0)
                    jsonObject.put("transactionInterval", transaction.getTransactionInterval());
                jsonObject.put("TransactionTypeId", transaction.getTypeId());
                String result = String.valueOf(jsonObject);
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = result.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
                i++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (caller != null) caller.onDone(transaction);
    }

    @Override
    public void saveToDatabase(Transaction transaction, Context context, String akcija, String databaseAction) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Uri transactionsUri = Uri.parse("content://rma.provider.transactions/elements");
        ContentValues values = new ContentValues();
        values.put(TransactionListDBOpenHelper.AKCIJA, akcija);
        if (!akcija.equals("add")) values.put(TransactionListDBOpenHelper.TRANSACTION_ID, transaction.getId());
        values.put(TransactionListDBOpenHelper.TITLE, transaction.getTitle());
        values.put(TransactionListDBOpenHelper.AMOUNT, transaction.getAmount());
        values.put(TransactionListDBOpenHelper.TYPE, transaction.getType());
        values.put(TransactionListDBOpenHelper.DATE, transaction.getDate().toString());
        if (transaction.getEndDate() != null) values.put(TransactionListDBOpenHelper.END_DATE, transaction.getEndDate().toString());
        values.put(TransactionListDBOpenHelper.TRANSACTION_INTERVAL, transaction.getTransactionInterval());
        if (transaction.getItemDescription() != null) values.put(TransactionListDBOpenHelper.ITEM_DESCRIPTION, transaction.getItemDescription());
        if (databaseAction.equals("update")) {
            String where = TransactionListDBOpenHelper.DATABASE_TRANS_ID + " = " + transaction.getDatabaseId();
            resolver.update(transactionsUri, values, where, null);
        } else if (databaseAction.equals("insert")) resolver.insert(transactionsUri, values);
    }

    @Override
    public void deleteFromDatabase(Transaction datebaseTransaction, Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Uri transactionsUri = Uri.parse("content://rma.provider.transactions/elements");
        String where = TransactionListDBOpenHelper.DATABASE_TRANS_ID + " = " + datebaseTransaction.getDatabaseId();
        resolver.delete(transactionsUri, where, null);
    }
}
