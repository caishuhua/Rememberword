package com.roycai.rememberword;

import static com.roycai.rememberword.WordDbManager.SELECTTION_ALREADY_REMEMBER;
import static com.roycai.rememberword.WordDbManager.SELECTTION_EXCEPT_UNIMPORTANCE;
import static com.roycai.rememberword.WordDbManager.SELECTTION_NOT_REMEMBER;
import static com.roycai.rememberword.WordDbManager.SELECTTION_ONLY_IMPORTANCE;
import static com.roycai.rememberword.WordDbManager.SELECTTION_REMEMBERING;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class WordDetailActivity extends Activity {

	private WordDbManager mWordDbManager;
	private Cursor mCursor;
	private int mPosition = 0;
	
	private TextView wordTextView = null;
	private TextView yinBiaoTextView = null;
	private TextView explainTextView = null;
	private View imp_icon1 = null, imp_icon2 = null, imp_icon3 = null;
	
	private static final int HAD_REMEMBERED = Menu.FIRST;
	private static final int NOT_REMEMBER = Menu.FIRST + 1;
	private static final int IMPORTANCE = Menu.FIRST + 2;
	private static final int UNIMPORTANCE = Menu.FIRST + 3;
	private static final int DISPLAY_WORD = Menu.FIRST + 4;
	private static final int DISPLAY_DETAIL = Menu.FIRST + 5;

	private boolean isDelayShowExplain = false;
	private View showExplainView = null, notShowExplainView = null;
	private long currentWordId = 0L;
	private int currentWordImportance = WordConstants.WORD_IMPORTANCE_MEDIUM;
	private int currentWordRememberStatus = WordConstants.WORD_STATUS_REMEMBERING;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.word_detail);
		
		mWordDbManager = new WordDbManager(this);
		mWordDbManager.open();
		Intent intent = this.getIntent();
		String[] sqlSelections = intent.getStringArrayExtra("sqlSelections");
		setActivityTitle(sqlSelections);
		
		long wordBookId = intent.getLongExtra("wordBookId", 1L);
		mPosition = intent.getIntExtra("position", 1);
		
		mCursor = mWordDbManager.getWordsWithSelection(wordBookId,sqlSelections);
		startManagingCursor(mCursor);
		
		wordTextView = (TextView)findViewById(R.id.word_detail_word);
		yinBiaoTextView = (TextView)findViewById(R.id.word_detail_yinbiao);
		explainTextView = (TextView)findViewById(R.id.word_detail_explain);
		showExplainView = findViewById(R.id.explain_view1);
		notShowExplainView = findViewById(R.id.explain_view2);
		
		imp_icon1 = findViewById(R.id.imp_1);
		imp_icon2 = findViewById(R.id.imp_2);
		imp_icon3 = findViewById(R.id.imp_3);
		
		findViewById(R.id.btn_show_detail).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showExplainView.setVisibility(View.VISIBLE);
				notShowExplainView.setVisibility(View.GONE);
			}
		});
		
		findViewById(R.id.btn_previous).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mPosition <= 0){
					mPosition = 0;
					Toast.makeText(WordDetailActivity.this, "已经是第一个了",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				mPosition--;
				renderListView();
			}
		});
		findViewById(R.id.btn_next).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mPosition >= mCursor.getCount() - 1){
					mPosition = mCursor.getCount() - 1;
					Toast.makeText(WordDetailActivity.this, "已经是最后一个了",
							Toast.LENGTH_SHORT).show();
					return;
				}
				mPosition++;
				renderListView();
			}
		});
		
		renderListView();
	}
	
	private void setActivityTitle(String[] sqlSelections) {
		String importance = "";
		String rememberStatus = "";
		for (String str : sqlSelections) {
			if(str == null)
				continue ;
			if(str.equals(SELECTTION_ALREADY_REMEMBER)){
				rememberStatus = getString(R.string.txt_had_remembered);
			}else if(str.equals(SELECTTION_REMEMBERING)){
				rememberStatus = getString(R.string.txt_remembering);
			}else if(str.equals(SELECTTION_NOT_REMEMBER)){
				rememberStatus = getString(R.string.txt_not_remember);
			}else if(str.equals(SELECTTION_ONLY_IMPORTANCE)){
				importance = "重要";
			}else if(str.equals(SELECTTION_EXCEPT_UNIMPORTANCE)){
				importance = "重要及一般";
			}
		}
		
		String title = "";
		if(importance.equals("") && rememberStatus.equals("")){
			title = "全部单词";
		}else if(rememberStatus.equals("") || importance.equals("")){
			title = rememberStatus + importance;
		}else{
			title = rememberStatus + "-" + importance;
		}
		setTitle(title);
	}

	private void renderListView(){
		mCursor.moveToPosition(mPosition);
		
		if(mCursor.getColumnCount() == 0)
			return ;
		
		currentWordId = mCursor.getLong(0);
		currentWordImportance = mCursor.getInt(4);
		currentWordRememberStatus = mCursor.getInt(5);
		
		wordTextView.setText(mCursor.getString(1));
		explainTextView.setText(mCursor.getString(2));
		yinBiaoTextView.setText(mCursor.getString(3).trim());
		
		if(isDelayShowExplain && showExplainView.getVisibility() != View.GONE){
			showExplainView.setVisibility(View.GONE);
			notShowExplainView.setVisibility(View.VISIBLE);
		}else if(!isDelayShowExplain && showExplainView.getVisibility() == View.GONE){
			showExplainView.setVisibility(View.VISIBLE);
			notShowExplainView.setVisibility(View.GONE);
		}
		
		if(currentWordImportance == WordConstants.WORD_IMPORTANCE_IMPORTANT){
			imp_icon1.setVisibility(View.VISIBLE);
			imp_icon2.setVisibility(View.VISIBLE);
			imp_icon3.setVisibility(View.VISIBLE);
		}else if(currentWordImportance == WordConstants.WORD_IMPORTANCE_MEDIUM){
			imp_icon1.setVisibility(View.VISIBLE);
			imp_icon2.setVisibility(View.VISIBLE);
			imp_icon3.setVisibility(View.INVISIBLE);
		}else{
			imp_icon1.setVisibility(View.VISIBLE);
			imp_icon2.setVisibility(View.INVISIBLE);
			imp_icon3.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, HAD_REMEMBERED, 0, R.string.txt_had_remembered);
		menu.add(0, NOT_REMEMBER, 1, R.string.txt_not_remember);
		menu.add(1, IMPORTANCE, 0, R.string.txt_importance);
		menu.add(1, UNIMPORTANCE, 1, R.string.txt_unimportance);
		menu.add(2, DISPLAY_WORD, 0, R.string.display_word);
		menu.add(2, DISPLAY_DETAIL, 1, R.string.display_detail);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String toastText = "";
		String errorTxt = "标记失败";
		 switch (item.getItemId()) {
         case HAD_REMEMBERED:
        	 if(currentWordRememberStatus == WordConstants.WORD_STATUS_ALREADY_REMEMBER){
        		 toastText = "状态本为已记住，无需标记";
        	 }else{
        		 boolean flag =  mWordDbManager.updateWordRememberStatus(currentWordId,WordConstants.WORD_STATUS_ALREADY_REMEMBER);
        		 if(flag){
 					toastText = "成功标记为'已记住'!";
 					currentWordRememberStatus = WordConstants.WORD_STATUS_ALREADY_REMEMBER;
        		 }
 				else 
 					toastText = errorTxt;
        	 }
        	 
        	 Toast.makeText(WordDetailActivity.this, toastText,
						Toast.LENGTH_SHORT).show();
 			return true;
 		case NOT_REMEMBER:
			if (currentWordRememberStatus == WordConstants.WORD_STATUS_NOT_REMEMBER) {
				toastText = "状态本为未记住，无需标记";
			} else {
				boolean flag = mWordDbManager.updateWordRememberStatus(currentWordId,
						WordConstants.WORD_STATUS_NOT_REMEMBER);
				if(flag){
					toastText = "成功标记为'未记住'!";
					currentWordRememberStatus = WordConstants.WORD_STATUS_NOT_REMEMBER;
				}else 
					toastText = errorTxt;
			}
			 Toast.makeText(WordDetailActivity.this, toastText,
						Toast.LENGTH_SHORT).show();
 			return true;
 		case IMPORTANCE:
 			if(currentWordImportance == WordConstants.WORD_IMPORTANCE_IMPORTANT){
 				toastText = "单词本为重要，无需标记";
 			}else{
 				boolean flag = mWordDbManager.updateWordImportance(currentWordId,WordConstants.WORD_IMPORTANCE_IMPORTANT);
 				if(flag){
					toastText = "成功标记为'重要'!";
					currentWordImportance = WordConstants.WORD_IMPORTANCE_IMPORTANT;
 				}else 
					toastText = errorTxt;
 			}
 			
 			Toast.makeText(WordDetailActivity.this, toastText,
					Toast.LENGTH_SHORT).show();
 			return true;
 		 case UNIMPORTANCE:
			if (currentWordImportance == WordConstants.WORD_IMPORTANCE_UNIMPORTANCE) {
				toastText = "单词本为不重要，无需标记";
			} else {
				boolean flag = mWordDbManager.updateWordImportance(currentWordId,
						WordConstants.WORD_IMPORTANCE_UNIMPORTANCE);
				if(flag){
					toastText = "成功标记为'不重要'!";
					currentWordImportance = WordConstants.WORD_IMPORTANCE_UNIMPORTANCE;
				}else 
					toastText = errorTxt;
			}
			Toast.makeText(WordDetailActivity.this, toastText,
					Toast.LENGTH_SHORT).show();
  			return true;
  		case DISPLAY_WORD:
  			isDelayShowExplain = true;
  			renderListView();
  			return true;
  		case DISPLAY_DETAIL:
  			isDelayShowExplain = false;
  			renderListView();
  			return true;
  			
		 }
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWordDbManager.closeclose();
	}
}
