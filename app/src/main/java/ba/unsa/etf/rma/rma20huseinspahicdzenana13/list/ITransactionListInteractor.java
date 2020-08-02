package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public interface ITransactionListInteractor {
    ArrayList<Transaction> getTransactions();
    ArrayList<Transaction> getTransactionFromDatabase(Context context);

    void deleteFromDatabase(ArrayList<Transaction> datebaseTransactions, Context context);

    Cursor getCursor(String where, String sort, Context applicationContext);
}
