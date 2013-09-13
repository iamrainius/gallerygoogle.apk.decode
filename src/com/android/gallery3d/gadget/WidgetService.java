package com.android.gallery3d.gadget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;

@TargetApi(11)
public class WidgetService extends RemoteViewsService
{
  public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent paramIntent)
  {
    int i = paramIntent.getIntExtra("appWidgetId", 0);
    int j = paramIntent.getIntExtra("widget-type", 0);
    String str = paramIntent.getStringExtra("album-path");
    return new PhotoRVFactory((GalleryApp)getApplicationContext(), i, j, str);
  }

  private static class EmptySource
    implements WidgetSource
  {
    public void close()
    {
    }

    public Uri getContentUri(int paramInt)
    {
      throw new UnsupportedOperationException();
    }

    public Bitmap getImage(int paramInt)
    {
      throw new UnsupportedOperationException();
    }

    public void reload()
    {
    }

    public void setContentListener(ContentListener paramContentListener)
    {
    }

    public int size()
    {
      return 0;
    }
  }

  private static class PhotoRVFactory
    implements RemoteViewsService.RemoteViewsFactory, ContentListener
  {
    private final String mAlbumPath;
    private final GalleryApp mApp;
    private final int mAppWidgetId;
    private WidgetSource mSource;
    private final int mType;

    public PhotoRVFactory(GalleryApp paramGalleryApp, int paramInt1, int paramInt2, String paramString)
    {
      this.mApp = paramGalleryApp;
      this.mAppWidgetId = paramInt1;
      this.mType = paramInt2;
      this.mAlbumPath = paramString;
    }

    public int getCount()
    {
      return this.mSource.size();
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public RemoteViews getLoadingView()
    {
      RemoteViews localRemoteViews = new RemoteViews(this.mApp.getAndroidContext().getPackageName(), 2130968582);
      localRemoteViews.setProgressBar(2131558409, 0, 0, true);
      return localRemoteViews;
    }

    public RemoteViews getViewAt(int paramInt)
    {
      Bitmap localBitmap = this.mSource.getImage(paramInt);
      if (localBitmap == null)
        return getLoadingView();
      RemoteViews localRemoteViews = new RemoteViews(this.mApp.getAndroidContext().getPackageName(), 2130968584);
      localRemoteViews.setImageViewBitmap(2131558411, localBitmap);
      localRemoteViews.setOnClickFillInIntent(2131558411, new Intent().setFlags(67108864).setData(this.mSource.getContentUri(paramInt)));
      return localRemoteViews;
    }

    public int getViewTypeCount()
    {
      return 1;
    }

    public boolean hasStableIds()
    {
      return true;
    }

    public void onContentDirty()
    {
      AppWidgetManager.getInstance(this.mApp.getAndroidContext()).notifyAppWidgetViewDataChanged(this.mAppWidgetId, 2131558412);
    }

    public void onCreate()
    {
      MediaSet localMediaSet;
      Object localObject;
      if (this.mType == 2)
      {
        Path localPath = Path.fromString(this.mAlbumPath);
        localMediaSet = (MediaSet)this.mApp.getDataManager().getMediaObject(localPath);
        if (localMediaSet == null)
          localObject = new WidgetService.EmptySource(null);
      }
      for (this.mSource = ((WidgetSource)localObject); ; this.mSource = new LocalPhotoSource(this.mApp.getAndroidContext()))
      {
        label46: this.mSource.setContentListener(this);
        AppWidgetManager.getInstance(this.mApp.getAndroidContext()).notifyAppWidgetViewDataChanged(this.mAppWidgetId, 2131558412);
        return;
        localObject = new MediaSetSource(localMediaSet);
        break label46:
      }
    }

    public void onDataSetChanged()
    {
      this.mSource.reload();
    }

    public void onDestroy()
    {
      this.mSource.close();
      this.mSource = null;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.WidgetService
 * JD-Core Version:    0.5.4
 */