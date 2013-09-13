package com.google.android.gms.panorama;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.panorama.internal.PanoramaClientImpl;

public class PanoramaClient
  implements GooglePlayServicesClient
{
  private final PanoramaClientImpl mClientImpl;

  public PanoramaClient(Context paramContext, GooglePlayServicesClient.ConnectionCallbacks paramConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this.mClientImpl = new PanoramaClientImpl(paramContext, paramConnectionCallbacks, paramOnConnectionFailedListener);
  }

  public void connect()
  {
    this.mClientImpl.connect();
  }

  public void disconnect()
  {
    this.mClientImpl.disconnect();
  }

  public boolean isConnected()
  {
    return this.mClientImpl.isConnected();
  }

  public void loadPanoramaInfoAndGrantAccess(OnPanoramaInfoLoadedListener paramOnPanoramaInfoLoadedListener, Uri paramUri, Bundle paramBundle)
  {
    this.mClientImpl.loadPanoramaInfo(paramOnPanoramaInfoLoadedListener, paramUri, paramBundle, true);
  }

  public static abstract interface OnPanoramaInfoLoadedListener
  {
    public abstract void onPanoramaInfoLoaded(ConnectionResult paramConnectionResult, int paramInt, Intent paramIntent);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.gms.panorama.PanoramaClient
 * JD-Core Version:    0.5.4
 */