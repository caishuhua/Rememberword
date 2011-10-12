package com.roycai.rememberword;

import android.provider.BaseColumns;

public class WordConstants implements BaseColumns {
    
    public static final int WORD_STATUS_ALREADY_REMEMBER = 0x01;	//已记住
    public static final int WORD_STATUS_REMEMBERING = 0x02;			//记忆中
    public static final int WORD_STATUS_NOT_REMEMBER = 0x03;		//完全不记得
    
    public static final int WORD_IMPORTANCE_IMPORTANT = 0x01;		//重要的
    public static final int WORD_IMPORTANCE_MEDIUM = 0x02;			//中等的
    public static final int WORD_IMPORTANCE_UNIMPORTANCE= 0x03;		//不重要的
}
