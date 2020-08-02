package ba.unsa.etf.rma.rma20huseinspahicdzenana13.buget;

import android.content.Context;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.AccountInteractor;

public class BudgetPresenter implements IBudgetPresenter {
    private Context context;
    private IBudgetView view;
    private Account account;
    private AccountInteractor accountInteractor;
    private boolean internet;

    public BudgetPresenter(IBudgetView view, Context context) {
        this.context = context;
        this.view = view;
        accountInteractor = new AccountInteractor();
        try {
            this.account = new AccountInteractor().execute().get();
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }
        if (account == null) account = accountInteractor.getAccountDatabase(context.getApplicationContext());
    }

    @Override
    public void updateAccount(Account account) {
        HashMap<String, Account> map = new HashMap<>();
        map.put("update", account);
        new AccountInteractor().execute(map);
        String akcija = null;
        if (accountInteractor.getAccountDatabase(context.getApplicationContext()) == null)
            akcija = "insert";
        accountInteractor.saveToDatabase(account, akcija, context.getApplicationContext());
    }

    @Override
    public Account getAccount() {
        if (account == null || (accountInteractor.getAccountDatabase(context.getApplicationContext()) != null
                && internet == false)) account = accountInteractor.getAccountDatabase(context.getApplicationContext());
        return account;
    }

    @Override
    public void setInternet(boolean b) {
        this.internet = b;
    }
}
