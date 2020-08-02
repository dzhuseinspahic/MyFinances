package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;


public class TransactionListInteractor extends AsyncTask<String, Integer, ArrayList<Transaction>> implements ITransactionListInteractor {
    static ArrayList<Transaction> transactions;

    public TransactionListInteractor() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected ArrayList<Transaction> doInBackground(String... strings) {
        try {
            this.transactions = new ArrayList<>();
            int j = 0;
            for (;;) {
                String url1;
                if (strings[0].contains("getAll")) url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" +
                        "9dd38c33-17d8-4a9b-b3a8-a3fe14bbff6b/transactions?page=" + j;
                else url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" +
                        "9dd38c33-17d8-4a9b-b3a8-a3fe14bbff6b/transactions/filter?page=" + j + strings[0];
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = convertStreamToString(in);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("transactions");
                if (results.length() == 0) break;
                for (int i = 0; i < results.length(); i++) {
                    JSONObject transaction = results.getJSONObject(i);
                    int id = transaction.getInt("id");
                    String s = transaction.getString("date").substring(0, 10);
                    s = s.replace("-", "");
                    LocalDate date = LocalDate.of(Integer.parseInt(s.substring(0, 4)),
                            Integer.parseInt(s.substring(4, 6)), Integer.parseInt(s.substring(6, 8)));
                    double amount = transaction.getDouble("amount");
                    String title = transaction.getString("title");
                    int type = transaction.getInt("TransactionTypeId");
                    String itemDesc = transaction.getString("itemDescription");
                    if (itemDesc.equals("null")) itemDesc = null;
                    String interval = transaction.getString("transactionInterval");
                    String s2 = transaction.getString("endDate");
                    s2 = s2.replace("-", "");
                    LocalDate endDate = null;
                    if (!s2.equals("null"))
                        endDate = LocalDate.of(Integer.parseInt(s2.substring(0, 4)),
                                Integer.parseInt(s2.substring(4, 6)), Integer.parseInt(s2.substring(6, 8)));
                    try {
                        if (interval.equals("null")) {
                            Transaction trans = new Transaction(id, date, amount, title, type, itemDesc, 0, endDate);
                            transactions.add(trans);
                        }
                        else {
                            Transaction trans = new Transaction(id, date, amount, title, type, itemDesc, Integer.parseInt(interval), endDate);
                            transactions.add(trans);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                j++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is));
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
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public ArrayList<Transaction> getTransactionFromDatabase(Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        String[] kolone = null;
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        Cursor cursor = resolver.query(adresa, kolone, null, null ,null);
        ArrayList<Transaction> transactions = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int dataId = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.DATABASE_TRANS_ID);
                    int transId = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TRANSACTION_ID);
                    int akcija = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.AKCIJA);
                    int date = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.DATE);
                    int title = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TITLE);
                    int amount = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.AMOUNT);
                    int type = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TYPE);
                    int transInterval = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.TRANSACTION_INTERVAL);
                    int itemDesc = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.ITEM_DESCRIPTION);
                    int endDate = cursor.getColumnIndexOrThrow(TransactionListDBOpenHelper.END_DATE);
                    try{
                        LocalDate dat = LocalDate.parse(cursor.getString(date));
                        LocalDate end = null;
                        if (cursor.getString(endDate) != null) end = LocalDate.parse(cursor.getString(endDate));
                        Transaction transaction = new Transaction(cursor.getInt(dataId), cursor.getString(akcija), cursor.getInt(transId),
                                dat, Double.parseDouble(cursor.getString(amount)), cursor.getString(title), cursor.getString(type),
                                cursor.getString(itemDesc), cursor.getInt(transInterval), end);
                        transactions.add(transaction);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        return transactions;
    }

    @Override
    public void deleteFromDatabase(ArrayList<Transaction> datebaseTransactions, Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Uri transactionsUri = Uri.parse("content://rma.provider.transactions/elements");
        for(Transaction transaction: datebaseTransactions) {
            String where = TransactionListDBOpenHelper.DATABASE_TRANS_ID + " = " + transaction.getDatabaseId();
            resolver.delete(transactionsUri, where, null);
        }
    }

    @Override
    public Cursor getCursor(String where, String sort, Context context) {
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionListDBOpenHelper.DATABASE_TRANS_ID,
                TransactionListDBOpenHelper.AKCIJA,
                TransactionListDBOpenHelper.TRANSACTION_ID,
                TransactionListDBOpenHelper.DATE,
                TransactionListDBOpenHelper.AMOUNT,
                TransactionListDBOpenHelper.TITLE,
                TransactionListDBOpenHelper.TYPE,
                TransactionListDBOpenHelper.TRANSACTION_INTERVAL,
                TransactionListDBOpenHelper.ITEM_DESCRIPTION,
                TransactionListDBOpenHelper.END_DATE
        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        Cursor cur = resolver.query(adresa, kolone, where, null, sort);
        return cur;
    }
}
