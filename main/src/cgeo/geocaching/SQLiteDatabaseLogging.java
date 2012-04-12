package cgeo.geocaching;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteTransactionListener;
import android.util.Log;
import android.util.Pair;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SQLiteDatabaseLogging {


    SQLiteDatabase sqlDatabase = null;

    public SQLiteDatabase getSql() {
        return sqlDatabase;
    }

    public void setSql(SQLiteDatabase sql) {
        this.sqlDatabase = sql;
    }

    public void acquireReference() {
        sqlDatabase.acquireReference();
    }

    public void beginTransaction() {
        sqlDatabase.beginTransaction();
    }

    public void beginTransactionNonExclusive() {
        sqlDatabase.beginTransactionNonExclusive();
    }

    public void beginTransactionWithListener(SQLiteTransactionListener transactionListener) {
        sqlDatabase.beginTransactionWithListener(transactionListener);
    }

    public void beginTransactionWithListenerNonExclusive(SQLiteTransactionListener transactionListener) {
        sqlDatabase.beginTransactionWithListenerNonExclusive(transactionListener);
    }

    public void close() {
        sqlDatabase.close();
    }

    public SQLiteStatement compileStatement(String sql) throws SQLException {
        return sqlDatabase.compileStatement(sql);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return sqlDatabase.delete(table, whereClause, whereArgs);
    }

    public boolean enableWriteAheadLogging() {
        return sqlDatabase.enableWriteAheadLogging();
    }

    public void endTransaction() {
        sqlDatabase.endTransaction();
    }

    public boolean equals(Object o) {
        return sqlDatabase.equals(o);
    }

    public void execSQL(String sql, Object[] bindArgs) throws SQLException {
        long start = System.currentTimeMillis();
        sqlDatabase.execSQL(sql, bindArgs);
        long end = System.currentTimeMillis();
        Object[] array = new Object[] { sql, bindArgs };
        System.out.println("Runtime execSQL" + (end - start) + "ms");
        logMethodArgs(array);
    }

    public void execSQL(String sql) throws SQLException {
        long start = System.currentTimeMillis();
        sqlDatabase.execSQL(sql);
        long end = System.currentTimeMillis();
        Object[] array = new Object[] { sql };
        System.out.println("Runtime execSQL" + (end - start) + "ms");
        logMethodArgs(array);
    }

    public List<Pair<String, String>> getAttachedDbs() {
        return sqlDatabase.getAttachedDbs();
    }

    public long getMaximumSize() {
        return sqlDatabase.getMaximumSize();
    }

    public long getPageSize() {
        return sqlDatabase.getPageSize();
    }

    public final String getPath() {
        return sqlDatabase.getPath();
    }

    public Map<String, String> getSyncedTables() {
        return sqlDatabase.getSyncedTables();
    }

    public int getVersion() {
        return sqlDatabase.getVersion();
    }

    public int hashCode() {
        return sqlDatabase.hashCode();
    }

    public boolean inTransaction() {
        return sqlDatabase.inTransaction();
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        return sqlDatabase.insert(table, nullColumnHack, values);
    }

    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) throws SQLException {
        return sqlDatabase.insertOrThrow(table, nullColumnHack, values);
    }

    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
        return sqlDatabase.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
    }

    public boolean isDatabaseIntegrityOk() {
        return sqlDatabase.isDatabaseIntegrityOk();
    }

    public boolean isDbLockedByCurrentThread() {
        return sqlDatabase.isDbLockedByCurrentThread();
    }

    public boolean isDbLockedByOtherThreads() {
        return sqlDatabase.isDbLockedByOtherThreads();
    }

    public boolean isOpen() {
        return sqlDatabase.isOpen();
    }

    public boolean isReadOnly() {
        return sqlDatabase.isReadOnly();
    }

    public void markTableSyncable(String table, String foreignKey, String updateTable) {
        sqlDatabase.markTableSyncable(table, foreignKey, updateTable);
    }

    public void markTableSyncable(String table, String deletedTable) {
        sqlDatabase.markTableSyncable(table, deletedTable);
    }

    public boolean needUpgrade(int newVersion) {
        return sqlDatabase.needUpgrade(newVersion);
    }

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        long start = System.currentTimeMillis();
        Cursor var = sqlDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        long end = System.currentTimeMillis();
        Object[] array = new Object[] { distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit };
        Log.v("perfTime", "Runtime query " + (end - start) + "ms");
        logMethodArgs(array);
        return var;
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        long start = System.currentTimeMillis();
        Cursor var = sqlDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        long end = System.currentTimeMillis();
        Object[] array = new Object[] { table, columns, selection, selectionArgs, groupBy, having, orderBy, limit };
        Log.v("perfTime", "Runtime query " + (end - start) + "ms");
        logMethodArgs(array);
        return var;

    }

    private void logMethodArgs(Object[] array) {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] instanceof Object[])
            {
                Object[] a = (Object[]) array[i];
                for (int j = 0; j < a.length; j++) {
                    Object object = a[j];
                    Log.v("perfArgs", object != null ? object.toString() : "null");
                }
            }
            else {
                Log.v("perfArgs", array[i] != null ? array[i].toString() : "null");
            }
        }
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        long start = System.currentTimeMillis();
        Cursor var = sqlDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        long end = System.currentTimeMillis();
        Object[] array = new Object[] { table, columns, selection, selectionArgs, groupBy, having, orderBy };
        Log.v("perfTime", "Runtime query" + (end - start) + "ms");
        logMethodArgs(array);
        return var;

    }

    public Cursor queryWithFactory(CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        long start = System.currentTimeMillis();
        Cursor var = sqlDatabase.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        long end = System.currentTimeMillis();
        Object[] array = new Object[] { cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit };
        Log.v("perfTime", "Runtime queryWithFactory" + (end - start) + "ms");
        logMethodArgs(array);
        return var;

    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        long start = System.currentTimeMillis();
        Cursor var = sqlDatabase.rawQuery(sql, selectionArgs);
        long end = System.currentTimeMillis();
        Object[] array = new Object[] { sql, selectionArgs };
        Log.v("perfTime", "Runtime rawQuery" + (end - start) + "ms");
        logMethodArgs(array);
        return var;

    }

    public Cursor rawQueryWithFactory(CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable) {
        long start = System.currentTimeMillis();
        Cursor var = sqlDatabase.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable);
        long end = System.currentTimeMillis();
        Object[] array = new Object[] { cursorFactory, sql, selectionArgs, editTable };
        Log.v("perfTime", "Runtime rawQueryWithFactory" + (end - start) + "ms");
        logMethodArgs(array);
        return var;

    }

    public void releaseReference() {
        sqlDatabase.releaseReference();
    }

    public void releaseReferenceFromContainer() {
        sqlDatabase.releaseReferenceFromContainer();
    }

    public long replace(String table, String nullColumnHack, ContentValues initialValues) {
        return sqlDatabase.replace(table, nullColumnHack, initialValues);
    }

    public long replaceOrThrow(String table, String nullColumnHack, ContentValues initialValues) throws SQLException {
        return sqlDatabase.replaceOrThrow(table, nullColumnHack, initialValues);
    }

    public void setLocale(Locale locale) {
        sqlDatabase.setLocale(locale);
    }

    public void setLockingEnabled(boolean lockingEnabled) {
        sqlDatabase.setLockingEnabled(lockingEnabled);
    }

    public void setMaxSqlCacheSize(int cacheSize) {
        sqlDatabase.setMaxSqlCacheSize(cacheSize);
    }

    public long setMaximumSize(long numBytes) {
        return sqlDatabase.setMaximumSize(numBytes);
    }

    public void setPageSize(long numBytes) {
        sqlDatabase.setPageSize(numBytes);
    }

    public void setTransactionSuccessful() {
        sqlDatabase.setTransactionSuccessful();
    }

    public void setVersion(int version) {
        sqlDatabase.setVersion(version);
    }

    public String toString() {
        return sqlDatabase.toString();
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return sqlDatabase.update(table, values, whereClause, whereArgs);
    }

    public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
        return sqlDatabase.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
    }

    public boolean yieldIfContended() {
        return sqlDatabase.yieldIfContended();
    }

    public boolean yieldIfContendedSafely() {
        return sqlDatabase.yieldIfContendedSafely();
    }

    public boolean yieldIfContendedSafely(long sleepAfterYieldDelay) {
        return sqlDatabase.yieldIfContendedSafely(sleepAfterYieldDelay);
    }

    SQLiteDatabaseLogging(SQLiteDatabase s) {
        super();
        sqlDatabase = s;
    }

}
