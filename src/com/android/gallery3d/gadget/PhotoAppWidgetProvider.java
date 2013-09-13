package com.android.gallery3d.gadget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.onetimeinitializer.GalleryWidgetMigrator;

public class PhotoAppWidgetProvider extends AppWidgetProvider
{
  static RemoteViews buildFrameWidget(Context paramContext, int paramInt, WidgetDatabaseHelper.Entry paramEntry)
  {
    RemoteViews localRemoteViews = new RemoteViews(paramContext.getPackageName(), 2130968623);
    try
    {
      byte[] arrayOfByte = paramEntry.imageData;
      localRemoteViews.setImageViewBitmap(2131558541, BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length));
      if (paramEntry.imageUri == null);
    }
    catch (Throwable localThrowable1)
    {
      try
      {
        Uri localUri = Uri.parse(paramEntry.imageUri);
        localRemoteViews.setOnClickPendingIntent(2131558541, PendingIntent.getActivity(paramContext, 0, new Intent(paramContext, WidgetClickHandler.class).setData(localUri), 268435456));
        return localRemoteViews;
        localThrowable1 = localThrowable1;
        Log.w("WidgetProvider", "cannot load widget image: " + paramInt, localThrowable1);
      }
      catch (Throwable localThrowable2)
      {
        Log.w("WidgetProvider", "cannot load widget uri: " + paramInt, localThrowable2);
      }
    }
    return localRemoteViews;
  }

  @TargetApi(11)
  private static RemoteViews buildStackWidget(Context paramContext, int paramInt, WidgetDatabaseHelper.Entry paramEntry)
  {
    RemoteViews localRemoteViews = new RemoteViews(paramContext.getPackageName(), 2130968583);
    Intent localIntent = new Intent(paramContext, WidgetService.class);
    localIntent.putExtra("appWidgetId", paramInt);
    localIntent.putExtra("widget-type", paramEntry.type);
    localIntent.putExtra("album-path", paramEntry.albumPath);
    localIntent.setData(Uri.parse("widget://gallery/" + paramInt));
    localRemoteViews.setRemoteAdapter(paramInt, 2131558412, localIntent);
    localRemoteViews.setEmptyView(2131558412, 2131558410);
    localRemoteViews.setPendingIntentTemplate(2131558412, PendingIntent.getActivity(paramContext, 0, new Intent(paramContext, WidgetClickHandler.class), 134217728));
    return localRemoteViews;
  }

  static RemoteViews buildWidget(Context paramContext, int paramInt, WidgetDatabaseHelper.Entry paramEntry)
  {
    switch (paramEntry.type)
    {
    default:
      throw new RuntimeException("invalid type - " + paramEntry.type);
    case 1:
    case 2:
      return buildStackWidget(paramContext, paramInt, paramEntry);
    case 0:
    }
    return buildFrameWidget(paramContext, paramInt, paramEntry);
  }

  public void onDeleted(Context paramContext, int[] paramArrayOfInt)
  {
    WidgetDatabaseHelper localWidgetDatabaseHelper = new WidgetDatabaseHelper(paramContext);
    int i = paramArrayOfInt.length;
    for (int j = 0; j < i; ++j)
      localWidgetDatabaseHelper.deleteEntry(paramArrayOfInt[j]);
    localWidgetDatabaseHelper.close();
  }

  public void onUpdate(Context paramContext, AppWidgetManager paramAppWidgetManager, int[] paramArrayOfInt)
  {
    if (ApiHelper.HAS_REMOTE_VIEWS_SERVICE)
      GalleryWidgetMigrator.migrateGalleryWidgets(paramContext);
    WidgetDatabaseHelper localWidgetDatabaseHelper = new WidgetDatabaseHelper(paramContext);
    while (true)
    {
      int j;
      int k;
      try
      {
        int i = paramArrayOfInt.length;
        j = 0;
        if (j >= i)
          break label110;
        k = paramArrayOfInt[j];
        WidgetDatabaseHelper.Entry localEntry = localWidgetDatabaseHelper.getEntry(k);
        if (localEntry != null)
          paramAppWidgetManager.updateAppWidget(k, buildWidget(paramContext, k, localEntry));
      }
      finally
      {
        localWidgetDatabaseHelper.close();
      }
      label110: localWidgetDatabaseHelper.close();
      super.onUpdate(paramContext, paramAppWidgetManager, paramArrayOfInt);
      return;
      ++j;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.PhotoAppWidgetProvider
 * JD-Core Version:    0.5.4
 */