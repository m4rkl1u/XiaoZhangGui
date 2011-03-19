package com.android.licai;

import com.android.licai.data.Constant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class AddrEdit extends Activity{

	AutoCompleteTextView edit;
	String []items = {"",""};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addredit);
        
        edit = (AutoCompleteTextView)findViewById(R.id.addrlist);
        
        edit.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {	
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//Log.v(Constant.LiCai,(String) s);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//Log.v(Constant.LiCai,(String) s);
			}
        	
        });
        edit.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, items));
        Button ok = (Button)findViewById(R.id.ok);
        Button cancel = (Button)findViewById(R.id.cancel);
        
        ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//update Queue
				Constant.queue.add(edit.getText().toString());
				//set result
				Intent result = new Intent();
				String s = edit.getText().toString();
        		result.putExtra("Addr", s);
				setResult(RESULT_OK,result);
				finish();
			}
		});
        
        cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED,null);
				finish();
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(Constant.queue!=null && Constant.queue.size()>0){
			String []tmp = new String[Constant.queue.size()];	
			for(int i = 0;i<Constant.queue.size();i++){
				tmp[i] = Constant.queue.get(i);
			}
			edit.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, tmp));
		}
	}
	
	@Override 
	public void onStop(){
		super.onStop();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}
}
