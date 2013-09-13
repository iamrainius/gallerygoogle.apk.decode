package com.google.android.apps.lightcycle.storage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.apps.lightcycle.util.Callback;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

public class InitializeLocalDatabaseTask extends AsyncTask<Void, Integer, Void>
{
  private static final FileFilter JPEG_FILTER = new FileFilter()
  {
    public boolean accept(File paramFile)
    {
      return paramFile.getName().endsWith(".jpg");
    }
  };
  private static final Comparator<File> SORT_BY_DATE = new Comparator()
  {
    public int compare(File paramFile1, File paramFile2)
    {
      return Long.valueOf(paramFile1.lastModified()).compareTo(Long.valueOf(paramFile2.lastModified()));
    }
  };
  private static final String TAG = InitializeLocalDatabaseTask.class.getSimpleName();
  private final Callback<Void> doneCallback;
  private final ProgressDialog progressDialog;
  private final LocalFileStorageManager storageManager;

  InitializeLocalDatabaseTask(Context paramContext, LocalFileStorageManager paramLocalFileStorageManager, ProgressDialog paramProgressDialog, Callback<Void> paramCallback)
  {
    this.storageManager = paramLocalFileStorageManager;
    this.progressDialog = paramProgressDialog;
    this.doneCallback = paramCallback;
  }

  protected Void doInBackground(Void[] paramArrayOfVoid)
  {
    File localFile = this.storageManager.getPanoDirectory();
    if ((!localFile.exists()) || (!localFile.isDirectory()))
    {
      Log.e(TAG, "Unable to import panos. Directory '" + localFile.getAbsolutePath() + "' doesn't exist.");
      return null;
    }
    File[] arrayOfFile = localFile.listFiles(JPEG_FILTER);
    Arrays.sort(arrayOfFile, SORT_BY_DATE);
    Log.d(TAG, "Found " + arrayOfFile.length + " pano files.");
    this.progressDialog.setMax(arrayOfFile.length);
    for (int i = 0; ; ++i)
    {
      if (i < arrayOfFile.length);
      if (arrayOfFile[i].length() > 0L)
        this.storageManager.addExistingPanoramaSession(arrayOfFile[i], 1000, 4.0F);
      Integer[] arrayOfInteger = new Integer[1];
      arrayOfInteger[0] = Integer.valueOf(i);
      publishProgress(arrayOfInteger);
    }
  }

  protected void onPostExecute(Void paramVoid)
  {
    try
    {
      this.progressDialog.dismiss();
      this.doneCallback.onCallback(null);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
    }
  }

  protected void onPreExecute()
  {
    this.progressDialog.show();
  }

  protected void onProgressUpdate(Integer[] paramArrayOfInteger)
  {
    this.progressDialog.setProgress(paramArrayOfInteger[0].intValue());
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.InitializeLocalDatabaseTask
 * JD-Core Version:    0.5.4
 */