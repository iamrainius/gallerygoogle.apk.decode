package com.android.gallery3d.filtershow.tools;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import com.adobe.xmp.XMPMeta;
import com.android.gallery3d.filtershow.cache.ImageLoader;
import com.android.gallery3d.filtershow.presets.ImagePreset;
import com.android.gallery3d.util.XmpUtilHelper;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class SaveCopyTask extends AsyncTask<ImagePreset, Void, Uri>
{
  private static final String[] COPY_EXIF_ATTRIBUTES = { "FNumber", "DateTime", "ExposureTime", "Flash", "FocalLength", "GPSAltitude", "GPSAltitudeRef", "GPSDateStamp", "GPSLatitude", "GPSLatitudeRef", "GPSLongitude", "GPSLongitudeRef", "GPSProcessingMethod", "GPSDateStamp", "ISOSpeedRatings", "Make", "Model", "WhiteBalance" };
  private final Callback callback;
  private final Context context;
  private final File destinationFile;
  private final String saveFileName;
  private final Uri sourceUri;

  public SaveCopyTask(Context paramContext, Uri paramUri, File paramFile, Callback paramCallback)
  {
    this.context = paramContext;
    this.sourceUri = paramUri;
    this.callback = paramCallback;
    if (paramFile == null);
    for (this.destinationFile = getNewFile(paramContext, paramUri); ; this.destinationFile = paramFile)
    {
      this.saveFileName = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
      return;
    }
  }

  private static void closeStream(Closeable paramCloseable)
  {
    if (paramCloseable != null);
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  private void copyExif(Uri paramUri, String paramString)
  {
    if ("file".equals(paramUri.getScheme()))
    {
      copyExif(paramUri.getPath(), paramString);
      return;
    }
    String[] arrayOfString = { "_data" };
    try
    {
      Cursor localCursor = this.context.getContentResolver().query(paramUri, arrayOfString, null, null, null);
      if (localCursor.moveToFirst())
      {
        String str = localCursor.getString(0);
        if (new File(str).exists())
          copyExif(str, paramString);
      }
      localCursor.close();
      return;
    }
    catch (Exception localException)
    {
      Log.w("SaveCopyTask", "Failed to copy exif", localException);
    }
  }

  private static void copyExif(String paramString1, String paramString2)
  {
    while (true)
    {
      int k;
      try
      {
        ExifInterface localExifInterface1 = new ExifInterface(paramString1);
        ExifInterface localExifInterface2 = new ExifInterface(paramString2);
        int i = 0;
        String[] arrayOfString = COPY_EXIF_ATTRIBUTES;
        int j = arrayOfString.length;
        k = 0;
        if (k < j)
        {
          String str1 = arrayOfString[k];
          String str2 = localExifInterface1.getAttribute(str1);
          if (str2 != null)
          {
            i = 1;
            localExifInterface2.setAttribute(str1, str2);
          }
        }
        else
        {
          if (i != 0)
            localExifInterface2.saveAttributes();
          return;
        }
      }
      catch (IOException localIOException)
      {
        Log.w("SaveCopyTask", "Failed to copy exif metadata", localIOException);
        return;
      }
      ++k;
    }
  }

  public static File getFinalSaveDirectory(Context paramContext, Uri paramUri)
  {
    File localFile = getSaveDirectory(paramContext, paramUri);
    if ((localFile == null) || (!localFile.canWrite()))
      localFile = new File(Environment.getExternalStorageDirectory(), "EditedOnlinePhotos");
    if (!localFile.exists())
      localFile.mkdirs();
    return localFile;
  }

  public static File getNewFile(Context paramContext, Uri paramUri)
  {
    File localFile = getFinalSaveDirectory(paramContext, paramUri);
    String str = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
    return new File(localFile, str + ".JPG");
  }

  private static File getSaveDirectory(Context paramContext, Uri paramUri)
  {
    File[] arrayOfFile = new File[1];
    querySource(paramContext, paramUri, new String[] { "_data" }, new ContentResolverQueryCallback(arrayOfFile)
    {
      public void onCursorResult(Cursor paramCursor)
      {
        this.val$dir[0] = new File(paramCursor.getString(0)).getParentFile();
      }
    });
    return arrayOfFile[0];
  }

  public static Uri insertContent(Context paramContext, Uri paramUri, File paramFile, String paramString)
  {
    long l = System.currentTimeMillis() / 1000L;
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("title", paramString);
    localContentValues.put("_display_name", paramFile.getName());
    localContentValues.put("mime_type", "image/jpeg");
    localContentValues.put("datetaken", Long.valueOf(l));
    localContentValues.put("date_modified", Long.valueOf(l));
    localContentValues.put("date_added", Long.valueOf(l));
    localContentValues.put("orientation", Integer.valueOf(0));
    localContentValues.put("_data", paramFile.getAbsolutePath());
    localContentValues.put("_size", Long.valueOf(paramFile.length()));
    querySource(paramContext, paramUri, new String[] { "datetaken", "latitude", "longitude" }, new ContentResolverQueryCallback(localContentValues)
    {
      public void onCursorResult(Cursor paramCursor)
      {
        this.val$values.put("datetaken", Long.valueOf(paramCursor.getLong(0)));
        double d1 = paramCursor.getDouble(1);
        double d2 = paramCursor.getDouble(2);
        if ((d1 == 0.0D) && (d2 == 0.0D))
          return;
        this.val$values.put("latitude", Double.valueOf(d1));
        this.val$values.put("longitude", Double.valueOf(d2));
      }
    });
    return paramContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
  }

  private Bitmap loadMutableBitmap()
    throws FileNotFoundException
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inMutable = true;
    return ImageLoader.rotateToPortrait(BitmapFactory.decodeStream(this.context.getContentResolver().openInputStream(this.sourceUri), null, localOptions), ImageLoader.getOrientation(this.context, this.sourceUri));
  }

  private static void querySource(Context paramContext, Uri paramUri, String[] paramArrayOfString, ContentResolverQueryCallback paramContentResolverQueryCallback)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    Cursor localCursor = null;
    try
    {
      localCursor = localContentResolver.query(paramUri, paramArrayOfString, null, null, null);
      if ((localCursor != null) && (localCursor.moveToNext()))
        paramContentResolverQueryCallback.onCursorResult(localCursor);
      return;
    }
    catch (Exception localException)
    {
      return;
    }
    finally
    {
      if (localCursor != null)
        localCursor.close();
    }
  }

  // ERROR //
  public static void saveBitmap(Bitmap paramBitmap, File paramFile, Object paramObject)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: new 347	java/io/FileOutputStream
    //   5: dup
    //   6: aload_1
    //   7: invokespecial 350	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   10: astore 4
    //   12: aload_0
    //   13: getstatic 356	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   16: bipush 95
    //   18: aload 4
    //   20: invokevirtual 362	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   23: pop
    //   24: aload 4
    //   26: invokestatic 364	com/android/gallery3d/filtershow/tools/SaveCopyTask:closeStream	(Ljava/io/Closeable;)V
    //   29: aload_2
    //   30: ifnull +12 -> 42
    //   33: aload_1
    //   34: invokevirtual 280	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   37: aload_2
    //   38: invokestatic 370	com/android/gallery3d/util/XmpUtilHelper:writeXMPMeta	(Ljava/lang/String;Ljava/lang/Object;)Z
    //   41: pop
    //   42: return
    //   43: astore 10
    //   45: ldc 164
    //   47: new 212	java/lang/StringBuilder
    //   50: dup
    //   51: invokespecial 213	java/lang/StringBuilder:<init>	()V
    //   54: ldc_w 372
    //   57: invokevirtual 217	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: aload_1
    //   61: invokevirtual 280	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   64: invokevirtual 217	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: invokevirtual 222	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   70: invokestatic 376	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   73: pop
    //   74: aload_3
    //   75: invokestatic 364	com/android/gallery3d/filtershow/tools/SaveCopyTask:closeStream	(Ljava/io/Closeable;)V
    //   78: goto -49 -> 29
    //   81: astore 6
    //   83: aload_3
    //   84: invokestatic 364	com/android/gallery3d/filtershow/tools/SaveCopyTask:closeStream	(Ljava/io/Closeable;)V
    //   87: aload 6
    //   89: athrow
    //   90: astore 6
    //   92: aload 4
    //   94: astore_3
    //   95: goto -12 -> 83
    //   98: astore 5
    //   100: aload 4
    //   102: astore_3
    //   103: goto -58 -> 45
    //
    // Exception table:
    //   from	to	target	type
    //   2	12	43	java/io/FileNotFoundException
    //   2	12	81	finally
    //   45	74	81	finally
    //   12	24	90	finally
    //   12	24	98	java/io/FileNotFoundException
  }

  protected Uri doInBackground(ImagePreset[] paramArrayOfImagePreset)
  {
    if (paramArrayOfImagePreset[0] == null)
      return null;
    ImagePreset localImagePreset = paramArrayOfImagePreset[0];
    try
    {
      Bitmap localBitmap = localImagePreset.apply(loadMutableBitmap());
      boolean bool = localImagePreset.isPanoramaSafe();
      XMPMeta localXMPMeta = null;
      if (bool)
        localXMPMeta = XmpUtilHelper.extractXMPMeta(this.context.getContentResolver().openInputStream(this.sourceUri));
      saveBitmap(localBitmap, this.destinationFile, localXMPMeta);
      copyExif(this.sourceUri, this.destinationFile.getAbsolutePath());
      Uri localUri = insertContent(this.context, this.sourceUri, this.destinationFile, this.saveFileName);
      localBitmap.recycle();
      return localUri;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Log.w("SaveCopyTask", "Failed to save image!", localFileNotFoundException);
    }
    return null;
  }

  protected void onPostExecute(Uri paramUri)
  {
    if (this.callback == null)
      return;
    this.callback.onComplete(paramUri);
  }

  public static abstract interface Callback
  {
    public abstract void onComplete(Uri paramUri);
  }

  private static abstract interface ContentResolverQueryCallback
  {
    public abstract void onCursorResult(Cursor paramCursor);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.tools.SaveCopyTask
 * JD-Core Version:    0.5.4
 */