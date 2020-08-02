package ba.unsa.etf.rma.rma20huseinspahicdzenana13.detail;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.Transaction;

public interface ITransactionDetailInteractor {
    void saveToDatabase(Transaction transaction, Context context, String akcija, String databaseAction);

    void deleteFromDatabase(Transaction datebaseTransactions, Context context);
}
