package com.android.gallery3d.filtershow.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ConditionVariable;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;

public class SharedImageProvider extends ContentProvider
{
  public static final Uri CONTENT_URI = Uri.parse("content://com.android.gallery3d.filtershow.provider.SharedImageProvider/image");
  private static ConditionVariable mImageReadyCond = new ConditionVariable(false);
  private final String[] mMimeStreamType = { "image/jpeg" };

  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    return 0;
  }

  public String[] getStreamTypes(Uri paramUri, String paramString)
  {
    return this.mMimeStreamType;
  }

  public String getType(Uri paramUri)
  {
    return "image/jpeg";
  }

  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    if (paramContentValues.containsKey("prepare"))
    {
      if (!paramContentValues.getAsBoolean("prepare").booleanValue())
        break label29;
      mImageReadyCond.close();
    }
    while (true)
    {
      return null;
      label29: mImageReadyCond.open();
    }
  }

  public boolean onCreate()
  {
    return true;
  }

  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    String str = paramUri.getLastPathSegment();
    if (str == null)
      return null;
    mImageReadyCond.block();
    return ParcelFileDescriptor.open(new File(str), 0x0 | 0x10000000);
  }

  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    String str = paramUri.getLastPathSegment();
    if (str == null)
      return null;
    if (paramArrayOfString1 == null)
      paramArrayOfString1 = new String[] { "_id", "_data", "_display_name", "_size" };
    mImageReadyCond.block();
    File localFile = new File(str);
    MatrixCursor localMatrixCursor = new MatrixCursor(paramArrayOfString1);
    Object[] arrayOfObject = new Object[paramArrayOfString1.length];
    int i = 0;
    if (i < paramArrayOfString1.length)
    {
      if (paramArrayOfString1[i].equalsIgnoreCase("_id"))
        label79: arrayOfObject[i] = Integer.valueOf(0);
      while (true)
      {
        ++i;
        break label79:
        if (paramArrayOfString1[i].equalsIgnoreCase("_data"))
          arrayOfObject[i] = paramUri;
        if (paramArrayOfString1[i].equalsIgnoreCase("_display_name"))
          arrayOfObject[i] = localFile.getName();
        if (!paramArrayOfString1[i].equalsIgnoreCase("_size"))
          continue;
        arrayOfObject[i] = Long.valueOf(localFile.length());
      }
    }
    localMatrixCursor.addRow(arrayOfObject);
    return localMatrixCursor;
  }

  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    return 0;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.provider.SharedImageProvider
 * JD-Core Version:    0.5.4
 */