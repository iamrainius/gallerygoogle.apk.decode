package com.google.android.gms.common.internal;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IGmsServiceBroker extends IInterface
{
  public abstract void getPanoramaService(IGmsCallbacks paramIGmsCallbacks, int paramInt, String paramString, Bundle paramBundle)
    throws RemoteException;

  public abstract void getPlusService(IGmsCallbacks paramIGmsCallbacks, int paramInt, String paramString1, String paramString2, String[] paramArrayOfString, String paramString3, Bundle paramBundle)
    throws RemoteException;

  public static abstract class Stub extends Binder
    implements IGmsServiceBroker
  {
    public Stub()
    {
      attachInterface(this, "com.google.android.gms.common.internal.IGmsServiceBroker");
    }

    public static IGmsServiceBroker asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null)
        return null;
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
      if ((localIInterface != null) && (localIInterface instanceof IGmsServiceBroker))
        return (IGmsServiceBroker)localIInterface;
      return new Proxy(paramIBinder);
    }

    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default:
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902:
        paramParcel2.writeString("com.google.android.gms.common.internal.IGmsServiceBroker");
        return true;
      case 1:
        paramParcel1.enforceInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
        IGmsCallbacks localIGmsCallbacks2 = IGmsCallbacks.Stub.asInterface(paramParcel1.readStrongBinder());
        int j = paramParcel1.readInt();
        String str2 = paramParcel1.readString();
        String str3 = paramParcel1.readString();
        String[] arrayOfString = paramParcel1.createStringArray();
        String str4 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0);
        for (Bundle localBundle2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle2 = null)
        {
          getPlusService(localIGmsCallbacks2, j, str2, str3, arrayOfString, str4, localBundle2);
          paramParcel2.writeNoException();
          return true;
        }
      case 2:
      }
      paramParcel1.enforceInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
      IGmsCallbacks localIGmsCallbacks1 = IGmsCallbacks.Stub.asInterface(paramParcel1.readStrongBinder());
      int i = paramParcel1.readInt();
      String str1 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0);
      for (Bundle localBundle1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle1 = null)
      {
        getPanoramaService(localIGmsCallbacks1, i, str1, localBundle1);
        paramParcel2.writeNoException();
        return true;
      }
    }

    private static class Proxy
      implements IGmsServiceBroker
    {
      private IBinder mRemote;

      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }

      public IBinder asBinder()
      {
        return this.mRemote;
      }

      public void getPanoramaService(IGmsCallbacks paramIGmsCallbacks, int paramInt, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.common.internal.IGmsServiceBroker");
          IBinder localIBinder;
          if (paramIGmsCallbacks != null)
          {
            localIBinder = paramIGmsCallbacks.asBinder();
            localParcel1.writeStrongBinder(localIBinder);
            localParcel1.writeInt(paramInt);
            localParcel1.writeString(paramString);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(2, localParcel1, localParcel2, 0);
              localParcel2.readException();
              return;
            }
          }
          else
          {
            localIBinder = null;
          }
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }

      public void getPlusService(IGmsCallbacks paramIGmsCallbacks, int paramInt, String paramString1, String paramString2, String[] paramArrayOfString, String paramString3, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.common.internal.IGmsServiceBroker");
          IBinder localIBinder;
          if (paramIGmsCallbacks != null)
          {
            localIBinder = paramIGmsCallbacks.asBinder();
            localParcel1.writeStrongBinder(localIBinder);
            localParcel1.writeInt(paramInt);
            localParcel1.writeString(paramString1);
            localParcel1.writeString(paramString2);
            localParcel1.writeStringArray(paramArrayOfString);
            localParcel1.writeString(paramString3);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
              localParcel2.readException();
              return;
            }
          }
          else
          {
            localIBinder = null;
          }
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.gms.common.internal.IGmsServiceBroker
 * JD-Core Version:    0.5.4
 */