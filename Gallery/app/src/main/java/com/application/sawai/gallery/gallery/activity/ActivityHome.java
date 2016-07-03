package com.application.sawai.gallery.gallery.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.application.sawai.gallery.MyService;
import com.application.sawai.gallery.R;
import com.application.sawai.gallery.UtilClass;
import com.application.sawai.gallery.gallery.adapter.SlideMenuAdapter;
import com.application.sawai.gallery.gallery.model.SlideData;

import java.util.ArrayList;

public class ActivityHome extends ActionBarActivity implements SlideMenuAdapter.SlideMenuAdapterInterface{

    private static final int INT = 10000;
    private static int kJobId = 0;
    private Context mContext;
	private Toolbar toolbar;
	private DrawerLayout Drawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
	private Fragment currentFragment=null;
	
	private ListView slidingList;
	private SlideMenuAdapter mSlideMenuAdapter;
	private int currentPosition=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
         mContext=ActivityHome.this;
		 initiate();
         startUploadService();
		 initializeActionBar();
		 initialCalling();
		 
	}

    private void startUploadService() {
        startService(new Intent(getBaseContext(), MyService.class));
    }

    @Override
	public void onBackPressed() {
		if(Drawer.isDrawerOpen(Gravity.LEFT)){
			Drawer.closeDrawer(Gravity.LEFT);
	    }else{
	        super.onBackPressed();
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void slideRowClickEvent(int postion) {
		if (currentPosition== postion) {
			closeDrware();
			return;
		}
		currentPosition= postion;
		getFragment(postion);
		attachedFragment();
	}
	
	private void initializeActionBar() {
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		
		slidingList=(ListView)findViewById(R.id.sliding_listView);
		mSlideMenuAdapter=new SlideMenuAdapter(mContext, getSlideList());
		mSlideMenuAdapter.setSlidemenuadapterinterface(this);
		slidingList.setAdapter(mSlideMenuAdapter);

		Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
		mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar,
				R.string.openDrawer, R.string.closeDrawer) {

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

		};
		Drawer.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
		

	}
	
	private void closeDrware(){
		if(Drawer.isDrawerOpen(Gravity.LEFT)){
			Drawer.closeDrawer(Gravity.LEFT);
	    }
	}
	
	private void initialCalling(){
		fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

		getFragment(0);
		attachedFragment();
	}
	
	
	
	private void attachedFragment(){
		try {
			if (currentFragment != null) {
				if (fragmentTransaction.isEmpty()) {
					fragmentTransaction.add(R.id.fragment_container, currentFragment,"" + currentFragment.toString());
					fragmentTransaction.commit();
					toolbar.setTitle(title[currentPosition]);
				}else {
					fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.fragment_container, currentFragment,"" + currentFragment.toString());
					fragmentTransaction.commit();
					toolbar.setTitle(title[currentPosition]);
				}
				
			}
			closeDrware();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getFragment(int postion){
		switch (postion) { 
		case 0:
			currentFragment = new GalleryFragment();
			break;
		case 1:
			currentFragment = new CameraFragment();
			break;
		case 2:
			currentFragment = new VideoFragment();
			break;

		default:
			break;
		}
	}
	
	
	/**
	 * Slide Menu List Array.
	 */
	private String[] title={"All Images","Camera","Video"};
	private int[] titleLogo={R.drawable.selector_allpic,R.drawable.selector_camera,R.drawable.selector_video};
	private ArrayList<SlideData> getSlideList(){
		ArrayList<SlideData> arrayList=new ArrayList<SlideData>();
		for (int i = 0; i < title.length; i++) {
			SlideData mSlideData=new SlideData();
			mSlideData.setIcon(titleLogo[i]);
			mSlideData.setName(title[i]);
			mSlideData.setState((i==0)?1:0);
			arrayList.add(mSlideData);
		}
		return arrayList;

	}

	private void initiate() {
        setUserId();
        checkAndgrantPermission();
	}

	private void checkAndgrantPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    INT);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_BOOT_COMPLETED);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    INT);
        }
    }

    private void setUserId() {
        SharedPreferences sharedpreferences = getSharedPreferences(UtilClass.PREFS_NAME, Context.MODE_PRIVATE);
        UtilClass.getUserId(sharedpreferences);


    }

}
