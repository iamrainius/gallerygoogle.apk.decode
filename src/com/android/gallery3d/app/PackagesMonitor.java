package com.android.gallery3d.app;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.android.gallery3d.picasasource.PicasaSource;

public class PackagesMonitor extends BroadcastReceiver
{
  public static int getPackagesVersion(Context paramContext)
  {
    monitorenter;
    try
    {
      int i = PreferenceManager.getDefaultSharedPreferences(paramContext).getInt("packages-version", 1);
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private static void onReceiveAsync(Context paramContext, Intent paramIntent)
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
    int i = localSharedPreferences.getInt("packages-version", 1);
    localSharedPreferences.edit().putInt("packages-version", i + 1).commit();
    String str1 = paramIntent.getAction();
    String str2 = paramIntent.getData().getSchemeSpecificPart();
    if ("android.intent.action.PACKAGE_ADDED".equals(str1))
      PicasaSource.onPackageAdded(paramContext, str2);
    do
    {
      return;
      if (!"android.intent.action.PACKAGE_REMOVED".equals(str1))
        continue;
      PicasaSource.onPackageRemoved(paramContext, str2);
      return;
    }
    while (!"android.intent.action.PACKAGE_CHANGED".equals(str1));
    PicasaSource.onPackageChanged(paramContext, str2);
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramIntent.setClass(paramContext, AsyncService.class);
    paramContext.startService(paramIntent);
  }

  public static class AsyncService extends IntentService
  {
    public AsyncService()
    {
      super("GalleryPackagesMonitorAsync");
    }

    protected void onHandleIntent(Intent paramIntent)
    {
      PackagesMonitor.access$000(this, paramIntent);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.PackagesMonitor
 * JD-Core Version:    0.5.4
 */