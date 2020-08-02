package ba.unsa.etf.rma.rma20huseinspahicdzenana13.account;

import android.content.Context;

public interface IAccountInteractor {
    Account getAccount();
    void saveToDatabase(Account account, String akcija, Context context);

    void deleteFromDatabase(Account account, Context context);
    Account getAccountDatabase(Context context);
}
