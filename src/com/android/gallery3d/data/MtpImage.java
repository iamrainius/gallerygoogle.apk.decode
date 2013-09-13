package com.android.gallery3d.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.hardware.usb.UsbDevice;
import android.mtp.MtpObjectInfo;
import android.net.Uri;
import android.util.Log;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.provider.GalleryProvider;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.text.DateFormat;
import java.util.Date;

@TargetApi(12)
public class MtpImage extends MediaItem
{
  private final Context mContext;
  private long mDateTaken;
  private final int mDeviceId;
  private String mFileName;
  private final int mImageHeight;
  private final int mImageWidth;
  private final MtpContext mMtpContext;
  private final MtpObjectInfo mObjInfo;
  private int mObjectId;
  private int mObjectSize;

  MtpImage(Path paramPath, GalleryApp paramGalleryApp, int paramInt1, int paramInt2, MtpContext paramMtpContext)
  {
    this(paramPath, paramGalleryApp, paramInt1, MtpDevice.getObjectInfo(paramMtpContext, paramInt1, paramInt2), paramMtpContext);
  }

  MtpImage(Path paramPath, GalleryApp paramGalleryApp, int paramInt, MtpObjectInfo paramMtpObjectInfo, MtpContext paramMtpContext)
  {
    super(paramPath, nextVersionNumber());
    this.mContext = paramGalleryApp.getAndroidContext();
    this.mDeviceId = paramInt;
    this.mObjInfo = paramMtpObjectInfo;
    this.mObjectId = paramMtpObjectInfo.getObjectHandle();
    this.mObjectSize = paramMtpObjectInfo.getCompressedSize();
    this.mDateTaken = paramMtpObjectInfo.getDateCreated();
    this.mFileName = paramMtpObjectInfo.getName();
    this.mImageWidth = paramMtpObjectInfo.getImagePixWidth();
    this.mImageHeight = paramMtpObjectInfo.getImagePixHeight();
    this.mMtpContext = paramMtpContext;
  }

  public boolean Import()
  {
    return this.mMtpContext.copyFile(UsbDevice.getDeviceName(this.mDeviceId), this.mObjInfo);
  }

  public Uri getContentUri()
  {
    return GalleryProvider.getUriFor(this.mContext, this.mPath);
  }

  public long getDateInMs()
  {
    return this.mDateTaken;
  }

  public MediaDetails getDetails()
  {
    MediaDetails localMediaDetails = super.getDetails();
    DateFormat localDateFormat = DateFormat.getDateTimeInstance();
    localMediaDetails.addDetail(1, this.mFileName);
    localMediaDetails.addDetail(3, localDateFormat.format(new Date(this.mDateTaken)));
    localMediaDetails.addDetail(5, Integer.valueOf(this.mImageWidth));
    localMediaDetails.addDetail(6, Integer.valueOf(this.mImageHeight));
    localMediaDetails.addDetail(10, Long.valueOf(this.mObjectSize));
    return localMediaDetails;
  }

  public int getHeight()
  {
    return this.mImageHeight;
  }

  public byte[] getImageData()
  {
    return this.mMtpContext.getMtpClient().getObject(UsbDevice.getDeviceName(this.mDeviceId), this.mObjectId, this.mObjectSize);
  }

  public int getMediaType()
  {
    return 2;
  }

  public String getMimeType()
  {
    return "image/jpeg";
  }

  public long getSize()
  {
    return this.mObjectSize;
  }

  public int getSupportedOperations()
  {
    return 2112;
  }

  public int getWidth()
  {
    return this.mImageWidth;
  }

  public ThreadPool.Job<Bitmap> requestImage(int paramInt)
  {
    return new ThreadPool.Job()
    {
      public Bitmap run(ThreadPool.JobContext paramJobContext)
      {
        byte[] arrayOfByte = MtpImage.this.mMtpContext.getMtpClient().getThumbnail(UsbDevice.getDeviceName(MtpImage.this.mDeviceId), MtpImage.this.mObjectId);
        if (arrayOfByte == null)
        {
          Log.w("MtpImage", "decoding thumbnail failed");
          return null;
        }
        return DecodeUtils.decode(paramJobContext, arrayOfByte, null);
      }
    };
  }

  public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage()
  {
    return new ThreadPool.Job()
    {
      public BitmapRegionDecoder run(ThreadPool.JobContext paramJobContext)
      {
        byte[] arrayOfByte = MtpImage.this.mMtpContext.getMtpClient().getObject(UsbDevice.getDeviceName(MtpImage.this.mDeviceId), MtpImage.this.mObjectId, MtpImage.this.mObjectSize);
        return DecodeUtils.createBitmapRegionDecoder(paramJobContext, arrayOfByte, 0, arrayOfByte.length, false);
      }
    };
  }

  public void updateContent(MtpObjectInfo paramMtpObjectInfo)
  {
    if ((this.mObjectId == paramMtpObjectInfo.getObjectHandle()) && (this.mDateTaken == paramMtpObjectInfo.getDateCreated()))
      return;
    this.mObjectId = paramMtpObjectInfo.getObjectHandle();
    this.mDateTaken = paramMtpObjectInfo.getDateCreated();
    this.mDataVersion = nextVersionNumber();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MtpImage
 * JD-Core Version:    0.5.4
 */