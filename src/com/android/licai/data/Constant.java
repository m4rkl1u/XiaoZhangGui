package com.android.licai.data;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public class Constant {

	//activity编号
	public final static int Manage   	= 1;
	public final static int Statistics	= 2;
	public final static int NumInput 	= 3;
	public final static int About		= 4;
	public final static int AddNew      = 5;
	public final static int ListItems	= 6;
	public final static int ListAll		= 7;
	public final static int AddrEdit	= 8;
	//tag
	public final static String LiCai	="LiCai";
	
	//dbname
	public final static String DBname 	="licai.db";
	public final static String DBTable	="licai";
	
	//db
	public static SQLiteDatabase licaidb=null;
	
	//date
	public final static int week		= 1;
	public final static int month		= 2;
	public final static int year		= 3;
	public final static int day			= 4;
	
	//dateformat
	public final static String dateformat = "yyyy年MM月dd日";
	
	//string 
	public final static String gnew		  = "太好了，没有任何消费支出";
	
	//color
	public final static int []color = {	0xaa0000ff,	//1		   						   
										0xaa00ffff,	//2
										0xaaffffff,	//3
										0xaa00ff00,	//4
										0xaaff00ff,	//5
										0xaaff0000,	//6
										0xaa000000,	//7
										0xaaffff00,	//8
										0xaa7f00ff,	//9
										0xaa7f7f00,	//10
										0xaaff7f7f	//11
									   };
	
	//view status
	public final static int pieChart = 0;
	public final static int curve	 = 1;
	
	//web warnning
	public final static String web_warnning = "很抱歉，因为网络问题，该功能无法实现";
	
	//web connection condition
	public static boolean connectToInternet = true;
	
	//location
	public static String city				= "南京";
	
	//Addr
	public static List<String>  queue = null;
	
}
