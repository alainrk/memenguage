package alaindc.memenguage;

/**
 * Created by narko on 03/07/16.
 */
public class Constants {
    public static final String DBNAME = "memenguageDB";

    public static final String TABLE_WORDS = "words";

    public static final String FIELD_ID = "_id";
    public static final String FIELD_ITA = "ita";
    public static final String FIELD_ENG = "eng";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_USED = "used";

    public static final int ENGLISH_GUESS = 0;
    public static final int ITALIAN_GUESS = 1;

    public static final String ACTION_ADD_WORD = "ACTION_ADD_WORD";
    public static final String ACTION_EDIT_WORD = "ACTION_EDIT_WORD";
    public static final String EXTRA_EDIT_ITA = "EXTRA_EDIT_ITA";
    public static final String EXTRA_EDIT_ENG = "EXTRA_EDIT_ENG";
    public static final String EXTRA_EDIT_ID = "EXTRA_EDIT_ID";

    public static final String ACTION_RANDOM_WORD = "ACTION_RANDOM_WORD";
    public static final String ACTION_RANDOM_START = "ACTION_RANDOM_START";

    public static final String ACTION_GUESS_ACTIVITY = "ACTION_GUESS_ACTIVITY";
    public static final String EXTRA_GUESS_IDWORD = "EXTRA_GUESS_IDWORD";


    public static final int TIMEOUT_RANDOM_WORDS_MILLISECONDS = 10*1000;

    public static final int ID_NOTIFICATION_RANDOM_WORD = 666;
}
