package com.android.gallery3d.app;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.android.gallery3d.anim.StateTransitionAnimation;
import com.android.gallery3d.anim.StateTransitionAnimation.Transition;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.PreparePageFadeoutTexture;
import com.android.gallery3d.ui.RawTexture;
import com.android.gallery3d.util.GalleryUtils;

public abstract class ActivityState
{
  protected AbstractGalleryActivity mActivity;
  protected float[] mBackgroundColor;
  private GLView mContentPane;
  private ContentResolver mContentResolver;
  protected Bundle mData;
  private boolean mDestroyed = false;
  protected int mFlags;
  protected boolean mHapticsEnabled;
  private StateTransitionAnimation mIntroAnimation;
  boolean mIsFinishing = false;
  private StateTransitionAnimation.Transition mNextTransition = StateTransitionAnimation.Transition.None;
  private boolean mPlugged = false;
  BroadcastReceiver mPowerIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (!"android.intent.action.BATTERY_CHANGED".equals(paramIntent.getAction()))
        return;
      int i = paramIntent.getIntExtra("plugged", 0);
      boolean bool = false;
      if (i != 0)
        bool = true;
      if (bool == ActivityState.this.mPlugged)
        return;
      ActivityState.access$002(ActivityState.this, bool);
      ActivityState.this.setScreenFlags();
    }
  };
  protected ResultEntry mReceivedResults;
  protected ResultEntry mResult;

  private void setScreenFlags()
  {
    Window localWindow = this.mActivity.getWindow();
    WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
    if (((0x8 & this.mFlags) != 0) || ((this.mPlugged) && ((0x4 & this.mFlags) != 0)))
    {
      localLayoutParams.flags = (0x80 | localLayoutParams.flags);
      label51: if ((0x10 & this.mFlags) == 0)
        break label113;
      localLayoutParams.flags = (0x1 | localLayoutParams.flags);
      label71: if ((0x20 & this.mFlags) == 0)
        break label127;
    }
    for (localLayoutParams.flags = (0x80000 | localLayoutParams.flags); ; localLayoutParams.flags = (0xFFF7FFFF & localLayoutParams.flags))
    {
      localWindow.setAttributes(localLayoutParams);
      return;
      localLayoutParams.flags = (0xFFFFFF7F & localLayoutParams.flags);
      break label51:
      label113: localLayoutParams.flags = (0xFFFFFFFE & localLayoutParams.flags);
      label127: break label71:
    }
  }

  protected void clearStateResult()
  {
  }

  protected float[] getBackgroundColor()
  {
    return this.mBackgroundColor;
  }

  protected int getBackgroundColorId()
  {
    return 2131296278;
  }

  public Bundle getData()
  {
    return this.mData;
  }

  protected MenuInflater getSupportMenuInflater()
  {
    return this.mActivity.getMenuInflater();
  }

  void initialize(AbstractGalleryActivity paramAbstractGalleryActivity, Bundle paramBundle)
  {
    this.mActivity = paramAbstractGalleryActivity;
    this.mData = paramBundle;
    this.mContentResolver = paramAbstractGalleryActivity.getAndroidContext().getContentResolver();
  }

  boolean isDestroyed()
  {
    return this.mDestroyed;
  }

  protected void onBackPressed()
  {
    this.mActivity.getStateManager().finishState(this);
  }

  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
  }

  protected void onCreate(Bundle paramBundle1, Bundle paramBundle2)
  {
    this.mBackgroundColor = GalleryUtils.intColorToFloatARGBArray(this.mActivity.getResources().getColor(getBackgroundColorId()));
  }

  protected boolean onCreateActionBar(Menu paramMenu)
  {
    return true;
  }

  protected void onDestroy()
  {
    this.mDestroyed = true;
  }

  protected boolean onItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }

  protected void onPause()
  {
    if ((0x4 & this.mFlags) != 0)
      this.mActivity.unregisterReceiver(this.mPowerIntentReceiver);
    if (this.mNextTransition == StateTransitionAnimation.Transition.None)
      return;
    this.mActivity.getTransitionStore().put("transition-in", this.mNextTransition);
    PreparePageFadeoutTexture.prepareFadeOutTexture(this.mActivity, this.mContentPane);
    this.mNextTransition = StateTransitionAnimation.Transition.None;
  }

  protected void onResume()
  {
    RawTexture localRawTexture = (RawTexture)this.mActivity.getTransitionStore().get("fade_texture");
    this.mNextTransition = ((StateTransitionAnimation.Transition)this.mActivity.getTransitionStore().get("transition-in", StateTransitionAnimation.Transition.None));
    if (this.mNextTransition == StateTransitionAnimation.Transition.None)
      return;
    this.mIntroAnimation = new StateTransitionAnimation(this.mNextTransition, localRawTexture);
    this.mNextTransition = StateTransitionAnimation.Transition.None;
  }

  protected void onSaveState(Bundle paramBundle)
  {
  }

  protected void onStateResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
  }

  void resume()
  {
    int i = 1;
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    ActionBar localActionBar = localAbstractGalleryActivity.getActionBar();
    if (localActionBar != null)
    {
      if ((0x1 & this.mFlags) == 0)
        break label211;
      localActionBar.hide();
      label29: int l = this.mActivity.getStateManager().getStateCount();
      GalleryActionBar localGalleryActionBar = this.mActivity.getGalleryActionBar();
      if (l <= i)
        break label218;
      int i1 = i;
      label59: localGalleryActionBar.setDisplayOptions(i1, i);
      localActionBar.setNavigationMode(0);
    }
    localAbstractGalleryActivity.invalidateOptionsMenu();
    setScreenFlags();
    if ((0x2 & this.mFlags) != 0)
    {
      int j = i;
      this.mActivity.getGLRoot().setLightsOutMode(j);
      ResultEntry localResultEntry = this.mReceivedResults;
      if (localResultEntry != null)
      {
        this.mReceivedResults = null;
        onStateResult(localResultEntry.requestCode, localResultEntry.resultCode, localResultEntry.resultData);
      }
      if ((0x4 & this.mFlags) != 0)
      {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        localAbstractGalleryActivity.registerReceiver(this.mPowerIntentReceiver, localIntentFilter);
      }
    }
    while (true)
      try
      {
        if (Settings.System.getInt(this.mContentResolver, "haptic_feedback_enabled") != 0)
        {
          this.mHapticsEnabled = i;
          label196: onResume();
          this.mActivity.getTransitionStore().clear();
          return;
          label211: localActionBar.show();
          break label29:
          label218: int i2 = 0;
          break label59:
          int k = 0;
        }
        i = 0;
      }
      catch (Settings.SettingNotFoundException localSettingNotFoundException)
      {
        this.mHapticsEnabled = false;
        break label196:
      }
  }

  protected void setContentPane(GLView paramGLView)
  {
    this.mContentPane = paramGLView;
    if (this.mIntroAnimation != null)
    {
      this.mContentPane.setIntroAnimation(this.mIntroAnimation);
      this.mIntroAnimation = null;
    }
    this.mContentPane.setBackgroundColor(getBackgroundColor());
    this.mActivity.getGLRoot().setContentPane(this.mContentPane);
  }

  protected void setStateResult(int paramInt, Intent paramIntent)
  {
    if (this.mResult == null)
      return;
    this.mResult.resultCode = paramInt;
    this.mResult.resultData = paramIntent;
  }

  protected void transitionOnNextPause(Class<? extends ActivityState> paramClass1, Class<? extends ActivityState> paramClass2, StateTransitionAnimation.Transition paramTransition)
  {
    if ((paramClass1 == PhotoPage.class) && (paramClass2 == AlbumPage.class))
    {
      this.mNextTransition = StateTransitionAnimation.Transition.Outgoing;
      return;
    }
    if ((paramClass1 == AlbumPage.class) && (paramClass2 == PhotoPage.class))
    {
      this.mNextTransition = StateTransitionAnimation.Transition.PhotoIncoming;
      return;
    }
    this.mNextTransition = paramTransition;
  }

  protected static class ResultEntry
  {
    public int requestCode;
    public int resultCode = 0;
    public Intent resultData;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.ActivityState
 * JD-Core Version:    0.5.4
 */