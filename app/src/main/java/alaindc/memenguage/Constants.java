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

public class Constants {
    public static final String DBNAME = "memenguageDB";
    public static final int DB_VERSION = 2;

    public static final String PREF_FILE = "memenguagesharedpreference";
    public static final String PREF_GOOGLEACCOUNT_ISLOGGED = "PREF_GOOGLEACCOUNT_ISLOGGED";
    public static final String PREF_GOOGLEACCOUNT_NAME = "PREF_GOOGLEACCOUNT_NAME";
    public static final String PREF_GOOGLEACCOUNT_EMAIL = "PREF_GOOGLEACCOUNT_EMAIL";
    public static final String PREF_GOOGLEACCOUNT_ID = "PREF_GOOGLEACCOUNT_ID";
    public static final String PREF_GOOGLEACCOUNT_PHOTOURI = "PREF_GOOGLEACCOUNT_PHOTOURI";

    public static final String PREF_STATS_NUMATTEMPTS = "PREF_STATS_NUMATTEMPTS";
    public static final String PREF_STATS_NUMCORRECT = "PREF_STATS_NUMCORRECT";

    public static final String TABLE_WORDS = "words";
    public static final String TABLE_CONTEXT = "context";

    public static final String FIELD_ID = "_id";
    public static final String FIELD_ITA = "ita";
    public static final String FIELD_ENG = "eng";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_USED = "used";

    public static final String FIELD_CONTEXT = "desc";

    public static final int ENGLISH_GUESS = 0;
    public static final int ITALIAN_GUESS = 1;

    public static final int PREF_GUESS_MIXED = 0;
    public static final int PREF_GUESS_ENGLISH = 1;
    public static final int PREF_GUESS_ITALIAN = 2;


    public static final String ACTION_ADD_WORD = "ACTION_ADD_WORD";
    public static final String ACTION_EDIT_WORD = "ACTION_EDIT_WORD";
    public static final String EXTRA_EDIT_ITA = "EXTRA_EDIT_ITA";
    public static final String EXTRA_EDIT_ENG = "EXTRA_EDIT_ENG";
    public static final String EXTRA_EDIT_ID = "EXTRA_EDIT_ID";

    public static final String ACTION_RANDOM_WORD = "ACTION_RANDOM_WORD";
    public static final String ACTION_RANDOM_START = "ACTION_RANDOM_START";

    public static final String ACTION_GUESS_ACTIVITY = "ACTION_GUESS_ACTIVITY";
    public static final String EXTRA_GUESS_IDWORD = "EXTRA_GUESS_IDWORD";

    public static final String SIGNIN_LOGOUT = "SIGNIN_LOGOUT";

    public static final String INTENT_VIEW_UPDATE = "INTENT_VIEW_UPDATE";

    public static final int ID_NOTIFICATION_RANDOM_WORD = 666;
}
