package com.android.gallery3d.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings.System;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import com.android.gallery3d.ui.OrientationSource;
import java.util.ArrayList;

public class OrientationManager
  implements OrientationSource
{
  private Activity mActivity;
  private ArrayList<Listener> mListeners;
  private int mOrientation = -1;
  private int mOrientationCompensation = 0;
  private MyOrientationEventListener mOrientationListener;
  private boolean mOrientationLocked = false;
  private boolean mRotationLockedSetting = false;

  public OrientationManager(Activity paramActivity)
  {
    this.mActivity = paramActivity;
    this.mListeners = new ArrayList();
    this.mOrientationListener = new MyOrientationEventListener(paramActivity);
  }

  private void disableCompensation()
  {
    if (this.mOrientationCompensation == 0)
      return;
    this.mOrientationCompensation = 0;
    notifyListeners();
  }

  private static int getDisplayRotation(Activity paramActivity)
  {
    switch (paramActivity.getWindowManager().getDefaultDisplay().getRotation())
    {
    case 0:
    default:
      return 0;
    case 1:
      return 90;
    case 2:
      return 180;
    case 3:
    }
    return 270;
  }

  private void notifyListeners()
  {
    ArrayList localArrayList = this.mListeners;
    monitorenter;
    int i = 0;
    try
    {
      int j = this.mListeners.size();
      while (i < j)
      {
        ((Listener)this.mListeners.get(i)).onOrientationCompensationChanged();
        ++i;
      }
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  private static int roundOrientation(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1)
    {
      j = 1;
      if (j != 0)
        label7: paramInt2 = 90 * ((paramInt1 + 45) / 90) % 360;
      return paramInt2;
    }
    int i = Math.abs(paramInt1 - paramInt2);
    if (Math.min(i, 360 - i) >= 50);
    for (int j = 1; ; j = 0)
      break label7:
  }

  private void updateCompensation()
  {
    if (this.mOrientation == -1);
    int i;
    do
    {
      return;
      i = (this.mOrientation + getDisplayRotation(this.mActivity)) % 360;
    }
    while (this.mOrientationCompensation == i);
    this.mOrientationCompensation = i;
    notifyListeners();
  }

  public void addListener(Listener paramListener)
  {
    synchronized (this.mListeners)
    {
      this.mListeners.add(paramListener);
      return;
    }
  }

  public int getCompensation()
  {
    return this.mOrientationCompensation;
  }

  public int getDisplayRotation()
  {
    return getDisplayRotation(this.mActivity);
  }

  public void lockOrientation()
  {
    int i = 1;
    if (this.mOrientationLocked)
      return;
    this.mOrientationLocked = i;
    int j = getDisplayRotation();
    int k;
    label29: Activity localActivity2;
    int l;
    if (j < 180)
    {
      k = i;
      if (this.mActivity.getResources().getConfiguration().orientation != 2)
        break label91;
      Log.d("OrientationManager", "lock orientation to landscape");
      localActivity2 = this.mActivity;
      l = 0;
      if (k == 0)
        break label84;
    }
    while (true)
    {
      localActivity2.setRequestedOrientation(l);
      label74: updateCompensation();
      return;
      k = 0;
      break label29:
      label84: l = 8;
    }
    if ((j == 90) || (j == 270))
    {
      label91: if (k != 0)
        break label137;
      k = i;
    }
    label110: Log.d("OrientationManager", "lock orientation to portrait");
    Activity localActivity1 = this.mActivity;
    if (k != 0);
    while (true)
    {
      localActivity1.setRequestedOrientation(i);
      break label74:
      label137: k = 0;
      break label110:
      i = 9;
    }
  }

  public void pause()
  {
    this.mOrientationListener.disable();
  }

  public void removeListener(Listener paramListener)
  {
    synchronized (this.mListeners)
    {
      this.mListeners.remove(paramListener);
      return;
    }
  }

  public void resume()
  {
    int i = 1;
    if (Settings.System.getInt(this.mActivity.getContentResolver(), "accelerometer_rotation", 0) != i);
    while (true)
    {
      this.mRotationLockedSetting = i;
      this.mOrientationListener.enable();
      return;
      i = 0;
    }
  }

  public void unlockOrientation()
  {
    if (!this.mOrientationLocked);
    do
      return;
    while (this.mRotationLockedSetting);
    this.mOrientationLocked = false;
    Log.d("OrientationManager", "unlock orientation");
    this.mActivity.setRequestedOrientation(-1);
    disableCompensation();
  }

  public static abstract interface Listener
  {
    public abstract void onOrientationCompensationChanged();
  }

  private class MyOrientationEventListener extends OrientationEventListener
  {
    public MyOrientationEventListener(Context arg2)
    {
      super(localContext);
    }

    public void onOrientationChanged(int paramInt)
    {
      if (paramInt == -1);
      do
      {
        return;
        OrientationManager.access$002(OrientationManager.this, OrientationManager.access$100(paramInt, OrientationManager.this.mOrientation));
      }
      while (!OrientationManager.this.mOrientationLocked);
      OrientationManager.this.updateCompensation();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.OrientationManager
 * JD-Core Version:    0.5.4
 */