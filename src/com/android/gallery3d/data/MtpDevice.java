package com.android.gallery3d.data;

import android.annotation.TargetApi;
import android.hardware.usb.UsbDevice;
import android.mtp.MtpObjectInfo;
import android.mtp.MtpStorageInfo;
import android.net.Uri;
import android.util.Log;
import com.android.gallery3d.app.GalleryApp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TargetApi(12)
public class MtpDevice extends MediaSet
{
  private final GalleryApp mApplication;
  private final int mDeviceId;
  private final String mDeviceName;
  private final Path mItemPath;
  private List<MtpObjectInfo> mJpegChildren;
  private final MtpContext mMtpContext;
  private final String mName;
  private final ChangeNotifier mNotifier;

  public MtpDevice(Path paramPath, GalleryApp paramGalleryApp, int paramInt, MtpContext paramMtpContext)
  {
    this(paramPath, paramGalleryApp, paramInt, MtpDeviceSet.getDeviceName(paramMtpContext, paramInt), paramMtpContext);
  }

  public MtpDevice(Path paramPath, GalleryApp paramGalleryApp, int paramInt, String paramString, MtpContext paramMtpContext)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = paramGalleryApp;
    this.mDeviceId = paramInt;
    this.mDeviceName = UsbDevice.getDeviceName(paramInt);
    this.mMtpContext = paramMtpContext;
    this.mName = paramString;
    this.mNotifier = new ChangeNotifier(this, Uri.parse("mtp://"), paramGalleryApp);
    this.mItemPath = Path.fromString("/mtp/item/" + String.valueOf(paramInt));
    this.mJpegChildren = new ArrayList();
  }

  private void collectJpegChildren(int paramInt1, int paramInt2, ArrayList<MtpObjectInfo> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    queryChildren(paramInt1, paramInt2, paramArrayList, localArrayList);
    int i = 0;
    int j = localArrayList.size();
    while (i < j)
    {
      collectJpegChildren(paramInt1, ((MtpObjectInfo)localArrayList.get(i)).getObjectHandle(), paramArrayList);
      ++i;
    }
  }

  public static MtpObjectInfo getObjectInfo(MtpContext paramMtpContext, int paramInt1, int paramInt2)
  {
    String str = UsbDevice.getDeviceName(paramInt1);
    return paramMtpContext.getMtpClient().getObjectInfo(str, paramInt2);
  }

  private List<MtpObjectInfo> loadItems()
  {
    ArrayList localArrayList = new ArrayList();
    List localList = this.mMtpContext.getMtpClient().getStorageList(this.mDeviceName);
    if (localList == null)
      return localArrayList;
    Iterator localIterator = localList.iterator();
    while (true)
    {
      if (localIterator.hasNext());
      collectJpegChildren(((MtpStorageInfo)localIterator.next()).getStorageId(), 0, localArrayList);
    }
  }

  private void queryChildren(int paramInt1, int paramInt2, ArrayList<MtpObjectInfo> paramArrayList1, ArrayList<MtpObjectInfo> paramArrayList2)
  {
    List localList = this.mMtpContext.getMtpClient().getObjectList(this.mDeviceName, paramInt1, paramInt2);
    if (localList == null)
      return;
    Iterator localIterator = localList.iterator();
    while (true)
    {
      if (localIterator.hasNext());
      MtpObjectInfo localMtpObjectInfo = (MtpObjectInfo)localIterator.next();
      int i = localMtpObjectInfo.getFormat();
      switch (i)
      {
      default:
        Log.w("MtpDevice", "other type: name = " + localMtpObjectInfo.getName() + ", format = " + i);
        break;
      case 14337:
      case 14344:
        paramArrayList1.add(localMtpObjectInfo);
        break;
      case 12289:
      }
      paramArrayList2.add(localMtpObjectInfo);
    }
  }

  public boolean Import()
  {
    return this.mMtpContext.copyAlbum(this.mDeviceName, this.mName, this.mJpegChildren);
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    int i = Math.min(paramInt1 + paramInt2, this.mJpegChildren.size());
    DataManager localDataManager = this.mApplication.getDataManager();
    int j = paramInt1;
    if (j < i)
    {
      MtpObjectInfo localMtpObjectInfo = (MtpObjectInfo)this.mJpegChildren.get(j);
      Path localPath = this.mItemPath.getChild(localMtpObjectInfo.getObjectHandle());
      synchronized (DataManager.LOCK)
      {
        MtpImage localMtpImage = (MtpImage)localDataManager.peekMediaObject(localPath);
        if (localMtpImage == null)
        {
          localMtpImage = new MtpImage(localPath, this.mApplication, this.mDeviceId, localMtpObjectInfo, this.mMtpContext);
          localArrayList.add(localMtpImage);
          ++j;
        }
        localMtpImage.updateContent(localMtpObjectInfo);
      }
    }
    return localArrayList;
  }

  public int getMediaItemCount()
  {
    return this.mJpegChildren.size();
  }

  public String getName()
  {
    return this.mName;
  }

  public int getSupportedOperations()
  {
    return 2048;
  }

  public boolean isLeafAlbum()
  {
    return true;
  }

  public long reload()
  {
    if (this.mNotifier.isDirty())
    {
      this.mDataVersion = nextVersionNumber();
      this.mJpegChildren = loadItems();
    }
    return this.mDataVersion;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MtpDevice
 * JD-Core Version:    0.5.4
 */