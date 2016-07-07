package alaindc.memenguage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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

    public WordsAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
        this.activityContext = context;
    }

    @Override
    public View newView(Context ctx, Cursor arg1, ViewGroup arg2)
    {
        LayoutInflater inflater = (LayoutInflater) activityContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.wordstextview, null);
        return v;
    }
    @Override
    public void bindView(View v, Context ctx, Cursor crs)
    {
        v.setBackgroundColor((crs.getPosition() % 2 == 0) ? Color.argb(50,76,175,80) : Color.argb(50,255,248,225));

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

    }
    @Override
    public long getItemId(int position)
    {
        Cursor crs = this.getCursor();
        crs.moveToPosition(position);
        return crs.getLong(crs.getColumnIndex(Constants.FIELD_ID));
    }
}