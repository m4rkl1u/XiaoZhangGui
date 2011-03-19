package com.android.licai;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.licai.data.Constant;
import com.android.licai.data.MyTime;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class ListAll extends Activity{
	
	ExpandableListView 				expandList = null;
	SimpleExpandableListAdapter		adapter    = null;
	List<Map<String, Object>> 		group	   = null;
	List<List<Map<String, Object>>> child	   = null;
	List<Map<String, Object>> 		childdata  = null;
	TextView						empty	   = null;
	int							    set 	   = 0;
	GregorianCalendar				c		   = null;
	
	public boolean initialData() {  
        group = new ArrayList<Map<String, Object>> ();  
        child = new ArrayList<List<Map<String, Object>>>();
        
        Intent intent = getIntent();
        set 	= intent.getIntExtra("date", -1);
        Map<String, Object> map = null;
       
     //   boolean flag = false;
        TextView tv = (TextView)findViewById(R.id.curDate);
        MyTime time = new MyTime();
        
        switch(set){
        case Constant.day:
        	//step1:设置文字内容
			Log.v(Constant.LiCai,new SimpleDateFormat(Constant.dateformat).format(c.getTime()));
			tv.setText(new SimpleDateFormat(Constant.dateformat).format(c.getTime()));
			map = new HashMap<String, Object>();
        	map.put("date",new SimpleDateFormat(Constant.dateformat).format(c.getTime()));
        	group.add(map);
        	//step2:增加data
        	if(fillThisDay(c.getTime())!= false){
	        	child.add(childdata);
	        	setUpAdapter(true);
        	}else{
        		child.add(null);
        		setUpAdapter(false);
        	}
			break;
        case Constant.week:
        	GregorianCalendar weekmonday = time.GetThisMondayOfTheWeek(new GregorianCalendar(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)));
        	GregorianCalendar weeksunday = time.GetThisSundayOfTheWeek(new GregorianCalendar(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)));
        	tv.setText(time.DateToString(weekmonday, Constant.dateformat)+" 至 "+time.DateToString(weeksunday, Constant.dateformat));       	
        	setUpAdapter(fillLongTime(weekmonday.getTime(),weeksunday.getTime()));
        	break;
        case Constant.month:
        	GregorianCalendar firstMonthDay = time.getFirstDayOfMonth(new GregorianCalendar(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)));
        	GregorianCalendar LastMonthDay  = time.getLastDayOfMonth(new GregorianCalendar(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)));
        	tv.setText(time.DateToString(firstMonthDay, Constant.dateformat)+" 至 "+time.DateToString(LastMonthDay, Constant.dateformat));
        	setUpAdapter(fillLongTime(firstMonthDay.getTime(),LastMonthDay.getTime()));
        	break;
        }
        return false;
    }
	
	private boolean fillLongTime(Date beginDay,Date endDay){
		String[] col = {"money","item","addr","date"};
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str="date>="+"\'"+sdf.format(beginDay)+" 00:00:00"+"\'"+" AND "+
		   		   "date<="+"\'"+sdf.format(endDay)+" 00:00:00"+"\'";
		String order = "date";
		
		Cursor cur = Constant.licaidb.query(Constant.DBTable,col,str,
				null, null, null, order);
		
		long rt = cur.getCount();
		if(rt == 0){
			//done by setUpAdapter
			return false;
		}
		
		String curDate = "";
		String date    = "";
		Map<String, Object> map = null;
		childdata = null;
		SimpleDateFormat sdf2 = new SimpleDateFormat(Constant.dateformat);
		cur.moveToFirst();
		//从数据库中提取数据
		do{//将数据放入容器
			curDate = cur.getString(3);
			if(curDate.equals(date)){
				map = new HashMap<String, Object>();
	    		str = Double.toString(cur.getDouble(0));
	    		map.put("money", str+"元");
	    		str = cur.getString(1);
	    		map.put("item", str);
	    		if((str= cur.getString(2)) == null){
	    			map.put("addr", "其他");
	    		}else {
	    			map.put("addr", str);
	    		}childdata.add(map);
			}else{//假若不是当前日期，新建一个
				if(childdata != null) child.add(childdata);
				date = curDate;
				map = new HashMap<String, Object>();
				try {
					map.put("date",sdf2.format(sdf.parse(curDate).getTime()));
				} catch (ParseException e) {
					map.put("date","未知");
					e.printStackTrace();
					Log.e(Constant.LiCai,e.getMessage());
				}
    			group.add(map);
    			childdata = new ArrayList<Map<String, Object>>();
    			map = new HashMap<String, Object>();
	    		str = Double.toString(cur.getDouble(0));
	    		map.put("money", str+"元");
	    		str = cur.getString(1);
	    		map.put("item", str);
	    		if((str= cur.getString(2)) == null){
	    			map.put("addr", "其他");
	    		}else {
	    			map.put("addr", str);
	    		}childdata.add(map);		
			}
		}while(cur.moveToNext());
		if(childdata != null) child.add(childdata);
		return true;
	}
	
	private boolean fillThisDay(Date date)
	{
		String[] col = {"money","item","addr","date"};
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str="date="+"\'"+sdf.format(date)+" 00:00:00"+"\'";
		
		childdata = new ArrayList<Map<String, Object>>();
		
	    Cursor cur = Constant.licaidb.query(Constant.DBTable,col,str,
								null, null, null, null);
	    
	    if(cur.getCount() == 0) return false;
	    
	    if(cur.moveToFirst()){
	    	Map<String, Object> map = null;
	    	str = "";
	    	do{
	    		map = new HashMap<String, Object>();
	    		str = Double.toString(cur.getDouble(0));
	    		map.put("money", str+"元");
	    		str = cur.getString(1);
	    		map.put("item", str);
	    		if((str= cur.getString(2)) == null){
	    			map.put("addr", "其他");
	    		}else {
	    			map.put("addr", str);
	    		}childdata.add(map);
	    	}while(cur.moveToNext());
	    }
	    cur.close();
		return true;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listall);
        expandList = (ExpandableListView) findViewById(R.id.expandList);
        empty	   = (TextView)findViewById(R.id.empty);
        
        
        Button back= (Button)findViewById(R.id.goback);
        back.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			} 	
        });
        //修改时间handle
        Button pre = (Button)findViewById(R.id.pre);
        pre.setOnClickListener(prehandle);
        
        Button nxt = (Button)findViewById(R.id.nxt);
        nxt.setOnClickListener(nxthandle);
    }
	
	View.OnClickListener prehandle = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			switch(set){
			case Constant.day:
				c.set(Calendar.DATE,c.get(Calendar.DATE)-1 );
				initialData();
				break;
			case Constant.week:
				//c.add(Calendar.DATE, -7);
				c.add(Calendar.WEEK_OF_YEAR, -1);
				initialData();
				break;
			case Constant.month:
				c.add(Calendar.MONTH, -1);
				initialData();
				break;
			}
		}
    };
    
    View.OnClickListener nxthandle = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(set){
			case Constant.day:
				c.set(Calendar.DATE,c.get(Calendar.DATE)+1 );
				initialData();
				break;
			case Constant.week:
				//c.add(Calendar.DATE, +7);
				c.add(Calendar.WEEK_OF_YEAR, +1);
				initialData();
				break;
			case Constant.month:
				c.add(Calendar.MONTH, +1);
				initialData();
				break;
			}
		}
	};
    
    private void setUpAdapter(boolean b) {
    	
    	if(b == true){//设置adapter   
			adapter = new SimpleExpandableListAdapter(this, 
													  group, R.layout.listall_parent, R.layout.listall_parent,
													  new String[]{"date"},new int[]{R.id.date}, 
													  child, R.layout.listall_child,
													  new String[]{"item","money","addr"},
													  new int[]{R.id.item,R.id.money,R.id.addr});
			expandList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			empty.setText("");
		}else{
			adapter = null;
			expandList.setAdapter(adapter);
			//empty.setTextColor(Color.WHITE);
			empty.setText(Constant.gnew);
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		c = new GregorianCalendar();
		Log.v(Constant.LiCai,new SimpleDateFormat(Constant.dateformat).format(c.getTime()));
		initialData();
	}
		
}
