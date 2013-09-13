package com.google.android.picasasync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;

public class PicasaFacade
{
  private static PicasaFacade sInstance;
  private final Uri mAlbumCoversUri;
  private final Uri mAlbumsUri;
  private final Context mContext;
  private final Uri mPhotosUri;
  private final Uri mPostAlbumsUri;
  private final Uri mPostPhotosUri;
  private final Uri mSettingsUri;
  private final Uri mSyncRequestUri;
  private final Uri mUploadRecordsUri;
  private final Uri mUploadsUri;
  private final Uri mUsersUri;

  private PicasaFacade(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    enableProviderAndSyncService();
    Uri localUri = Uri.parse("content://com.google.android.gallery3d.GooglePhotoProvider");
    this.mPhotosUri = Uri.withAppendedPath(localUri, "photos");
    this.mAlbumsUri = Uri.withAppendedPath(localUri, "albums");
    this.mPostAlbumsUri = Uri.withAppendedPath(localUri, "posts_album");
    this.mPostPhotosUri = Uri.withAppendedPath(localUri, "posts");
    this.mUsersUri = Uri.withAppendedPath(localUri, "users");
    this.mUploadsUri = Uri.withAppendedPath(localUri, "uploads");
    this.mUploadRecordsUri = Uri.withAppendedPath(localUri, "upload_records");
    this.mSettingsUri = Uri.withAppendedPath(localUri, "settings");
    this.mSyncRequestUri = Uri.withAppendedPath(localUri, "sync_request");
    this.mAlbumCoversUri = Uri.withAppendedPath(localUri, "albumcovers");
  }

  private void enableProviderAndSyncService()
  {
    this.mContext.getPackageManager().setComponentEnabledSetting(new ComponentName(this.mContext, PicasaSyncService.class), 0, 1);
  }

  public static PicasaFacade get(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sInstance == null)
        sInstance = new PicasaFacade(paramContext);
      PicasaFacade localPicasaFacade = sInstance;
      return localPicasaFacade;
    }
    finally
    {
      monitorexit;
    }
  }

  public void enablePicasasync(boolean paramBoolean)
  {
    int i = 0;
    PackageManager localPackageManager = this.mContext.getPackageManager();
    if (paramBoolean)
      if (localPackageManager.getComponentEnabledSetting(new ComponentName(this.mContext, ConnectivityReceiver.class)) != 1);
    do
    {
      return;
      Log.d("PicasaFacade", "enable picasasync in gallery");
      localPackageManager.setComponentEnabledSetting(new ComponentName(this.mContext, ConnectivityReceiver.class), 1, 1);
      localPackageManager.setComponentEnabledSetting(new ComponentName(this.mContext, BatteryReceiver.class), 1, 1);
      Account[] arrayOfAccount2 = AccountManager.get(this.mContext).getAccountsByType("com.google");
      int l = arrayOfAccount2.length;
      while (true)
      {
        if (i < l);
        Account localAccount2 = arrayOfAccount2[i];
        if (ContentResolver.getIsSyncable(localAccount2, "com.google.android.gallery3d.GooglePhotoProvider") == 0)
        {
          ContentResolver.setIsSyncable(localAccount2, "com.google.android.gallery3d.GooglePhotoProvider", -1);
          ContentResolver.requestSync(localAccount2, "com.google.android.gallery3d.GooglePhotoProvider", new Bundle());
        }
        ++i;
      }
    }
    while (localPackageManager.getComponentEnabledSetting(new ComponentName(this.mContext, ConnectivityReceiver.class)) == 2);
    Log.d("PicasaFacade", "disable picasasync in gallery");
    localPackageManager.setComponentEnabledSetting(new ComponentName(this.mContext, ConnectivityReceiver.class), 2, 1);
    localPackageManager.setComponentEnabledSetting(new ComponentName(this.mContext, BatteryReceiver.class), 2, 1);
    Account[] arrayOfAccount1 = AccountManager.get(this.mContext).getAccountsByType("com.google");
    int j = arrayOfAccount1.length;
    for (int k = 0; ; ++k)
    {
      if (k < j);
      Account localAccount1 = arrayOfAccount1[k];
      ContentResolver.setIsSyncable(localAccount1, "com.google.android.gallery3d.GooglePhotoProvider", 0);
      ContentResolver.cancelSync(localAccount1, "com.google.android.gallery3d.GooglePhotoProvider");
    }
  }

  public Uri getAlbumUri(long paramLong)
  {
    return this.mAlbumsUri.buildUpon().appendPath(String.valueOf(paramLong)).build();
  }

  public Uri getAlbumsUri()
  {
    return this.mAlbumsUri;
  }

  public String getAuthority()
  {
    return "com.google.android.gallery3d.GooglePhotoProvider";
  }

  public Uri getPhotosUri()
  {
    return this.mPhotosUri;
  }

  public Uri getPostAlbumsUri()
  {
    return this.mPostAlbumsUri;
  }

  public Uri getPostPhotosUri()
  {
    return this.mPostPhotosUri;
  }

  public Uri getSettingsUri()
  {
    return this.mSettingsUri;
  }

  public Uri getSyncRequestUri()
  {
    return this.mSyncRequestUri;
  }

  public Uri getUploadRecordsUri()
  {
    return this.mUploadRecordsUri;
  }

  public Uri getUploadUri(long paramLong)
  {
    return this.mUploadsUri.buildUpon().appendPath(String.valueOf(paramLong)).build();
  }

  public Uri getUploadsUri()
  {
    return this.mUploadsUri;
  }

  public Uri getUsersUri()
  {
    return this.mUsersUri;
  }

  public boolean isMaster()
  {
    return true;
  }

  public Uri requestImmediateSyncOnAlbum(long paramLong)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("task", "immediate_photos");
    localContentValues.put("album_id", Long.valueOf(paramLong));
    return this.mContext.getContentResolver().insert(this.mSyncRequestUri, localContentValues);
  }

  public Uri requestImmediateSyncOnAlbumList(String paramString)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("task", "immediate_albums");
    localContentValues.put("account", paramString);
    return this.mContext.getContentResolver().insert(this.mSyncRequestUri, localContentValues);
  }

  public void requestSync()
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("task", "manual_metadata");
    this.mContext.getContentResolver().insert(this.mSyncRequestUri, localContentValues);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.PicasaFacade
 * JD-Core Version:    0.5.4
 */