package com.google.android.apps.lightcycle.storage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.google.android.apps.lightcycle.LightCycleApp;
import com.google.android.apps.lightcycle.panorama.LightCycleNative;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.FileUtil;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.Utils;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocalFileStorageManager
  implements StorageManager
{
  private static final FileFilter JPEG_FILTER = new FileFilter()
  {
    public boolean accept(File paramFile)
    {
      return paramFile.getName().endsWith(".jpg");
    }
  };
  private Context mContext;
  private LocalDatabase mLocalDatabase;
  private File mPanoDirectory;

  private void cleanUpOldSessions(long paramLong)
  {
    File[] arrayOfFile = getSessionBaseDirectory().listFiles(new FileFilter()
    {
      public boolean accept(File paramFile)
      {
        return paramFile.isDirectory();
      }
    });
    long l = System.currentTimeMillis();
    int i = arrayOfFile.length;
    for (int j = 0; j < i; ++j)
    {
      File localFile = arrayOfFile[j];
      if ((localFile.lastModified() >= l - paramLong) || (FileUtil.deleteDirectoryRecursively(localFile)))
        continue;
      Log.w("LightCycle-storage", "Could not clean up " + localFile.getAbsolutePath());
    }
  }

  private File getDefaultPanoDirectory()
  {
    File localFile = new File(Environment.getExternalStorageDirectory(), "panoramas");
    if ((!localFile.exists()) && (!localFile.mkdirs()))
    {
      Log.e("LightCycle-storage", "Panorama directory not created.");
      localFile = null;
    }
    return localFile;
  }

  private int getNumImageFiles(String paramString)
  {
    LG.d("capture directory : " + paramString);
    File[] arrayOfFile = new File(paramString).listFiles(JPEG_FILTER);
    if (arrayOfFile == null)
      return 0;
    return arrayOfFile.length;
  }

  private File getThumbnailDirectory()
  {
    File localFile = new File(getPanoDirectory(), "thumbnails");
    if ((!localFile.exists()) && (!localFile.mkdirs()))
    {
      Log.e("LightCycle-storage", "Thumbnails directory not created.");
      localFile = null;
    }
    return localFile;
  }

  boolean addExistingPanoramaSession(File paramFile, int paramInt, float paramFloat)
  {
    File localFile1 = getThumbnailDirectory();
    if (localFile1 == null)
      Log.e("LightCycle-storage", "Could not get the thumbnail directory.");
    File localFile2;
    do
    {
      return false;
      localFile2 = new File(localFile1, paramFile.getName());
    }
    while ((!localFile2.exists()) && (!LightCycleNative.CreateThumbnailImage(paramFile.getAbsolutePath(), localFile2.getAbsolutePath(), paramInt, paramFloat)));
    LocalSessionEntry localLocalSessionEntry = new LocalSessionEntry();
    localLocalSessionEntry.captureDirectory = null;
    localLocalSessionEntry.creatorVersion = null;
    localLocalSessionEntry.id = paramFile.getName();
    localLocalSessionEntry.metadataFile = null;
    localLocalSessionEntry.stitchedExists = true;
    localLocalSessionEntry.thumbnailExists = true;
    localLocalSessionEntry.stitchedFile = paramFile.getAbsolutePath();
    localLocalSessionEntry.thumbnailFile = localFile2.getAbsolutePath();
    this.mLocalDatabase.addSession(localLocalSessionEntry);
    return true;
  }

  public void addExistingPanoramaSessions(Callback<Void> paramCallback, ProgressDialog paramProgressDialog)
  {
    new InitializeLocalDatabaseTask(this.mContext, this, paramProgressDialog, paramCallback).execute(new Void[0]);
  }

  public void addSessionData(LocalSessionStorage paramLocalSessionStorage)
  {
    Log.d("LightCycle-storage", "Adding session data");
    LocalSessionEntry localLocalSessionEntry = new LocalSessionEntry();
    if (!new File(paramLocalSessionStorage.sessionDir).exists())
    {
      Log.e("LightCycle-storage", "The storage directory does not exist.");
      return;
    }
    localLocalSessionEntry.id = paramLocalSessionStorage.sessionId;
    localLocalSessionEntry.captureDirectory = paramLocalSessionStorage.sessionDir;
    localLocalSessionEntry.metadataFile = paramLocalSessionStorage.metadataFilePath;
    if (new File(paramLocalSessionStorage.mosaicFilePath).exists())
    {
      localLocalSessionEntry.stitchedExists = true;
      localLocalSessionEntry.stitchedFile = paramLocalSessionStorage.mosaicFilePath;
      label96: if (!new File(paramLocalSessionStorage.thumbnailFilePath).exists())
        break label156;
      localLocalSessionEntry.thumbnailExists = true;
    }
    for (localLocalSessionEntry.thumbnailFile = paramLocalSessionStorage.thumbnailFilePath; ; localLocalSessionEntry.thumbnailFile = "")
    {
      localLocalSessionEntry.creatorVersion = LightCycleApp.getAppVersion();
      this.mLocalDatabase.addSession(localLocalSessionEntry);
      return;
      localLocalSessionEntry.stitchedExists = false;
      localLocalSessionEntry.stitchedFile = "";
      break label96:
      label156: localLocalSessionEntry.thumbnailExists = false;
    }
  }

  public void deleteSession(String paramString)
  {
    LocalSessionEntry localLocalSessionEntry = this.mLocalDatabase.getSession(paramString);
    if (localLocalSessionEntry.stitchedExists)
    {
      Log.d("LightCycle-storage", "Deleting stiched pano file.");
      if (!FileUtil.deleteFile(localLocalSessionEntry.stitchedFile))
        Log.e("LightCycle-storage", "Unable to delete pano file.");
    }
    if (localLocalSessionEntry.thumbnailExists)
    {
      Log.d("LightCycle-storage", "Deleting thumbnail file.");
      if (!FileUtil.deleteFile(localLocalSessionEntry.thumbnailFile))
        Log.e("LightCycle-storage", "Unable to delete pano file.");
    }
    if (localLocalSessionEntry.captureDirectory != null)
    {
      Log.d("LightCycle-storage", "Deleting capture directory.");
      File localFile = new File(localLocalSessionEntry.captureDirectory);
      if ((localFile.isDirectory()) && (localFile.exists()) && (!FileUtil.deleteDirectoryRecursively(localFile)))
        Log.e("LightCycle-storage", "Unable to delete pano capture directory.");
    }
    if (this.mLocalDatabase.deleteSession(localLocalSessionEntry.id))
      return;
    Log.e("LightCycle-storage", "Unable to delete session entry in local database.");
  }

  public LocalSessionStorage getExistingLocalSessionStorage(String paramString)
  {
    LocalSessionEntry localLocalSessionEntry = this.mLocalDatabase.getSession(paramString);
    LocalSessionStorage localLocalSessionStorage;
    if (localLocalSessionEntry == null)
      localLocalSessionStorage = null;
    do
    {
      return localLocalSessionStorage;
      localLocalSessionStorage = new LocalSessionStorage();
      localLocalSessionStorage.sessionId = localLocalSessionEntry.id;
      localLocalSessionStorage.sessionDir = localLocalSessionEntry.captureDirectory;
      localLocalSessionStorage.metadataFilePath = localLocalSessionEntry.metadataFile;
      if ((localLocalSessionStorage.metadataFilePath != null) && (localLocalSessionStorage.metadataFilePath.endsWith("metadata.csv")))
        localLocalSessionStorage.metadataFilePath = localLocalSessionStorage.metadataFilePath.replace("metadata.csv", "session.meta");
      if (localLocalSessionEntry.stitchedExists)
        localLocalSessionStorage.mosaicFilePath = localLocalSessionEntry.stitchedFile;
      if (!localLocalSessionEntry.thumbnailExists)
        continue;
      localLocalSessionStorage.thumbnailFilePath = localLocalSessionEntry.thumbnailFile;
    }
    while (localLocalSessionStorage.sessionDir == null);
    localLocalSessionStorage.orientationFilePath = new File(new File(localLocalSessionStorage.sessionDir), "orientations.txt").getAbsolutePath();
    return localLocalSessionStorage;
  }

  public LocalSessionStorage getLocalSessionStorage()
  {
    String str1 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File localFile1 = new File(getSessionBaseDirectory(), "session_" + str1);
    if (!localFile1.mkdirs())
      LG.d("Image directory already exists.");
    try
    {
      if (!localFile1.isDirectory())
        break label127;
      String[] arrayOfString = localFile1.list();
      int i = 0;
      if (i >= arrayOfString.length)
        break label127;
      File localFile3 = new File(localFile1, arrayOfString[i]);
      localFile3.delete();
      label127: ++i;
    }
    catch (Exception localException)
    {
      Log.e("LightCycle-storage", "Could not delete temporary images.");
      LocalSessionStorage localLocalSessionStorage = new LocalSessionStorage();
      localLocalSessionStorage.sessionId = str1;
      localLocalSessionStorage.sessionDir = localFile1.getAbsolutePath();
      String str2 = "PANO_" + str1 + ".jpg";
      localLocalSessionStorage.mosaicFilePath = new File(this.mPanoDirectory, str2).getAbsolutePath();
      String str3 = "preview_" + str1 + ".jpg";
      localLocalSessionStorage.previewMosaicFilePath = new File(this.mPanoDirectory, str3).getAbsolutePath();
      if (getThumbnailDirectory() == null)
        Log.e("LightCycle-storage", "Could not get the thumbnail directory.");
      File localFile2;
      for (localLocalSessionStorage.thumbnailFilePath = ""; ; localLocalSessionStorage.thumbnailFilePath = localFile2.getAbsolutePath())
      {
        localLocalSessionStorage.orientationFilePath = new File(localFile1, "orientations.txt").getAbsolutePath();
        localLocalSessionStorage.metadataFilePath = new File(localFile1, "session.meta").getAbsolutePath();
        return localLocalSessionStorage;
        localFile2 = new File(getThumbnailDirectory(), str2);
      }
    }
  }

  public File getPanoDirectory()
  {
    LG.d("Panorama directory is : " + this.mPanoDirectory.getAbsolutePath());
    return this.mPanoDirectory;
  }

  public File getSessionBaseDirectory()
  {
    File localFile = new File(this.mContext.getExternalFilesDir(null), "panorama_sessions");
    if ((!localFile.exists()) && (!localFile.mkdirs()))
    {
      Log.e("LightCycle-storage", "Sessions root directory could not be created.");
      localFile = null;
    }
    return localFile;
  }

  public List<String> getSessionIdList()
  {
    return this.mLocalDatabase.getSessionIdList();
  }

  public SessionMetadata getSessionMetadata(String paramString)
  {
    LocalSessionEntry localLocalSessionEntry = this.mLocalDatabase.getSession(paramString);
    if (localLocalSessionEntry == null)
    {
      LG.d("ID not found in database : " + paramString);
      return null;
    }
    SessionMetadata localSessionMetadata = new SessionMetadata();
    localSessionMetadata.creatorVersion = localLocalSessionEntry.creatorVersion;
    if (localLocalSessionEntry.captureDirectory != null);
    for (localSessionMetadata.numPhotos = getNumImageFiles(localLocalSessionEntry.captureDirectory); ; localSessionMetadata.numPhotos = 0)
    {
      localSessionMetadata.stitchedPanoramaExists = localLocalSessionEntry.stitchedExists;
      localSessionMetadata.thumbnailExists = localLocalSessionEntry.thumbnailExists;
      localSessionMetadata.creatorVersion = localLocalSessionEntry.creatorVersion;
      return localSessionMetadata;
    }
  }

  public File getTempDirectory()
  {
    File localFile = new File(getPanoDirectory(), "temp");
    if ((!localFile.exists()) && (!localFile.mkdirs()))
    {
      Log.e("LightCycle-storage", "Temp directory not created.");
      localFile = null;
    }
    return localFile;
  }

  public ZippableSession getZippableSession(String paramString)
  {
    return new LocalZippableSession(this.mLocalDatabase.getSession(paramString));
  }

  public void init(Context paramContext)
  {
    this.mContext = paramContext;
    if (!Utils.isDogfoodApp(this.mContext))
      cleanUpOldSessions(86400000L);
    this.mLocalDatabase = new LocalDatabase(paramContext);
    this.mPanoDirectory = getDefaultPanoDirectory();
  }

  public File retrieveStitchedPanorama(String paramString)
  {
    return new File(this.mLocalDatabase.getSession(paramString).stitchedFile);
  }

  public File retrieveThumbnail(String paramString)
  {
    return new File(this.mLocalDatabase.getSession(paramString).thumbnailFile);
  }

  public boolean setPanoramaDestination(String paramString)
  {
    this.mPanoDirectory = new File(paramString);
    if ((!this.mPanoDirectory.exists()) && (!this.mPanoDirectory.mkdirs()))
    {
      Log.e("LightCycle-storage", "Panorama directory not created.");
      return false;
    }
    return true;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.LocalFileStorageManager
 * JD-Core Version:    0.5.4
 */