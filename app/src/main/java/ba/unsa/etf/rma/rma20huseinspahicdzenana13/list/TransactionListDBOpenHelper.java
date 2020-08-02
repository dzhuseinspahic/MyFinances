package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TransactionListDBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Database.db";
    public static final int DATABASE_VERSION = 9;


    public TransactionListDBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TransactionListDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String TRANSACTION_TABLE = "transactions";
    public static final String DATABASE_TRANS_ID = "_id";
    public static final String AKCIJA = "akcija";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String DATE = "date";
    public static final String AMOUNT = "amount";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String ITEM_DESCRIPTION = "itemDescription";
    public static final String TRANSACTION_INTERVAL = "transactionInterval";
    public static final String END_DATE = "endDate";

    private static final String TRANSACTION_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE + "("
                    + DATABASE_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + AKCIJA + " TEXT NOT NULL, "
                    + TRANSACTION_ID + " INTEGER UNIQUE, "
                    + DATE + " DATE NOT NULL, "
                    + AMOUNT + " TEXT NOT NULL, "
                    + TITLE + " TEXT NOT NULL, "
                    + TYPE + " TEXT NOT NULL, "
                    + ITEM_DESCRIPTION + " TEXT, "
                    + TRANSACTION_INTERVAL + " INTEGER, "
                    + END_DATE + " DATE);";

    private static final String TRANSACTION_DROP = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE;

    public static final String ACCOUNT_TABLE = "account";
    public static final String DATABASE_ACC_ID = "_id";
    public static final String ACCOUNT_ID = "accountId";
    public static final String BUDGET = "budget";
    public static final String MONTH_LIMIT = "monthLimit";
    public static final String TOTAL_LIMIT = "totalLimit";

    private static final String ACCOUNT_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE + "("
                    + DATABASE_ACC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ACCOUNT_ID + " INTEGER, "
                    + BUDGET + " DOUBLE, "
                    + MONTH_LIMIT + " DOUBLE, "
                    + TOTAL_LIMIT + " DOUBLE);";

    private static final String ACCOUNT_DROP = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRANSACTION_TABLE_CREATE);
        db.execSQL(ACCOUNT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TRANSACTION_DROP);
        db.execSQL(ACCOUNT_DROP);
        onCreate(db);
    }
}
