package com.roycai.openfile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.roycai.rememberword.R;

/**
 * Date: 2010-1-4
 * Time: 14:07:51
 *
 * @author tosmart@gmail.com
 */
public class FileItemsAdapter extends IconTextListAdapter {

    public FileItemsAdapter(
            Context context, List<String> names) {
        super(context, names);
    }

    public View getView(
            int position, View view, ViewGroup viewGroup) {

        view = super.getView(position, view, viewGroup);

        ImageView image = (ImageView) view.findViewById(R.id.image_openfile);
        TextView text = (TextView) view.findViewById(R.id.text_openfile);

        String name = (String) getItem(position);

        if (name.equals("..")) {
            image.setImageResource(R.drawable.ic_uplevel);
        }
        else if (name.endsWith("/")) {
            image.setImageResource(R.drawable.ic_folder);
            name = name.substring(0, name.length() - 1);
        }
        else if (name.toLowerCase().endsWith(".pgn")) {
            image.setImageResource(R.drawable.ic_pgn);
        }
        else if (name.toLowerCase().endsWith(".fen")) {
            image.setImageResource(R.drawable.ic_fen);
        }else if (name.toLowerCase().endsWith(".xml")) {
            image.setImageResource(R.drawable.icon);
        }
        else {
            image.setImageResource(R.drawable.ic_other);
        }
        text.setText(name);

        return view;
    }
}
