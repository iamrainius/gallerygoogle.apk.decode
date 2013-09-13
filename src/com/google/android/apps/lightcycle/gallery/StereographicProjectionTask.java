package com.google.android.apps.lightcycle.gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import com.google.android.apps.lightcycle.panorama.LightCycleNative;
import com.google.android.apps.lightcycle.storage.LocalSessionStorage;
import com.google.android.apps.lightcycle.util.Dialogs;
import java.io.File;

public class StereographicProjectionTask extends AsyncTask<LocalSessionStorage, Void, String>
{
  private final Activity context;
  private ProgressDialog progressDialog;

  public StereographicProjectionTask(Activity paramActivity)
  {
    this.context = paramActivity;
  }

  protected String doInBackground(LocalSessionStorage[] paramArrayOfLocalSessionStorage)
  {
    LocalSessionStorage localLocalSessionStorage = paramArrayOfLocalSessionStorage[0];
    File localFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Little_Planets");
    if (!localFile1.exists())
      localFile1.mkdirs();
    File localFile2 = new File(localFile1, localLocalSessionStorage.sessionId + ".jpg");
    String str1 = localLocalSessionStorage.mosaicFilePath;
    String str2 = localFile2.getAbsolutePath();
    if (!LightCycleNative.StereographicProject(0.25F, str1, str2, 2048))
      str2 = null;
    return str2;
  }

  protected void onPostExecute(String paramString)
  {
    if (paramString == null)
    {
      this.progressDialog.dismiss();
      Dialogs.showDialog(2131361822, 2131361823, this.context, null);
      return;
    }
    MediaScannerConnection.scanFile(this.context, new String[] { paramString }, new String[] { "image/jpeg" }, new MediaScannerConnection.OnScanCompletedListener(paramString)
    {
      public void onScanCompleted(String paramString, Uri paramUri)
      {
        StereographicProjectionTask.this.progressDialog.dismiss();
        Intent localIntent = new Intent();
        localIntent.setAction("android.intent.action.VIEW");
        localIntent.setDataAndType(Uri.parse("file://" + this.val$result), "image/*");
        StereographicProjectionTask.this.context.startActivity(localIntent);
      }
    });
  }

  protected void onPreExecute()
  {
    this.progressDialog = new ProgressDialog(this.context);
    this.progressDialog.setTitle(2131361796);
    this.progressDialog.show();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.StereographicProjectionTask
 * JD-Core Version:    0.5.4
 */