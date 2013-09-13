package com.android.camera;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class LocationManager
{
  private Context mContext;
  private Listener mListener;
  LocationListener[] mLocationListeners;
  private android.location.LocationManager mLocationManager;
  private boolean mRecordLocation;

  public LocationManager(Context paramContext, Listener paramListener)
  {
    LocationListener[] arrayOfLocationListener = new LocationListener[2];
    arrayOfLocationListener[0] = new LocationListener("gps");
    arrayOfLocationListener[1] = new LocationListener("network");
    this.mLocationListeners = arrayOfLocationListener;
    this.mContext = paramContext;
    this.mListener = paramListener;
  }

  // ERROR //
  private void startReceivingLocationUpdates()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 47	com/android/camera/LocationManager:mLocationManager	Landroid/location/LocationManager;
    //   4: ifnonnull +19 -> 23
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 32	com/android/camera/LocationManager:mContext	Landroid/content/Context;
    //   12: ldc 49
    //   14: invokevirtual 55	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   17: checkcast 57	android/location/LocationManager
    //   20: putfield 47	com/android/camera/LocationManager:mLocationManager	Landroid/location/LocationManager;
    //   23: aload_0
    //   24: getfield 47	com/android/camera/LocationManager:mLocationManager	Landroid/location/LocationManager;
    //   27: ifnull +66 -> 93
    //   30: aload_0
    //   31: getfield 47	com/android/camera/LocationManager:mLocationManager	Landroid/location/LocationManager;
    //   34: ldc 28
    //   36: ldc2_w 58
    //   39: fconst_0
    //   40: aload_0
    //   41: getfield 30	com/android/camera/LocationManager:mLocationListeners	[Lcom/android/camera/LocationManager$LocationListener;
    //   44: iconst_1
    //   45: aaload
    //   46: invokevirtual 63	android/location/LocationManager:requestLocationUpdates	(Ljava/lang/String;JFLandroid/location/LocationListener;)V
    //   49: aload_0
    //   50: getfield 47	com/android/camera/LocationManager:mLocationManager	Landroid/location/LocationManager;
    //   53: ldc 23
    //   55: ldc2_w 58
    //   58: fconst_0
    //   59: aload_0
    //   60: getfield 30	com/android/camera/LocationManager:mLocationListeners	[Lcom/android/camera/LocationManager$LocationListener;
    //   63: iconst_0
    //   64: aaload
    //   65: invokevirtual 63	android/location/LocationManager:requestLocationUpdates	(Ljava/lang/String;JFLandroid/location/LocationListener;)V
    //   68: aload_0
    //   69: getfield 34	com/android/camera/LocationManager:mListener	Lcom/android/camera/LocationManager$Listener;
    //   72: ifnull +13 -> 85
    //   75: aload_0
    //   76: getfield 34	com/android/camera/LocationManager:mListener	Lcom/android/camera/LocationManager$Listener;
    //   79: iconst_0
    //   80: invokeinterface 69 2 0
    //   85: ldc 71
    //   87: ldc 72
    //   89: invokestatic 78	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   92: pop
    //   93: return
    //   94: astore 8
    //   96: ldc 71
    //   98: ldc 80
    //   100: aload 8
    //   102: invokestatic 84	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   105: pop
    //   106: goto -57 -> 49
    //   109: astore_1
    //   110: ldc 71
    //   112: new 86	java/lang/StringBuilder
    //   115: dup
    //   116: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   119: ldc 89
    //   121: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload_1
    //   125: invokevirtual 97	java/lang/IllegalArgumentException:getMessage	()Ljava/lang/String;
    //   128: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   134: invokestatic 78	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   137: pop
    //   138: goto -89 -> 49
    //   141: astore 6
    //   143: ldc 71
    //   145: ldc 80
    //   147: aload 6
    //   149: invokestatic 84	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   152: pop
    //   153: goto -68 -> 85
    //   156: astore_3
    //   157: ldc 71
    //   159: new 86	java/lang/StringBuilder
    //   162: dup
    //   163: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   166: ldc 89
    //   168: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   171: aload_3
    //   172: invokevirtual 97	java/lang/IllegalArgumentException:getMessage	()Ljava/lang/String;
    //   175: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   181: invokestatic 78	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   184: pop
    //   185: goto -100 -> 85
    //
    // Exception table:
    //   from	to	target	type
    //   30	49	94	java/lang/SecurityException
    //   30	49	109	java/lang/IllegalArgumentException
    //   49	85	141	java/lang/SecurityException
    //   49	85	156	java/lang/IllegalArgumentException
  }

  private void stopReceivingLocationUpdates()
  {
    int i;
    if (this.mLocationManager != null)
      i = 0;
    while (true)
    {
      if (i < this.mLocationListeners.length);
      try
      {
        this.mLocationManager.removeUpdates(this.mLocationListeners[i]);
        label31: ++i;
      }
      catch (Exception localException)
      {
        Log.i("LocationManager", "fail to remove location listners, ignore", localException);
        break label31:
        Log.d("LocationManager", "stopReceivingLocationUpdates");
        if (this.mListener == null)
          return;
        this.mListener.hideGpsOnScreenIndicator();
      }
    }
  }

  public Location getCurrentLocation()
  {
    Location localLocation;
    if (!this.mRecordLocation)
    {
      localLocation = null;
      return localLocation;
    }
    for (int i = 0; i < this.mLocationListeners.length; ++i)
    {
      localLocation = this.mLocationListeners[i].current();
      if (localLocation != null);
    }
    Log.d("LocationManager", "No location received yet.");
    return null;
  }

  public void recordLocation(boolean paramBoolean)
  {
    if (this.mRecordLocation != paramBoolean)
    {
      this.mRecordLocation = paramBoolean;
      if (!paramBoolean)
        break label22;
      startReceivingLocationUpdates();
    }
    return;
    label22: stopReceivingLocationUpdates();
  }

  public static abstract interface Listener
  {
    public abstract void hideGpsOnScreenIndicator();

    public abstract void showGpsOnScreenIndicator(boolean paramBoolean);
  }

  private class LocationListener
    implements LocationListener
  {
    Location mLastLocation;
    String mProvider;
    boolean mValid = false;

    public LocationListener(String arg2)
    {
      Object localObject;
      this.mProvider = localObject;
      this.mLastLocation = new Location(this.mProvider);
    }

    public Location current()
    {
      if (this.mValid)
        return this.mLastLocation;
      return null;
    }

    public void onLocationChanged(Location paramLocation)
    {
      if ((paramLocation.getLatitude() == 0.0D) && (paramLocation.getLongitude() == 0.0D))
        return;
      if ((LocationManager.this.mListener != null) && (LocationManager.this.mRecordLocation) && ("gps".equals(this.mProvider)))
        LocationManager.this.mListener.showGpsOnScreenIndicator(true);
      if (!this.mValid)
        Log.d("LocationManager", "Got first location.");
      this.mLastLocation.set(paramLocation);
      this.mValid = true;
    }

    public void onProviderDisabled(String paramString)
    {
      this.mValid = false;
    }

    public void onProviderEnabled(String paramString)
    {
    }

    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
    {
      switch (paramInt)
      {
      default:
      case 0:
      case 1:
      }
      do
      {
        return;
        this.mValid = false;
      }
      while ((LocationManager.this.mListener == null) || (!LocationManager.this.mRecordLocation) || (!"gps".equals(paramString)));
      LocationManager.this.mListener.showGpsOnScreenIndicator(false);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.LocationManager
 * JD-Core Version:    0.5.4
 */