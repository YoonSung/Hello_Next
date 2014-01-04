package org.nhnnext;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class Main extends ActivityGroup {

	private TabHost tabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
		
		tabHost = (TabHost) findViewById(R.id.tabHost);
		
		tabHost.setup(getLocalActivityManager());
		
		
		tabHost.addTab(tabHost.newTabSpec("TAB_CONTACTS")
			.setIndicator("리스트")
			.setContent(new Intent(this, ListTab.class)));
		
		tabHost.addTab(tabHost.newTabSpec("TAB_SETTING")
				.setIndicator("게임")
				.setContent(new Intent(this, GameTab.class)));
		
		
		tabHost.addTab(tabHost.newTabSpec("TAB_SETTING")
			.setIndicator("더보기")
			.setContent(new Intent(this, AdditionalTab.class)));
		

	}

}
