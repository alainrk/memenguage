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