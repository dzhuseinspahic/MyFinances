package ba.unsa.etf.rma.rma20huseinspahicdzenana13.detail;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.AccountInteractor;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.IAccountInteractor;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.ITransactionListInteractor;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.Transaction;
import ba.unsa.etf.rma.rma20huseinspahicdzenana13.list.TransactionListInteractor;

public class TransactionDetailPresenter implements ITransactionDetailPresenter, TransactinoDetailInteractor.OnActionDone {
    private Context context;
    private ITransactionDetailView view;
    private Transaction transaction;
    private ITransactionListInteractor interactor;
    private ArrayList<Transaction> transactions;
    private ITransactionDetailInteractor detailInteractor;
    private Account account;
    private ArrayList<Transaction> databaseTransactions;
    private IAccountInteractor accountInteractor;

    public TransactionDetailPresenter(ITransactionDetailView view, Context context) {
        this.context = context;
        this.view = view;
        this.interactor = new TransactionListInteractor();
        this.accountInteractor = new AccountInteractor();
        databaseTransactions = interactor.getTransactionFromDatabase(context);
        detailInteractor = new TransactinoDetailInteractor();
        try {
            this.transactions = new TransactionListInteractor().execute("getAll").get();
        } catch (ExecutionException e) {
            this.transactions = interactor.getTransactions();
        } catch (InterruptedException e) {
            this.transactions = interactor.getTransactions();
        }
        try {
            this.account = new AccountInteractor().execute().get();
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }
        if (transactions == null) transactions = databaseTransactions;
        if (account == null)
            account = accountInteractor.getAccountDatabase(context.getApplicationContext());
    }

    @Override
    public void addTransaction(Transaction transaction) {
        HashMap<String, Transaction> map = new HashMap<>();
        map.put("add", transaction);
        new TransactinoDetailInteractor(this).execute(map);
        updateAccount("add", transaction);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        HashMap<String, Transaction> map = new HashMap<>();
        map.put("delete", transaction);
        new TransactinoDetailInteractor(this).execute(map);
        updateAccount("delete", transaction);
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        HashMap<String, Transaction> map = new HashMap<>();
        map.put("update", transaction);
        new TransactinoDetailInteractor(this).execute(map);
        if (this.transaction.getAmount() != transaction.getAmount()
                || (this.transaction.getType().contains("income") && transaction.getType().contains("payment"))
                || ((this.transaction.getType().contains("payment") || this.transaction.getType().equals("Purchase"))
                && transaction.getType().contains("income"))
                || ((transaction.getType().contains("payment") || transaction.getType().equals("Purchase"))
                && this.transaction.getType().contains("income")))
            updateAccount("update", transaction);
    }

    @Override
    public void addTransactionToDatabase(Transaction transaction, String akcija) {
        if (akcija.equals("undo")) {
            if (transaction.getId() == 0) detailInteractor.saveToDatabase(transaction, context.getApplicationContext(), "add", "update");
            else detailInteractor.saveToDatabase(transaction, context.getApplicationContext(), "update", "update");
            updateAccount("addData", transaction);
            return;
        }
        if (!akcija.equals("add")) {
            for (Transaction trans : databaseTransactions) {
                if (trans.getDatabaseId() == this.transaction.getDatabaseId() || trans.getId() == this.transaction.getId()) {
                    detailInteractor.saveToDatabase(transaction, context.getApplicationContext(), akcija, "update");
                    if (this.transaction.getAmount() != transaction.getAmount()
                            || (this.transaction.getType().contains("income") && transaction.getType().contains("payment"))
                            || ((this.transaction.getType().contains("payment") || this.transaction.getType().equals("Purchase"))
                            && transaction.getType().contains("income"))
                            || ((transaction.getType().contains("payment") || transaction.getType().equals("Purchase"))
                            && this.transaction.getType().contains("income")))
                        updateAccount("updateData", transaction);
                    if (akcija.equals("delete")) updateAccount("deleteData", transaction);
                    return;
                }
            }
        }
        detailInteractor.saveToDatabase(transaction, context.getApplicationContext(), akcija, "insert");
        if (akcija.equals("delete")) updateAccount("deleteData", transaction);
        else if (akcija.equals("add")) updateAccount("addData", transaction);
        else if (this.transaction.getAmount() != transaction.getAmount()
                || (this.transaction.getType().contains("income") && transaction.getType().contains("payment"))
                || ((this.transaction.getType().contains("payment") || this.transaction.getType().equals("Purchase"))
                && transaction.getType().contains("income"))
                || ((transaction.getType().contains("payment") || transaction.getType().equals("Purchase"))
                && this.transaction.getType().contains("income")))
            updateAccount("updateData", transaction);
    }

    private void updateAccount(String action, Transaction transaction) {
        HashMap<String, Account> accMap = new HashMap<>();
        Account account = this.account;
        if (account == null) account = accountInteractor.getAccountDatabase(context.getApplicationContext());
        if (action.contains("delete")) account.setBudget(account.getBudget() - transaction.getAmount());
        else if (action.contains("update")) account.setBudget(account.getBudget() - this.transaction.getAmount() + transaction.getAmount());
        else if (action.contains("add")) account.setBudget(account.getBudget() + transaction.getAmount());
        accMap.put("update", account);
        try {
            new AccountInteractor().execute(accMap).get();
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }
        if (accountInteractor.getAccountDatabase(context.getApplicationContext()) == null)
            accountInteractor.saveToDatabase(account, "insert", context.getApplicationContext());
        else accountInteractor.saveToDatabase(account, null, context.getApplicationContext());
        this.account = account;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean checkLimits(Transaction transaction) {
        if (transaction.getAmount() > 0) return false;
        if (this.transaction != null && this.transaction.getAmount() == transaction.getAmount()) return false;

        ArrayList<Transaction> transactions = this.transactions;
        double monthLimit = 0;
        for(Transaction trans : transactions) {
            if (trans.getDate().isEqual(transaction.getDate()) && transaction != trans) monthLimit += trans.getAmount();
            if (trans.getEndDate() != null && trans != transaction) {
                int end = interval(trans, transaction);
                for (int i=0; i<end; i++) monthLimit += trans.getAmount();
            }
        }

        if (account == null) account = accountInteractor.getAccountDatabase(context.getApplicationContext());
        double budget = account.getBudget();
        if (getTransaction() != null) budget -= getTransaction().getAmount();
        if (transaction.getEndDate() != null) {
            int end = interval(transaction, transaction);
            for (int i=0; i<end; i++) monthLimit += transaction.getAmount();
        }
        else monthLimit += transaction.getAmount();
        budget += transaction.getAmount();

        if (monthLimit < account.getMonthLimit() || budget < account.getTotalLimit()) return true;
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int interval(Transaction trans, Transaction transaction) {
        int result = 0;
        LocalDate transactionDate = trans.getDate();
        while(transactionDate.getYear() <= transaction.getDate().getYear()
                && (transactionDate.isBefore(trans.getEndDate()) || transactionDate.equals(trans.getEndDate()))) {
            if (transactionDate.getMonthValue() == transaction.getDate().getMonthValue()
                    && transactionDate.getYear() == transaction.getDate().getYear()) result += 1;
            transactionDate = transactionDate.plusDays(trans.getTransactionInterval());
        }
        return result;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public Account getAccount() {
        if (account == null) account = accountInteractor.getAccountDatabase(context.getApplicationContext());
        return account;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void create(Transaction transaction) {
         this.transaction = transaction;
    }

    @Override
    public void setTransactionFromAnotherFragment(Parcelable transaction) {
        this.transaction = (Transaction) transaction;
    }

    @Override
    public ArrayList<Transaction> getList() {
        return transactions;
    }

    @Override
    public void onDone(Transaction transaction) {

    }
}
