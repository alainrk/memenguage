package alaindc.memenguage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by narko on 03/07/16.
 */
public class WordsAdapter extends CursorAdapter implements Filterable {

    Context context;

    public WordsAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context ctx, Cursor arg1, ViewGroup arg2)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.wordstextview, null);
        return v;
    }
    @Override
    public void bindView(View v, Context arg1, Cursor crs)
    {
//                int color = (crs.getPosition() % 2 == 0) ? Color.argb(50,178,245,242) : Color.WHITE;
        int color = (crs.getPosition() % 2 == 0) ? Color.argb(50,255,202,40) : Color.WHITE;
        v.setBackgroundColor(color);
        String ita = crs.getString(crs.getColumnIndex(Constants.FIELD_ITA));
        String eng = crs.getString(crs.getColumnIndex(Constants.FIELD_ENG));

        TextView itatxt = (TextView) v.findViewById(R.id.itatxt);
        TextView engtxt = (TextView) v.findViewById(R.id.engtxt);
        itatxt.setText(ita);
        itatxt.setTag("itatxt");
        engtxt.setText(eng);
        itatxt.setTag("engtxt");

    }
    @Override
    public long getItemId(int position)
    {
        Cursor crs = this.getCursor();
        crs.moveToPosition(position);
        return crs.getLong(crs.getColumnIndex(Constants.FIELD_ID));
    }
}