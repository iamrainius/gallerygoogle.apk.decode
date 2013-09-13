package com.android.gallery3d.ui;

import android.content.Context;
import android.os.StatFs;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.io.File;

public class CacheStorageUsageInfo
{
  private AbstractGalleryActivity mActivity;
  private Context mContext;
  private long mTargetCacheBytes;
  private long mTotalBytes;
  private long mUsedBytes;
  private long mUsedCacheBytes;
  private long mUserChangeDelta;

  public CacheStorageUsageInfo(AbstractGalleryActivity paramAbstractGalleryActivity)
  {
    this.mActivity = paramAbstractGalleryActivity;
    this.mContext = paramAbstractGalleryActivity.getAndroidContext();
  }

  public long getExpectedUsedBytes()
  {
    return this.mUsedBytes - this.mUsedCacheBytes + this.mTargetCacheBytes + this.mUserChangeDelta;
  }

  public long getFreeBytes()
  {
    return this.mTotalBytes - this.mUsedBytes;
  }

  public long getTotalBytes()
  {
    return this.mTotalBytes;
  }

  public long getUsedBytes()
  {
    return this.mUsedBytes;
  }

  public void increaseTargetCacheSize(long paramLong)
  {
    this.mUserChangeDelta = (paramLong + this.mUserChangeDelta);
  }

  public void loadStorageInfo(ThreadPool.JobContext paramJobContext)
  {
    File localFile = this.mContext.getExternalCacheDir();
    if (localFile == null)
      localFile = this.mContext.getCacheDir();
    StatFs localStatFs = new StatFs(localFile.getAbsolutePath());
    long l1 = localStatFs.getBlockSize();
    long l2 = localStatFs.getAvailableBlocks();
    long l3 = localStatFs.getBlockCount();
    this.mTotalBytes = (l1 * l3);
    this.mUsedBytes = (l1 * (l3 - l2));
    this.mUsedCacheBytes = this.mActivity.getDataManager().getTotalUsedCacheSize();
    this.mTargetCacheBytes = this.mActivity.getDataManager().getTotalTargetCacheSize();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.CacheStorageUsageInfo
 * JD-Core Version:    0.5.4
 */