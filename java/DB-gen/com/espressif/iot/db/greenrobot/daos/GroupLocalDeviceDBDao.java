package com.espressif.iot.db.greenrobot.daos;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.espressif.iot.db.greenrobot.daos.GroupLocalDeviceDB;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * DAO for table GROUP_LOCAL_DEVICE_DB.
 */
public class GroupLocalDeviceDBDao extends AbstractDao<GroupLocalDeviceDB, Long> {

    public static final String TABLENAME = "GROUP_LOCAL_DEVICE_DB";

    /**
     * Properties of entity GroupLocalDeviceDB.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property GroupId = new Property(1, long.class, "groupId", false, "GROUP_ID");
        public final static Property Bssid = new Property(2, String.class, "bssid", false, "BSSID");
    };

    private DaoSession daoSession;

    private Query<GroupLocalDeviceDB> groupDB_LocalDevicesQuery;

    public GroupLocalDeviceDBDao(DaoConfig config) {
        super(config);
    }

    public GroupLocalDeviceDBDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'GROUP_LOCAL_DEVICE_DB' (" + //
            "'_id' INTEGER PRIMARY KEY ," + // 0: id
            "'GROUP_ID' INTEGER NOT NULL ," + // 1: groupId
            "'BSSID' TEXT NOT NULL );"); // 2: bssid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'GROUP_LOCAL_DEVICE_DB'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, GroupLocalDeviceDB entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getGroupId());
        stmt.bindString(3, entity.getBssid());
    }

    @Override
    protected void attachEntity(GroupLocalDeviceDB entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public GroupLocalDeviceDB readEntity(Cursor cursor, int offset) {
        GroupLocalDeviceDB entity = new GroupLocalDeviceDB( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // groupId
            cursor.getString(offset + 2) // bssid
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, GroupLocalDeviceDB entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGroupId(cursor.getLong(offset + 1));
        entity.setBssid(cursor.getString(offset + 2));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(GroupLocalDeviceDB entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(GroupLocalDeviceDB entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    /** Internal query to resolve the "localDevices" to-many relationship of GroupDB. */
    public List<GroupLocalDeviceDB> _queryGroupDB_LocalDevices(long groupId) {
        synchronized (this) {
            if (groupDB_LocalDevicesQuery == null) {
                QueryBuilder<GroupLocalDeviceDB> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.GroupId.eq(null));
                groupDB_LocalDevicesQuery = queryBuilder.build();
            }
        }
        Query<GroupLocalDeviceDB> query = groupDB_LocalDevicesQuery.forCurrentThread();
        query.setParameter(0, groupId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getGroupDBDao().getAllColumns());
            builder.append(" FROM GROUP_LOCAL_DEVICE_DB T");
            builder.append(" LEFT JOIN GROUP_DB T0 ON T.'GROUP_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }

    protected GroupLocalDeviceDB loadCurrentDeep(Cursor cursor, boolean lock) {
        GroupLocalDeviceDB entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        GroupDB groupDB = loadCurrentOther(daoSession.getGroupDBDao(), cursor, offset);
        if (groupDB != null) {
            entity.setGroupDB(groupDB);
        }

        return entity;
    }

    public GroupLocalDeviceDB loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();

        String[] keyArray = new String[] {key.toString()};
        Cursor cursor = db.rawQuery(sql, keyArray);

        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }

    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<GroupLocalDeviceDB> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<GroupLocalDeviceDB> list = new ArrayList<GroupLocalDeviceDB>(count);

        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }

    protected List<GroupLocalDeviceDB> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<GroupLocalDeviceDB> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }

}
