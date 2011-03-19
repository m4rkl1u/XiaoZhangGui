package com.android.licai;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.*;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.android.licai.data.Constant;
import com.android.licai.data.MyTime;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class Manage extends ListActivity {
	
	private int mYear;
	private int mMonth;
	private int mDay;
    
	View.OnClickListener listener =	new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(Manage.this,AddNew.class);
			startActivityForResult(intent,Constant.AddNew);
		}
	};
    
    TextView date,money,weather,weather2;
	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage);
       // empty = (TextView)findViewById(R.id.empty);
		
		Button addnew = (Button)findViewById(R.id.add);
		addnew.setOnClickListener(listener);
		
		date = (TextView)findViewById(R.id.mag_time);
		money= (TextView)findViewById(R.id.mag_money);
		weather=(TextView)findViewById(R.id.weather);
		weather2=(TextView)findViewById(R.id.weather2);
		
		final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
		
		date.setText(new SimpleDateFormat(Constant.dateformat).format(c.getTime()));
		
        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.manage_date,
												  new String[]{"title","info","img"},
												  new int[]{R.id.title,R.id.info,R.id.img});
        
		setListAdapter(adapter);
		
	}
	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		MyTime time = new MyTime();
		SimpleDateFormat sdf=new SimpleDateFormat(Constant.dateformat);
		GregorianCalendar calendar = new GregorianCalendar();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "今天");
		map.put("info", sdf.format(calendar.getTime()));
		map.put("img", R.drawable.day);
		list.add(map);
		
		calendar = new GregorianCalendar();
		GregorianCalendar monday = time.GetThisMondayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
		GregorianCalendar sunday = time.GetThisSundayOfTheWeek(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
		
		map = new HashMap<String, Object>();
		map.put("title", "本周");
		map.put("info", sdf.format(monday.getTime()) + " 至 "+sdf.format(sunday.getTime()));
		map.put("img", R.drawable.week);
		list.add(map);
		

		GregorianCalendar beginDay = time.getFirstDayOfMonth((new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))));
		GregorianCalendar endDay = time.getLastDayOfMonth(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)));
		
		map = new HashMap<String, Object>();
		map.put("title", "本月");
		map.put("info", sdf.format(beginDay.getTime())+" 至 "+sdf.format(endDay.getTime()));
		map.put("img", R.drawable.month);
		list.add(map);
		
		return list;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.v(Constant.LiCai,Integer.toString(position));
		Intent intent = new Intent();
		intent.setClass(Manage.this,ListAll.class);
		switch(position){
		case 0://本日统计 
			intent.putExtra("date",Constant.day);
			startActivityForResult(intent,Constant.ListAll);
			break;
		case 1://本周统计
			intent.putExtra("date",Constant.week);
			startActivityForResult(intent,Constant.ListAll);
			break;
		case 2://本月统计
			intent.putExtra("date",Constant.month);
			startActivityForResult(intent,Constant.ListAll);
			break;
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		Calendar c = Calendar.getInstance();
        //完成当前支出
        String[] col = {"money"};
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str="date="+"\'"+sdf.format(c.getTime())+" 00:00:00"+"\'";
		
	    Cursor cur = Constant.licaidb.query(Constant.DBTable,col,str,
											null, null, null, null);
        
	    if(cur.getCount() == 0) {
	    	//money.setTextColor(Color.WHITE);
	    	money.setText(Constant.gnew);
	    	getCurrentWeather();
	    	return;
	    }
	    
	    double sum = 0;
	    cur.moveToFirst();
	    do{
	    	sum  += cur.getDouble(0);
	    }while(cur.moveToNext());
	    
	    DecimalFormat df=new DecimalFormat("#.00");
	    
	    money.setText("今天已消费金额："+df.format(sum)+"元");
	    cur.close();
	    
	    getCurrentWeather();
	}
	
	private void getCurrentWeather(){
		
		//http://www.google.com/ig/api?hl=zh-cn&weather=nanjing weather api
		String url = "http://www.google.com/ig/api?hl=zh-cn&weather="+Constant.city;
		String weather = "";
		String weather2= "";
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpUriRequest req = new HttpGet(url);
		try {
			HttpResponse resp = client.execute(req);
			
			HttpEntity ent = resp.getEntity();
			InputStream stream = ent.getContent();
			//将GBK转为utf-8，java只支持utf-8，FML
			String prase = GBKtoUTF8(stream);
			
			ByteArrayInputStream is = new ByteArrayInputStream(prase.getBytes());  
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
			DocumentBuilder db = dbf.newDocumentBuilder();  
			Document doc = db.parse(is);
			NodeList n = doc.getElementsByTagName("current_conditions");
			
			//天气状况
			weather  = "天气："+n.item(0).getChildNodes().item(0).getAttributes().item(0).getNodeValue();
			weather +="、";
			//温度
			weather += "气温："+n.item(0).getChildNodes().item(2).getAttributes().item(0).getNodeValue();
			weather +="℃ ";
			//湿度
			/*weather += n.item(0).getChildNodes().item(3).getAttributes().item(0).getNodeValue();
			weather +=" ";*/
			//风速
			weather2 += n.item(0).getChildNodes().item(5).getAttributes().item(0).getNodeValue();
			
		} catch (Exception e) {
		//	Log.v(Constant.LiCai,e.getMessage());
			this.weather.setText(weather);
		}
		this.weather.setText(weather);
		this.weather2.setText(weather2);
	}
	
	private String GBKtoUTF8(InputStream stream) throws IOException{
		String rt = "";
		BufferedInputStream bis = new BufferedInputStream(stream);
		ByteArrayBuffer buf = new ByteArrayBuffer(50);  
        int read_data = -1;  
        while ((read_data = bis.read()) != -1) {  
            buf.append(read_data);  
        }  
        rt = EncodingUtils.getString(buf.toByteArray(), "GBK");  
        
		return rt;
	}
}
