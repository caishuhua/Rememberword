package com.roycai.rememberword;

import static android.provider.BaseColumns._ID;

import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static com.roycai.rememberword.WordConstants.*;

public class WordDbManager {
public static final String DATABASE_NAME = "word.db";
	
	//数据表
    public static final String TABLE_WORD_BOOK = "wordbook";  
    public static final String TABLE_WORD = "word";
    
    // 单词本表字段
    public static final String WORD_BOOK_COLUM_NAME = "name";
    public static final String WORD_BOOK_COLUM_CREATEDATE = "createdate";
    public static final int INDEX_COLUM_WORD_BOOK_NAME = 1;

    //单词表字段
    public static final String WORD_COLUM_WORD = "word";
    public static final String WORD_COLUM_YINBIAO = "yinbiao";
    public static final String WORD_COLUM_EXPLAIN = "explain";
    public static final String WORD_COLUM_REMEMBER_STATUS = "status";
    public static final String WORD_COLUM_IMPORTANCE = "importance";
    public static final String WORD_COLUM_CREATEDATE = "createdate";
    public static final String WORD_COLUM_WORDBOOK_ID = "wordbookid";
    
    //SQL查询条件语句
    public static final String SELECTTION_EXCEPT_UNIMPORTANCE = WORD_COLUM_IMPORTANCE + "!=" + WORD_IMPORTANCE_UNIMPORTANCE; 
    public static final String SELECTTION_ONLY_IMPORTANCE = WORD_COLUM_IMPORTANCE + "=" + WORD_IMPORTANCE_IMPORTANT;
    
    public static final String SELECTTION_ALREADY_REMEMBER = WORD_COLUM_REMEMBER_STATUS + "=" + WORD_STATUS_ALREADY_REMEMBER; 
    public static final String SELECTTION_REMEMBERING = WORD_COLUM_REMEMBER_STATUS + "=" + WORD_STATUS_REMEMBERING;
    public static final String SELECTTION_NOT_REMEMBER = WORD_COLUM_REMEMBER_STATUS + "=" + WORD_STATUS_NOT_REMEMBER;
    
	private final Context mContext;
	private static final String TAG = "WordDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDatabase;
	
	private static final int DATABASE_VERSION = 15 ;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}


	   @Override
	   public void onCreate(SQLiteDatabase db) {
	       Log.d(TAG, "WordHelper onCreate");
	           try {
	               StringBuilder bld = new StringBuilder();
	               bld.append("CREATE TABLE " + TABLE_WORD_BOOK + " ("
	                       + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	                       + WORD_BOOK_COLUM_NAME + " TEXT,"
	                       + WORD_BOOK_COLUM_CREATEDATE + " DATETIME"
	                       + ");");
	               db.execSQL(bld.toString());
	               bld.delete(0, bld.length());
	               
	               bld.append("CREATE TABLE " + TABLE_WORD + " ("
	                       + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	                       + WORD_COLUM_WORD + " TEXT,"
	                       + WORD_COLUM_YINBIAO + " TEXT,"
	                       + WORD_COLUM_EXPLAIN + " TEXT,"
	                       + WORD_COLUM_REMEMBER_STATUS + " INTEGER,"
	                       + WORD_COLUM_IMPORTANCE + " INTEGER,"
	                       + WORD_COLUM_CREATEDATE + " DATETIME,"
	                       + WORD_COLUM_WORDBOOK_ID + " INTEGER not null"
	                       + ");");
	               db.execSQL(bld.toString());
	               bld.delete(0, bld.length());
	               
//	               bld.append("CREATE INDEX IF NOT EXISTS Index_Word ON "
//	                       + TABLE_WORD + "("
//	                       + WORD_COLUM_WORD + ");");
	               db.execSQL(bld.toString());
	               
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	   }

	   @Override
	   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	       if (oldVersion < newVersion) {
	           try {
	                   db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD_BOOK);
	                   db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
	                   onCreate(db);
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	   }

	   @Override
	   public void onOpen(SQLiteDatabase db) {
	       Log.d(TAG, "WordHelper onOpen");
	   }
	}

	
	public WordDbManager(Context ctx) {
		this.mContext = ctx;
	}
	
	public WordDbManager open() throws SQLException {
		mDbHelper = new DatabaseHelper(mContext);
		mDatabase = mDbHelper.getWritableDatabase();
		return this;
	}

	public void closeclose() {
		mDbHelper.close();
	}

	public long addWordBook(String wordBookName){
		ContentValues values = new ContentValues();
		values.put(WORD_BOOK_COLUM_NAME, wordBookName);
		
		Calendar calendar = Calendar.getInstance();
		String created = calendar.get(Calendar.YEAR) + "年"
				+ calendar.get(Calendar.MONTH) + "月"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "日";
		values.put(WORD_BOOK_COLUM_CREATEDATE, created);
		return mDatabase.insert(TABLE_WORD_BOOK, null, values);
	}
	public boolean updaeWordBookName(long wordBookId, String newWorkBookName){
		ContentValues args = new ContentValues();
		args.put(WORD_BOOK_COLUM_NAME, newWorkBookName);
		return mDatabase.update(TABLE_WORD_BOOK, args, _ID + "=" + wordBookId, null) > 0;
	}
	
	public boolean deleteWordBook(long id){
		//首先删除对应的单词
		boolean flag = mDatabase.delete(TABLE_WORD, WORD_COLUM_WORDBOOK_ID + "=" + id, null) > 0;
		return flag && mDatabase.delete(TABLE_WORD_BOOK, _ID + "=" + id, null) > 0;
	}
	
	public boolean deleteWord(long id) {
		return mDatabase.delete(TABLE_WORD, _ID + "=" + id, null) > 0;
	}
	
	public Cursor getAllWordBooks() {
		return mDatabase.query(TABLE_WORD_BOOK, new String[] {_ID,
				WORD_BOOK_COLUM_NAME, WORD_BOOK_COLUM_CREATEDATE }, null, null,
				null, null, WORD_BOOK_COLUM_CREATEDATE + " DESC");
	}
	public Cursor getWordsInOneWordBook(long wordBookId) {
		return getWordsWithSelection(wordBookId, new String[]{});
	}
	/**
	 * @param wordBookId
	 * @param selections @see SELECTTION_EXCEPTUNIMPORTANCE...
	 * @return
	 */
	public Cursor getWordsWithSelection(long wordBookId, String[] selections) {
		String sqlSelection = "";
		for (String string : selections) {
			if(string != null && !string.equals(""))
				sqlSelection += " and " + string;
		}
		return mDatabase.query(TABLE_WORD, new String[] {_ID,
				WORD_COLUM_WORD, WORD_COLUM_EXPLAIN, WORD_COLUM_YINBIAO,WORD_COLUM_IMPORTANCE, WORD_COLUM_REMEMBER_STATUS},  WORD_COLUM_WORDBOOK_ID + "=" + wordBookId + sqlSelection, null,
				null, null, WORD_COLUM_CREATEDATE + " DESC");
	}

	public void batchAddWords(List<Word> wordList, long workBookId) {
		mDatabase.beginTransaction();
		for (Word word : wordList) {
			this.addWord(word.getWord(), word.getExplain(), word.getYinBiao(),
					workBookId);
		}
		mDatabase.setTransactionSuccessful();
		mDatabase.endTransaction();
	}
	
	public long addWord(String word, String explain, String yinBiao,long workBookId){
		ContentValues values = new ContentValues();
		values.put(WORD_COLUM_WORD, word);
		values.put(WORD_COLUM_EXPLAIN, explain);
		values.put(WORD_COLUM_YINBIAO, yinBiao);
		values.put(WORD_COLUM_REMEMBER_STATUS, WordConstants.WORD_STATUS_REMEMBERING);
		values.put(WORD_COLUM_IMPORTANCE, WordConstants.WORD_IMPORTANCE_MEDIUM);
		values.put(WORD_COLUM_WORDBOOK_ID, workBookId);
		Calendar calendar = Calendar.getInstance();
		String created = calendar.get(Calendar.YEAR) + "年"
				+ calendar.get(Calendar.MONTH) + "月"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "日";
		values.put(WORD_COLUM_CREATEDATE, created);
		return mDatabase.insert(TABLE_WORD, null, values);
	}

	public boolean updateWordImportance(long wordId,int importance) {
		ContentValues args = new ContentValues();
		args.put(WORD_COLUM_IMPORTANCE, importance);
		return mDatabase.update(TABLE_WORD, args, _ID + "=" + wordId, null) > 0;
	}
	public boolean updateWordRememberStatus(long wordId,int rememberStatus) {
		ContentValues args = new ContentValues();
		args.put(WORD_COLUM_REMEMBER_STATUS, rememberStatus);
		return mDatabase.update(TABLE_WORD, args, _ID + "=" + wordId, null) > 0;
	}
}
