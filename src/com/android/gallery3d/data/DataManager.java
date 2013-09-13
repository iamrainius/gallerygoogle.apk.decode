package com.android.gallery3d.data;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.app.StitchingChangeListener;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.picasasource.PicasaSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

public class DataManager
  implements StitchingChangeListener
{
  public static final Object LOCK = new Object();
  private static final String TOP_IMAGE_SET_PATH;
  private static final String TOP_SET_PATH;
  public static final Comparator<MediaItem> sDateTakenComparator;
  private int mActiveCount = 0;
  private GalleryApp mApplication;
  private final Handler mDefaultMainHandler;
  private HashMap<Uri, NotifyBroker> mNotifierMap = new HashMap();
  private HashMap<String, MediaSource> mSourceMap = new LinkedHashMap();

  static
  {
    String str1;
    if (ApiHelper.HAS_MTP)
    {
      str1 = "/combo/{/mtp,/local/all,/picasa/all}";
      label19: TOP_SET_PATH = str1;
      if (!ApiHelper.HAS_MTP)
        break label54;
    }
    for (String str2 = "/combo/{/mtp,/local/image,/picasa/image}"; ; str2 = "/combo/{/local/image,/picasa/image}")
    {
      TOP_IMAGE_SET_PATH = str2;
      sDateTakenComparator = new DateTakenComparator(null);
      return;
      str1 = "/combo/{/local/all,/picasa/all}";
      label54: break label19:
    }
  }

  public DataManager(GalleryApp paramGalleryApp)
  {
    this.mApplication = paramGalleryApp;
    this.mDefaultMainHandler = new Handler(paramGalleryApp.getMainLooper());
  }

  void addSource(MediaSource paramMediaSource)
  {
    if (paramMediaSource == null)
      return;
    this.mSourceMap.put(paramMediaSource.getPrefix(), paramMediaSource);
  }

  public void delete(Path paramPath)
  {
    getMediaObject(paramPath).delete();
  }

  public Path findPathByUri(Uri paramUri, String paramString)
  {
    if (paramUri == null)
      return null;
    Iterator localIterator = this.mSourceMap.values().iterator();
    Path localPath;
    while (localIterator.hasNext())
    {
      localPath = ((MediaSource)localIterator.next()).findPathByUri(paramUri, paramString);
      if (localPath != null)
        return localPath;
    }
    return null;
  }

  public Uri getContentUri(Path paramPath)
  {
    return getMediaObject(paramPath).getContentUri();
  }

  public Path getDefaultSetOf(Path paramPath)
  {
    MediaSource localMediaSource = (MediaSource)this.mSourceMap.get(paramPath.getPrefix());
    if (localMediaSource == null)
      return null;
    return localMediaSource.getDefaultSetOf(paramPath);
  }

  public MediaObject getMediaObject(Path paramPath)
  {
    synchronized (LOCK)
    {
      MediaObject localMediaObject1 = paramPath.getObject();
      if (localMediaObject1 != null)
        return localMediaObject1;
      MediaSource localMediaSource = (MediaSource)this.mSourceMap.get(paramPath.getPrefix());
      if (localMediaSource == null)
      {
        Log.w("DataManager", "cannot find media source for path: " + paramPath);
        return null;
      }
      try
      {
        MediaObject localMediaObject2 = localMediaSource.createMediaObject(paramPath);
        if (localMediaObject2 == null)
          Log.w("DataManager", "cannot create media object: " + paramPath);
        return localMediaObject2;
      }
      catch (Throwable localThrowable)
      {
        Log.w("DataManager", "exception in creating media object: " + paramPath, localThrowable);
        return null;
      }
    }
  }

  public MediaObject getMediaObject(String paramString)
  {
    return getMediaObject(Path.fromString(paramString));
  }

  public MediaSet getMediaSet(Path paramPath)
  {
    return (MediaSet)getMediaObject(paramPath);
  }

  public MediaSet getMediaSet(String paramString)
  {
    return (MediaSet)getMediaObject(paramString);
  }

  public MediaSet[] getMediaSetsFromString(String paramString)
  {
    String[] arrayOfString = Path.splitSequence(paramString);
    int i = arrayOfString.length;
    MediaSet[] arrayOfMediaSet = new MediaSet[i];
    for (int j = 0; j < i; ++j)
      arrayOfMediaSet[j] = getMediaSet(arrayOfString[j]);
    return arrayOfMediaSet;
  }

  public int getMediaType(Path paramPath)
  {
    return getMediaObject(paramPath).getMediaType();
  }

  public int getSupportedOperations(Path paramPath)
  {
    return getMediaObject(paramPath).getSupportedOperations();
  }

  public String getTopSetPath(int paramInt)
  {
    switch (paramInt)
    {
    case 4:
    default:
      throw new IllegalArgumentException();
    case 1:
      return TOP_IMAGE_SET_PATH;
    case 2:
      return "/combo/{/local/video,/picasa/video}";
    case 3:
      return TOP_SET_PATH;
    case 5:
      return "/local/image";
    case 6:
      return "/local/video";
    case 7:
    }
    return "/local/all";
  }

  public long getTotalTargetCacheSize()
  {
    long l = 0L;
    Iterator localIterator = this.mSourceMap.values().iterator();
    while (localIterator.hasNext())
      l += ((MediaSource)localIterator.next()).getTotalTargetCacheSize();
    return l;
  }

  public long getTotalUsedCacheSize()
  {
    long l = 0L;
    Iterator localIterator = this.mSourceMap.values().iterator();
    while (localIterator.hasNext())
      l += ((MediaSource)localIterator.next()).getTotalUsedCacheSize();
    return l;
  }

  public void initializeSourceMap()
  {
    monitorenter;
    Iterator localIterator;
    try
    {
      boolean bool = this.mSourceMap.isEmpty();
      if (!bool);
      do
      {
        do
        {
          return;
          addSource(new LocalSource(this.mApplication));
          addSource(new PicasaSource(this.mApplication));
          if (ApiHelper.HAS_MTP)
            addSource(new MtpSource(this.mApplication));
          addSource(new ComboSource(this.mApplication));
          addSource(new ClusterSource(this.mApplication));
          addSource(new FilterSource(this.mApplication));
          addSource(new SecureSource(this.mApplication));
          addSource(new UriSource(this.mApplication));
          addSource(new SnailSource(this.mApplication));
        }
        while (this.mActiveCount <= 0);
        localIterator = this.mSourceMap.values().iterator();
      }
      while (!localIterator.hasNext());
    }
    finally
    {
      monitorexit;
    }
  }

  public void mapMediaItems(ArrayList<Path> paramArrayList, MediaSet.ItemConsumer paramItemConsumer, int paramInt)
  {
    HashMap localHashMap = new HashMap();
    int i = paramArrayList.size();
    for (int j = 0; j < i; ++j)
    {
      Path localPath = (Path)paramArrayList.get(j);
      String str2 = localPath.getPrefix();
      ArrayList localArrayList = (ArrayList)localHashMap.get(str2);
      if (localArrayList == null)
      {
        localArrayList = new ArrayList();
        localHashMap.put(str2, localArrayList);
      }
      localArrayList.add(new MediaSource.PathId(localPath, j + paramInt));
    }
    Iterator localIterator = localHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      ((MediaSource)this.mSourceMap.get(str1)).mapMediaItems((ArrayList)localEntry.getValue(), paramItemConsumer);
    }
  }

  public void onStitchingProgress(Uri paramUri, int paramInt)
  {
  }

  public void onStitchingQueued(Uri paramUri)
  {
  }

  public void onStitchingResult(Uri paramUri)
  {
    Path localPath = findPathByUri(paramUri, null);
    if (localPath == null)
      return;
    MediaObject localMediaObject = getMediaObject(localPath);
    if (localMediaObject == null)
      return;
    localMediaObject.clearCachedPanoramaSupport();
  }

  public void pause()
  {
    int i = -1 + this.mActiveCount;
    this.mActiveCount = i;
    if (i != 0)
      return;
    Iterator localIterator = this.mSourceMap.values().iterator();
    while (localIterator.hasNext())
      ((MediaSource)localIterator.next()).pause();
  }

  public MediaObject peekMediaObject(Path paramPath)
  {
    return paramPath.getObject();
  }

  public void registerChangeNotifier(Uri paramUri, ChangeNotifier paramChangeNotifier)
  {
    Object localObject3;
    NotifyBroker localNotifyBroker;
    synchronized (this.mNotifierMap)
    {
      localObject3 = (NotifyBroker)this.mNotifierMap.get(paramUri);
      if (localObject3 == null)
        localNotifyBroker = new NotifyBroker(this.mDefaultMainHandler);
    }
    try
    {
      this.mApplication.getContentResolver().registerContentObserver(paramUri, true, localNotifyBroker);
      this.mNotifierMap.put(paramUri, localNotifyBroker);
      localObject3 = localNotifyBroker;
      monitorexit;
      ((NotifyBroker)localObject3).registerNotifier(paramChangeNotifier);
      return;
      localObject1 = finally;
      monitorexit;
      throw localObject1;
    }
    finally
    {
    }
  }

  public void resume()
  {
    int i = 1 + this.mActiveCount;
    this.mActiveCount = i;
    if (i != 1)
      return;
    Iterator localIterator = this.mSourceMap.values().iterator();
    while (localIterator.hasNext())
      ((MediaSource)localIterator.next()).resume();
  }

  public void rotate(Path paramPath, int paramInt)
  {
    getMediaObject(paramPath).rotate(paramInt);
  }

  private static class DateTakenComparator
    implements Comparator<MediaItem>
  {
    public int compare(MediaItem paramMediaItem1, MediaItem paramMediaItem2)
    {
      return -Utils.compare(paramMediaItem1.getDateInMs(), paramMediaItem2.getDateInMs());
    }
  }

  private static class NotifyBroker extends ContentObserver
  {
    private WeakHashMap<ChangeNotifier, Object> mNotifiers = new WeakHashMap();

    public NotifyBroker(Handler paramHandler)
    {
      super(paramHandler);
    }

    public void onChange(boolean paramBoolean)
    {
      monitorenter;
      Iterator localIterator;
      try
      {
        localIterator = this.mNotifiers.keySet().iterator();
        if (!localIterator.hasNext())
          break label45;
      }
      finally
      {
        monitorexit;
      }
      label45: monitorexit;
    }

    public void registerNotifier(ChangeNotifier paramChangeNotifier)
    {
      monitorenter;
      try
      {
        this.mNotifiers.put(paramChangeNotifier, null);
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
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.DataManager
 * JD-Core Version:    0.5.4
 */