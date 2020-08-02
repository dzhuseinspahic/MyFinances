package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.ArrayList;

public class Transaction implements Parcelable {
    private LocalDate date;
    private double amount;
    private String title;
    private String type;
    private String itemDescription;
    private int transactionInterval;
    private LocalDate endDate;
    private ArrayList<String> types;
    private int typeId;
    private int id;
    private int databaseId;
    private String akcija;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected Transaction(Parcel parcel) {
        databaseId = parcel.readInt();
        akcija = parcel.readString();
        id = parcel.readInt();
        date = LocalDate.parse(parcel.readString());
        amount = parcel.readDouble();
        title = parcel.readString();
        type = parcel.readString();
        itemDescription = parcel.readString();
        transactionInterval = parcel.readInt();
        endDate = LocalDate.parse(parcel.readString());
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Transaction(LocalDate date, double amount, String title, String type, String itemDescription, int transactionInterval, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(date)) throw new IllegalArgumentException("End date must be after date");
        this.date = date;
        int newType;
        try{
            newType = Integer.parseInt(type);
            setTypeId(newType);
            type = getType();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (((type.toUpperCase().contains("PAYMENT") || type.toUpperCase().contains("PURCHASE"))
             && amount > 0 ) || (type.toUpperCase().contains("INCOME") && amount < 0)) amount = -amount;
        this.amount = amount;
        this.setTitle(title);
        this.setType(type);
        if (type.contains("Regular") && endDate == null) throw new IllegalArgumentException("No end date");
        if (type.toUpperCase().contains("INCOME")) this.itemDescription = null;
        else this.itemDescription = itemDescription;
        if (type.toUpperCase().contains("REGULAR")) {
            if (transactionInterval == 0) transactionInterval = 30;
            this.transactionInterval = transactionInterval;
        }
        else this.transactionInterval = 0;
        if (type.toUpperCase().contains("REGULAR")) this.endDate = endDate;
        else this.endDate = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Transaction(LocalDate date, double amount, String title, int typeId, String itemDescription, int transactionInterval, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(date)) throw new IllegalArgumentException("End date must be after date");
        this.date = date;
        if (((typeId == 1 || typeId == 5 || typeId == 3) && amount > 0)
                || ((typeId == 2  || typeId == 4) && amount < 0)) amount = -amount;
        this.amount = amount;
        this.setTitle(title);
        this.setTypeId(typeId);
        if (type.contains("Regular") && endDate == null) throw new IllegalArgumentException("No end date");
        if (typeId == 2  || typeId == 4) this.itemDescription = null;
        else this.itemDescription = itemDescription;
        if (typeId == 1 || typeId == 2) {
            if (transactionInterval == 0) transactionInterval = 30;
            this.transactionInterval = transactionInterval;
        }
        else this.transactionInterval = 0;
        if (typeId == 1 || typeId == 2) this.endDate = endDate;
        else this.endDate = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Transaction(int id, LocalDate date, double amount, String title, String type, String itemDescription, int transactionInterval, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(date)) throw new IllegalArgumentException("End date must be after date");
        this.id = id;
        this.date = date;
        int newType;
        try{
            newType = Integer.parseInt(type);
            setTypeId(newType);
            type = getType();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (((type.toUpperCase().contains("PAYMENT") || type.toUpperCase().contains("PURCHASE"))
                && amount > 0 ) || (type.toUpperCase().contains("INCOME") && amount < 0)) amount = -amount;
        this.amount = amount;
        this.setTitle(title);
        this.setType(type);
        if (type.contains("Regular") && endDate == null) throw new IllegalArgumentException("No end date");
        if (type.toUpperCase().contains("INCOME")) this.itemDescription = null;
        else this.itemDescription = itemDescription;
        if (type.toUpperCase().contains("REGULAR")) {
            if (transactionInterval == 0) transactionInterval = 30;
            this.transactionInterval = transactionInterval;
        }
        else this.transactionInterval = 0;
        if (type.toUpperCase().contains("REGULAR")) this.endDate = endDate;
        else this.endDate = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Transaction(int id, LocalDate date, double amount, String title, int typeId, String itemDescription, int transactionInterval, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(date)) throw new IllegalArgumentException("End date must be after date");
        this.id = id;
        this.date = date;
        if (((typeId == 1 || typeId == 5 || typeId == 3) && amount > 0)
                || ((typeId == 2  || typeId == 4) && amount < 0)) amount = -amount;
        this.amount = amount;
        this.setTitle(title);
        this.setTypeId(typeId);
        if (type.contains("Regular") && endDate == null) throw new IllegalArgumentException("No end date");
        if (typeId == 2  || typeId == 4) this.itemDescription = null;
        else this.itemDescription = itemDescription;
        if (typeId == 1 || typeId == 2) {
            if (transactionInterval == 0) transactionInterval = 30;
            this.transactionInterval = transactionInterval;
        }
        else this.transactionInterval = 0;
        if (typeId == 1 || typeId == 2) this.endDate = endDate;
        else this.endDate = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Transaction(int databaseId, String akcija, int id, LocalDate date, double amount, String title, String type, String itemDescription, int transactionInterval, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(date)) throw new IllegalArgumentException("End date must be after date");
        this.databaseId = databaseId;
        this.akcija = akcija;
        this.id = id;
        this.date = date;
        int newType;
        try{
            newType = Integer.parseInt(type);
            setTypeId(newType);
            type = getType();
        } catch (NumberFormatException e) {
        }
        if (((type.toUpperCase().contains("PAYMENT") || type.toUpperCase().contains("PURCHASE"))
                && amount > 0 ) || (type.toUpperCase().contains("INCOME") && amount < 0)) amount = -amount;
        this.amount = amount;
        this.setTitle(title);
        this.setType(type);
        if (type.toUpperCase().contains("INCOME")) this.itemDescription = null;
        else this.itemDescription = itemDescription;
        if (type.toUpperCase().contains("REGULAR")) {
            if (transactionInterval == 0) transactionInterval = 30;
            this.transactionInterval = transactionInterval;
        }
        else this.transactionInterval = 0;
        if (type.toUpperCase().contains("REGULAR")) this.endDate = endDate;
        else this.endDate = null;
    }

    public String getAkcija() {
        return akcija;
    }

    public void setAkcija(String akcija) {
        this.akcija = akcija;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getType() {
        return type;
    }

    public void typeInteractor()  {
        TypeInteractor typeInteractor = new TypeInteractor();
        types = typeInteractor.getTypes();
    }

    public void setTypeId(int typeId) {
        typeInteractor();
        for (int i=1; i<=types.size(); i++) {
            if (typeId == i)  {
                this.type = types.get(i-1);
                return;
            }
        }
        throw new IllegalArgumentException("Wrong type of transaction");
    }

    public void setType(String type) {
        typeInteractor();
        for (int i=0; i<types.size(); i++) {
            if (type.toUpperCase().replace(" ", "").equals(types.get(i).toUpperCase().replace(" ", ""))) {
                this.type = types.get(i);
                this.typeId = i+1;
                return;
            }
        }
        throw new IllegalArgumentException("Wrong type of transaction");
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title.length() < 3 || title.length() > 15) throw new IllegalArgumentException("Wrong size of title");
        this.title = title;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(int transactionInterval) {
        this.transactionInterval = transactionInterval;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Transaction)) return false;
        Transaction transaction = (Transaction) obj;

        if (transaction.getEndDate() == null && transaction.getItemDescription() == null)
            return (transaction.getDate().isEqual(getDate()) && transaction.getAmount() == getAmount()
                && transaction.getTitle().equals(getTitle()) && transaction.getType().equals(getType())
                && transaction.getTransactionInterval() == getTransactionInterval());

        if (transaction.getEndDate() == null) return (transaction.getDate().isEqual(getDate()) && transaction.getAmount() == getAmount()
                && transaction.getTitle().equals(getTitle()) && transaction.getType().equals(getType())
                && transaction.getItemDescription().equals(getItemDescription())
                && transaction.getTransactionInterval() == getTransactionInterval());

        if (transaction.getItemDescription() == null) return (transaction.getDate().isEqual(getDate()) && transaction.getAmount() == getAmount()
                && transaction.getTitle().equals(getTitle()) && transaction.getType().equals(getType())
                && transaction.getTransactionInterval() == getTransactionInterval()
                && transaction.getEndDate().isEqual(getEndDate()));

        return (transaction.getDate().isEqual(getDate()) && transaction.getAmount() == getAmount()
                 && transaction.getTitle().equals(getTitle()) && transaction.getType().equals(getType())
                 && transaction.getItemDescription().equals(getItemDescription())
                && transaction.getTransactionInterval() == getTransactionInterval()
                && transaction.getEndDate().isEqual(getEndDate()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(String.valueOf(date));
        dest.writeDouble(amount);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(itemDescription);
        dest.writeInt(transactionInterval);
        dest.writeString(akcija);
        dest.writeInt(databaseId);
        dest.writeString(String.valueOf(endDate));
    }
}
