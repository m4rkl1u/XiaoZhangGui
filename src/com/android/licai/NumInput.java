package com.android.licai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//first activity,create everything
public class NumInput extends Activity {
	
	EditText entry;
	Button one,two,three,four,five,six,seven,eight,nine,zero,clear,back,dot;
	double rt;
	
	boolean flag;
	int     afterDot;
	
	View.OnClickListener listener =	new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch(id)
			{
				case R.id.dot:
					addDot();
					break;
				case R.id.one:
					setNumber(1);
					break;
				case R.id.two:
					setNumber(2);
					break;
				case R.id.three:
					setNumber(3);
					break;
				case R.id.four:
					setNumber(4);
					break;
				case R.id.five:
					setNumber(5);
					break;
				case R.id.six:
					setNumber(6);
					break;
				case R.id.seven:
					setNumber(7);
					break;
				case R.id.eight:
					setNumber(8);
					break;
				case R.id.nine:
					setNumber(9);
					break;
				case R.id.zero:
					setNumber(0);
					break;
				case R.id.clear:
					clear();
					break;
				case R.id.back:
					back();
					break;
			}
		}
	};
	
	private void setNumber(int i){
		String num = entry.getText().toString();
			
		if(num.equals("0")){	
			String s = Integer.toString(i);  
			entry.setText(s);
			rt = Integer.parseInt(s);
		}else{
			if(!flag){
				num += Integer.toString(i);
				entry.setText(num);
				rt = Integer.parseInt(num);
			}else{
				if(afterDot < 2){
					num += Integer.toString(i);
					entry.setText(num);
					rt = Double.parseDouble(num);
					afterDot++;
				}
			}
		}

	}
	private void clear(){
		entry.setText("0");
		rt = 0;
		afterDot = 0;
		flag = false;
	}
	private void addDot(){
		String num = entry.getText().toString();
		num += ".";
		entry.setText(num);
		flag = true;
		afterDot = 0;
	}
	
	private void back(){
		String num = entry.getText().toString();
		if(num.length() == 1){
			entry.setText("0");
			rt = 0;
		}else{
			if(!flag){
				String s = (String) num.subSequence(0, num.length()-1);
				entry.setText(s);
				rt = Integer.parseInt(s);
			}else{
				if(afterDot > 1){
					afterDot--;
					String s = (String) num.subSequence(0, num.length()-1);
					entry.setText(s);
					rt = Double.parseDouble(s);
				}else{
					flag = false;
					String s = (String) num.subSequence(0, num.length()-2);
					entry.setText(s);
					rt = Integer.parseInt(s);
				}
			}
			
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numinput);
        
        rt = 0;
        
        entry = (EditText)this.findViewById(R.id.entry);
        one   = (Button)this.findViewById(R.id.one);
        two   = (Button)this.findViewById(R.id.two);
        three = (Button)this.findViewById(R.id.three);
        four  = (Button)this.findViewById(R.id.four);
        five  = (Button)this.findViewById(R.id.five);
        six   = (Button)this.findViewById(R.id.six);
        seven = (Button)this.findViewById(R.id.seven);
        eight = (Button)this.findViewById(R.id.eight);
        nine  = (Button)this.findViewById(R.id.nine);
        zero  = (Button)this.findViewById(R.id.zero);
        clear = (Button)this.findViewById(R.id.clear);
        back  = (Button)this.findViewById(R.id.back);
        dot	  = (Button)this.findViewById(R.id.dot);
        
        entry.setOnKeyListener(null);
        entry.setOnClickListener(null);
        
        one.setOnClickListener(listener);
        two.setOnClickListener(listener);
        three.setOnClickListener(listener);
        four.setOnClickListener(listener);
        five.setOnClickListener(listener);
        six.setOnClickListener(listener);
        seven.setOnClickListener(listener);
        eight.setOnClickListener(listener);
        nine.setOnClickListener(listener);
        zero.setOnClickListener(listener);
        clear.setOnClickListener(listener);
        back.setOnClickListener(listener);
        dot.setOnClickListener(listener);
        
        Button ok = (Button)this.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener(){
        	@Override
    		public void onClick(View v) {
        		Intent result = new Intent();
        		result.putExtra("Value", rt);
        		setResult(RESULT_OK,result);
        		finish();
        	}
        });
        
        Button cancel = (Button)this.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
        	@Override
    		public void onClick(View v) {
        		Intent result = new Intent();
        		result.putExtra("Value", 0);
        		setResult(RESULT_CANCELED ,result);
        		finish();
        	}
        });
    }
	
	/*private void moveCursor(){
		int pos = entry.getText().length();
		Selection.setSelection(entry.getText(), pos);
	}*/
	
	@Override
	public void onStart()
	{
		super.onStart();
		rt = (float) 0.0;
		entry.setText("0");
		flag = false;
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		/*Intent result = new Intent();
		result.putExtra("Value", rt);
		setResult(RESULT_OK,result);
		finish();*/
	}
	@Override
	public void onPause()
	{
		super.onPause();
		/*Intent result = new Intent();
		result.putExtra("Value", rt);
		setResult(RESULT_OK,result);
		finish();*/
	}
}
