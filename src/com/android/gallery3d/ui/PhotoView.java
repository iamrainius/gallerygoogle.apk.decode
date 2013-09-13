package com.android.gallery3d.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Message;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.RangeArray;

public class PhotoView extends GLView
{
  private static float TRANSITION_SCALE_FACTOR = 0.74F;
  private AccelerateInterpolator mAlphaInterpolator = new AccelerateInterpolator(0.9F);
  private Rect mCameraRect = new Rect();
  private Rect mCameraRelativeFrame = new Rect();
  private boolean mCancelExtraScalingPending;
  private int mCompensation = 0;
  private Context mContext;
  private int mDisplayRotation = 0;
  private EdgeView mEdgeView;
  private boolean mFilmMode = false;
  private boolean mFullScreenCamera;
  private final MyGestureListener mGestureListener;
  private final GestureRecognizer mGestureRecognizer;
  private SynchronizedHandler mHandler;
  private int mHolding;
  private Listener mListener;
  private Model mModel;
  private int mNextBound;
  private StringTexture mNoThumbnailText;
  private final RangeArray<Picture> mPictures = new RangeArray(-3, 3);
  private final int mPlaceholderColor;
  private final PositionController mPositionController;
  private int mPrevBound;
  private ZInterpolator mScaleInterpolator = new ZInterpolator(0.5F);
  private Size[] mSizes = new Size[7];
  private TileImageView mTileView;
  private boolean mTouchBoxDeletable;
  private int mTouchBoxIndex = 2147483647;
  private UndoBarView mUndoBar;
  private int mUndoBarState;
  private int mUndoIndexHint = 2147483647;
  private Texture mVideoPlayIcon;
  private boolean mWantPictureCenterCallbacks = false;

  public PhotoView(AbstractGalleryActivity paramAbstractGalleryActivity)
  {
    this.mTileView = new TileImageView(paramAbstractGalleryActivity);
    addComponent(this.mTileView);
    this.mContext = paramAbstractGalleryActivity.getAndroidContext();
    this.mPlaceholderColor = this.mContext.getResources().getColor(2131296287);
    this.mEdgeView = new EdgeView(this.mContext);
    addComponent(this.mEdgeView);
    this.mUndoBar = new UndoBarView(this.mContext);
    addComponent(this.mUndoBar);
    this.mUndoBar.setVisibility(1);
    this.mUndoBar.setOnClickListener(new GLView.OnClickListener()
    {
      public void onClick(GLView paramGLView)
      {
        PhotoView.this.mListener.onUndoDeleteImage();
        PhotoView.this.hideUndoBar();
      }
    });
    this.mNoThumbnailText = StringTexture.newInstance(this.mContext.getString(2131362192), 20.0F, -1);
    this.mHandler = new MyHandler(paramAbstractGalleryActivity.getGLRoot());
    this.mGestureListener = new MyGestureListener(null);
    this.mGestureRecognizer = new GestureRecognizer(this.mContext, this.mGestureListener);
    this.mPositionController = new PositionController(this.mContext, new PositionController.Listener()
    {
      public void invalidate()
      {
        PhotoView.this.invalidate();
      }

      public boolean isHoldingDelete()
      {
        return (0x4 & PhotoView.this.mHolding) != 0;
      }

      public boolean isHoldingDown()
      {
        return (0x1 & PhotoView.this.mHolding) != 0;
      }

      public void onAbsorb(int paramInt1, int paramInt2)
      {
        PhotoView.this.mEdgeView.onAbsorb(paramInt1, paramInt2);
      }

      public void onPull(int paramInt1, int paramInt2)
      {
        PhotoView.this.mEdgeView.onPull(paramInt1, paramInt2);
      }
    });
    this.mVideoPlayIcon = new ResourceTexture(this.mContext, 2130837654);
    int i = -3;
    if (i > 3)
      label330: return;
    if (i == 0)
      this.mPictures.put(i, new FullPicture());
    while (true)
    {
      ++i;
      break label330:
      this.mPictures.put(i, new ScreenNailPicture(i));
    }
  }

  private static float calculateMoveOutProgress(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt2 - paramInt1;
    if (i < paramInt3)
    {
      int j = paramInt3 / 2 - i / 2;
      if (paramInt1 > j)
        return -(paramInt1 - j) / (paramInt3 - j);
      return (paramInt1 - j) / (-i - j);
    }
    if (paramInt1 > 0)
      return -paramInt1 / paramInt3;
    if (paramInt2 < paramInt3)
      return (paramInt3 - paramInt2) / paramInt3;
    return 0.0F;
  }

  private void captureAnimationDone(int paramInt)
  {
    this.mHolding = (0xFFFFFFFD & this.mHolding);
    if ((paramInt == 1) && (!this.mFilmMode))
    {
      this.mListener.onActionBarAllowed(true);
      this.mListener.onActionBarWanted();
    }
    snapback();
  }

  private void checkFocusSwitching()
  {
    if (!this.mFilmMode);
    do
      return;
    while ((this.mHandler.hasMessages(3)) || (switchPosition() == 0));
    this.mHandler.sendEmptyMessage(3);
  }

  private void checkHideUndoBar(int paramInt)
  {
    this.mUndoBarState = (paramInt | this.mUndoBarState);
    if ((0x1 & this.mUndoBarState) == 0)
      return;
    int i;
    label31: int j;
    label42: int k;
    if ((0x2 & this.mUndoBarState) != 0)
    {
      i = 1;
      if ((0x4 & this.mUndoBarState) == 0)
        break label96;
      j = 1;
      if ((0x8 & this.mUndoBarState) == 0)
        break label101;
      k = 1;
      label55: if ((0x10 & this.mUndoBarState) == 0)
        break label107;
    }
    for (int l = 1; ; l = 0)
    {
      if (((i != 0) && (l != 0)) || (k != 0) || (j != 0));
      hideUndoBar();
      return;
      i = 0;
      break label31:
      label96: j = 0;
      break label42:
      label101: k = 0;
      label107: break label55:
    }
  }

  private void drawLoadingFailMessage(GLCanvas paramGLCanvas)
  {
    StringTexture localStringTexture = this.mNoThumbnailText;
    localStringTexture.draw(paramGLCanvas, -localStringTexture.getWidth() / 2, -localStringTexture.getHeight() / 2);
  }

  private void drawPlaceHolder(GLCanvas paramGLCanvas, Rect paramRect)
  {
    paramGLCanvas.fillRect(paramRect.left, paramRect.top, paramRect.width(), paramRect.height(), this.mPlaceholderColor);
  }

  private void drawVideoPlayIcon(GLCanvas paramGLCanvas, int paramInt)
  {
    int i = paramInt / 6;
    this.mVideoPlayIcon.draw(paramGLCanvas, -i / 2, -i / 2, i, i);
  }

  private static int gapToSide(int paramInt1, int paramInt2)
  {
    return Math.max(0, (paramInt2 - paramInt1) / 2);
  }

  private int getCameraRotation()
  {
    return (360 + (this.mCompensation - this.mDisplayRotation)) % 360;
  }

  private float getOffsetAlpha(float paramFloat)
  {
    float f1 = paramFloat / 0.5F;
    float f2;
    if (f1 > 0.0F)
      f2 = 1.0F - f1;
    while (true)
    {
      return Utils.clamp(f2, 0.03F, 1.0F);
      f2 = 1.0F + f1;
    }
  }

  private int getPanoramaRotation()
  {
    int i;
    if ((this.mContext.getResources().getConfiguration().orientation == 1) && (((this.mDisplayRotation == 90) || (this.mDisplayRotation == 270))))
    {
      i = 1;
      label38: if (this.mDisplayRotation < 180)
        break label73;
    }
    for (int j = 1; j != i; j = 0)
    {
      return (180 + this.mCompensation) % 360;
      i = 0;
      label73: break label38:
    }
    return this.mCompensation;
  }

  private static int getRotated(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 % 180 == 0)
      return paramInt2;
    return paramInt3;
  }

  private float getScrollAlpha(float paramFloat)
  {
    float f = 1.0F;
    if (paramFloat < 0.0F)
      f = this.mAlphaInterpolator.getInterpolation(f - Math.abs(paramFloat));
    return f;
  }

  private float getScrollScale(float paramFloat)
  {
    float f = this.mScaleInterpolator.getInterpolation(Math.abs(paramFloat));
    return 1.0F - f + f * TRANSITION_SCALE_FACTOR;
  }

  private void hideUndoBar()
  {
    this.mHandler.removeMessages(7);
    this.mListener.onCommitDeleteImage();
    this.mUndoBar.animateVisibility(1);
    this.mUndoBarState = 0;
    this.mUndoIndexHint = 2147483647;
    this.mListener.onUndoBarVisibilityChanged(false);
  }

  private static float interpolate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return paramFloat2 + paramFloat1 * (paramFloat1 * (paramFloat3 - paramFloat2));
  }

  private void setPictureSize(int paramInt)
  {
    Picture localPicture = (Picture)this.mPictures.get(paramInt);
    PositionController localPositionController = this.mPositionController;
    Size localSize = localPicture.getSize();
    if ((paramInt == 0) && (localPicture.isCamera()));
    for (Rect localRect = this.mCameraRect; ; localRect = null)
    {
      localPositionController.setImageSize(paramInt, localSize, localRect);
      return;
    }
  }

  private void showUndoBar(boolean paramBoolean)
  {
    this.mHandler.removeMessages(7);
    this.mUndoBarState = 1;
    if (paramBoolean)
      this.mUndoBarState = (0x10 | this.mUndoBarState);
    this.mUndoBar.animateVisibility(0);
    this.mHandler.sendEmptyMessageDelayed(7, 3000L);
    if (this.mListener == null)
      return;
    this.mListener.onUndoBarVisibilityChanged(true);
  }

  private boolean slideToNextPicture()
  {
    if (this.mNextBound <= 0)
      return false;
    switchToNextImage();
    this.mPositionController.startHorizontalSlide();
    return true;
  }

  private boolean slideToPrevPicture()
  {
    if (this.mPrevBound >= 0)
      return false;
    switchToPrevImage();
    this.mPositionController.startHorizontalSlide();
    return true;
  }

  private boolean snapToNeighborImage()
  {
    Rect localRect = this.mPositionController.getPosition(0);
    int i = getWidth();
    int j = i / 5 + gapToSide(localRect.width(), i);
    boolean bool;
    if (i - localRect.right > j)
      bool = slideToNextPicture();
    int k;
    do
    {
      return bool;
      k = localRect.left;
      bool = false;
    }
    while (k <= j);
    return slideToPrevPicture();
  }

  private void snapback()
  {
    if ((0xFFFFFFFB & this.mHolding) != 0);
    do
      return;
    while ((!this.mFilmMode) && (snapToNeighborImage()));
    this.mPositionController.snapback();
  }

  private boolean swipeImages(float paramFloat1, float paramFloat2)
  {
    if (this.mFilmMode);
    boolean bool;
    int i;
    do
    {
      do
      {
        return false;
        PositionController localPositionController = this.mPositionController;
        bool = localPositionController.isAtMinimalScale();
        i = localPositionController.getImageAtEdges();
      }
      while ((!bool) && (Math.abs(paramFloat2) > Math.abs(paramFloat1)) && ((((i & 0x4) == 0) || ((i & 0x8) == 0))));
      if ((paramFloat1 < -300.0F) && (((bool) || ((i & 0x2) != 0))))
        return slideToNextPicture();
    }
    while ((paramFloat1 <= 300.0F) || ((!bool) && ((i & 0x1) == 0)));
    return slideToPrevPicture();
  }

  private void switchFocus()
  {
    if (this.mHolding != 0)
      return;
    switch (switchPosition())
    {
    case 0:
    default:
      return;
    case -1:
      switchToPrevImage();
      return;
    case 1:
    }
    switchToNextImage();
  }

  private int switchPosition()
  {
    Rect localRect1 = this.mPositionController.getPosition(0);
    int i = getWidth() / 2;
    if ((localRect1.left > i) && (this.mPrevBound < 0))
    {
      Rect localRect3 = this.mPositionController.getPosition(-1);
      int k = localRect1.left - i;
      if (i - localRect3.right < k)
        return -1;
    }
    else if ((localRect1.right < i) && (this.mNextBound > 0))
    {
      Rect localRect2 = this.mPositionController.getPosition(1);
      int j = i - localRect1.right;
      if (localRect2.left - i < j)
        return 1;
    }
    return 0;
  }

  private void switchToFirstImage()
  {
    this.mModel.moveTo(0);
  }

  private void switchToHitPicture(int paramInt1, int paramInt2)
  {
    if ((this.mPrevBound < 0) && (this.mPositionController.getPosition(-1).right >= paramInt1))
      slideToPrevPicture();
    do
      return;
    while ((this.mNextBound <= 0) || (this.mPositionController.getPosition(1).left > paramInt1));
    slideToNextPicture();
  }

  private void switchToNextImage()
  {
    this.mModel.moveTo(1 + this.mModel.getCurrentIndex());
  }

  private void switchToPrevImage()
  {
    this.mModel.moveTo(-1 + this.mModel.getCurrentIndex());
  }

  private boolean switchWithCaptureAnimationLocked(int paramInt)
  {
    if (this.mHolding != 0)
      return true;
    if (paramInt == 1)
    {
      if (this.mNextBound <= 0)
        return false;
      if (!this.mFilmMode)
        this.mListener.onActionBarAllowed(false);
      switchToNextImage();
      this.mPositionController.startCaptureAnimationSlide(-1);
    }
    while (true)
    {
      this.mHolding = (0x2 | this.mHolding);
      Message localMessage = this.mHandler.obtainMessage(4, paramInt, 0);
      this.mHandler.sendMessageDelayed(localMessage, 700L);
      return true;
      if (paramInt != -1)
        break;
      if (this.mPrevBound >= 0)
        return false;
      if (this.mFilmMode)
        setFilmMode(false);
      if (this.mModel.getCurrentIndex() > 3)
      {
        switchToFirstImage();
        this.mPositionController.skipToFinalPosition();
        return true;
      }
      switchToFirstImage();
      this.mPositionController.startCaptureAnimationSlide(1);
    }
    return false;
  }

  private void updateActionBar()
  {
    if ((((Picture)this.mPictures.get(0)).isCamera()) && (!this.mFilmMode))
      this.mListener.onActionBarAllowed(false);
    do
    {
      return;
      this.mListener.onActionBarAllowed(true);
    }
    while (!this.mFilmMode);
    this.mListener.onActionBarWanted();
  }

  private void updateCameraRect()
  {
    int i = getWidth();
    int j = getHeight();
    if (this.mCompensation % 180 != 0)
    {
      int i3 = i;
      i = j;
      j = i3;
    }
    int k = this.mCameraRelativeFrame.left;
    int l = this.mCameraRelativeFrame.top;
    int i1 = this.mCameraRelativeFrame.right;
    int i2 = this.mCameraRelativeFrame.bottom;
    switch (this.mCompensation)
    {
    default:
    case 0:
    case 90:
    case 180:
    case 270:
    }
    while (true)
    {
      Log.d("PhotoView", "compensation = " + this.mCompensation + ", CameraRelativeFrame = " + this.mCameraRelativeFrame + ", mCameraRect = " + this.mCameraRect);
      return;
      this.mCameraRect.set(k, l, i1, i2);
      continue;
      this.mCameraRect.set(j - i2, k, j - l, i1);
      continue;
      this.mCameraRect.set(i - i1, j - i2, i - k, j - l);
      continue;
      this.mCameraRect.set(l, i - i1, i2, i - k);
    }
  }

  public boolean canUndo()
  {
    return (0x1 & this.mUndoBarState) != 0;
  }

  public boolean getFilmMode()
  {
    return this.mFilmMode;
  }

  public void notifyDataChange(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    this.mPrevBound = paramInt1;
    this.mNextBound = paramInt2;
    int k;
    if (this.mTouchBoxIndex != 2147483647)
    {
      k = this.mTouchBoxIndex;
      this.mTouchBoxIndex = 2147483647;
    }
    for (int l = 0; ; ++l)
    {
      if (l < 7)
      {
        if (paramArrayOfInt[l] != k)
          continue;
        this.mTouchBoxIndex = (l - 3);
      }
      if ((this.mUndoIndexHint != 2147483647) && (Math.abs(this.mUndoIndexHint - this.mModel.getCurrentIndex()) >= 3))
        hideUndoBar();
      for (int i = -3; ; ++i)
      {
        if (i > 3)
          break label151;
        Picture localPicture = (Picture)this.mPictures.get(i);
        localPicture.reload();
        this.mSizes[(i + 3)] = localPicture.getSize();
      }
    }
    label151: boolean bool1 = this.mPositionController.hasDeletingBox();
    PositionController localPositionController = this.mPositionController;
    boolean bool2;
    if (this.mPrevBound < 0)
    {
      bool2 = true;
      label176: if (this.mNextBound <= 0)
        break label238;
    }
    for (boolean bool3 = true; ; bool3 = false)
    {
      localPositionController.moveBox(paramArrayOfInt, bool2, bool3, this.mModel.isCamera(0), this.mSizes);
      for (int j = -3; ; ++j)
      {
        if (j > 3)
          break label244;
        setPictureSize(j);
      }
      bool2 = false;
      label238: break label176:
    }
    label244: boolean bool4 = this.mPositionController.hasDeletingBox();
    if ((bool1) && (!bool4))
    {
      this.mHandler.removeMessages(6);
      Message localMessage = this.mHandler.obtainMessage(6);
      this.mHandler.sendMessageDelayed(localMessage, 600L);
    }
    invalidate();
  }

  public void notifyImageChange(int paramInt)
  {
    if (paramInt == 0)
      this.mListener.onCurrentImageUpdated();
    ((Picture)this.mPictures.get(paramInt)).reload();
    setPictureSize(paramInt);
    invalidate();
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    this.mTileView.layout(0, 0, i, j);
    this.mEdgeView.layout(0, 0, i, j);
    this.mUndoBar.measure(0, 0);
    this.mUndoBar.layout(0, j - this.mUndoBar.getMeasuredHeight(), i, j);
    GLRoot localGLRoot = getGLRoot();
    int k = localGLRoot.getDisplayRotation();
    int l = localGLRoot.getCompensation();
    if ((this.mDisplayRotation != k) || (this.mCompensation != l))
    {
      this.mDisplayRotation = k;
      this.mCompensation = l;
      for (int i1 = -3; i1 <= 3; ++i1)
      {
        Picture localPicture = (Picture)this.mPictures.get(i1);
        if (!localPicture.isCamera())
          continue;
        localPicture.forceSize();
      }
    }
    updateCameraRect();
    this.mPositionController.setConstrainedFrame(this.mCameraRect);
    if (!paramBoolean)
      return;
    this.mPositionController.setViewSize(getWidth(), getHeight());
  }

  protected boolean onTouch(MotionEvent paramMotionEvent)
  {
    this.mGestureRecognizer.onTouchEvent(paramMotionEvent);
    return true;
  }

  public void pause()
  {
    this.mPositionController.skipAnimation();
    this.mTileView.freeTextures();
    for (int i = -3; i <= 3; ++i)
      ((Picture)this.mPictures.get(i)).setScreenNail(null);
    hideUndoBar();
  }

  protected void render(GLCanvas paramGLCanvas)
  {
    boolean bool;
    if ((!this.mFilmMode) && (((Picture)this.mPictures.get(0)).isCamera()) && (this.mPositionController.isCenter()) && (this.mPositionController.isAtMinimalScale()))
    {
      bool = true;
      if (bool != this.mFullScreenCamera)
      {
        label48: this.mFullScreenCamera = bool;
        this.mListener.onFullScreenChanged(bool);
        if (bool)
          this.mHandler.sendEmptyMessage(8);
      }
      if (!this.mFullScreenCamera)
        break label149;
    }
    for (int k = 0; ; k = 3)
    {
      for (int l = k; ; --l)
      {
        label95: if (l < -k)
          break label207;
        Rect localRect = this.mPositionController.getPosition(l);
        ((Picture)this.mPictures.get(l)).draw(paramGLCanvas, localRect);
      }
      bool = false;
      break label48:
      label149: int i;
      if (this.mPositionController.getFilmRatio() == 0.0F)
      {
        i = 1;
        label163: if ((0x2 & this.mHolding) == 0)
          break label195;
      }
      for (int j = 1; (i != 0) && (j == 0); j = 0)
      {
        k = 1;
        break label95:
        i = 0;
        label195: break label163:
      }
    }
    label207: renderChild(paramGLCanvas, this.mEdgeView);
    renderChild(paramGLCanvas, this.mUndoBar);
    this.mPositionController.advanceAnimation();
    checkFocusSwitching();
  }

  public void resetToFirstPicture()
  {
    this.mModel.moveTo(0);
    setFilmMode(false);
  }

  public void resume()
  {
    this.mTileView.prepareTextures();
    this.mPositionController.skipToFinalPosition();
  }

  public void setFilmMode(boolean paramBoolean)
  {
    int i = 1;
    if (this.mFilmMode == paramBoolean)
      return;
    this.mFilmMode = paramBoolean;
    this.mPositionController.setFilmMode(this.mFilmMode);
    Model localModel1 = this.mModel;
    label39: Model localModel2;
    if (!paramBoolean)
    {
      int j = i;
      localModel1.setNeedFullImage(j);
      localModel2 = this.mModel;
      if (!this.mFilmMode)
        break label89;
    }
    while (true)
    {
      localModel2.setFocusHintDirection(i);
      updateActionBar();
      this.mListener.onFilmModeChanged(paramBoolean);
      return;
      int k = 0;
      break label39:
      label89: i = 0;
    }
  }

  public void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  public void setModel(Model paramModel)
  {
    this.mModel = paramModel;
    this.mTileView.setModel(this.mModel);
  }

  public void setSwipingEnabled(boolean paramBoolean)
  {
    this.mGestureListener.setSwipingEnabled(paramBoolean);
  }

  public void setWantPictureCenterCallbacks(boolean paramBoolean)
  {
    this.mWantPictureCenterCallbacks = paramBoolean;
  }

  public void stopScrolling()
  {
    this.mPositionController.stopScrolling();
  }

  public void switchToImage(int paramInt)
  {
    this.mModel.moveTo(paramInt);
  }

  public boolean switchWithCaptureAnimation(int paramInt)
  {
    GLRoot localGLRoot = getGLRoot();
    if (localGLRoot == null)
      return false;
    localGLRoot.lockRenderThread();
    try
    {
      boolean bool = switchWithCaptureAnimationLocked(paramInt);
      return bool;
    }
    finally
    {
      localGLRoot.unlockRenderThread();
    }
  }

  class FullPicture
    implements PhotoView.Picture
  {
    private boolean mIsCamera;
    private boolean mIsDeletable;
    private boolean mIsPanorama;
    private boolean mIsStaticCamera;
    private boolean mIsVideo;
    private int mLoadingState = 0;
    private int mRotation;
    private PhotoView.Size mSize = new PhotoView.Size();

    FullPicture()
    {
    }

    private void drawTileView(GLCanvas paramGLCanvas, Rect paramRect)
    {
      float f1 = PhotoView.this.mPositionController.getImageScale();
      int i = PhotoView.this.getWidth();
      int j = PhotoView.this.getHeight();
      float f2 = paramRect.exactCenterX();
      float f3 = paramRect.exactCenterY();
      float f4 = 1.0F;
      paramGLCanvas.save(3);
      float f5 = PhotoView.this.mPositionController.getFilmRatio();
      int k;
      label115: int l;
      label143: int i2;
      int i3;
      float f11;
      if ((!this.mIsCamera) && (f5 != 1.0F) && (!((PhotoView.Picture)PhotoView.this.mPictures.get(-1)).isCamera()) && (!PhotoView.this.mPositionController.inOpeningAnimation()))
      {
        k = 1;
        if ((!this.mIsDeletable) || (f5 != 1.0F) || (paramRect.centerY() == j / 2))
          break label382;
        l = 1;
        if (k == 0)
          break label404;
        i2 = paramRect.left;
        i3 = paramRect.right;
        float f7 = Utils.clamp(PhotoView.access$2300(i2, i3, i), -1.0F, 1.0F);
        if (f7 < 0.0F)
        {
          float f8 = PhotoView.this.getScrollScale(f7);
          float f9 = PhotoView.this.getScrollAlpha(f7);
          f4 = PhotoView.access$2600(f5, f8, 1.0F);
          float f10 = PhotoView.access$2600(f5, f9, 1.0F);
          f1 *= f4;
          paramGLCanvas.multiplyAlpha(f10);
          if (i3 - i2 > i)
            break label388;
          f11 = i / 2.0F;
          label256: f2 = PhotoView.access$2600(f5, f11, f2);
        }
      }
      while (true)
      {
        setTileViewPosition(f2, f3, i, j, f1);
        PhotoView.this.renderChild(paramGLCanvas, PhotoView.this.mTileView);
        paramGLCanvas.translate((int)(0.5F + f2), (int)(0.5F + f3));
        int i1 = (int)(0.5F + f4 * Math.min(paramRect.width(), paramRect.height()));
        if (this.mIsVideo)
          PhotoView.this.drawVideoPlayIcon(paramGLCanvas, i1);
        if (this.mLoadingState == 2)
          PhotoView.this.drawLoadingFailMessage(paramGLCanvas);
        paramGLCanvas.restore();
        return;
        k = 0;
        break label115:
        label382: l = 0;
        break label143:
        label388: f11 = f4 * (i3 - i2) / 2.0F;
        break label256:
        label404: if (l == 0)
          continue;
        float f6 = (paramRect.centerY() - j / 2) / j;
        paramGLCanvas.multiplyAlpha(PhotoView.this.getOffsetAlpha(f6));
      }
    }

    private void setTileViewPosition(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, float paramFloat3)
    {
      int i = PhotoView.this.mPositionController.getImageWidth();
      int j = PhotoView.this.mPositionController.getImageHeight();
      int k = (int)(0.5F + (i / 2.0F + (paramInt1 / 2.0F - paramFloat1) / paramFloat3));
      int l = (int)(0.5F + (j / 2.0F + (paramInt2 / 2.0F - paramFloat2) / paramFloat3));
      int i1 = i - k;
      int i2 = j - l;
      int i3;
      switch (this.mRotation)
      {
      default:
        throw new RuntimeException(String.valueOf(this.mRotation));
      case 0:
        i3 = k;
      case 90:
      case 180:
      case 270:
      }
      for (int i4 = l; ; i4 = k)
      {
        while (true)
        {
          PhotoView.this.mTileView.setPosition(i3, i4, paramFloat3, this.mRotation);
          return;
          i3 = l;
          i4 = i1;
          continue;
          i3 = i1;
          i4 = i2;
        }
        i3 = i2;
      }
    }

    private void updateSize()
    {
      if (this.mIsPanorama);
      for (this.mRotation = PhotoView.this.getPanoramaRotation(); ; this.mRotation = PhotoView.this.mModel.getImageRotation(0))
        while (true)
        {
          int i = PhotoView.this.mTileView.mImageWidth;
          int j = PhotoView.this.mTileView.mImageHeight;
          this.mSize.width = PhotoView.access$2000(this.mRotation, i, j);
          this.mSize.height = PhotoView.access$2000(this.mRotation, j, i);
          return;
          if ((!this.mIsCamera) || (this.mIsStaticCamera))
            break;
          this.mRotation = PhotoView.this.getCameraRotation();
        }
    }

    public void draw(GLCanvas paramGLCanvas, Rect paramRect)
    {
      drawTileView(paramGLCanvas, paramRect);
      if ((0xFFFFFFFE & PhotoView.this.mHolding) != 0);
      do
        return;
      while ((!PhotoView.this.mWantPictureCenterCallbacks) || (!PhotoView.this.mPositionController.isCenter()));
      PhotoView.this.mListener.onPictureCenter(this.mIsCamera);
    }

    public void forceSize()
    {
      updateSize();
      PhotoView.this.mPositionController.forceImageSize(0, this.mSize);
    }

    public PhotoView.Size getSize()
    {
      return this.mSize;
    }

    public boolean isCamera()
    {
      return this.mIsCamera;
    }

    public boolean isDeletable()
    {
      return this.mIsDeletable;
    }

    public void reload()
    {
      PhotoView.this.mTileView.notifyModelInvalidated();
      this.mIsCamera = PhotoView.this.mModel.isCamera(0);
      this.mIsPanorama = PhotoView.this.mModel.isPanorama(0);
      this.mIsStaticCamera = PhotoView.this.mModel.isStaticCamera(0);
      this.mIsVideo = PhotoView.this.mModel.isVideo(0);
      this.mIsDeletable = PhotoView.this.mModel.isDeletable(0);
      this.mLoadingState = PhotoView.this.mModel.getLoadingState(0);
      setScreenNail(PhotoView.this.mModel.getScreenNail(0));
      updateSize();
    }

    public void setScreenNail(ScreenNail paramScreenNail)
    {
      PhotoView.this.mTileView.setScreenNail(paramScreenNail);
    }
  }

  public static abstract interface Listener
  {
    public abstract void onActionBarAllowed(boolean paramBoolean);

    public abstract void onActionBarWanted();

    public abstract void onCommitDeleteImage();

    public abstract void onCurrentImageUpdated();

    public abstract void onDeleteImage(Path paramPath, int paramInt);

    public abstract void onFilmModeChanged(boolean paramBoolean);

    public abstract void onFullScreenChanged(boolean paramBoolean);

    public abstract void onPictureCenter(boolean paramBoolean);

    public abstract void onSingleTapUp(int paramInt1, int paramInt2);

    public abstract void onUndoBarVisibilityChanged(boolean paramBoolean);

    public abstract void onUndoDeleteImage();
  }

  public static abstract interface Model extends TileImageView.Model
  {
    public abstract int getCurrentIndex();

    public abstract int getImageRotation(int paramInt);

    public abstract void getImageSize(int paramInt, PhotoView.Size paramSize);

    public abstract int getLoadingState(int paramInt);

    public abstract MediaItem getMediaItem(int paramInt);

    public abstract ScreenNail getScreenNail(int paramInt);

    public abstract boolean isCamera(int paramInt);

    public abstract boolean isDeletable(int paramInt);

    public abstract boolean isPanorama(int paramInt);

    public abstract boolean isStaticCamera(int paramInt);

    public abstract boolean isVideo(int paramInt);

    public abstract void moveTo(int paramInt);

    public abstract void setFocusHintDirection(int paramInt);

    public abstract void setFocusHintPath(Path paramPath);

    public abstract void setNeedFullImage(boolean paramBoolean);
  }

  private class MyGestureListener
    implements GestureRecognizer.Listener
  {
    private float mAccScale;
    private boolean mCanChangeMode;
    private int mDeltaY;
    private boolean mDownInScrolling;
    private boolean mFirstScrollX;
    private boolean mHadFling;
    private boolean mIgnoreScalingGesture;
    private boolean mIgnoreSwipingGesture;
    private boolean mIgnoreUpEvent = false;
    private boolean mModeChanged;
    private boolean mScrolledAfterDown;

    private MyGestureListener()
    {
    }

    private int calculateDeltaY(float paramFloat)
    {
      if (PhotoView.this.mTouchBoxDeletable)
        return (int)(paramFloat + 0.5F);
      int i = PhotoView.this.getHeight();
      float f1 = 0.15F * i;
      float f2;
      if (Math.abs(paramFloat) >= i)
        if (paramFloat > 0.0F)
          f2 = f1;
      while (true)
      {
        return (int)(f2 + 0.5F);
        f2 = -f1;
        continue;
        f2 = f1 * FloatMath.sin(1.570796F * (paramFloat / i));
      }
    }

    private void deleteAfterAnimation(int paramInt)
    {
      MediaItem localMediaItem = PhotoView.this.mModel.getMediaItem(PhotoView.this.mTouchBoxIndex);
      if (localMediaItem == null)
        return;
      PhotoView.this.mListener.onCommitDeleteImage();
      PhotoView.access$3602(PhotoView.this, PhotoView.this.mModel.getCurrentIndex() + PhotoView.this.mTouchBoxIndex);
      PhotoView.access$376(PhotoView.this, 4);
      Message localMessage = PhotoView.this.mHandler.obtainMessage(5);
      localMessage.obj = localMediaItem.getPath();
      localMessage.arg1 = PhotoView.this.mTouchBoxIndex;
      PhotoView.this.mHandler.sendMessageDelayed(localMessage, paramInt);
    }

    private boolean flingImages(float paramFloat1, float paramFloat2)
    {
      int i = (int)(paramFloat1 + 0.5F);
      int j = (int)(paramFloat2 + 0.5F);
      boolean bool2;
      if (!PhotoView.this.mFilmMode)
        bool2 = PhotoView.this.mPositionController.flingPage(i, j);
      boolean bool3;
      do
      {
        int k;
        do
        {
          boolean bool1;
          do
          {
            return bool2;
            if (Math.abs(paramFloat1) > Math.abs(paramFloat2))
              return PhotoView.this.mPositionController.flingFilmX(i);
            bool1 = PhotoView.this.mFilmMode;
            bool2 = false;
          }
          while (!bool1);
          k = PhotoView.this.mTouchBoxIndex;
          bool2 = false;
        }
        while (k == 2147483647);
        bool3 = PhotoView.this.mTouchBoxDeletable;
        bool2 = false;
      }
      while (!bool3);
      int l = GalleryUtils.dpToPixel(4000);
      int i1 = GalleryUtils.dpToPixel(2500);
      int i2 = PhotoView.this.mPositionController.getPosition(PhotoView.this.mTouchBoxIndex).centerY();
      int i6;
      label186: int i7;
      if ((Math.abs(j) > i1) && (Math.abs(j) > Math.abs(i)))
        if (j > 0)
        {
          i6 = 1;
          if (i2 <= PhotoView.this.getHeight() / 2)
            break label310;
          i7 = 1;
          label203: if (i6 != i7)
            break label316;
        }
      for (int i3 = 1; ; i3 = 0)
      {
        bool2 = false;
        if (i3 != 0);
        int i4 = Math.min(j, l);
        int i5 = PhotoView.this.mPositionController.flingFilmY(PhotoView.this.mTouchBoxIndex, i4);
        bool2 = false;
        if (i5 >= 0);
        PositionController localPositionController = PhotoView.this.mPositionController;
        boolean bool4 = false;
        if (i4 < 0)
          bool4 = true;
        localPositionController.setPopFromTop(bool4);
        deleteAfterAnimation(i5);
        PhotoView.access$3302(PhotoView.this, 2147483647);
        return true;
        i6 = 0;
        break label186:
        label310: i7 = 0;
        label316: break label203:
      }
    }

    private void startExtraScalingIfNeeded()
    {
      if (PhotoView.this.mCancelExtraScalingPending)
        return;
      PhotoView.this.mHandler.sendEmptyMessageDelayed(2, 700L);
      PhotoView.this.mPositionController.setExtraScalingRange(true);
      PhotoView.access$702(PhotoView.this, true);
    }

    private void stopExtraScalingIfNeeded()
    {
      if (!PhotoView.this.mCancelExtraScalingPending)
        return;
      PhotoView.this.mHandler.removeMessages(2);
      PhotoView.this.mPositionController.setExtraScalingRange(false);
      PhotoView.access$702(PhotoView.this, false);
    }

    public boolean onDoubleTap(float paramFloat1, float paramFloat2)
    {
      if (this.mIgnoreSwipingGesture)
        return true;
      if (((PhotoView.Picture)PhotoView.this.mPictures.get(0)).isCamera())
        return false;
      PositionController localPositionController = PhotoView.this.mPositionController;
      float f = localPositionController.getImageScale();
      this.mIgnoreUpEvent = true;
      if ((f <= 0.75F) || (localPositionController.isAtMinimalScale()))
        localPositionController.zoomIn(paramFloat1, paramFloat2, Math.max(1.0F, 1.5F * f));
      while (true)
      {
        return true;
        localPositionController.resetToFullView();
      }
    }

    public void onDown(float paramFloat1, float paramFloat2)
    {
      PhotoView.this.checkHideUndoBar(4);
      this.mDeltaY = 0;
      this.mModeChanged = false;
      if (this.mIgnoreSwipingGesture)
        return;
      PhotoView.access$376(PhotoView.this, 1);
      if ((PhotoView.this.mFilmMode) && (PhotoView.this.mPositionController.isScrolling()))
      {
        this.mDownInScrolling = true;
        PhotoView.this.mPositionController.stopScrolling();
      }
      while (true)
      {
        this.mHadFling = false;
        this.mScrolledAfterDown = false;
        if (!PhotoView.this.mFilmMode)
          break label216;
        int i = (int)(paramFloat1 + 0.5F);
        int j = (int)(paramFloat2 + 0.5F);
        PhotoView.access$3302(PhotoView.this, PhotoView.this.mPositionController.hitTest(i, j));
        if ((PhotoView.this.mTouchBoxIndex >= PhotoView.this.mPrevBound) && (PhotoView.this.mTouchBoxIndex <= PhotoView.this.mNextBound))
          break;
        PhotoView.access$3302(PhotoView.this, 2147483647);
        return;
        this.mDownInScrolling = false;
      }
      PhotoView.access$3402(PhotoView.this, ((PhotoView.Picture)PhotoView.this.mPictures.get(PhotoView.this.mTouchBoxIndex)).isDeletable());
      return;
      label216: PhotoView.access$3302(PhotoView.this, 2147483647);
    }

    public boolean onFling(float paramFloat1, float paramFloat2)
    {
      if (this.mIgnoreSwipingGesture);
      do
        return true;
      while (this.mModeChanged);
      if (PhotoView.this.swipeImages(paramFloat1, paramFloat2) != 0)
        this.mIgnoreUpEvent = true;
      while (true)
      {
        this.mHadFling = true;
        return true;
        flingImages(paramFloat1, paramFloat2);
      }
    }

    public boolean onScale(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      if (this.mIgnoreSwipingGesture);
      do
        return true;
      while ((this.mIgnoreScalingGesture) || (this.mModeChanged));
      if ((Float.isNaN(paramFloat3)) || (Float.isInfinite(paramFloat3)))
        return false;
      int i = PhotoView.this.mPositionController.scaleBy(paramFloat3, paramFloat1, paramFloat2);
      this.mAccScale = (paramFloat3 * this.mAccScale);
      if ((this.mAccScale < 0.97F) || (this.mAccScale > 1.03F));
      for (int j = 1; (this.mCanChangeMode) && (j != 0) && ((((i < 0) && (!PhotoView.this.mFilmMode)) || ((i > 0) && (PhotoView.this.mFilmMode)))); j = 0)
      {
        stopExtraScalingIfNeeded();
        PhotoView.access$372(PhotoView.this, -2);
        PhotoView localPhotoView = PhotoView.this;
        boolean bool1 = PhotoView.this.mFilmMode;
        boolean bool2 = false;
        if (!bool1)
          bool2 = true;
        localPhotoView.setFilmMode(bool2);
        onScaleEnd();
        this.mModeChanged = true;
        return true;
      }
      if (i != 0)
      {
        startExtraScalingIfNeeded();
        return true;
      }
      stopExtraScalingIfNeeded();
      return true;
    }

    public boolean onScaleBegin(float paramFloat1, float paramFloat2)
    {
      if (this.mIgnoreSwipingGesture);
      do
      {
        return true;
        this.mIgnoreScalingGesture = ((PhotoView.Picture)PhotoView.this.mPictures.get(0)).isCamera();
      }
      while (this.mIgnoreScalingGesture);
      PhotoView.this.mPositionController.beginScale(paramFloat1, paramFloat2);
      if ((PhotoView.this.mFilmMode) || (PhotoView.this.mPositionController.isAtMinimalScale()));
      for (int i = 1; ; i = 0)
      {
        this.mCanChangeMode = i;
        this.mAccScale = 1.0F;
        return true;
      }
    }

    public void onScaleEnd()
    {
      if (this.mIgnoreSwipingGesture);
      do
        return;
      while ((this.mIgnoreScalingGesture) || (this.mModeChanged));
      PhotoView.this.mPositionController.endScale();
    }

    public boolean onScroll(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      if (this.mIgnoreSwipingGesture);
      int i;
      int j;
      label89: int k;
      int l;
      do
      {
        do
        {
          return true;
          if (!this.mScrolledAfterDown)
          {
            this.mScrolledAfterDown = true;
            if (Math.abs(paramFloat1) <= Math.abs(paramFloat2))
              break label89;
          }
          for (int i1 = 1; ; i1 = 0)
          {
            this.mFirstScrollX = i1;
            i = (int)(0.5F + -paramFloat1);
            j = (int)(0.5F + -paramFloat2);
            if (!PhotoView.this.mFilmMode)
              break label156;
            if (!this.mFirstScrollX)
              break;
            PhotoView.this.mPositionController.scrollFilmX(i);
            return true;
          }
        }
        while (PhotoView.this.mTouchBoxIndex == 2147483647);
        k = calculateDeltaY(paramFloat4);
        l = k - this.mDeltaY;
      }
      while (l == 0);
      PhotoView.this.mPositionController.scrollFilmY(PhotoView.this.mTouchBoxIndex, l);
      this.mDeltaY = k;
      return true;
      label156: PhotoView.this.mPositionController.scrollPage(i, j);
      return true;
    }

    public boolean onSingleTapUp(float paramFloat1, float paramFloat2)
    {
      if ((Build.VERSION.SDK_INT < 14) && ((0x1 & PhotoView.this.mHolding) == 0));
      do
      {
        return true;
        PhotoView.access$372(PhotoView.this, -2);
        if ((!PhotoView.this.mFilmMode) || (this.mDownInScrolling))
          continue;
        PhotoView.this.switchToHitPicture((int)(paramFloat1 + 0.5F), (int)(paramFloat2 + 0.5F));
        MediaItem localMediaItem = PhotoView.this.mModel.getMediaItem(0);
        int i = 0;
        if (localMediaItem != null)
          i = localMediaItem.getSupportedOperations();
        if ((0x8000 & i) != 0)
          continue;
        PhotoView.this.setFilmMode(false);
        this.mIgnoreUpEvent = true;
        return true;
      }
      while (PhotoView.this.mListener == null);
      Matrix localMatrix1 = PhotoView.this.getGLRoot().getCompensationMatrix();
      Matrix localMatrix2 = new Matrix();
      localMatrix1.invert(localMatrix2);
      float[] arrayOfFloat = { paramFloat1, paramFloat2 };
      localMatrix2.mapPoints(arrayOfFloat);
      PhotoView.this.mListener.onSingleTapUp((int)(0.5F + arrayOfFloat[0]), (int)(0.5F + arrayOfFloat[1]));
      return true;
    }

    public void onUp()
    {
      if (this.mIgnoreSwipingGesture);
      do
      {
        return;
        PhotoView.access$372(PhotoView.this, -2);
        PhotoView.this.mEdgeView.onRelease();
        int j;
        PositionController localPositionController;
        if ((PhotoView.this.mFilmMode) && (this.mScrolledAfterDown) && (!this.mFirstScrollX) && (PhotoView.this.mTouchBoxIndex != 2147483647))
        {
          Rect localRect = PhotoView.this.mPositionController.getPosition(PhotoView.this.mTouchBoxIndex);
          int i = PhotoView.this.getHeight();
          if (Math.abs(localRect.centerY() - 0.5F * i) > 0.4F * i)
          {
            j = PhotoView.this.mPositionController.flingFilmY(PhotoView.this.mTouchBoxIndex, 0);
            if (j >= 0)
            {
              localPositionController = PhotoView.this.mPositionController;
              if (localRect.centerY() >= 0.5F * i)
                break label191;
            }
          }
        }
        for (boolean bool = true; ; bool = false)
        {
          localPositionController.setPopFromTop(bool);
          deleteAfterAnimation(j);
          if (!this.mIgnoreUpEvent)
            break;
          this.mIgnoreUpEvent = false;
          label191: return;
        }
      }
      while ((PhotoView.this.mFilmMode) && (!this.mHadFling) && (this.mFirstScrollX) && (PhotoView.this.snapToNeighborImage() != 0));
      PhotoView.this.snapback();
    }

    public void setSwipingEnabled(boolean paramBoolean)
    {
      if (!paramBoolean);
      for (int i = 1; ; i = 0)
      {
        this.mIgnoreSwipingGesture = i;
        return;
      }
    }
  }

  class MyHandler extends SynchronizedHandler
  {
    public MyHandler(GLRoot arg2)
    {
      super(localGLRoot);
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        throw new AssertionError(paramMessage.what);
      case 2:
        PhotoView.this.mGestureRecognizer.cancelScale();
        PhotoView.this.mPositionController.setExtraScalingRange(false);
        PhotoView.access$702(PhotoView.this, false);
      case 3:
      case 4:
      case 5:
      case 6:
        do
        {
          return;
          PhotoView.this.switchFocus();
          return;
          PhotoView.this.captureAnimationDone(paramMessage.arg1);
          return;
          PhotoView.this.mListener.onDeleteImage((Path)paramMessage.obj, paramMessage.arg1);
          PhotoView.this.mHandler.removeMessages(6);
          Message localMessage = PhotoView.this.mHandler.obtainMessage(6);
          PhotoView.this.mHandler.sendMessageDelayed(localMessage, 2000L);
          int i = 1 + (PhotoView.this.mNextBound - PhotoView.this.mPrevBound);
          if ((i == 2) && (((PhotoView.this.mModel.isCamera(PhotoView.this.mNextBound)) || (PhotoView.this.mModel.isCamera(PhotoView.this.mPrevBound)))))
            --i;
          PhotoView localPhotoView = PhotoView.this;
          if (i <= 1);
          for (boolean bool = true; ; bool = false)
          {
            localPhotoView.showUndoBar(bool);
            return;
          }
        }
        while (PhotoView.this.mHandler.hasMessages(5));
        PhotoView.access$372(PhotoView.this, -5);
        PhotoView.this.snapback();
        return;
      case 7:
        PhotoView.this.checkHideUndoBar(2);
        return;
      case 8:
      }
      PhotoView.this.checkHideUndoBar(8);
    }
  }

  private static abstract interface Picture
  {
    public abstract void draw(GLCanvas paramGLCanvas, Rect paramRect);

    public abstract void forceSize();

    public abstract PhotoView.Size getSize();

    public abstract boolean isCamera();

    public abstract boolean isDeletable();

    public abstract void reload();

    public abstract void setScreenNail(ScreenNail paramScreenNail);
  }

  private class ScreenNailPicture
    implements PhotoView.Picture
  {
    private int mIndex;
    private boolean mIsCamera;
    private boolean mIsDeletable;
    private boolean mIsPanorama;
    private boolean mIsStaticCamera;
    private boolean mIsVideo;
    private int mLoadingState = 0;
    private int mRotation;
    private ScreenNail mScreenNail;
    private PhotoView.Size mSize = new PhotoView.Size();

    public ScreenNailPicture(int arg2)
    {
      int i;
      this.mIndex = i;
    }

    private boolean isScreenNailAnimating()
    {
      return (this.mScreenNail instanceof TiledScreenNail) && (((TiledScreenNail)this.mScreenNail).isAnimating());
    }

    private void updateSize()
    {
      if (this.mIsPanorama)
      {
        this.mRotation = PhotoView.this.getPanoramaRotation();
        label18: if (this.mScreenNail == null)
          break label157;
        this.mSize.width = this.mScreenNail.getWidth();
        this.mSize.height = this.mScreenNail.getHeight();
      }
      while (true)
      {
        int i = this.mSize.width;
        int j = this.mSize.height;
        this.mSize.width = PhotoView.access$2000(this.mRotation, i, j);
        this.mSize.height = PhotoView.access$2000(this.mRotation, j, i);
        return;
        if ((this.mIsCamera) && (!this.mIsStaticCamera))
          this.mRotation = PhotoView.this.getCameraRotation();
        this.mRotation = PhotoView.this.mModel.getImageRotation(this.mIndex);
        break label18:
        label157: PhotoView.this.mModel.getImageSize(this.mIndex, this.mSize);
      }
    }

    public void draw(GLCanvas paramGLCanvas, Rect paramRect)
    {
      if (this.mScreenNail == null)
      {
        if ((this.mIndex >= PhotoView.this.mPrevBound) && (this.mIndex <= PhotoView.this.mNextBound))
          PhotoView.this.drawPlaceHolder(paramGLCanvas, paramRect);
        return;
      }
      int i = PhotoView.this.getWidth();
      int j = PhotoView.this.getHeight();
      if ((paramRect.left >= i) || (paramRect.right <= 0) || (paramRect.top >= j) || (paramRect.bottom <= 0))
      {
        this.mScreenNail.noDraw();
        return;
      }
      float f1 = PhotoView.this.mPositionController.getFilmRatio();
      int k;
      label154: int l;
      if ((this.mIndex > 0) && (f1 != 1.0F) && (!((PhotoView.Picture)PhotoView.this.mPictures.get(0)).isCamera()))
      {
        k = 1;
        if ((!this.mIsDeletable) || (f1 != 1.0F) || (paramRect.centerY() == j / 2))
          break label466;
        l = 1;
        label182: if (k == 0)
          break label472;
        int i1 = (int)(0.5F + PhotoView.access$2600(f1, i / 2, paramRect.centerX()));
        label207: int i3 = paramRect.centerY();
        paramGLCanvas.save(3);
        paramGLCanvas.translate(i1, i3);
        if (k == 0)
          break label481;
        float f3 = Utils.clamp((i / 2 - paramRect.centerX()) / i, -1.0F, 1.0F);
        float f4 = PhotoView.this.getScrollAlpha(f3);
        float f5 = PhotoView.this.getScrollScale(f3);
        float f6 = PhotoView.access$2600(f1, f4, 1.0F);
        float f7 = PhotoView.access$2600(f1, f5, 1.0F);
        paramGLCanvas.multiplyAlpha(f6);
        paramGLCanvas.scale(f7, f7, 1.0F);
      }
      while (true)
      {
        if (this.mRotation != 0)
          paramGLCanvas.rotate(this.mRotation, 0.0F, 0.0F, 1.0F);
        int i4 = PhotoView.access$2000(this.mRotation, paramRect.width(), paramRect.height());
        int i5 = PhotoView.access$2000(this.mRotation, paramRect.height(), paramRect.width());
        this.mScreenNail.draw(paramGLCanvas, -i4 / 2, -i5 / 2, i4, i5);
        if (isScreenNailAnimating())
          PhotoView.this.invalidate();
        int i6 = Math.min(i4, i5);
        if (this.mIsVideo)
          PhotoView.this.drawVideoPlayIcon(paramGLCanvas, i6);
        if (this.mLoadingState == 2)
          PhotoView.this.drawLoadingFailMessage(paramGLCanvas);
        paramGLCanvas.restore();
        return;
        k = 0;
        break label154:
        label466: l = 0;
        break label182:
        label472: int i2 = paramRect.centerX();
        break label207:
        label481: if (l == 0)
          continue;
        float f2 = (paramRect.centerY() - j / 2) / j;
        paramGLCanvas.multiplyAlpha(PhotoView.this.getOffsetAlpha(f2));
      }
    }

    public void forceSize()
    {
      updateSize();
      PhotoView.this.mPositionController.forceImageSize(this.mIndex, this.mSize);
    }

    public PhotoView.Size getSize()
    {
      return this.mSize;
    }

    public boolean isCamera()
    {
      return this.mIsCamera;
    }

    public boolean isDeletable()
    {
      return this.mIsDeletable;
    }

    public void reload()
    {
      this.mIsCamera = PhotoView.this.mModel.isCamera(this.mIndex);
      this.mIsPanorama = PhotoView.this.mModel.isPanorama(this.mIndex);
      this.mIsStaticCamera = PhotoView.this.mModel.isStaticCamera(this.mIndex);
      this.mIsVideo = PhotoView.this.mModel.isVideo(this.mIndex);
      this.mIsDeletable = PhotoView.this.mModel.isDeletable(this.mIndex);
      this.mLoadingState = PhotoView.this.mModel.getLoadingState(this.mIndex);
      setScreenNail(PhotoView.this.mModel.getScreenNail(this.mIndex));
      updateSize();
    }

    public void setScreenNail(ScreenNail paramScreenNail)
    {
      this.mScreenNail = paramScreenNail;
    }
  }

  public static class Size
  {
    public int height;
    public int width;
  }

  private static class ZInterpolator
  {
    private float focalLength;

    public ZInterpolator(float paramFloat)
    {
      this.focalLength = paramFloat;
    }

    public float getInterpolation(float paramFloat)
    {
      return (1.0F - this.focalLength / (paramFloat + this.focalLength)) / (1.0F - this.focalLength / (1.0F + this.focalLength));
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.PhotoView
 * JD-Core Version:    0.5.4
 */