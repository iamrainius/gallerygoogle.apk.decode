package com.google.android.apps.lightcycle.util;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationProvider
{
  private static final String TAG = LocationProvider.class.getSimpleName();
  private Location currentLocation;
  private LocationListener gpsLocationListener;
  private final LocationManager locationManager;
  private LocationListener networkLocationListener;

  public LocationProvider(LocationManager paramLocationManager)
  {
    this.locationManager = paramLocationManager;
  }

  private boolean isBetterLocation(Location paramLocation1, Location paramLocation2)
  {
    if (paramLocation2 == null)
      return true;
    long l = paramLocation1.getTime() - paramLocation2.getTime();
    int i;
    label27: int j;
    if (l > 120000L)
    {
      i = 1;
      if (l >= -120000L)
        break label60;
      j = 1;
      label38: if (l <= 0L)
        break label66;
    }
    for (int k = 1; i != 0; k = 0)
    {
      return true;
      i = 0;
      break label27:
      label60: j = 0;
      label66: break label38:
    }
    if (j != 0)
      return false;
    int i1 = (int)(paramLocation1.getAccuracy() - paramLocation2.getAccuracy());
    int i2;
    label99: int i3;
    if (i1 > 0)
    {
      i2 = 1;
      if (i1 >= 0)
        break label145;
      i3 = 1;
      label107: if (i1 <= 200)
        break label151;
    }
    boolean bool;
    for (int i4 = 1; ; i4 = 0)
    {
      bool = isSameProvider(paramLocation1.getProvider(), paramLocation2.getProvider());
      if (i3 == 0)
        break;
      return true;
      i2 = 0;
      break label99:
      label145: i3 = 0;
      label151: break label107:
    }
    if ((k != 0) && (i2 == 0))
      return true;
    return (k != 0) && (i4 == 0) && (bool);
  }

  private boolean isRunning()
  {
    return (this.gpsLocationListener != null) && (this.networkLocationListener != null);
  }

  private boolean isSameProvider(String paramString1, String paramString2)
  {
    if (paramString1 == null)
      return paramString2 == null;
    return paramString1.equals(paramString2);
  }

  private void updateCurrentLocation(Location paramLocation)
  {
    if (!isBetterLocation(paramLocation, this.currentLocation))
      return;
    this.currentLocation = paramLocation;
  }

  public Location getCurrentLocation()
  {
    return this.currentLocation;
  }

  public void startProvider()
  {
    if (isRunning())
      Log.d(TAG, "LocationProvider is already running.");
    do
    {
      return;
      if (!this.locationManager.isProviderEnabled("gps"))
        continue;
      this.gpsLocationListener = new MyLocationListener(null);
      this.locationManager.requestLocationUpdates("gps", 5000L, 5.0F, this.gpsLocationListener);
    }
    while (!this.locationManager.isProviderEnabled("network"));
    this.networkLocationListener = new MyLocationListener(null);
    this.locationManager.requestLocationUpdates("network", 5000L, 5.0F, this.networkLocationListener);
  }

  public void stopProvider()
  {
    this.currentLocation = null;
    if (this.gpsLocationListener != null)
    {
      this.locationManager.removeUpdates(this.gpsLocationListener);
      this.gpsLocationListener = null;
    }
    if (this.networkLocationListener == null)
      return;
    this.locationManager.removeUpdates(this.networkLocationListener);
    this.networkLocationListener = null;
  }

  private class MyLocationListener
    implements LocationListener
  {
    private MyLocationListener()
    {
    }

    public void onLocationChanged(Location paramLocation)
    {
      LocationProvider.this.updateCurrentLocation(paramLocation);
    }

    public void onProviderDisabled(String paramString)
    {
    }

    public void onProviderEnabled(String paramString)
    {
    }

    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
    {
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.LocationProvider
 * JD-Core Version:    0.5.4
 */