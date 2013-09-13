package com.android.gallery3d.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.data.BitmapPool;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLRootView;
import com.android.gallery3d.util.LightCycleHelper.PanoramaViewHelper;
import com.android.gallery3d.util.ThreadPool;

public class AbstractGalleryActivity extends Activity
  implements GalleryContext
{
  private GalleryActionBar mActionBar;
  private AlertDialog mAlertDialog = null;
  private boolean mDisableToggleStatusBar;
  private GLRootView mGLRootView;
  private IntentFilter mMountFilter = new IntentFilter("android.intent.action.MEDIA_MOUNTED");
  private BroadcastReceiver mMountReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (AbstractGalleryActivity.this.getExternalCacheDir() == null)
        return;
      AbstractGalleryActivity.this.onStorageReady();
    }
  };
  private OrientationManager mOrientationManager;
  private LightCycleHelper.PanoramaViewHelper mPanoramaViewHelper;
  private StateManager mStateManager;
  private TransitionStore mTransitionStore = new TransitionStore();

  private static void clearBitmapPool(BitmapPool paramBitmapPool)
  {
    if (paramBitmapPool == null)
      return;
    paramBitmapPool.clear();
  }

  @TargetApi(11)
  private static void setAlertDialogIconAttribute(AlertDialog.Builder paramBuilder)
  {
    paramBuilder.setIconAttribute(16843605);
  }

  private void toggleStatusBarByOrientation()
  {
    if (this.mDisableToggleStatusBar)
      return;
    Window localWindow = getWindow();
    if (getResources().getConfiguration().orientation == 1)
    {
      localWindow.clearFlags(1024);
      return;
    }
    localWindow.addFlags(1024);
  }

  protected void disableToggleStatusBar()
  {
    this.mDisableToggleStatusBar = true;
  }

  public Context getAndroidContext()
  {
    return this;
  }

  public DataManager getDataManager()
  {
    return ((GalleryApp)getApplication()).getDataManager();
  }

  public GLRoot getGLRoot()
  {
    return this.mGLRootView;
  }

  public GalleryActionBar getGalleryActionBar()
  {
    if (this.mActionBar == null)
      this.mActionBar = new GalleryActionBar(this);
    return this.mActionBar;
  }

  public OrientationManager getOrientationManager()
  {
    return this.mOrientationManager;
  }

  public LightCycleHelper.PanoramaViewHelper getPanoramaViewHelper()
  {
    return this.mPanoramaViewHelper;
  }

  public StateManager getStateManager()
  {
    monitorenter;
    try
    {
      if (this.mStateManager == null)
        this.mStateManager = new StateManager(this);
      StateManager localStateManager = this.mStateManager;
      return localStateManager;
    }
    finally
    {
      monitorexit;
    }
  }

  public ThreadPool getThreadPool()
  {
    return ((GalleryApp)getApplication()).getThreadPool();
  }

  public TransitionStore getTransitionStore()
  {
    return this.mTransitionStore;
  }

  protected boolean isFullscreen()
  {
    return (0x400 & getWindow().getAttributes().flags) != 0;
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mGLRootView.lockRenderThread();
    try
    {
      getStateManager().notifyActivityResult(paramInt1, paramInt2, paramIntent);
      return;
    }
    finally
    {
      this.mGLRootView.unlockRenderThread();
    }
  }

  public void onBackPressed()
  {
    GLRoot localGLRoot = getGLRoot();
    localGLRoot.lockRenderThread();
    try
    {
      getStateManager().onBackPressed();
      return;
    }
    finally
    {
      localGLRoot.unlockRenderThread();
    }
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mStateManager.onConfigurationChange(paramConfiguration);
    getGalleryActionBar().onConfigurationChanged();
    invalidateOptionsMenu();
    toggleStatusBarByOrientation();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mOrientationManager = new OrientationManager(this);
    toggleStatusBarByOrientation();
    getWindow().setBackgroundDrawable(null);
    this.mPanoramaViewHelper = new LightCycleHelper.PanoramaViewHelper(this);
    this.mPanoramaViewHelper.onCreate();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    return getStateManager().createOptionsMenu(paramMenu);
  }

  protected void onDestroy()
  {
    super.onDestroy();
    this.mGLRootView.lockRenderThread();
    try
    {
      getStateManager().destroy();
      return;
    }
    finally
    {
      this.mGLRootView.unlockRenderThread();
    }
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    GLRoot localGLRoot = getGLRoot();
    localGLRoot.lockRenderThread();
    try
    {
      boolean bool = getStateManager().itemSelected(paramMenuItem);
      return bool;
    }
    finally
    {
      localGLRoot.unlockRenderThread();
    }
  }

  protected void onPause()
  {
    super.onPause();
    this.mOrientationManager.pause();
    this.mGLRootView.onPause();
    this.mGLRootView.lockRenderThread();
    try
    {
      getStateManager().pause();
      getDataManager().pause();
      this.mGLRootView.unlockRenderThread();
      clearBitmapPool(MediaItem.getMicroThumbPool());
      clearBitmapPool(MediaItem.getThumbPool());
      return;
    }
    finally
    {
      this.mGLRootView.unlockRenderThread();
    }
  }

  protected void onResume()
  {
    super.onResume();
    this.mGLRootView.lockRenderThread();
    try
    {
      getStateManager().resume();
      getDataManager().resume();
      this.mGLRootView.unlockRenderThread();
      this.mGLRootView.onResume();
      return;
    }
    finally
    {
      this.mGLRootView.unlockRenderThread();
    }
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    this.mGLRootView.lockRenderThread();
    try
    {
      super.onSaveInstanceState(paramBundle);
      getStateManager().saveState(paramBundle);
      return;
    }
    finally
    {
      this.mGLRootView.unlockRenderThread();
    }
  }

  protected void onStart()
  {
    super.onStart();
    AlertDialog.Builder localBuilder;
    if (getExternalCacheDir() == null)
    {
      2 local2 = new DialogInterface.OnCancelListener()
      {
        public void onCancel(DialogInterface paramDialogInterface)
        {
          AbstractGalleryActivity.this.finish();
        }
      };
      3 local3 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          paramDialogInterface.cancel();
        }
      };
      localBuilder = new AlertDialog.Builder(this).setTitle(2131362325).setMessage(2131362326).setNegativeButton(17039360, local3).setOnCancelListener(local2);
      if (!ApiHelper.HAS_SET_ICON_ATTRIBUTE)
        break label100;
      setAlertDialogIconAttribute(localBuilder);
    }
    while (true)
    {
      this.mAlertDialog = localBuilder.show();
      registerReceiver(this.mMountReceiver, this.mMountFilter);
      this.mPanoramaViewHelper.onStart();
      return;
      label100: localBuilder.setIcon(17301543);
    }
  }

  protected void onStop()
  {
    super.onStop();
    if (this.mAlertDialog != null)
    {
      unregisterReceiver(this.mMountReceiver);
      this.mAlertDialog.dismiss();
      this.mAlertDialog = null;
    }
    this.mPanoramaViewHelper.onStop();
  }

  protected void onStorageReady()
  {
    if (this.mAlertDialog == null)
      return;
    this.mAlertDialog.dismiss();
    this.mAlertDialog = null;
    unregisterReceiver(this.mMountReceiver);
  }

  public void setContentView(int paramInt)
  {
    super.setContentView(paramInt);
    this.mGLRootView = ((GLRootView)findViewById(2131558501));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.AbstractGalleryActivity
 * JD-Core Version:    0.5.4
 */