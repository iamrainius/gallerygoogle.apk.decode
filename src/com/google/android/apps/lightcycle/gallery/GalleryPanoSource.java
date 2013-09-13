package com.google.android.apps.lightcycle.gallery;

import com.google.android.apps.lightcycle.storage.SessionMetadata;
import com.google.android.apps.lightcycle.storage.StorageManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GalleryPanoSource
{
  private List<String> stitchedSessions = new ArrayList();
  private final StorageManager storageManager;
  private List<String> unstitchedSessions = new ArrayList();

  public GalleryPanoSource(StorageManager paramStorageManager)
  {
    this.storageManager = paramStorageManager;
    refresh();
  }

  public SessionMetadata getSession(String paramString)
  {
    return this.storageManager.getSessionMetadata(paramString);
  }

  public String getStitchedFile(String paramString)
  {
    return this.storageManager.retrieveStitchedPanorama(paramString).getAbsolutePath();
  }

  public List<String> getStitchedSessions()
  {
    return this.stitchedSessions;
  }

  public String getThumbnailFile(String paramString)
  {
    return this.storageManager.retrieveThumbnail(paramString).getAbsolutePath();
  }

  public List<String> getUnstitchedSessions()
  {
    return this.unstitchedSessions;
  }

  public void refresh()
  {
    this.stitchedSessions.clear();
    this.unstitchedSessions.clear();
    Iterator localIterator = this.storageManager.getSessionIdList().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (this.storageManager.getSessionMetadata(str).stitchedPanoramaExists)
        this.stitchedSessions.add(str);
      this.unstitchedSessions.add(str);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.GalleryPanoSource
 * JD-Core Version:    0.5.4
 */