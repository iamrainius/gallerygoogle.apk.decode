package com.android.gallery3d.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.AsyncTaskUtil;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MtpImage;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.picasasource.PicasaSource;
import com.android.gallery3d.util.GalleryUtils;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GalleryProvider extends ContentProvider
{
  public static final Uri BASE_URI = Uri.parse("content://com.android.gallery3d.provider");
  private static final String[] SUPPORTED_PICASA_COLUMNS = { "user_account", "picasa_id", "_display_name", "_size", "mime_type", "datetaken", "latitude", "longitude", "orientation" };
  private static Uri sBaseUri;
  private DataManager mDataManager;

  public static String getAuthority(Context paramContext)
  {
    return paramContext.getPackageName() + ".provider";
  }

  public static Uri getUriFor(Context paramContext, Path paramPath)
  {
    if (sBaseUri == null)
      sBaseUri = Uri.parse("content://" + paramContext.getPackageName() + ".provider");
    return sBaseUri.buildUpon().appendEncodedPath(paramPath.toString().substring(1)).build();
  }

  private static <T> ParcelFileDescriptor openPipeHelper(T paramT, PipeDataWriter<T> paramPipeDataWriter)
    throws FileNotFoundException
  {
    try
    {
      ParcelFileDescriptor[] arrayOfParcelFileDescriptor = ParcelFileDescriptor.createPipe();
      AsyncTaskUtil.executeInParallel(new AsyncTask(paramPipeDataWriter, arrayOfParcelFileDescriptor, paramT)
      {
        protected Object doInBackground(Object[] paramArrayOfObject)
        {
          try
          {
            this.val$func.writeDataToPipe(this.val$pipe[1], this.val$args);
            return null;
          }
          finally
          {
            Utils.closeSilently(this.val$pipe[1]);
          }
        }
      }
      , (Object[])null);
      ParcelFileDescriptor localParcelFileDescriptor = arrayOfParcelFileDescriptor[0];
      return localParcelFileDescriptor;
    }
    catch (IOException localIOException)
    {
      throw new FileNotFoundException("failure making pipe");
    }
  }

  private Cursor queryMtpItem(MtpImage paramMtpImage, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    Object[] arrayOfObject = new Object[paramArrayOfString1.length];
    int i = 0;
    int j = paramArrayOfString1.length;
    if (i < j)
    {
      label14: String str = paramArrayOfString1[i];
      if ("_display_name".equals(str))
        arrayOfObject[i] = paramMtpImage.getName();
      while (true)
      {
        ++i;
        break label14:
        if ("_size".equals(str))
          arrayOfObject[i] = Long.valueOf(paramMtpImage.getSize());
        if ("mime_type".equals(str))
          arrayOfObject[i] = paramMtpImage.getMimeType();
        if ("datetaken".equals(str))
          arrayOfObject[i] = Long.valueOf(paramMtpImage.getDateInMs());
        Log.w("GalleryProvider", "unsupported column: " + str);
      }
    }
    MatrixCursor localMatrixCursor = new MatrixCursor(paramArrayOfString1);
    localMatrixCursor.addRow(arrayOfObject);
    return localMatrixCursor;
  }

  private Cursor queryPicasaItem(MediaObject paramMediaObject, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    if (paramArrayOfString1 == null)
      paramArrayOfString1 = SUPPORTED_PICASA_COLUMNS;
    Object[] arrayOfObject = new Object[paramArrayOfString1.length];
    double d1 = PicasaSource.getLatitude(paramMediaObject);
    double d2 = PicasaSource.getLongitude(paramMediaObject);
    boolean bool = GalleryUtils.isValidLocation(d1, d2);
    int i = 0;
    int j = paramArrayOfString1.length;
    if (i < j)
    {
      label43: String str = paramArrayOfString1[i];
      if ("user_account".equals(str))
        arrayOfObject[i] = PicasaSource.getUserAccount(getContext(), paramMediaObject);
      while (true)
      {
        label79: ++i;
        break label43:
        if ("picasa_id".equals(str))
          arrayOfObject[i] = Long.valueOf(PicasaSource.getPicasaId(paramMediaObject));
        if ("_display_name".equals(str))
          arrayOfObject[i] = PicasaSource.getImageTitle(paramMediaObject);
        if ("_size".equals(str))
          arrayOfObject[i] = Integer.valueOf(PicasaSource.getImageSize(paramMediaObject));
        if ("mime_type".equals(str))
          arrayOfObject[i] = PicasaSource.getContentType(paramMediaObject);
        if ("datetaken".equals(str))
          arrayOfObject[i] = Long.valueOf(PicasaSource.getDateTaken(paramMediaObject));
        if ("latitude".equals(str))
        {
          if (bool);
          for (Double localDouble2 = Double.valueOf(d1); ; localDouble2 = null)
          {
            arrayOfObject[i] = localDouble2;
            break label79:
          }
        }
        if ("longitude".equals(str))
        {
          if (bool);
          for (Double localDouble1 = Double.valueOf(d2); ; localDouble1 = null)
          {
            arrayOfObject[i] = localDouble1;
            break label79:
          }
        }
        if ("orientation".equals(str))
          arrayOfObject[i] = Integer.valueOf(PicasaSource.getRotation(paramMediaObject));
        Log.w("GalleryProvider", "unsupported column: " + str);
      }
    }
    MatrixCursor localMatrixCursor = new MatrixCursor(paramArrayOfString1);
    localMatrixCursor.addRow(arrayOfObject);
    return localMatrixCursor;
  }

  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException();
  }

  public String getType(Uri paramUri)
  {
    long l = Binder.clearCallingIdentity();
    String str2;
    try
    {
      Path localPath = Path.fromString(paramUri.getPath());
      MediaItem localMediaItem = (MediaItem)this.mDataManager.getMediaObject(localPath);
      if (localMediaItem != null)
      {
        String str1 = localMediaItem.getMimeType();
        str2 = str1;
        return str2;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }

  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    throw new UnsupportedOperationException();
  }

  public boolean onCreate()
  {
    this.mDataManager = ((GalleryApp)getContext().getApplicationContext()).getDataManager();
    return true;
  }

  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      throw new FileNotFoundException("cannot open file for write");
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    Path localPath = Path.fromString(paramUri.getPath());
    MediaObject localMediaObject = this.mDataManager.getMediaObject(localPath);
    if (localMediaObject == null)
      throw new FileNotFoundException(paramUri.toString());
    if (PicasaSource.isPicasaImage(localMediaObject))
    {
      ParcelFileDescriptor localParcelFileDescriptor2 = PicasaSource.openFile(getContext(), localMediaObject, paramString);
      Binder.restoreCallingIdentity(l);
      return localParcelFileDescriptor2;
    }
    if (localMediaObject instanceof MtpImage)
    {
      ParcelFileDescriptor localParcelFileDescriptor1 = openPipeHelper(null, new MtpPipeDataWriter((MtpImage)localMediaObject, null));
      Binder.restoreCallingIdentity(l);
      return localParcelFileDescriptor1;
    }
    throw new FileNotFoundException("unspported type: " + localMediaObject);
  }

  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      Path localPath = Path.fromString(paramUri.getPath());
      MediaObject localMediaObject = this.mDataManager.getMediaObject(localPath);
      if (localMediaObject == null)
      {
        Log.w("GalleryProvider", "cannot find: " + paramUri);
        return null;
      }
      if (PicasaSource.isPicasaImage(localMediaObject))
      {
        Cursor localCursor2 = queryPicasaItem(localMediaObject, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
        return localCursor2;
      }
      if (localMediaObject instanceof MtpImage)
      {
        Cursor localCursor1 = queryMtpItem((MtpImage)localMediaObject, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
        return localCursor1;
      }
      return null;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }

  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    throw new UnsupportedOperationException();
  }

  private final class MtpPipeDataWriter
    implements GalleryProvider.PipeDataWriter<Object>
  {
    private final MtpImage mImage;

    private MtpPipeDataWriter(MtpImage arg2)
    {
      Object localObject;
      this.mImage = localObject;
    }

    // ERROR //
    public void writeDataToPipe(ParcelFileDescriptor paramParcelFileDescriptor, Object paramObject)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_3
      //   2: new 29	android/os/ParcelFileDescriptor$AutoCloseOutputStream
      //   5: dup
      //   6: aload_1
      //   7: invokespecial 32	android/os/ParcelFileDescriptor$AutoCloseOutputStream:<init>	(Landroid/os/ParcelFileDescriptor;)V
      //   10: astore 4
      //   12: aload 4
      //   14: aload_0
      //   15: getfield 20	com/android/gallery3d/provider/GalleryProvider$MtpPipeDataWriter:mImage	Lcom/android/gallery3d/data/MtpImage;
      //   18: invokevirtual 38	com/android/gallery3d/data/MtpImage:getImageData	()[B
      //   21: invokevirtual 44	java/io/FileOutputStream:write	([B)V
      //   24: aload 4
      //   26: invokestatic 50	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   29: return
      //   30: astore 5
      //   32: ldc 52
      //   34: new 54	java/lang/StringBuilder
      //   37: dup
      //   38: invokespecial 55	java/lang/StringBuilder:<init>	()V
      //   41: ldc 57
      //   43: invokevirtual 61	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   46: aload_0
      //   47: getfield 20	com/android/gallery3d/provider/GalleryProvider$MtpPipeDataWriter:mImage	Lcom/android/gallery3d/data/MtpImage;
      //   50: invokevirtual 65	java/lang/Object:toString	()Ljava/lang/String;
      //   53: invokevirtual 61	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   56: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   59: aload 5
      //   61: invokestatic 72	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   64: pop
      //   65: aload_3
      //   66: invokestatic 50	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   69: return
      //   70: astore 6
      //   72: aload_3
      //   73: invokestatic 50	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   76: aload 6
      //   78: athrow
      //   79: astore 6
      //   81: aload 4
      //   83: astore_3
      //   84: goto -12 -> 72
      //   87: astore 5
      //   89: aload 4
      //   91: astore_3
      //   92: goto -60 -> 32
      //
      // Exception table:
      //   from	to	target	type
      //   2	12	30	java/io/IOException
      //   2	12	70	finally
      //   32	65	70	finally
      //   12	24	79	finally
      //   12	24	87	java/io/IOException
    }
  }

  private static abstract interface PipeDataWriter<T>
  {
    public abstract void writeDataToPipe(ParcelFileDescriptor paramParcelFileDescriptor, T paramT);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.provider.GalleryProvider
 * JD-Core Version:    0.5.4
 */