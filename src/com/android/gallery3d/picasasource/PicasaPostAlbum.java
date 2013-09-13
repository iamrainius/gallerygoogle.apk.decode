package com.android.gallery3d.picasasource;

import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.Log;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaSet.SyncListener;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.google.android.picasasync.PicasaFacade;
import java.util.ArrayList;
import java.util.Iterator;

class PicasaPostAlbum extends BasePicasaAlbum
{
  private static final String[] ALBUMS_PROJECTION;
  private static final String[] COUNT_PROJECTION = { "count(*)" };
  private static final String[] USERS_ACCOUNT_PROJECTION;
  private int mCachedCount = -1;
  private String mName;
  private Uri mPostAlbumsUri;
  private Uri mPostPhotosUri;
  private String mUserId;

  static
  {
    ALBUMS_PROJECTION = new String[] { "_id" };
    USERS_ACCOUNT_PROJECTION = new String[] { "account" };
  }

  public PicasaPostAlbum(Path paramPath, PicasaSource paramPicasaSource, long paramLong, int paramInt)
  {
    super(paramPath, paramPicasaSource, nextVersionNumber());
    String str1 = String.valueOf(paramLong);
    this.mPostPhotosUri = this.mSource.getPicasaFacade().getPostPhotosUri().buildUpon().appendQueryParameter("user_id", str1).appendQueryParameter("type", getTypeString(paramInt)).build();
    this.mPostAlbumsUri = this.mSource.getPicasaFacade().getPostAlbumsUri().buildUpon().appendQueryParameter("user_id", str1).appendQueryParameter("type", getTypeString(paramInt)).build();
    this.mUserId = getAlbumUserId(this.mSource, paramLong);
    String str2 = this.mUserId;
    int i = str2.indexOf('@');
    if (i >= 0)
      str2 = str2.substring(0, i);
    this.mName = ("Posts - " + str2);
  }

  private static String getAlbumUserId(PicasaSource paramPicasaSource, long paramLong)
  {
    Uri localUri = paramPicasaSource.getPicasaFacade().getUsersUri();
    String[] arrayOfString1 = USERS_ACCOUNT_PROJECTION;
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramLong);
    Cursor localCursor = paramPicasaSource.query(localUri, arrayOfString1, "_id = ?", arrayOfString2, null);
    if (localCursor == null)
      return "";
    try
    {
      Utils.assertTrue(localCursor.moveToNext());
      String str = localCursor.getString(0);
      return str;
    }
    finally
    {
      localCursor.close();
    }
  }

  public int getMediaItemCount()
  {
    Cursor localCursor;
    if (this.mCachedCount == -1)
    {
      localCursor = this.mSource.query(this.mPostPhotosUri, COUNT_PROJECTION, null, null, null);
      if (localCursor == null)
        break label63;
    }
    while (true)
    {
      int i;
      try
      {
        if (localCursor.moveToNext())
        {
          i = localCursor.getInt(0);
          this.mCachedCount = i;
          return this.mCachedCount;
        }
      }
      finally
      {
        label63: localCursor.close();
      }
    }
  }

  public String getName()
  {
    return this.mName;
  }

  public int getSupportedOperations()
  {
    return 1028;
  }

  protected Cursor internalQuery(int paramInt1, int paramInt2)
  {
    Uri localUri = this.mPostPhotosUri.buildUpon().appendQueryParameter("limit", paramInt1 + "," + paramInt2).build();
    return this.mSource.query(localUri, SCHEMA.getProjection(), null, null, "date_taken DESC");
  }

  public long reload()
  {
    long l = this.mDataVersion;
    if (super.reload() > l)
      this.mCachedCount = -1;
    return this.mDataVersion;
  }

  public Future<Integer> requestSync(MediaSet.SyncListener paramSyncListener)
  {
    monitorenter;
    try
    {
      PostSyncFuture localPostSyncFuture = new PostSyncFuture(paramSyncListener);
      localPostSyncFuture.startSync();
      monitorexit;
      return localPostSyncFuture;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private class PostSyncFuture
    implements Future<Integer>, MediaSet.SyncListener
  {
    private long[] mAlbumIds;
    private int mAlbumIndex;
    private ArrayList<Future<Integer>> mFutures;
    private boolean mIsCancelled = false;
    private final MediaSet.SyncListener mListener;
    private int mPendingCount;
    private int mResult = 0;

    PostSyncFuture(MediaSet.SyncListener arg2)
    {
      Object localObject;
      this.mListener = localObject;
    }

    private void syncNextPhotos(int paramInt)
    {
      int j;
      for (int i = paramInt; ; i = j)
      {
        j = i - 1;
        if ((i <= 0) || (this.mAlbumIndex >= this.mAlbumIds.length))
          return;
        BasePicasaAlbum.PicasaSyncTaskFuture localPicasaSyncTaskFuture = new BasePicasaAlbum.PicasaSyncTaskFuture(PicasaPostAlbum.this.mSource, PicasaPostAlbum.this, this);
        this.mFutures.add(localPicasaSyncTaskFuture);
        this.mPendingCount = (1 + this.mPendingCount);
        long[] arrayOfLong = this.mAlbumIds;
        int k = this.mAlbumIndex;
        this.mAlbumIndex = (k + 1);
        localPicasaSyncTaskFuture.startSync(arrayOfLong[k]);
      }
    }

    public void cancel()
    {
      monitorenter;
      while (true)
      {
        label21: Iterator localIterator;
        try
        {
          if (this.mPendingCount != 0)
          {
            boolean bool = this.mIsCancelled;
            if (!bool)
              break label21;
          }
          return;
          this.mIsCancelled = true;
          localIterator = this.mFutures.iterator();
          if (!localIterator.hasNext())
            break label65;
        }
        finally
        {
          monitorexit;
        }
        label65: this.mResult = 1;
      }
    }

    public Integer get()
    {
      monitorenter;
      try
      {
        waitDone();
        Integer localInteger = Integer.valueOf(this.mResult);
        monitorexit;
        return localInteger;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public boolean isCancelled()
    {
      monitorenter;
      try
      {
        boolean bool = this.mIsCancelled;
        monitorexit;
        return bool;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public boolean isDone()
    {
      monitorenter;
      try
      {
        int i = this.mPendingCount;
        if (i == 0)
        {
          j = 1;
          return j;
        }
        int j = 0;
      }
      finally
      {
        monitorexit;
      }
    }

    // ERROR //
    public void onSyncDone(com.android.gallery3d.data.MediaSet paramMediaSet, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 43	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mAlbumIds	[J
      //   4: ifnonnull +111 -> 115
      //   7: aconst_null
      //   8: astore 7
      //   10: aload_0
      //   11: getfield 28	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:this$0	Lcom/android/gallery3d/picasasource/PicasaPostAlbum;
      //   14: getfield 51	com/android/gallery3d/picasasource/BasePicasaAlbum:mSource	Lcom/android/gallery3d/picasasource/PicasaSource;
      //   17: aload_0
      //   18: getfield 28	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:this$0	Lcom/android/gallery3d/picasasource/PicasaPostAlbum;
      //   21: invokestatic 108	com/android/gallery3d/picasasource/PicasaPostAlbum:access$100	(Lcom/android/gallery3d/picasasource/PicasaPostAlbum;)Landroid/net/Uri;
      //   24: invokestatic 112	com/android/gallery3d/picasasource/PicasaPostAlbum:access$200	()[Ljava/lang/String;
      //   27: aconst_null
      //   28: aconst_null
      //   29: aconst_null
      //   30: invokevirtual 118	com/android/gallery3d/picasasource/PicasaSource:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   33: astore 7
      //   35: aload 7
      //   37: ifnull +73 -> 110
      //   40: aload 7
      //   42: invokeinterface 123 1 0
      //   47: ifeq +63 -> 110
      //   50: aload_0
      //   51: aload 7
      //   53: invokeinterface 127 1 0
      //   58: newarray long
      //   60: putfield 43	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mAlbumIds	[J
      //   63: aload_0
      //   64: iconst_0
      //   65: putfield 41	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mAlbumIndex	I
      //   68: iconst_0
      //   69: istore 9
      //   71: iload 9
      //   73: aload_0
      //   74: getfield 43	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mAlbumIds	[J
      //   77: arraylength
      //   78: if_icmpge +32 -> 110
      //   81: aload_0
      //   82: getfield 43	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mAlbumIds	[J
      //   85: iload 9
      //   87: aload 7
      //   89: iconst_0
      //   90: invokeinterface 131 2 0
      //   95: lastore
      //   96: aload 7
      //   98: invokeinterface 134 1 0
      //   103: pop
      //   104: iinc 9 1
      //   107: goto -36 -> 71
      //   110: aload 7
      //   112: invokestatic 140	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
      //   115: aload_0
      //   116: monitorenter
      //   117: aload_0
      //   118: iconst_m1
      //   119: aload_0
      //   120: getfield 64	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mPendingCount	I
      //   123: iadd
      //   124: putfield 64	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mPendingCount	I
      //   127: iload_2
      //   128: iconst_2
      //   129: if_icmpne +108 -> 237
      //   132: aload_0
      //   133: iconst_2
      //   134: putfield 35	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mResult	I
      //   137: aload_0
      //   138: getfield 64	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mPendingCount	I
      //   141: istore 4
      //   143: aconst_null
      //   144: astore 5
      //   146: iload 4
      //   148: ifne +13 -> 161
      //   151: aload_0
      //   152: getfield 37	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mListener	Lcom/android/gallery3d/data/MediaSet$SyncListener;
      //   155: astore 5
      //   157: aload_0
      //   158: invokevirtual 143	java/lang/Object:notifyAll	()V
      //   161: aload_0
      //   162: monitorexit
      //   163: ldc 145
      //   165: new 147	java/lang/StringBuilder
      //   168: dup
      //   169: invokespecial 148	java/lang/StringBuilder:<init>	()V
      //   172: ldc 150
      //   174: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   177: aload_1
      //   178: invokevirtual 160	com/android/gallery3d/data/MediaSet:getName	()Ljava/lang/String;
      //   181: invokestatic 164	com/android/gallery3d/common/Utils:maskDebugInfo	(Ljava/lang/Object;)Ljava/lang/String;
      //   184: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   187: ldc 166
      //   189: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   192: aload_0
      //   193: getfield 64	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mPendingCount	I
      //   196: invokevirtual 169	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   199: invokevirtual 172	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   202: invokestatic 178	com/android/gallery3d/data/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   205: pop
      //   206: aload 5
      //   208: ifnull +18 -> 226
      //   211: aload 5
      //   213: aload_0
      //   214: getfield 28	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:this$0	Lcom/android/gallery3d/picasasource/PicasaPostAlbum;
      //   217: aload_0
      //   218: getfield 35	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mResult	I
      //   221: invokeinterface 180 3 0
      //   226: return
      //   227: astore 8
      //   229: aload 7
      //   231: invokestatic 140	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
      //   234: aload 8
      //   236: athrow
      //   237: aload_0
      //   238: getfield 33	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mIsCancelled	Z
      //   241: ifne -104 -> 137
      //   244: aload_0
      //   245: iconst_2
      //   246: aload_0
      //   247: getfield 64	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:mPendingCount	I
      //   250: isub
      //   251: invokespecial 182	com/android/gallery3d/picasasource/PicasaPostAlbum$PostSyncFuture:syncNextPhotos	(I)V
      //   254: goto -117 -> 137
      //   257: astore_3
      //   258: aload_0
      //   259: monitorexit
      //   260: aload_3
      //   261: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   10	35	227	finally
      //   40	68	227	finally
      //   71	104	227	finally
      //   117	127	257	finally
      //   132	137	257	finally
      //   137	143	257	finally
      //   151	161	257	finally
      //   161	163	257	finally
      //   237	254	257	finally
      //   258	260	257	finally
    }

    public void startSync()
    {
      monitorenter;
      try
      {
        PicasaAlbumSet.PicasaSyncTaskFuture localPicasaSyncTaskFuture = new PicasaAlbumSet.PicasaSyncTaskFuture(PicasaPostAlbum.this.mSource, PicasaPostAlbum.this, this);
        this.mFutures = new ArrayList();
        this.mFutures.add(localPicasaSyncTaskFuture);
        this.mPendingCount = 1;
        localPicasaSyncTaskFuture.startSync(PicasaPostAlbum.this.mUserId);
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

    public void waitDone()
    {
      monitorenter;
      while (true)
        try
        {
          if (isDone())
            break label25;
        }
        catch (InterruptedException localInterruptedException)
        {
          Log.d("PostSyncFuture", "waitDone() interrupted");
          label25: return;
        }
        finally
        {
          monitorexit;
        }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.picasasource.PicasaPostAlbum
 * JD-Core Version:    0.5.4
 */