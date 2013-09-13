package com.android.gallery3d.filtershow.imageshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.android.gallery3d.filtershow.HistoryAdapter;
import com.android.gallery3d.filtershow.PanelController;
import com.android.gallery3d.filtershow.filters.ImageFilter;
import com.android.gallery3d.filtershow.presets.ImagePreset;

public class ImageSlave extends ImageShow
{
  private ImageShow mMasterImageShow = null;

  public ImageSlave(Context paramContext)
  {
    super(paramContext);
  }

  public ImageSlave(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public ImageFilter getCurrentFilter()
  {
    return this.mMasterImageShow.getCurrentFilter();
  }

  public Rect getDisplayedImageBounds()
  {
    return this.mMasterImageShow.getDisplayedImageBounds();
  }

  public Bitmap getFilteredImage()
  {
    return this.mMasterImageShow.getFilteredImage();
  }

  public HistoryAdapter getHistory()
  {
    return this.mMasterImageShow.getHistory();
  }

  public ImagePreset getImagePreset()
  {
    return this.mMasterImageShow.getImagePreset();
  }

  public ImageShow getMaster()
  {
    return this.mMasterImageShow;
  }

  public PanelController getPanelController()
  {
    return this.mMasterImageShow.getPanelController();
  }

  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
  }

  public void requestFilteredImages()
  {
    this.mMasterImageShow.requestFilteredImages();
  }

  public void resetImageCaches(ImageShow paramImageShow)
  {
    this.mMasterImageShow.resetImageCaches(paramImageShow);
  }

  public void setCurrentFilter(ImageFilter paramImageFilter)
  {
    this.mMasterImageShow.setCurrentFilter(paramImageFilter);
  }

  public void setImagePreset(ImagePreset paramImagePreset, boolean paramBoolean)
  {
    this.mMasterImageShow.setImagePreset(paramImagePreset, paramBoolean);
  }

  public void setMaster(ImageShow paramImageShow)
  {
    this.mMasterImageShow = paramImageShow;
  }

  public void setPanelController(PanelController paramPanelController)
  {
    this.mMasterImageShow.setPanelController(paramPanelController);
  }

  public boolean showTitle()
  {
    return false;
  }

  public void updateImage()
  {
    this.mMasterImageShow.updateImage();
  }

  public void updateImagePresets(boolean paramBoolean)
  {
    this.mMasterImageShow.updateImagePresets(paramBoolean);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.imageshow.ImageSlave
 * JD-Core Version:    0.5.4
 */