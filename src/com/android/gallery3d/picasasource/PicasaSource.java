package com.android.gallery3d.picasasource;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.BlobCache;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.Log;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet.ItemConsumer;
import com.android.gallery3d.data.MediaSource;
import com.android.gallery3d.data.MediaSource.PathId;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.data.PathMatcher;
import com.android.gallery3d.exif.ExifData;
import com.android.gallery3d.exif.ExifTag;
import com.android.gallery3d.exif.Rational;
import com.android.gallery3d.provider.GalleryProvider;
import com.android.gallery3d.settings.GallerySettings;
import com.android.gallery3d.util.CacheManager;
import com.android.gallery3d.util.GalleryUtils;
import com.google.android.picasastore.PicasaStoreFacade;
import com.google.android.picasasync.PhotoEntry;
import com.google.android.picasasync.PicasaFacade;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PicasaSource extends MediaSource
{
  public static final Path ALBUM_PATH;
  private static final EntrySchema SCHEMA = PhotoEntry.SCHEMA;
  private static BlobCache sBlobCache;
  private static final IdComparator sIdComparator;
  private static final int sIdIndex = SCHEMA.getColumnIndex("_id");
  private final GalleryApp mApplication;
  private ContentProviderClient mClient;
  private PicasaFacade mFacade;
  private final Handler mHandler;
  private boolean mIsActive = false;
  private final PathMatcher mMatcher;
  private ContentProviderClient mStoreClient;
  private PicasaStoreFacade mStoreFacade;
  private final UriMatcher mUriMatcher = new UriMatcher(-1);

  static
  {
    sIdComparator = new IdComparator(null);
    ALBUM_PATH = Path.fromString("/picasa/all");
  }

  public PicasaSource(GalleryApp paramGalleryApp)
  {
    super("picasa");
    this.mApplication = paramGalleryApp;
    this.mMatcher = new PathMatcher();
    this.mMatcher.add("/picasa/all", 0);
    this.mMatcher.add("/picasa/image", 0);
    this.mMatcher.add("/picasa/video", 0);
    this.mMatcher.add("/picasa/all/*", 1);
    this.mMatcher.add("/picasa/video/*", 3);
    this.mMatcher.add("/picasa/image/*", 2);
    this.mMatcher.add("/picasa/post/*/*", 6);
    this.mMatcher.add("/picasa/item/*", 4);
    this.mMatcher.add("/picasa/face/*/*", 5);
    Context localContext = paramGalleryApp.getAndroidContext();
    this.mUriMatcher.addURI(PicasaFacade.get(localContext).getAuthority(), "photos/#", 1);
    this.mUriMatcher.addURI(GalleryProvider.getAuthority(localContext), "picasa/item/#", 1);
    this.mHandler = new Handler(paramGalleryApp.getMainLooper())
    {
      public void handleMessage(Message paramMessage)
      {
        if (paramMessage.what == 100);
        for (boolean bool = true; ; bool = false)
        {
          Utils.assertTrue(bool);
          synchronized (PicasaSource.this)
          {
            if (PicasaSource.this.mClient != null)
            {
              PicasaSource.this.mClient.release();
              PicasaSource.access$102(PicasaSource.this, null);
            }
            if (PicasaSource.this.mStoreClient != null)
            {
              PicasaSource.this.mStoreClient.release();
              PicasaSource.access$202(PicasaSource.this, null);
            }
            return;
          }
        }
      }
    };
  }

  static boolean checkPlusOneVersion(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      PackageInfo localPackageInfo = localPackageManager.getPackageInfo("com.google.android.apps.plus", 0);
      if ((localPackageInfo.applicationInfo != null) && (!localPackageInfo.applicationInfo.enabled))
        break label62;
      int i = localPackageInfo.versionCode;
      boolean bool = false;
      if (i >= 260000000)
        bool = true;
      PicasaFacade.get(paramContext).enablePicasasync(bool);
      label62: return bool;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      PicasaFacade.get(paramContext).enablePicasasync(true);
    }
    return true;
  }

  public static void extractExifValues(MediaItem paramMediaItem, ExifData paramExifData)
  {
    PhotoEntry localPhotoEntry = ((PicasaImage)paramMediaItem).getPhotoEntry();
    if (!TextUtils.isEmpty(localPhotoEntry.exifMake))
      paramExifData.addTag(271).setValue(localPhotoEntry.exifMake);
    if (!TextUtils.isEmpty(localPhotoEntry.exifModel))
      paramExifData.addTag(272).setValue(localPhotoEntry.exifModel);
    if (localPhotoEntry.exifIso != 0)
      paramExifData.addTag(-30681).setValue(localPhotoEntry.exifIso);
    if (localPhotoEntry.exifFocalLength != 0.0F)
      paramExifData.addTag(-28150).setValue(new Rational((int)(100.0F * localPhotoEntry.exifFocalLength), 100L));
    if (localPhotoEntry.exifFlash != 0)
      paramExifData.addTag(-28151).setValue(localPhotoEntry.exifFlash);
    if (localPhotoEntry.exifFstop != 0.0F)
      paramExifData.addTag(-28158).setValue(new Rational((int)(10.0F * localPhotoEntry.exifFstop), 10L));
    if (localPhotoEntry.exifExposure != 0.0F)
      paramExifData.addTag(-32102).setValue(new Rational((int)(100000.0F * localPhotoEntry.exifExposure), 100000L));
    if (localPhotoEntry.dateTaken != 0L)
    {
      paramExifData.addTag(-28669).setTimeValue(localPhotoEntry.dateTaken);
      paramExifData.addTag(-28668).setTimeValue(localPhotoEntry.dateTaken);
    }
    if (localPhotoEntry.dateUpdated != 0L)
      paramExifData.addTag(306).setTimeValue(localPhotoEntry.dateUpdated);
    double d1 = localPhotoEntry.latitude;
    double d2 = localPhotoEntry.longitude;
    if (!GalleryUtils.isValidLocation(d1, d2))
      return;
    paramExifData.addGpsTags(d1, d2);
  }

  public static String getContentType(MediaObject paramMediaObject)
  {
    return ((PicasaImage)paramMediaObject).getPhotoEntry().contentType;
  }

  public static long getDateTaken(MediaObject paramMediaObject)
  {
    return ((PicasaImage)paramMediaObject).getPhotoEntry().dateTaken;
  }

  public static BlobCache getFaceCache(Context paramContext)
  {
    monitorenter;
    try
    {
      if (sBlobCache == null)
        sBlobCache = CacheManager.getCache(paramContext, "face", 1000, 3072000, 1);
      BlobCache localBlobCache = sBlobCache;
      return localBlobCache;
    }
    finally
    {
      monitorexit;
    }
  }

  public static MediaItem getFaceItem(Context paramContext, MediaItem paramMediaItem, int paramInt)
  {
    monitorenter;
    try
    {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramMediaItem.getPath().getSuffix();
      arrayOfObject[1] = Integer.valueOf(paramInt);
      Path localPath = Path.fromString(String.format("/picasa/face/%s/%d", arrayOfObject));
      GalleryApp localGalleryApp;
      synchronized (DataManager.LOCK)
      {
        Object localObject4 = (MediaItem)localGalleryApp.getDataManager().peekMediaObject(localPath);
        if (localObject4 == null)
          localObject4 = new FaceImage(localPath, (PicasaImage)paramMediaItem, paramInt, getFaceCache(paramContext));
        monitorexit;
        return localObject4;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public static int getImageSize(MediaObject paramMediaObject)
  {
    return ((PicasaImage)paramMediaObject).getPhotoEntry().size;
  }

  public static String getImageTitle(MediaObject paramMediaObject)
  {
    return ((PicasaImage)paramMediaObject).getPhotoEntry().title;
  }

  public static double getLatitude(MediaObject paramMediaObject)
  {
    return ((PicasaImage)paramMediaObject).getPhotoEntry().latitude;
  }

  public static double getLongitude(MediaObject paramMediaObject)
  {
    return ((PicasaImage)paramMediaObject).getPhotoEntry().longitude;
  }

  private static MediaItem[] getMediaItemById(PicasaSource paramPicasaSource, ArrayList<Long> paramArrayList)
  {
    MediaItem[] arrayOfMediaItem = new MediaItem[paramArrayList.size()];
    if (paramArrayList.isEmpty())
      return arrayOfMediaItem;
    long l1 = ((Long)paramArrayList.get(0)).longValue();
    long l2 = ((Long)paramArrayList.get(-1 + paramArrayList.size())).longValue();
    DataManager localDataManager = paramPicasaSource.getApplication().getDataManager();
    Uri localUri = paramPicasaSource.getPicasaFacade().getPhotosUri();
    String[] arrayOfString1 = SCHEMA.getProjection();
    String[] arrayOfString2 = new String[2];
    arrayOfString2[0] = String.valueOf(l1);
    arrayOfString2[1] = String.valueOf(l2);
    Cursor localCursor = paramPicasaSource.query(localUri, arrayOfString1, "_id BETWEEN ? AND ?", arrayOfString2, "_id");
    if (localCursor == null)
    {
      Log.w("PicasaSource", "query fail");
      return arrayOfMediaItem;
    }
    try
    {
      int i = paramArrayList.size();
      for (int j = 0; (j < i) && (localCursor.moveToNext()); ++j)
      {
        long l3;
        do
          l3 = localCursor.getLong(sIdIndex);
        while (((Long)paramArrayList.get(j)).longValue() > l3);
        while (true)
        {
          long l4 = ((Long)paramArrayList.get(j)).longValue();
          if (l4 >= l3)
            break;
          if (++j >= i)
            return arrayOfMediaItem;
        }
        PhotoEntry localPhotoEntry = (PhotoEntry)SCHEMA.cursorToObject(localCursor, new PhotoEntry());
        arrayOfMediaItem[j] = loadOrUpdateItem(PicasaImage.ITEM_PATH.getChild(l3), localPhotoEntry, localDataManager, paramPicasaSource);
      }
      return arrayOfMediaItem;
    }
    finally
    {
      localCursor.close();
    }
  }

  public static long getPicasaId(MediaObject paramMediaObject)
  {
    return ((PicasaImage)paramMediaObject).getPhotoEntry().id;
  }

  public static int getRotation(MediaObject paramMediaObject)
  {
    return ((PicasaImage)paramMediaObject).getPhotoEntry().rotation;
  }

  public static String getUserAccount(Context paramContext, MediaObject paramMediaObject)
  {
    long l = ((PicasaImage)paramMediaObject).getPhotoEntry().userId;
    Uri localUri = PicasaFacade.get(paramContext).getUsersUri();
    ContentResolver localContentResolver = paramContext.getContentResolver();
    String[] arrayOfString1 = { "account" };
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(l);
    Cursor localCursor = localContentResolver.query(localUri, arrayOfString1, "_id=?", arrayOfString2, null);
    Object localObject1 = null;
    if (localCursor != null);
    try
    {
      boolean bool = localCursor.moveToNext();
      localObject1 = null;
      if (bool)
      {
        String str = localCursor.getString(0);
        localObject1 = str;
      }
      return localObject1;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  public static Dialog getVersionCheckDialog(Activity paramActivity)
  {
    return getVersionCheckDialog(paramActivity, 2131362080);
  }

  public static Dialog getVersionCheckDialog(Activity paramActivity, int paramInt)
  {
    if (checkPlusOneVersion(paramActivity))
      return null;
    2 local2 = new DialogInterface.OnClickListener(String.format("market://details?id=%s", new Object[] { "com.google.android.apps.plus" }), paramActivity)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        Intent localIntent;
        switch (paramInt)
        {
        default:
          return;
        case -1:
          localIntent = new Intent("android.intent.action.VIEW").setData(Uri.parse(this.val$marketUri)).addFlags(268435456);
        case -2:
        }
        try
        {
          this.val$activity.startActivity(localIntent);
          label58: paramDialogInterface.cancel();
          return;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          Log.w("PicasaSource", "not found", localActivityNotFoundException);
          break label58:
          paramDialogInterface.cancel();
        }
      }
    };
    return new AlertDialog.Builder(paramActivity).setPositiveButton(2131362078, local2).setNegativeButton(17039360, local2).setTitle(2131362079).setMessage(paramActivity.getString(paramInt, new Object[] { "2.6.0" })).create();
  }

  public static void initialize(Context paramContext)
  {
    com.google.android.picasasync.R.init(com.android.gallery3d.R.class);
  }

  public static boolean isPicasaImage(MediaObject paramMediaObject)
  {
    return paramMediaObject instanceof PicasaImage;
  }

  public static MediaItem loadOrUpdateItem(Path paramPath, PhotoEntry paramPhotoEntry, DataManager paramDataManager, PicasaSource paramPicasaSource)
  {
    synchronized (DataManager.LOCK)
    {
      PicasaImage localPicasaImage = (PicasaImage)paramDataManager.peekMediaObject(paramPath);
      if (localPicasaImage == null)
      {
        localPicasaImage = new PicasaImage(paramPath, paramPicasaSource, paramPhotoEntry);
        return localPicasaImage;
      }
      localPicasaImage.updateContent(paramPhotoEntry);
    }
  }

  public static void onPackageAdded(Context paramContext, String paramString)
  {
    PicasaStoreFacade.get(paramContext).onPackageAdded(paramString);
  }

  public static void onPackageChanged(Context paramContext, String paramString)
  {
    PicasaStoreFacade.get(paramContext).onPackageChanged(paramString);
  }

  public static void onPackageRemoved(Context paramContext, String paramString)
  {
    PicasaStoreFacade.get(paramContext).onPackageRemoved(paramString);
  }

  public static ParcelFileDescriptor openFile(Context paramContext, MediaObject paramMediaObject, String paramString)
    throws FileNotFoundException
  {
    PhotoEntry localPhotoEntry = ((PicasaImage)paramMediaObject).getPhotoEntry();
    Uri localUri = PicasaStoreFacade.get(paramContext).getPhotoUri(localPhotoEntry.id, "full", localPhotoEntry.contentUrl);
    return paramContext.getContentResolver().openFileDescriptor(localUri, paramString);
  }

  public static void requestSync(Context paramContext)
  {
    PicasaFacade.get(paramContext).requestSync();
  }

  public static void showSignInReminder(Activity paramActivity)
  {
    GallerySettings.addAccount(paramActivity, true);
  }

  public MediaObject createMediaObject(Path paramPath)
  {
    switch (this.mMatcher.match(paramPath))
    {
    case 5:
    default:
      throw new RuntimeException("bad path: " + paramPath);
    case 0:
      return new PicasaAlbumSet(paramPath, this);
    case 1:
      return PicasaAlbum.find(paramPath, this, this.mMatcher.getLongVar(0), 6);
    case 2:
      return PicasaAlbum.find(paramPath, this, this.mMatcher.getLongVar(0), 2);
    case 3:
      return PicasaAlbum.find(paramPath, this, this.mMatcher.getLongVar(0), 4);
    case 6:
      String str = this.mMatcher.getVar(0);
      return new PicasaPostAlbum(paramPath, this, this.mMatcher.getLongVar(1), MediaObject.getTypeFromString(str));
    case 4:
    }
    return PicasaImage.find(paramPath, this, this.mMatcher.getLongVar(0));
  }

  public Path findPathByUri(Uri paramUri, String paramString)
  {
    try
    {
      switch (this.mUriMatcher.match(paramUri))
      {
      case 1:
        Path localPath = Path.fromString(paramUri.getPath());
        return localPath;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.w("PicasaSource", "uri: " + paramUri.toString(), localNumberFormatException);
    }
    return null;
  }

  public GalleryApp getApplication()
  {
    return this.mApplication;
  }

  public ContentProviderClient getContentProvider()
  {
    monitorenter;
    try
    {
      if (this.mClient == null)
      {
        String str = PicasaFacade.get(this.mApplication.getAndroidContext()).getAuthority();
        this.mClient = this.mApplication.getContentResolver().acquireContentProviderClient(str);
        if (this.mClient != null)
          break label85;
        Log.w("PicasaSource", "cannot connect to picasa provider: " + str);
      }
      do
      {
        ContentProviderClient localContentProviderClient = this.mClient;
        label85: return localContentProviderClient;
      }
      while (this.mIsActive);
    }
    finally
    {
      monitorexit;
    }
  }

  public ContentResolver getContentResolver()
  {
    return this.mApplication.getAndroidContext().getContentResolver();
  }

  public Path getDefaultSetOf(Path paramPath)
  {
    MediaObject localMediaObject = this.mApplication.getDataManager().getMediaObject(paramPath);
    if (localMediaObject instanceof PicasaImage)
    {
      PicasaImage localPicasaImage = (PicasaImage)localMediaObject;
      return ALBUM_PATH.getChild(localPicasaImage.getAlbumId());
    }
    return null;
  }

  public PicasaFacade getPicasaFacade()
  {
    if (this.mFacade == null)
      this.mFacade = PicasaFacade.get(this.mApplication.getAndroidContext());
    return this.mFacade;
  }

  public PicasaStoreFacade getPicasaStoreFacade()
  {
    if (this.mStoreFacade == null)
      this.mStoreFacade = PicasaStoreFacade.get(this.mApplication.getAndroidContext());
    return this.mStoreFacade;
  }

  public ContentProviderClient getStoreProvider()
  {
    monitorenter;
    try
    {
      if (this.mStoreClient == null)
      {
        String str = PicasaStoreFacade.get(this.mApplication.getAndroidContext()).getAuthority();
        this.mStoreClient = this.mApplication.getContentResolver().acquireContentProviderClient(str);
        if (this.mStoreClient != null)
          break label85;
        Log.w("PicasaSource", "cannot connect to picasa store provider: " + str);
      }
      do
      {
        ContentProviderClient localContentProviderClient = this.mStoreClient;
        label85: return localContentProviderClient;
      }
      while (this.mIsActive);
    }
    finally
    {
      monitorexit;
    }
  }

  public long getTotalTargetCacheSize()
  {
    return PicasaAlbumSet.getTotalTargetCacheSize(this);
  }

  public long getTotalUsedCacheSize()
  {
    return PicasaAlbumSet.getTotalUsedCacheSize(this.mApplication.getAndroidContext());
  }

  public void mapMediaItems(ArrayList<MediaSource.PathId> paramArrayList, MediaSet.ItemConsumer paramItemConsumer)
  {
    if (paramArrayList.size() < 500)
    {
      super.mapMediaItems(paramArrayList, paramItemConsumer);
      return;
    }
    Collections.sort(paramArrayList, sIdComparator);
    int i = paramArrayList.size();
    int l;
    for (int j = 0; ; j = l)
    {
      if (j < i);
      ArrayList localArrayList = new ArrayList();
      int k = 0;
      for (l = j; ; ++l)
      {
        if (l < i)
        {
          localArrayList.add(Long.valueOf(Long.parseLong(((MediaSource.PathId)paramArrayList.get(l)).path.getSuffix())));
          if (++k != 100)
            continue;
        }
        MediaItem[] arrayOfMediaItem = getMediaItemById(this, localArrayList);
        for (int i1 = j; ; ++i1)
        {
          if (i1 >= l)
            break label154;
          label154: paramItemConsumer.consume(((MediaSource.PathId)paramArrayList.get(i1)).id, arrayOfMediaItem[(i1 - j)]);
        }
      }
    }
  }

  public void pause()
  {
    monitorenter;
    try
    {
      this.mIsActive = false;
      if (this.mClient != null)
      {
        this.mClient.release();
        this.mClient = null;
      }
      if (this.mStoreClient != null)
      {
        this.mStoreClient.release();
        this.mStoreClient = null;
      }
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    ContentProviderClient localContentProviderClient = getContentProvider();
    if (localContentProviderClient == null)
      return null;
    try
    {
      Cursor localCursor = localContentProviderClient.query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
      return localCursor;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("PicasaSource", "query fail!", localRemoteException);
    }
    return null;
  }

  public void resume()
  {
    monitorenter;
    try
    {
      this.mIsActive = true;
      this.mHandler.removeMessages(100);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    ContentProviderClient localContentProviderClient = getContentProvider();
    if (localContentProviderClient == null)
      throw new IllegalArgumentException();
    try
    {
      int i = localContentProviderClient.update(paramUri, paramContentValues, paramString, paramArrayOfString);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("PicasaSource", "update fail!", localRemoteException);
    }
    return 0;
  }

  private static class IdComparator
    implements Comparator<MediaSource.PathId>
  {
    public int compare(MediaSource.PathId paramPathId1, MediaSource.PathId paramPathId2)
    {
      String str1 = paramPathId1.path.getSuffix();
      String str2 = paramPathId2.path.getSuffix();
      int i = str1.length();
      int j = str2.length();
      if (i < j)
        return -1;
      if (i > j)
        return 1;
      return str1.compareTo(str2);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.picasasource.PicasaSource
 * JD-Core Version:    0.5.4
 */