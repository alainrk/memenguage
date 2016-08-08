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
import android.database.DatabaseUtils;
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

    // Edit or insert based of existence yet or not
    public long fillContext(long id, String text) {
        try {

            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor crs;

            String whereClause = Constants.FIELD_ID +  " = ?";
            String[] whereArgs = {String.valueOf(id)};
            crs = db.query(Constants.TABLE_CONTEXT, null, whereClause, whereArgs, null, null, null, null);

            db = dbhelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(Constants.FIELD_ID, id);
            cv.put(Constants.FIELD_CONTEXT, text);

            long res = -1;

            if (crs.getCount() == 0) {
                res = db.insert(Constants.TABLE_CONTEXT, null, cv);
            } else if (crs.getCount() == 1) {
                res = db.update(Constants.TABLE_CONTEXT, cv, whereClause, whereArgs);
            }

            return res;

        } catch (SQLiteException sqle) {
            return -1;
        }
    }

    public Cursor getContextById (long id) {
        Cursor crs;
        try {
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String whereClause = Constants.FIELD_ID +  " = ?";
            String[] whereArgs = {String.valueOf(id)};

            crs = db.query(Constants.TABLE_CONTEXT, null, whereClause, whereArgs, null, null, null, null);

        } catch(SQLiteException sqle) {
            return null;
        }
        return crs;
    }

    public int deleteWord(long id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            if (db.delete(Constants.TABLE_WORDS, Constants.FIELD_ID + "=?", new String[]{ Long.toString(id) }) > 0)
                return deleteContext(id);
            return 0;
        }
        catch (SQLiteException sqle) {
            return -1;
        }

    }

    public int deleteContext(long id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            return db.delete(Constants.TABLE_CONTEXT, Constants.FIELD_ID + "=?", new String[]{ Long.toString(id) });
        }
        catch (SQLiteException sqle) {
            String e = sqle.toString();
            Log.d("FANCULO",e);
            return 999;
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

    public int resetAllWordsNotUsedInRangeTime(String timestart, String timeend) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.FIELD_USED, 0);

            String selection = Constants.FIELD_ID + " > ? AND " +
                    Constants.FIELD_TIMESTAMP + " >= ? AND " +
                    Constants.FIELD_TIMESTAMP + " <= ?";
            String[] selectionArgs = { String.valueOf(-1), timestart, timeend };

            return db.update(Constants.TABLE_WORDS, values, selection, selectionArgs);
        }
        catch (SQLiteException sqle) {
            return -1;
        }
    }

    public int setWordUsed(long id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            String q = "UPDATE " + Constants.TABLE_WORDS + " SET " + Constants.FIELD_USED + " = " + Constants.FIELD_USED + " + 1 WHERE " + Constants.FIELD_ID + " = " + id;
            db.execSQL(q);
            return 1;
        }
        catch (SQLiteException sqle) {
            return -1;
        }
    }

    public int setRating(long id, int rating) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.FIELD_RATING, rating);

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

            String whereClause = Constants.FIELD_USED +  " < " + Constants.FIELD_RATING;

            crs = db.query(Constants.TABLE_WORDS, null, whereClause, null, null, null, "random()", "1");
            if (crs.getCount() == 0) {
                resetAllWordsNotUsed();
                crs = db.query(Constants.TABLE_WORDS, null, whereClause, null, null, null, "random()", "1");
            }
        } catch(SQLiteException sqle) {
            return null;
        }
        return crs;
    }

    public Cursor getRandomWordNotUsedInRangeTime(String timestart, String timeend) {
        Cursor crs;
        try {
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String whereClause = Constants.FIELD_USED + " < " + Constants.FIELD_RATING + " AND " +
                    Constants.FIELD_TIMESTAMP + " >= ? AND " +
                    Constants.FIELD_TIMESTAMP + " <= ?";
            String[] whereArgs = {timestart, timeend};

            crs = db.query(Constants.TABLE_WORDS, null, whereClause, whereArgs, null, null, "random()", "1");
            if (crs.getCount() == 0) {
                resetAllWordsNotUsedInRangeTime(timestart, timeend);
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

        } catch(SQLiteException sqle) {
            return null;
        }
        return crs;
    }

    public long countWords () {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            return DatabaseUtils.queryNumEntries(db, Constants.TABLE_WORDS, null, null);
        }
        catch (SQLiteException sqle) {
            return 0;
        }
    }

    public long countGuessedWords () {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        try {
            return DatabaseUtils.queryNumEntries(db, Constants.TABLE_WORDS, Constants.FIELD_USED +  " < " + Constants.FIELD_RATING, null);
        }
        catch (SQLiteException sqle) {
            return 0;
        }
    }
}
