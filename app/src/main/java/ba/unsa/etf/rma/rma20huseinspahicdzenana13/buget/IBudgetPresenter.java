package ba.unsa.etf.rma.rma20huseinspahicdzenana13.buget;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;

public interface IBudgetPresenter {
    void updateAccount(Account account);
    Account getAccount();

    void setInternet(boolean b);
}
