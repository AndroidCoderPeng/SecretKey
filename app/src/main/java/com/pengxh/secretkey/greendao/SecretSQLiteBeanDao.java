package com.pengxh.secretkey.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.pengxh.secretkey.bean.SecretSQLiteBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SECRET_SQLITE_BEAN".
*/
public class SecretSQLiteBeanDao extends AbstractDao<SecretSQLiteBean, Long> {

    public static final String TABLENAME = "SECRET_SQLITE_BEAN";

    /**
     * Properties of entity SecretSQLiteBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Category = new Property(1, String.class, "category", false, "CATEGORY");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Account = new Property(3, String.class, "account", false, "ACCOUNT");
        public final static Property Password = new Property(4, String.class, "password", false, "PASSWORD");
        public final static Property Remarks = new Property(5, String.class, "remarks", false, "REMARKS");
    }


    public SecretSQLiteBeanDao(DaoConfig config) {
        super(config);
    }
    
    public SecretSQLiteBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SECRET_SQLITE_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CATEGORY\" TEXT," + // 1: category
                "\"TITLE\" TEXT," + // 2: title
                "\"ACCOUNT\" TEXT," + // 3: account
                "\"PASSWORD\" TEXT," + // 4: password
                "\"REMARKS\" TEXT);"); // 5: remarks
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SECRET_SQLITE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SecretSQLiteBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String category = entity.getCategory();
        if (category != null) {
            stmt.bindString(2, category);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(4, account);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(5, password);
        }
 
        String remarks = entity.getRemarks();
        if (remarks != null) {
            stmt.bindString(6, remarks);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SecretSQLiteBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String category = entity.getCategory();
        if (category != null) {
            stmt.bindString(2, category);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(4, account);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(5, password);
        }
 
        String remarks = entity.getRemarks();
        if (remarks != null) {
            stmt.bindString(6, remarks);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SecretSQLiteBean readEntity(Cursor cursor, int offset) {
        SecretSQLiteBean entity = new SecretSQLiteBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // category
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // account
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // password
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // remarks
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SecretSQLiteBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCategory(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAccount(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPassword(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setRemarks(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SecretSQLiteBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SecretSQLiteBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SecretSQLiteBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
