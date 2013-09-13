package com.google.android.apps.lightcycle.glass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.apps.lightcycle.PanoramaCaptureActivity;
import com.google.android.apps.lightcycle.gallery.GalleryActivity;
import com.google.android.apps.lightcycle.sensor.SensorReader;

public class GlassMainMenuActivity extends Activity
{
  private static final float[] BUTTON_CENTERS_DEG;
  private static final int[] ITEM_IDS;
  private static final int MAX_ROT_YAW_DEG;
  private static final String TAG = GlassMainMenuActivity.class.getSimpleName();
  private static final int[] TITLES;
  private ImageView[] buttons = new ImageView[ITEM_IDS.length];
  private float currentYawDegrees = 0.0F;
  private Thread renderThread;
  private int selectedButton = 0;
  private TextView selectedItemTitle;
  private SensorReader sensorReader = new SensorReader();
  private PowerManager.WakeLock wakeLock;

  static
  {
    ITEM_IDS = new int[] { 2131558519, 2131558518, 2131558495 };
    TITLES = new int[] { 2131361817, 2131361827, 2131361818 };
    MAX_ROT_YAW_DEG = (int)(15.0F * (-1 + ITEM_IDS.length));
    BUTTON_CENTERS_DEG = new float[ITEM_IDS.length];
  }

  private float distanceToWeight(float paramFloat)
  {
    return (15.0F - paramFloat) / 15.0F;
  }

  private void onMenuItemSelected()
  {
    if (this.selectedButton == 0)
      finish();
    do
    {
      return;
      if (this.selectedButton != 2)
        continue;
      startActivity(new Intent(this, GalleryActivity.class));
      return;
    }
    while (this.selectedButton != 1);
    startActivity(new Intent(this, PanoramaCaptureActivity.class));
  }

  private void render()
  {
    float f1 = this.sensorReader.getAndResetGyroData()[1];
    this.currentYawDegrees = (float)(this.currentYawDegrees + Math.toDegrees(f1));
    this.currentYawDegrees = Math.max(Math.min(this.currentYawDegrees, MAX_ROT_YAW_DEG), 0.0F);
    float[] arrayOfFloat = new float[ITEM_IDS.length];
    for (int i = 0; i < ITEM_IDS.length; ++i)
    {
      float f2 = Math.abs(this.currentYawDegrees - BUTTON_CENTERS_DEG[i]);
      if (f2 >= 15.0F)
        continue;
      arrayOfFloat[i] = distanceToWeight(f2);
    }
    updateButtonSizes(arrayOfFloat);
  }

  private void updateButtonSizes(float[] paramArrayOfFloat)
  {
    int i = 0;
    if (i >= paramArrayOfFloat.length)
      label2: return;
    float f = 1.0F + paramArrayOfFloat[i];
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams((int)(f * 155.0F), (int)(f * 155.0F));
    this.buttons[i].setLayoutParams(localLayoutParams);
    if (f > 1.5D)
    {
      this.selectedButton = i;
      this.buttons[i].setAlpha(1.0F);
    }
    while (true)
    {
      this.selectedItemTitle.setText(TITLES[this.selectedButton]);
      ++i;
      break label2:
      this.buttons[i].setAlpha(0.3F);
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968613);
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 23)
    {
      onMenuItemSelected();
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  protected void onPause()
  {
    super.onPause();
    if (this.sensorReader != null)
      this.sensorReader.stop();
    this.renderThread.interrupt();
    this.wakeLock.release();
  }

  protected void onResume()
  {
    super.onResume();
    this.currentYawDegrees = (MAX_ROT_YAW_DEG / 2.0F);
    this.wakeLock = ((PowerManager)getSystemService("power")).newWakeLock(26, TAG);
    this.wakeLock.acquire();
    for (int i = 0; i < ITEM_IDS.length; ++i)
    {
      this.buttons[i] = ((ImageView)findViewById(ITEM_IDS[i]));
      BUTTON_CENTERS_DEG[i] = (15.0F * i);
    }
    this.selectedItemTitle = ((TextView)findViewById(2131558520));
    this.sensorReader.enableEkf(true);
    this.sensorReader.start(this);
    this.renderThread = new Thread()
    {
      public void run()
      {
        while (!isInterrupted())
          try
          {
            sleep(16L);
            GlassMainMenuActivity.this.runOnUiThread(new Runnable()
            {
              public void run()
              {
                GlassMainMenuActivity.this.render();
              }
            });
          }
          catch (InterruptedException localInterruptedException)
          {
          }
      }
    };
    this.renderThread.start();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 0)
    {
      onMenuItemSelected();
      return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.glass.GlassMainMenuActivity
 * JD-Core Version:    0.5.4
 */