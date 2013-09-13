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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.MediaSet.SyncListener;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.picasasource.PicasaSource;
import com.android.gallery3d.settings.GallerySettings;
import com.android.gallery3d.ui.ActionModeHandler;
import com.android.gallery3d.ui.ActionModeHandler.ActionModeListener;
import com.android.gallery3d.ui.AlbumSetSlotRenderer;
import com.android.gallery3d.ui.DetailsHelper;
import com.android.gallery3d.ui.DetailsHelper.CloseListener;
import com.android.gallery3d.ui.DetailsHelper.DetailsSource;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.SelectionManager;
import com.android.gallery3d.ui.SelectionManager.SelectionListener;
import com.android.gallery3d.ui.SlotView;
import com.android.gallery3d.ui.SlotView.SimpleListener;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.HelpUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AlbumSetPage extends ActivityState
  implements EyePosition.EyePositionListener, GalleryActionBar.ClusterRunner, MediaSet.SyncListener, SelectionManager.SelectionListener
{
  private GalleryActionBar mActionBar;
  private ActionModeHandler mActionModeHandler;
  private AlbumSetDataLoader mAlbumSetDataAdapter;
  private AlbumSetSlotRenderer mAlbumSetView;
  private Button mCameraButton;
  private Config.AlbumSetPage mConfig;
  private DetailsHelper mDetailsHelper;
  private MyDetailsSource mDetailsSource;
  WeakReference<Toast> mEmptyAlbumToast = null;
  private EyePosition mEyePosition;
  private boolean mGetAlbum;
  private boolean mGetContent;
  private Handler mHandler;
  private boolean mInitialSynced = false;
  private boolean mIsActive = false;
  private int mLoadingBits = 0;
  private MediaSet mMediaSet;
  private final GLView mRootPane = new GLView()
  {
    private final float[] mMatrix = new float[16];

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      AlbumSetPage.this.mEyePosition.resetPosition();
      int i = AlbumSetPage.this.mActionBar.getHeight() + AlbumSetPage.this.mConfig.paddingTop;
      int j = paramInt4 - paramInt2 - AlbumSetPage.this.mConfig.paddingBottom;
      int k = paramInt3 - paramInt1;
      if (AlbumSetPage.this.mShowDetails)
        AlbumSetPage.this.mDetailsHelper.layout(paramInt1, i, paramInt3, paramInt4);
      while (true)
      {
        AlbumSetPage.this.mSlotView.layout(0, i, k, j);
        return;
        AlbumSetPage.this.mAlbumSetView.setHighlightItemPath(null);
      }
    }

    protected void render(GLCanvas paramGLCanvas)
    {
      paramGLCanvas.save(2);
      GalleryUtils.setViewPointMatrix(this.mMatrix, getWidth() / 2 + AlbumSetPage.this.mX, getHeight() / 2 + AlbumSetPage.this.mY, AlbumSetPage.this.mZ);
      paramGLCanvas.multiplyMatrix(this.mMatrix, 0);
      super.render(paramGLCanvas);
      paramGLCanvas.restore();
    }
  };
  private int mSelectedAction;
  protected SelectionManager mSelectionManager;
  private boolean mShowClusterMenu;
  private boolean mShowDetails;
  private boolean mShowedEmptyToastForSelf = false;
  private SlotView mSlotView;
  private String mSubtitle;
  private Future<Integer> mSyncTask = null;
  private String mTitle;
  private Vibrator mVibrator;
  private float mX;
  private float mY;
  private float mZ;

  private static boolean albumShouldOpenInFilmstrip(MediaSet paramMediaSet)
  {
    if (paramMediaSet.getMediaItemCount() == 1);
    for (ArrayList localArrayList = paramMediaSet.getMediaItem(0, 1); (localArrayList != null) && (!localArrayList.isEmpty()); localArrayList = null)
      return true;
    return false;
  }

  private void cleanupCameraButton()
  {
    if (this.mCameraButton == null);
    RelativeLayout localRelativeLayout;
    do
    {
      return;
      localRelativeLayout = (RelativeLayout)this.mActivity.findViewById(2131558510);
    }
    while (localRelativeLayout == null);
    localRelativeLayout.removeView(this.mCameraButton);
    this.mCameraButton = null;
  }

  private void clearLoadingBit(int paramInt)
  {
    this.mLoadingBits &= (paramInt ^ 0xFFFFFFFF);
    if ((this.mLoadingBits == 0) && (this.mIsActive) && (this.mAlbumSetDataAdapter.size() == 0))
      if (this.mActivity.getStateManager().getStateCount() > 1)
      {
        Intent localIntent = new Intent();
        localIntent.putExtra("empty-album", true);
        setStateResult(-1, localIntent);
        this.mActivity.getStateManager().finishState(this);
      }
    do
    {
      return;
      this.mShowedEmptyToastForSelf = true;
      showEmptyAlbumToast(1);
      this.mSlotView.invalidate();
      showCameraButton();
      return;
    }
    while (!this.mShowedEmptyToastForSelf);
    this.mShowedEmptyToastForSelf = false;
    hideEmptyAlbumToast();
    hideCameraButton();
  }

  private String getSelectedString()
  {
    int i = this.mSelectionManager.getSelectedCount();
    if (this.mActionBar.getClusterTypeAction() == 1);
    for (int j = 2131820546; ; j = 2131820547)
    {
      String str = this.mActivity.getResources().getQuantityString(j, i);
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(i);
      return String.format(str, arrayOfObject);
    }
  }

  private void getSlotCenter(int paramInt, int[] paramArrayOfInt)
  {
    Rect localRect1 = new Rect();
    this.mRootPane.getBoundsOf(this.mSlotView, localRect1);
    Rect localRect2 = this.mSlotView.getSlotRect(paramInt);
    int i = this.mSlotView.getScrollX();
    int j = this.mSlotView.getScrollY();
    paramArrayOfInt[0] = (localRect1.left + (localRect2.left + localRect2.right) / 2 - i);
    paramArrayOfInt[1] = (localRect1.top + (localRect2.top + localRect2.bottom) / 2 - j);
  }

  private void hideCameraButton()
  {
    if (this.mCameraButton == null)
      return;
    this.mCameraButton.setVisibility(8);
  }

  private void hideDetails()
  {
    this.mShowDetails = false;
    this.mDetailsHelper.hide();
    this.mAlbumSetView.setHighlightItemPath(null);
    this.mSlotView.invalidate();
  }

  private void hideEmptyAlbumToast()
  {
    if (this.mEmptyAlbumToast == null)
      return;
    Toast localToast = (Toast)this.mEmptyAlbumToast.get();
    if (localToast == null)
      return;
    localToast.cancel();
  }

  private void initializeData(Bundle paramBundle)
  {
    String str = paramBundle.getString("media-path");
    this.mMediaSet = this.mActivity.getDataManager().getMediaSet(str);
    this.mSelectionManager.setSourceMediaSet(this.mMediaSet);
    this.mAlbumSetDataAdapter = new AlbumSetDataLoader(this.mActivity, this.mMediaSet, 256);
    this.mAlbumSetDataAdapter.setLoadingListener(new MyLoadingListener(null));
    this.mAlbumSetView.setModel(this.mAlbumSetDataAdapter);
  }

  private void initializeViews()
  {
    this.mSelectionManager = new SelectionManager(this.mActivity, true);
    this.mSelectionManager.setSelectionListener(this);
    this.mConfig = Config.AlbumSetPage.get(this.mActivity);
    this.mSlotView = new SlotView(this.mActivity, this.mConfig.slotViewSpec);
    this.mAlbumSetView = new AlbumSetSlotRenderer(this.mActivity, this.mSelectionManager, this.mSlotView, this.mConfig.labelSpec, this.mConfig.placeholderColor);
    this.mSlotView.setSlotRenderer(this.mAlbumSetView);
    this.mSlotView.setListener(new SlotView.SimpleListener()
    {
      public void onDown(int paramInt)
      {
        AlbumSetPage.this.onDown(paramInt);
      }

      public void onLongTap(int paramInt)
      {
        AlbumSetPage.this.onLongTap(paramInt);
      }

      public void onSingleTapUp(int paramInt)
      {
        AlbumSetPage.this.onSingleTapUp(paramInt);
      }

      public void onUp(boolean paramBoolean)
      {
        AlbumSetPage.this.onUp(paramBoolean);
      }
    });
    this.mActionModeHandler = new ActionModeHandler(this.mActivity, this.mSelectionManager);
    this.mActionModeHandler.setActionModeListener(new ActionModeHandler.ActionModeListener()
    {
      public boolean onActionItemClicked(MenuItem paramMenuItem)
      {
        return AlbumSetPage.this.onItemSelected(paramMenuItem);
      }
    });
    this.mRootPane.addComponent(this.mSlotView);
  }

  private void onDown(int paramInt)
  {
    this.mAlbumSetView.setPressedIndex(paramInt);
  }

  private void onUp(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mAlbumSetView.setPressedIndex(-1);
      return;
    }
    this.mAlbumSetView.setPressedUp();
  }

  private void pickAlbum(int paramInt)
  {
    if (!this.mIsActive);
    MediaSet localMediaSet;
    do
    {
      return;
      localMediaSet = this.mAlbumSetDataAdapter.getMediaSet(paramInt);
    }
    while (localMediaSet == null);
    if (localMediaSet.getTotalMediaItemCount() == 0)
    {
      showEmptyAlbumToast(0);
      return;
    }
    hideEmptyAlbumToast();
    String str = localMediaSet.getPath().toString();
    Bundle localBundle = new Bundle(getData());
    int[] arrayOfInt = new int[2];
    getSlotCenter(paramInt, arrayOfInt);
    localBundle.putIntArray("set-center", arrayOfInt);
    if ((this.mGetAlbum) && (localMediaSet.isLeafAlbum()))
    {
      AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
      localAbstractGalleryActivity.setResult(-1, new Intent().putExtra("album-path", localMediaSet.getPath().toString()));
      localAbstractGalleryActivity.finish();
      return;
    }
    if (localMediaSet.getSubMediaSetCount() > 0)
    {
      localBundle.putString("media-path", str);
      this.mActivity.getStateManager().startStateForResult(AlbumSetPage.class, 1, localBundle);
      return;
    }
    if ((!this.mGetContent) && ((0x800 & localMediaSet.getSupportedOperations()) != 0))
      localBundle.putBoolean("auto-select-all", true);
    do
    {
      localBundle.putString("media-path", str);
      boolean bool1 = this.mActivity.getStateManager().hasStateClass(AlbumPage.class);
      boolean bool2 = false;
      if (!bool1)
        bool2 = true;
      localBundle.putBoolean("cluster-menu", bool2);
      this.mActivity.getStateManager().startStateForResult(AlbumPage.class, 1, localBundle);
      return;
    }
    while ((this.mGetContent) || (!albumShouldOpenInFilmstrip(localMediaSet)));
    localBundle.putParcelable("open-animation-rect", this.mSlotView.getSlotRect(paramInt, this.mRootPane));
    localBundle.putInt("index-hint", 0);
    localBundle.putString("media-set-path", str);
    localBundle.putBoolean("start-in-filmstrip", true);
    localBundle.putBoolean("in_camera_roll", localMediaSet.isCameraRoll());
    this.mActivity.getStateManager().startStateForResult(PhotoPage.class, 2, localBundle);
  }

  private void setLoadingBit(int paramInt)
  {
    this.mLoadingBits = (paramInt | this.mLoadingBits);
  }

  private boolean setupCameraButton()
  {
    if (!GalleryUtils.isCameraAvailable(this.mActivity));
    RelativeLayout localRelativeLayout;
    do
    {
      return false;
      localRelativeLayout = (RelativeLayout)this.mActivity.findViewById(2131558510);
    }
    while (localRelativeLayout == null);
    this.mCameraButton = new Button(this.mActivity);
    this.mCameraButton.setText(2131361892);
    this.mCameraButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837622, 0, 0);
    this.mCameraButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        GalleryUtils.startCameraActivity(AlbumSetPage.this.mActivity);
      }
    });
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
    localLayoutParams.addRule(13);
    localRelativeLayout.addView(this.mCameraButton, localLayoutParams);
    return true;
  }

  private void showCameraButton()
  {
    if ((this.mCameraButton == null) && (!setupCameraButton()))
      return;
    this.mCameraButton.setVisibility(0);
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
          AlbumSetPage.this.hideDetails();
        }
      });
    }
    this.mDetailsHelper.show();
  }

  private void showEmptyAlbumToast(int paramInt)
  {
    if (this.mEmptyAlbumToast != null)
    {
      Toast localToast2 = (Toast)this.mEmptyAlbumToast.get();
      if (localToast2 != null)
      {
        localToast2.show();
        return;
      }
    }
    Toast localToast1 = Toast.makeText(this.mActivity, 2131362253, paramInt);
    this.mEmptyAlbumToast = new WeakReference(localToast1);
    localToast1.show();
  }

  public void doCluster(int paramInt)
  {
    String str = FilterUtils.switchClusterPath(this.mMediaSet.getPath().toString(), paramInt);
    Bundle localBundle = new Bundle(getData());
    localBundle.putString("media-path", str);
    localBundle.putInt("selected-cluster", paramInt);
    this.mActivity.getStateManager().switchState(this, AlbumSetPage.class, localBundle);
  }

  protected int getBackgroundColorId()
  {
    return 2131296279;
  }

  public void onBackPressed()
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
    super.onBackPressed();
  }

  public void onCreate(Bundle paramBundle1, Bundle paramBundle2)
  {
    super.onCreate(paramBundle1, paramBundle2);
    initializeViews();
    initializeData(paramBundle1);
    Context localContext = this.mActivity.getAndroidContext();
    this.mGetContent = paramBundle1.getBoolean("get-content", false);
    this.mGetAlbum = paramBundle1.getBoolean("get-album", false);
    this.mTitle = paramBundle1.getString("set-title");
    this.mSubtitle = paramBundle1.getString("set-subtitle");
    this.mEyePosition = new EyePosition(localContext, this);
    this.mDetailsSource = new MyDetailsSource(null);
    this.mVibrator = ((Vibrator)localContext.getSystemService("vibrator"));
    this.mActionBar = this.mActivity.getGalleryActionBar();
    this.mSelectedAction = paramBundle1.getInt("selected-cluster", 1);
    this.mHandler = new SynchronizedHandler(this.mActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
          throw new AssertionError(paramMessage.what);
        case 1:
        }
        AlbumSetPage.this.pickAlbum(paramMessage.arg1);
      }
    };
  }

  protected boolean onCreateActionBar(Menu paramMenu)
  {
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    boolean bool1 = this.mActivity.getStateManager().hasStateClass(AlbumPage.class);
    MenuInflater localMenuInflater = getSupportMenuInflater();
    if (this.mGetContent)
    {
      localMenuInflater.inflate(2131886093, paramMenu);
      int l = this.mData.getInt("type-bits", 1);
      this.mActionBar.setTitle(GalleryUtils.getSelectionModePrompt(l));
      return true;
    }
    if (this.mGetAlbum)
    {
      localMenuInflater.inflate(2131886093, paramMenu);
      this.mActionBar.setTitle(2131362204);
      return true;
    }
    localMenuInflater.inflate(2131886081, paramMenu);
    boolean bool2 = this.mShowClusterMenu;
    int i;
    label118: int j;
    label142: MenuItem localMenuItem1;
    if (!bool1)
    {
      i = 1;
      this.mShowClusterMenu = i;
      if ((bool1) || (this.mActionBar.getClusterTypeAction() != 1))
        break label325;
      j = 1;
      localMenuItem1 = paramMenu.findItem(2131558625);
      if (j == 0)
        break label331;
    }
    for (int k = 2131362204; ; k = 2131362205)
    {
      localMenuItem1.setTitle(localAbstractGalleryActivity.getString(k));
      paramMenu.findItem(2131558623).setVisible(GalleryUtils.isCameraAvailable(localAbstractGalleryActivity));
      FilterUtils.setupMenuItems(this.mActionBar, this.mMediaSet.getPath(), false);
      Intent localIntent = HelpUtils.getHelpIntent(localAbstractGalleryActivity, 2131362324);
      MenuItem localMenuItem2 = paramMenu.findItem(2131558630);
      boolean bool3 = false;
      if (localIntent != null)
        bool3 = true;
      localMenuItem2.setVisible(bool3);
      if (localIntent != null)
        localMenuItem2.setIntent(localIntent);
      this.mActionBar.setTitle(this.mTitle);
      this.mActionBar.setSubtitle(this.mSubtitle);
      if (this.mShowClusterMenu != bool2);
      if (!this.mShowClusterMenu)
        break;
      this.mActionBar.enableClusterMenu(this.mSelectedAction, this);
      return true;
      i = 0;
      break label118:
      label325: j = 0;
      label331: break label142:
    }
    this.mActionBar.disableClusterMenu(true);
    return true;
  }

  public void onDestroy()
  {
    cleanupCameraButton();
    super.onDestroy();
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

  protected boolean onItemSelected(MenuItem paramMenuItem)
  {
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 2131558677:
      localAbstractGalleryActivity.setResult(0);
      localAbstractGalleryActivity.finish();
      return true;
    case 2131558625:
      this.mSelectionManager.setAutoLeaveSelectionMode(false);
      this.mSelectionManager.enterSelectionMode();
      return true;
    case 2131558672:
      if (this.mAlbumSetDataAdapter.size() != 0)
      {
        if (this.mShowDetails)
        {
          hideDetails();
          return true;
        }
        showDetails();
        return true;
      }
      Toast.makeText(localAbstractGalleryActivity, localAbstractGalleryActivity.getText(2131362252), 0).show();
      return true;
    case 2131558623:
      GalleryUtils.startCameraActivity(localAbstractGalleryActivity);
      return true;
    case 2131558627:
      Bundle localBundle = new Bundle();
      localBundle.putString("media-path", this.mActivity.getDataManager().getTopSetPath(3));
      this.mActivity.getStateManager().startState(ManageCachePage.class, localBundle);
      return true;
    case 2131558628:
      PicasaSource.requestSync(localAbstractGalleryActivity);
      return true;
    case 2131558629:
    }
    localAbstractGalleryActivity.startActivity(new Intent(localAbstractGalleryActivity, GallerySettings.class));
    return true;
  }

  public void onLongTap(int paramInt)
  {
    if ((this.mGetContent) || (this.mGetAlbum));
    MediaSet localMediaSet;
    do
    {
      return;
      localMediaSet = this.mAlbumSetDataAdapter.getMediaSet(paramInt);
    }
    while (localMediaSet == null);
    this.mSelectionManager.setAutoLeaveSelectionMode(true);
    this.mSelectionManager.toggle(localMediaSet.getPath());
    this.mSlotView.invalidate();
  }

  public void onPause()
  {
    super.onPause();
    this.mIsActive = false;
    this.mActionModeHandler.pause();
    this.mAlbumSetDataAdapter.pause();
    this.mAlbumSetView.pause();
    this.mEyePosition.pause();
    DetailsHelper.pause();
    this.mActionBar.disableClusterMenu(false);
    if (this.mSyncTask == null)
      return;
    this.mSyncTask.cancel();
    this.mSyncTask = null;
    clearLoadingBit(2);
  }

  public void onResume()
  {
    super.onResume();
    this.mIsActive = true;
    setContentPane(this.mRootPane);
    setLoadingBit(1);
    this.mAlbumSetDataAdapter.resume();
    this.mAlbumSetView.resume();
    this.mEyePosition.resume();
    this.mActionModeHandler.resume();
    if (this.mShowClusterMenu)
      this.mActionBar.enableClusterMenu(this.mSelectedAction, this);
    if (this.mInitialSynced)
      return;
    setLoadingBit(2);
    this.mSyncTask = this.mMediaSet.requestSync(this);
  }

  public void onSelectionChange(Path paramPath, boolean paramBoolean)
  {
    this.mActionModeHandler.setTitle(getSelectedString());
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
        this.mActionBar.disableClusterMenu(true);
        this.mActionModeHandler.startActionMode();
      }
      while (!this.mHapticsEnabled);
      this.mVibrator.vibrate(100L);
      return;
    case 2:
      this.mActionModeHandler.finishActionMode();
      if (this.mShowClusterMenu)
        this.mActionBar.enableClusterMenu(this.mSelectedAction, this);
      this.mRootPane.invalidate();
      return;
    case 3:
    }
    this.mActionModeHandler.updateSupportedOperation();
    this.mRootPane.invalidate();
  }

  public void onSingleTapUp(int paramInt)
  {
    if (!this.mIsActive);
    MediaSet localMediaSet;
    do
    {
      return;
      if (!this.mSelectionManager.inSelectionMode())
        break label50;
      localMediaSet = this.mAlbumSetDataAdapter.getMediaSet(paramInt);
    }
    while (localMediaSet == null);
    this.mSelectionManager.toggle(localMediaSet.getPath());
    this.mSlotView.invalidate();
    return;
    label50: this.mAlbumSetView.setPressedIndex(paramInt);
    this.mAlbumSetView.setPressedUp();
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, paramInt, 0), 180L);
  }

  protected void onStateResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramIntent != null) && (paramIntent.getBooleanExtra("empty-album", false)))
      showEmptyAlbumToast(0);
    switch (paramInt1)
    {
    default:
      return;
    case 1:
    }
    this.mSlotView.startRisingAnimation();
  }

  public void onSyncDone(MediaSet paramMediaSet, int paramInt)
  {
    if (paramInt == 2)
      Log.d("AlbumSetPage", "onSyncDone: " + Utils.maskDebugInfo(paramMediaSet.getName()) + " result=" + paramInt);
    this.mActivity.runOnUiThread(new Runnable(paramInt)
    {
      public void run()
      {
        GLRoot localGLRoot = AlbumSetPage.this.mActivity.getGLRoot();
        localGLRoot.lockRenderThread();
        try
        {
          if (this.val$resultCode == 0)
            AlbumSetPage.access$1602(AlbumSetPage.this, true);
          AlbumSetPage.this.clearLoadingBit(2);
          if ((this.val$resultCode == 2) && (AlbumSetPage.this.mIsActive))
            Log.w("AlbumSetPage", "failed to load album set");
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
      MediaSet localMediaSet = AlbumSetPage.this.mAlbumSetDataAdapter.getMediaSet(this.mIndex);
      if (localMediaSet != null)
      {
        AlbumSetPage.this.mAlbumSetView.setHighlightItemPath(localMediaSet.getPath());
        return localMediaSet.getDetails();
      }
      return null;
    }

    public int setIndex()
    {
      Path localPath = (Path)AlbumSetPage.this.mSelectionManager.getSelected(false).get(0);
      this.mIndex = AlbumSetPage.this.mAlbumSetDataAdapter.findSet(localPath);
      return this.mIndex;
    }

    public int size()
    {
      return AlbumSetPage.this.mAlbumSetDataAdapter.size();
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
      AlbumSetPage.this.clearLoadingBit(1);
    }

    public void onLoadingStarted()
    {
      AlbumSetPage.this.setLoadingBit(1);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.AlbumSetPage
 * JD-Core Version:    0.5.4
 */