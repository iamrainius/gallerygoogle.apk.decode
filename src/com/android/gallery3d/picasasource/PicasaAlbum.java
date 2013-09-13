package com.android.gallery3d.picasasource;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.ParcelFileDescriptor;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.BytesBufferPool;
import com.android.gallery3d.data.BytesBufferPool.BytesBuffer;
import com.android.gallery3d.data.DecodeUtils;
import com.android.gallery3d.data.Log;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet.SyncListener;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.google.android.picasastore.PicasaStoreFacade;
import com.google.android.picasasync.AlbumEntry;
import com.google.android.picasasync.PhotoEntry;
import com.google.android.picasasync.PicasaFacade;

class PicasaAlbum extends BasePicasaAlbum
{
  private static final EntrySchema SCHEMA = PhotoEntry.SCHEMA;
  private CoverItem mCoverItem;
  private AlbumData mData;
  private int mType;

  public PicasaAlbum(Path paramPath, PicasaSource paramPicasaSource, AlbumData paramAlbumData, int paramInt)
  {
    super(paramPath, paramPicasaSource, nextVersionNumber());
    this.mData = paramAlbumData;
    this.mType = paramInt;
  }

  public static PicasaAlbum find(Path paramPath, PicasaSource paramPicasaSource, long paramLong, int paramInt)
  {
    AlbumData localAlbumData = PicasaAlbumSet.getAlbumData(paramPicasaSource, paramLong);
    if (localAlbumData == null)
      return null;
    return new PicasaAlbum(paramPath, paramPicasaSource, localAlbumData, paramInt);
  }

  public static PhotoEntry getPhotoEntry(PicasaSource paramPicasaSource, long paramLong)
  {
    Uri localUri = paramPicasaSource.getPicasaFacade().getPhotosUri();
    String[] arrayOfString1 = SCHEMA.getProjection();
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramLong);
    Cursor localCursor = paramPicasaSource.query(localUri, arrayOfString1, "_id=?", arrayOfString2, null);
    if (localCursor != null);
    while (true)
      try
      {
        if (localCursor.moveToNext())
        {
          localPhotoEntry = (PhotoEntry)SCHEMA.cursorToObject(localCursor, new PhotoEntry());
          return localPhotoEntry;
        }
        PhotoEntry localPhotoEntry = null;
      }
      finally
      {
        Utils.closeSilently(localCursor);
      }
  }

  public void cache(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return;
    case 2:
    case 1:
    case 0:
    }
    for (int i = 2; ; i = 0)
      while (true)
      {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("cache_flag", Integer.valueOf(i));
        this.mSource.update(this.mSource.getPicasaFacade().getAlbumUri(this.mData.id), localContentValues, null, null);
        return;
        i = 1;
      }
  }

  public int getCacheFlag()
  {
    if (this.mData.cacheFlag == 2)
      return 2;
    if (this.mData.cacheFlag == 1)
      return 1;
    return 0;
  }

  public long getCacheSize()
  {
    return this.mData.cacheSize;
  }

  public int getCacheStatus()
  {
    switch (this.mData.cacheStatus)
    {
    default:
      return 0;
    case 1:
      return 1;
    case 2:
      return 2;
    case 3:
    }
    return 3;
  }

  public MediaItem getCoverMediaItem()
  {
    monitorenter;
    try
    {
      if (this.mCoverItem == null)
        this.mCoverItem = new CoverItem(this.mPath.getChild("cover"), MediaObject.nextVersionNumber());
      CoverItem localCoverItem = this.mCoverItem;
      return localCoverItem;
    }
    finally
    {
      monitorexit;
    }
  }

  public int getMediaItemCount()
  {
    return this.mData.numPhotos;
  }

  public String getName()
  {
    return this.mData.title;
  }

  public int getSupportedOperations()
  {
    return 1284;
  }

  protected Cursor internalQuery(int paramInt1, int paramInt2)
  {
    Uri localUri = this.mSource.getPicasaFacade().getPhotosUri().buildUpon().appendQueryParameter("limit", paramInt1 + "," + paramInt2).build();
    WhereEntry localWhereEntry = new WhereEntry(this.mData.id, this.mType);
    return this.mSource.query(localUri, SCHEMA.getProjection(), localWhereEntry.selection, localWhereEntry.args, "display_index");
  }

  public Future<Integer> requestSync(MediaSet.SyncListener paramSyncListener)
  {
    monitorenter;
    try
    {
      BasePicasaAlbum.PicasaSyncTaskFuture localPicasaSyncTaskFuture = new BasePicasaAlbum.PicasaSyncTaskFuture(this.mSource, this, paramSyncListener);
      localPicasaSyncTaskFuture.startSync(this.mData.id);
      monitorexit;
      return localPicasaSyncTaskFuture;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  protected void updateContent(AlbumData paramAlbumData)
  {
    if (this.mData.equals(Utils.checkNotNull(paramAlbumData)))
      return;
    if ((this.mCoverItem != null) && (!Utils.equals(this.mData.thumbnailUrl, paramAlbumData.thumbnailUrl)))
      this.mCoverItem.updateContent();
    this.mData = paramAlbumData;
    this.mDataVersion = nextVersionNumber();
  }

  private class CoverItem extends MediaItem
  {
    public CoverItem(Path paramLong, long arg3)
    {
      super(paramLong, localObject);
    }

    public int getHeight()
    {
      return 0;
    }

    public String getMimeType()
    {
      return "image/jpeg";
    }

    public int getWidth()
    {
      return 0;
    }

    public ThreadPool.Job<Bitmap> requestImage(int paramInt)
    {
      return new PicasaAlbum.PicasaBitmapJob(PicasaAlbum.this, PicasaAlbum.this.mSource.getPicasaStoreFacade().getAlbumCoverUri(PicasaAlbum.this.mData.id, PicasaAlbum.this.mData.thumbnailUrl));
    }

    public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage()
    {
      throw new UnsupportedOperationException();
    }

    public void updateContent()
    {
      this.mDataVersion = nextVersionNumber();
    }
  }

  private class PicasaBitmapJob
    implements ThreadPool.Job<Bitmap>
  {
    private Uri mPhotoUri;

    public PicasaBitmapJob(Uri arg2)
    {
      Object localObject;
      this.mPhotoUri = localObject;
    }

    public Bitmap run(ThreadPool.JobContext paramJobContext)
    {
      ParcelFileDescriptor localParcelFileDescriptor = null;
      BytesBufferPool.BytesBuffer localBytesBuffer = MediaItem.getBytesBufferPool().get();
      try
      {
        boolean bool1 = paramJobContext.isCancelled();
        if (bool1)
          return null;
        localParcelFileDescriptor = PicasaAlbum.this.mSource.getStoreProvider().openFile(this.mPhotoUri, "r");
        localBytesBuffer.readFrom(paramJobContext, localParcelFileDescriptor.getFileDescriptor());
        boolean bool2 = paramJobContext.isCancelled();
        if (bool2)
          return null;
        Bitmap localBitmap = DecodeUtils.decode(paramJobContext, localBytesBuffer.data, localBytesBuffer.offset, localBytesBuffer.length, null, MediaItem.getMicroThumbPool());
        return localBitmap;
      }
      catch (Throwable localThrowable)
      {
        Log.w("PicasaAlbum", "fail to decode bitmap", localThrowable);
        return null;
      }
      finally
      {
        Utils.closeSilently(localParcelFileDescriptor);
        MediaItem.getBytesBufferPool().recycle(localBytesBuffer);
      }
    }
  }

  private static class WhereEntry
  {
    public String[] args;
    public String selection;

    public WhereEntry(long paramLong, int paramInt)
    {
      String str = String.valueOf(paramLong);
      switch (paramInt)
      {
      case 3:
      default:
        this.selection = "album_id = ?";
        this.args = new String[] { str };
        return;
      case 2:
        this.selection = "album_id = ? AND content_type LIKE ?";
        this.args = new String[] { str, "image/%" };
        return;
      case 4:
      }
      this.selection = "album_id = ? AND content_type LIKE ?";
      this.args = new String[] { str, "video/%" };
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.picasasource.PicasaAlbum
 * JD-Core Version:    0.5.4
 */