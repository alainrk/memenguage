package alaindc.memenguage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by narko on 03/07/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, Constants.DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String q = "CREATE TABLE " + Constants.TABLE_WORDS + " (" +
                Constants.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Constants.FIELD_ITA + " TEXT," +
                Constants.FIELD_ENG + " TEXT," +
                Constants.FIELD_TIMESTAMP + " NUMERIC," +
                Constants.FIELD_USED + " NUMERIC"
                + ")";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
