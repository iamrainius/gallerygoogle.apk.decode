package com.android.gallery3d.picasasource;

import android.content.ContentResolver;
import android.database.Cursor;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.data.ChangeNotifier;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.Log;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.MediaSet.SyncListener;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.GalleryUtils;
import com.google.android.picasasync.PhotoEntry;
import com.google.android.picasasync.PicasaFacade;
import java.util.ArrayList;

public abstract class BasePicasaAlbum extends MediaSet
{
  protected static final EntrySchema SCHEMA = PhotoEntry.SCHEMA;
  private ChangeNotifier mNotifier;
  protected final PicasaSource mSource;

  public BasePicasaAlbum(Path paramPath, PicasaSource paramPicasaSource, long paramLong)
  {
    super(paramPath, paramLong);
    this.mSource = paramPicasaSource;
    this.mNotifier = new ChangeNotifier(this, paramPicasaSource.getPicasaFacade().getPhotosUri(), paramPicasaSource.getApplication());
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    GalleryUtils.assertNotInRenderThread();
    ArrayList localArrayList = new ArrayList();
    Cursor localCursor = internalQuery(paramInt1, paramInt2);
    if (localCursor == null)
    {
      Log.w("BasePicasaAlbum", "query media item fail");
      return new ArrayList();
    }
    DataManager localDataManager;
    PhotoEntry localPhotoEntry;
    try
    {
      localDataManager = this.mSource.getApplication().getDataManager();
      if (!localCursor.moveToNext())
        break label126;
      localPhotoEntry = (PhotoEntry)SCHEMA.cursorToObject(localCursor, new PhotoEntry());
    }
    finally
    {
      localCursor.close();
    }
    label126: localCursor.close();
    return localArrayList;
  }

  protected Cursor internalQuery(int paramInt1, int paramInt2)
  {
    return null;
  }

  public boolean isLeafAlbum()
  {
    return true;
  }

  public long reload()
  {
    if (this.mNotifier.isDirty())
      this.mDataVersion = nextVersionNumber();
    return this.mDataVersion;
  }

  protected static class PicasaSyncTaskFuture extends PicasaAlbumSet.PicasaSyncTaskFuture
  {
    PicasaSyncTaskFuture(PicasaSource paramPicasaSource, MediaSet paramMediaSet, MediaSet.SyncListener paramSyncListener)
    {
      super(paramPicasaSource, paramMediaSet, paramSyncListener);
    }

    void startSync(long paramLong)
    {
      ContentResolver localContentResolver = this.mSource.getContentResolver();
      monitorenter;
      try
      {
        this.mUri = this.mSource.getPicasaFacade().requestImmediateSyncOnAlbum(paramLong);
        localContentResolver.registerContentObserver(this.mUri, false, this);
        this.mResult = getSyncResult();
        int i = this.mResult;
        MediaSet.SyncListener localSyncListener = null;
        if (i >= 0)
        {
          localSyncListener = this.mListener;
          this.mListener = null;
          localContentResolver.unregisterContentObserver(this);
        }
        monitorexit;
        if (localSyncListener != null);
        return;
      }
      catch (Throwable localThrowable)
      {
        Log.e("BasePicasaAlbum", "requestImmediateSyncOnAlbum: " + localThrowable);
      }
      finally
      {
        monitorexit;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.picasasource.BasePicasaAlbum
 * JD-Core Version:    0.5.4
 */