package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TransactionContentProvider extends ContentProvider {
    private static final UriMatcher uM;
    private static final int ALLROWS = 1;
    private static final int ONEROW = 2;

    static {
        uM = new UriMatcher(UriMatcher.NO_MATCH);
        uM.addURI("rma.provider.transactions", "elements", ALLROWS);
        uM.addURI("rma.provider.transactions", "elements/#", ONEROW);
    }

    TransactionListDBOpenHelper helper;

    @Override
    public boolean onCreate() {
        helper = new TransactionListDBOpenHelper(getContext(),
                TransactionListDBOpenHelper.DATABASE_NAME, null, TransactionListDBOpenHelper.DATABASE_VERSION);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database;
        try {
            database = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            database = helper.getReadableDatabase();
        }

        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        switch (uM.match(uri)) {
            case ONEROW:
                String idRow = uri.getPathSegments().get(1);
                query.appendWhere(TransactionListDBOpenHelper.DATABASE_TRANS_ID + "=" + idRow);
            default:break;
        }
        query.setTables(TransactionListDBOpenHelper.TRANSACTION_TABLE);
        Cursor cursor = query.query(database, projection, selection, selectionArgs, groupBy, having, sortOrder);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uM.match(uri)) {
            case ALLROWS:
                return "vnd.android.cursor.dir/vnd.rma.elemental";
            case ONEROW:
                return "vnd.android.cursor.item/vnd.rma.elemental";
            default:
                throw new IllegalArgumentException("Unsuported uri: " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database;
        try {
            database = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            database = helper.getReadableDatabase();
        }
        long id = database.insert(TransactionListDBOpenHelper.TRANSACTION_TABLE, null, values);
        return uri.buildUpon().appendPath(String.valueOf(id)).build();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database;
        try {
            database = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            database = helper.getReadableDatabase();
        }
        return database.delete(TransactionListDBOpenHelper.TRANSACTION_TABLE, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database;
        try {
            database = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            database = helper.getReadableDatabase();
        }
        return database.update(TransactionListDBOpenHelper.TRANSACTION_TABLE, values, selection, selectionArgs);
    }
}
