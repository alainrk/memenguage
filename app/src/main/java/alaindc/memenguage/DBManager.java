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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBManager {
    private DBHelper dbhelper;

    public DBManager(Context ctx) {
        dbhelper = new DBHelper(ctx);
    }

    public long insertWord(String ita, String eng) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Constants.FIELD_ITA, ita);
        cv.put(Constants.FIELD_ENG, eng);
        cv.put(Constants.FIELD_TIMESTAMP, System.currentTimeMillis());
        cv.put(Constants.FIELD_USED, "0");

        try {
            return db.insert(Constants.TABLE_WORDS, null, cv);
        } catch (SQLiteException sqle) {
            return -1;
        }
    }

    public int deleteWord(long id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            return db.delete(Constants.TABLE_WORDS, Constants.FIELD_ID + "=?", new String[]{ Long.toString(id) });
        }
        catch (SQLiteException sqle) {
            return -1;
        }

    }

    public int resetAllWordsNotUsed() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.FIELD_USED, 0);

            String selection = Constants.FIELD_ID + " > ?";
            String[] selectionArgs = { String.valueOf(-1) };

            return db.update(Constants.TABLE_WORDS, values, selection, selectionArgs);
        }
        catch (SQLiteException sqle) {
            return -1;
        }
    }

    public int setWordUsed(long id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.FIELD_USED, 1);

            String selection = Constants.FIELD_ID + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            return db.update(Constants.TABLE_WORDS, values, selection, selectionArgs);
        }
        catch (SQLiteException sqle) {
            return -1;
        }
    }

    public int setWordNotUsed(long id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.FIELD_USED, 0);

            String selection = Constants.FIELD_ID + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            return db.update(Constants.TABLE_WORDS, values, selection, selectionArgs);
        }
        catch (SQLiteException sqle) {
            return -1;
        }
    }

    public int updateWord(long id, String ita, String eng) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        try {
            ContentValues cv = new ContentValues();
            cv.put(Constants.FIELD_ITA, ita);
            cv.put(Constants.FIELD_ENG, eng);
            cv.put(Constants.FIELD_TIMESTAMP, System.currentTimeMillis());
            cv.put(Constants.FIELD_USED, 0);

            String selection = Constants.FIELD_ID + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            return db.update(Constants.TABLE_WORDS, cv, selection, selectionArgs);
        }
        catch (SQLiteException sqle) {
            return -1;
        }
    }

    public Cursor getRandomWordNotUsed() {
        Cursor crs;
        try {
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String whereClause = Constants.FIELD_USED +  " = ?";
            String[] whereArgs = {"0"};

            crs = db.query(Constants.TABLE_WORDS, null, whereClause, whereArgs, null, null, "random()", "1");
            if (crs.getCount() == 0) {
                resetAllWordsNotUsed();
                crs = db.query(Constants.TABLE_WORDS, null, whereClause, whereArgs, null, null, "random()", "1");
            }
        } catch(SQLiteException sqle) {
            return null;
        }
        return crs;
    }

    public Cursor getAllWords() {
        Cursor crs = null;
        try {
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            crs = db.query(Constants.TABLE_WORDS, null, null, null, null, null, Constants.FIELD_TIMESTAMP+" DESC");
        } catch(SQLiteException sqle) {
            return null;
        }
        return crs;
    }

    public Cursor getWordByIdAndSetUsed(long id) {
        Cursor crs;
        try {
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String whereClause = Constants.FIELD_ID +  " = ?";
            String[] whereArgs = {String.valueOf(id)};

            crs = db.query(Constants.TABLE_WORDS, null, whereClause, whereArgs, null, null, null, null);
            setWordUsed(id);

        } catch(SQLiteException sqle) {
            return null;
        }
        return crs;
    }

    public Cursor getWordById(long id) {
        Cursor crs;
        try {
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String whereClause = Constants.FIELD_ID +  " = ?";
            String[] whereArgs = {String.valueOf(id)};

            crs = db.query(Constants.TABLE_WORDS, null, whereClause, whereArgs, null, null, null, null);

        } catch(SQLiteException sqle) {
            return null;
        }
        return crs;
    }

    public Cursor getMatchingWords(String search) {
        if (search.equals(""))
            return getAllWords();

        Cursor crs;
        try {
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String whereClause = Constants.FIELD_ITA +  " like ? or " + Constants.FIELD_ENG + " like ?";
            String[] whereArgs = {"%"+search+"%", "%"+search+"%"};

            crs = db.query(Constants.TABLE_WORDS, null, whereClause, whereArgs, null, null, Constants.FIELD_TIMESTAMP+" DESC", null);
            Log.d("aaaaaa","aaa");
        } catch(SQLiteException sqle) {
            return null;
        }
        return crs;
    }
}
