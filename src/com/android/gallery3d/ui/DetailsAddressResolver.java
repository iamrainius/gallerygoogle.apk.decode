package com.android.gallery3d.ui;

import android.content.Context;
import android.location.Address;
import android.os.Handler;
import android.os.Looper;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ReverseGeocoder;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

public class DetailsAddressResolver
{
  private Future<Address> mAddressLookupJob;
  private final AbstractGalleryActivity mContext;
  private final Handler mHandler;
  private AddressResolvingListener mListener;

  public DetailsAddressResolver(AbstractGalleryActivity paramAbstractGalleryActivity)
  {
    this.mContext = paramAbstractGalleryActivity;
    this.mHandler = new Handler(Looper.getMainLooper());
  }

  private void updateLocation(Address paramAddress)
  {
    if (paramAddress == null)
      return;
    Context localContext = this.mContext.getAndroidContext();
    String[] arrayOfString = new String[9];
    arrayOfString[0] = paramAddress.getAdminArea();
    arrayOfString[1] = paramAddress.getSubAdminArea();
    arrayOfString[2] = paramAddress.getLocality();
    arrayOfString[3] = paramAddress.getSubLocality();
    arrayOfString[4] = paramAddress.getThoroughfare();
    arrayOfString[5] = paramAddress.getSubThoroughfare();
    arrayOfString[6] = paramAddress.getPremises();
    arrayOfString[7] = paramAddress.getPostalCode();
    arrayOfString[8] = paramAddress.getCountryName();
    String str1 = "";
    int i = 0;
    if (i < arrayOfString.length)
    {
      label91: if ((arrayOfString[i] == null) || (arrayOfString[i].isEmpty()));
      while (true)
      {
        ++i;
        break label91:
        if (!str1.isEmpty())
          str1 = str1 + ", ";
        str1 = str1 + arrayOfString[i];
      }
    }
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = DetailsHelper.getDetailsName(localContext, 4);
    arrayOfObject[1] = str1;
    String str2 = String.format("%s : %s", arrayOfObject);
    this.mListener.onAddressAvailable(str2);
  }

  public void cancel()
  {
    if (this.mAddressLookupJob == null)
      return;
    this.mAddressLookupJob.cancel();
    this.mAddressLookupJob = null;
  }

  public String resolveAddress(double[] paramArrayOfDouble, AddressResolvingListener paramAddressResolvingListener)
  {
    this.mListener = paramAddressResolvingListener;
    this.mAddressLookupJob = this.mContext.getThreadPool().submit(new AddressLookupJob(paramArrayOfDouble), new FutureListener()
    {
      public void onFutureDone(Future<Address> paramFuture)
      {
        DetailsAddressResolver.access$102(DetailsAddressResolver.this, null);
        if (paramFuture.isCancelled())
          return;
        DetailsAddressResolver.this.mHandler.post(new Runnable(paramFuture)
        {
          public void run()
          {
            DetailsAddressResolver.this.updateLocation((Address)this.val$future.get());
          }
        });
      }
    });
    return GalleryUtils.formatLatitudeLongitude("(%f,%f)", paramArrayOfDouble[0], paramArrayOfDouble[1]);
  }

  private class AddressLookupJob
    implements ThreadPool.Job<Address>
  {
    private double[] mLatlng;

    protected AddressLookupJob(double[] arg2)
    {
      Object localObject;
      this.mLatlng = localObject;
    }

    public Address run(ThreadPool.JobContext paramJobContext)
    {
      return new ReverseGeocoder(DetailsAddressResolver.this.mContext.getAndroidContext()).lookupAddress(this.mLatlng[0], this.mLatlng[1], true);
    }
  }

  public static abstract interface AddressResolvingListener
  {
    public abstract void onAddressAvailable(String paramString);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.DetailsAddressResolver
 * JD-Core Version:    0.5.4
 */