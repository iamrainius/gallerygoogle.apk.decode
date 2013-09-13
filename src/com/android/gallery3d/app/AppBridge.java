package com.android.gallery3d.app;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.gallery3d.ui.ScreenNail;

public abstract class AppBridge
  implements Parcelable
{
  public abstract ScreenNail attachScreenNail();

  public int describeContents()
  {
    return 0;
  }

  public abstract void detachScreenNail();

  public abstract boolean isPanorama();

  public abstract boolean isStaticCamera();

  public abstract void onFullScreenChanged(boolean paramBoolean);

  public abstract boolean onSingleTapUp(int paramInt1, int paramInt2);

  public abstract void setServer(Server paramServer);

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
  }

  public static abstract interface Server
  {
    public abstract void addSecureAlbumItem(boolean paramBoolean, int paramInt);

    public abstract void notifyScreenNailChanged();

    public abstract void setSwipingEnabled(boolean paramBoolean);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.AppBridge
 * JD-Core Version:    0.5.4
 */