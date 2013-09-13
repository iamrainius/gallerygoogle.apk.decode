package com.android.gallery3d.ui;

import android.app.Activity;
import android.content.res.Resources;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.data.Path;

public class ManageCacheDrawer extends AlbumSetSlotRenderer
{
  private final int mCachePinMargin;
  private final int mCachePinSize;
  private final StringTexture mCachingText;
  private final ResourceTexture mCheckedItem;
  private final ResourceTexture mLocalAlbumIcon;
  private final SelectionManager mSelectionManager;
  private final ResourceTexture mUnCheckedItem;

  public ManageCacheDrawer(AbstractGalleryActivity paramAbstractGalleryActivity, SelectionManager paramSelectionManager, SlotView paramSlotView, AlbumSetSlotRenderer.LabelSpec paramLabelSpec, int paramInt1, int paramInt2)
  {
    super(paramAbstractGalleryActivity, paramSelectionManager, paramSlotView, paramLabelSpec, paramAbstractGalleryActivity.getResources().getColor(2131296289));
    this.mCheckedItem = new ResourceTexture(paramAbstractGalleryActivity, 2130837531);
    this.mUnCheckedItem = new ResourceTexture(paramAbstractGalleryActivity, 2130837530);
    this.mLocalAlbumIcon = new ResourceTexture(paramAbstractGalleryActivity, 2130837529);
    this.mCachingText = StringTexture.newInstance(paramAbstractGalleryActivity.getString(2131362231), 12.0F, -1);
    this.mSelectionManager = paramSelectionManager;
    this.mCachePinSize = paramInt1;
    this.mCachePinMargin = paramInt2;
  }

  private void drawCachingPin(GLCanvas paramGLCanvas, Path paramPath, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
  {
    if (isLocal(paramInt1));
    for (ResourceTexture localResourceTexture = this.mLocalAlbumIcon; ; localResourceTexture = this.mUnCheckedItem)
      while (true)
      {
        int i = this.mCachePinSize;
        localResourceTexture.draw(paramGLCanvas, paramInt2 - this.mCachePinMargin - i, paramInt3 - i, i, i);
        if (paramBoolean1)
        {
          int j = this.mCachingText.getWidth();
          int k = this.mCachingText.getHeight();
          this.mCachingText.draw(paramGLCanvas, (paramInt2 - j) / 2, paramInt3 - k);
        }
        return;
        if (!paramBoolean2)
          break;
        localResourceTexture = this.mCheckedItem;
      }
  }

  private static boolean isLocal(int paramInt)
  {
    return paramInt != 2;
  }

  public int renderSlot(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    AlbumSetSlidingWindow.AlbumSetEntry localAlbumSetEntry = this.mDataWindow.get(paramInt1);
    int i;
    label22: boolean bool1;
    label39: boolean bool2;
    if (localAlbumSetEntry.cacheFlag == 2)
    {
      i = 1;
      if ((i == 0) || (localAlbumSetEntry.cacheStatus == 3))
        break label182;
      bool1 = true;
      bool2 = i ^ this.mSelectionManager.isItemSelected(localAlbumSetEntry.setPath);
      if ((!isLocal(localAlbumSetEntry.sourceType)) && (!bool2))
        break label188;
    }
    for (int j = 1; ; j = 0)
    {
      if (j == 0)
      {
        paramGLCanvas.save(1);
        paramGLCanvas.multiplyAlpha(0.6F);
      }
      int k = 0x0 | renderContent(paramGLCanvas, localAlbumSetEntry, paramInt3, paramInt4);
      if (j == 0)
        paramGLCanvas.restore();
      int l = k | renderLabel(paramGLCanvas, localAlbumSetEntry, paramInt3, paramInt4);
      drawCachingPin(paramGLCanvas, localAlbumSetEntry.setPath, localAlbumSetEntry.sourceType, bool1, bool2, paramInt3, paramInt4);
      return l | renderOverlay(paramGLCanvas, paramInt1, localAlbumSetEntry, paramInt3, paramInt4);
      i = 0;
      break label22:
      label182: bool1 = false;
      label188: break label39:
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.ManageCacheDrawer
 * JD-Core Version:    0.5.4
 */