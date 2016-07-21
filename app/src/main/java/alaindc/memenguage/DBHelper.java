/*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
* This file is part of Memenguage Android app.
* Copyright (C) 2016 Alain Di Chiappari
*/

package alaindc.memenguage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by narko on 03/07/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, Constants.DBNAME, null, Constants.DB_VERSION);
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

        q = "CREATE TABLE " + Constants.TABLE_CONTEXT + " (" +
                Constants.FIELD_ID + " INTEGER PRIMARY KEY," +
                Constants.FIELD_CONTEXT + " TEXT"
                + ")";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        switch (oldVersion) {
            case 1:
                Log.d("DBHelper", "Old DB version 1");
                String q = "CREATE TABLE " + Constants.TABLE_CONTEXT + " (" +
                    Constants.FIELD_ID + " INTEGER PRIMARY KEY," +
                    Constants.FIELD_CONTEXT + " TEXT"
                    + ")";
                db.execSQL(q);
                break;
            default:
                Log.d("DBHelper", "OldVersion Unknown");
                break;
        }
    }
}
