package ba.unsa.etf.rma.rma20huseinspahicdzenana13.account;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.TransactionListDBOpenHelper;

public class AccountInteractor extends AsyncTask<HashMap<String, Account>, Integer, Account> implements IAccountInteractor {
    Account account;

    public AccountInteractor() {
    }

    @Override
    protected Account doInBackground(HashMap<String, Account>... map) {
        try {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" +
                    "9dd38c33-17d8-4a9b-b3a8-a3fe14bbff6b";
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (map.length != 0) {
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                Account account = map[0].get("update");
                jsonObject.put("budget", account.getBudget());
                jsonObject.put("totalLimit", account.getTotalLimit());
                jsonObject.put("monthLimit", account.getMonthLimit());
                String result = String.valueOf(jsonObject);
                try(OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = result.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
            } else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = convertStreamToString(in);
                JSONObject jsonObject = new JSONObject(result);
                int id = jsonObject.getInt("id");
                double budget = jsonObject.getDouble("budget");
                double totalLimit = jsonObject.getDouble("totalLimit");
                double monthLimit = jsonObject.getDouble("monthLimit");
                String acHash = jsonObject.getString("acHash");
                String email = jsonObject.getString("email");
                account = new Account(id, budget, totalLimit, monthLimit, acHash, email);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return account;
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void saveToDatabase(Account account, String akcija, Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Uri uri = Uri.parse("content://rma.provider.account/elements");
        ContentValues values = new ContentValues();
        values.put(TransactionListDBOpenHelper.ACCOUNT_ID, account.getId());
        values.put(TransactionListDBOpenHelper.BUDGET, account.getBudget());
        values.put(TransactionListDBOpenHelper.MONTH_LIMIT, account.getMonthLimit());
        values.put(TransactionListDBOpenHelper.TOTAL_LIMIT, account.getTotalLimit());
        if(akcija != null && akcija.equals("insert")) resolver.insert(uri, values);
        else {
            String where = TransactionListDBOpenHelper.DATABASE_ACC_ID + " = " + account.getDatebaseId()
                    + " OR " + TransactionListDBOpenHelper.ACCOUNT_ID + " = " + account.getId();
            resolver.update(uri, values, where, null);
        }
    }

    @Override
    public void deleteFromDatabase(Account account, Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Uri uri = Uri.parse("content://rma.provider.account/elements");
        resolver.delete(uri, null, null);
    }

    @Override
    public Account getAccountDatabase(Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        String[] kolone = null;
        Uri adresa = Uri.parse("content://rma.provider.account/elements");
        Cursor cursor = resolver.query(adresa, kolone, null, null ,null);
        Account account = null;
        if (cursor != null && cursor.moveToLast()) {
            int budget = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.BUDGET);
            int dataid = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.DATABASE_ACC_ID);
            int id = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.ACCOUNT_ID);
            int monthLimit = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.MONTH_LIMIT);
            int totalLimit = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TOTAL_LIMIT);
            account = new Account(cursor.getInt(dataid), cursor.getInt(id), cursor.getDouble(budget), cursor.getDouble(totalLimit),
                    cursor.getDouble(monthLimit));
        }
        cursor.close();
        return account;
    }
}
