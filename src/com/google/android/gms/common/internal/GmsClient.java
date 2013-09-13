package com.google.android.gms.common.internal;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.util.ArrayList;

public abstract class GmsClient<T extends IInterface>
  implements GooglePlayServicesClient
{
  public static final String[] GOOGLE_PLUS_REQUIRED_FEATURES = { "service_esmobile", "service_googleme" };
  private final ArrayList<GmsClient<T>.CallbackProxy<?>> mCallbackProxyList = new ArrayList();
  private ServiceConnection mConnection;
  private ArrayList<GooglePlayServicesClient.OnConnectionFailedListener> mConnectionFailedListeners;
  private ArrayList<GooglePlayServicesClient.ConnectionCallbacks> mConnectionListeners;
  final ArrayList<GooglePlayServicesClient.ConnectionCallbacks> mConnectionListenersRemoved = new ArrayList();
  private final Context mContext;
  final Handler mHandler;
  private boolean mIsProcessingConnectionCallback = false;
  private boolean mIsProcessingOnConnectionFailed = false;
  boolean mPerformConnectionCallbacks = false;
  private final String[] mScopes;
  private T mService;

  protected GmsClient(Context paramContext, GooglePlayServicesClient.ConnectionCallbacks paramConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener paramOnConnectionFailedListener, String[] paramArrayOfString)
  {
    if (Looper.getMainLooper().getThread() != Thread.currentThread())
      throw new IllegalStateException("Clients must be created on the UI thread.");
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext));
    this.mConnectionListeners = new ArrayList();
    this.mConnectionListeners.add(Preconditions.checkNotNull(paramConnectionCallbacks));
    this.mConnectionFailedListeners = new ArrayList();
    this.mConnectionFailedListeners.add(Preconditions.checkNotNull(paramOnConnectionFailedListener));
    this.mHandler = new CallbackHandler();
    this.mScopes = paramArrayOfString;
  }

  protected final void checkConnected()
  {
    if (isConnected())
      return;
    throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
  }

  public void connect()
  {
    this.mPerformConnectionCallbacks = true;
    int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mContext);
    if (i != 0)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(3, Integer.valueOf(i)));
      return;
    }
    Intent localIntent = new Intent(getStartServiceAction());
    if (this.mConnection != null)
    {
      Log.e("GmsClient", "Calling connect() while still connected, missing disconnect().");
      this.mService = null;
      this.mContext.unbindService(this.mConnection);
    }
    this.mConnection = new GmsServiceConnection();
    boolean bool = this.mContext.bindService(localIntent, this.mConnection, 129);
    Log.i("GmsClient", "connect: bindService returned " + bool + " for " + localIntent);
  }

  protected abstract T createServiceInterface(IBinder paramIBinder);

  public void disconnect()
  {
    this.mPerformConnectionCallbacks = false;
    synchronized (this.mCallbackProxyList)
    {
      int i = this.mCallbackProxyList.size();
      for (int j = 0; j < i; ++j)
        ((CallbackProxy)this.mCallbackProxyList.get(j)).removeListener();
      this.mCallbackProxyList.clear();
      this.mService = null;
      if (this.mConnection != null)
      {
        this.mContext.unbindService(this.mConnection);
        this.mConnection = null;
      }
      return;
    }
  }

  public final void doCallback(GmsClient<T>.CallbackProxy<?> paramGmsClient)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(2, paramGmsClient));
  }

  public final Context getContext()
  {
    return this.mContext;
  }

  protected final T getService()
  {
    checkConnected();
    return this.mService;
  }

  protected abstract String getServiceDescriptor();

  protected abstract void getServiceFromBroker(IGmsServiceBroker paramIGmsServiceBroker, GmsClient<T>.GmsCallbacks paramGmsClient)
    throws RemoteException;

  protected abstract String getStartServiceAction();

  public boolean isConnected()
  {
    return this.mService != null;
  }

  protected final void onConnectionFailure(ConnectionResult paramConnectionResult)
  {
    this.mHandler.removeMessages(4);
    while (true)
    {
      int i;
      synchronized (this.mConnectionFailedListeners)
      {
        this.mIsProcessingOnConnectionFailed = true;
        ArrayList localArrayList2 = this.mConnectionFailedListeners;
        i = 0;
        int j = localArrayList2.size();
        if (i < j)
        {
          if (!this.mPerformConnectionCallbacks)
            return;
          if (this.mConnectionFailedListeners.contains(localArrayList2.get(i)))
            ((GooglePlayServicesClient.OnConnectionFailedListener)localArrayList2.get(i)).onConnectionFailed(paramConnectionResult);
        }
        else
        {
          this.mIsProcessingOnConnectionFailed = false;
          return;
        }
      }
      ++i;
    }
  }

  protected final void onConnectionSuccess()
  {
    boolean bool1 = true;
    while (true)
    {
      int i;
      synchronized (this.mConnectionListeners)
      {
        if (!this.mIsProcessingConnectionCallback)
        {
          bool2 = bool1;
          Preconditions.checkState(bool2);
          this.mHandler.removeMessages(4);
          this.mIsProcessingConnectionCallback = true;
          if (this.mConnectionListenersRemoved.size() != 0)
            break label157;
          Preconditions.checkState(bool1);
          ArrayList localArrayList2 = this.mConnectionListeners;
          i = 0;
          int j = localArrayList2.size();
          if ((i >= j) || (!this.mPerformConnectionCallbacks) || (!isConnected()))
          {
            this.mConnectionListenersRemoved.clear();
            this.mIsProcessingConnectionCallback = false;
            return;
          }
          this.mConnectionListenersRemoved.size();
          if (!this.mConnectionListenersRemoved.contains(localArrayList2.get(i)))
            ((GooglePlayServicesClient.ConnectionCallbacks)localArrayList2.get(i)).onConnected();
        }
      }
      boolean bool2 = false;
      continue;
      label157: bool1 = false;
      continue;
      ++i;
    }
  }

  protected final void onDisconnection()
  {
    this.mHandler.removeMessages(4);
    while (true)
    {
      int i;
      synchronized (this.mConnectionListeners)
      {
        this.mIsProcessingConnectionCallback = true;
        ArrayList localArrayList2 = this.mConnectionListeners;
        i = 0;
        int j = localArrayList2.size();
        if ((i >= j) || (!this.mPerformConnectionCallbacks))
        {
          this.mIsProcessingConnectionCallback = false;
          return;
        }
        if (this.mConnectionListeners.contains(localArrayList2.get(i)))
          ((GooglePlayServicesClient.ConnectionCallbacks)localArrayList2.get(i)).onDisconnected();
      }
      ++i;
    }
  }

  protected final void onServiceBrokerBound(IBinder paramIBinder)
  {
    try
    {
      getServiceFromBroker(IGmsServiceBroker.Stub.asInterface(paramIBinder), new GmsCallbacks());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("GmsClient", "service died");
    }
  }

  final class CallbackHandler extends Handler
  {
    CallbackHandler()
    {
    }

    public void handleMessage(Message paramMessage)
    {
      if (paramMessage.what == 3)
        GmsClient.this.onConnectionFailure(new ConnectionResult(((Integer)paramMessage.obj).intValue(), null));
      do
      {
        return;
        if (paramMessage.what != 4)
          continue;
        synchronized (GmsClient.this.mConnectionListeners)
        {
          if ((GmsClient.this.mPerformConnectionCallbacks) && (GmsClient.this.isConnected()) && (GmsClient.this.mConnectionListeners.contains(paramMessage.obj)))
            ((GooglePlayServicesClient.ConnectionCallbacks)paramMessage.obj).onConnected();
          return;
        }
      }
      while (((paramMessage.what == 2) && (!GmsClient.this.isConnected())) || ((paramMessage.what != 2) && (paramMessage.what != 1)));
      ((GmsClient.CallbackProxy)paramMessage.obj).deliverCallback();
    }
  }

  protected abstract class CallbackProxy<TListener>
  {
    private TListener mListener;

    public CallbackProxy()
    {
      Object localObject1;
      this.mListener = localObject1;
      synchronized (GmsClient.this.mCallbackProxyList)
      {
        GmsClient.this.mCallbackProxyList.add(this);
        return;
      }
    }

    public void deliverCallback()
    {
      monitorenter;
      try
      {
        Object localObject2 = this.mListener;
        monitorexit;
        return;
      }
      finally
      {
        monitorexit;
      }
    }

    protected abstract void deliverCallback(TListener paramTListener);

    public void removeListener()
    {
      monitorenter;
      try
      {
        this.mListener = null;
        return;
      }
      finally
      {
        monitorexit;
      }
    }
  }

  protected final class GmsCallbacks extends IGmsCallbacks.Stub
  {
    protected GmsCallbacks()
    {
    }

    public void onPostInitComplete(int paramInt, IBinder paramIBinder, Bundle paramBundle)
    {
      GmsClient.this.mHandler.sendMessage(GmsClient.this.mHandler.obtainMessage(1, new GmsClient.PostInitCallback(GmsClient.this, paramInt, paramIBinder, paramBundle)));
    }
  }

  final class GmsServiceConnection
    implements ServiceConnection
  {
    GmsServiceConnection()
    {
    }

    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      Log.d("GmsClient", "service broker connected, binder: " + paramIBinder);
      GmsClient.this.onServiceBrokerBound(paramIBinder);
    }

    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      Log.d("GmsClient", "service disconnected: " + paramComponentName);
      GmsClient.access$002(GmsClient.this, null);
      GmsClient.this.onDisconnection();
    }
  }

  protected final class PostInitCallback extends GmsClient<T>.CallbackProxy<Boolean>
  {
    public final Bundle resolution;
    public final IBinder service;
    public final int statusCode;

    public PostInitCallback(int paramIBinder, IBinder paramBundle, Bundle arg4)
    {
      super(GmsClient.this, Boolean.valueOf(true));
      this.statusCode = paramIBinder;
      this.service = paramBundle;
      Object localObject;
      this.resolution = localObject;
    }

    protected void deliverCallback(Boolean paramBoolean)
    {
      if (paramBoolean == null)
        return;
      switch (this.statusCode)
      {
      default:
        PendingIntent localPendingIntent = (PendingIntent)this.resolution.getParcelable("pendingIntent");
        GmsClient.this.onConnectionFailure(new ConnectionResult(this.statusCode, localPendingIntent));
        return;
      case 0:
      }
      try
      {
        String str = this.service.getInterfaceDescriptor();
        if (!GmsClient.this.getServiceDescriptor().equals(str))
          break label135;
        Log.d("GmsClient", "bound to service broker");
        GmsClient.access$002(GmsClient.this, GmsClient.this.createServiceInterface(this.service));
        if (GmsClient.this.mService == null)
          break label135;
        GmsClient.this.onConnectionSuccess();
        label135: return;
      }
      catch (RemoteException localRemoteException)
      {
        GmsClient.this.mContext.unbindService(GmsClient.this.mConnection);
        GmsClient.access$302(GmsClient.this, null);
        GmsClient.access$002(GmsClient.this, null);
        GmsClient.this.onConnectionFailure(new ConnectionResult(8, null));
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.gms.common.internal.GmsClient
 * JD-Core Version:    0.5.4
 */