package com.roycai.rememberword;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.roycai.openfile.FileDialog;
import com.roycai.openfile.MessageBox;
import com.roycai.openfile.OpenFileDialog;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import static  com.roycai.rememberword.WordDbManager.*;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final int DELETE_ID = Menu.FIRST;
	private static final int RENAME_ID = Menu.FIRST + 1;
	
	private static final int IMPORT_WORD = Menu.FIRST + 2;
	private static final int HELP = Menu.FIRST + 3;
	
	public static final int CHOOSE_XML_FILE = 0x01;
	public static final int HANDLE_TYPE_NEW = 1;
	public static final int HANDLE_TYPE_RENAME = 2;
	private int currentHandleType = 1;
	
	private Cursor mCursor;
	private WordDbManager mWordDbManager;
	private ListView wordBookListView = null;
	private long selectedWordBookId = 0L;
	private ProgressDialog progressDlg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("单词表");
		setContentView(R.layout.main);
		mWordDbManager = new WordDbManager(this);
		mWordDbManager.open();

		wordBookListView = (ListView) findViewById(R.id.word_book_list);
		wordBookListView.setOnItemClickListener(showWordBook);
		//监听长按ListView弹出Menu  此类中实现的onContextItemSelected()  
		//和onCreateContextMenu 与之对应
		wordBookListView.setOnCreateContextMenuListener(this);  
		findViewById(R.id.add_word_book).setOnClickListener(addNewWordBook);
//		mWordDbManager.addWordBook("新概念第一册");
//		mWordDbManager.addWordBook("新概念第二册");
//		mWordDbManager.addWordBook("新概念第三册");
//		mWordDbManager.addWordBook("新概念第四册");
		renderListView();
	}
	private View.OnClickListener addNewWordBook = new View.OnClickListener() {
		public void onClick(View v) {
			wordBookHandle(HANDLE_TYPE_NEW);
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, HELP, 0,"帮助");
		return true;
	}
	
	private Dialog addWordBookDialog = null;
	
	private OnItemClickListener showWordBook = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			long wordBookId = Long.valueOf(((TextView)view.findViewById(R.id.word_book_id)).getText().toString());
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, WordsListActivity.class);
			intent.putExtra("wordBookId", wordBookId);
			startActivity(intent);
		}
	};
	
	private void wordBookHandle(int handleType) {
		currentHandleType = handleType;
		if (addWordBookDialog == null) {
			LinearLayout addWordBookDialogView = (LinearLayout) LayoutInflater
					.from(MainActivity.this).inflate(
							R.layout.add_word_book_dialog, null);
			addWordBookDialog = new Dialog(MainActivity.this);
			addWordBookDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			addWordBookDialog.setContentView(addWordBookDialogView);

			addWordBookDialog.findViewById(R.id.add_word_book_confirm_btn)
					.setOnClickListener(confimAddWordBook);

			Window window = addWordBookDialog.getWindow();
			window.setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0,
					0, 0)));
			WindowManager.LayoutParams layoutParams = window
					.getAttributes();
			layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		}
		addWordBookDialog.show();
	};
	
	private View.OnClickListener confimAddWordBook = new View.OnClickListener() {
		public void onClick(View v) {
			EditText editText = (EditText)addWordBookDialog.findViewById(R.id.add_word_book_name);
			String newWordBookName = editText.getText().toString();
			if(currentHandleType == HANDLE_TYPE_NEW){
				long id = mWordDbManager.addWordBook(newWordBookName);
                if(id > 0){
                	Toast.makeText(MainActivity.this, "添加成功!", Toast.LENGTH_SHORT).show();
                	renderListView();
                }
			}else if(currentHandleType == HANDLE_TYPE_RENAME)
			{
				boolean flag = mWordDbManager.updaeWordBookName(selectedWordBookId, newWordBookName);
                if(flag){
                	Toast.makeText(MainActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
                	renderListView();
                }
			}
			renderListView();
			editText.setText("");
			addWordBookDialog.dismiss();
		}
	};
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }
        Cursor cursor = (Cursor) wordBookListView.getAdapter().getItem(info.position);
        if (cursor == null) {
            return;
        }
        menu.setHeaderTitle(cursor.getString(INDEX_COLUM_WORD_BOOK_NAME));
        menu.add(0, IMPORT_WORD, 0, "导入单词");
		menu.add(0, DELETE_ID, 0, "删除");
		menu.add(0, RENAME_ID, 0, "重命名");
		
	};
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == DELETE_ID || item.getItemId() == RENAME_ID || item.getItemId() == IMPORT_WORD) {
	        AdapterView.AdapterContextMenuInfo info;
	        try {
	             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	        } catch (ClassCastException e) {
	            Log.e(TAG, "bad menuInfo", e);
	            return false;
	        }
	        Cursor cursor = (Cursor) wordBookListView.getAdapter().getItem(info.position);
	        if (cursor == null) {
	            return false;
	        }
	        
	        long id = cursor.getLong(0);
	        selectedWordBookId = id;
	        switch (item.getItemId()) {
	            case DELETE_ID: 
	                boolean flag = mWordDbManager.deleteWordBook(id);
	                if(flag){
	                	Toast.makeText(MainActivity.this, "删除成功!", Toast.LENGTH_SHORT).show();
	                	renderListView();
	                }
	                return true;
	            case RENAME_ID: 
	            	wordBookHandle(HANDLE_TYPE_RENAME);
	                return true;
	            case IMPORT_WORD:
	     			Intent intent = new Intent(MainActivity.this, OpenFileDialog.class);
	     	        startActivityForResult(intent, CHOOSE_XML_FILE);
	     			return true;
	        }
	        return false;
		}
		
		
		 switch (item.getItemId()) {
         case HELP:
     		MessageBox.showAbout(this);
  			return true;
     }
		return super.onMenuItemSelected(featureId, item);
	 }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CHOOSE_XML_FILE && resultCode == OpenFileDialog.RESULT_OK) {
			String path = data.getStringExtra(FileDialog.KEY_NAME);
			importWord(path);
		}
	}

	private static final int WHAT_IMPORT_SUCCESS = 1;
	private static final int WHAT_IMPORT_ERROR = 2;
	private void importWord(final String path) {
		
		progressDlg = ProgressDialog.show(this,
				getString(R.string.title_please_wait),
				getString(R.string.message_wait_for_open), true, false);

		new Thread() {
			public void run() {
				Message message = new Message();
				try {
					//从本地获取地震数据
					InputStream inStream = new FileInputStream(new File(path));
					PullWordHandler pullHandler = new PullWordHandler();
					List<Word> wordList = pullHandler.parse(inStream);
					if(wordList.size() == 0)
						throw new RuntimeException("请注意只支持有道词典xml格式！");
					
					mWordDbManager.batchAddWords(wordList, selectedWordBookId);
					message.what = WHAT_IMPORT_SUCCESS;
				} catch (Exception e) {
					message.what = WHAT_IMPORT_ERROR;
					message.obj = e.getMessage();
				}
				handler.sendMessage(message);
			}
		}.start();
	}
	
	 private Handler handler = new Handler() {
	        public void handleMessage(Message message) {
	          if (message.what == WHAT_IMPORT_SUCCESS) {
	                progressDlg.dismiss();
	                Toast.makeText(MainActivity.this, "成功导入单词!", Toast.LENGTH_SHORT).show();
	            }
	            else if (message.what == WHAT_IMPORT_ERROR) {
	                progressDlg.dismiss();
	                Toast.makeText(MainActivity.this, "导入失败，" + (String)message.obj, Toast.LENGTH_SHORT).show();
	            }
	        }
	    };

	
	private void renderListView() {
		mCursor = mWordDbManager.getAllWordBooks();
		startManagingCursor(mCursor);
		String[] from = new String[] { BaseColumns._ID,
				WordDbManager.WORD_BOOK_COLUM_NAME,
				WordDbManager.WORD_BOOK_COLUM_CREATEDATE };
		int[] to = new int[] { R.id.word_book_id, R.id.word_book_name,
				R.id.word_book_createdate };
		SimpleCursorAdapter workBooks = new SimpleCursorAdapter(this,
				R.layout.word_book_item, mCursor, from, to);
		wordBookListView.setAdapter(workBooks);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWordDbManager.closeclose();
	}
}