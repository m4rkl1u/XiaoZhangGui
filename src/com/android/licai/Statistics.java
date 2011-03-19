package com.android.licai;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.android.licai.data.Constant;
import com.android.licai.data.MyTime;
import com.android.licai.view.*;
import android.app.*;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;

public class Statistics extends Activity {
	
	StatView sv;
	Button 	 nxt,prt;
	ListView lv;
	WebView  wv;
	TextView wv_tv,date;
	String []items;
	int 	 viewStatus;
	int		 statStatus;
	
	
	final int showPie = Menu.FIRST;
	final int showCurve = Menu.FIRST+1;
	final int weekStat	= Menu.FIRST+2;
	final int monthStat = Menu.FIRST+3;
	final int yearStat	= Menu.FIRST+4;
	final int curveId = 0;
	final int pieId	  = 1;
	final int otherId = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        viewStatus = Constant.pieChart;
        statStatus = weekStat;
        
        sv = (StatView)findViewById(R.id.statView);
        nxt= (Button)findViewById(R.id.stat_nxt);
        lv = (ListView)findViewById(R.id.listLeft);
        items = getResources().getStringArray(R.array.items);
        date  = (TextView)findViewById(R.id.date);
        wv 	  = (WebView)findViewById(R.id.lineChart);
        
        nxt.setOnClickListener(nxtListener);
        
        prt = (Button)findViewById(R.id.stat_pre);
        prt.setOnClickListener(prtListener);
        
	}
	
	View.OnClickListener nxtListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			GregorianCalendar beginDay,endDay;
			MyTime time = new MyTime();
			switch(statStatus){
			case weekStat:
				calendar.add(Calendar.WEEK_OF_YEAR, +1);
				beginDay = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				endDay = time.GetThisSundayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
						     new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
				if(viewStatus == Constant.pieChart) reDraw(beginDay,endDay);
				else if(viewStatus == Constant.curve) {
					/*if((rt = genLineChart(beginDay,endDay))!= null){
						wv.loadData(rt,"text/html","utf-8");
					}else{
						wv.removeAllViews();
						wv_tv.setText(Constant.gnew);
					}*/
					wv.loadData(genLineChart(beginDay,endDay),"text/html","utf-8");
				}
				break;
			case monthStat:
				calendar.add(Calendar.MONTH, +1);
				beginDay = time.getFirstDayOfMonth((new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))));
				endDay = time.getLastDayOfMonth(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
					     new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
				if(viewStatus == Constant.pieChart) reDraw(beginDay,endDay);
				else if(viewStatus == Constant.curve) {
					wv.loadData(genLineChart(beginDay,endDay),"text/html","utf-8");
				}
				break;
			}
		}
	};
	
	View.OnClickListener prtListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			GregorianCalendar beginDay,endDay;
			MyTime time = new MyTime();
			switch(statStatus){
			case weekStat:
				calendar.add(Calendar.WEEK_OF_YEAR, -1);
				beginDay = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				endDay = time.GetThisSundayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
						     new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
				if(viewStatus == Constant.pieChart) reDraw(beginDay,endDay);
				else if(viewStatus == Constant.curve) {
					wv.loadData(genLineChart(beginDay,endDay),"text/html","utf-8");
				}
				break;
			case monthStat:
				calendar.add(Calendar.MONTH, -1);
				beginDay = time.getFirstDayOfMonth((new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))));
				endDay = time.getLastDayOfMonth(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
					     	 new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
				if(viewStatus == Constant.pieChart) reDraw(beginDay,endDay);
				else if(viewStatus == Constant.curve) {
					wv.loadData(genLineChart(beginDay,endDay),"text/html","utf-8");
				}
				break;
			}
			
		}
	};
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		
		menu.add(pieId,showCurve,Menu.NONE,getResources().getString(R.string.getCurve));
		menu.add(curveId,showPie,Menu.NONE,getResources().getString(R.string.pieChart));
		
		menu.add(otherId,weekStat,Menu.NONE,getResources().getString(R.string.weekStat));
		menu.add(otherId,monthStat,Menu.NONE,getResources().getString(R.string.monthStat));
		//menu.add(otherId,yearStat,Menu.NONE,getResources().getString(R.string.yearStat));
		
		menu.setGroupVisible(curveId, false);
		menu.setGroupVisible(otherId, true);
		if(Constant.connectToInternet == false) menu.setGroupVisible(pieId,false);
		else menu.setGroupVisible(pieId, true);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		switch(viewStatus){
		case Constant.pieChart:
			menu.setGroupVisible(curveId, false);
			menu.setGroupVisible(pieId, true);
			break;
		case Constant.curve:
			menu.setGroupVisible(curveId, true);
			menu.setGroupVisible(pieId, false);
			break;
		}
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId,MenuItem item){
		//getMenu();
		
		MyTime time = new MyTime();
		GregorianCalendar beginDay = null;
		GregorianCalendar endDay = null;
		this.calendar = new GregorianCalendar();
		
		switch(item.getItemId()){
		case showCurve:
			Log.v(Constant.LiCai,"curve");
			this.setContentView(R.layout.stat2);
			setUpContentforLineChart();
			switch(statStatus){
				case weekStat:
					beginDay = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
					endDay = time.GetThisSundayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));					
					break;
				case monthStat:
					beginDay = time.getFirstDayOfMonth((new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))));
					endDay = time.getLastDayOfMonth(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
					break;
			}
			date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
				     	 	 new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
			wv.loadData(genLineChart(beginDay,endDay),"text/html","utf-8");
			viewStatus = Constant.curve;
			break;
		case showPie:
			Log.v(Constant.LiCai,"pie chart");
			this.setContentView(R.layout.statistics);
			setUpContent();
			switch(statStatus){
			case weekStat:
				beginDay = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				endDay = time.GetThisSundayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));				
				break;
			case monthStat:
				beginDay = time.getFirstDayOfMonth((new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))));
				endDay = time.getLastDayOfMonth(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				break;
			}
			date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
				         new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
			reDraw(beginDay,endDay);
			viewStatus = Constant.pieChart;
			break;
		case weekStat:
			statStatus = weekStat;
			switch(viewStatus){
			case Constant.pieChart:
				beginDay = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				endDay = time.GetThisSundayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
						     new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
				reDraw(beginDay,endDay);
				break;
			case Constant.curve:
				beginDay = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				endDay = time.GetThisSundayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));					
				wv.loadData(genLineChart(beginDay,endDay),"text/html","utf-8");
				break;
			}
			break;
		case monthStat:
			statStatus = monthStat;
			switch(viewStatus){
			case Constant.pieChart:
				beginDay = time.getFirstDayOfMonth((new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))));
				endDay = time.getLastDayOfMonth(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
					     new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
				reDraw(beginDay,endDay);
				break;
			case Constant.curve:
				beginDay = time.getFirstDayOfMonth((new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))));
				endDay = time.getLastDayOfMonth(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
				date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
					     	 new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
				wv.loadData(genLineChart(beginDay,endDay),"text/html","utf-8");
				break;
			}
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void setUpContentforLineChart(){
		wv 	  = (WebView)findViewById(R.id.lineChart);
		wv_tv = (TextView)findViewById(R.id.web_wranning);
		nxt= (Button)findViewById(R.id.stat_nxt);
	    prt = (Button)findViewById(R.id.stat_pre);
	    date  = (TextView)findViewById(R.id.date);
	    
	    nxt.setOnClickListener(nxtListener);
        
        prt.setOnClickListener(prtListener);
	}
	
	private String genLineChart(GregorianCalendar beginDay,GregorianCalendar endDay){
		
		int range = 0;
		switch(statStatus){
		case weekStat:
			range = 7;
			break;
		case monthStat:
			range = new MyTime().getMonthDay(beginDay);
			break;
		}
		String rtString = "";
		MyTime time = new MyTime();
		
		String[] col = {"money","date"};
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str="date>="+"\'"+sdf.format(beginDay.getTime())+" 00:00:00"+"\'"+" AND "+
				   "date<="+"\'"+sdf.format(endDay.getTime())+" 00:00:00"+"\'";
		String order = "date";
		
		Cursor cur = Constant.licaidb.query(Constant.DBTable,col,str,
											null, null, null, order);
		long rt = cur.getCount();
		if(rt == 0){//没有记录，直接返回空的试图
			rtString = "<html><body>"+													//body
		 	   "<img src=http://chart.apis.google.com/chart?chf=bg,s,FFFFFF"+			//head
		 	   "&chxl=0:|"+ XAxis(range)												/*星期一|星期二|星期三|星期四|星期五|星期六|星期天|*/+		//x轴
		 	   "1:|"+"0|100|200|300|400|500"+											//"0|20|40|60|80|135"+						//y轴
		 	   "&chxr=1,"+Integer.toString((int)0)+","+Integer.toString((int)500)+		//"&chxr=1,0,105"+	//待修改
		 	   "&chxs=0,000000,11.5,0,l,000000|1,000000,11.5,0,l,000000"+
		 	   "&chxt=x,y"+
		 	   "&chs=310x220"+
		 	   "&cht=lc"+
		 	   "&chco=FF0000"+
		 	   "&chds="+Integer.toString((int)0)+","+Integer.toString((int)500)+		//最大最小值
		 	   "&chdlp=b"+
		 	   "&chg=0,20"+
		 	   "&chls=2"+
		 	   "&chma=0,5,5,25"+
		 	   "&chtt="+(range == 7?"周消费统计":"月消费统计")+							//label
		 	   "&chts=FFFFFF,11.5"+
		 	   "</body></html>";
			return rtString;
		}
		
		double[] everyDate = new double[range];
		String[] week	   = new String[range];
		//String curDay	   = null;
		String tmpDay	   = null;
		int max        = 0;
		//double min 		   = 0;
		
		for(int i = 0;i<range;i++){
			week[i] = sdf.format(beginDay.getTime())+" 00:00:00";
			beginDay.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		if(statStatus == weekStat){
			beginDay = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
		}else if(statStatus == monthStat){
			beginDay = time.getFirstDayOfMonth((new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))));
		}
		int begin = 0;
		
		int i;
		cur.moveToFirst();
		do{									//a little algorithm,a sorted array classification
			tmpDay = cur.getString(1);
			for(i = begin;i<range;i++){
				if(week[i].equals(tmpDay)){
					everyDate[i] += cur.getDouble(0);
					break;
				}else begin++;
			}
			if(max < everyDate[i]) max = (int)everyDate[i];
		}while(cur.moveToNext());
		
		int []yAxis = new int[6];
		int newMax = 0;
		String maxStr = Integer.toString(max);
		int len = maxStr.length();
		if(len>2){
			newMax = (max/(int)Math.pow(10, len-2))+1;
			newMax = newMax *(int)Math.pow(10, len-2);
		}else{
			newMax = max;
		}
		
		max = newMax;

		
		yAxis[0] = 0;
		yAxis[5] = (int) max;
		
		yAxis[1] = yAxis[0] + (int)max /5;
		yAxis[2] = yAxis[0] + (int)max*2/5;
		yAxis[3] = yAxis[0] + (int)max*3/5;
		yAxis[4] = yAxis[0] + (int)max*4/5;
		
		//google chart api,FML
		try{
			rtString = "<html><body>"+																	//body
				 	   "<img src=http://chart.apis.google.com/chart?chf=bg,s,FFFFFF"+					//head
				 	   "&chxl=0:|"+ XAxis(range)/*星期一|星期二|星期三|星期四|星期五|星期六|星期天|*/+		//x轴
				 	   "1:|"+YAxis(yAxis)+//"0|20|40|60|80|135"+										//y轴
				 	   "&chxr=1,"+Integer.toString((int)0)+","+Integer.toString((int)max)+//"&chxr=1,0,105"+	//待修改
				 	   "&chxs=0,000000,11.5,0,l,000000|1,000000,11.5,0,l,000000"+
				 	   "&chxt=x,y"+
				 	   "&chs=310x220"+
				 	   "&cht=lc"+
				 	   "&chco=FF0000"+
				 	   "&chds="+Integer.toString((int)0)+","+Integer.toString((int)max)+				//最大最小值
				 	   "&chd=t:"+ fetchDataString(everyDate,range)+//"&chd=t:20,30,40,50,60,70,80"+		//每点的y值
				 	   "&chdlp=b"+
				 	   "&chg=0,20"+
				 	   "&chls=2"+
				 	   "&chma=0,5,5,25"+
				 	   "&chtt="+(range == 7?"周消费统计":"月消费统计")+//周消费统计"+//to do
				 	   "&chts=FFFFFF,11.5"+
				 	   "</body></html>";
			/*rtString = "<html><body>"+"<img src="+"http://chart.apis.google.com/chart"+
					   "?chf=bg,s,FFFFFF"+
					   "&chxl=0:|星期一|星期二|星期三|星期四|星期五|星期六|星期天|1:|0|20|40|60|80|100"+
					   "&chxr=1,0,9482"+
					   "&chxs=0,000000,11.5,0,l,000000|1,000000,11.5,0,l,000000"+
					   "&chxt=x,y"+
					   "&chs=310x200"+
					   "&cht=lc"+
					   "&chco=FF0000"+
					   "&chds=0,9482"+
					   "&chd=t:0,0,521,5967,2294,0,9482"+
					   "&chdlp=b"+
					   "&chg=0,20"+
					   "&chls=2"+
					   "&chma=0,5,5,25"+
					   "&chtt=周消费统计"+
					   "&chts=000000,11.5"+"</body></html>";*/
		}catch(Exception e){
			Log.v(Constant.LiCai,e.getMessage());
		}
		
		cur.close();
		return rtString;
	}
	
	//获取X轴数据
	private String XAxis(int r){
		String rt = "";
		//int i = 0;
		if(r == 7) rt = "星期一|星期二|星期三|星期四|星期五|星期六|星期天|";
		else {
			/*while(i<r){
				rt = rt+(i+1)+"|";
				i++;
			}*/
			rt = "1|5|10|15|20|25|"+r+'|';
		}		
		return rt;
	}
	
	//获取每天的数据
	private String fetchDataString(double []date,int r){
		String rt = "";
		int i = 0;
		if(r == 7){
			while(i<6){
				rt += Integer.toString((int)date[i]);
				rt +=",";
				i++;
			}
			rt += Integer.toString((int)date[i]);
		}else{
			while(i<r-1){
				rt += Integer.toString((int)date[i]);
				rt +=",";
				i++;
			}
			rt += Integer.toString((int)date[i]);
		}
		
		return rt;
	}
	
	//获取y轴数据
	private String YAxis(int []date){
		String rt = "";
		int i = 0;
		while(i<5){
			rt += Integer.toString(date[i]);
			rt +="|";
			i++;
		}
		rt += Integer.toString(date[5]);
		return rt;
	}
	
	GregorianCalendar calendar;	//当前日期
	
	private void setUpContent(){
		
		sv = (StatView)findViewById(R.id.statView);
        nxt= (Button)findViewById(R.id.stat_nxt);
        prt = (Button)findViewById(R.id.stat_pre);
        lv = (ListView)findViewById(R.id.listLeft);
        items = getResources().getStringArray(R.array.items);       
        date  = (TextView)findViewById(R.id.date);
        nxt.setOnClickListener(nxtListener);
       
        prt.setOnClickListener(prtListener);
	}
	
	private void reDraw(GregorianCalendar beginDay,GregorianCalendar endDay){
		String[] col = {"money","item"};
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str="date>="+"\'"+sdf.format(beginDay.getTime())+" 00:00:00"+"\'"+" AND "+
				   "date<="+"\'"+sdf.format(endDay.getTime())+" 00:00:00"+"\'";
		String order = "date";
		
		Cursor cur = Constant.licaidb.query(Constant.DBTable,col,str,
								null, null, null, order);
		
		long rt = cur.getCount();
		if(rt == 0){
			sv.drawMyCircle(null);
			setUpAdapter(null);
			return ;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();

		double num;
		Object tmp;
		
		for(int i = 0;i<items.length;i++){
			map.put(items[i],0.0);
		}
		
		cur.moveToFirst();
		do{
			str = cur.getString(1);
			num = cur.getDouble(0);
			map.put(str, (tmp = map.get(str)) == null?num:(Double)tmp + num);
		}while(cur.moveToNext());
		cur.close();
		//draw the circle
		sv.drawMyCircle(new HashMap<String, Object>(map));
		
		setUpAdapter(new HashMap<String, Object>(map));
	}
	
	@Override
	public void onResume(){
		super.onResume();
		this.setContentView(R.layout.statistics);
		setUpContent();
		viewStatus = Constant.pieChart;
        statStatus = weekStat;
        
		calendar = new GregorianCalendar();
		MyTime time = new MyTime();
		GregorianCalendar beginDay = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
		GregorianCalendar endDay   = time.GetThisSundayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
		//date.setTextColor(Color.WHITE);
		date.setText(new SimpleDateFormat(Constant.dateformat).format(beginDay.getTime())+" 至 "+
					 new SimpleDateFormat(Constant.dateformat).format(endDay.getTime()));
		reDraw(beginDay,endDay);
	}
	
	public void setUpAdapter(Map<String,Object> map){
		
		TextView tv = (TextView)findViewById(R.id.empty);
		if(map != null){
			StatListAdapter sla = new StatListAdapter(lv.getContext(),getData(map));
			lv.setAdapter(sla);
			tv.setText("");
		}else{
			lv.setAdapter(null);
		//	tv.setTextColor(Color.BLACK);
			tv.setText(Constant.gnew);
		}
	}
	
	
	//获取List数据，从Map data映射到List
	private List<Map<String, Object>> getData(Map<String,Object> data) {
		if(data == null || data.size() == 0)
			return null;
		
		double [] d = new double[items.length];
		double sum = 0;
		Double rt   = null;
		int i = 0;
		while(i<data.size()){
			d[i] = (rt = (Double)data.get(items[i])) == null?0:rt;
			sum += d[i];
			i++;
		}
		
		i = 0;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String,Object> map = null;
		
		DecimalFormat df=new DecimalFormat("#.00");
		
		while(i<data.size()){
			if(d[i]>0){
				map = new HashMap<String, Object>();
				map.put("item", items[i]);
				map.put("percent", df.format((d[i]*100/sum))+"%");
				map.put("color", Constant.color[i]);
				list.add(map);
			}
			i++;
		}
		return list;
	}
	
	//adapter的视图-数据映射
	public final class ViewHolder{
		public TextView	color;
		public TextView item;
		public TextView percent;
	}
	
	public class StatListAdapter extends BaseAdapter{

		private List<Map<String, Object>> list;
		private LayoutInflater mInflater;
		public StatListAdapter(Context context,List<Map<String, Object>> _list){
			list = _list;
			this.mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		//custom list修改
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = mInflater.inflate(R.layout.stat_data, null);
				vh.color = (TextView)convertView.findViewById(R.id.color);
				vh.item  = (TextView)convertView.findViewById(R.id.item);
				vh.percent = (TextView)convertView.findViewById(R.id.percent);
				convertView.setTag(vh);
			}else {
				vh = (ViewHolder)convertView.getTag();
			}
			
			Map<String, Object> map = list.get(position);
			vh.color.setBackgroundColor((Integer)map.get("color"));		
			vh.item.setText((String)map.get("item"));
			vh.percent.setText((String)map.get("percent"));
			return convertView;
		}
	}
}
