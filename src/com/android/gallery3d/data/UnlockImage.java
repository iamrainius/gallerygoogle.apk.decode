package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;

public class UnlockImage extends ActionImage
{
  public UnlockImage(Path paramPath, GalleryApp paramGalleryApp)
  {
    super(paramPath, paramGalleryApp, 2130837873);
  }

  public int getSupportedOperations()
  {
    return 0x2000 | super.getSupportedOperations();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.UnlockImage
 * JD-Core Version:    0.5.4
 */