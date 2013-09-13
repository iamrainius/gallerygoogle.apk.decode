package com.android.gallery3d.gadget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.android.gallery3d.app.AlbumPicker;
import com.android.gallery3d.app.DialogPicker;
import com.android.gallery3d.common.ApiHelper;

public class WidgetConfigure extends Activity
{
  private static int MAX_WIDGET_SIDE;
  private static float WIDGET_SCALE_FACTOR = 1.5F;
  private int mAppWidgetId = -1;
  private Uri mPickedItem;

  static
  {
    MAX_WIDGET_SIDE = 360;
  }

  private void setChoosenAlbum(Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("album-path");
    WidgetDatabaseHelper localWidgetDatabaseHelper = new WidgetDatabaseHelper(this);
    try
    {
      localWidgetDatabaseHelper.setWidget(this.mAppWidgetId, 2, str);
      updateWidgetAndFinish(localWidgetDatabaseHelper.getEntry(this.mAppWidgetId));
      return;
    }
    finally
    {
      localWidgetDatabaseHelper.close();
    }
  }

  private void setChoosenPhoto(Intent paramIntent)
  {
    Resources localResources = getResources();
    float f1 = localResources.getDimension(2131624010);
    float f2 = localResources.getDimension(2131624011);
    float f3 = Math.min(WIDGET_SCALE_FACTOR, MAX_WIDGET_SIDE / Math.max(f1, f2));
    int i = Math.round(f1 * f3);
    int j = Math.round(f2 * f3);
    this.mPickedItem = paramIntent.getData();
    startActivityForResult(new Intent("com.android.camera.action.CROP", this.mPickedItem).putExtra("outputX", i).putExtra("outputY", j).putExtra("aspectX", i).putExtra("aspectY", j).putExtra("scaleUpIfNeeded", true).putExtra("scale", true).putExtra("return-data", true), 3);
  }

  private void setPhotoWidget(Intent paramIntent)
  {
    Bitmap localBitmap = (Bitmap)paramIntent.getParcelableExtra("data");
    WidgetDatabaseHelper localWidgetDatabaseHelper = new WidgetDatabaseHelper(this);
    try
    {
      localWidgetDatabaseHelper.setPhoto(this.mAppWidgetId, this.mPickedItem, localBitmap);
      updateWidgetAndFinish(localWidgetDatabaseHelper.getEntry(this.mAppWidgetId));
      return;
    }
    finally
    {
      localWidgetDatabaseHelper.close();
    }
  }

  private void setWidgetType(Intent paramIntent)
  {
    int i = paramIntent.getIntExtra("widget-type", 2131558423);
    if (i == 2131558421)
    {
      startActivityForResult(new Intent(this, AlbumPicker.class), 2);
      return;
    }
    if (i == 2131558423)
    {
      WidgetDatabaseHelper localWidgetDatabaseHelper = new WidgetDatabaseHelper(this);
      try
      {
        localWidgetDatabaseHelper.setWidget(this.mAppWidgetId, 1, null);
        updateWidgetAndFinish(localWidgetDatabaseHelper.getEntry(this.mAppWidgetId));
        return;
      }
      finally
      {
        localWidgetDatabaseHelper.close();
      }
    }
    startActivityForResult(new Intent(this, DialogPicker.class).setAction("android.intent.action.GET_CONTENT").setType("image/*"), 4);
  }

  private void updateWidgetAndFinish(WidgetDatabaseHelper.Entry paramEntry)
  {
    AppWidgetManager localAppWidgetManager = AppWidgetManager.getInstance(this);
    RemoteViews localRemoteViews = PhotoAppWidgetProvider.buildWidget(this, this.mAppWidgetId, paramEntry);
    localAppWidgetManager.updateAppWidget(this.mAppWidgetId, localRemoteViews);
    setResult(-1, new Intent().putExtra("appWidgetId", this.mAppWidgetId));
    finish();
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt2 != -1)
    {
      setResult(paramInt2, new Intent().putExtra("appWidgetId", this.mAppWidgetId));
      finish();
      return;
    }
    if (paramInt1 == 1)
    {
      setWidgetType(paramIntent);
      return;
    }
    if (paramInt1 == 2)
    {
      setChoosenAlbum(paramIntent);
      return;
    }
    if (paramInt1 == 4)
    {
      setChoosenPhoto(paramIntent);
      return;
    }
    if (paramInt1 == 3)
    {
      setPhotoWidget(paramIntent);
      return;
    }
    throw new AssertionError("unknown request: " + paramInt1);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mAppWidgetId = getIntent().getIntExtra("appWidgetId", -1);
    if (this.mAppWidgetId == -1)
    {
      setResult(0);
      finish();
      return;
    }
    if (paramBundle == null)
    {
      if (ApiHelper.HAS_REMOTE_VIEWS_SERVICE)
      {
        startActivityForResult(new Intent(this, WidgetTypeChooser.class), 1);
        return;
      }
      setWidgetType(new Intent().putExtra("widget-type", 2131558422));
      return;
    }
    this.mPickedItem = ((Uri)paramBundle.getParcelable("picked-item"));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.WidgetConfigure
 * JD-Core Version:    0.5.4
 */