package com.xpila.awake;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.content.Context;
import android.view.View;
import android.view.KeyEvent;


public class MainActivity extends Activity
{
	private PowerManager mPowerManager = null; //power manager service
	private WakeLock mWakeLock = null; //current wake lock
	private int mWakeMode = 0; //current wake mode
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE); //obtain power manager
		if (savedInstanceState != null)
		{
			int wakeMode = savedInstanceState.getInt("WakeMode", 0);
			setWakeMode(wakeMode);
		}
		updateUI();
	}
	@Override protected void onDestroy()
	{
		setWakeMode(0); //release and free current wake lock
		super.onDestroy();
	}
	@Override public void onSaveInstanceState(Bundle outState)
	{
		outState.putInt("WakeMode", mWakeMode);
		super.onSaveInstanceState(outState);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{
		//minimize the app on back pressed
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			if (mWakeMode == 0)
				finish(); //exit app when wake lock disabled
			else
				this.moveTaskToBack(true); //minimize the app
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}	
    public void Wake_onClick(View v)
	{
		//set wake mode 
		if (v.getId() == R.id.btnWake_Disabled) setWakeMode(0);
		else if (v.getId() == R.id.btnWake_Partial) setWakeMode(PowerManager.PARTIAL_WAKE_LOCK);
		else if (v.getId() == R.id.btnWake_ScreenDim) setWakeMode(PowerManager.SCREEN_DIM_WAKE_LOCK);
		else if (v.getId() == R.id.btnWake_ScreenBright) setWakeMode(PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
		else if (v.getId() == R.id.btnWake_Full) setWakeMode(PowerManager.FULL_WAKE_LOCK);
		updateUI();
		if (mWakeMode == 0)
			finish(); //exit app when wake lock disabled
		else
			this.moveTaskToBack(true); //minimize the app
	}	
	private void setWakeMode(int wakeMode)
	{
		//release and free old wake lock (if exists)
		if ((mWakeMode != 0) && (mWakeLock != null))
		{
			mWakeLock.release();
			mWakeLock = null;
		}
		//create and acquire new wake lock
		if (wakeMode != 0)
		{
			mWakeLock = mPowerManager.newWakeLock(wakeMode, "AWake");
			mWakeLock.acquire();
		}
		//set current mode
		mWakeMode = wakeMode;		
	}
	private void updateUI()
	{
		//enable/disable buttons
		findViewById(R.id.btnWake_Disabled).setEnabled(mWakeMode != 0);
		findViewById(R.id.btnWake_Partial).setEnabled(mWakeMode != PowerManager.PARTIAL_WAKE_LOCK);
		findViewById(R.id.btnWake_ScreenDim).setEnabled(mWakeMode != PowerManager.SCREEN_DIM_WAKE_LOCK);
		findViewById(R.id.btnWake_ScreenBright).setEnabled(mWakeMode != PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
		findViewById(R.id.btnWake_Full).setEnabled(mWakeMode != PowerManager.FULL_WAKE_LOCK);		
	}
}
