package com.google.android.gms.panorama.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.GmsClient;
import com.google.android.gms.common.internal.GmsClient.CallbackProxy;
import com.google.android.gms.common.internal.IGmsServiceBroker;
import com.google.android.gms.panorama.PanoramaClient.OnPanoramaInfoLoadedListener;

public class PanoramaClientImpl extends GmsClient<IPanoramaService>
{
  public PanoramaClientImpl(Context paramContext, GooglePlayServicesClient.ConnectionCallbacks paramConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramConnectionCallbacks, paramOnConnectionFailedListener, (String[])null);
  }

  public IPanoramaService createServiceInterface(IBinder paramIBinder)
  {
    return IPanoramaService.Stub.asInterface(paramIBinder);
  }

  protected String getServiceDescriptor()
  {
    return "com.google.android.gms.panorama.internal.IPanoramaService";
  }

  protected void getServiceFromBroker(IGmsServiceBroker paramIGmsServiceBroker, GmsClient<IPanoramaService>.GmsCallbacks paramGmsClient)
    throws RemoteException
  {
    Bundle localBundle = new Bundle();
    paramIGmsServiceBroker.getPanoramaService(paramGmsClient, 1, getContext().getPackageName(), localBundle);
  }

  protected String getStartServiceAction()
  {
    return "com.google.android.gms.panorama.service.START";
  }

  public void loadPanoramaInfo(PanoramaClient.OnPanoramaInfoLoadedListener paramOnPanoramaInfoLoadedListener, Uri paramUri, Bundle paramBundle, boolean paramBoolean)
  {
    checkConnected();
    if (paramBoolean)
      getContext().grantUriPermission("com.google.android.gms", paramUri, 1);
    Uri localUri;
    if (paramBoolean)
      localUri = paramUri;
    while (true)
    {
      PanoramaInfoLoadedBinderCallbacks localPanoramaInfoLoadedBinderCallbacks = new PanoramaInfoLoadedBinderCallbacks(paramOnPanoramaInfoLoadedListener, localUri);
      try
      {
        ((IPanoramaService)getService()).loadPanoramaInfo(localPanoramaInfoLoadedBinderCallbacks, paramUri, paramBundle, paramBoolean);
        return;
        localUri = null;
      }
      catch (RemoteException localRemoteException)
      {
        localPanoramaInfoLoadedBinderCallbacks.onPanoramaInfoLoaded(8, null, 0, null);
      }
    }
  }

  final class PanoramaInfoLoadedBinderCallbacks extends IPanoramaCallbacks.Stub
  {
    private final PanoramaClient.OnPanoramaInfoLoadedListener mListener;
    private final Uri mRevokeUri;

    public PanoramaInfoLoadedBinderCallbacks(PanoramaClient.OnPanoramaInfoLoadedListener paramUri, Uri arg3)
    {
      this.mListener = paramUri;
      Object localObject;
      this.mRevokeUri = localObject;
    }

    public void onPanoramaInfoLoaded(int paramInt1, Bundle paramBundle, int paramInt2, Intent paramIntent)
    {
      if (this.mRevokeUri != null)
        PanoramaClientImpl.this.getContext().revokeUriPermission(this.mRevokeUri, 1);
      PendingIntent localPendingIntent = null;
      if (paramBundle != null)
        localPendingIntent = (PendingIntent)paramBundle.getParcelable("pendingIntent");
      ConnectionResult localConnectionResult = new ConnectionResult(paramInt1, localPendingIntent);
      PanoramaClientImpl.this.doCallback(new PanoramaClientImpl.PanoramaInfoLoadedCallback(PanoramaClientImpl.this, this.mListener, localConnectionResult, paramInt2, paramIntent));
    }
  }

  final class PanoramaInfoLoadedCallback extends GmsClient<IPanoramaService>.CallbackProxy<PanoramaClient.OnPanoramaInfoLoadedListener>
  {
    public final ConnectionResult status;
    public final int type;
    public final Intent viewerIntent;

    public PanoramaInfoLoadedCallback(PanoramaClient.OnPanoramaInfoLoadedListener paramConnectionResult, ConnectionResult paramInt, int paramIntent, Intent arg5)
    {
      super(PanoramaClientImpl.this, paramConnectionResult);
      this.status = paramInt;
      this.type = paramIntent;
      Object localObject;
      this.viewerIntent = localObject;
    }

    protected void deliverCallback(PanoramaClient.OnPanoramaInfoLoadedListener paramOnPanoramaInfoLoadedListener)
    {
      if (paramOnPanoramaInfoLoadedListener == null)
        return;
      paramOnPanoramaInfoLoadedListener.onPanoramaInfoLoaded(this.status, this.type, this.viewerIntent);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.gms.panorama.internal.PanoramaClientImpl
 * JD-Core Version:    0.5.4
 */