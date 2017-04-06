package emblab.colostate.sensorsampling.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Zemel on 4/5/17.
 */

public class AccDataSource {
    private SQLiteDatabase database;
    private AccDataHelper dbHelper;
    private String[] allColumns = {
            AccDataHelper.COLUMN_ID,
            AccDataHelper.COLUMN_X,
            AccDataHelper.COLUMN_Y,
            AccDataHelper.COLUMN_Z
    };

    public AccDataSource(Context context, String tableName){
        dbHelper = new AccDataHelper(context,tableName);
    }

    /**
     * Open DB
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Create Table in DB
     *
     * @throws SQLException
     */
    public void createTable() throws SQLException {
        dbHelper.onCreate(database);
    }

    /**
     * Close DB
     */
    public void close() {
        dbHelper.close();
    }


}
