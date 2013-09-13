package com.android.gallery3d.onetimeinitializer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.LocalAlbum;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.gadget.WidgetDatabaseHelper;
import com.android.gallery3d.gadget.WidgetDatabaseHelper.Entry;
import com.android.gallery3d.util.GalleryUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GalleryWidgetMigrator
{
  private static final String NEW_EXT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
  private static final int RELATIVE_PATH_START = NEW_EXT_PATH.length();

  public static void migrateGalleryWidgets(Context paramContext)
  {
    if ("/mnt/sdcard".equals(NEW_EXT_PATH));
    SharedPreferences localSharedPreferences;
    do
    {
      return;
      localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
    }
    while (localSharedPreferences.getBoolean("gallery_widget_migration_done", false));
    try
    {
      migrateGalleryWidgetsInternal(paramContext);
      localSharedPreferences.edit().putBoolean("gallery_widget_migration_done", true).commit();
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("GalleryWidgetMigrator", "migrateGalleryWidgets", localThrowable);
    }
  }

  private static void migrateGalleryWidgetsInternal(Context paramContext)
  {
    DataManager localDataManager = ((GalleryApp)paramContext.getApplicationContext()).getDataManager();
    WidgetDatabaseHelper localWidgetDatabaseHelper = new WidgetDatabaseHelper(paramContext);
    List localList = localWidgetDatabaseHelper.getEntries(2);
    if (localList == null)
      return;
    HashMap localHashMap = new HashMap(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      WidgetDatabaseHelper.Entry localEntry = (WidgetDatabaseHelper.Entry)localIterator.next();
      Path localPath = Path.fromString(localEntry.albumPath);
      if (!((MediaSet)localDataManager.getMediaObject(localPath)) instanceof LocalAlbum)
        continue;
      localHashMap.put(Integer.valueOf(Integer.parseInt(localPath.getSuffix())), localEntry);
    }
    if (localHashMap.isEmpty())
      return;
    migrateLocalEntries(localHashMap, localWidgetDatabaseHelper);
  }

  private static void migrateLocalEntries(HashMap<Integer, WidgetDatabaseHelper.Entry> paramHashMap, WidgetDatabaseHelper paramWidgetDatabaseHelper)
  {
    File localFile = Environment.getExternalStorageDirectory();
    updatePath(new File(localFile, "DCIM"), paramHashMap, paramWidgetDatabaseHelper);
    if (paramHashMap.isEmpty())
      return;
    updatePath(localFile, paramHashMap, paramWidgetDatabaseHelper);
  }

  private static void updatePath(File paramFile, HashMap<Integer, WidgetDatabaseHelper.Entry> paramHashMap, WidgetDatabaseHelper paramWidgetDatabaseHelper)
  {
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile == null)
      return;
    int i = arrayOfFile.length;
    for (int j = 0; j < i; ++j)
    {
      File localFile = arrayOfFile[j];
      if ((!localFile.isDirectory()) || (paramHashMap.isEmpty()))
        continue;
      String str1 = localFile.getAbsolutePath();
      WidgetDatabaseHelper.Entry localEntry = (WidgetDatabaseHelper.Entry)paramHashMap.remove(Integer.valueOf(GalleryUtils.getBucketId("/mnt/sdcard" + str1.substring(RELATIVE_PATH_START))));
      if (localEntry != null)
      {
        int k = GalleryUtils.getBucketId(str1);
        String str2 = Path.fromString(localEntry.albumPath).getParent().getChild(k).toString();
        Log.d("GalleryWidgetMigrator", "migrate from " + localEntry.albumPath + " to " + str2);
        localEntry.albumPath = str2;
        paramWidgetDatabaseHelper.updateEntry(localEntry);
      }
      updatePath(localFile, paramHashMap, paramWidgetDatabaseHelper);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.onetimeinitializer.GalleryWidgetMigrator
 * JD-Core Version:    0.5.4
 */