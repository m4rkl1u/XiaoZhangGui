package com.android.licai;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.android.licai.data.Constant;

import android.app.*;
import android.content.*;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;


public class AddNew extends ListActivity {
	
	private List<Map<String, Object>> mData;
	AddNewAdapter adapter;
	
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;
	
	private int mYear =0;
    private int mMonth=0;
    private int mDay=0;
    
    double lat,lng;
    
    LocationListener locationListener = new LocationListener(){
		public void onLocationChanged(Location location){
			if(location!=null){
				lat = location.getLatitude();
				lng = location.getLongitude();
			}
		}
		public void onProviderDisabled(String provider){
			lat = -1;
			lng = -1;
		}
		public void onProviderEnabled(String provider){}
		public void onStatusChanged(String provider,int status,Bundle extras){}
	};
	
	/*private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
            }
        };
    */    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
    	new DatePickerDialog.OnDateSetListener() {
    	public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
    			mYear = year;
    			mMonth = monthOfYear;
    			mDay = dayOfMonth;
    			Map<String, Object> map = new HashMap<String, Object>();
				map.put("info", "时间："+Integer.toString(mYear)+"年"+Integer.toString(mMonth+1)+"月"+Integer.toString(mDay)+"日");
				mData.set(1, map);
				adapter.notifyDataSetChanged();
    			
    		}
    	};

    private double money = 0;
    
    private String item = "其他杂项";
    
    private String pos = "";
	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnew);
        
        lat = -1;
        lng = -1;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        lm.requestLocationUpdates(provider, 100, 5, this.locationListener);
        
		final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONDAY);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        mData = getData();
		adapter = new AddNewAdapter(this);
		setListAdapter(adapter);
		
		Button ok = (Button)findViewById(R.id.ok);
		ok.setOnClickListener(oklistener);
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(cancellistener);
		
		
		//TODO add ZXing Barcode reader.
		Button auto = (Button)findViewById(R.id.autodo);
		auto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	View.OnClickListener oklistener =	new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			//存储
			ContentValues values = new ContentValues();
			values.put("money",money);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			GregorianCalendar date = new GregorianCalendar(mYear,mMonth,mDay);
			String str = sdf.format(date.getTime())+" 00:00:00";
			values.put("date", str);
			values.put("item",item);
			values.put("addr", pos);
			values.put("info", " ");
			long rt = Constant.licaidb.insert(Constant.DBTable, null, values);
			Log.v(Constant.LiCai,Long.toString(rt));
			setResult(RESULT_OK,null);
			finish();
		}
	};
	
	View.OnClickListener cancellistener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED ,null);
			finish();
		}
	};
	
	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info", "金额："+"0元");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("info", "时间："+Integer.toString(mYear)+"年"+Integer.toString(mMonth+1)+"月"+Integer.toString(mDay)+"日");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("info", "项目：其他杂项");
		list.add(map);
		
		map = new HashMap<String,Object>();
		map.put("info", "地点：点击这里获取当前地址");
		list.add(map);
		
		return list;
	}
	
	boolean addrSucc = false;
	// ListView 中某项被选中后
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		Log.v(Constant.LiCai, (String) mData.get(position).get("info"));
		Intent intent =null;
		switch(position)
		{
		case 0:
			intent = new Intent();
			intent.setClass(AddNew.this,NumInput.class);
			startActivityForResult(intent,Constant.NumInput);
			break;
		case 1:
			showDialog(DATE_DIALOG_ID);
			break;
		case 2:
			intent = new Intent();
			intent.setClass(AddNew.this,ListItems.class);
			startActivityForResult(intent,Constant.ListItems);
			break;
		case 3:
			if(lat!=-1&&lng!=-1&&addrSucc==false){
				Geocoder gc = new Geocoder(this,Locale.getDefault());
				List<Address> addrs = null;
				try{
					addrs = gc.getFromLocation(lat, lng, 5);
				}
				catch(Exception e){
					Log.e("where am i error",e.getMessage());
					intent = new Intent();
					intent.setClass(this, AddrEdit.class);
					startActivityForResult(intent,Constant.AddrEdit);
					break;
				}
				if(addrs != null && addrs.size() > 0){
					Address addr = addrs.get(0);
				 	pos = addr.getLocality();
				 	Log.v(Constant.LiCai,pos);
				 	Map<String, Object> map = new HashMap<String, Object>();
				 	map.put("info", "位置："+pos);
				 	mData.set(3, map);
				 	adapter.notifyDataSetChanged();		
				}else{
					intent = new Intent();
					intent.setClass(this, AddrEdit.class);
					startActivityForResult(intent,Constant.AddrEdit);
				}
				addrSucc = true;
			}else{
				intent = new Intent();
				intent.setClass(this, AddrEdit.class);
				startActivityForResult(intent,Constant.AddrEdit);
			}
		}
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            /*case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);*/
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                            mDateSetListener,
                            mYear, mMonth, mDay);
        }
        return null;
    }
	
    //activity返回结果处理
	@Override 
    public void onActivityResult(int requestCode,int resultCode,Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode){
    		case Constant.NumInput:
    			if(resultCode == RESULT_OK && data != null){
    				double i = data.getDoubleExtra("Value",-1);
    				Log.v("LiCai",Double.toString(i));
    				if(i != -1){
    					Map<String, Object> map = new HashMap<String, Object>();
    					map.put("info", "金额："+new DecimalFormat("#.00").format(i)+"元");
    					mData.set(0, map);
    					adapter.notifyDataSetChanged();
    					money = i;
    				}
    			}
    			break;
    		case Constant.ListItems:
    			if(resultCode == RESULT_OK && data != null){
    				String s = data.getStringExtra("item");
    				Log.v(Constant.LiCai,s);
    				Map<String, Object> map = new HashMap<String, Object>();
    				map.put("info", "项目："+s);
    				mData.set(2,map);
    				adapter.notifyDataSetChanged();
    				item = s;
    			}
    			break;
    		case Constant.AddrEdit:
    			if(resultCode == RESULT_OK && data != null){
    				String s = data.getStringExtra("Addr");
    				Log.v(Constant.LiCai,s);
    				Map<String, Object> map = new HashMap<String, Object>();
    				if(s!=""){
    					map.put("info", "位置："+s);
    					mData.set(3, map);
				 		adapter.notifyDataSetChanged();
				 		pos = s;
    				}
    			}else{
    				Map<String, Object> map = new HashMap<String, Object>();
					map.put("info", "位置：获取失败");
					mData.set(3, map);
					adapter.notifyDataSetChanged();
					pos = "";
    			}
    			break;
    		default:
    			break;
    	}
    }
	
	public final class ViewHolder{
		public TextView info;
	}
	
	public class AddNewAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		public AddNewAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {
				
				holder=new ViewHolder();  
				
				convertView = mInflater.inflate(R.layout.addnew_data, null);
				holder.info = (TextView)convertView.findViewById(R.id.info);
				/*if(position != 3){
				//	holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
				}else {
				//	holder.viewBtn = null;
				}*/
				convertView.setTag(holder);
				
			}else {
				
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.info.setText((String)mData.get(position).get("info"));
			return convertView;
		}
	}
}
