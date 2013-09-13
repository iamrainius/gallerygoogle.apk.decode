package com.google.android.apps.lightcycle.storage;

import android.app.ProgressDialog;
import android.content.Context;
import com.google.android.apps.lightcycle.util.Callback;
import java.io.File;
import java.util.List;

public abstract interface StorageManager
{
  public abstract void addExistingPanoramaSessions(Callback<Void> paramCallback, ProgressDialog paramProgressDialog);

  public abstract void addSessionData(LocalSessionStorage paramLocalSessionStorage);

  public abstract void deleteSession(String paramString);

  public abstract LocalSessionStorage getExistingLocalSessionStorage(String paramString);

  public abstract LocalSessionStorage getLocalSessionStorage();

  public abstract List<String> getSessionIdList();

  public abstract SessionMetadata getSessionMetadata(String paramString);

  public abstract File getTempDirectory();

  public abstract ZippableSession getZippableSession(String paramString);

  public abstract void init(Context paramContext);

  public abstract File retrieveStitchedPanorama(String paramString);

  public abstract File retrieveThumbnail(String paramString);

  public abstract boolean setPanoramaDestination(String paramString);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.StorageManager
 * JD-Core Version:    0.5.4
 */