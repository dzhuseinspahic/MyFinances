package ba.unsa.etf.rma.rma20huseinspahicdzenana13.buget;

import ba.unsa.etf.rma.rma20huseinspahicdzenana13.account.Account;

public interface IBudgetView {
    void setBudget(Account account);
    void notifyBudgetDataSetChanged();
}
