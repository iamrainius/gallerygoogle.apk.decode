package com.google.android.picasasync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncResult;
import android.content.SyncStats;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;
import com.google.android.picasastore.MetricsUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class PicasaSyncManager
{
  private static PicasaSyncManager sInstance;
  private boolean mBackgroundData = false;
  private final Context mContext;
  private volatile SyncSession mCurrentSession;
  private final PicasaFacade mFacade;
  private boolean mHasWifiConnectivity = false;
  private final HashSet<String> mInvalidAccounts = new HashSet();
  private boolean mIsPlugged = false;
  private boolean mIsRoaming = false;
  private final ArrayList<SyncTaskProvider> mProviders = new ArrayList();
  private final Handler mSyncHandler;
  private final PicasaSyncHelper mSyncHelper;
  private ArrayList<SyncRequest> mSyncRequests = new ArrayList();

  private PicasaSyncManager(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mFacade = PicasaFacade.get(this.mContext);
    this.mSyncHelper = PicasaSyncHelper.getInstance(this.mContext);
    HandlerThread localHandlerThread = new HandlerThread("picasa-sync-manager", 10);
    localHandlerThread.start();
    this.mSyncHandler = initSyncHandler(localHandlerThread);
    this.mSyncHandler.sendEmptyMessage(6);
    this.mSyncHandler.sendEmptyMessage(4);
    this.mSyncHandler.sendEmptyMessage(2);
    this.mSyncHandler.sendEmptyMessage(5);
    1 local1 = new OnAccountsUpdateListener()
    {
      public void onAccountsUpdated(Account[] paramArrayOfAccount)
      {
        Log.i("PicasaSyncManager", "account change detect - update database");
        PicasaSyncManager.this.mSyncHandler.sendEmptyMessage(4);
      }
    };
    AccountManager.get(this.mContext).addOnAccountsUpdatedListener(local1, null, false);
  }

  private boolean acceptSyncTask(SyncTask paramSyncTask)
  {
    if (paramSyncTask.isAutoSync())
    {
      if (!ContentResolver.getMasterSyncAutomatically())
      {
        Log.d("PicasaSyncManager", "reject " + paramSyncTask + " because master auto sync is off");
        paramSyncTask.onRejected(6);
        return false;
      }
      if (!ContentResolver.getSyncAutomatically(new Account(paramSyncTask.syncAccount, "com.google"), this.mFacade.getAuthority()))
      {
        Log.d("PicasaSyncManager", "reject " + paramSyncTask + " because auto sync is off");
        paramSyncTask.onRejected(6);
        return false;
      }
    }
    if ((!this.mBackgroundData) && (paramSyncTask.isBackgroundSync()))
    {
      Log.d("PicasaSyncManager", "reject " + paramSyncTask + " for disabled background data");
      paramSyncTask.onRejected(8);
      return false;
    }
    if ((!this.mIsPlugged) && (!paramSyncTask.isSyncOnBattery()))
    {
      Log.d("PicasaSyncManager", "reject " + paramSyncTask + " on battery");
      paramSyncTask.onRejected(4);
      return false;
    }
    if (!this.mHasWifiConnectivity)
    {
      if (paramSyncTask.isSyncOnWifiOnly())
      {
        Log.d("PicasaSyncManager", "reject " + paramSyncTask + " for non-wifi connection");
        paramSyncTask.onRejected(2);
        return false;
      }
      if ((this.mIsRoaming) && (!paramSyncTask.isSyncOnRoaming()))
      {
        Log.d("PicasaSyncManager", "reject " + paramSyncTask + " for roaming");
        paramSyncTask.onRejected(3);
        return false;
      }
    }
    if ((!isExternalStorageMounted()) && (paramSyncTask.isSyncOnExternalStorageOnly()))
    {
      Log.d("PicasaSyncManager", "reject " + paramSyncTask + " on external storage");
      paramSyncTask.onRejected(11);
      return false;
    }
    synchronized (this.mInvalidAccounts)
    {
      if (!this.mInvalidAccounts.contains(paramSyncTask.syncAccount))
        break label441;
      Log.d("PicasaSyncManager", "reject " + paramSyncTask + " for invalid account: " + Utils.maskDebugInfo(paramSyncTask.syncAccount));
      return false;
    }
    label441: monitorexit;
    return true;
  }

  private void checkSyncRequests()
  {
    monitorenter;
    boolean bool;
    while (true)
    {
      SQLiteDatabase localSQLiteDatabase;
      SyncRequest localSyncRequest;
      UserEntry localUserEntry;
      try
      {
        ArrayList localArrayList1 = this.mSyncRequests;
        this.mSyncRequests = new ArrayList();
        monitorexit;
        bool = false;
        localSQLiteDatabase = this.mSyncHelper.getWritableDatabase();
        ArrayList localArrayList2 = null;
        Iterator localIterator1 = localArrayList1.iterator();
        Iterator localIterator2;
        do
        {
          if (!localIterator1.hasNext())
            break label160;
          localSyncRequest = (SyncRequest)localIterator1.next();
          if (localSyncRequest.account != null)
            break label139;
          if (localArrayList2 == null)
            localArrayList2 = this.mSyncHelper.getUsers();
          localIterator2 = localArrayList2.iterator();
        }
        while (!localIterator2.hasNext());
        localUserEntry = (UserEntry)localIterator2.next();
      }
      finally
      {
        monitorexit;
      }
      label139: bool |= localSyncRequest.state.onSyncRequested(localSQLiteDatabase, localSyncRequest.account);
    }
    if (!bool)
      label160: return;
    updateTasks(1000L);
  }

  public static PicasaSyncManager get(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new PicasaSyncManager(paramContext);
      PicasaSyncManager localPicasaSyncManager = sInstance;
      return localPicasaSyncManager;
    }
    finally
    {
      monitorexit;
    }
  }

  private void getNextSyncTask(SyncSession paramSyncSession)
  {
    int i = MetricsUtils.begin("PicasaSyncManager.getNextSyncTask");
    FutureTask localFutureTask = new FutureTask(new GetNextSyncTask(paramSyncSession));
    this.mSyncHandler.post(localFutureTask);
    try
    {
      localFutureTask.get();
      MetricsUtils.end(i);
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PicasaSyncManager", "fail to get next task", localThrowable);
    }
  }

  private Handler initSyncHandler(HandlerThread paramHandlerThread)
  {
    return new Handler(paramHandlerThread.getLooper())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
          throw new AssertionError("unknown message: " + paramMessage.what);
        case 6:
          PicasaSyncManager.this.setupTaskProviders();
          return;
        case 1:
          PicasaSyncManager.this.checkSyncRequests();
          return;
        case 2:
          PicasaSyncManager.this.updateEnvironment();
          return;
        case 5:
          PicasaSyncManager.this.updateBatteryState((Boolean)paramMessage.obj);
          return;
        case 3:
          PicasaSyncManager.this.updateTasksInternal();
          return;
        case 4:
        }
        PicasaSyncManager.this.mSyncHelper.syncAccounts(PicasaSyncManager.this.mFacade.getAuthority());
      }
    };
  }

  private static boolean isExternalStorageMounted()
  {
    String str = Environment.getExternalStorageState();
    return (str.equals("mounted")) || (str.equals("mounted_ro"));
  }

  private boolean isValidAccount(Account paramAccount)
  {
    for (Account localAccount : AccountManager.get(this.mContext).getAccountsByType("com.google"))
      if (paramAccount.name.equals(localAccount.name))
        return true;
    return false;
  }

  private static boolean isWifiNetwork(int paramInt)
  {
    switch (paramInt)
    {
    case 1:
    default:
      return true;
    case 0:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    }
    return false;
  }

  private SyncTask nextSyncTaskInternal(String paramString)
  {
    int i = 0;
    int j = this.mProviders.size();
    while (i < j)
    {
      SyncTaskProvider localSyncTaskProvider = (SyncTaskProvider)this.mProviders.get(i);
      ArrayList localArrayList = new ArrayList();
      localSyncTaskProvider.collectTasks(localArrayList);
      Object localObject = null;
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        SyncTask localSyncTask = (SyncTask)localIterator.next();
        localSyncTask.mPriority = i;
        if ((!acceptSyncTask(localSyncTask)) || ((localObject != null) && (!localSyncTask.syncAccount.equals(paramString))))
          continue;
        localObject = localSyncTask;
      }
      if (localObject != null)
        return localObject;
      ++i;
    }
    return null;
  }

  private void onAccountInvalid(String paramString)
  {
    Log.w("PicasaSyncManager", "account: " + Utils.maskDebugInfo(paramString) + " has been removed ?!");
    synchronized (this.mInvalidAccounts)
    {
      this.mInvalidAccounts.add(paramString);
      return;
    }
  }

  private void performSyncInternal(SyncSession paramSyncSession)
  {
    SyncStats localSyncStats = paramSyncSession.result.stats;
    if (!paramSyncSession.isSyncCancelled())
    {
      getNextSyncTask(paramSyncSession);
      if ((paramSyncSession.mCurrentTask != null) && (paramSyncSession.account.equals(paramSyncSession.mCurrentTask.syncAccount)))
        break label45;
    }
    return;
    try
    {
      label45: Log.d("PicasaSyncManager", "perform sync task: " + paramSyncSession.mCurrentTask);
      paramSyncSession.mCurrentTask.performSync(paramSyncSession.result);
      if (paramSyncSession.result.hasError())
        Log.d("PicasaSyncManager", "sync complete w error:" + paramSyncSession.result);
      paramSyncSession.mCurrentTask = null;
      if (localSyncStats.numIoExceptions > 0L);
      Log.w("PicasaSyncManager", "stop sync session due to io error");
      return;
    }
    catch (IOException localIOException)
    {
      Log.w("PicasaSyncManager", "perform sync fail", localIOException);
      localSyncStats.numIoExceptions = (1L + localSyncStats.numIoExceptions);
    }
    catch (Throwable localThrowable)
    {
      Log.w("PicasaSyncManager", "perform sync fail", localThrowable);
    }
    finally
    {
      paramSyncSession.mCurrentTask = null;
    }
  }

  private void requestSync(String paramString, SyncState paramSyncState)
  {
    monitorenter;
    try
    {
      if (this.mSyncRequests.size() == 0)
        this.mSyncHandler.sendEmptyMessage(1);
      this.mSyncRequests.add(new SyncRequest(paramString, paramSyncState));
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  private void setupTaskProviders()
  {
    UploadsManager localUploadsManager = UploadsManager.getInstance(this.mContext);
    monitorenter;
    try
    {
      this.mProviders.add(new MetadataSync(this.mContext, true));
      this.mProviders.add(localUploadsManager.getManualPhotoUploadTaskProvider());
      this.mProviders.add(localUploadsManager.getManualVideoUploadTaskProvider());
      this.mProviders.add(new MetadataSync(this.mContext, false));
      this.mProviders.add(new PhotoPrefetch(this.mContext, 2));
      this.mProviders.add(new PhotoPrefetch(this.mContext, 3));
      this.mProviders.add(new PhotoPrefetch(this.mContext, 1));
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  private void updateBatteryState(Boolean paramBoolean)
  {
    int i = 1;
    this.mSyncHandler.removeMessages(5);
    if (paramBoolean == null)
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
      Intent localIntent = this.mContext.registerReceiver(null, localIntentFilter);
      if (localIntent == null)
      {
        Log.w("PicasaSyncManager", "there is no battery info yet");
        return;
      }
      int j = localIntent.getIntExtra("plugged", -1);
      if ((j != i) && (j != 2))
        break label129;
    }
    while (true)
    {
      paramBoolean = Boolean.valueOf(i);
      Log.d("PicasaSyncManager", "battery info: " + paramBoolean);
      if (this.mIsPlugged != paramBoolean.booleanValue());
      this.mIsPlugged = paramBoolean.booleanValue();
      updateTasksInternal();
      return;
      label129: i = 0;
    }
  }

  private void updateEnvironment()
  {
    this.mSyncHandler.removeMessages(2);
    ConnectivityManager localConnectivityManager = (ConnectivityManager)this.mContext.getSystemService("connectivity");
    NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
    boolean bool1;
    label43: int i;
    if ((localNetworkInfo != null) && (isWifiNetwork(localNetworkInfo.getType())))
    {
      bool1 = true;
      boolean bool2 = this.mHasWifiConnectivity;
      i = 0;
      if (bool1 != bool2)
      {
        i = 1;
        this.mHasWifiConnectivity = bool1;
        Log.d("PicasaSyncManager", "active network: " + localNetworkInfo);
      }
      if ((localNetworkInfo == null) || (!localNetworkInfo.isRoaming()))
        break label190;
    }
    for (int j = 1; ; j = 0)
    {
      if (j != this.mIsRoaming)
      {
        i = 1;
        this.mIsRoaming = j;
      }
      boolean bool3 = localConnectivityManager.getBackgroundDataSetting();
      if (this.mBackgroundData != bool3)
      {
        i = 1;
        this.mBackgroundData = bool3;
        Log.d("PicasaSyncManager", "background data: " + bool3);
      }
      if (i != 0)
        updateTasksInternal();
      return;
      bool1 = false;
      label190: break label43:
    }
  }

  private void updateTasksInternal()
  {
    SyncSession localSyncSession = this.mCurrentSession;
    label5: SyncTask localSyncTask3;
    if (localSyncSession == null)
    {
      this.mSyncHandler.removeMessages(3);
      localSyncTask3 = nextSyncTaskInternal(null);
      if (localSyncTask3 != null);
    }
    SyncTask localSyncTask1;
    SyncTask localSyncTask2;
    do
    {
      do
      {
        return;
        Account localAccount = new Account(localSyncTask3.syncAccount, "com.google");
        if (isValidAccount(localAccount))
        {
          Bundle localBundle = new Bundle();
          localBundle.putBoolean("picasa-sync-manager-requested", true);
          localBundle.putBoolean("ignore_settings", true);
          Log.d("PicasaSyncManager", "request sync for " + localSyncTask3);
          ContentResolver.requestSync(localAccount, this.mFacade.getAuthority(), localBundle);
          return;
        }
        onAccountInvalid(localAccount.name);
        localSyncSession = this.mCurrentSession;
        break label5:
        localSyncTask1 = localSyncSession.mCurrentTask;
      }
      while (localSyncTask1 == null);
      if (!acceptSyncTask(localSyncTask1))
      {
        Log.d("PicasaSyncManager", "stop task: " + localSyncTask1 + " due to environment change");
        localSyncTask1.cancelSync();
        return;
      }
      localSyncTask2 = nextSyncTaskInternal(localSyncSession.account);
    }
    while ((localSyncTask2 == null) || (localSyncTask2.mPriority >= localSyncTask1.mPriority));
    Log.d("PicasaSyncManager", "cancel task: " + localSyncTask1 + " for " + localSyncTask2);
    localSyncTask1.cancelSync();
  }

  public SyncSession createSession(String paramString, SyncResult paramSyncResult)
  {
    return new SyncSession(paramString, paramSyncResult);
  }

  public void onAccountInitialized(String paramString)
  {
    monitorenter;
    try
    {
      if (this.mSyncHelper.findUser(paramString) == null)
      {
        SQLiteDatabase localSQLiteDatabase = this.mSyncHelper.getWritableDatabase();
        UserEntry localUserEntry = new UserEntry(paramString);
        UserEntry.SCHEMA.insertOrReplace(localSQLiteDatabase, localUserEntry);
      }
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public void onBatteryStateChanged(boolean paramBoolean)
  {
    Handler localHandler = this.mSyncHandler;
    if (paramBoolean);
    for (Boolean localBoolean = Boolean.TRUE; ; localBoolean = Boolean.FALSE)
    {
      Message localMessage = Message.obtain(localHandler, 5, localBoolean);
      this.mSyncHandler.sendMessage(localMessage);
      return;
    }
  }

  public void onEnvironmentChanged()
  {
    this.mSyncHandler.sendEmptyMessage(2);
  }

  public void performSync(SyncSession paramSyncSession)
  {
    this.mCurrentSession = ((SyncSession)Utils.checkNotNull(paramSyncSession));
    monitorenter;
    Iterator localIterator;
    try
    {
      localIterator = this.mProviders.iterator();
      if (!localIterator.hasNext())
        break label52;
    }
    finally
    {
      monitorexit;
    }
    label52: monitorexit;
    try
    {
      performSyncInternal(paramSyncSession);
      this.mCurrentSession = null;
      try
      {
        if (!paramSyncSession.mSyncCancelled)
          updateTasks(0L);
        monitorexit;
        return;
      }
      finally
      {
        monitorexit;
      }
    }
    finally
    {
      this.mCurrentSession = null;
    }
  }

  public void requestAccountSync()
  {
    this.mSyncHandler.sendEmptyMessage(4);
  }

  public void requestMetadataSync(boolean paramBoolean)
  {
    if (paramBoolean);
    for (SyncState localSyncState = SyncState.METADATA_MANUAL; ; localSyncState = SyncState.METADATA)
    {
      requestSync(null, localSyncState);
      return;
    }
  }

  public void requestPrefetchSync()
  {
    PhotoPrefetch.onRequestSync(this.mContext);
    requestSync(null, SyncState.PREFETCH_FULL_IMAGE);
    requestSync(null, SyncState.PREFETCH_SCREEN_NAIL);
    requestSync(null, SyncState.PREFETCH_ALBUM_COVER);
  }

  public void resetSyncStates()
  {
    synchronized (this.mInvalidAccounts)
    {
      this.mInvalidAccounts.clear();
      monitorenter;
      Iterator localIterator;
      try
      {
        localIterator = this.mProviders.iterator();
        if (!localIterator.hasNext())
          break label65;
      }
      finally
      {
        monitorexit;
      }
    }
    label65: monitorexit;
  }

  public void updateTasks(long paramLong)
  {
    this.mSyncHandler.sendEmptyMessageDelayed(3, paramLong);
  }

  private class GetNextSyncTask
    implements Callable<Void>
  {
    private final PicasaSyncManager.SyncSession mSession;

    public GetNextSyncTask(PicasaSyncManager.SyncSession arg2)
    {
      Object localObject;
      this.mSession = localObject;
    }

    public Void call()
    {
      PicasaSyncManager.this.mSyncHandler.removeMessages(3);
      SyncTask localSyncTask = PicasaSyncManager.this.nextSyncTaskInternal(this.mSession.account);
      synchronized (this.mSession)
      {
        if (this.mSession.mSyncCancelled)
          return null;
        this.mSession.mCurrentTask = localSyncTask;
        return null;
      }
    }
  }

  private static class SyncRequest
  {
    public String account;
    public SyncState state;

    public SyncRequest(String paramString, SyncState paramSyncState)
    {
      this.account = paramString;
      this.state = paramSyncState;
    }
  }

  public static final class SyncSession
  {
    public final String account;
    SyncTask mCurrentTask;
    boolean mSyncCancelled;
    public final SyncResult result;

    public SyncSession(String paramString, SyncResult paramSyncResult)
    {
      this.account = paramString;
      this.result = paramSyncResult;
    }

    public void cancelSync()
    {
      monitorenter;
      try
      {
        Log.d("PicasaSyncManager", "cancelSync on " + Utils.maskDebugInfo(this.account));
        this.mSyncCancelled = true;
        if (this.mCurrentTask != null)
        {
          this.mCurrentTask.cancelSync();
          this.mCurrentTask = null;
        }
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public boolean isSyncCancelled()
    {
      monitorenter;
      try
      {
        boolean bool = this.mSyncCancelled;
        monitorexit;
        return bool;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaSyncManager
 * JD-Core Version:    0.5.4
 */