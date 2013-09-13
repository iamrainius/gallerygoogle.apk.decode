package com.android.gallery3d.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.MediaSet.SyncListener;
import com.android.gallery3d.data.MtpDevice;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.ActionModeHandler;
import com.android.gallery3d.ui.ActionModeHandler.ActionModeListener;
import com.android.gallery3d.ui.AlbumSlotRenderer;
import com.android.gallery3d.ui.DetailsHelper;
import com.android.gallery3d.ui.DetailsHelper.CloseListener;
import com.android.gallery3d.ui.DetailsHelper.DetailsSource;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.PhotoFallbackEffect;
import com.android.gallery3d.ui.PhotoFallbackEffect.PositionProvider;
import com.android.gallery3d.ui.RelativePosition;
import com.android.gallery3d.ui.SelectionManager;
import com.android.gallery3d.ui.SelectionManager.SelectionListener;
import com.android.gallery3d.ui.SlotView;
import com.android.gallery3d.ui.SlotView.SimpleListener;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.MediaSetUtils;
import java.util.ArrayList;

public class AlbumPage extends ActivityState
  implements GalleryActionBar.ClusterRunner, GalleryActionBar.OnAlbumModeSelectedListener, MediaSet.SyncListener, SelectionManager.SelectionListener
{
  private ActionModeHandler mActionModeHandler;
  private AlbumDataLoader mAlbumDataAdapter;
  private AlbumSlotRenderer mAlbumView;
  private DetailsHelper mDetailsHelper;
  private MyDetailsSource mDetailsSource;
  private int mFocusIndex = 0;
  private boolean mGetContent;
  private Handler mHandler;
  private boolean mInCameraAndWantQuitOnPause;
  private boolean mInCameraApp;
  private boolean mInitialSynced = false;
  private boolean mIsActive = false;
  private boolean mLaunchedFromPhotoPage;
  private int mLoadingBits = 0;
  private MediaSet mMediaSet;
  private Path mMediaSetPath;
  private RelativePosition mOpenCenter = new RelativePosition();
  private String mParentMediaSetString;
  private PhotoFallbackEffect.PositionProvider mPositionProvider = new PhotoFallbackEffect.PositionProvider()
  {
    public int getItemIndex(Path paramPath)
    {
      int i = AlbumPage.this.mSlotView.getVisibleStart();
      int j = AlbumPage.this.mSlotView.getVisibleEnd();
      for (int k = i; k < j; ++k)
      {
        MediaItem localMediaItem = AlbumPage.this.mAlbumDataAdapter.get(k);
        if ((localMediaItem != null) && (localMediaItem.getPath() == paramPath))
          return k;
      }
      return -1;
    }

    public Rect getPosition(int paramInt)
    {
      Rect localRect1 = AlbumPage.this.mSlotView.getSlotRect(paramInt);
      Rect localRect2 = AlbumPage.this.mSlotView.bounds();
      localRect1.offset(localRect2.left - AlbumPage.this.mSlotView.getScrollX(), localRect2.top - AlbumPage.this.mSlotView.getScrollY());
      return localRect1;
    }
  };
  private PhotoFallbackEffect mResumeEffect;
  private final GLView mRootPane = new GLView()
  {
    private final float[] mMatrix = new float[16];

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = AlbumPage.this.mActivity.getGalleryActionBar().getHeight();
      int j = paramInt4 - paramInt2;
      int k = paramInt3 - paramInt1;
      if (AlbumPage.this.mShowDetails)
        AlbumPage.this.mDetailsHelper.layout(paramInt1, i, paramInt3, paramInt4);
      while (true)
      {
        AlbumPage.this.mOpenCenter.setReferencePosition(0, i);
        AlbumPage.this.mSlotView.layout(0, i, k, j);
        GalleryUtils.setViewPointMatrix(this.mMatrix, (paramInt3 - paramInt1) / 2, (paramInt4 - paramInt2) / 2, -AlbumPage.this.mUserDistance);
        return;
        AlbumPage.this.mAlbumView.setHighlightItemPath(null);
      }
    }

    protected void render(GLCanvas paramGLCanvas)
    {
      paramGLCanvas.save(2);
      paramGLCanvas.multiplyMatrix(this.mMatrix, 0);
      super.render(paramGLCanvas);
      if (AlbumPage.this.mResumeEffect != null)
      {
        if (!AlbumPage.this.mResumeEffect.draw(paramGLCanvas))
        {
          AlbumPage.access$702(AlbumPage.this, null);
          AlbumPage.this.mAlbumView.setSlotFilter(null);
        }
        invalidate();
      }
      paramGLCanvas.restore();
    }
  };
  protected SelectionManager mSelectionManager;
  private boolean mShowClusterMenu;
  private boolean mShowDetails;
  private SlotView mSlotView;
  private Future<Integer> mSyncTask = null;
  private float mUserDistance;
  private Vibrator mVibrator;

  private void clearLoadingBit(int paramInt)
  {
    this.mLoadingBits &= (paramInt ^ 0xFFFFFFFF);
    if ((this.mLoadingBits != 0) || (!this.mIsActive) || (this.mAlbumDataAdapter.size() != 0))
      return;
    Intent localIntent = new Intent();
    localIntent.putExtra("empty-album", true);
    setStateResult(-1, localIntent);
    this.mActivity.getStateManager().finishState(this);
  }

  private void hideDetails()
  {
    this.mShowDetails = false;
    this.mDetailsHelper.hide();
    this.mAlbumView.setHighlightItemPath(null);
    this.mSlotView.invalidate();
  }

  private void initializeData(Bundle paramBundle)
  {
    this.mMediaSetPath = Path.fromString(paramBundle.getString("media-path"));
    this.mParentMediaSetString = paramBundle.getString("parent-media-path");
    this.mMediaSet = this.mActivity.getDataManager().getMediaSet(this.mMediaSetPath);
    if (this.mMediaSet == null)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.mMediaSetPath;
      Utils.fail("MediaSet is null. Path = %s", arrayOfObject);
    }
    this.mSelectionManager.setSourceMediaSet(this.mMediaSet);
    this.mAlbumDataAdapter = new AlbumDataLoader(this.mActivity, this.mMediaSet);
    this.mAlbumDataAdapter.setLoadingListener(new MyLoadingListener(null));
    this.mAlbumView.setModel(this.mAlbumDataAdapter);
  }

  private void initializeViews()
  {
    this.mSelectionManager = new SelectionManager(this.mActivity, false);
    this.mSelectionManager.setSelectionListener(this);
    Config.AlbumPage localAlbumPage = Config.AlbumPage.get(this.mActivity);
    this.mSlotView = new SlotView(this.mActivity, localAlbumPage.slotViewSpec);
    this.mAlbumView = new AlbumSlotRenderer(this.mActivity, this.mSlotView, this.mSelectionManager, localAlbumPage.placeholderColor);
    this.mSlotView.setSlotRenderer(this.mAlbumView);
    this.mRootPane.addComponent(this.mSlotView);
    this.mSlotView.setListener(new SlotView.SimpleListener()
    {
      public void onDown(int paramInt)
      {
        AlbumPage.this.onDown(paramInt);
      }

      public void onLongTap(int paramInt)
      {
        AlbumPage.this.onLongTap(paramInt);
      }

      public void onSingleTapUp(int paramInt)
      {
        AlbumPage.this.onSingleTapUp(paramInt);
      }

      public void onUp(boolean paramBoolean)
      {
        AlbumPage.this.onUp(paramBoolean);
      }
    });
    this.mActionModeHandler = new ActionModeHandler(this.mActivity, this.mSelectionManager);
    this.mActionModeHandler.setActionModeListener(new ActionModeHandler.ActionModeListener()
    {
      public boolean onActionItemClicked(MenuItem paramMenuItem)
      {
        return AlbumPage.this.onItemSelected(paramMenuItem);
      }
    });
  }

  private void onDown(int paramInt)
  {
    this.mAlbumView.setPressedIndex(paramInt);
  }

  private void onGetContent(MediaItem paramMediaItem)
  {
    DataManager localDataManager = this.mActivity.getDataManager();
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    if (this.mData.getString("crop") != null)
    {
      Intent localIntent = new Intent("com.android.camera.action.CROP", localDataManager.getContentUri(paramMediaItem.getPath())).addFlags(33554432).putExtras(getData());
      if (this.mData.getParcelable("output") == null)
        localIntent.putExtra("return-data", true);
      localAbstractGalleryActivity.startActivity(localIntent);
      localAbstractGalleryActivity.finish();
      return;
    }
    localAbstractGalleryActivity.setResult(-1, new Intent(null, paramMediaItem.getContentUri()).addFlags(1));
    localAbstractGalleryActivity.finish();
  }

  private void onSingleTapUp(int paramInt)
  {
    if (!this.mIsActive);
    MediaItem localMediaItem;
    do
    {
      return;
      if (!this.mSelectionManager.inSelectionMode())
        break label50;
      localMediaItem = this.mAlbumDataAdapter.get(paramInt);
    }
    while (localMediaItem == null);
    this.mSelectionManager.toggle(localMediaItem.getPath());
    this.mSlotView.invalidate();
    return;
    label50: this.mAlbumView.setPressedIndex(paramInt);
    this.mAlbumView.setPressedUp();
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, paramInt, 0), 180L);
  }

  private void onUp(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mAlbumView.setPressedIndex(-1);
      return;
    }
    this.mAlbumView.setPressedUp();
  }

  private void onUpPressed()
  {
    if (this.mInCameraApp)
      GalleryUtils.startGalleryActivity(this.mActivity);
    do
    {
      return;
      if (this.mActivity.getStateManager().getStateCount() <= 1)
        continue;
      super.onBackPressed();
      return;
    }
    while (this.mParentMediaSetString == null);
    Bundle localBundle = new Bundle(getData());
    localBundle.putString("media-path", this.mParentMediaSetString);
    this.mActivity.getStateManager().switchState(this, AlbumSetPage.class, localBundle);
  }

  private void pickPhoto(int paramInt)
  {
    pickPhoto(paramInt, false);
  }

  private void pickPhoto(int paramInt, boolean paramBoolean)
  {
    if (!this.mIsActive);
    MediaItem localMediaItem;
    do
    {
      return;
      if (!paramBoolean)
        this.mActivity.getGLRoot().setLightsOutMode(true);
      localMediaItem = this.mAlbumDataAdapter.get(paramInt);
    }
    while (localMediaItem == null);
    if (this.mGetContent)
    {
      onGetContent(localMediaItem);
      return;
    }
    if (this.mLaunchedFromPhotoPage)
    {
      TransitionStore localTransitionStore = this.mActivity.getTransitionStore();
      localTransitionStore.put("albumpage-transition", Integer.valueOf(4));
      localTransitionStore.put("index-hint", Integer.valueOf(paramInt));
      onBackPressed();
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putInt("index-hint", paramInt);
    localBundle.putParcelable("open-animation-rect", this.mSlotView.getSlotRect(paramInt, this.mRootPane));
    localBundle.putString("media-set-path", this.mMediaSetPath.toString());
    localBundle.putString("media-item-path", localMediaItem.getPath().toString());
    localBundle.putInt("albumpage-transition", 1);
    localBundle.putBoolean("start-in-filmstrip", paramBoolean);
    localBundle.putBoolean("in_camera_roll", this.mMediaSet.isCameraRoll());
    if (paramBoolean)
    {
      this.mActivity.getStateManager().switchState(this, PhotoPage.class, localBundle);
      return;
    }
    this.mActivity.getStateManager().startStateForResult(PhotoPage.class, 2, localBundle);
  }

  private void prepareAnimationBackToFilmstrip(int paramInt)
  {
    if ((this.mAlbumDataAdapter == null) || (!this.mAlbumDataAdapter.isActive(paramInt)));
    do
      return;
    while (this.mAlbumDataAdapter.get(paramInt) == null);
    TransitionStore localTransitionStore = this.mActivity.getTransitionStore();
    localTransitionStore.put("index-hint", Integer.valueOf(paramInt));
    localTransitionStore.put("open-animation-rect", this.mSlotView.getSlotRect(paramInt, this.mRootPane));
  }

  private void setLoadingBit(int paramInt)
  {
    this.mLoadingBits = (paramInt | this.mLoadingBits);
  }

  private void showDetails()
  {
    this.mShowDetails = true;
    if (this.mDetailsHelper == null)
    {
      this.mDetailsHelper = new DetailsHelper(this.mActivity, this.mRootPane, this.mDetailsSource);
      this.mDetailsHelper.setCloseListener(new DetailsHelper.CloseListener()
      {
        public void onClose()
        {
          AlbumPage.this.hideDetails();
        }
      });
    }
    this.mDetailsHelper.show();
  }

  private void switchToFilmstrip()
  {
    if (this.mAlbumDataAdapter.size() < 1)
      return;
    int i = this.mSlotView.getVisibleStart();
    prepareAnimationBackToFilmstrip(i);
    if (this.mLaunchedFromPhotoPage)
    {
      onBackPressed();
      return;
    }
    pickPhoto(i, true);
  }

  public void doCluster(int paramInt)
  {
    String str = FilterUtils.newClusterPath(this.mMediaSet.getPath().toString(), paramInt);
    Bundle localBundle = new Bundle(getData());
    localBundle.putString("media-path", str);
    if (this.mShowClusterMenu)
    {
      Context localContext = this.mActivity.getAndroidContext();
      localBundle.putString("set-title", this.mMediaSet.getName());
      localBundle.putString("set-subtitle", GalleryActionBar.getClusterByTypeString(localContext, paramInt));
    }
    this.mActivity.getStateManager().startStateForResult(AlbumSetPage.class, 3, localBundle);
  }

  protected int getBackgroundColorId()
  {
    return 2131296284;
  }

  public void onAlbumModeSelected(int paramInt)
  {
    if (paramInt != 0)
      return;
    switchToFilmstrip();
  }

  protected void onBackPressed()
  {
    if (this.mShowDetails)
    {
      hideDetails();
      return;
    }
    if (this.mSelectionManager.inSelectionMode())
    {
      this.mSelectionManager.leaveSelectionMode();
      return;
    }
    if (this.mLaunchedFromPhotoPage)
      this.mActivity.getTransitionStore().putIfNotPresent("albumpage-transition", Integer.valueOf(2));
    if (this.mInCameraApp)
    {
      super.onBackPressed();
      return;
    }
    onUpPressed();
  }

  protected void onCreate(Bundle paramBundle1, Bundle paramBundle2)
  {
    super.onCreate(paramBundle1, paramBundle2);
    this.mUserDistance = GalleryUtils.meterToPixel(0.3F);
    initializeViews();
    initializeData(paramBundle1);
    this.mGetContent = paramBundle1.getBoolean("get-content", false);
    this.mShowClusterMenu = paramBundle1.getBoolean("cluster-menu", false);
    this.mDetailsSource = new MyDetailsSource(null);
    this.mVibrator = ((Vibrator)this.mActivity.getAndroidContext().getSystemService("vibrator"));
    if (paramBundle1.getBoolean("auto-select-all"))
      this.mSelectionManager.selectAll();
    this.mLaunchedFromPhotoPage = this.mActivity.getStateManager().hasStateClass(PhotoPage.class);
    this.mInCameraApp = paramBundle1.getBoolean("app-bridge", false);
    this.mHandler = new SynchronizedHandler(this.mActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
          throw new AssertionError(paramMessage.what);
        case 0:
        }
        AlbumPage.this.pickPhoto(paramMessage.arg1);
      }
    };
  }

  protected boolean onCreateActionBar(Menu paramMenu)
  {
    GalleryActionBar localGalleryActionBar = this.mActivity.getGalleryActionBar();
    MenuInflater localMenuInflater = getSupportMenuInflater();
    if (this.mGetContent)
    {
      localMenuInflater.inflate(2131886093, paramMenu);
      localGalleryActionBar.setTitle(GalleryUtils.getSelectionModePrompt(this.mData.getInt("type-bits", 1)));
      label46: localGalleryActionBar.setSubtitle(null);
      return true;
    }
    localMenuInflater.inflate(2131886080, paramMenu);
    localGalleryActionBar.setTitle(this.mMediaSet.getName());
    MenuItem localMenuItem1 = paramMenu.findItem(2131558624);
    if (!this.mMediaSet instanceof MtpDevice);
    for (boolean bool1 = true; ; bool1 = false)
    {
      localMenuItem1.setVisible(bool1);
      FilterUtils.setupMenuItems(localGalleryActionBar, this.mMediaSetPath, true);
      paramMenu.findItem(2131558626).setVisible(this.mShowClusterMenu);
      MenuItem localMenuItem2 = paramMenu.findItem(2131558623);
      boolean bool2 = MediaSetUtils.isCameraSource(this.mMediaSetPath);
      boolean bool3 = false;
      if (bool2)
      {
        boolean bool4 = GalleryUtils.isCameraAvailable(this.mActivity);
        bool3 = false;
        if (bool4)
          bool3 = true;
      }
      localMenuItem2.setVisible(bool3);
      break label46:
    }
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mAlbumDataAdapter == null)
      return;
    this.mAlbumDataAdapter.setLoadingListener(null);
  }

  protected boolean onItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 16908332:
      onUpPressed();
      return true;
    case 2131558677:
      this.mActivity.getStateManager().finishState(this);
      return true;
    case 2131558625:
      this.mSelectionManager.setAutoLeaveSelectionMode(false);
      this.mSelectionManager.enterSelectionMode();
      return true;
    case 2131558626:
      this.mActivity.getGalleryActionBar().showClusterDialog(this);
      return true;
    case 2131558624:
      this.mInCameraAndWantQuitOnPause = false;
      Bundle localBundle = new Bundle();
      localBundle.putString("media-set-path", this.mMediaSetPath.toString());
      localBundle.putBoolean("repeat", true);
      this.mActivity.getStateManager().startStateForResult(SlideshowPage.class, 1, localBundle);
      return true;
    case 2131558672:
      if (this.mShowDetails)
      {
        hideDetails();
        return true;
      }
      showDetails();
      return true;
    case 2131558623:
    }
    GalleryUtils.startCameraActivity(this.mActivity);
    return true;
  }

  public void onLongTap(int paramInt)
  {
    if (this.mGetContent);
    MediaItem localMediaItem;
    do
    {
      return;
      localMediaItem = this.mAlbumDataAdapter.get(paramInt);
    }
    while (localMediaItem == null);
    this.mSelectionManager.setAutoLeaveSelectionMode(true);
    this.mSelectionManager.toggle(localMediaItem.getPath());
    this.mSlotView.invalidate();
  }

  protected void onPause()
  {
    super.onPause();
    this.mIsActive = false;
    if (this.mSelectionManager.inSelectionMode())
      this.mSelectionManager.leaveSelectionMode();
    this.mAlbumView.setSlotFilter(null);
    this.mAlbumDataAdapter.pause();
    this.mAlbumView.pause();
    DetailsHelper.pause();
    if (!this.mGetContent)
      this.mActivity.getGalleryActionBar().disableAlbumModeMenu(true);
    if (this.mSyncTask != null)
    {
      this.mSyncTask.cancel();
      this.mSyncTask = null;
      clearLoadingBit(2);
    }
    this.mActionModeHandler.pause();
  }

  protected void onResume()
  {
    super.onResume();
    this.mIsActive = true;
    this.mResumeEffect = ((PhotoFallbackEffect)this.mActivity.getTransitionStore().get("resume_animation"));
    if (this.mResumeEffect != null)
    {
      this.mAlbumView.setSlotFilter(this.mResumeEffect);
      this.mResumeEffect.setPositionProvider(this.mPositionProvider);
      this.mResumeEffect.start();
    }
    setContentPane(this.mRootPane);
    int i;
    if (this.mActivity.getStateManager().getStateCount() > 1)
    {
      i = 1;
      label89: if (this.mParentMediaSetString == null)
        break label204;
    }
    for (int j = 1; ; j = 0)
    {
      boolean bool = i | j;
      GalleryActionBar localGalleryActionBar = this.mActivity.getGalleryActionBar();
      localGalleryActionBar.setDisplayOptions(bool, false);
      if (!this.mGetContent)
        localGalleryActionBar.enableAlbumModeMenu(1, this);
      setLoadingBit(1);
      this.mAlbumDataAdapter.resume();
      this.mAlbumView.resume();
      this.mAlbumView.setPressedIndex(-1);
      this.mActionModeHandler.resume();
      if (!this.mInitialSynced)
      {
        setLoadingBit(2);
        this.mSyncTask = this.mMediaSet.requestSync(this);
      }
      this.mInCameraAndWantQuitOnPause = this.mInCameraApp;
      return;
      i = 0;
      label204: break label89:
    }
  }

  public void onSelectionChange(Path paramPath, boolean paramBoolean)
  {
    int i = this.mSelectionManager.getSelectedCount();
    String str = this.mActivity.getResources().getQuantityString(2131820545, i);
    ActionModeHandler localActionModeHandler = this.mActionModeHandler;
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Integer.valueOf(i);
    localActionModeHandler.setTitle(String.format(str, arrayOfObject));
    this.mActionModeHandler.updateSupportedOperation(paramPath, paramBoolean);
  }

  public void onSelectionModeChange(int paramInt)
  {
    switch (paramInt)
    {
    default:
    case 1:
      do
      {
        return;
        this.mActionModeHandler.startActionMode();
      }
      while (!this.mHapticsEnabled);
      this.mVibrator.vibrate(100L);
      return;
    case 2:
      this.mActionModeHandler.finishActionMode();
      this.mRootPane.invalidate();
      return;
    case 3:
    }
    this.mActionModeHandler.updateSupportedOperation();
    this.mRootPane.invalidate();
  }

  protected void onStateResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    switch (paramInt1)
    {
    default:
    case 1:
    case 2:
      do
      {
        do
          return;
        while (paramIntent == null);
        this.mFocusIndex = paramIntent.getIntExtra("photo-index", 0);
        this.mSlotView.setCenterIndex(this.mFocusIndex);
        return;
      }
      while (paramIntent == null);
      this.mFocusIndex = paramIntent.getIntExtra("return-index-hint", 0);
      this.mSlotView.makeSlotVisible(this.mFocusIndex);
      return;
    case 3:
    }
    this.mSlotView.startRisingAnimation();
  }

  public void onSyncDone(MediaSet paramMediaSet, int paramInt)
  {
    Log.d("AlbumPage", "onSyncDone: " + Utils.maskDebugInfo(paramMediaSet.getName()) + " result=" + paramInt);
    this.mActivity.runOnUiThread(new Runnable(paramInt)
    {
      public void run()
      {
        GLRoot localGLRoot = AlbumPage.this.mActivity.getGLRoot();
        localGLRoot.lockRenderThread();
        try
        {
          if (this.val$resultCode == 0)
            AlbumPage.access$1502(AlbumPage.this, true);
          AlbumPage.this.clearLoadingBit(2);
          if ((this.val$resultCode == 2) && (AlbumPage.this.mIsActive) && (AlbumPage.this.mAlbumDataAdapter.size() == 0))
            Toast.makeText(AlbumPage.this.mActivity, 2131362245, 1).show();
          return;
        }
        finally
        {
          localGLRoot.unlockRenderThread();
        }
      }
    });
  }

  private class MyDetailsSource
    implements DetailsHelper.DetailsSource
  {
    private int mIndex;

    private MyDetailsSource()
    {
    }

    public MediaDetails getDetails()
    {
      MediaItem localMediaItem = AlbumPage.this.mAlbumDataAdapter.get(this.mIndex);
      if (localMediaItem != null)
      {
        AlbumPage.this.mAlbumView.setHighlightItemPath(localMediaItem.getPath());
        return localMediaItem.getDetails();
      }
      return null;
    }

    public int setIndex()
    {
      Path localPath = (Path)AlbumPage.this.mSelectionManager.getSelected(false).get(0);
      this.mIndex = AlbumPage.this.mAlbumDataAdapter.findItem(localPath);
      return this.mIndex;
    }

    public int size()
    {
      return AlbumPage.this.mAlbumDataAdapter.size();
    }
  }

  private class MyLoadingListener
    implements LoadingListener
  {
    private MyLoadingListener()
    {
    }

    public void onLoadingFinished()
    {
      AlbumPage.this.clearLoadingBit(1);
    }

    public void onLoadingStarted()
    {
      AlbumPage.this.setLoadingBit(1);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.AlbumPage
 * JD-Core Version:    0.5.4
 */