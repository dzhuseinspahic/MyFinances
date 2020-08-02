package ba.unsa.etf.rma.rma20huseinspahicdzenana13.account;

import android.os.Parcel;
import android.os.Parcelable;


public class Account implements Parcelable {
    private int id;
    private double budget;
    private double totalLimit;
    private double monthLimit;
    private String acHash;
    private String email;
    private int datebaseId;

    public Account(int databaseId, double budget, double totalLimit, double monthLimit) {
        this.datebaseId = databaseId;
        this.budget = budget;
        if (totalLimit > 0) totalLimit = -totalLimit;
        if (monthLimit > 0) monthLimit = -monthLimit;
        this.totalLimit = totalLimit;
        this.monthLimit = monthLimit;
    }

    public Account(int id, double budget, double totalLimit, double monthLimit, String acHash, String email) {
        this.id = id;
        this.budget = budget;
        if (totalLimit > 0) totalLimit = -totalLimit;
        if (monthLimit > 0) monthLimit = -monthLimit;
        this.totalLimit = totalLimit;
        this.monthLimit = monthLimit;
        this.acHash = acHash;
        this.email = email;
    }

    public Account(int datebaseId, int id, double budget, double totalLimit, double monthLimit) {
        this.id = id;
        this.datebaseId = datebaseId;
        this.budget = budget;
        if (totalLimit > 0) totalLimit = -totalLimit;
        if (monthLimit > 0) monthLimit = -monthLimit;
        this.totalLimit = totalLimit;
        this.monthLimit = monthLimit;
    }

    protected Account(Parcel in) {
        id = in.readInt();
        datebaseId = in.readInt();
        budget = in.readDouble();
        totalLimit = in.readDouble();
        monthLimit = in.readDouble();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAcHash() {
        return acHash;
    }

    public void setAcHash(String acHash) {
        this.acHash = acHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDatebaseId() {
        return datebaseId;
    }

    public void setDatebaseId(int datebaseId) {
        this.datebaseId = datebaseId;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(double totalLimit) {
        this.totalLimit = totalLimit;
    }

    public double getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(double monthLimit) {
        this.monthLimit = monthLimit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(datebaseId);
        dest.writeDouble(budget);
        dest.writeDouble(totalLimit);
        dest.writeDouble(monthLimit);
    }
}
