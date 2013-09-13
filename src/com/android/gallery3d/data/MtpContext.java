package com.android.gallery3d.data;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.mtp.MtpDevice;
import android.mtp.MtpObjectInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.android.gallery3d.util.GalleryUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TargetApi(12)
public class MtpContext
  implements MtpClient.Listener
{
  private MtpClient mClient;
  private Context mContext;
  private ScannerClient mScannerClient;

  public MtpContext(Context paramContext)
  {
    this.mContext = paramContext;
    this.mScannerClient = new ScannerClient(paramContext);
    this.mClient = new MtpClient(this.mContext);
  }

  private void notifyDirty()
  {
    this.mContext.getContentResolver().notifyChange(Uri.parse("mtp://"), null);
  }

  private void showToast(int paramInt)
  {
    Toast.makeText(this.mContext, paramInt, 0).show();
  }

  public boolean copyAlbum(String paramString1, String paramString2, List<MtpObjectInfo> paramList)
  {
    File localFile = new File(Environment.getExternalStorageDirectory(), paramString2);
    localFile.mkdirs();
    int i = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      MtpObjectInfo localMtpObjectInfo = (MtpObjectInfo)localIterator.next();
      if (!GalleryUtils.hasSpaceForSize(localMtpObjectInfo.getCompressedSize()))
        continue;
      String str = new File(localFile, localMtpObjectInfo.getName()).getAbsolutePath();
      if (!this.mClient.importFile(paramString1, localMtpObjectInfo.getObjectHandle(), str))
        continue;
      this.mScannerClient.scanPath(str);
      ++i;
    }
    return i == paramList.size();
  }

  public boolean copyFile(String paramString, MtpObjectInfo paramMtpObjectInfo)
  {
    if (GalleryUtils.hasSpaceForSize(paramMtpObjectInfo.getCompressedSize()))
    {
      File localFile = new File(Environment.getExternalStorageDirectory(), "Imported");
      localFile.mkdirs();
      String str = new File(localFile, paramMtpObjectInfo.getName()).getAbsolutePath();
      int i = paramMtpObjectInfo.getObjectHandle();
      if (this.mClient.importFile(paramString, i, str))
      {
        this.mScannerClient.scanPath(str);
        return true;
      }
    }
    else
    {
      Log.w("MtpContext", "No space to import " + paramMtpObjectInfo.getName() + " whose size = " + paramMtpObjectInfo.getCompressedSize());
    }
    return false;
  }

  public void deviceAdded(MtpDevice paramMtpDevice)
  {
    notifyDirty();
    showToast(2131362304);
  }

  public void deviceRemoved(MtpDevice paramMtpDevice)
  {
    notifyDirty();
    showToast(2131362305);
  }

  public MtpClient getMtpClient()
  {
    return this.mClient;
  }

  public void pause()
  {
    this.mClient.removeListener(this);
  }

  public void resume()
  {
    this.mClient.addListener(this);
    notifyDirty();
  }

  private static final class ScannerClient
    implements MediaScannerConnection.MediaScannerConnectionClient
  {
    boolean mConnected;
    Object mLock = new Object();
    ArrayList<String> mPaths = new ArrayList();
    MediaScannerConnection mScannerConnection = new MediaScannerConnection(paramContext, this);

    public ScannerClient(Context paramContext)
    {
    }

    public void onMediaScannerConnected()
    {
      synchronized (this.mLock)
      {
        this.mConnected = true;
        if (this.mPaths.isEmpty())
          break label75;
        Iterator localIterator = this.mPaths.iterator();
        if (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          this.mScannerConnection.scanFile(str, null);
        }
      }
      this.mPaths.clear();
      label75: monitorexit;
    }

    public void onScanCompleted(String paramString, Uri paramUri)
    {
    }

    public void scanPath(String paramString)
    {
      synchronized (this.mLock)
      {
        if (this.mConnected)
        {
          this.mScannerConnection.scanFile(paramString, null);
          return;
        }
        this.mPaths.add(paramString);
        this.mScannerConnection.connect();
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MtpContext
 * JD-Core Version:    0.5.4
 */