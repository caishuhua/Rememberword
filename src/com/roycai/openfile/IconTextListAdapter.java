package com.roycai.openfile;

import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.List;

import com.roycai.rememberword.R;

/**
 * Date: 2010-1-5
 * Time: 15:20:14
 *
 * @author tosmart@gmail.com
 */
public abstract class IconTextListAdapter extends BaseAdapter {

    public IconTextListAdapter(
            Context context, List<String> items) {

        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(
            int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(
                    R.layout.file_item,
                    viewGroup,
                    false
            );
        }

        return view;
    }

    protected List<String> items;
    protected LayoutInflater inflater;
}
