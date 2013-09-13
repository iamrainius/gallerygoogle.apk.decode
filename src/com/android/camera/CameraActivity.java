package com.android.camera;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.camera.ui.CameraSwitcher;
import com.android.camera.ui.CameraSwitcher.CameraSwitchListener;
import com.android.gallery3d.app.StateManager;
import com.android.gallery3d.util.LightCycleHelper;

public class CameraActivity extends ActivityBase
  implements CameraSwitcher.CameraSwitchListener
{
  private static final int[] DRAW_IDS = { 2130837751, 2130837758, 2130837752, 2130837757 };
  private View mControlsBackground;
  CameraModule mCurrentModule;
  private int mCurrentModuleIndex;
  private MotionEvent mDown;
  private Drawable[] mDrawables;
  private FrameLayout mFrame;
  private int mOrientation = -1;
  private int mOrientationCompensation = 0;
  private MyOrientationEventListener mOrientationListener;
  private ShutterButton mShutter;
  private View mShutterSwitcher;
  private CameraSwitcher mSwitcher;

  private boolean canReuseScreenNail()
  {
    return (this.mCurrentModuleIndex == 0) || (this.mCurrentModuleIndex == 1);
  }

  private void closeModule(CameraModule paramCameraModule)
  {
    paramCameraModule.onPauseBeforeSuper();
    paramCameraModule.onPauseAfterSuper();
    this.mFrame.removeAllViews();
  }

  private void openModule(CameraModule paramCameraModule, boolean paramBoolean)
  {
    FrameLayout localFrameLayout = this.mFrame;
    if ((paramBoolean) && (canReuseScreenNail()));
    for (boolean bool = true; ; bool = false)
    {
      paramCameraModule.init(this, localFrameLayout, bool);
      this.mPaused = false;
      paramCameraModule.onResumeBeforeSuper();
      paramCameraModule.onResumeAfterSuper();
      return;
    }
  }

  public void cancelActivityTouchHandling()
  {
    if (this.mDown == null)
      return;
    MotionEvent localMotionEvent = MotionEvent.obtain(this.mDown);
    localMotionEvent.setAction(3);
    super.dispatchTouchEvent(localMotionEvent);
  }

  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionMasked() == 0)
      this.mDown = paramMotionEvent;
    if ((this.mSwitcher != null) && (this.mSwitcher.showsPopup()) && (!this.mSwitcher.isInsidePopup(paramMotionEvent)))
      return this.mSwitcher.onTouch(null, paramMotionEvent);
    return (this.mShutterSwitcher.dispatchTouchEvent(paramMotionEvent)) || (this.mCurrentModule.dispatchTouchEvent(paramMotionEvent));
  }

  public long getAutoFocusTime()
  {
    if (this.mCurrentModule instanceof PhotoModule)
      return ((PhotoModule)this.mCurrentModule).mAutoFocusTime;
    return -1L;
  }

  public CameraScreenNail getCameraScreenNail()
  {
    return (CameraScreenNail)this.mCameraScreenNail;
  }

  public long getCaptureStartTime()
  {
    if (this.mCurrentModule instanceof PhotoModule)
      return ((PhotoModule)this.mCurrentModule).mCaptureStartTime;
    return -1L;
  }

  public long getJpegCallbackFinishTime()
  {
    if (this.mCurrentModule instanceof PhotoModule)
      return ((PhotoModule)this.mCurrentModule).mJpegCallbackFinishTime;
    return -1L;
  }

  public long getPictureDisplayedToJpegCallbackTime()
  {
    if (this.mCurrentModule instanceof PhotoModule)
      return ((PhotoModule)this.mCurrentModule).mPictureDisplayedToJpegCallbackTime;
    return -1L;
  }

  public ShutterButton getShutterButton()
  {
    return this.mShutter;
  }

  public long getShutterLag()
  {
    if (this.mCurrentModule instanceof PhotoModule)
      return ((PhotoModule)this.mCurrentModule).mShutterLag;
    return -1L;
  }

  public long getShutterToPictureDisplayedTime()
  {
    if (this.mCurrentModule instanceof PhotoModule)
      return ((PhotoModule)this.mCurrentModule).mShutterToPictureDisplayedTime;
    return -1L;
  }

  public void hideSwitcher()
  {
    this.mSwitcher.closePopup();
    this.mSwitcher.setVisibility(4);
  }

  public void hideUI()
  {
    this.mControlsBackground.setVisibility(4);
    hideSwitcher();
    this.mShutter.setVisibility(8);
  }

  public void init()
  {
    this.mControlsBackground = findViewById(2131558417);
    this.mShutterSwitcher = findViewById(2131558416);
    this.mShutter = ((ShutterButton)findViewById(2131558418));
    this.mSwitcher = ((CameraSwitcher)findViewById(2131558419));
    this.mSwitcher.setDrawIds(DRAW_IDS);
    int i;
    label68: int[] arrayOfInt;
    int j;
    int k;
    if (LightCycleHelper.hasLightCycleCapture(this))
    {
      i = DRAW_IDS.length;
      arrayOfInt = new int[i];
      j = 0;
      k = 0;
      label77: if (k >= this.mDrawables.length)
        break label136;
      if ((k != 3) || (LightCycleHelper.hasLightCycleCapture(this)))
        break label116;
    }
    while (true)
    {
      ++k;
      break label77:
      i = -1 + DRAW_IDS.length;
      break label68:
      label116: int l = j + 1;
      arrayOfInt[j] = DRAW_IDS[k];
      j = l;
    }
    label136: this.mSwitcher.setDrawIds(arrayOfInt);
    this.mSwitcher.setSwitchListener(this);
    this.mSwitcher.setCurrentIndex(this.mCurrentModuleIndex);
  }

  protected void installIntentFilter()
  {
    super.installIntentFilter();
    this.mCurrentModule.installIntentFilter();
  }

  public boolean isInCameraApp()
  {
    return this.mShowCameraAppView;
  }

  public boolean isPanoramaActivity()
  {
    return this.mCurrentModuleIndex == 2;
  }

  public boolean isRecording()
  {
    if (this.mCurrentModule instanceof VideoModule)
      return ((VideoModule)this.mCurrentModule).isRecording();
    return false;
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    this.mCurrentModule.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onBackPressed()
  {
    if (this.mCurrentModule.onBackPressed())
      return;
    super.onBackPressed();
  }

  public void onCameraSelected(int paramInt)
  {
    if (this.mPaused);
    do
      return;
    while (paramInt == this.mCurrentModuleIndex);
    this.mPaused = true;
    boolean bool = canReuseScreenNail();
    CameraHolder.instance().keep();
    closeModule(this.mCurrentModule);
    this.mCurrentModuleIndex = paramInt;
    switch (paramInt)
    {
    default:
    case 1:
    case 0:
    case 2:
    case 3:
    }
    while (true)
    {
      openModule(this.mCurrentModule, bool);
      this.mCurrentModule.onOrientationChanged(this.mOrientation);
      return;
      this.mCurrentModule = new VideoModule();
      continue;
      this.mCurrentModule = new PhotoModule();
      continue;
      this.mCurrentModule = new PanoramaModule();
      continue;
      this.mCurrentModule = LightCycleHelper.createPanoramaModule();
    }
  }

  public void onCaptureTextureCopied()
  {
    this.mCurrentModule.onCaptureTextureCopied();
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    ViewGroup localViewGroup = (ViewGroup)findViewById(2131558408);
    localViewGroup.removeView(findViewById(2131558416));
    getLayoutInflater().inflate(2130968587, localViewGroup);
    init();
    if (this.mShowCameraAppView)
      showUI();
    while (true)
    {
      this.mCurrentModule.onConfigurationChanged(paramConfiguration);
      return;
      hideUI();
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968586);
    this.mFrame = ((FrameLayout)findViewById(2131558415));
    this.mDrawables = new Drawable[DRAW_IDS.length];
    for (int i = 0; i < DRAW_IDS.length; ++i)
      this.mDrawables[i] = getResources().getDrawable(DRAW_IDS[i]);
    init();
    if (("android.media.action.VIDEO_CAMERA".equals(getIntent().getAction())) || ("android.media.action.VIDEO_CAPTURE".equals(getIntent().getAction())))
      this.mCurrentModule = new VideoModule();
    for (this.mCurrentModuleIndex = 1; ; this.mCurrentModuleIndex = 0)
    {
      this.mCurrentModule.init(this, this.mFrame, true);
      this.mSwitcher.setCurrentIndex(this.mCurrentModuleIndex);
      this.mOrientationListener = new MyOrientationEventListener(this);
      return;
      this.mCurrentModule = new PhotoModule();
    }
  }

  protected void onFullScreenChanged(boolean paramBoolean)
  {
    if (paramBoolean)
      showUI();
    while (true)
    {
      super.onFullScreenChanged(paramBoolean);
      this.mCurrentModule.onFullScreenChanged(paramBoolean);
      return;
      hideUI();
    }
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return (this.mCurrentModule.onKeyDown(paramInt, paramKeyEvent)) || (super.onKeyDown(paramInt, paramKeyEvent));
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return (this.mCurrentModule.onKeyUp(paramInt, paramKeyEvent)) || (super.onKeyUp(paramInt, paramKeyEvent));
  }

  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    getStateManager().clearActivityResult();
  }

  public void onPause()
  {
    this.mPaused = true;
    this.mOrientationListener.disable();
    this.mCurrentModule.onPauseBeforeSuper();
    super.onPause();
    this.mCurrentModule.onPauseAfterSuper();
  }

  public void onPreviewTextureCopied()
  {
    this.mCurrentModule.onPreviewTextureCopied();
  }

  public void onResume()
  {
    this.mPaused = false;
    this.mOrientationListener.enable();
    this.mCurrentModule.onResumeBeforeSuper();
    super.onResume();
    this.mCurrentModule.onResumeAfterSuper();
  }

  public void onShowSwitcherPopup()
  {
    this.mCurrentModule.onShowSwitcherPopup();
  }

  protected void onSingleTapUp(View paramView, int paramInt1, int paramInt2)
  {
    this.mCurrentModule.onSingleTapUp(paramView, paramInt1, paramInt2);
  }

  protected void onStop()
  {
    super.onStop();
    this.mCurrentModule.onStop();
    getStateManager().clearTasks();
  }

  public void onUserInteraction()
  {
    super.onUserInteraction();
    this.mCurrentModule.onUserInteraction();
  }

  public void showSwitcher()
  {
    if (!this.mCurrentModule.needsSwitcher())
      return;
    this.mSwitcher.setVisibility(0);
  }

  public void showUI()
  {
    this.mControlsBackground.setVisibility(0);
    showSwitcher();
    this.mShutter.setVisibility(0);
  }

  public boolean superDispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    return super.dispatchTouchEvent(paramMotionEvent);
  }

  public void updateCameraAppView()
  {
    super.updateCameraAppView();
    this.mCurrentModule.updateCameraAppView();
  }

  protected boolean updateStorageHintOnResume()
  {
    return this.mCurrentModule.updateStorageHintOnResume();
  }

  private class MyOrientationEventListener extends OrientationEventListener
  {
    public MyOrientationEventListener(Context arg2)
    {
      super(localContext);
    }

    public void onOrientationChanged(int paramInt)
    {
      if (paramInt == -1)
        return;
      CameraActivity.access$002(CameraActivity.this, Util.roundOrientation(paramInt, CameraActivity.this.mOrientation));
      int i = (CameraActivity.this.mOrientation + Util.getDisplayRotation(CameraActivity.this)) % 360;
      if (CameraActivity.this.mOrientationCompensation != i)
        CameraActivity.access$102(CameraActivity.this, i);
      CameraActivity.this.mCurrentModule.onOrientationChanged(paramInt);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraActivity
 * JD-Core Version:    0.5.4
 */