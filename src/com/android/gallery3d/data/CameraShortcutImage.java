package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;

public class CameraShortcutImage extends ActionImage
{
  public CameraShortcutImage(Path paramPath, GalleryApp paramGalleryApp)
  {
    super(paramPath, paramGalleryApp, 2130837871);
  }

  public int getSupportedOperations()
  {
    return 0x10000 | super.getSupportedOperations();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.CameraShortcutImage
 * JD-Core Version:    0.5.4
 */