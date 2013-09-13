package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.HistoryAdapter;
import com.android.gallery3d.filtershow.ImageStateAdapter;
import com.android.gallery3d.filtershow.PanelController;
import com.android.gallery3d.filtershow.cache.ImageLoader;
import com.android.gallery3d.filtershow.filters.ImageFilter;
import com.android.gallery3d.filtershow.presets.ImagePreset;
import com.android.gallery3d.filtershow.ui.SliderController;
import com.android.gallery3d.filtershow.ui.SliderListener;
import java.io.File;

public class ImageShow extends View
  implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, SeekBar.OnSeekBarChangeListener, SliderListener
{
  private static int UNVEIL_HORIZONTAL;
  private static int UNVEIL_VERTICAL;
  private static int mBackgroundColor;
  private static String mOriginalText;
  private static int mOriginalTextMargin;
  private static int mOriginalTextSize;
  protected static int mTextPadding;
  protected static int mTextSize = 24;
  private final boolean USE_BACKGROUND_IMAGE = false;
  private final boolean USE_SLIDER_GESTURE = false;
  private FilterShowActivity mActivity = null;
  private Bitmap mBackgroundImage = null;
  private PanelController mController = null;
  private ImageFilter mCurrentFilter = null;
  private boolean mDirtyGeometry = false;
  private Bitmap mFilteredImage = null;
  private Bitmap mFiltersOnlyImage = null;
  private Bitmap mGeometryOnlyImage = null;
  private GestureDetector mGestureDetector = null;
  private final Handler mHandler = new Handler();
  private HistoryAdapter mHistoryAdapter = null;
  private Rect mImageBounds = null;
  protected ImagePreset mImageFiltersOnlyPreset = null;
  protected ImagePreset mImageGeometryOnlyPreset = null;
  protected ImageLoader mImageLoader = null;
  protected ImagePreset mImagePreset = null;
  private ImageStateAdapter mImageStateAdapter = null;
  private boolean mImportantToast = false;
  protected Paint mPaint = new Paint();
  private SeekBar mSeekBar = null;
  private boolean mShowControls = false;
  private boolean mShowOriginal = false;
  private int mShowOriginalDirection = 0;
  private boolean mShowToast = false;
  protected SliderController mSliderController = new SliderController();
  private String mToast = null;
  private int mTouchDownX = 0;
  private int mTouchDownY = 0;
  private boolean mTouchShowOriginal = false;
  private long mTouchShowOriginalDate = 0L;
  private final long mTouchShowOriginalDelayMax = 300L;
  private final long mTouchShowOriginalDelayMin = 200L;
  protected float mTouchX = 0.0F;
  protected float mTouchY = 0.0F;

  static
  {
    mTextPadding = 20;
    mBackgroundColor = -65536;
    UNVEIL_HORIZONTAL = 1;
    UNVEIL_VERTICAL = 2;
    mOriginalTextMargin = 8;
    mOriginalTextSize = 26;
    mOriginalText = "Original";
  }

  public ImageShow(Context paramContext)
  {
    super(paramContext);
    this.mHistoryAdapter = new HistoryAdapter(paramContext, 2130968598, 2131558489);
    setupGestureDetector(paramContext);
    this.mActivity = ((FilterShowActivity)paramContext);
  }

  public ImageShow(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mHistoryAdapter = new HistoryAdapter(paramContext, 2130968598, 2131558489);
    this.mImageStateAdapter = new ImageStateAdapter(paramContext, 2130968599);
    setupGestureDetector(paramContext);
    this.mActivity = ((FilterShowActivity)paramContext);
  }

  private void imageSizeChanged(Bitmap paramBitmap)
  {
    if ((paramBitmap == null) || (getImagePreset() == null));
    float f1;
    float f2;
    RectF localRectF1;
    do
    {
      return;
      f1 = paramBitmap.getWidth();
      f2 = paramBitmap.getHeight();
      localRectF1 = getImagePreset().mGeoData.getPhotoBounds();
    }
    while ((f1 == localRectF1.width()) && (f2 == localRectF1.height()));
    RectF localRectF2 = new RectF(0.0F, 0.0F, f1, f2);
    getImagePreset().mGeoData.setPhotoBounds(localRectF2);
    getImagePreset().mGeoData.setCropBounds(localRectF2);
    setDirtyGeometryFlag();
  }

  private int parameterToUI(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return paramInt4 * (paramInt1 - paramInt2) / (paramInt3 - paramInt2);
  }

  public static void setDefaultBackgroundColor(int paramInt)
  {
    mBackgroundColor = paramInt;
  }

  private void setDirtyGeometryFlag()
  {
    this.mDirtyGeometry = true;
  }

  public static void setOriginalText(String paramString)
  {
    mOriginalText = paramString;
  }

  public static void setOriginalTextMargin(int paramInt)
  {
    mOriginalTextMargin = paramInt;
  }

  public static void setOriginalTextSize(int paramInt)
  {
    mOriginalTextSize = paramInt;
  }

  public static void setTextPadding(int paramInt)
  {
    mTextPadding = paramInt;
  }

  public static void setTextSize(int paramInt)
  {
    mTextSize = paramInt;
  }

  private int uiToParameter(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return paramInt2 + paramInt1 * (paramInt3 - paramInt2) / paramInt4;
  }

  protected void clearDirtyGeometryFlag()
  {
    this.mDirtyGeometry = false;
  }

  public void defaultDrawImage(Canvas paramCanvas)
  {
    drawImage(paramCanvas, getFilteredImage());
    drawPartialImage(paramCanvas, getGeometryOnlyImage());
  }

  public void drawBackground(Canvas paramCanvas)
  {
    paramCanvas.drawColor(mBackgroundColor);
  }

  public void drawImage(Canvas paramCanvas, Bitmap paramBitmap)
  {
    if (paramBitmap == null)
      return;
    Rect localRect1 = new Rect(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
    float f1 = GeometryMath.scale(paramBitmap.getWidth(), paramBitmap.getHeight(), getWidth(), getHeight());
    float f2 = f1 * paramBitmap.getWidth();
    float f3 = f1 * paramBitmap.getHeight();
    float f4 = (getHeight() - f3) / 2.0F;
    float f5 = (getWidth() - f2) / 2.0F;
    Rect localRect2 = new Rect((int)f5, (int)f4, (int)(f2 + f5), (int)(f3 + f4));
    this.mImageBounds = localRect2;
    paramCanvas.drawBitmap(paramBitmap, localRect1, localRect2, this.mPaint);
  }

  public void drawPartialImage(Canvas paramCanvas, Bitmap paramBitmap)
  {
    if (!this.mTouchShowOriginal)
      return;
    paramCanvas.save();
    label55: int i;
    int j;
    label90: Paint localPaint;
    if (paramBitmap != null)
    {
      if (this.mShowOriginalDirection == 0)
      {
        if (this.mTouchY - this.mTouchDownY <= this.mTouchX - this.mTouchDownX)
          break label339;
        this.mShowOriginalDirection = UNVEIL_VERTICAL;
      }
      if (this.mShowOriginalDirection != UNVEIL_VERTICAL)
        break label349;
      i = this.mImageBounds.width();
      j = (int)(this.mTouchY - this.mImageBounds.top);
      paramCanvas.clipRect(new Rect(this.mImageBounds.left, this.mImageBounds.top, i + this.mImageBounds.left, j + this.mImageBounds.top));
      drawImage(paramCanvas, paramBitmap);
      localPaint = new Paint();
      localPaint.setColor(-16777216);
      if (this.mShowOriginalDirection != UNVEIL_VERTICAL)
        break label377;
      paramCanvas.drawLine(this.mImageBounds.left, this.mTouchY - 1.0F, this.mImageBounds.right, this.mTouchY - 1.0F, localPaint);
    }
    while (true)
    {
      Rect localRect = new Rect();
      localPaint.setTextSize(mOriginalTextSize);
      localPaint.getTextBounds(mOriginalText, 0, mOriginalText.length(), localRect);
      localPaint.setColor(-16777216);
      paramCanvas.drawText(mOriginalText, 1 + (this.mImageBounds.left + mOriginalTextMargin), 1 + (this.mImageBounds.top + localRect.height() + mOriginalTextMargin), localPaint);
      localPaint.setColor(-1);
      paramCanvas.drawText(mOriginalText, this.mImageBounds.left + mOriginalTextMargin, this.mImageBounds.top + localRect.height() + mOriginalTextMargin, localPaint);
      paramCanvas.restore();
      return;
      label339: this.mShowOriginalDirection = UNVEIL_HORIZONTAL;
      break label55:
      label349: i = (int)(this.mTouchX - this.mImageBounds.left);
      j = this.mImageBounds.height();
      break label90:
      label377: paramCanvas.drawLine(this.mTouchX - 1.0F, this.mImageBounds.top, this.mTouchX - 1.0F, this.mImageBounds.bottom, localPaint);
    }
  }

  public void drawToast(Canvas paramCanvas)
  {
    Paint localPaint;
    int i;
    int j;
    if ((this.mShowToast) && (this.mToast != null))
    {
      localPaint = new Paint();
      localPaint.setTextSize(128.0F);
      float f = localPaint.measureText(this.mToast);
      i = (int)((getWidth() - f) / 2.0F);
      j = (int)(getHeight() / 3.0F);
      localPaint.setARGB(255, 0, 0, 0);
      paramCanvas.drawText(this.mToast, i - 2, j - 2, localPaint);
      paramCanvas.drawText(this.mToast, i - 2, j, localPaint);
      paramCanvas.drawText(this.mToast, i, j - 2, localPaint);
      paramCanvas.drawText(this.mToast, i + 2, j + 2, localPaint);
      paramCanvas.drawText(this.mToast, i + 2, j, localPaint);
      paramCanvas.drawText(this.mToast, i, j + 2, localPaint);
      if (!this.mImportantToast)
        break label213;
      localPaint.setARGB(255, 200, 0, 0);
    }
    while (true)
    {
      paramCanvas.drawText(this.mToast, i, j, localPaint);
      return;
      label213: localPaint.setARGB(255, 255, 255, 255);
    }
  }

  public ImageFilter getCurrentFilter()
  {
    return this.mCurrentFilter;
  }

  protected boolean getDirtyGeometryFlag()
  {
    return this.mDirtyGeometry;
  }

  public Rect getDisplayedImageBounds()
  {
    return this.mImageBounds;
  }

  public Bitmap getFilteredImage()
  {
    return this.mFilteredImage;
  }

  public Bitmap getFiltersOnlyImage()
  {
    return this.mFiltersOnlyImage;
  }

  protected GeometryMetadata getGeometry()
  {
    return new GeometryMetadata(getImagePreset().mGeoData);
  }

  public Bitmap getGeometryOnlyImage()
  {
    return this.mGeometryOnlyImage;
  }

  public HistoryAdapter getHistory()
  {
    return this.mHistoryAdapter;
  }

  public Rect getImageBounds()
  {
    Rect localRect = new Rect();
    getImagePreset().mGeoData.getPhotoBounds().roundOut(localRect);
    return localRect;
  }

  public ImagePreset getImagePreset()
  {
    return this.mImagePreset;
  }

  public ArrayAdapter getImageStateAdapter()
  {
    return this.mImageStateAdapter;
  }

  public PanelController getPanelController()
  {
    return this.mController;
  }

  public boolean hasModifications()
  {
    if (getImagePreset() == null)
      return false;
    return getImagePreset().hasModifications();
  }

  public void imageLoaded()
  {
    updateImage();
    invalidate();
  }

  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public boolean onDoubleTapEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public boolean onDown(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public void onDraw(Canvas paramCanvas)
  {
    drawBackground(paramCanvas);
    requestFilteredImages();
    defaultDrawImage(paramCanvas);
    if ((showTitle()) && (getImagePreset() != null))
    {
      this.mPaint.setARGB(200, 0, 0, 0);
      this.mPaint.setTextSize(mTextSize);
      paramCanvas.drawRect(new Rect(0, 0, getWidth(), mTextSize + mTextPadding), this.mPaint);
      this.mPaint.setARGB(255, 200, 200, 200);
      paramCanvas.drawText(getImagePreset().name(), mTextPadding, 1.5F * mTextPadding, this.mPaint);
    }
    if (showControls());
    drawToast(paramCanvas);
  }

  public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    if (((!this.mActivity.isShowingHistoryPanel()) && (paramMotionEvent1.getX() > paramMotionEvent2.getX())) || ((this.mActivity.isShowingHistoryPanel()) && (paramMotionEvent2.getX() > paramMotionEvent1.getX()) && (((!this.mTouchShowOriginal) || ((this.mTouchShowOriginal) && (System.currentTimeMillis() - this.mTouchShowOriginalDate < 300L))))))
      this.mActivity.toggleHistoryPanel();
    return true;
  }

  public void onItemClick(int paramInt)
  {
    setImagePreset(new ImagePreset((ImagePreset)this.mHistoryAdapter.getItem(paramInt)), false);
    this.mHistoryAdapter.setCurrentPreset(paramInt);
  }

  public void onLongPress(MotionEvent paramMotionEvent)
  {
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt2));
  }

  public void onNewValue(int paramInt)
  {
    int i = 100;
    int j = -100;
    if (getCurrentFilter() != null)
    {
      getCurrentFilter().setParameter(paramInt);
      i = getCurrentFilter().getMaxParameter();
      j = getCurrentFilter().getMinParameter();
    }
    if (getImagePreset() != null)
    {
      this.mImageLoader.resetImageForPreset(getImagePreset(), this);
      getImagePreset().fillImageStateAdapter(this.mImageStateAdapter);
    }
    if (getPanelController() != null)
      getPanelController().onNewValue(paramInt);
    updateSeekBar(paramInt, j, i);
    invalidate();
  }

  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (getCurrentFilter() != null)
    {
      int j = getCurrentFilter().getMaxParameter();
      i = uiToParameter(paramInt, getCurrentFilter().getMinParameter(), j, paramSeekBar.getMax());
    }
    onNewValue(i);
  }

  public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    return false;
  }

  public void onShowPress(MotionEvent paramMotionEvent)
  {
  }

  public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    return false;
  }

  public void onStartTrackingTouch(SeekBar paramSeekBar)
  {
  }

  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
  }

  public void onTouchDown(float paramFloat1, float paramFloat2)
  {
    this.mTouchX = paramFloat1;
    this.mTouchY = paramFloat2;
    invalidate();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    super.onTouchEvent(paramMotionEvent);
    if (this.mGestureDetector != null)
      this.mGestureDetector.onTouchEvent(paramMotionEvent);
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    if (paramMotionEvent.getAction() == 0)
    {
      this.mTouchDownX = i;
      this.mTouchDownY = j;
      this.mTouchShowOriginalDate = System.currentTimeMillis();
      this.mShowOriginalDirection = 0;
    }
    if (paramMotionEvent.getAction() == 2)
    {
      this.mTouchX = i;
      this.mTouchY = j;
      if ((!this.mActivity.isShowingHistoryPanel()) && (System.currentTimeMillis() - this.mTouchShowOriginalDate > 200L))
        this.mTouchShowOriginal = true;
    }
    if (paramMotionEvent.getAction() == 1)
    {
      this.mTouchShowOriginal = false;
      this.mTouchDownX = 0;
      this.mTouchDownY = 0;
      this.mTouchX = 0.0F;
      this.mTouchY = 0.0F;
    }
    invalidate();
    return true;
  }

  public void onTouchUp()
  {
  }

  public void requestFilteredImages()
  {
    if (this.mImageLoader != null)
    {
      Bitmap localBitmap1 = this.mImageLoader.getImageForPreset(this, getImagePreset(), showHires());
      if (localBitmap1 != null)
      {
        if (this.mFilteredImage == null)
          invalidate();
        this.mFilteredImage = localBitmap1;
      }
      updateImagePresets(false);
      if (this.mImageGeometryOnlyPreset != null)
      {
        Bitmap localBitmap3 = this.mImageLoader.getImageForPreset(this, this.mImageGeometryOnlyPreset, showHires());
        if (localBitmap3 != null)
          this.mGeometryOnlyImage = localBitmap3;
      }
      if (this.mImageFiltersOnlyPreset != null)
      {
        Bitmap localBitmap2 = this.mImageLoader.getImageForPreset(this, this.mImageFiltersOnlyPreset, showHires());
        if (localBitmap2 != null)
          this.mFiltersOnlyImage = localBitmap2;
      }
    }
    if (!this.mShowOriginal)
      return;
    this.mFilteredImage = this.mGeometryOnlyImage;
  }

  public void resetImageCaches(ImageShow paramImageShow)
  {
    if (this.mImageLoader == null)
      return;
    updateImagePresets(true);
  }

  public void resetParameter()
  {
    ImageFilter localImageFilter = getCurrentFilter();
    if (localImageFilter == null)
      return;
    onNewValue(localImageFilter.getDefaultParameter());
  }

  public void saveImage(FilterShowActivity paramFilterShowActivity, File paramFile)
  {
    this.mImageLoader.saveImage(getImagePreset(), paramFilterShowActivity, paramFile);
  }

  public void select()
  {
    if (getCurrentFilter() != null)
    {
      int i = getCurrentFilter().getParameter();
      int j = getCurrentFilter().getMaxParameter();
      updateSeekBar(i, getCurrentFilter().getMinParameter(), j);
    }
    if (this.mSeekBar == null)
      return;
    this.mSeekBar.setOnSeekBarChangeListener(this);
  }

  public void setCurrentFilter(ImageFilter paramImageFilter)
  {
    this.mCurrentFilter = paramImageFilter;
  }

  public void setImageLoader(ImageLoader paramImageLoader)
  {
    this.mImageLoader = paramImageLoader;
    if (this.mImageLoader == null)
      return;
    this.mImageLoader.addListener(this);
    if (this.mImagePreset == null)
      return;
    this.mImagePreset.setImageLoader(this.mImageLoader);
  }

  public void setImagePreset(ImagePreset paramImagePreset)
  {
    setImagePreset(paramImagePreset, true);
  }

  public void setImagePreset(ImagePreset paramImagePreset, boolean paramBoolean)
  {
    if (paramImagePreset == null)
      return;
    this.mImagePreset = paramImagePreset;
    getImagePreset().setImageLoader(this.mImageLoader);
    updateImagePresets(true);
    if (paramBoolean)
      this.mHistoryAdapter.addHistoryItem(getImagePreset());
    getImagePreset().setEndpoint(this);
    updateImage();
    this.mImagePreset.fillImageStateAdapter(this.mImageStateAdapter);
    invalidate();
  }

  public void setPanelController(PanelController paramPanelController)
  {
    this.mController = paramPanelController;
  }

  public void setSeekBar(SeekBar paramSeekBar)
  {
    this.mSeekBar = paramSeekBar;
  }

  public ImageShow setShowControls(boolean paramBoolean)
  {
    this.mShowControls = paramBoolean;
    if (this.mShowControls)
      if (this.mSeekBar != null)
        this.mSeekBar.setVisibility(0);
    do
      return this;
    while (this.mSeekBar == null);
    this.mSeekBar.setVisibility(4);
    return this;
  }

  public void setupGestureDetector(Context paramContext)
  {
    this.mGestureDetector = new GestureDetector(paramContext, this);
  }

  public boolean showControls()
  {
    return this.mShowControls;
  }

  public boolean showHires()
  {
    return true;
  }

  public boolean showTitle()
  {
    return false;
  }

  public void showToast(String paramString)
  {
    showToast(paramString, false);
  }

  public void showToast(String paramString, boolean paramBoolean)
  {
    this.mToast = paramString;
    this.mShowToast = true;
    this.mImportantToast = paramBoolean;
    invalidate();
    this.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        ImageShow.access$002(ImageShow.this, false);
        ImageShow.this.invalidate();
      }
    }
    , 400L);
  }

  public void unselect()
  {
  }

  public void updateFilteredImage(Bitmap paramBitmap)
  {
    this.mFilteredImage = paramBitmap;
  }

  public boolean updateGeometryFlags()
  {
    return true;
  }

  public void updateImage()
  {
    if (!updateGeometryFlags());
    Bitmap localBitmap;
    do
    {
      return;
      localBitmap = this.mImageLoader.getOriginalBitmapLarge();
    }
    while (localBitmap == null);
    imageSizeChanged(localBitmap);
    invalidate();
  }

  public void updateImagePresets(boolean paramBoolean)
  {
    ImagePreset localImagePreset1 = getImagePreset();
    if (localImagePreset1 == null);
    ImagePreset localImagePreset3;
    do
    {
      do
      {
        return;
        if (paramBoolean)
          this.mImageLoader.resetImageForPreset(getImagePreset(), this);
        if ((!paramBoolean) && (this.mImageGeometryOnlyPreset != null))
          continue;
        ImagePreset localImagePreset2 = new ImagePreset(localImagePreset1);
        localImagePreset2.setDoApplyFilters(false);
        if ((this.mImageGeometryOnlyPreset != null) && (localImagePreset2.same(this.mImageGeometryOnlyPreset)))
          continue;
        this.mImageGeometryOnlyPreset = localImagePreset2;
        this.mGeometryOnlyImage = null;
      }
      while ((!paramBoolean) && (this.mImageFiltersOnlyPreset != null));
      localImagePreset3 = new ImagePreset(localImagePreset1);
      localImagePreset3.setDoApplyGeometry(false);
    }
    while ((this.mImageFiltersOnlyPreset != null) && (localImagePreset3.same(this.mImageFiltersOnlyPreset)));
    this.mImageFiltersOnlyPreset = localImagePreset3;
    this.mFiltersOnlyImage = null;
  }

  public void updateSeekBar(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mSeekBar == null);
    do
    {
      return;
      int i = parameterToUI(paramInt1, paramInt2, paramInt3, this.mSeekBar.getMax());
      this.mSeekBar.setProgress(i);
    }
    while (getPanelController() == null);
    getPanelController().onNewValue(paramInt1);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageShow
 * JD-Core Version:    0.5.4
 */