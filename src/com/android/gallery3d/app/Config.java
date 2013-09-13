package com.android.gallery3d.app;

import android.content.Context;
import android.content.res.Resources;
import com.android.gallery3d.ui.AlbumSetSlotRenderer.LabelSpec;
import com.android.gallery3d.ui.SlotView.Spec;

final class Config
{
  public static class AlbumPage
  {
    private static AlbumPage sInstance;
    public int placeholderColor;
    public SlotView.Spec slotViewSpec;

    private AlbumPage(Context paramContext)
    {
      Resources localResources = paramContext.getResources();
      this.placeholderColor = localResources.getColor(2131296285);
      this.slotViewSpec = new SlotView.Spec();
      this.slotViewSpec.rowsLand = localResources.getInteger(2131755010);
      this.slotViewSpec.rowsPort = localResources.getInteger(2131755011);
      this.slotViewSpec.slotGap = localResources.getDimensionPixelSize(2131624025);
    }

    public static AlbumPage get(Context paramContext)
    {
      monitorenter;
      try
      {
        if (sInstance == null)
          sInstance = new AlbumPage(paramContext);
        AlbumPage localAlbumPage = sInstance;
        return localAlbumPage;
      }
      finally
      {
        monitorexit;
      }
    }
  }

  public static class AlbumSetPage
  {
    private static AlbumSetPage sInstance;
    public AlbumSetSlotRenderer.LabelSpec labelSpec;
    public int paddingBottom;
    public int paddingTop;
    public int placeholderColor;
    public SlotView.Spec slotViewSpec;

    private AlbumSetPage(Context paramContext)
    {
      Resources localResources = paramContext.getResources();
      this.placeholderColor = localResources.getColor(2131296280);
      this.slotViewSpec = new SlotView.Spec();
      this.slotViewSpec.rowsLand = localResources.getInteger(2131755008);
      this.slotViewSpec.rowsPort = localResources.getInteger(2131755009);
      this.slotViewSpec.slotGap = localResources.getDimensionPixelSize(2131624016);
      this.slotViewSpec.slotHeightAdditional = 0;
      this.paddingTop = localResources.getDimensionPixelSize(2131624014);
      this.paddingBottom = localResources.getDimensionPixelSize(2131624015);
      this.labelSpec = new AlbumSetSlotRenderer.LabelSpec();
      this.labelSpec.labelBackgroundHeight = localResources.getDimensionPixelSize(2131624017);
      this.labelSpec.titleOffset = localResources.getDimensionPixelSize(2131624018);
      this.labelSpec.countOffset = localResources.getDimensionPixelSize(2131624019);
      this.labelSpec.titleFontSize = localResources.getDimensionPixelSize(2131624020);
      this.labelSpec.countFontSize = localResources.getDimensionPixelSize(2131624021);
      this.labelSpec.leftMargin = localResources.getDimensionPixelSize(2131624022);
      this.labelSpec.titleRightMargin = localResources.getDimensionPixelSize(2131624023);
      this.labelSpec.iconSize = localResources.getDimensionPixelSize(2131624024);
      this.labelSpec.backgroundColor = localResources.getColor(2131296281);
      this.labelSpec.titleColor = localResources.getColor(2131296282);
      this.labelSpec.countColor = localResources.getColor(2131296283);
    }

    public static AlbumSetPage get(Context paramContext)
    {
      monitorenter;
      try
      {
        if (sInstance == null)
          sInstance = new AlbumSetPage(paramContext);
        AlbumSetPage localAlbumSetPage = sInstance;
        return localAlbumSetPage;
      }
      finally
      {
        monitorexit;
      }
    }
  }

  public static class ManageCachePage extends Config.AlbumSetPage
  {
    private static ManageCachePage sInstance;
    public final int cachePinMargin;
    public final int cachePinSize;

    public ManageCachePage(Context paramContext)
    {
      super(paramContext, null);
      Resources localResources = paramContext.getResources();
      this.cachePinSize = localResources.getDimensionPixelSize(2131624026);
      this.cachePinMargin = localResources.getDimensionPixelSize(2131624027);
    }

    public static ManageCachePage get(Context paramContext)
    {
      monitorenter;
      try
      {
        if (sInstance == null)
          sInstance = new ManageCachePage(paramContext);
        ManageCachePage localManageCachePage = sInstance;
        return localManageCachePage;
      }
      finally
      {
        monitorexit;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.Config
 * JD-Core Version:    0.5.4
 */