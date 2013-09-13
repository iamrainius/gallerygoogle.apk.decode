package com.google.android.apps.lightcycle.storage;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.apps.lightcycle.util.Callback;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LocalZippableSession
  implements ZippableSession
{
  private static final String TAG = LocalZippableSession.class.getSimpleName();
  private final LocalSessionEntry sessionEntry;

  public LocalZippableSession(LocalSessionEntry paramLocalSessionEntry)
  {
    this.sessionEntry = paramLocalSessionEntry;
  }

  private static boolean addDirectory(String paramString1, String paramString2, File paramFile, ZipOutputStream paramZipOutputStream)
  {
    if (!paramFile.isDirectory())
      return false;
    for (File localFile : paramFile.listFiles())
    {
      if (localFile.isDirectory())
        addDirectory(paramString1, paramString2, localFile, paramZipOutputStream);
      String str1 = localFile.getAbsolutePath();
      if (!str1.startsWith(paramString1))
      {
        Log.e(TAG, "Something went wrong... entry not part of root: '" + paramString1 + "' vs '" + str1 + "'.");
        return false;
      }
      String str2 = str1.substring(paramString1.length());
      if (!putZipEntry(paramString2 + File.separatorChar + str2, localFile, paramZipOutputStream));
    }
    return true;
  }

  private static boolean putZipEntry(String paramString, File paramFile, ZipOutputStream paramZipOutputStream)
  {
    ZipEntry localZipEntry = new ZipEntry(paramString);
    try
    {
      paramZipOutputStream.putNextEntry(localZipEntry);
      paramZipOutputStream.setLevel(0);
      writeZosEntry(paramFile, paramZipOutputStream);
      paramZipOutputStream.closeEntry();
      return true;
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, localIOException.getMessage(), localIOException);
    }
    return false;
  }

  private static void writeZosEntry(File paramFile, ZipOutputStream paramZipOutputStream)
    throws IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(paramFile));
    byte[] arrayOfByte = new byte[4096];
    while (true)
    {
      int i = localBufferedInputStream.read(arrayOfByte);
      if (i <= 0)
        break;
      paramZipOutputStream.write(arrayOfByte, 0, i);
    }
    localBufferedInputStream.close();
  }

  public void saveAs(File paramFile, Callback<Boolean> paramCallback)
  {
    Log.d(TAG, "Saving temporary zip file to: " + paramFile.getAbsolutePath());
    new AsyncTask(paramFile, paramCallback)
    {
      // ERROR //
      protected Void doInBackground(Void[] paramArrayOfVoid)
      {
        // Byte code:
        //   0: new 40	java/util/zip/ZipOutputStream
        //   3: dup
        //   4: new 42	java/io/BufferedOutputStream
        //   7: dup
        //   8: new 44	java/io/FileOutputStream
        //   11: dup
        //   12: aload_0
        //   13: getfield 22	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:val$file	Ljava/io/File;
        //   16: invokespecial 47	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
        //   19: invokespecial 50	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
        //   22: invokespecial 51	java/util/zip/ZipOutputStream:<init>	(Ljava/io/OutputStream;)V
        //   25: astore_2
        //   26: aload_0
        //   27: getfield 20	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:this$0	Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;
        //   30: invokestatic 55	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$100	(Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;)Lcom/google/android/apps/lightcycle/storage/LocalSessionEntry;
        //   33: getfield 61	com/google/android/apps/lightcycle/storage/LocalSessionEntry:thumbnailExists	Z
        //   36: ifeq +36 -> 72
        //   39: new 63	java/io/File
        //   42: dup
        //   43: aload_0
        //   44: getfield 20	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:this$0	Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;
        //   47: invokestatic 55	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$100	(Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;)Lcom/google/android/apps/lightcycle/storage/LocalSessionEntry;
        //   50: getfield 67	com/google/android/apps/lightcycle/storage/LocalSessionEntry:thumbnailFile	Ljava/lang/String;
        //   53: invokespecial 70	java/io/File:<init>	(Ljava/lang/String;)V
        //   56: astore_3
        //   57: aload_3
        //   58: invokevirtual 74	java/io/File:isFile	()Z
        //   61: ifeq +11 -> 72
        //   64: ldc 76
        //   66: aload_3
        //   67: aload_2
        //   68: invokestatic 80	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$200	(Ljava/lang/String;Ljava/io/File;Ljava/util/zip/ZipOutputStream;)Z
        //   71: pop
        //   72: aload_0
        //   73: getfield 20	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:this$0	Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;
        //   76: invokestatic 55	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$100	(Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;)Lcom/google/android/apps/lightcycle/storage/LocalSessionEntry;
        //   79: getfield 83	com/google/android/apps/lightcycle/storage/LocalSessionEntry:stitchedExists	Z
        //   82: ifeq +42 -> 124
        //   85: new 63	java/io/File
        //   88: dup
        //   89: aload_0
        //   90: getfield 20	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:this$0	Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;
        //   93: invokestatic 55	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$100	(Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;)Lcom/google/android/apps/lightcycle/storage/LocalSessionEntry;
        //   96: getfield 86	com/google/android/apps/lightcycle/storage/LocalSessionEntry:stitchedFile	Ljava/lang/String;
        //   99: invokespecial 70	java/io/File:<init>	(Ljava/lang/String;)V
        //   102: astore 4
        //   104: aload 4
        //   106: invokevirtual 74	java/io/File:isFile	()Z
        //   109: ifeq +15 -> 124
        //   112: aload 4
        //   114: invokevirtual 90	java/io/File:getName	()Ljava/lang/String;
        //   117: aload 4
        //   119: aload_2
        //   120: invokestatic 80	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$200	(Ljava/lang/String;Ljava/io/File;Ljava/util/zip/ZipOutputStream;)Z
        //   123: pop
        //   124: aload_0
        //   125: getfield 20	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:this$0	Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;
        //   128: invokestatic 55	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$100	(Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;)Lcom/google/android/apps/lightcycle/storage/LocalSessionEntry;
        //   131: getfield 93	com/google/android/apps/lightcycle/storage/LocalSessionEntry:captureDirectory	Ljava/lang/String;
        //   134: ifnull +63 -> 197
        //   137: new 63	java/io/File
        //   140: dup
        //   141: aload_0
        //   142: getfield 20	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:this$0	Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;
        //   145: invokestatic 55	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$100	(Lcom/google/android/apps/lightcycle/storage/LocalZippableSession;)Lcom/google/android/apps/lightcycle/storage/LocalSessionEntry;
        //   148: getfield 93	com/google/android/apps/lightcycle/storage/LocalSessionEntry:captureDirectory	Ljava/lang/String;
        //   151: invokespecial 70	java/io/File:<init>	(Ljava/lang/String;)V
        //   154: astore 5
        //   156: aload 5
        //   158: invokevirtual 96	java/io/File:isDirectory	()Z
        //   161: ifeq +36 -> 197
        //   164: new 98	java/lang/StringBuilder
        //   167: dup
        //   168: invokespecial 99	java/lang/StringBuilder:<init>	()V
        //   171: aload 5
        //   173: invokevirtual 102	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   176: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   179: getstatic 110	java/io/File:separatorChar	C
        //   182: invokevirtual 113	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
        //   185: invokevirtual 116	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   188: ldc 118
        //   190: aload 5
        //   192: aload_2
        //   193: invokestatic 122	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$300	(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;Ljava/util/zip/ZipOutputStream;)Z
        //   196: pop
        //   197: aload_2
        //   198: invokevirtual 125	java/util/zip/ZipOutputStream:close	()V
        //   201: invokestatic 128	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$000	()Ljava/lang/String;
        //   204: ldc 130
        //   206: invokestatic 136	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
        //   209: pop
        //   210: aload_0
        //   211: getfield 24	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:val$doneCallback	Lcom/google/android/apps/lightcycle/util/Callback;
        //   214: iconst_1
        //   215: invokestatic 142	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
        //   218: invokeinterface 148 2 0
        //   223: aconst_null
        //   224: areturn
        //   225: astore 11
        //   227: invokestatic 128	com/google/android/apps/lightcycle/storage/LocalZippableSession:access$000	()Ljava/lang/String;
        //   230: aload 11
        //   232: invokevirtual 151	java/io/FileNotFoundException:getMessage	()Ljava/lang/String;
        //   235: aload 11
        //   237: invokestatic 155	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   240: pop
        //   241: aload_0
        //   242: getfield 24	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:val$doneCallback	Lcom/google/android/apps/lightcycle/util/Callback;
        //   245: iconst_0
        //   246: invokestatic 142	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
        //   249: invokeinterface 148 2 0
        //   254: aconst_null
        //   255: areturn
        //   256: astore 6
        //   258: aload_0
        //   259: getfield 24	com/google/android/apps/lightcycle/storage/LocalZippableSession$1:val$doneCallback	Lcom/google/android/apps/lightcycle/util/Callback;
        //   262: iconst_0
        //   263: invokestatic 142	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
        //   266: invokeinterface 148 2 0
        //   271: aconst_null
        //   272: areturn
        //
        // Exception table:
        //   from	to	target	type
        //   0	26	225	java/io/FileNotFoundException
        //   197	201	256	java/io/IOException
      }
    }
    .execute(new Void[0]);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.storage.LocalZippableSession
 * JD-Core Version:    0.5.4
 */