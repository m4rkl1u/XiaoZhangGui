package com.android.licai;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import com.android.licai.data.Constant;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

//实时汇率获取，每天限制使用10次
public class ExchangeRate extends Activity{

	//there is limit,store it
	//update everyday night
	Map<String,Object> map;
	ListView lv;
	
	GregorianCalendar thisDay = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.exchange);
		
		lv = (ListView)findViewById(R.id.liste);
		
		thisDay = new GregorianCalendar();
		
		setUpAdapter(getData());
		
	//	updateCurrentRate();
	}
	
	private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	private void updateCurrentRate(){
		//http://xurrency.com/api/usd/cny/100 a limit to 10 times everyday
		HttpClient httpclient = new DefaultHttpClient();
		String []country = {"usd","eur","hkd","jpy"};
		
		map = new HashMap<String,Object>();
		
		for(int i = 0;i<country.length;i++){
			HttpGet httpget = new HttpGet("http://xurrency.com/api/"+country[i]+"/cny/100");
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					
					InputStream instream = entity.getContent();
	                String result= convertStreamToString(instream);
	                Log.v(Constant.LiCai,result);//result
	 
	                // JSON
	                JSONObject json=new JSONObject(result);
	               
	                JSONArray nameArray=json.names();
	                JSONArray valArray=json.toJSONArray(nameArray);
	                
	                int j;
	                for(j =0;j<nameArray.length();j++){
	                	if(nameArray.getString(i).equals("status")) break;
	                }
	                if(valArray.getString(j).equals("fail")){
	                	//to do
	                	return ;
	                }
	                
	                for(j = 0;j<nameArray.length();j++){
	                	if(nameArray.getString(i).equals("result")) break;
	                }
	                
	                JSONObject	rate = new JSONObject(valArray.getString(j));
	                nameArray = rate.names();
	                valArray  = rate.toJSONArray(nameArray);
	                
	                for(j = 0;j<valArray.length();j++){
	                	if(nameArray.getString(j).equals("value")) break;
	                }
	                
	                map.put(country[i],valArray.getString(j));
	                
	                instream.close();
				}
			} catch (ClientProtocolException e) {
				Log.v(Constant.LiCai,e.getMessage());
			} catch (IOException e) {
				Log.v(Constant.LiCai,e.getMessage());
			} catch (JSONException e) {
				Log.v(Constant.LiCai,e.getMessage());
			}
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		long DAY = 24L * 60L * 60L * 1000L;
		GregorianCalendar curDay = new GregorianCalendar();
		//Log.v(Constant.LiCai,(curDay.getTimeInMillis()- thisDay.getTimeInMillis())/DAY);
		if((curDay.getTimeInMillis()-thisDay.getTimeInMillis())/DAY > 1){
	//		updateCurrentRate();
			thisDay = curDay;
		}
	}
	
	private void setUpAdapter(List<Map<String, Object>> list){
		if(list!=null){
			SimpleAdapter adapter = new SimpleAdapter(this,list,R.layout.exchange_data,
					  								  new String[]{"country","ex"},
					  								  new int[]{R.id.country,R.id.ex});
			lv.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}
	
	private List<Map<String, Object>> getData(){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("country", "美国");
		map.put("ex", "6.87元");
		
		list.add(map);
		
		return list;
	}
}
