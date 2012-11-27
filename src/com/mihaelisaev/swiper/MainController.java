package com.mihaelisaev.swiper;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainController extends TabActivity {
	String TAG = getClass().getName();
	private float MENU_WIDTH = .8f;
	private TabHost tabHost;
	Button oneButton;
	Button twoButton;
	Button exitButton;
	private RelativeLayout header;
	private RelativeLayout content;
	private RelativeLayout contentViewOverlay;
	private TextView headerTitle;
	private MainControllerGestureEventListener gestureEventListenerHeaderView;
	private MainControllerGestureEventListener gestureEventListenerMenuButton;
	private GestureDetector gestureScannerHeaderView;
	private GestureDetector gestureScannerBackButton;
	Map<String, String> mapActivityTitles;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        tabHost = getTabHost();
        setup();
        addActivities();
	}
	
	private void addActivities() {
		createActivity(OneActivity.class, getString(R.string.title_activity_one));
		createActivity(TwoActivity.class, getString(R.string.title_activity_two));
		tabHost.setCurrentTabByTag(OneActivity.class.toString());
		headerTitle.setText(mapActivityTitles.get(OneActivity.class.toString()));
	}
	
	private void createActivity(Class<?> cls, String title) {
		TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec(cls.toString());
        tabSpec.setIndicator("");
        tabSpec.setContent(new Intent(this, cls));
        tabHost.addTab(tabSpec);
        mapActivityTitles.put(cls.toString(), title);
	}
	
	private void setup() {
		header = (RelativeLayout)findViewById(R.id.header);
		content = (RelativeLayout)findViewById(R.id.tabLayout);
		contentViewOverlay = (RelativeLayout)findViewById(R.id.contentOverlay);
		headerTitle = (TextView)findViewById(R.id.headerTitle);
		mapActivityTitles = new HashMap<String, String>();
		createGestureEventListener();
		bindHeaderView();		
		bindBackButton();
		bindContentViewOverlayOnTouchWhenMenuOpened();
		bindButtons();
		tabHost.setOnTabChangedListener(new OnTabChangeListener()
		{
		    @Override
		    public void onTabChanged(String className) {
		    	try{
		    		final View tabContent = tabHost.findViewById(android.R.id.tabcontent);
			    	if(tabContent!=null){
			    		Animation fadeIn = AnimationUtils.loadAnimation(getBaseContext(), android.R.anim.fade_in);
			    	    tabContent.startAnimation(fadeIn);
			    	    headerTitle.startAnimation(fadeIn);
			    	}
		    	}catch(Exception e){
		    		Log.d(TAG, "onTabChanged exception: "+e.toString());
		    	}
		    }
		});
	}
	
	private void createGestureEventListener() {
		gestureEventListenerHeaderView = new MainControllerGestureEventListener() {
			@Override
			public void onSwipeRight() {
				if(!isMenuOpened())
					openMenu();
			}
			@Override
			public void onSwipeLeft() {
				if(isMenuOpened())
					closeMenu();
			}
			@Override
			public void onSwipeTop() {}
			@Override
			public void onSwipeBottom() {}
			@Override
			public void tapUp() {}
			@Override
			public void onScroll(float fromX, float fromY, float toX, float toY, float movedInX, float movedInY) {}
		};
		gestureScannerHeaderView = new GestureDetector(getApplicationContext(), new HeaderGestureListener(gestureEventListenerHeaderView));
		gestureEventListenerMenuButton = new MainControllerGestureEventListener() {
			@Override
			public void onSwipeRight() {
				if(!isMenuOpened())
					openMenu();
			}
			@Override
			public void onSwipeLeft() {
				if(isMenuOpened())
					closeMenu();
			}
			@Override
			public void onSwipeTop() {}
			@Override
			public void onSwipeBottom() {}
			@Override
			public void tapUp() {
				if(isMenuOpened())
					closeMenu();
				else
					openMenu();
			}
			@Override
			public void onScroll(float fromX, float fromY, float toX, float toY, float movedInX, float movedInY) {}
		};
		gestureScannerBackButton = new GestureDetector(getApplicationContext(), new HeaderGestureListener(gestureEventListenerMenuButton));
	}
	
	private void bindHeaderView() {
		header.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureScannerHeaderView.onTouchEvent(event);
			}
		});
	}
	
	private void manuallySetActivityLeftMargin(int left) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        params = (RelativeLayout.LayoutParams)content.getLayoutParams();
        params.setMargins(left,0,(left*(-1)),0);
        content.setLayoutParams(params);
	}
	
	public boolean isMenuOpened() {
		return (((RelativeLayout.LayoutParams)content.getLayoutParams()).leftMargin==0) ? false:true;
	}
	
	private void bindBackButton() {
		ImageView menuButton = (ImageView)header.findViewById(R.id.menuButton);
		menuButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureScannerBackButton.onTouchEvent(event);
			}
		});
	}
	
	private void bindContentViewOverlayOnTouchWhenMenuOpened() {
		contentViewOverlay.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(isMenuOpened()){
					closeMenu();
					return false;
				}else
					return true;
			}
		});
	}
	
	public void openMenu() {
		contentViewOverlay.setVisibility(View.VISIBLE);
		Animation moveRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_to_right);
		moveRight.setAnimationListener(
	        new AnimationListener() {
	            @Override
	            public void onAnimationStart(Animation animation) {}
	            @Override
	            public void onAnimationRepeat(Animation animation) {}
	            @Override
	            public void onAnimationEnd(Animation animation) {
	            	content.clearAnimation();
	            	manuallySetActivityLeftMargin((int)(getWindowWidth()*MENU_WIDTH));
	            }
	        }
	    );
		content.startAnimation(moveRight);
	}
	
	public int getWindowWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	return metrics.widthPixels;
	}
	
	public void closeMenu() {
		contentViewOverlay.setVisibility(View.GONE);
		Animation moveLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_to_left);
		manuallySetActivityLeftMargin(0);
		moveLeft.setAnimationListener(
		        new AnimationListener() {
		            @Override
		            public void onAnimationStart(Animation animation) {}
		            @Override
		            public void onAnimationRepeat(Animation animation) {}
		            @Override
		            public void onAnimationEnd(Animation animation) {
		            	content.clearAnimation();
		            }
		        }
		    );
		content.startAnimation(moveLeft);
	}
	
	private void bindButtons() {
		oneButton = (Button)findViewById(R.id.oneButton);
		twoButton = (Button)findViewById(R.id.twoButton);
		exitButton = (Button)findViewById(R.id.logoutButton);
		oneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeMenuAndStartIntent(OneActivity.class.toString());
			}
		});
		twoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeMenuAndStartIntent(TwoActivity.class.toString());
			}
		});
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void startIntent(final String tag) {
		final View tabContent = tabHost.findViewById(android.R.id.tabcontent);
    	if( tabContent != null ){
    		Animation fadeOut = AnimationUtils.loadAnimation(getBaseContext(), android.R.anim.fade_out);
   			tabContent.startAnimation(fadeOut);
   			headerTitle.startAnimation(fadeOut);
    	}
    	tabHost.setCurrentTabByTag(tag);
    	headerTitle.setText(mapActivityTitles.get(tag));
	}
	
	private void closeMenuAndStartIntent(String tag) {
		closeMenu();
		if(!tabHost.getCurrentTabTag().equals(tag))
			startIntent(tag);
	}
	
	@Override
	public void onBackPressed() {
		if(isMenuOpened())
			closeMenu();
		else
			super.onBackPressed();
	}
}
