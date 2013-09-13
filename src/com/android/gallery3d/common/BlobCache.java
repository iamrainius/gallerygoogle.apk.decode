package com.android.gallery3d.common;

import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;
import java.util.zip.Adler32;

public class BlobCache
  implements Closeable
{
  private int mActiveBytes;
  private RandomAccessFile mActiveDataFile;
  private int mActiveEntries;
  private int mActiveHashStart;
  private int mActiveRegion;
  private Adler32 mAdler32 = new Adler32();
  private byte[] mBlobHeader = new byte[20];
  private RandomAccessFile mDataFile0;
  private RandomAccessFile mDataFile1;
  private int mFileOffset;
  private RandomAccessFile mInactiveDataFile;
  private int mInactiveHashStart;
  private MappedByteBuffer mIndexBuffer;
  private FileChannel mIndexChannel;
  private RandomAccessFile mIndexFile;
  private byte[] mIndexHeader = new byte[32];
  private LookupRequest mLookupRequest = new LookupRequest();
  private int mMaxBytes;
  private int mMaxEntries;
  private int mSlotOffset;
  private int mVersion;

  public BlobCache(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    throws IOException
  {
    this.mIndexFile = new RandomAccessFile(paramString + ".idx", "rw");
    this.mDataFile0 = new RandomAccessFile(paramString + ".0", "rw");
    this.mDataFile1 = new RandomAccessFile(paramString + ".1", "rw");
    this.mVersion = paramInt3;
    if ((!paramBoolean) && (loadIndex()));
    do
    {
      return;
      resetCache(paramInt1, paramInt2);
    }
    while (loadIndex());
    closeAll();
    throw new IOException("unable to load index");
  }

  private void clearHash(int paramInt)
  {
    byte[] arrayOfByte = new byte[1024];
    this.mIndexBuffer.position(paramInt);
    int i = 12 * this.mMaxEntries;
    while (i > 0)
    {
      int j = Math.min(i, 1024);
      this.mIndexBuffer.put(arrayOfByte, 0, j);
      i -= j;
    }
  }

  private void closeAll()
  {
    closeSilently(this.mIndexChannel);
    closeSilently(this.mIndexFile);
    closeSilently(this.mDataFile0);
    closeSilently(this.mDataFile1);
  }

  static void closeSilently(Closeable paramCloseable)
  {
    if (paramCloseable == null)
      return;
    try
    {
      paramCloseable.close();
      return;
    }
    catch (Throwable localThrowable)
    {
    }
  }

  private static void deleteFileSilently(String paramString)
  {
    try
    {
      new File(paramString).delete();
      return;
    }
    catch (Throwable localThrowable)
    {
    }
  }

  public static void deleteFiles(String paramString)
  {
    deleteFileSilently(paramString + ".idx");
    deleteFileSilently(paramString + ".0");
    deleteFileSilently(paramString + ".1");
  }

  private void flipRegion()
    throws IOException
  {
    this.mActiveRegion = (1 - this.mActiveRegion);
    this.mActiveEntries = 0;
    this.mActiveBytes = 4;
    writeInt(this.mIndexHeader, 12, this.mActiveRegion);
    writeInt(this.mIndexHeader, 16, this.mActiveEntries);
    writeInt(this.mIndexHeader, 20, this.mActiveBytes);
    updateIndexHeader();
    setActiveVariables();
    clearHash(this.mActiveHashStart);
    syncIndex();
  }

  private boolean getBlob(RandomAccessFile paramRandomAccessFile, int paramInt, LookupRequest paramLookupRequest)
    throws IOException
  {
    byte[] arrayOfByte1 = this.mBlobHeader;
    long l1 = paramRandomAccessFile.getFilePointer();
    long l2 = paramInt;
    try
    {
      paramRandomAccessFile.seek(l2);
      if (paramRandomAccessFile.read(arrayOfByte1) != 20)
      {
        Log.w("BlobCache", "cannot read blob header");
        return false;
      }
      long l3 = readLong(arrayOfByte1, 0);
      if (l3 == 0L)
        return false;
      if (l3 != paramLookupRequest.key)
      {
        Log.w("BlobCache", "blob key does not match: " + l3);
        return false;
      }
      int i = readInt(arrayOfByte1, 8);
      int j = readInt(arrayOfByte1, 12);
      if (j != paramInt)
      {
        Log.w("BlobCache", "blob offset does not match: " + j);
        return false;
      }
      int k = readInt(arrayOfByte1, 16);
      if ((k < 0) || (k > -20 + (this.mMaxBytes - paramInt)))
      {
        Log.w("BlobCache", "invalid blob length: " + k);
        return false;
      }
      if ((paramLookupRequest.buffer == null) || (paramLookupRequest.buffer.length < k))
        paramLookupRequest.buffer = new byte[k];
      byte[] arrayOfByte2 = paramLookupRequest.buffer;
      paramLookupRequest.length = k;
      if (paramRandomAccessFile.read(arrayOfByte2, 0, k) != k)
      {
        Log.w("BlobCache", "cannot read blob data");
        return false;
      }
      if (checkSum(arrayOfByte2, 0, k) != i)
      {
        Log.w("BlobCache", "blob checksum does not match: " + i);
        return false;
      }
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e("BlobCache", "getBlob failed.", localThrowable);
      return false;
    }
    finally
    {
      paramRandomAccessFile.seek(l1);
    }
  }

  private void insertInternal(long paramLong, byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = this.mBlobHeader;
    int i = checkSum(paramArrayOfByte);
    writeLong(arrayOfByte, 0, paramLong);
    writeInt(arrayOfByte, 8, i);
    writeInt(arrayOfByte, 12, this.mActiveBytes);
    writeInt(arrayOfByte, 16, paramInt);
    this.mActiveDataFile.write(arrayOfByte);
    this.mActiveDataFile.write(paramArrayOfByte, 0, paramInt);
    this.mIndexBuffer.putLong(this.mSlotOffset, paramLong);
    this.mIndexBuffer.putInt(8 + this.mSlotOffset, this.mActiveBytes);
    this.mActiveBytes += paramInt + 20;
    writeInt(this.mIndexHeader, 20, this.mActiveBytes);
  }

  private boolean loadIndex()
  {
    try
    {
      this.mIndexFile.seek(0L);
      this.mDataFile0.seek(0L);
      this.mDataFile1.seek(0L);
      byte[] arrayOfByte1 = this.mIndexHeader;
      if (this.mIndexFile.read(arrayOfByte1) != 32)
      {
        Log.w("BlobCache", "cannot read header");
        return false;
      }
      if (readInt(arrayOfByte1, 0) != -1289277392)
      {
        Log.w("BlobCache", "cannot read header magic");
        return false;
      }
      if (readInt(arrayOfByte1, 24) != this.mVersion)
      {
        Log.w("BlobCache", "version mismatch");
        return false;
      }
      this.mMaxEntries = readInt(arrayOfByte1, 4);
      this.mMaxBytes = readInt(arrayOfByte1, 8);
      this.mActiveRegion = readInt(arrayOfByte1, 12);
      this.mActiveEntries = readInt(arrayOfByte1, 16);
      this.mActiveBytes = readInt(arrayOfByte1, 20);
      int i = readInt(arrayOfByte1, 28);
      if (checkSum(arrayOfByte1, 0, 28) != i)
      {
        Log.w("BlobCache", "header checksum does not match");
        return false;
      }
      if (this.mMaxEntries <= 0)
      {
        Log.w("BlobCache", "invalid max entries");
        return false;
      }
      if (this.mMaxBytes <= 0)
      {
        Log.w("BlobCache", "invalid max bytes");
        return false;
      }
      if ((this.mActiveRegion != 0) && (this.mActiveRegion != 1))
      {
        Log.w("BlobCache", "invalid active region");
        return false;
      }
      if ((this.mActiveEntries < 0) || (this.mActiveEntries > this.mMaxEntries))
      {
        Log.w("BlobCache", "invalid active entries");
        return false;
      }
      if ((this.mActiveBytes < 4) || (this.mActiveBytes > this.mMaxBytes))
      {
        Log.w("BlobCache", "invalid active bytes");
        return false;
      }
      if (this.mIndexFile.length() != 32 + 2 * (12 * this.mMaxEntries))
      {
        Log.w("BlobCache", "invalid index file length");
        return false;
      }
      byte[] arrayOfByte2 = new byte[4];
      if (this.mDataFile0.read(arrayOfByte2) != 4)
      {
        Log.w("BlobCache", "cannot read data file magic");
        return false;
      }
      if (readInt(arrayOfByte2, 0) != -1121680112)
      {
        Log.w("BlobCache", "invalid data file magic");
        return false;
      }
      if (this.mDataFile1.read(arrayOfByte2) != 4)
      {
        Log.w("BlobCache", "cannot read data file magic");
        return false;
      }
      if (readInt(arrayOfByte2, 0) != -1121680112)
      {
        Log.w("BlobCache", "invalid data file magic");
        return false;
      }
      this.mIndexChannel = this.mIndexFile.getChannel();
      this.mIndexBuffer = this.mIndexChannel.map(FileChannel.MapMode.READ_WRITE, 0L, this.mIndexFile.length());
      this.mIndexBuffer.order(ByteOrder.LITTLE_ENDIAN);
      setActiveVariables();
      return true;
    }
    catch (IOException localIOException)
    {
      Log.e("BlobCache", "loadIndex failed.", localIOException);
    }
    return false;
  }

  private boolean lookupInternal(long paramLong, int paramInt)
  {
    int i = (int)(paramLong % this.mMaxEntries);
    if (i < 0)
      i += this.mMaxEntries;
    int j = i;
    while (true)
    {
      int k = paramInt + i * 12;
      long l = this.mIndexBuffer.getLong(k);
      int i1 = this.mIndexBuffer.getInt(k + 8);
      if (i1 == 0)
      {
        this.mSlotOffset = k;
        return false;
      }
      if (l == paramLong)
      {
        this.mSlotOffset = k;
        this.mFileOffset = i1;
        return true;
      }
      if (++i >= this.mMaxEntries)
        i = 0;
      if (i != j)
        continue;
      Log.w("BlobCache", "corrupted index: clear the slot.");
      this.mIndexBuffer.putInt(8 + (paramInt + i * 12), 0);
    }
  }

  static int readInt(byte[] paramArrayOfByte, int paramInt)
  {
    return 0xFF & paramArrayOfByte[paramInt] | (0xFF & paramArrayOfByte[(paramInt + 1)]) << 8 | (0xFF & paramArrayOfByte[(paramInt + 2)]) << 16 | (0xFF & paramArrayOfByte[(paramInt + 3)]) << 24;
  }

  static long readLong(byte[] paramArrayOfByte, int paramInt)
  {
    long l = 0xFF & paramArrayOfByte[(paramInt + 7)];
    for (int i = 6; i >= 0; --i)
      l = l << 8 | 0xFF & paramArrayOfByte[(paramInt + i)];
    return l;
  }

  private void resetCache(int paramInt1, int paramInt2)
    throws IOException
  {
    this.mIndexFile.setLength(0L);
    this.mIndexFile.setLength(32 + 2 * (paramInt1 * 12));
    this.mIndexFile.seek(0L);
    byte[] arrayOfByte = this.mIndexHeader;
    writeInt(arrayOfByte, 0, -1289277392);
    writeInt(arrayOfByte, 4, paramInt1);
    writeInt(arrayOfByte, 8, paramInt2);
    writeInt(arrayOfByte, 12, 0);
    writeInt(arrayOfByte, 16, 0);
    writeInt(arrayOfByte, 20, 4);
    writeInt(arrayOfByte, 24, this.mVersion);
    writeInt(arrayOfByte, 28, checkSum(arrayOfByte, 0, 28));
    this.mIndexFile.write(arrayOfByte);
    this.mDataFile0.setLength(0L);
    this.mDataFile1.setLength(0L);
    this.mDataFile0.seek(0L);
    this.mDataFile1.seek(0L);
    writeInt(arrayOfByte, 0, -1121680112);
    this.mDataFile0.write(arrayOfByte, 0, 4);
    this.mDataFile1.write(arrayOfByte, 0, 4);
  }

  private void setActiveVariables()
    throws IOException
  {
    RandomAccessFile localRandomAccessFile1;
    if (this.mActiveRegion == 0)
    {
      localRandomAccessFile1 = this.mDataFile0;
      label12: this.mActiveDataFile = localRandomAccessFile1;
      if (this.mActiveRegion != 1)
        break label103;
    }
    for (RandomAccessFile localRandomAccessFile2 = this.mDataFile0; ; localRandomAccessFile2 = this.mDataFile1)
    {
      this.mInactiveDataFile = localRandomAccessFile2;
      this.mActiveDataFile.setLength(this.mActiveBytes);
      this.mActiveDataFile.seek(this.mActiveBytes);
      this.mActiveHashStart = 32;
      this.mInactiveHashStart = 32;
      if (this.mActiveRegion != 0)
        break;
      this.mInactiveHashStart += 12 * this.mMaxEntries;
      return;
      localRandomAccessFile1 = this.mDataFile1;
      label103: break label12:
    }
    this.mActiveHashStart += 12 * this.mMaxEntries;
  }

  private void updateIndexHeader()
  {
    writeInt(this.mIndexHeader, 28, checkSum(this.mIndexHeader, 0, 28));
    this.mIndexBuffer.position(0);
    this.mIndexBuffer.put(this.mIndexHeader);
  }

  static void writeInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < 4; ++i)
    {
      paramArrayOfByte[(paramInt1 + i)] = (byte)(paramInt2 & 0xFF);
      paramInt2 >>= 8;
    }
  }

  static void writeLong(byte[] paramArrayOfByte, int paramInt, long paramLong)
  {
    for (int i = 0; i < 8; ++i)
    {
      paramArrayOfByte[(paramInt + i)] = (byte)(int)(0xFF & paramLong);
      paramLong >>= 8;
    }
  }

  int checkSum(byte[] paramArrayOfByte)
  {
    this.mAdler32.reset();
    this.mAdler32.update(paramArrayOfByte);
    return (int)this.mAdler32.getValue();
  }

  int checkSum(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.mAdler32.reset();
    this.mAdler32.update(paramArrayOfByte, paramInt1, paramInt2);
    return (int)this.mAdler32.getValue();
  }

  public void clearEntry(long paramLong)
    throws IOException
  {
    if (!lookupInternal(paramLong, this.mActiveHashStart))
      return;
    byte[] arrayOfByte = this.mBlobHeader;
    Arrays.fill(arrayOfByte, 0);
    this.mActiveDataFile.seek(this.mFileOffset);
    this.mActiveDataFile.write(arrayOfByte);
  }

  public void close()
  {
    syncAll();
    closeAll();
  }

  public void insert(long paramLong, byte[] paramArrayOfByte)
    throws IOException
  {
    if (24 + paramArrayOfByte.length > this.mMaxBytes)
      throw new RuntimeException("blob is too large!");
    if ((20 + this.mActiveBytes + paramArrayOfByte.length > this.mMaxBytes) || (2 * this.mActiveEntries >= this.mMaxEntries))
      flipRegion();
    if (!lookupInternal(paramLong, this.mActiveHashStart))
    {
      this.mActiveEntries = (1 + this.mActiveEntries);
      writeInt(this.mIndexHeader, 16, this.mActiveEntries);
    }
    insertInternal(paramLong, paramArrayOfByte, paramArrayOfByte.length);
    updateIndexHeader();
  }

  public boolean lookup(LookupRequest paramLookupRequest)
    throws IOException
  {
    if ((lookupInternal(paramLookupRequest.key, this.mActiveHashStart)) && (getBlob(this.mActiveDataFile, this.mFileOffset, paramLookupRequest)));
    int i;
    do
    {
      return true;
      i = this.mSlotOffset;
      if ((!lookupInternal(paramLookupRequest.key, this.mInactiveHashStart)) || (!getBlob(this.mInactiveDataFile, this.mFileOffset, paramLookupRequest)))
        break label163;
    }
    while ((20 + this.mActiveBytes + paramLookupRequest.length > this.mMaxBytes) || (2 * this.mActiveEntries >= this.mMaxEntries));
    this.mSlotOffset = i;
    try
    {
      insertInternal(paramLookupRequest.key, paramLookupRequest.buffer, paramLookupRequest.length);
      this.mActiveEntries = (1 + this.mActiveEntries);
      writeInt(this.mIndexHeader, 16, this.mActiveEntries);
      updateIndexHeader();
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e("BlobCache", "cannot copy over");
      return true;
    }
    label163: return false;
  }

  public byte[] lookup(long paramLong)
    throws IOException
  {
    this.mLookupRequest.key = paramLong;
    this.mLookupRequest.buffer = null;
    boolean bool = lookup(this.mLookupRequest);
    byte[] arrayOfByte = null;
    if (bool)
      arrayOfByte = this.mLookupRequest.buffer;
    return arrayOfByte;
  }

  public void syncAll()
  {
    syncIndex();
    try
    {
      this.mDataFile0.getFD().sync();
    }
    catch (Throwable localThrowable1)
    {
      try
      {
        this.mDataFile1.getFD().sync();
        return;
        localThrowable1 = localThrowable1;
        Log.w("BlobCache", "sync data file 0 failed", localThrowable1);
      }
      catch (Throwable localThrowable2)
      {
        Log.w("BlobCache", "sync data file 1 failed", localThrowable2);
      }
    }
  }

  public void syncIndex()
  {
    try
    {
      this.mIndexBuffer.force();
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("BlobCache", "sync index failed", localThrowable);
    }
  }

  public static class LookupRequest
  {
    public byte[] buffer;
    public long key;
    public int length;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.BlobCache
 * JD-Core Version:    0.5.4
 */