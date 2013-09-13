package com.android.gallery3d.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.CacheStorageUsageInfo;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.ManageCacheDrawer;
import com.android.gallery3d.ui.MenuExecutor;
import com.android.gallery3d.ui.MenuExecutor.ProgressListener;
import com.android.gallery3d.ui.SelectionManager;
import com.android.gallery3d.ui.SelectionManager.SelectionListener;
import com.android.gallery3d.ui.SlotView;
import com.android.gallery3d.ui.SlotView.SimpleListener;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.ArrayList;

public class ManageCachePage extends ActivityState
  implements View.OnClickListener, EyePosition.EyePositionListener, MenuExecutor.ProgressListener, SelectionManager.SelectionListener
{
  private int mAlbumCountToMakeAvailableOffline;
  private AlbumSetDataLoader mAlbumSetDataAdapter;
  private CacheStorageUsageInfo mCacheStorageInfo;
  private EyePosition mEyePosition;
  private View mFooterContent;
  private Handler mHandler;
  private boolean mLayoutReady = false;
  private MediaSet mMediaSet;
  private GLView mRootPane = new GLView()
  {
    private float[] mMatrix = new float[16];

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!ManageCachePage.this.mLayoutReady)
      {
        ManageCachePage.this.mHandler.sendEmptyMessage(2);
        return;
      }
      ManageCachePage.access$002(ManageCachePage.this, false);
      ManageCachePage.this.mEyePosition.resetPosition();
      int i = ManageCachePage.this.mActivity.getGalleryActionBar().getHeight();
      int j = paramInt4 - paramInt2;
      View localView = ManageCachePage.this.mActivity.findViewById(2131558512);
      if (localView != null)
      {
        int[] arrayOfInt = { 0, 0 };
        localView.getLocationOnScreen(arrayOfInt);
        j = arrayOfInt[1];
      }
      ManageCachePage.this.mSlotView.layout(0, i, paramInt3 - paramInt1, j);
    }

    protected void render(GLCanvas paramGLCanvas)
    {
      paramGLCanvas.save(2);
      GalleryUtils.setViewPointMatrix(this.mMatrix, getWidth() / 2 + ManageCachePage.this.mX, getHeight() / 2 + ManageCachePage.this.mY, ManageCachePage.this.mZ);
      paramGLCanvas.multiplyMatrix(this.mMatrix, 0);
      super.render(paramGLCanvas);
      paramGLCanvas.restore();
    }

    protected void renderBackground(GLCanvas paramGLCanvas)
    {
      paramGLCanvas.clearBuffer(getBackgroundColor());
    }
  };
  protected ManageCacheDrawer mSelectionDrawer;
  protected SelectionManager mSelectionManager;
  private SlotView mSlotView;
  private Future<Void> mUpdateStorageInfo;
  private ThreadPool.Job<Void> mUpdateStorageInfoJob = new ThreadPool.Job()
  {
    public Void run(ThreadPool.JobContext paramJobContext)
    {
      ManageCachePage.this.mCacheStorageInfo.loadStorageInfo(paramJobContext);
      if (!paramJobContext.isCancelled())
        ManageCachePage.this.mHandler.sendEmptyMessage(1);
      return null;
    }
  };
  private float mX;
  private float mY;
  private float mZ;

  private void initializeData(Bundle paramBundle)
  {
    String str = paramBundle.getString("media-path");
    this.mMediaSet = this.mActivity.getDataManager().getMediaSet(str);
    this.mSelectionManager.setSourceMediaSet(this.mMediaSet);
    this.mSelectionManager.setAutoLeaveSelectionMode(false);
    this.mSelectionManager.enterSelectionMode();
    this.mAlbumSetDataAdapter = new AlbumSetDataLoader(this.mActivity, this.mMediaSet, 256);
    this.mSelectionDrawer.setModel(this.mAlbumSetDataAdapter);
  }

  private void initializeFooterViews()
  {
    this.mFooterContent = this.mActivity.getLayoutInflater().inflate(2130968615, null);
    this.mFooterContent.findViewById(2131558523).setOnClickListener(this);
    refreshCacheStorageInfo();
  }

  private void initializeViews()
  {
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    this.mSelectionManager = new SelectionManager(this.mActivity, true);
    this.mSelectionManager.setSelectionListener(this);
    Config.ManageCachePage localManageCachePage = Config.ManageCachePage.get(localAbstractGalleryActivity);
    this.mSlotView = new SlotView(this.mActivity, localManageCachePage.slotViewSpec);
    this.mSelectionDrawer = new ManageCacheDrawer(this.mActivity, this.mSelectionManager, this.mSlotView, localManageCachePage.labelSpec, localManageCachePage.cachePinSize, localManageCachePage.cachePinMargin);
    this.mSlotView.setSlotRenderer(this.mSelectionDrawer);
    this.mSlotView.setListener(new SlotView.SimpleListener()
    {
      public void onDown(int paramInt)
      {
        ManageCachePage.this.onDown(paramInt);
      }

      public void onSingleTapUp(int paramInt)
      {
        ManageCachePage.this.onSingleTapUp(paramInt);
      }

      public void onUp(boolean paramBoolean)
      {
        ManageCachePage.this.onUp();
      }
    });
    this.mRootPane.addComponent(this.mSlotView);
    initializeFooterViews();
  }

  private void onDown(int paramInt)
  {
    this.mSelectionDrawer.setPressedIndex(paramInt);
  }

  private void onUp()
  {
    this.mSelectionDrawer.setPressedIndex(-1);
  }

  private void refreshCacheStorageInfo()
  {
    ProgressBar localProgressBar = (ProgressBar)this.mFooterContent.findViewById(2131558522);
    TextView localTextView = (TextView)this.mFooterContent.findViewById(2131558521);
    localProgressBar.setMax(10000);
    long l1 = this.mCacheStorageInfo.getTotalBytes();
    long l2 = this.mCacheStorageInfo.getUsedBytes();
    long l3 = this.mCacheStorageInfo.getExpectedUsedBytes();
    long l4 = this.mCacheStorageInfo.getFreeBytes();
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    if (l1 == 0L)
    {
      localProgressBar.setProgress(0);
      localProgressBar.setSecondaryProgress(0);
      localTextView.setText(localAbstractGalleryActivity.getString(2131362297, new Object[] { "-" }));
      return;
    }
    localProgressBar.setProgress((int)(10000L * l2 / l1));
    localProgressBar.setSecondaryProgress((int)(10000L * l3 / l1));
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Formatter.formatFileSize(localAbstractGalleryActivity, l4);
    localTextView.setText(localAbstractGalleryActivity.getString(2131362297, arrayOfObject));
  }

  private void showToast()
  {
    if (this.mAlbumCountToMakeAvailableOffline <= 0)
      return;
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    Toast.makeText(localAbstractGalleryActivity, localAbstractGalleryActivity.getResources().getQuantityString(2131820548, this.mAlbumCountToMakeAvailableOffline), 0).show();
  }

  private void showToastForLocalAlbum()
  {
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    Toast.makeText(localAbstractGalleryActivity, localAbstractGalleryActivity.getResources().getString(2131362292), 0).show();
  }

  protected int getBackgroundColorId()
  {
    return 2131296288;
  }

  public void onClick(View paramView)
  {
    int i = paramView.getId();
    boolean bool = false;
    if (i == 2131558523)
      bool = true;
    Utils.assertTrue(bool);
    GLRoot localGLRoot = this.mActivity.getGLRoot();
    localGLRoot.lockRenderThread();
    try
    {
      if (this.mSelectionManager.getSelected(false).size() == 0)
      {
        onBackPressed();
        return;
      }
      showToast();
      new MenuExecutor(this.mActivity, this.mSelectionManager).startAction(2131558402, 2131362230, this);
      return;
    }
    finally
    {
      localGLRoot.unlockRenderThread();
    }
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    initializeFooterViews();
    FrameLayout localFrameLayout = (FrameLayout)this.mActivity.findViewById(2131558512);
    if (localFrameLayout.getVisibility() != 0)
      return;
    localFrameLayout.removeAllViews();
    localFrameLayout.addView(this.mFooterContent);
  }

  public void onConfirmDialogDismissed(boolean paramBoolean)
  {
  }

  public void onConfirmDialogShown()
  {
  }

  public void onCreate(Bundle paramBundle1, Bundle paramBundle2)
  {
    super.onCreate(paramBundle1, paramBundle2);
    this.mCacheStorageInfo = new CacheStorageUsageInfo(this.mActivity);
    initializeViews();
    initializeData(paramBundle1);
    this.mEyePosition = new EyePosition(this.mActivity.getAndroidContext(), this);
    this.mHandler = new SynchronizedHandler(this.mActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
          return;
        case 1:
          ManageCachePage.this.refreshCacheStorageInfo();
          return;
        case 2:
        }
        ManageCachePage.access$002(ManageCachePage.this, true);
        removeMessages(2);
        ManageCachePage.this.mRootPane.requestLayout();
      }
    };
  }

  public void onEyePositionChanged(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.mRootPane.lockRendering();
    this.mX = paramFloat1;
    this.mY = paramFloat2;
    this.mZ = paramFloat3;
    this.mRootPane.unlockRendering();
    this.mRootPane.invalidate();
  }

  public void onPause()
  {
    super.onPause();
    this.mAlbumSetDataAdapter.pause();
    this.mSelectionDrawer.pause();
    this.mEyePosition.pause();
    if (this.mUpdateStorageInfo != null)
    {
      this.mUpdateStorageInfo.cancel();
      this.mUpdateStorageInfo = null;
    }
    this.mHandler.removeMessages(1);
    FrameLayout localFrameLayout = (FrameLayout)this.mActivity.findViewById(2131558512);
    localFrameLayout.removeAllViews();
    localFrameLayout.setVisibility(4);
  }

  public void onProgressComplete(int paramInt)
  {
    onBackPressed();
  }

  public void onProgressStart()
  {
  }

  public void onProgressUpdate(int paramInt)
  {
  }

  public void onResume()
  {
    super.onResume();
    setContentPane(this.mRootPane);
    this.mAlbumSetDataAdapter.resume();
    this.mSelectionDrawer.resume();
    this.mEyePosition.resume();
    this.mUpdateStorageInfo = this.mActivity.getThreadPool().submit(this.mUpdateStorageInfoJob);
    FrameLayout localFrameLayout = (FrameLayout)this.mActivity.findViewById(2131558512);
    localFrameLayout.addView(this.mFooterContent);
    localFrameLayout.setVisibility(0);
  }

  public void onSelectionChange(Path paramPath, boolean paramBoolean)
  {
  }

  public void onSelectionModeChange(int paramInt)
  {
  }

  public void onSingleTapUp(int paramInt)
  {
    MediaSet localMediaSet = this.mAlbumSetDataAdapter.getMediaSet(paramInt);
    if (localMediaSet == null)
      return;
    if ((0x100 & localMediaSet.getSupportedOperations()) == 0)
    {
      showToastForLocalAlbum();
      return;
    }
    Path localPath = localMediaSet.getPath();
    boolean bool1;
    label46: boolean bool2;
    if (localMediaSet.getCacheFlag() == 2)
    {
      bool1 = true;
      bool2 = this.mSelectionManager.isItemSelected(localPath);
      if (!bool1)
        if (!bool2)
          break label134;
    }
    for (this.mAlbumCountToMakeAvailableOffline = (-1 + this.mAlbumCountToMakeAvailableOffline); ; this.mAlbumCountToMakeAvailableOffline = (1 + this.mAlbumCountToMakeAvailableOffline))
    {
      long l = localMediaSet.getCacheSize();
      CacheStorageUsageInfo localCacheStorageUsageInfo = this.mCacheStorageInfo;
      if ((bool1 ^ bool2))
        l = -l;
      localCacheStorageUsageInfo.increaseTargetCacheSize(l);
      refreshCacheStorageInfo();
      this.mSelectionManager.toggle(localPath);
      this.mSlotView.invalidate();
      return;
      bool1 = false;
      label134: break label46:
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.ManageCachePage
 * JD-Core Version:    0.5.4
 */