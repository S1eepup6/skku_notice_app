package edu.skku.map.personalproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private ArrayList<AnnounceItem> items;

    public ListAdapter (Context context, ArrayList<AnnounceItem> items) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public AnnounceItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if ( view == null ) view = inflater.inflate(R.layout.item_layout, viewGroup, false);

        AnnounceItem item = items.get(i);

        TextView tv1 = (TextView)view.findViewById(R.id.announce_title);
        TextView tv2 = (TextView)view.findViewById(R.id.hidden_url);

        tv1.setText(item.getTitle());
        tv2.setText(item.getURL());

        return view;
    }
}
