package com.mihaelisaev.swiper;

import android.app.Activity;
import android.os.Bundle;

public class OneActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one);
	}
	
	@Override
	public void onBackPressed() {
		this.getParent().onBackPressed();
	}
}
