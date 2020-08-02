package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TypeInteractor extends AsyncTask<String, Integer, ArrayList<String>> implements ITypeInteractor {
    static ArrayList<String> types;

    public TypeInteractor() {
       // this.types = new ArrayList<>();
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
    protected ArrayList<String> doInBackground(String... strings) {
        types = new ArrayList<>();
        String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/transactionTypes";
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = convertStreamToString(in);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray results = jsonObject.getJSONArray("rows");
            for (int i = 0; i < results.length(); i++) {
                JSONObject temp = results.getJSONObject(i);
                String name = temp.getString("name");
                types.add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return types;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
    }

    @Override
    public ArrayList<String> getTypes() {
        if (types.size() == 0) {
            types.add("Regular payment");
            types.add("Regular income");
            types.add("Purchase");
            types.add("Individual income");
            types.add("Individual payment");
        }
        return types;
    }
}