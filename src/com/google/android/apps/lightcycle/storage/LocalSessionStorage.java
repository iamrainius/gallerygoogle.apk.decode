package com.google.android.apps.lightcycle.storage;

import android.net.Uri;
import java.io.Serializable;

public class LocalSessionStorage
  implements Serializable
{
  public Uri imageUri;
  public String metadataFilePath;
  public String mosaicFilePath;
  public String orientationFilePath;
  public String previewMosaicFilePath;
  public String sessionDir;
  public String sessionId;
  public String thumbnailFilePath;

  public String toString()
  {
    return "Session ID : " + this.sessionId + "\n SessionDir : " + this.sessionDir + "\n mosaic : " + this.mosaicFilePath + "\n thumbnail : " + this.thumbnailFilePath + "\n metadata : " + this.metadataFilePath + "\n orientationFile : " + this.orientationFilePath;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.LocalSessionStorage
 * JD-Core Version:    0.5.4
 */