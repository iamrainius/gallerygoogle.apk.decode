package com.android.gallery3d.data;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.mtp.MtpDeviceInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.MediaSetUtils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@TargetApi(12)
public class MtpDeviceSet extends MediaSet
  implements FutureListener<ArrayList<MediaSet>>
{
  private GalleryApp mApplication;
  private ArrayList<MediaSet> mDeviceSet = new ArrayList();
  private final Handler mHandler;
  private boolean mIsLoading;
  private ArrayList<MediaSet> mLoadBuffer;
  private Future<ArrayList<MediaSet>> mLoadTask;
  private final MtpContext mMtpContext;
  private final String mName;
  private final ChangeNotifier mNotifier;

  public MtpDeviceSet(Path paramPath, GalleryApp paramGalleryApp, MtpContext paramMtpContext)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = paramGalleryApp;
    this.mNotifier = new ChangeNotifier(this, Uri.parse("mtp://"), paramGalleryApp);
    this.mMtpContext = paramMtpContext;
    this.mName = paramGalleryApp.getResources().getString(2131362295);
    this.mHandler = new Handler(this.mApplication.getMainLooper());
  }

  public static String getDeviceName(MtpContext paramMtpContext, int paramInt)
  {
    android.mtp.MtpDevice localMtpDevice = paramMtpContext.getMtpClient().getDevice(paramInt);
    if (localMtpDevice == null)
      return "";
    MtpDeviceInfo localMtpDeviceInfo = localMtpDevice.getDeviceInfo();
    if (localMtpDeviceInfo == null)
      return "";
    String str1 = localMtpDeviceInfo.getManufacturer().trim();
    String str2 = localMtpDeviceInfo.getModel().trim();
    return str1 + " " + str2;
  }

  public String getName()
  {
    return this.mName;
  }

  public MediaSet getSubMediaSet(int paramInt)
  {
    if (paramInt < this.mDeviceSet.size())
      return (MediaSet)this.mDeviceSet.get(paramInt);
    return null;
  }

  public int getSubMediaSetCount()
  {
    return this.mDeviceSet.size();
  }

  public boolean isLoading()
  {
    monitorenter;
    try
    {
      boolean bool = this.mIsLoading;
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

  public void onFutureDone(Future<ArrayList<MediaSet>> paramFuture)
  {
    monitorenter;
    try
    {
      Future localFuture = this.mLoadTask;
      if (paramFuture != localFuture)
        return;
      this.mLoadBuffer = ((ArrayList)paramFuture.get());
      this.mIsLoading = false;
      if (this.mLoadBuffer == null)
        this.mLoadBuffer = new ArrayList();
    }
    finally
    {
      monitorexit;
    }
  }

  public long reload()
  {
    monitorenter;
    Iterator localIterator;
    try
    {
      if (this.mNotifier.isDirty())
      {
        if (this.mLoadTask != null)
          this.mLoadTask.cancel();
        this.mIsLoading = true;
        this.mLoadTask = this.mApplication.getThreadPool().submit(new DevicesLoader(null), this);
      }
      if (this.mLoadBuffer == null)
        break label127;
      this.mDeviceSet = this.mLoadBuffer;
      this.mLoadBuffer = null;
      localIterator = this.mDeviceSet.iterator();
      if (!localIterator.hasNext())
        break label120;
    }
    finally
    {
      monitorexit;
    }
    label120: this.mDataVersion = nextVersionNumber();
    label127: long l = this.mDataVersion;
    monitorexit;
    return l;
  }

  private class DevicesLoader
    implements ThreadPool.Job<ArrayList<MediaSet>>
  {
    private DevicesLoader()
    {
    }

    public ArrayList<MediaSet> run(ThreadPool.JobContext paramJobContext)
    {
      DataManager localDataManager = MtpDeviceSet.this.mApplication.getDataManager();
      ArrayList localArrayList = new ArrayList();
      List localList = MtpDeviceSet.this.mMtpContext.getMtpClient().getDeviceList();
      Log.v("MtpDeviceSet", "loadDevices: " + localList + ", size=" + localList.size());
      Iterator localIterator = localList.iterator();
      if (localIterator.hasNext())
      {
        android.mtp.MtpDevice localMtpDevice = (android.mtp.MtpDevice)localIterator.next();
        synchronized (DataManager.LOCK)
        {
          int i = localMtpDevice.getDeviceId();
          Path localPath = MtpDeviceSet.this.mPath.getChild(i);
          MtpDevice localMtpDevice1 = (MtpDevice)localDataManager.peekMediaObject(localPath);
          if (localMtpDevice1 == null)
            localMtpDevice1 = new MtpDevice(localPath, MtpDeviceSet.this.mApplication, i, MtpDeviceSet.this.mMtpContext);
          Log.d("MtpDeviceSet", "add device " + localMtpDevice1);
          localArrayList.add(localMtpDevice1);
        }
      }
      Collections.sort(localArrayList, MediaSetUtils.NAME_COMPARATOR);
      return localArrayList;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MtpDeviceSet
 * JD-Core Version:    0.5.4
 */