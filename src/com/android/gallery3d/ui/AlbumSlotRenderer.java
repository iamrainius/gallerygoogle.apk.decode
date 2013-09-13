package com.android.gallery3d.ui;

import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.app.AlbumDataLoader;
import com.android.gallery3d.data.Path;

public class AlbumSlotRenderer extends AbstractSlotRenderer
{
  private final AbstractGalleryActivity mActivity;
  private boolean mAnimatePressedUp;
  private AlbumSlidingWindow mDataWindow;
  private Path mHighlightItemPath = null;
  private boolean mInSelectionMode;
  private final int mPlaceholderColor;
  private int mPressedIndex = -1;
  private final SelectionManager mSelectionManager;
  private SlotFilter mSlotFilter;
  private final SlotView mSlotView;
  private final ColorTexture mWaitLoadingTexture;

  public AlbumSlotRenderer(AbstractGalleryActivity paramAbstractGalleryActivity, SlotView paramSlotView, SelectionManager paramSelectionManager, int paramInt)
  {
    super(paramAbstractGalleryActivity);
    this.mActivity = paramAbstractGalleryActivity;
    this.mSlotView = paramSlotView;
    this.mSelectionManager = paramSelectionManager;
    this.mPlaceholderColor = paramInt;
    this.mWaitLoadingTexture = new ColorTexture(this.mPlaceholderColor);
    this.mWaitLoadingTexture.setSize(1, 1);
  }

  private static Texture checkTexture(Texture paramTexture)
  {
    if ((paramTexture instanceof TiledTexture) && (!((TiledTexture)paramTexture).isReady()))
      paramTexture = null;
    return paramTexture;
  }

  private int renderOverlay(GLCanvas paramGLCanvas, int paramInt1, AlbumSlidingWindow.AlbumEntry paramAlbumEntry, int paramInt2, int paramInt3)
  {
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
        if ((paramAlbumEntry.path != null) && (this.mHighlightItemPath == paramAlbumEntry.path))
        {
          drawSelectedFrame(paramGLCanvas, paramInt2, paramInt3);
          return 0;
        }
        bool1 = this.mInSelectionMode;
        i = 0;
      }
      while (!bool1);
      bool2 = this.mSelectionManager.isItemSelected(paramAlbumEntry.path);
      i = 0;
    }
    while (!bool2);
    drawSelectedFrame(paramGLCanvas, paramInt2, paramInt3);
    return 0;
  }

  public void onSlotSizeChanged(int paramInt1, int paramInt2)
  {
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

  public int renderSlot(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.mSlotFilter != null) && (!this.mSlotFilter.acceptSlot(paramInt1)))
      return 0;
    AlbumSlidingWindow.AlbumEntry localAlbumEntry = this.mDataWindow.get(paramInt1);
    Object localObject = checkTexture(localAlbumEntry.content);
    if (localObject == null)
    {
      localObject = this.mWaitLoadingTexture;
      localAlbumEntry.isWaitDisplayed = true;
    }
    while (true)
    {
      drawContent(paramGLCanvas, (Texture)localObject, paramInt3, paramInt4, localAlbumEntry.rotation);
      boolean bool1 = localObject instanceof FadeInTexture;
      int i = 0;
      if (bool1)
      {
        boolean bool2 = ((FadeInTexture)localObject).isAnimating();
        i = 0;
        if (bool2)
          i = 0x0 | 0x2;
      }
      if (localAlbumEntry.mediaType == 4)
        drawVideoOverlay(paramGLCanvas, paramInt3, paramInt4);
      if (localAlbumEntry.isPanorama)
        drawPanoramaIcon(paramGLCanvas, paramInt3, paramInt4);
      return i | renderOverlay(paramGLCanvas, paramInt1, localAlbumEntry, paramInt3, paramInt4);
      if (!localAlbumEntry.isWaitDisplayed)
        continue;
      localAlbumEntry.isWaitDisplayed = false;
      localObject = new FadeInTexture(this.mPlaceholderColor, localAlbumEntry.bitmapTexture);
      localAlbumEntry.content = ((Texture)localObject);
    }
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

  public void setModel(AlbumDataLoader paramAlbumDataLoader)
  {
    if (this.mDataWindow != null)
    {
      this.mDataWindow.setListener(null);
      this.mSlotView.setSlotCount(0);
      this.mDataWindow = null;
    }
    if (paramAlbumDataLoader == null)
      return;
    this.mDataWindow = new AlbumSlidingWindow(this.mActivity, paramAlbumDataLoader, 96);
    this.mDataWindow.setListener(new MyDataModelListener(null));
    this.mSlotView.setSlotCount(paramAlbumDataLoader.size());
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

  public void setSlotFilter(SlotFilter paramSlotFilter)
  {
    this.mSlotFilter = paramSlotFilter;
  }

  private class MyDataModelListener
    implements AlbumSlidingWindow.Listener
  {
    private MyDataModelListener()
    {
    }

    public void onContentChanged()
    {
      AlbumSlotRenderer.this.mSlotView.invalidate();
    }

    public void onSizeChanged(int paramInt)
    {
      AlbumSlotRenderer.this.mSlotView.setSlotCount(paramInt);
    }
  }

  public static abstract interface SlotFilter
  {
    public abstract boolean acceptSlot(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.AlbumSlotRenderer
 * JD-Core Version:    0.5.4
 */