package com.android.gallery3d.ui;

import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.app.AlbumSetDataLoader;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;

public class AlbumSetSlotRenderer extends AbstractSlotRenderer
{
  private final AbstractGalleryActivity mActivity;
  private boolean mAnimatePressedUp;
  private final ResourceTexture mCameraOverlay;
  protected AlbumSetSlidingWindow mDataWindow;
  private Path mHighlightItemPath = null;
  private boolean mInSelectionMode;
  protected final LabelSpec mLabelSpec;
  private final int mPlaceholderColor;
  private int mPressedIndex = -1;
  private final SelectionManager mSelectionManager;
  private SlotView mSlotView;
  private final ColorTexture mWaitLoadingTexture;

  public AlbumSetSlotRenderer(AbstractGalleryActivity paramAbstractGalleryActivity, SelectionManager paramSelectionManager, SlotView paramSlotView, LabelSpec paramLabelSpec, int paramInt)
  {
    super(paramAbstractGalleryActivity);
    this.mActivity = paramAbstractGalleryActivity;
    this.mSelectionManager = paramSelectionManager;
    this.mSlotView = paramSlotView;
    this.mLabelSpec = paramLabelSpec;
    this.mPlaceholderColor = paramInt;
    this.mWaitLoadingTexture = new ColorTexture(this.mPlaceholderColor);
    this.mWaitLoadingTexture.setSize(1, 1);
    this.mCameraOverlay = new ResourceTexture(paramAbstractGalleryActivity, 2130837653);
  }

  private static Texture checkContentTexture(Texture paramTexture)
  {
    if ((paramTexture instanceof TiledTexture) && (!((TiledTexture)paramTexture).isReady()))
      paramTexture = null;
    return paramTexture;
  }

  private static Texture checkLabelTexture(Texture paramTexture)
  {
    if ((paramTexture instanceof UploadedTexture) && (((UploadedTexture)paramTexture).isUploading()))
      paramTexture = null;
    return paramTexture;
  }

  public void onSlotSizeChanged(int paramInt1, int paramInt2)
  {
    if (this.mDataWindow == null)
      return;
    this.mDataWindow.onSlotSizeChanged(paramInt1, paramInt2);
  }

  public void onVisibleRangeChanged(int paramInt1, int paramInt2)
  {
    if (this.mDataWindow == null)
      return;
    this.mDataWindow.setActiveWindow(paramInt1, paramInt2);
  }

  public void pause()
  {
    this.mDataWindow.pause();
  }

  public void prepareDrawing()
  {
    this.mInSelectionMode = this.mSelectionManager.inSelectionMode();
  }

  protected int renderContent(GLCanvas paramGLCanvas, AlbumSetSlidingWindow.AlbumSetEntry paramAlbumSetEntry, int paramInt1, int paramInt2)
  {
    Object localObject = checkContentTexture(paramAlbumSetEntry.content);
    if (localObject == null)
    {
      localObject = this.mWaitLoadingTexture;
      paramAlbumSetEntry.isWaitLoadingDisplayed = true;
    }
    while (true)
    {
      drawContent(paramGLCanvas, (Texture)localObject, paramInt1, paramInt2, paramAlbumSetEntry.rotation);
      boolean bool1 = localObject instanceof FadeInTexture;
      int i = 0;
      if (bool1)
      {
        boolean bool2 = ((FadeInTexture)localObject).isAnimating();
        i = 0;
        if (bool2)
          i = 0x0 | 0x2;
      }
      return i;
      if (!paramAlbumSetEntry.isWaitLoadingDisplayed)
        continue;
      paramAlbumSetEntry.isWaitLoadingDisplayed = false;
      localObject = new FadeInTexture(this.mPlaceholderColor, paramAlbumSetEntry.bitmapTexture);
      paramAlbumSetEntry.content = ((Texture)localObject);
    }
  }

  protected int renderLabel(GLCanvas paramGLCanvas, AlbumSetSlidingWindow.AlbumSetEntry paramAlbumSetEntry, int paramInt1, int paramInt2)
  {
    Object localObject = checkLabelTexture(paramAlbumSetEntry.labelTexture);
    if (localObject == null)
      localObject = this.mWaitLoadingTexture;
    int i = AlbumLabelMaker.getBorderSize();
    int j = this.mLabelSpec.labelBackgroundHeight;
    ((Texture)localObject).draw(paramGLCanvas, -i, i + (paramInt2 - j), i + (paramInt1 + i), j);
    return 0;
  }

  protected int renderOverlay(GLCanvas paramGLCanvas, int paramInt1, AlbumSetSlidingWindow.AlbumSetEntry paramAlbumSetEntry, int paramInt2, int paramInt3)
  {
    if ((paramAlbumSetEntry.album != null) && (paramAlbumSetEntry.album.isCameraRoll()))
    {
      int j = paramInt3 - this.mLabelSpec.labelBackgroundHeight;
      int k = j / 2;
      this.mCameraOverlay.draw(paramGLCanvas, (paramInt2 - k) / 2, (j - k) / 2, k, k);
    }
    int i;
    if (this.mPressedIndex == paramInt1)
      if (this.mAnimatePressedUp)
      {
        drawPressedUpFrame(paramGLCanvas, paramInt2, paramInt3);
        i = 0x0 | 0x2;
        if (isPressedUpFrameFinished())
        {
          this.mAnimatePressedUp = false;
          this.mPressedIndex = -1;
        }
      }
    boolean bool2;
    do
    {
      boolean bool1;
      do
      {
        return i;
        drawPressedFrame(paramGLCanvas, paramInt2, paramInt3);
        return 0;
        if ((this.mHighlightItemPath != null) && (this.mHighlightItemPath == paramAlbumSetEntry.setPath))
        {
          drawSelectedFrame(paramGLCanvas, paramInt2, paramInt3);
          return 0;
        }
        bool1 = this.mInSelectionMode;
        i = 0;
      }
      while (!bool1);
      bool2 = this.mSelectionManager.isItemSelected(paramAlbumSetEntry.setPath);
      i = 0;
    }
    while (!bool2);
    drawSelectedFrame(paramGLCanvas, paramInt2, paramInt3);
    return 0;
  }

  public int renderSlot(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    AlbumSetSlidingWindow.AlbumSetEntry localAlbumSetEntry = this.mDataWindow.get(paramInt1);
    return 0x0 | renderContent(paramGLCanvas, localAlbumSetEntry, paramInt3, paramInt4) | renderLabel(paramGLCanvas, localAlbumSetEntry, paramInt3, paramInt4) | renderOverlay(paramGLCanvas, paramInt1, localAlbumSetEntry, paramInt3, paramInt4);
  }

  public void resume()
  {
    this.mDataWindow.resume();
  }

  public void setHighlightItemPath(Path paramPath)
  {
    if (this.mHighlightItemPath == paramPath)
      return;
    this.mHighlightItemPath = paramPath;
    this.mSlotView.invalidate();
  }

  public void setModel(AlbumSetDataLoader paramAlbumSetDataLoader)
  {
    if (this.mDataWindow != null)
    {
      this.mDataWindow.setListener(null);
      this.mDataWindow = null;
      this.mSlotView.setSlotCount(0);
    }
    if (paramAlbumSetDataLoader == null)
      return;
    this.mDataWindow = new AlbumSetSlidingWindow(this.mActivity, paramAlbumSetDataLoader, this.mLabelSpec, 96);
    this.mDataWindow.setListener(new MyCacheListener(null));
    this.mSlotView.setSlotCount(this.mDataWindow.size());
  }

  public void setPressedIndex(int paramInt)
  {
    if (this.mPressedIndex == paramInt)
      return;
    this.mPressedIndex = paramInt;
    this.mSlotView.invalidate();
  }

  public void setPressedUp()
  {
    if (this.mPressedIndex == -1)
      return;
    this.mAnimatePressedUp = true;
    this.mSlotView.invalidate();
  }

  public static class LabelSpec
  {
    public int backgroundColor;
    public int countColor;
    public int countFontSize;
    public int countOffset;
    public int iconSize;
    public int labelBackgroundHeight;
    public int leftMargin;
    public int titleColor;
    public int titleFontSize;
    public int titleOffset;
    public int titleRightMargin;
  }

  private class MyCacheListener
    implements AlbumSetSlidingWindow.Listener
  {
    private MyCacheListener()
    {
    }

    public void onContentChanged()
    {
      AlbumSetSlotRenderer.this.mSlotView.invalidate();
    }

    public void onSizeChanged(int paramInt)
    {
      AlbumSetSlotRenderer.this.mSlotView.setSlotCount(paramInt);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.AlbumSetSlotRenderer
 * JD-Core Version:    0.5.4
 */