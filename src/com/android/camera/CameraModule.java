package com.android.camera;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public abstract interface CameraModule
{
  public abstract boolean dispatchTouchEvent(MotionEvent paramMotionEvent);

  public abstract void init(CameraActivity paramCameraActivity, View paramView, boolean paramBoolean);

  public abstract void installIntentFilter();

  public abstract boolean needsSwitcher();

  public abstract void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent);

  public abstract boolean onBackPressed();

  public abstract void onCaptureTextureCopied();

  public abstract void onConfigurationChanged(Configuration paramConfiguration);

  public abstract void onFullScreenChanged(boolean paramBoolean);

  public abstract boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent);

  public abstract boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent);

  public abstract void onOrientationChanged(int paramInt);

  public abstract void onPauseAfterSuper();

  public abstract void onPauseBeforeSuper();

  public abstract void onPreviewTextureCopied();

  public abstract void onResumeAfterSuper();

  public abstract void onResumeBeforeSuper();

  public abstract void onShowSwitcherPopup();

  public abstract void onSingleTapUp(View paramView, int paramInt1, int paramInt2);

  public abstract void onStop();

  public abstract void onUserInteraction();

  public abstract void updateCameraAppView();

  public abstract boolean updateStorageHintOnResume();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraModule
 * JD-Core Version:    0.5.4
 */