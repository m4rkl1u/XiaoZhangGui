package com.android.licai;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListItems extends ListActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // …Ë÷√Õ∏√˜
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources()
                									 .getStringArray(R.array.items)));
        getListView().setTextFilterEnabled(true);
       // setContentView(R.layout.listitems);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent();
		setResult(RESULT_OK,intent);
		intent.putExtra("item", getResources().getStringArray(R.array.items)[position]);
		finish();
	}

}
