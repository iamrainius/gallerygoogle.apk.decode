package com.android.gallery3d.app;

import android.annotation.TargetApi;
import android.app.ActionBar.OnMenuVisibilityListener;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.camera.CameraActivity;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.data.ComboAlbum;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.FilterDeleteSet;
import com.android.gallery3d.data.LocalImage;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaObject.PanoramaSupportCallback;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.MtpSource;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.data.SecureAlbum;
import com.android.gallery3d.data.SecureSource;
import com.android.gallery3d.data.SnailAlbum;
import com.android.gallery3d.data.SnailItem;
import com.android.gallery3d.data.SnailSource;
import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.picasasource.PicasaSource;
import com.android.gallery3d.ui.DetailsHelper;
import com.android.gallery3d.ui.DetailsHelper.CloseListener;
import com.android.gallery3d.ui.DetailsHelper.DetailsSource;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.ImportCompleteListener;
import com.android.gallery3d.ui.MenuExecutor;
import com.android.gallery3d.ui.MenuExecutor.ProgressListener;
import com.android.gallery3d.ui.PhotoView;
import com.android.gallery3d.ui.PhotoView.Listener;
import com.android.gallery3d.ui.PhotoView.Model;
import com.android.gallery3d.ui.SelectionManager;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.LightCycleHelper.PanoramaViewHelper;
import java.util.ArrayList;
import java.util.List;

public class PhotoPage extends ActivityState
  implements AppBridge.Server, GalleryActionBar.OnAlbumModeSelectedListener, OrientationManager.Listener, PhotoPageBottomControls.Delegate, PhotoView.Listener
{
  private GalleryActionBar mActionBar;
  private volatile boolean mActionBarAllowed = true;
  private AppBridge mAppBridge;
  private GalleryApp mApplication;
  private PhotoPageBottomControls mBottomControls;
  private long mCameraSwitchCutoff = 0L;
  private MenuExecutor.ProgressListener mConfirmDialogListener = new MenuExecutor.ProgressListener()
  {
    public void onConfirmDialogDismissed(boolean paramBoolean)
    {
      PhotoPage.this.refreshHidingMessage();
    }

    public void onConfirmDialogShown()
    {
      PhotoPage.this.mHandler.removeMessages(1);
    }

    public void onProgressComplete(int paramInt)
    {
    }

    public void onProgressStart()
    {
    }

    public void onProgressUpdate(int paramInt)
    {
    }
  };
  private int mCurrentIndex = 0;
  private MediaItem mCurrentPhoto = null;
  private long mDeferUpdateUntil = 9223372036854775807L;
  private boolean mDeferredUpdateWaiting = false;
  private boolean mDeleteIsFocus;
  private Path mDeletePath;
  private DetailsHelper mDetailsHelper;
  private Handler mHandler;
  private boolean mHasActivityResult;
  private boolean mHasCameraScreennailOrPlaceholder = false;
  private boolean mHaveImageEditor;
  private boolean mIsActive;
  private boolean mIsMenuVisible;
  private boolean mIsPanorama;
  private boolean mIsPanorama360;
  private FilterDeleteSet mMediaSet;
  private MenuExecutor mMenuExecutor;
  private final MyMenuVisibilityListener mMenuVisibilityListener = new MyMenuVisibilityListener(null);
  private Model mModel;
  private Uri[] mNfcPushUris = new Uri[1];
  private OrientationManager mOrientationManager;
  private String mOriginalSetPathString;
  private PhotoView mPhotoView;
  private PhotoPageProgressBar mProgressBar;
  private UpdateProgressListener mProgressListener;
  private boolean mRecenterCameraOnResume = true;
  private final MediaObject.PanoramaSupportCallback mRefreshBottomControlsCallback = new MediaObject.PanoramaSupportCallback()
  {
    public void panoramaInfoAvailable(MediaObject paramMediaObject, boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = 1;
      Handler localHandler;
      int j;
      if (paramMediaObject == PhotoPage.this.mCurrentPhoto)
      {
        localHandler = PhotoPage.this.mHandler;
        if (!paramBoolean1)
          break label51;
        j = i;
        label31: if (!paramBoolean2)
          break label57;
      }
      while (true)
      {
        localHandler.obtainMessage(8, j, i, paramMediaObject).sendToTarget();
        return;
        label51: j = 0;
        break label31:
        label57: i = 0;
      }
    }
  };
  private final GLView mRootPane = new GLView()
  {
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      PhotoPage.this.mPhotoView.layout(0, 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
      if (!PhotoPage.this.mShowDetails)
        return;
      PhotoPage.this.mDetailsHelper.layout(paramInt1, PhotoPage.this.mActionBar.getHeight(), paramInt3, paramInt4);
    }
  };
  private SnailItem mScreenNailItem;
  private SnailAlbum mScreenNailSet;
  private SecureAlbum mSecureAlbum;
  private SelectionManager mSelectionManager;
  private String mSetPathString;
  private boolean mShowBars = true;
  private boolean mShowDetails;
  private boolean mShowSpinner;
  private boolean mSkipUpdateCurrentPhoto = false;
  private boolean mStartInFilmstrip;
  private boolean mTreatBackAsUp;
  private final MediaObject.PanoramaSupportCallback mUpdatePanoramaMenuItemsCallback = new MediaObject.PanoramaSupportCallback()
  {
    public void panoramaInfoAvailable(MediaObject paramMediaObject, boolean paramBoolean1, boolean paramBoolean2)
    {
      Handler localHandler;
      if (paramMediaObject == PhotoPage.this.mCurrentPhoto)
      {
        localHandler = PhotoPage.this.mHandler;
        if (!paramBoolean2)
          break label42;
      }
      for (int i = 1; ; i = 0)
      {
        localHandler.obtainMessage(16, i, 0, paramMediaObject).sendToTarget();
        label42: return;
      }
    }
  };
  private final MediaObject.PanoramaSupportCallback mUpdateShareURICallback = new MediaObject.PanoramaSupportCallback()
  {
    public void panoramaInfoAvailable(MediaObject paramMediaObject, boolean paramBoolean1, boolean paramBoolean2)
    {
      Handler localHandler;
      if (paramMediaObject == PhotoPage.this.mCurrentPhoto)
      {
        localHandler = PhotoPage.this.mHandler;
        if (!paramBoolean2)
          break label42;
      }
      for (int i = 1; ; i = 0)
      {
        localHandler.obtainMessage(15, i, 0, paramMediaObject).sendToTarget();
        label42: return;
      }
    }
  };

  private boolean canDoSlideShow()
  {
    if ((this.mMediaSet == null) || (this.mCurrentPhoto == null));
    do
      return false;
    while ((this.mCurrentPhoto.getMediaType() != 2) || (MtpSource.isMtpPath(this.mOriginalSetPathString)));
    return true;
  }

  private boolean canShowBars()
  {
    if ((this.mAppBridge != null) && (this.mCurrentIndex == 0) && (!this.mPhotoView.getFilmMode()));
    do
      return false;
    while (!this.mActionBarAllowed);
    return true;
  }

  private static Intent createShareIntent(MediaObject paramMediaObject)
  {
    int i = paramMediaObject.getMediaType();
    return new Intent("android.intent.action.SEND").setType(MenuExecutor.getMimeType(i)).putExtra("android.intent.extra.STREAM", paramMediaObject.getContentUri()).addFlags(1);
  }

  private static Intent createSharePanoramaIntent(Uri paramUri)
  {
    return new Intent("android.intent.action.SEND").setType("application/vnd.google.panorama360+jpg").putExtra("android.intent.extra.STREAM", paramUri).addFlags(1);
  }

  private void hideBars()
  {
    if (!this.mShowBars)
      return;
    this.mShowBars = false;
    this.mActionBar.hide();
    this.mActivity.getGLRoot().setLightsOutMode(true);
    this.mHandler.removeMessages(1);
    refreshBottomControlsWhenReady();
  }

  private void hideDetails()
  {
    this.mShowDetails = false;
    this.mDetailsHelper.hide();
  }

  private void launchCamera()
  {
    Intent localIntent = new Intent(this.mActivity, CameraActivity.class);
    this.mRecenterCameraOnResume = false;
    this.mActivity.startActivity(localIntent);
  }

  private void launchPhotoEditor()
  {
    MediaItem localMediaItem = this.mModel.getMediaItem(0);
    if ((localMediaItem == null) || ((0x200 & localMediaItem.getSupportedOperations()) == 0))
      return;
    Intent localIntent = new Intent("action_nextgen_edit");
    localIntent.setDataAndType(localMediaItem.getContentUri(), localMediaItem.getMimeType()).setFlags(1);
    if (this.mActivity.getPackageManager().queryIntentActivities(localIntent, 65536).size() == 0)
      localIntent.setAction("android.intent.action.EDIT");
    localIntent.putExtra("launch-fullscreen", this.mActivity.isFullscreen());
    this.mRecenterCameraOnResume = false;
    this.mActivity.startActivityForResult(Intent.createChooser(localIntent, null), 4);
    overrideTransitionToEditor();
  }

  private void launchTinyPlanet()
  {
    MediaItem localMediaItem = this.mModel.getMediaItem(0);
    Intent localIntent = new Intent("com.android.camera.action.TINY_PLANET");
    localIntent.setClass(this.mActivity, FilterShowActivity.class);
    localIntent.setDataAndType(localMediaItem.getContentUri(), localMediaItem.getMimeType()).setFlags(1);
    localIntent.putExtra("launch-fullscreen", this.mActivity.isFullscreen());
    this.mRecenterCameraOnResume = false;
    this.mActivity.startActivityForResult(localIntent, 4);
    overrideTransitionToEditor();
  }

  private void onUpPressed()
  {
    if ((((this.mStartInFilmstrip) || (this.mAppBridge != null))) && (!this.mPhotoView.getFilmMode()))
      this.mPhotoView.setFilmMode(true);
    do
    {
      return;
      if (this.mActivity.getStateManager().getStateCount() <= 1)
        continue;
      setResult();
      super.onBackPressed();
      return;
    }
    while (this.mOriginalSetPathString == null);
    if (this.mAppBridge == null)
    {
      Bundle localBundle = new Bundle(getData());
      localBundle.putString("media-path", this.mOriginalSetPathString);
      localBundle.putString("parent-media-path", this.mActivity.getDataManager().getTopSetPath(3));
      this.mActivity.getStateManager().switchState(this, AlbumPage.class, localBundle);
      return;
    }
    GalleryUtils.startGalleryActivity(this.mActivity);
  }

  private void overrideTransitionToEditor()
  {
    this.mActivity.overridePendingTransition(17432578, 17432577);
  }

  private void refreshHidingMessage()
  {
    this.mHandler.removeMessages(1);
    if ((this.mIsMenuVisible) || (this.mPhotoView.getFilmMode()))
      return;
    this.mHandler.sendEmptyMessageDelayed(1, 3500L);
  }

  private void requestDeferredUpdate()
  {
    this.mDeferUpdateUntil = (250L + SystemClock.uptimeMillis());
    if (this.mDeferredUpdateWaiting)
      return;
    this.mDeferredUpdateWaiting = true;
    this.mHandler.sendEmptyMessageDelayed(14, 250L);
  }

  private void setCurrentPhotoByIntent(Intent paramIntent)
  {
    if (paramIntent == null);
    Path localPath1;
    do
    {
      return;
      localPath1 = this.mApplication.getDataManager().findPathByUri(paramIntent.getData(), paramIntent.getType());
    }
    while (localPath1 == null);
    Path localPath2 = this.mApplication.getDataManager().getDefaultSetOf(localPath1);
    if (!localPath2.equalsIgnoreCase(this.mOriginalSetPathString))
    {
      Bundle localBundle = new Bundle(getData());
      localBundle.putString("media-set-path", localPath2.toString());
      localBundle.putString("media-item-path", localPath1.toString());
      this.mActivity.getStateManager().startState(PhotoPage.class, localBundle);
      return;
    }
    this.mModel.setCurrentPhoto(localPath1, this.mCurrentIndex);
  }

  private void setNfcBeamPushUri(Uri paramUri)
  {
    this.mNfcPushUris[0] = paramUri;
  }

  private void setResult()
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("return-index-hint", this.mCurrentIndex);
    setStateResult(-1, localIntent);
  }

  @TargetApi(16)
  private void setupNfcBeamPush()
  {
    if (!ApiHelper.HAS_SET_BEAM_PUSH_URIS);
    NfcAdapter localNfcAdapter;
    do
    {
      return;
      localNfcAdapter = NfcAdapter.getDefaultAdapter(this.mActivity);
    }
    while (localNfcAdapter == null);
    localNfcAdapter.setBeamPushUris(null, this.mActivity);
    localNfcAdapter.setBeamPushUrisCallback(new NfcAdapter.CreateBeamUrisCallback()
    {
      public Uri[] createBeamUris(NfcEvent paramNfcEvent)
      {
        return PhotoPage.this.mNfcPushUris;
      }
    }
    , this.mActivity);
  }

  private void showBars()
  {
    if (this.mShowBars)
      return;
    this.mShowBars = true;
    this.mOrientationManager.unlockOrientation();
    this.mActionBar.show();
    this.mActivity.getGLRoot().setLightsOutMode(false);
    refreshHidingMessage();
    refreshBottomControlsWhenReady();
  }

  private void showDetails()
  {
    this.mShowDetails = true;
    if (this.mDetailsHelper == null)
    {
      this.mDetailsHelper = new DetailsHelper(this.mActivity, this.mRootPane, new MyDetailsSource(null));
      this.mDetailsHelper.setCloseListener(new DetailsHelper.CloseListener()
      {
        public void onClose()
        {
          PhotoPage.this.hideDetails();
        }
      });
    }
    this.mDetailsHelper.show();
  }

  private void switchToGrid()
  {
    boolean bool1 = true;
    if (this.mActivity.getStateManager().hasStateClass(AlbumPage.class))
      onUpPressed();
    do
      return;
    while (this.mOriginalSetPathString == null);
    if (this.mProgressBar != null)
    {
      updateCurrentPhoto(null);
      this.mProgressBar.hideProgress();
    }
    Bundle localBundle = new Bundle(getData());
    localBundle.putString("media-path", this.mOriginalSetPathString);
    localBundle.putString("parent-media-path", this.mActivity.getDataManager().getTopSetPath(3));
    boolean bool2;
    label115: TransitionStore localTransitionStore;
    if ((!this.mActivity.getStateManager().hasStateClass(AlbumPage.class)) && (this.mAppBridge == null))
    {
      bool2 = bool1;
      localBundle.putBoolean("cluster-menu", bool2);
      if (this.mAppBridge == null)
        break label209;
      label130: localBundle.putBoolean("app-bridge", bool1);
      localTransitionStore = this.mActivity.getTransitionStore();
      if (this.mAppBridge == null)
        break label214;
    }
    for (int i = -1 + this.mCurrentIndex; ; i = this.mCurrentIndex)
    {
      localTransitionStore.put("return-index-hint", Integer.valueOf(i));
      if ((!this.mHasCameraScreennailOrPlaceholder) || (this.mAppBridge == null))
        break;
      this.mActivity.getStateManager().startState(AlbumPage.class, localBundle);
      return;
      bool2 = false;
      break label115:
      label209: bool1 = false;
      label214: break label130:
    }
    this.mActivity.getStateManager().switchState(this, AlbumPage.class, localBundle);
  }

  private void toggleBars()
  {
    if (this.mShowBars)
      hideBars();
    do
      return;
    while (!canShowBars());
    showBars();
  }

  private void transitionFromAlbumPageIfNeeded()
  {
    TransitionStore localTransitionStore = this.mActivity.getTransitionStore();
    int i = ((Integer)localTransitionStore.get("albumpage-transition", Integer.valueOf(0))).intValue();
    label56: boolean bool;
    if ((i == 0) && (this.mAppBridge != null) && (this.mRecenterCameraOnResume))
    {
      this.mCurrentIndex = 0;
      this.mPhotoView.resetToFirstPicture();
      if (i != 2)
        break label162;
      PhotoView localPhotoView = this.mPhotoView;
      if ((!this.mStartInFilmstrip) && (this.mAppBridge == null))
        break label156;
      bool = true;
      label84: localPhotoView.setFilmMode(bool);
    }
    do
    {
      return;
      int j = ((Integer)localTransitionStore.get("index-hint", Integer.valueOf(-1))).intValue();
      if (j >= 0);
      if (this.mHasCameraScreennailOrPlaceholder)
        ++j;
      if (j < this.mMediaSet.getMediaItemCount());
      this.mCurrentIndex = j;
      this.mModel.moveTo(this.mCurrentIndex);
      break label56:
      label156: bool = false;
      label162: break label84:
    }
    while (i != 4);
    this.mPhotoView.setFilmMode(false);
  }

  private void updateBars()
  {
    if (canShowBars())
      return;
    hideBars();
  }

  private void updateCurrentPhoto(MediaItem paramMediaItem)
  {
    if (this.mCurrentPhoto == paramMediaItem)
      return;
    this.mCurrentPhoto = paramMediaItem;
    if (this.mPhotoView.getFilmMode())
    {
      requestDeferredUpdate();
      return;
    }
    updateUIForCurrentPhoto();
  }

  private void updateMenuOperations()
  {
    Menu localMenu = this.mActionBar.getMenu();
    if (localMenu == null);
    boolean bool;
    do
    {
      return;
      MenuItem localMenuItem = localMenu.findItem(2131558624);
      if (localMenuItem == null)
        continue;
      if ((this.mSecureAlbum != null) || (!canDoSlideShow()))
        break label96;
      bool = true;
      label44: localMenuItem.setVisible(bool);
    }
    while (this.mCurrentPhoto == null);
    int i = this.mCurrentPhoto.getSupportedOperations();
    if (this.mSecureAlbum != null)
      i &= 1;
    while (true)
    {
      MenuExecutor.updateMenuOperation(localMenu, i);
      this.mCurrentPhoto.getPanoramaSupport(this.mUpdatePanoramaMenuItemsCallback);
      return;
      label96: bool = false;
      break label44:
      if (this.mHaveImageEditor)
        continue;
      i &= -513;
    }
  }

  private void updatePanoramaUI(boolean paramBoolean)
  {
    Menu localMenu = this.mActionBar.getMenu();
    if (localMenu == null);
    label69: MenuItem localMenuItem1;
    do
    {
      do
      {
        MenuItem localMenuItem2;
        do
        {
          return;
          MenuExecutor.updateMenuForPanorama(localMenu, paramBoolean, paramBoolean);
          if (!paramBoolean)
            break label69;
          localMenuItem2 = localMenu.findItem(2131558663);
        }
        while (localMenuItem2 == null);
        localMenuItem2.setShowAsAction(0);
        localMenuItem2.setTitle(this.mActivity.getResources().getString(2131362215));
        return;
      }
      while ((0x4 & this.mCurrentPhoto.getSupportedOperations()) == 0);
      localMenuItem1 = localMenu.findItem(2131558663);
    }
    while (localMenuItem1 == null);
    localMenuItem1.setShowAsAction(1);
    localMenuItem1.setTitle(this.mActivity.getResources().getString(2131362213));
  }

  private void updateProgressBar()
  {
    if (this.mProgressBar == null)
      return;
    this.mProgressBar.hideProgress();
    StitchingProgressManager localStitchingProgressManager = this.mApplication.getStitchingProgressManager();
    if ((localStitchingProgressManager == null) || (!this.mCurrentPhoto instanceof LocalImage))
      return;
    Integer localInteger = localStitchingProgressManager.getProgress(this.mCurrentPhoto.getContentUri());
    if (localInteger == null)
      return;
    this.mProgressBar.setProgress(localInteger.intValue());
  }

  private void updateUIForCurrentPhoto()
  {
    if (this.mCurrentPhoto == null)
      return;
    if (((0x8000 & this.mCurrentPhoto.getSupportedOperations()) != 0) && (!this.mPhotoView.getFilmMode()))
      this.mPhotoView.setWantPictureCenterCallbacks(true);
    updateMenuOperations();
    refreshBottomControlsWhenReady();
    if (this.mShowDetails)
      this.mDetailsHelper.reloadDetails();
    if ((this.mSecureAlbum == null) && ((0x4 & this.mCurrentPhoto.getSupportedOperations()) != 0))
      this.mCurrentPhoto.getPanoramaSupport(this.mUpdateShareURICallback);
    updateProgressBar();
  }

  private void wantBars()
  {
    if (!canShowBars())
      return;
    showBars();
  }

  public void addSecureAlbumItem(boolean paramBoolean, int paramInt)
  {
    this.mSecureAlbum.addMediaItem(paramBoolean, paramInt);
  }

  public boolean canDisplayBottomControl(int paramInt)
  {
    int i = 1;
    if (this.mCurrentPhoto == null)
      return false;
    switch (paramInt)
    {
    default:
      return false;
    case 2131558564:
      if ((this.mHaveImageEditor) && (this.mShowBars) && (!this.mPhotoView.getFilmMode()) && ((0x200 & this.mCurrentPhoto.getSupportedOperations()) != 0) && (this.mCurrentPhoto.getMediaType() == 2));
      while (true)
      {
        return i;
        i = 0;
      }
    case 2131558565:
      return this.mIsPanorama;
    case 2131558566:
    }
    if ((this.mHaveImageEditor) && (this.mShowBars) && (this.mIsPanorama360) && (!this.mPhotoView.getFilmMode()));
    while (true)
    {
      return i;
      i = 0;
    }
  }

  public boolean canDisplayBottomControls()
  {
    return (this.mIsActive) && (!this.mPhotoView.canUndo());
  }

  protected void clearStateResult()
  {
    this.mHasActivityResult = false;
  }

  protected int getBackgroundColorId()
  {
    return 2131296286;
  }

  public void notifyScreenNailChanged()
  {
    this.mScreenNailItem.setScreenNail(this.mAppBridge.attachScreenNail());
    this.mScreenNailSet.notifyChange();
  }

  public void onActionBarAllowed(boolean paramBoolean)
  {
    this.mActionBarAllowed = paramBoolean;
    this.mHandler.sendEmptyMessage(5);
  }

  public void onActionBarWanted()
  {
    this.mHandler.sendEmptyMessage(7);
  }

  public void onAlbumModeSelected(int paramInt)
  {
    if (paramInt != 1)
      return;
    switchToGrid();
  }

  protected void onBackPressed()
  {
    if (this.mShowDetails)
      hideDetails();
    do
      return;
    while ((this.mAppBridge != null) && (switchWithCaptureAnimation(-1)));
    setResult();
    if ((this.mStartInFilmstrip) && (!this.mPhotoView.getFilmMode()))
    {
      this.mPhotoView.setFilmMode(true);
      return;
    }
    if (this.mTreatBackAsUp)
    {
      onUpPressed();
      return;
    }
    super.onBackPressed();
  }

  public void onBottomControlClicked(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return;
    case 2131558564:
      launchPhotoEditor();
      return;
    case 2131558565:
      this.mRecenterCameraOnResume = false;
      this.mActivity.getPanoramaViewHelper().showPanorama(this.mCurrentPhoto.getContentUri());
      return;
    case 2131558566:
    }
    launchTinyPlanet();
  }

  public void onCommitDeleteImage()
  {
    if (this.mDeletePath == null)
      return;
    this.mSelectionManager.deSelectAll();
    this.mSelectionManager.toggle(this.mDeletePath);
    this.mMenuExecutor.onMenuClicked(2131558666, null, true, false);
    this.mDeletePath = null;
  }

  public void onCreate(Bundle paramBundle1, Bundle paramBundle2)
  {
    super.onCreate(paramBundle1, paramBundle2);
    this.mActionBar = this.mActivity.getGalleryActionBar();
    this.mSelectionManager = new SelectionManager(this.mActivity, false);
    this.mMenuExecutor = new MenuExecutor(this.mActivity, this.mSelectionManager);
    this.mPhotoView = new PhotoView(this.mActivity);
    this.mPhotoView.setListener(this);
    this.mRootPane.addComponent(this.mPhotoView);
    this.mApplication = ((GalleryApp)this.mActivity.getApplication());
    this.mOrientationManager = this.mActivity.getOrientationManager();
    this.mOrientationManager.addListener(this);
    this.mActivity.getGLRoot().setOrientationSource(this.mOrientationManager);
    this.mHandler = new SynchronizedHandler(this.mActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        case 2:
        case 3:
        default:
          throw new AssertionError(paramMessage.what);
        case 1:
          PhotoPage.this.hideBars();
        case 8:
        case 4:
        case 5:
        case 7:
        case 6:
        case 14:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 15:
        case 16:
        }
        do
        {
          do
          {
            do
            {
              do
                return;
              while ((PhotoPage.this.mCurrentPhoto != paramMessage.obj) || (PhotoPage.this.mBottomControls == null));
              PhotoPage localPhotoPage1 = PhotoPage.this;
              boolean bool3;
              label145: PhotoPage localPhotoPage2;
              if (paramMessage.arg1 == 1)
              {
                bool3 = true;
                PhotoPage.access$1102(localPhotoPage1, bool3);
                localPhotoPage2 = PhotoPage.this;
                if (paramMessage.arg2 != 1)
                  break label195;
              }
              for (boolean bool4 = true; ; bool4 = false)
              {
                PhotoPage.access$1202(localPhotoPage2, bool4);
                PhotoPage.this.mBottomControls.refresh();
                return;
                bool3 = false;
                label195: break label145:
              }
              AppBridge localAppBridge = PhotoPage.this.mAppBridge;
              if (paramMessage.arg1 == 1);
              for (boolean bool2 = true; ; bool2 = false)
              {
                localAppBridge.onFullScreenChanged(bool2);
                return;
              }
              PhotoPage.this.updateBars();
              return;
              PhotoPage.this.wantBars();
              return;
              PhotoPage.this.mActivity.getGLRoot().unfreeze();
              return;
              long l = PhotoPage.this.mDeferUpdateUntil - SystemClock.uptimeMillis();
              if (l <= 0L)
              {
                PhotoPage.access$1702(PhotoPage.this, false);
                PhotoPage.this.updateUIForCurrentPhoto();
                return;
              }
              PhotoPage.this.mHandler.sendEmptyMessageDelayed(14, l);
              return;
              PhotoPage.access$1902(PhotoPage.this, false);
              if (!PhotoPage.this.mPhotoView.getFilmMode());
              for (int j = 1; ; j = 1)
              {
                while (true)
                {
                  if (j != 0);
                  if (PhotoPage.this.mAppBridge != null)
                    break label451;
                  PhotoPage.this.launchCamera();
                  PhotoPage.this.mPhotoView.switchToImage(1);
                  return;
                  if ((SystemClock.uptimeMillis() >= PhotoPage.this.mCameraSwitchCutoff) || (PhotoPage.this.mMediaSet.getMediaItemCount() <= 1))
                    break;
                  PhotoPage.this.mPhotoView.switchToImage(1);
                  j = 0;
                }
                if (PhotoPage.this.mAppBridge == null)
                  continue;
                PhotoPage.this.mPhotoView.setFilmMode(false);
              }
              label451: PhotoPage.this.updateBars();
              PhotoPage.this.updateCurrentPhoto(PhotoPage.this.mModel.getMediaItem(0));
              return;
            }
            while ((PhotoPage.this.mPhotoView.getFilmMode()) || (PhotoPage.this.mCurrentPhoto == null) || ((0x8000 & PhotoPage.this.mCurrentPhoto.getSupportedOperations()) == 0));
            PhotoPage.this.mPhotoView.setFilmMode(true);
            return;
            MediaItem localMediaItem = PhotoPage.this.mCurrentPhoto;
            PhotoPage.access$102(PhotoPage.this, null);
            PhotoPage.this.updateCurrentPhoto(localMediaItem);
            return;
            PhotoPage.this.updateUIForCurrentPhoto();
            return;
            PhotoPage.this.updateProgressBar();
            return;
          }
          while (PhotoPage.this.mCurrentPhoto != paramMessage.obj);
          if (paramMessage.arg1 != 0);
          for (int i = 1; ; i = 0)
          {
            Uri localUri = PhotoPage.this.mCurrentPhoto.getContentUri();
            Intent localIntent1 = null;
            if (i != 0)
              localIntent1 = PhotoPage.access$2600(localUri);
            Intent localIntent2 = PhotoPage.access$2700(PhotoPage.this.mCurrentPhoto);
            PhotoPage.this.mActionBar.setShareIntents(localIntent1, localIntent2);
            PhotoPage.this.setNfcBeamPushUri(localUri);
            return;
          }
        }
        while (PhotoPage.this.mCurrentPhoto != paramMessage.obj);
        if (paramMessage.arg1 != 0);
        for (boolean bool1 = true; ; bool1 = false)
        {
          PhotoPage.this.updatePanoramaUI(bool1);
          return;
        }
      }
    };
    this.mSetPathString = paramBundle1.getString("media-set-path");
    this.mOriginalSetPathString = this.mSetPathString;
    setupNfcBeamPush();
    Object localObject;
    label198: boolean bool1;
    label517: int k;
    label746: boolean bool3;
    label756: boolean bool4;
    label766: boolean bool2;
    label820: label847: AbstractGalleryActivity localAbstractGalleryActivity1;
    if (paramBundle1.getString("media-item-path") != null)
    {
      localObject = Path.fromString(paramBundle1.getString("media-item-path"));
      this.mTreatBackAsUp = paramBundle1.getBoolean("treat-back-as-up", false);
      this.mStartInFilmstrip = paramBundle1.getBoolean("start-in-filmstrip", false);
      bool1 = paramBundle1.getBoolean("in_camera_roll", false);
      this.mCurrentIndex = paramBundle1.getInt("index-hint", 0);
      if (this.mSetPathString == null)
        break label1087;
      this.mShowSpinner = true;
      this.mAppBridge = ((AppBridge)paramBundle1.getParcelable("app-bridge"));
      if (this.mAppBridge == null)
        break label991;
      this.mShowBars = false;
      this.mHasCameraScreennailOrPlaceholder = true;
      this.mAppBridge.setServer(this);
      int i1 = SnailSource.newId();
      Path localPath1 = SnailSource.getSetPath(i1);
      Path localPath2 = SnailSource.getItemPath(i1);
      this.mScreenNailSet = ((SnailAlbum)this.mActivity.getDataManager().getMediaObject(localPath1));
      this.mScreenNailItem = ((SnailItem)this.mActivity.getDataManager().getMediaObject(localPath2));
      this.mScreenNailItem.setScreenNail(this.mAppBridge.attachScreenNail());
      if (paramBundle1.getBoolean("show_when_locked", false))
        this.mFlags = (0x20 | this.mFlags);
      if (!this.mSetPathString.equals("/local/all/0"))
      {
        if (SecureSource.isSecurePath(this.mSetPathString))
        {
          this.mSecureAlbum = ((SecureAlbum)this.mActivity.getDataManager().getMediaSet(this.mSetPathString));
          this.mShowSpinner = false;
        }
        this.mSetPathString = ("/filter/empty/{" + this.mSetPathString + "}");
      }
      this.mSetPathString = ("/combo/item/{" + localPath1 + "," + this.mSetPathString + "}");
      localObject = localPath2;
      MediaSet localMediaSet = this.mActivity.getDataManager().getMediaSet(this.mSetPathString);
      if ((this.mHasCameraScreennailOrPlaceholder) && (localMediaSet instanceof ComboAlbum))
        ((ComboAlbum)localMediaSet).useNameOfChild(1);
      this.mSelectionManager.setSourceMediaSet(localMediaSet);
      this.mSetPathString = ("/filter/delete/{" + this.mSetPathString + "}");
      this.mMediaSet = ((FilterDeleteSet)this.mActivity.getDataManager().getMediaSet(this.mSetPathString));
      if (this.mMediaSet == null)
        Log.w("PhotoPage", "failed to restore " + this.mSetPathString);
      if (localObject == null)
      {
        int l = this.mMediaSet.getMediaItemCount();
        if (l <= 0)
          break label985;
        if (this.mCurrentIndex >= l)
          this.mCurrentIndex = 0;
        localObject = ((MediaItem)this.mMediaSet.getMediaItem(this.mCurrentIndex, 1).get(0)).getPath();
      }
      AbstractGalleryActivity localAbstractGalleryActivity2 = this.mActivity;
      PhotoView localPhotoView2 = this.mPhotoView;
      FilterDeleteSet localFilterDeleteSet = this.mMediaSet;
      int j = this.mCurrentIndex;
      if (this.mAppBridge != null)
        break label1057;
      k = -1;
      if (this.mAppBridge != null)
        break label1063;
      bool3 = false;
      if (this.mAppBridge != null)
        break label1075;
      bool4 = false;
      PhotoDataAdapter localPhotoDataAdapter = new PhotoDataAdapter(localAbstractGalleryActivity2, localPhotoView2, localFilterDeleteSet, (Path)localObject, j, k, bool3, bool4);
      this.mModel = localPhotoDataAdapter;
      this.mPhotoView.setModel(this.mModel);
      localPhotoDataAdapter.setDataListener(new PhotoDataAdapter.DataListener()
      {
        public void onLoadingFinished()
        {
          if (!PhotoPage.this.mModel.isEmpty())
          {
            MediaItem localMediaItem = PhotoPage.this.mModel.getMediaItem(0);
            if (localMediaItem != null)
              PhotoPage.this.updateCurrentPhoto(localMediaItem);
          }
          do
            return;
          while ((!PhotoPage.this.mIsActive) || (PhotoPage.this.mMediaSet.getNumberOfDeletions() != 0));
          PhotoPage.this.mActivity.getStateManager().finishState(PhotoPage.this);
        }

        public void onLoadingStarted()
        {
        }

        public void onPhotoChanged(int paramInt, Path paramPath)
        {
          int i = PhotoPage.this.mCurrentIndex;
          PhotoPage.access$3002(PhotoPage.this, paramInt);
          if (PhotoPage.this.mHasCameraScreennailOrPlaceholder)
          {
            if (PhotoPage.this.mCurrentIndex > 0)
              PhotoPage.access$1902(PhotoPage.this, false);
            if ((i != 0) || (PhotoPage.this.mCurrentIndex <= 0) || (PhotoPage.this.mPhotoView.getFilmMode()))
              break label142;
            PhotoPage.this.mPhotoView.setFilmMode(true);
          }
          while (true)
          {
            if (!PhotoPage.this.mSkipUpdateCurrentPhoto)
            {
              if (paramPath != null)
              {
                MediaItem localMediaItem = PhotoPage.this.mModel.getMediaItem(0);
                if (localMediaItem != null)
                  PhotoPage.this.updateCurrentPhoto(localMediaItem);
              }
              PhotoPage.this.updateBars();
            }
            PhotoPage.this.refreshHidingMessage();
            return;
            if ((i == 2) && (PhotoPage.this.mCurrentIndex == 1))
            {
              label142: PhotoPage.access$2002(PhotoPage.this, 300L + SystemClock.uptimeMillis());
              PhotoPage.this.mPhotoView.stopScrolling();
            }
            if ((i < 1) || (PhotoPage.this.mCurrentIndex != 0))
              continue;
            PhotoPage.this.mPhotoView.setWantPictureCenterCallbacks(true);
            PhotoPage.access$1902(PhotoPage.this, true);
          }
        }
      });
      PhotoView localPhotoView1 = this.mPhotoView;
      if ((!this.mStartInFilmstrip) || (this.mMediaSet.getMediaItemCount() <= 1))
        break label1149;
      bool2 = true;
      localPhotoView1.setFilmMode(bool2);
      localAbstractGalleryActivity1 = this.mActivity;
      if (this.mAppBridge == null)
        break label1155;
    }
    for (int i = 2131558408; ; i = 2131558510)
    {
      RelativeLayout localRelativeLayout = (RelativeLayout)localAbstractGalleryActivity1.findViewById(i);
      if (localRelativeLayout != null)
      {
        if (this.mSecureAlbum == null)
          this.mBottomControls = new PhotoPageBottomControls(this, this.mActivity, localRelativeLayout);
        StitchingProgressManager localStitchingProgressManager = this.mApplication.getStitchingProgressManager();
        if (localStitchingProgressManager != null)
        {
          this.mProgressBar = new PhotoPageProgressBar(this.mActivity, localRelativeLayout);
          this.mProgressListener = new UpdateProgressListener(null);
          localStitchingProgressManager.addChangeListener(this.mProgressListener);
          if (this.mSecureAlbum != null)
            localStitchingProgressManager.addChangeListener(this.mSecureAlbum);
        }
      }
      label985: return;
      localObject = null;
      break label198:
      label991: if ((bool1) && (GalleryUtils.isCameraAvailable(this.mActivity)));
      this.mSetPathString = ("/combo/item/{/filter/camera_shortcut," + this.mSetPathString + "}");
      this.mCurrentIndex = (1 + this.mCurrentIndex);
      this.mHasCameraScreennailOrPlaceholder = true;
      break label517:
      label1057: k = 0;
      break label746:
      label1063: bool3 = this.mAppBridge.isPanorama();
      break label756:
      label1075: bool4 = this.mAppBridge.isStaticCamera();
      break label766:
      label1087: MediaItem localMediaItem = (MediaItem)this.mActivity.getDataManager().getMediaObject((Path)localObject);
      this.mModel = new SinglePhotoDataAdapter(this.mActivity, this.mPhotoView, localMediaItem);
      this.mPhotoView.setModel(this.mModel);
      updateCurrentPhoto(localMediaItem);
      this.mShowSpinner = false;
      break label820:
      label1149: bool2 = false;
      label1155: break label847:
    }
  }

  protected boolean onCreateActionBar(Menu paramMenu)
  {
    this.mActionBar.createActionBarMenu(2131886092, paramMenu);
    this.mHaveImageEditor = GalleryUtils.isEditorAvailable(this.mActivity, "image/*");
    updateMenuOperations();
    GalleryActionBar localGalleryActionBar = this.mActionBar;
    if (this.mMediaSet != null);
    for (String str = this.mMediaSet.getName(); ; str = "")
    {
      localGalleryActionBar.setTitle(str);
      return true;
    }
  }

  public void onCurrentImageUpdated()
  {
    this.mActivity.getGLRoot().unfreeze();
  }

  public void onDeleteImage(Path paramPath, int paramInt)
  {
    onCommitDeleteImage();
    this.mDeletePath = paramPath;
    if (paramInt == 0);
    for (int i = 1; ; i = 0)
    {
      this.mDeleteIsFocus = i;
      this.mMediaSet.addDeletion(paramPath, paramInt + this.mCurrentIndex);
      return;
    }
  }

  protected void onDestroy()
  {
    if (this.mAppBridge != null)
    {
      this.mAppBridge.setServer(null);
      this.mScreenNailItem.setScreenNail(null);
      this.mAppBridge.detachScreenNail();
      this.mAppBridge = null;
      this.mScreenNailSet = null;
      this.mScreenNailItem = null;
    }
    this.mOrientationManager.removeListener(this);
    this.mActivity.getGLRoot().setOrientationSource(null);
    if (this.mBottomControls != null)
      this.mBottomControls.cleanup();
    this.mHandler.removeCallbacksAndMessages(null);
    super.onDestroy();
  }

  public void onFilmModeChanged(boolean paramBoolean)
  {
    refreshBottomControlsWhenReady();
    if (this.mShowSpinner)
    {
      if (!paramBoolean)
        break label37;
      this.mActionBar.enableAlbumModeMenu(0, this);
    }
    while (paramBoolean)
    {
      this.mHandler.removeMessages(1);
      return;
      label37: this.mActionBar.disableAlbumModeMenu(true);
    }
    refreshHidingMessage();
  }

  public void onFullScreenChanged(boolean paramBoolean)
  {
    Handler localHandler = this.mHandler;
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      localHandler.obtainMessage(4, i, 0).sendToTarget();
      return;
    }
  }

  protected boolean onItemSelected(MenuItem paramMenuItem)
  {
    if (this.mModel == null)
      return true;
    refreshHidingMessage();
    MediaItem localMediaItem = this.mModel.getMediaItem(0);
    if (localMediaItem == null)
      return true;
    int i = this.mModel.getCurrentIndex();
    Path localPath = localMediaItem.getPath();
    DataManager localDataManager = this.mActivity.getDataManager();
    int j = paramMenuItem.getItemId();
    String str = null;
    switch (j)
    {
    default:
      return false;
    case 16908332:
      onUpPressed();
      return true;
    case 2131558624:
      Bundle localBundle = new Bundle();
      localBundle.putString("media-set-path", this.mMediaSet.getPath().toString());
      localBundle.putString("media-item-path", localPath.toString());
      localBundle.putInt("photo-index", i);
      localBundle.putBoolean("repeat", true);
      this.mActivity.getStateManager().startStateForResult(SlideshowPage.class, 1, localBundle);
      return true;
    case 2131558670:
      AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
      Intent localIntent2 = new Intent("com.android.camera.action.CROP");
      localIntent2.setClass(localAbstractGalleryActivity, FilterShowActivity.class);
      localIntent2.setDataAndType(localDataManager.getContentUri(localPath), localMediaItem.getMimeType()).setFlags(1);
      if (PicasaSource.isPicasaImage(localMediaItem));
      for (int k = 3; ; k = 2)
      {
        localAbstractGalleryActivity.startActivityForResult(localIntent2, k);
        return true;
      }
    case 2131558676:
      Intent localIntent1 = new Intent(this.mActivity, TrimVideo.class);
      localIntent1.setData(localDataManager.getContentUri(localPath));
      localIntent1.putExtra("media-item-path", localMediaItem.getFilePath());
      this.mActivity.startActivityForResult(localIntent1, 6);
      return true;
    case 2131558667:
      launchPhotoEditor();
      return true;
    case 2131558672:
      if (this.mShowDetails)
        hideDetails();
      while (true)
      {
        return true;
        showDetails();
      }
    case 2131558666:
      str = this.mActivity.getResources().getQuantityString(2131820544, 1);
    case 2131558668:
    case 2131558669:
    case 2131558671:
    case 2131558673:
      this.mSelectionManager.deSelectAll();
      this.mSelectionManager.toggle(localPath);
      this.mMenuExecutor.onMenuClicked(paramMenuItem, str, this.mConfirmDialogListener);
      return true;
    case 2131558664:
    }
    this.mSelectionManager.deSelectAll();
    this.mSelectionManager.toggle(localPath);
    this.mMenuExecutor.onMenuClicked(paramMenuItem, null, new ImportCompleteListener(this.mActivity));
    return true;
  }

  public void onOrientationCompensationChanged()
  {
    this.mActivity.getGLRoot().requestLayoutContentPane();
  }

  public void onPause()
  {
    super.onPause();
    this.mIsActive = false;
    this.mActivity.getGLRoot().unfreeze();
    this.mHandler.removeMessages(6);
    DetailsHelper.pause();
    if (this.mShowDetails)
      hideDetails();
    if (this.mModel != null)
      this.mModel.pause();
    this.mPhotoView.pause();
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(8);
    refreshBottomControlsWhenReady();
    this.mActionBar.removeOnMenuVisibilityListener(this.mMenuVisibilityListener);
    if (this.mShowSpinner)
      this.mActionBar.disableAlbumModeMenu(true);
    onCommitDeleteImage();
    this.mMenuExecutor.pause();
    if (this.mMediaSet == null)
      return;
    this.mMediaSet.clearDeletion();
  }

  public void onPictureCenter(boolean paramBoolean)
  {
    int i;
    label20: Handler localHandler;
    if ((paramBoolean) || ((this.mHasCameraScreennailOrPlaceholder) && (this.mAppBridge == null)))
    {
      i = 1;
      this.mPhotoView.setWantPictureCenterCallbacks(false);
      this.mHandler.removeMessages(9);
      this.mHandler.removeMessages(10);
      localHandler = this.mHandler;
      if (i == 0)
        break label72;
    }
    for (int j = 9; ; j = 10)
    {
      localHandler.sendEmptyMessage(j);
      return;
      i = 0;
      label72: break label20:
    }
  }

  protected void onResume()
  {
    super.onResume();
    if (this.mModel == null)
    {
      this.mActivity.getStateManager().finishState(this);
      return;
    }
    transitionFromAlbumPageIfNeeded();
    this.mActivity.getGLRoot().freeze();
    this.mIsActive = true;
    setContentPane(this.mRootPane);
    this.mModel.resume();
    this.mPhotoView.resume();
    GalleryActionBar localGalleryActionBar = this.mActionBar;
    if ((this.mSecureAlbum == null) && (this.mSetPathString != null));
    for (boolean bool1 = true; ; bool1 = false)
    {
      localGalleryActionBar.setDisplayOptions(bool1, false);
      this.mActionBar.addOnMenuVisibilityListener(this.mMenuVisibilityListener);
      refreshBottomControlsWhenReady();
      if ((this.mShowSpinner) && (this.mPhotoView.getFilmMode()))
        this.mActionBar.enableAlbumModeMenu(0, this);
      if (!this.mShowBars)
      {
        this.mActionBar.hide();
        this.mActivity.getGLRoot().setLightsOutMode(true);
      }
      boolean bool2 = GalleryUtils.isEditorAvailable(this.mActivity, "image/*");
      if (bool2 != this.mHaveImageEditor)
      {
        this.mHaveImageEditor = bool2;
        updateMenuOperations();
      }
      this.mHasActivityResult = false;
      this.mRecenterCameraOnResume = true;
      this.mHandler.sendEmptyMessageDelayed(6, 250L);
      return;
    }
  }

  public void onSingleTapUp(int paramInt1, int paramInt2)
  {
    if ((this.mAppBridge != null) && (this.mAppBridge.onSingleTapUp(paramInt1, paramInt2)));
    MediaItem localMediaItem;
    do
    {
      return;
      localMediaItem = this.mModel.getMediaItem(0);
    }
    while ((localMediaItem == null) || (localMediaItem == this.mScreenNailItem));
    int i = localMediaItem.getSupportedOperations();
    label68: int k;
    label80: int l;
    label92: int i1;
    if ((this.mSecureAlbum == null) && ((i & 0x80) != 0))
    {
      j = 1;
      if ((i & 0x2000) == 0)
        break label192;
      k = 1;
      if ((i & 0x4000) == 0)
        break label198;
      l = 1;
      if ((0x10000 & i) == 0)
        break label204;
      i1 = 1;
      if (j != 0)
      {
        label104: int i2 = this.mPhotoView.getWidth();
        int i3 = this.mPhotoView.getHeight();
        if ((12 * Math.abs(paramInt1 - i2 / 2) > i2) || (12 * Math.abs(paramInt2 - i3 / 2) > i3))
          break label210;
      }
    }
    for (int j = 1; j != 0; j = 0)
    {
      playVideo(this.mActivity, localMediaItem.getPlayUri(), localMediaItem.getName());
      return;
      j = 0;
      break label68:
      label192: k = 0;
      break label80:
      label198: l = 0;
      break label92:
      label204: i1 = 0;
      label210: break label104:
    }
    if (l != 0)
    {
      onBackPressed();
      return;
    }
    if (k != 0)
    {
      this.mActivity.getStateManager().finishState(this);
      return;
    }
    if (i1 != 0)
    {
      launchCamera();
      return;
    }
    toggleBars();
  }

  protected void onStateResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mHasActivityResult = true;
    switch (paramInt1)
    {
    default:
    case 4:
    case 2:
    case 3:
    case 1:
    }
    String str;
    int i;
    do
    {
      do
      {
        do
        {
          do
          {
            return;
            setCurrentPhotoByIntent(paramIntent);
            return;
          }
          while (paramInt2 != -1);
          setCurrentPhotoByIntent(paramIntent);
          return;
        }
        while (paramInt2 != -1);
        Context localContext = this.mActivity.getAndroidContext();
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = localContext.getString(2131362320);
        Toast.makeText(localContext, localContext.getString(2131362251, arrayOfObject), 0).show();
        return;
      }
      while (paramIntent == null);
      str = paramIntent.getStringExtra("media-item-path");
      i = paramIntent.getIntExtra("photo-index", 0);
    }
    while (str == null);
    this.mModel.setCurrentPhoto(Path.fromString(str), i);
  }

  public void onUndoBarVisibilityChanged(boolean paramBoolean)
  {
    refreshBottomControlsWhenReady();
  }

  public void onUndoDeleteImage()
  {
    if (this.mDeletePath == null)
      return;
    if (this.mDeleteIsFocus)
      this.mModel.setFocusHintPath(this.mDeletePath);
    this.mMediaSet.removeDeletion(this.mDeletePath);
    this.mDeletePath = null;
  }

  public void playVideo(Activity paramActivity, Uri paramUri, String paramString)
  {
    this.mRecenterCameraOnResume = false;
    try
    {
      paramActivity.startActivityForResult(new Intent("android.intent.action.VIEW").setDataAndType(paramUri, "video/*").putExtra("android.intent.extra.TITLE", paramString).putExtra("treat-up-as-back", true), 5);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Toast.makeText(paramActivity, paramActivity.getString(2131362235), 0).show();
    }
  }

  public void refreshBottomControlsWhenReady()
  {
    if (this.mBottomControls == null)
      return;
    MediaItem localMediaItem = this.mCurrentPhoto;
    if (localMediaItem == null)
    {
      this.mHandler.obtainMessage(8, 0, 0, localMediaItem).sendToTarget();
      return;
    }
    localMediaItem.getPanoramaSupport(this.mRefreshBottomControlsCallback);
  }

  public void setSwipingEnabled(boolean paramBoolean)
  {
    this.mPhotoView.setSwipingEnabled(paramBoolean);
  }

  public boolean switchWithCaptureAnimation(int paramInt)
  {
    return this.mPhotoView.switchWithCaptureAnimation(paramInt);
  }

  public static abstract interface Model extends PhotoView.Model
  {
    public abstract boolean isEmpty();

    public abstract void pause();

    public abstract void resume();

    public abstract void setCurrentPhoto(Path paramPath, int paramInt);
  }

  private class MyDetailsSource
    implements DetailsHelper.DetailsSource
  {
    private MyDetailsSource()
    {
    }

    public MediaDetails getDetails()
    {
      return PhotoPage.this.mModel.getMediaItem(0).getDetails();
    }

    public int setIndex()
    {
      return PhotoPage.this.mModel.getCurrentIndex();
    }

    public int size()
    {
      if (PhotoPage.this.mMediaSet != null)
        return PhotoPage.this.mMediaSet.getMediaItemCount();
      return 1;
    }
  }

  private class MyMenuVisibilityListener
    implements ActionBar.OnMenuVisibilityListener
  {
    private MyMenuVisibilityListener()
    {
    }

    public void onMenuVisibilityChanged(boolean paramBoolean)
    {
      PhotoPage.access$302(PhotoPage.this, paramBoolean);
      PhotoPage.this.refreshHidingMessage();
    }
  }

  private class UpdateProgressListener
    implements StitchingChangeListener
  {
    private UpdateProgressListener()
    {
    }

    private void sendUpdate(Uri paramUri, int paramInt)
    {
      MediaItem localMediaItem = PhotoPage.this.mCurrentPhoto;
      if ((localMediaItem instanceof LocalImage) && (localMediaItem.getContentUri().equals(paramUri)));
      for (int i = 1; ; i = 0)
      {
        if (i != 0)
          PhotoPage.this.mHandler.sendEmptyMessage(paramInt);
        return;
      }
    }

    public void onStitchingProgress(Uri paramUri, int paramInt)
    {
      sendUpdate(paramUri, 13);
    }

    public void onStitchingQueued(Uri paramUri)
    {
      sendUpdate(paramUri, 13);
    }

    public void onStitchingResult(Uri paramUri)
    {
      sendUpdate(paramUri, 11);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.PhotoPage
 * JD-Core Version:    0.5.4
 */