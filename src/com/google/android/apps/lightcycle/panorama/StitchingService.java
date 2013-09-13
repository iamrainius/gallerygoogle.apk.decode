package com.google.android.apps.lightcycle.panorama;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import com.google.android.apps.lightcycle.storage.LocalSessionStorage;
import com.google.android.apps.lightcycle.storage.StorageManager;
import com.google.android.apps.lightcycle.storage.StorageManagerFactory;
import com.google.android.apps.lightcycle.util.AnalyticsHelper;
import com.google.android.apps.lightcycle.util.AnalyticsHelper.Page;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.MetadataUtils;
import com.google.android.apps.lightcycle.util.Utils;
import java.io.File;

public class StitchingService extends Service
{
  private static final String TAG = StitchingService.class.getSimpleName();
  private AnalyticsHelper analyticsHelper;
  private final IBinder binder = new StitchingBinder();
  private StitchTask currentTask = null;
  private Notification inProgressNotification;
  private NotificationManager notificationManager;
  private boolean paused = false;
  private final ServiceController serviceController = new ServiceController();
  private StitchingServiceManager stitchingServiceManager;
  private PowerManager.WakeLock wakeLock;

  public static ContentValues createImageContentValues(String paramString)
  {
    ContentValues localContentValues = new ContentValues();
    File localFile1 = new File(paramString);
    String str1 = localFile1.getName();
    localContentValues.put("title", str1.substring(0, str1.indexOf('.')));
    localContentValues.put("_display_name", str1);
    localContentValues.put("datetaken", Long.valueOf(System.currentTimeMillis()));
    localContentValues.put("mime_type", "image/jpeg");
    localContentValues.put("_size", Long.valueOf(localFile1.length()));
    localContentValues.put("_data", localFile1.getAbsolutePath());
    File localFile2 = localFile1.getParentFile();
    String str2 = localFile2.toString().toLowerCase();
    String str3 = localFile2.getName().toLowerCase();
    localContentValues.put("bucket_id", Integer.valueOf(str2.hashCode()));
    localContentValues.put("bucket_display_name", str3);
    return localContentValues;
  }

  private Uri createImageURI(String paramString, Uri paramUri)
  {
    ContentValues localContentValues = createImageContentValues(paramString);
    ContentResolver localContentResolver = getContentResolver();
    if (paramUri == null)
      return localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
    localContentValues.remove("datetaken");
    localContentResolver.update(paramUri, localContentValues, null, null);
    return paramUri;
  }

  private Notification createInProgressNotification()
  {
    String str = (String)getText(2131361799);
    NotificationCompat.Builder localBuilder = new NotificationCompat.Builder(this);
    localBuilder.setSmallIcon(2130837757);
    localBuilder.setContentIntent(PendingIntent.getActivity(this, 1, new Intent(), 0));
    localBuilder.setTicker(str);
    localBuilder.setContentTitle(str);
    localBuilder.setProgress(100, 0, false);
    return localBuilder.build();
  }

  @SuppressLint({"NewApi"})
  private void stitchNextSession()
  {
    LG.d("Stitching next session.");
    StitchingServiceManager.StitchSession localStitchSession = this.stitchingServiceManager.popNextSession();
    if (localStitchSession == null)
    {
      stopSelf();
      return;
    }
    this.currentTask = new StitchTask(localStitchSession, localStitchSession.storage.sessionDir, localStitchSession.storage.mosaicFilePath, localStitchSession.storage.thumbnailFilePath, localStitchSession.storage.imageUri);
    if (this.paused)
      this.currentTask.suspend();
    if (Build.VERSION.SDK_INT >= 11)
    {
      this.currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
      return;
    }
    this.currentTask.execute(new Void[0]);
  }

  public IBinder onBind(Intent paramIntent)
  {
    return this.binder;
  }

  public void onCreate()
  {
    this.notificationManager = ((NotificationManager)getSystemService("notification"));
    this.stitchingServiceManager = StitchingServiceManager.getStitchingServiceManager(this);
    this.wakeLock = ((PowerManager)getSystemService("power")).newWakeLock(1, TAG);
    this.wakeLock.acquire();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("com.google.android.apps.lightcycle.panorama.PAUSE");
    localIntentFilter.addAction("com.google.android.apps.lightcycle.panorama.RESUME");
    LocalBroadcastManager.getInstance(this).registerReceiver(this.serviceController, localIntentFilter);
  }

  public void onDestroy()
  {
    this.notificationManager.cancel(1);
    this.stitchingServiceManager.stitchingFinished();
    this.wakeLock.release();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(this.serviceController);
  }

  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    this.inProgressNotification = createInProgressNotification();
    startForeground(1, this.inProgressNotification);
    Notification localNotification = this.inProgressNotification;
    localNotification.flags = (0xFFFFFFBF & localNotification.flags);
    this.notificationManager.notify(1, this.inProgressNotification);
    stitchNextSession();
    return 1;
  }

  public void pause()
  {
    this.paused = true;
    if (this.currentTask == null)
      return;
    this.currentTask.suspend();
  }

  public void resume()
  {
    this.paused = false;
    if ((this.currentTask == null) || (!this.currentTask.isSuspended()))
      return;
    this.currentTask.resume();
  }

  public class ServiceController extends BroadcastReceiver
  {
    public ServiceController()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent.getAction() == "com.google.android.apps.lightcycle.panorama.PAUSE")
        StitchingService.this.pause();
      do
        return;
      while (paramIntent.getAction() != "com.google.android.apps.lightcycle.panorama.RESUME");
      StitchingService.this.resume();
    }
  }

  class StitchTask extends AsyncTask<Void, Void, Integer>
  {
    private final Uri imageUri;
    private Object lock = new Object();
    private final String outputFile;
    private final String sessionPath;
    private final StitchingServiceManager.StitchSession stitchSession;
    private volatile boolean suspend = false;
    private final String thumbnailFile;

    public StitchTask(StitchingServiceManager.StitchSession paramString1, String paramString2, String paramString3, String paramUri, Uri arg6)
    {
      this.stitchSession = paramString1;
      this.sessionPath = paramString2;
      this.outputFile = paramString3;
      this.thumbnailFile = paramUri;
      Object localObject;
      this.imageUri = localObject;
    }

    private void waitIfSuspended()
    {
      if (!this.suspend)
        return;
      try
      {
        synchronized (this.lock)
        {
          this.lock.wait();
          return;
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
    }

    protected Integer doInBackground(Void[] paramArrayOfVoid)
    {
      Process.setThreadPriority(-4);
      waitIfSuspended();
      int i = LightCycleNative.CreateNewStitchingSession();
      LightCycleNative.setProgressCallback(i, new LightCycleView.ProgressCallback()
      {
        public void progress(int paramInt)
        {
          StitchingService.this.stitchingServiceManager.onStitchingProgress(StitchingService.StitchTask.this.outputFile, StitchingService.StitchTask.this.imageUri, paramInt);
          if (StitchingService.this.inProgressNotification != null)
          {
            StitchingService.this.inProgressNotification.contentView.setProgressBar(16908301, 100, paramInt, false);
            StitchingService.this.notificationManager.notify(1, StitchingService.this.inProgressNotification);
          }
          StitchingService.StitchTask.this.waitIfSuspended();
        }
      });
      long l1 = SystemClock.uptimeMillis();
      LG.d("Rendering panorama from source images at " + this.sessionPath);
      boolean bool1 = PreferenceManager.getDefaultSharedPreferences(StitchingService.this.getBaseContext()).getBoolean("useRealtimeAlignment", false);
      boolean bool2 = Utils.isDogfoodApp(StitchingService.this.getApplicationContext());
      int j = LightCycleNative.StitchPanorama(this.sessionPath, this.outputFile, bool2, this.thumbnailFile, 1000, 4.0F, i, bool1);
      long l2 = SystemClock.uptimeMillis() - l1;
      StitchingService.access$602(StitchingService.this, AnalyticsHelper.getInstance(StitchingService.this));
      StitchingService.this.analyticsHelper.trackPage(AnalyticsHelper.Page.STITCH_COMPLETE);
      StitchingService.this.analyticsHelper.trackEvent("Stitching", "Stitching", "Stitch time", (int)(l2 / 1000L));
      String str1 = this.outputFile;
      String str2 = this.stitchSession.storage.metadataFilePath;
      String str3 = this.sessionPath;
      if (j == 1);
      for (boolean bool3 = true; ; bool3 = false)
      {
        MetadataUtils.writeMetadataIntoJpegFile(str1, str2, str3, bool3);
        return Integer.valueOf(j);
      }
    }

    public boolean isSuspended()
    {
      return this.suspend;
    }

    protected void onPostExecute(Integer paramInteger)
    {
      if (paramInteger.intValue() == 1);
      while (true)
      {
        StorageManager localStorageManager = StorageManagerFactory.getStorageManager();
        localStorageManager.init(StitchingService.this);
        localStorageManager.addSessionData(this.stitchSession.storage);
        Uri localUri = StitchingService.this.createImageURI(this.outputFile, this.imageUri);
        StitchingService.this.stitchingServiceManager.onStitchingResult(this.outputFile, localUri);
        StitchingService.this.notificationManager.cancel(1);
        StitchingService.this.stitchNextSession();
        return;
      }
    }

    protected void onPreExecute()
    {
      System.gc();
    }

    public void resume()
    {
      this.suspend = false;
      synchronized (this.lock)
      {
        this.lock.notifyAll();
        return;
      }
    }

    public void suspend()
    {
      this.suspend = true;
    }
  }

  public class StitchingBinder extends Binder
  {
    public StitchingBinder()
    {
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.StitchingService
 * JD-Core Version:    0.5.4
 */