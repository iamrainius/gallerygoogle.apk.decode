package com.google.android.apps.lightcycle.util;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil
{
  private static final String TAG = FileUtil.class.getName();

  public static boolean deleteDirectoryRecursively(File paramFile)
  {
    if ((!paramFile.exists()) || (!paramFile.isDirectory()))
      return false;
    for (File localFile : paramFile.listFiles())
    {
      if (localFile.isDirectory())
        deleteDirectoryRecursively(localFile);
      if (!localFile.delete());
    }
    return paramFile.delete();
  }

  public static boolean deleteFile(String paramString)
  {
    File localFile = new File(paramString);
    if ((localFile.isFile()) && (localFile.exists()))
      return localFile.delete();
    return false;
  }

  public static void storeFile(InputStream paramInputStream, File paramFile)
    throws IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    byte[] arrayOfByte = new byte[4096];
    while (true)
    {
      int i = localBufferedInputStream.read(arrayOfByte);
      if (i == -1)
        break;
      localFileOutputStream.write(arrayOfByte, 0, i);
    }
    localFileOutputStream.close();
    paramInputStream.close();
    Log.d(TAG, "File stored: " + paramFile.getAbsolutePath());
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.FileUtil
 * JD-Core Version:    0.5.4
 */