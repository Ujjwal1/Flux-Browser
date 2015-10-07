package com.ujjwal.webbrowser;

import android.app.TabActivity;
/*import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
*/
@SuppressWarnings("deprecation")
public class AddTabs extends TabActivity{
	/*private static int tabIndex = 0;
    private TabHost tabHost;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabHost = getTabHost();

        addTab();
        Button addTab = (Button)findViewById(R.id.add_tab);
        addTab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				tabIndex++;
                addTab();
			}
		});
        
    }
    public void addTab(){
    	tabIndex++;
    	 Button tabBtn = new Button(AddTabs.this);
    	    tabBtn.setText("Tab "+tabIndex);
    	    Intent tabIntent = new Intent(AddTabs.this, MainActivity.class);

    	    setupTab(tabBtn, tabIntent,"Tab "+tabIndex);    }
    protected void setupTab(View tabBtn, Intent setClass,String tag) {
        TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabBtn).setContent(setClass);
        tabHost.addTab(setContent);
    }*/
}
