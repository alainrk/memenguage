package alaindc.memenguage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by narko on 03/07/16.
 */
public class WordsAdapter extends CursorAdapter implements Filterable {

    Context activityContext;
    private DBManager dbmanager;

    public WordsAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
        this.activityContext = context;
        dbmanager = new DBManager(context);
    }

    @Override
    public View newView(Context ctx, Cursor arg1, ViewGroup arg2)
    {
        LayoutInflater inflater = (LayoutInflater) activityContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        View v = inflater.inflate(R.layout.wordstextview, null);
        View v = inflater.inflate(R.layout.wordstextviewslide, null);

//        SwipeLayout swipeLayout =  (SwipeLayout)findViewById(R.id.wordstextviewslide);
//
////set show mode.
//        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
//
////add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
//        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_wrapper));
//
//        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
//            @Override
//            public void onClose(SwipeLayout layout) {
//                //when the SurfaceView totally cover the BottomView.
//            }
//
//            @Override
//            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
//                //you are swiping.
//            }
//
//            @Override
//            public void onStartOpen(SwipeLayout layout) {
//
//            }
//
//            @Override
//            public void onOpen(SwipeLayout layout) {
//                //when the BottomView totally show.
//            }
//
//            @Override
//            public void onStartClose(SwipeLayout layout) {
//
//            }
//
//            @Override
//            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
//                //when user's hand released.
//            }
//        });

        return v;
    }
    @Override
    public void bindView(View v, Context ctx, Cursor crs)
    {
        final Cursor cursor = crs;
        final Context context = ctx;

        v.setBackgroundColor((crs.getPosition() % 2 == 0) ? Color.argb(50,76,175,80) : Color.WHITE);

        String ita = crs.getString(crs.getColumnIndex(Constants.FIELD_ITA));
        String eng = crs.getString(crs.getColumnIndex(Constants.FIELD_ENG));

        TextView itatxt = (TextView) v.findViewById(R.id.itatxt);
        TextView engtxt = (TextView) v.findViewById(R.id.engtxt);

        SpannableString itastyle = new SpannableString("IT: "+ita);
        SpannableString engstyle = new SpannableString("EN: "+eng);
        itastyle.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
        engstyle.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);

        itatxt.setText(itastyle);
        itatxt.setTag("itatxt");
        engtxt.setText(engstyle);
        engtxt.setTag("engtxt");

        AppCompatImageButton editbutton = (AppCompatImageButton) v.findViewById(R.id.editcompbutton);
        editbutton.setBackgroundColor(Color.WHITE);

//        editbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent createWordIntentActivity = new Intent(activityContext, CreateEditActivity.class);
//                createWordIntentActivity.setAction(Constants.ACTION_EDIT_WORD);
//
//                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ITA, cursor.getString(cursor.getColumnIndex(Constants.FIELD_ITA)));
//                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ENG, cursor.getString(cursor.getColumnIndex(Constants.FIELD_ENG)));
//                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ID, getItemId(cursor.getPosition()));
//                createWordIntentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                activityContext.startActivity(createWordIntentActivity);
//            }
//        });

    }
    @Override
    public long getItemId(int position)
    {
        Cursor crs = this.getCursor();
        crs.moveToPosition(position);
        return crs.getLong(crs.getColumnIndex(Constants.FIELD_ID));
    }
}