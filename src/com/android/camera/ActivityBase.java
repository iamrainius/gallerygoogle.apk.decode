package com.android.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import com.android.camera.ui.LayoutChangeNotifier.Listener;
import com.android.camera.ui.PopupManager;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.app.AppBridge;
import com.android.gallery3d.app.AppBridge.Server;
import com.android.gallery3d.app.GalleryActionBar;
import com.android.gallery3d.app.PhotoPage;
import com.android.gallery3d.app.StateManager;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.ScreenNail;
import com.android.gallery3d.util.MediaSetUtils;

public abstract class ActivityBase extends AbstractGalleryActivity
  implements LayoutChangeNotifier.Listener
{
  private static boolean sFirstStartAfterScreenOn = true;
  private static BroadcastReceiver sScreenOffReceiver;
  protected static int sSecureAlbumId;
  protected GalleryActionBar mActionBar;
  protected MyAppBridge mAppBridge;
  protected View mCameraAppView;
  private Animation mCameraAppViewFadeIn;
  private Animation mCameraAppViewFadeOut;
  protected CameraManager.CameraProxy mCameraDevice;
  protected boolean mCameraDisabled;
  protected ScreenNail mCameraScreenNail;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 0:
      }
      ActivityBase.this.updateStorageHint();
    }
  };
  protected int mNumberOfCameras;
  protected boolean mOpenCameraFail;
  protected boolean mPaused;
  protected int mPendingSwitchCameraId = -1;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      String str = paramIntent.getAction();
      if ((!str.equals("android.intent.action.MEDIA_MOUNTED")) && (!str.equals("android.intent.action.MEDIA_UNMOUNTED")) && (!str.equals("android.intent.action.MEDIA_CHECKING")) && (!str.equals("android.intent.action.MEDIA_SCANNER_FINISHED")))
        return;
      ActivityBase.this.updateStorageSpaceAndHint();
    }
  };
  private int mResultCodeForTesting;
  private Intent mResultDataForTesting;
  private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      ActivityBase.this.finish();
    }
  };
  protected boolean mSecureCamera;
  protected boolean mShowCameraAppView = true;
  private View mSingleTapArea;
  private OnScreenHint mStorageHint;
  private long mStorageSpace = 50000000L;

  public static boolean isFirstStartAfterScreenOn()
  {
    return sFirstStartAfterScreenOn;
  }

  private boolean onSingleTapUp(int paramInt1, int paramInt2)
  {
    if ((this.mSingleTapArea == null) || (!this.mShowCameraAppView))
      return false;
    int[] arrayOfInt = Util.getRelativeLocation((View)getGLRoot(), this.mSingleTapArea);
    int i = paramInt1 - arrayOfInt[0];
    int j = paramInt2 - arrayOfInt[1];
    if ((i >= 0) && (i < this.mSingleTapArea.getWidth()) && (j >= 0) && (j < this.mSingleTapArea.getHeight()))
    {
      onSingleTapUp(this.mSingleTapArea, i, j);
      return true;
    }
    return false;
  }

  public static void resetFirstStartAfterScreenOn()
  {
    sFirstStartAfterScreenOn = false;
  }

  protected void addSecureAlbumItemIfNeeded(boolean paramBoolean, Uri paramUri)
  {
    if (!this.mSecureCamera)
      return;
    int i = Integer.parseInt(paramUri.getLastPathSegment());
    this.mAppBridge.addSecureAlbumItem(paramBoolean, i);
  }

  public ScreenNail createCameraScreenNail(boolean paramBoolean)
  {
    this.mCameraAppView = findViewById(2131558532);
    Bundle localBundle = new Bundle();
    String str;
    if (paramBoolean)
      if (this.mSecureCamera)
      {
        str = "/secure/all/" + sSecureAlbumId;
        label51: localBundle.putString("media-set-path", str);
        localBundle.putString("media-item-path", str);
        localBundle.putBoolean("show_when_locked", this.mSecureCamera);
        if (this.mAppBridge != null)
          this.mCameraScreenNail.recycle();
        this.mAppBridge = new MyAppBridge();
        localBundle.putParcelable("app-bridge", this.mAppBridge);
        if (getStateManager().getStateCount() != 0)
          break label180;
        getStateManager().startState(PhotoPage.class, localBundle);
      }
    while (true)
    {
      this.mCameraScreenNail = this.mAppBridge.getCameraScreenNail();
      return this.mCameraScreenNail;
      str = "/local/all/" + MediaSetUtils.CAMERA_BUCKET_ID;
      break label51:
      str = "/local/all/0";
      break label51:
      label180: getStateManager().switchState(getStateManager().getTopState(), PhotoPage.class, localBundle);
    }
  }

  public GalleryActionBar getGalleryActionBar()
  {
    return this.mActionBar;
  }

  public int getResultCode()
  {
    return this.mResultCodeForTesting;
  }

  public Intent getResultData()
  {
    return this.mResultDataForTesting;
  }

  protected long getStorageSpace()
  {
    return this.mStorageSpace;
  }

  protected void installIntentFilter()
  {
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.MEDIA_MOUNTED");
    localIntentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
    localIntentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
    localIntentFilter.addAction("android.intent.action.MEDIA_CHECKING");
    localIntentFilter.addDataScheme("file");
    registerReceiver(this.mReceiver, localIntentFilter);
  }

  public boolean isPanoramaActivity()
  {
    return false;
  }

  public boolean isSecureCamera()
  {
    return this.mSecureCamera;
  }

  public void notifyScreenNailChanged()
  {
    this.mAppBridge.notifyScreenNailChanged();
  }

  protected void onCaptureTextureCopied()
  {
  }

  public void onCreate(Bundle paramBundle)
  {
    super.disableToggleStatusBar();
    setTheme(2131492931);
    getWindow().addFlags(1024);
    label34: Intent localIntent;
    String str;
    if (ApiHelper.HAS_ACTION_BAR)
    {
      requestWindowFeature(9);
      localIntent = getIntent();
      str = localIntent.getAction();
      if (!"android.media.action.STILL_IMAGE_CAMERA_SECURE".equals(str))
        break label144;
      this.mSecureCamera = true;
      sSecureAlbumId = 1 + sSecureAlbumId;
    }
    while (true)
    {
      if (this.mSecureCamera)
      {
        IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(this.mScreenOffReceiver, localIntentFilter);
        if (sScreenOffReceiver == null)
        {
          sScreenOffReceiver = new ScreenOffReceiver(null);
          getApplicationContext().registerReceiver(sScreenOffReceiver, localIntentFilter);
        }
      }
      super.onCreate(paramBundle);
      return;
      requestWindowFeature(1);
      break label34:
      if ("android.media.action.IMAGE_CAPTURE_SECURE".equals(str))
        label144: this.mSecureCamera = true;
      this.mSecureCamera = localIntent.getBooleanExtra("secure_camera", false);
    }
  }

  protected void onDestroy()
  {
    PopupManager.removeInstance(this);
    if (this.mSecureCamera)
      unregisterReceiver(this.mScreenOffReceiver);
    super.onDestroy();
  }

  protected void onFullScreenChanged(boolean paramBoolean)
  {
    if (this.mShowCameraAppView == paramBoolean);
    do
    {
      return;
      this.mShowCameraAppView = paramBoolean;
    }
    while ((this.mPaused) || (isFinishing()));
    updateCameraAppView();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((((paramInt == 84) || (paramInt == 82))) && (paramKeyEvent.isLongPress()));
    do
      return true;
    while ((paramInt == 82) && (this.mShowCameraAppView));
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 82) && (this.mShowCameraAppView))
      return true;
    return super.onKeyUp(paramInt, paramKeyEvent);
  }

  public void onLayoutChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mAppBridge == null);
    int i;
    int j;
    do
    {
      return;
      i = paramInt3 - paramInt1;
      j = paramInt4 - paramInt2;
    }
    while (!ApiHelper.HAS_SURFACE_TEXTURE);
    CameraScreenNail localCameraScreenNail = (CameraScreenNail)this.mCameraScreenNail;
    if (Util.getDisplayRotation(this) % 180 == 0)
      localCameraScreenNail.setPreviewFrameLayoutSize(i, j);
    while (true)
    {
      notifyScreenNailChanged();
      return;
      localCameraScreenNail.setPreviewFrameLayoutSize(j, i);
    }
  }

  protected void onPause()
  {
    super.onPause();
    if (this.mStorageHint != null)
    {
      this.mStorageHint.cancel();
      this.mStorageHint = null;
    }
    unregisterReceiver(this.mReceiver);
  }

  protected void onPreviewTextureCopied()
  {
  }

  protected void onResume()
  {
    super.onResume();
    installIntentFilter();
    if (!updateStorageHintOnResume())
      return;
    updateStorageSpace();
    this.mHandler.sendEmptyMessageDelayed(0, 200L);
  }

  public boolean onSearchRequested()
  {
    return false;
  }

  protected void onSingleTapUp(View paramView, int paramInt1, int paramInt2)
  {
  }

  protected ScreenNail reuseCameraScreenNail(boolean paramBoolean)
  {
    this.mCameraAppView = findViewById(2131558532);
    Bundle localBundle = new Bundle();
    if (paramBoolean)
      if (!this.mSecureCamera);
    for (String str = "/secure/all/" + sSecureAlbumId; ; str = "/local/all/0")
      while (true)
      {
        localBundle.putString("media-set-path", str);
        localBundle.putString("media-item-path", str);
        localBundle.putBoolean("show_when_locked", this.mSecureCamera);
        if (this.mAppBridge == null)
          this.mAppBridge = new MyAppBridge();
        localBundle.putParcelable("app-bridge", this.mAppBridge);
        if (getStateManager().getStateCount() == 0)
          getStateManager().startState(PhotoPage.class, localBundle);
        this.mCameraScreenNail = this.mAppBridge.getCameraScreenNail();
        return this.mCameraScreenNail;
        str = "/local/all/" + MediaSetUtils.CAMERA_BUCKET_ID;
      }
  }

  public void setContentView(int paramInt)
  {
    super.setContentView(paramInt);
    this.mActionBar = new GalleryActionBar(this);
    this.mActionBar.hide();
  }

  protected void setResultEx(int paramInt)
  {
    this.mResultCodeForTesting = paramInt;
    setResult(paramInt);
  }

  protected void setResultEx(int paramInt, Intent paramIntent)
  {
    this.mResultCodeForTesting = paramInt;
    this.mResultDataForTesting = paramIntent;
    setResult(paramInt, paramIntent);
  }

  protected void setSingleTapUpListener(View paramView)
  {
    this.mSingleTapArea = paramView;
  }

  public void setSwipingEnabled(boolean paramBoolean)
  {
    this.mAppBridge.setSwipingEnabled(paramBoolean);
  }

  protected void updateCameraAppView()
  {
    if (this.mCameraAppViewFadeIn == null)
    {
      this.mCameraAppViewFadeIn = new AlphaAnimation(0.0F, 1.0F);
      this.mCameraAppViewFadeIn.setDuration(100L);
      this.mCameraAppViewFadeIn.setInterpolator(new DecelerateInterpolator());
      this.mCameraAppViewFadeOut = new AlphaAnimation(1.0F, 0.0F);
      this.mCameraAppViewFadeOut.setDuration(100L);
      this.mCameraAppViewFadeOut.setInterpolator(new DecelerateInterpolator());
      this.mCameraAppViewFadeOut.setAnimationListener(new HideCameraAppView(null));
    }
    if (this.mShowCameraAppView)
    {
      this.mCameraAppView.setVisibility(0);
      this.mCameraAppView.requestLayout();
      this.mCameraAppView.startAnimation(this.mCameraAppViewFadeIn);
      return;
    }
    this.mCameraAppView.startAnimation(this.mCameraAppViewFadeOut);
  }

  protected void updateStorageHint()
  {
    updateStorageHint(this.mStorageSpace);
  }

  protected void updateStorageHint(long paramLong)
  {
    String str;
    if (paramLong == -1L)
    {
      str = getString(2131361895);
      label17: if (str == null)
        break label124;
      if (this.mStorageHint != null)
        break label112;
      this.mStorageHint = OnScreenHint.makeText(this, str);
      label39: this.mStorageHint.show();
    }
    do
    {
      return;
      if (paramLong == -2L)
        str = getString(2131361896);
      if (paramLong == -3L)
        str = getString(2131361897);
      boolean bool = paramLong < 50000000L;
      str = null;
      if (!bool);
      str = getString(2131361962);
      break label17:
      label112: this.mStorageHint.setText(str);
      label124: break label39:
    }
    while (this.mStorageHint == null);
    this.mStorageHint.cancel();
    this.mStorageHint = null;
  }

  protected boolean updateStorageHintOnResume()
  {
    return true;
  }

  protected void updateStorageSpace()
  {
    this.mStorageSpace = Storage.getAvailableSpace();
  }

  protected void updateStorageSpaceAndHint()
  {
    updateStorageSpace();
    updateStorageHint(this.mStorageSpace);
  }

  private class HideCameraAppView
    implements Animation.AnimationListener
  {
    private HideCameraAppView()
    {
    }

    public void onAnimationEnd(Animation paramAnimation)
    {
      ActivityBase.this.mCameraAppView.setVisibility(4);
    }

    public void onAnimationRepeat(Animation paramAnimation)
    {
    }

    public void onAnimationStart(Animation paramAnimation)
    {
    }
  }

  class MyAppBridge extends AppBridge
    implements CameraScreenNail.Listener
  {
    private ScreenNail mCameraScreenNail;
    private AppBridge.Server mServer;

    MyAppBridge()
    {
    }

    private void notifyScreenNailChanged()
    {
      if (this.mServer == null)
        return;
      this.mServer.notifyScreenNailChanged();
    }

    private void setSwipingEnabled(boolean paramBoolean)
    {
      if (this.mServer == null)
        return;
      this.mServer.setSwipingEnabled(paramBoolean);
    }

    public void addSecureAlbumItem(boolean paramBoolean, int paramInt)
    {
      if (this.mServer == null)
        return;
      this.mServer.addSecureAlbumItem(paramBoolean, paramInt);
    }

    public ScreenNail attachScreenNail()
    {
      if (this.mCameraScreenNail == null)
        if (!ApiHelper.HAS_SURFACE_TEXTURE)
          break label30;
      label30: for (this.mCameraScreenNail = new CameraScreenNail(this); ; this.mCameraScreenNail = new StaticBitmapScreenNail(BitmapFactory.decodeResource(ActivityBase.this.getResources(), 2130837904)))
        return this.mCameraScreenNail;
    }

    public void detachScreenNail()
    {
      this.mCameraScreenNail = null;
    }

    public ScreenNail getCameraScreenNail()
    {
      return this.mCameraScreenNail;
    }

    public boolean isPanorama()
    {
      return ActivityBase.this.isPanoramaActivity();
    }

    public boolean isStaticCamera()
    {
      return !ApiHelper.HAS_SURFACE_TEXTURE;
    }

    public void onCaptureTextureCopied()
    {
      ActivityBase.this.onCaptureTextureCopied();
    }

    public void onFullScreenChanged(boolean paramBoolean)
    {
      ActivityBase.this.onFullScreenChanged(paramBoolean);
    }

    public void onPreviewTextureCopied()
    {
      ActivityBase.this.onPreviewTextureCopied();
    }

    public boolean onSingleTapUp(int paramInt1, int paramInt2)
    {
      return ActivityBase.this.onSingleTapUp(paramInt1, paramInt2);
    }

    public void requestRender()
    {
      ActivityBase.this.getGLRoot().requestRenderForced();
    }

    public void setServer(AppBridge.Server paramServer)
    {
      this.mServer = paramServer;
    }
  }

  private static class ScreenOffReceiver extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      ActivityBase.access$002(true);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ActivityBase
 * JD-Core Version:    0.5.4
 */