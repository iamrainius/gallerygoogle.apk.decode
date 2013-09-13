package com.google.android.gms.common;

public abstract interface GooglePlayServicesClient
{
  public static abstract interface ConnectionCallbacks
  {
    public abstract void onConnected();

    public abstract void onDisconnected();
  }

  public static abstract interface OnConnectionFailedListener
  {
    public abstract void onConnectionFailed(ConnectionResult paramConnectionResult);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.gms.common.GooglePlayServicesClient
 * JD-Core Version:    0.5.4
 */