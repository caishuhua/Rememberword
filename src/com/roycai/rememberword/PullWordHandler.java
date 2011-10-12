package com.roycai.rememberword;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.location.Location;
import android.util.Log;

public  class  PullWordHandler {  
    //xml解析用到的Tag   
    private  String kItemElementName =  "item" ;  
    private  String kWordElementName =  "word" ;  
    private  String kTransElementName =  "trans" ;  
    private  String kPhoneticdElementName =  "phonetic" ;  
    private  String kTagsElementName =  "tags" ;  
    //用于保存xml解析获取的结果   
    private  ArrayList<Word> wordEntryList =  null ;  
    private  Word wordEntry =  null ;   
    private  Boolean startEntryElementFlag =  false ;  
    //解析xml数据   
    public  ArrayList<Word> parse(InputStream inStream)  
    {  
        try  {  
                        //创建XmlPullParser,有两种方式   
            //方式一:使用工厂类XmlPullParserFactory   
            XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();  
            XmlPullParser xmlPullParser = pullFactory.newPullParser();  
//          //方式二:使用Android提供的实用工具类android.util.Xml   
//          XmlPullParser xmlPullParser = Xml.newPullParser();   
            xmlPullParser.setInput(inStream, "UTF-8" );  
            int  eventType = xmlPullParser.getEventType();  
            boolean  isDone =  false ;   
            //具体解析xml   
            while  ((eventType != XmlPullParser.END_DOCUMENT)&&(isDone !=  true )) {  
                String localName = null ;  
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {
					wordEntryList = new ArrayList<Word>();
				}
					break;
				case XmlPullParser.START_TAG: {
					localName = xmlPullParser.getName();
					if (localName.equalsIgnoreCase(kItemElementName)) {
						wordEntry = new Word();
						startEntryElementFlag = true;
					} else if (startEntryElementFlag == true) {

						if (localName.equalsIgnoreCase(kWordElementName)) {
							String word = xmlPullParser.nextText();
							wordEntry.setWord(word);
							
						} else if (localName.equalsIgnoreCase(kTransElementName)) {
							String explain = xmlPullParser.nextText();
							wordEntry.setExplain(explain);
							
						} else if (localName.equalsIgnoreCase(kPhoneticdElementName)) {
							String yinBiao = xmlPullParser.nextText();
							wordEntry.setYinBiao(yinBiao);
							
						}else  if(localName.equalsIgnoreCase(kTagsElementName)){
							
						}else {
							new RuntimeException("格式错误，只支持有道词典xml格式！");
						}
					}
				}
					break;
				case XmlPullParser.END_TAG: {
					localName = xmlPullParser.getName();
					if ((localName.equalsIgnoreCase(kItemElementName))
							&& (startEntryElementFlag == true)) {
						wordEntryList.add(wordEntry);
						startEntryElementFlag = false;
					}
				}
					break;
				default:
					break;
				}  
                eventType = xmlPullParser.next();  
            }  
        } catch  (Exception e) {  
            e.printStackTrace();  
        }  
        Log.v("Pull" ,  "End" );  
        return  wordEntryList;  
    }  
}  