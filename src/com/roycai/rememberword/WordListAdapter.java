package com.roycai.rememberword;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class WordListAdapter extends SimpleCursorAdapter {

	private boolean isShowExplain = false;
	
	public WordListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = super.getView(position, convertView, parent);
		View explainView = view.findViewById(R.id.words_list_explain);
		int visible = isShowExplain ? View.VISIBLE : View.GONE;
		if(explainView.getVisibility() != visible){
			explainView.setVisibility(visible);
		}
		return view;
	}

	public boolean isShowExplain() {
		return isShowExplain;
	}
	public void setShowExplain(boolean isShowExplain) {
		this.isShowExplain = isShowExplain;
	}
}
