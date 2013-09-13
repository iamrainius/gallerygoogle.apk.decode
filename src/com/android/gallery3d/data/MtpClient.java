package com.android.gallery3d.data;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.mtp.MtpDevice;
import android.mtp.MtpObjectInfo;
import android.mtp.MtpStorageInfo;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@TargetApi(12)
public class MtpClient
{
  private final Context mContext;
  private final HashMap<String, MtpDevice> mDevices = new HashMap();
  private final ArrayList<String> mIgnoredDevices = new ArrayList();
  private final ArrayList<Listener> mListeners = new ArrayList();
  private final PendingIntent mPermissionIntent;
  private final ArrayList<String> mRequestPermissionDevices = new ArrayList();
  private final UsbManager mUsbManager;
  private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      String str1 = paramIntent.getAction();
      UsbDevice localUsbDevice = (UsbDevice)paramIntent.getParcelableExtra("device");
      String str2 = localUsbDevice.getDeviceName();
      MtpDevice localMtpDevice;
      synchronized (MtpClient.this.mDevices)
      {
        localMtpDevice = (MtpDevice)MtpClient.this.mDevices.get(str2);
        if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(str1))
        {
          if (localMtpDevice == null)
            localMtpDevice = MtpClient.this.openDeviceLocked(localUsbDevice);
          if (localMtpDevice == null)
            break label365;
          Iterator localIterator1 = MtpClient.this.mListeners.iterator();
          if (!localIterator1.hasNext())
            break label365;
          ((MtpClient.Listener)localIterator1.next()).deviceAdded(localMtpDevice);
        }
      }
      if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(str1))
      {
        if (localMtpDevice == null)
          break label365;
        MtpClient.this.mDevices.remove(str2);
        MtpClient.this.mRequestPermissionDevices.remove(str2);
        MtpClient.this.mIgnoredDevices.remove(str2);
        Iterator localIterator2 = MtpClient.this.mListeners.iterator();
        while (true)
        {
          if (!localIterator2.hasNext())
            break label365;
          ((MtpClient.Listener)localIterator2.next()).deviceRemoved(localMtpDevice);
        }
      }
      if ("android.mtp.MtpClient.action.USB_PERMISSION".equals(str1))
      {
        MtpClient.this.mRequestPermissionDevices.remove(str2);
        boolean bool = paramIntent.getBooleanExtra("permission", false);
        Log.d("MtpClient", "ACTION_USB_PERMISSION: " + bool);
        if (bool)
        {
          if (localMtpDevice == null)
            localMtpDevice = MtpClient.this.openDeviceLocked(localUsbDevice);
          if (localMtpDevice == null)
            break label365;
          Iterator localIterator3 = MtpClient.this.mListeners.iterator();
          while (true)
          {
            if (!localIterator3.hasNext())
              break label365;
            ((MtpClient.Listener)localIterator3.next()).deviceAdded(localMtpDevice);
          }
        }
        MtpClient.this.mIgnoredDevices.add(str2);
      }
      label365: monitorexit;
    }
  };

  public MtpClient(Context paramContext)
  {
    this.mContext = paramContext;
    this.mUsbManager = ((UsbManager)paramContext.getSystemService("usb"));
    this.mPermissionIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent("android.mtp.MtpClient.action.USB_PERMISSION"), 0);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
    localIntentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
    localIntentFilter.addAction("android.mtp.MtpClient.action.USB_PERMISSION");
    paramContext.registerReceiver(this.mUsbReceiver, localIntentFilter);
  }

  public static boolean isCamera(UsbDevice paramUsbDevice)
  {
    int i = paramUsbDevice.getInterfaceCount();
    for (int j = 0; j < i; ++j)
    {
      UsbInterface localUsbInterface = paramUsbDevice.getInterface(j);
      if ((localUsbInterface.getInterfaceClass() == 6) && (localUsbInterface.getInterfaceSubclass() == 1) && (localUsbInterface.getInterfaceProtocol() == 1))
        return true;
    }
    return false;
  }

  private MtpDevice openDeviceLocked(UsbDevice paramUsbDevice)
  {
    String str = paramUsbDevice.getDeviceName();
    if ((isCamera(paramUsbDevice)) && (!this.mIgnoredDevices.contains(str)) && (!this.mRequestPermissionDevices.contains(str)))
    {
      if (this.mUsbManager.hasPermission(paramUsbDevice))
        break label68;
      this.mUsbManager.requestPermission(paramUsbDevice, this.mPermissionIntent);
      this.mRequestPermissionDevices.add(str);
    }
    while (true)
    {
      return null;
      label68: UsbDeviceConnection localUsbDeviceConnection = this.mUsbManager.openDevice(paramUsbDevice);
      if (localUsbDeviceConnection != null)
      {
        MtpDevice localMtpDevice = new MtpDevice(paramUsbDevice);
        if (localMtpDevice.open(localUsbDeviceConnection))
        {
          this.mDevices.put(paramUsbDevice.getDeviceName(), localMtpDevice);
          return localMtpDevice;
        }
        this.mIgnoredDevices.add(str);
      }
      this.mIgnoredDevices.add(str);
    }
  }

  public void addListener(Listener paramListener)
  {
    synchronized (this.mDevices)
    {
      if (!this.mListeners.contains(paramListener))
        this.mListeners.add(paramListener);
      return;
    }
  }

  public MtpDevice getDevice(int paramInt)
  {
    synchronized (this.mDevices)
    {
      MtpDevice localMtpDevice = (MtpDevice)this.mDevices.get(UsbDevice.getDeviceName(paramInt));
      return localMtpDevice;
    }
  }

  public MtpDevice getDevice(String paramString)
  {
    synchronized (this.mDevices)
    {
      MtpDevice localMtpDevice = (MtpDevice)this.mDevices.get(paramString);
      return localMtpDevice;
    }
  }

  public List<MtpDevice> getDeviceList()
  {
    synchronized (this.mDevices)
    {
      Iterator localIterator = this.mUsbManager.getDeviceList().values().iterator();
      UsbDevice localUsbDevice;
      do
      {
        if (!localIterator.hasNext())
          break label73;
        localUsbDevice = (UsbDevice)localIterator.next();
      }
      while (this.mDevices.get(localUsbDevice.getDeviceName()) != null);
      openDeviceLocked(localUsbDevice);
    }
    label73: ArrayList localArrayList = new ArrayList(this.mDevices.values());
    monitorexit;
    return localArrayList;
  }

  public byte[] getObject(String paramString, int paramInt1, int paramInt2)
  {
    MtpDevice localMtpDevice = getDevice(paramString);
    if (localMtpDevice == null)
      return null;
    return localMtpDevice.getObject(paramInt1, paramInt2);
  }

  public MtpObjectInfo getObjectInfo(String paramString, int paramInt)
  {
    MtpDevice localMtpDevice = getDevice(paramString);
    if (localMtpDevice == null)
      return null;
    return localMtpDevice.getObjectInfo(paramInt);
  }

  public List<MtpObjectInfo> getObjectList(String paramString, int paramInt1, int paramInt2)
  {
    MtpDevice localMtpDevice = getDevice(paramString);
    ArrayList localArrayList = null;
    if (localMtpDevice == null);
    int[] arrayOfInt;
    int i;
    int j;
    do
    {
      do
      {
        return localArrayList;
        if (paramInt2 == 0)
          paramInt2 = -1;
        arrayOfInt = localMtpDevice.getObjectHandles(paramInt1, 0, paramInt2);
        localArrayList = null;
      }
      while (arrayOfInt == null);
      i = arrayOfInt.length;
      localArrayList = new ArrayList(i);
      label61: j = 0;
    }
    while (j >= i);
    MtpObjectInfo localMtpObjectInfo = localMtpDevice.getObjectInfo(arrayOfInt[j]);
    if (localMtpObjectInfo == null)
      Log.w("MtpClient", "getObjectInfo failed");
    while (true)
    {
      ++j;
      break label61:
      localArrayList.add(localMtpObjectInfo);
    }
  }

  public List<MtpStorageInfo> getStorageList(String paramString)
  {
    MtpDevice localMtpDevice = getDevice(paramString);
    ArrayList localArrayList = null;
    if (localMtpDevice == null);
    int[] arrayOfInt;
    int i;
    int j;
    do
    {
      do
      {
        return localArrayList;
        arrayOfInt = localMtpDevice.getStorageIds();
        localArrayList = null;
      }
      while (arrayOfInt == null);
      i = arrayOfInt.length;
      localArrayList = new ArrayList(i);
      label45: j = 0;
    }
    while (j >= i);
    MtpStorageInfo localMtpStorageInfo = localMtpDevice.getStorageInfo(arrayOfInt[j]);
    if (localMtpStorageInfo == null)
      Log.w("MtpClient", "getStorageInfo failed");
    while (true)
    {
      ++j;
      break label45:
      localArrayList.add(localMtpStorageInfo);
    }
  }

  public byte[] getThumbnail(String paramString, int paramInt)
  {
    MtpDevice localMtpDevice = getDevice(paramString);
    if (localMtpDevice == null)
      return null;
    return localMtpDevice.getThumbnail(paramInt);
  }

  public boolean importFile(String paramString1, int paramInt, String paramString2)
  {
    MtpDevice localMtpDevice = getDevice(paramString1);
    if (localMtpDevice == null)
      return false;
    return localMtpDevice.importFile(paramInt, paramString2);
  }

  public void removeListener(Listener paramListener)
  {
    synchronized (this.mDevices)
    {
      this.mListeners.remove(paramListener);
      return;
    }
  }

  public static abstract interface Listener
  {
    public abstract void deviceAdded(MtpDevice paramMtpDevice);

    public abstract void deviceRemoved(MtpDevice paramMtpDevice);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MtpClient
 * JD-Core Version:    0.5.4
 */