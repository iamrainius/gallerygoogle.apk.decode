package com.android.gallery3d.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Fingerprint
{
  private static final MessageDigest DIGESTER;
  private static final int FINGERPRINT_BYTE_LENGTH;
  private static final int STREAM_ID_CS_01_LENGTH;
  private final byte[] mMd5Digest;

  static
  {
    try
    {
      DIGESTER = MessageDigest.getInstance("md5");
      FINGERPRINT_BYTE_LENGTH = DIGESTER.getDigestLength();
      STREAM_ID_CS_01_LENGTH = "cs_01_".length() + 2 * FINGERPRINT_BYTE_LENGTH;
      return;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new IllegalStateException(localNoSuchAlgorithmException);
    }
  }

  public Fingerprint(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length != FINGERPRINT_BYTE_LENGTH))
      throw new IllegalArgumentException();
    this.mMd5Digest = paramArrayOfByte;
  }

  private static void appendHexFingerprint(StringBuilder paramStringBuilder, byte[] paramArrayOfByte)
  {
    for (int i = 0; i < FINGERPRINT_BYTE_LENGTH; ++i)
    {
      int j = paramArrayOfByte[i];
      paramStringBuilder.append(Integer.toHexString(0xF & j >> 4));
      paramStringBuilder.append(Integer.toHexString(j & 0xF));
    }
  }

  public static Fingerprint extractFingerprint(List<String> paramList)
  {
    Iterator localIterator = paramList.iterator();
    String str;
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      if (str.startsWith("cs_01_"))
        return fromStreamId(str);
    }
    return null;
  }

  // ERROR //
  public static Fingerprint fromInputStream(java.io.InputStream paramInputStream, long[] paramArrayOfLong)
    throws java.io.IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: lconst_0
    //   3: lstore_3
    //   4: new 100	java/security/DigestInputStream
    //   7: dup
    //   8: aload_0
    //   9: getstatic 25	com/android/gallery3d/common/Fingerprint:DIGESTER	Ljava/security/MessageDigest;
    //   12: invokespecial 103	java/security/DigestInputStream:<init>	(Ljava/io/InputStream;Ljava/security/MessageDigest;)V
    //   15: astore 5
    //   17: sipush 8192
    //   20: newarray byte
    //   22: astore 7
    //   24: aload 5
    //   26: aload 7
    //   28: invokevirtual 107	java/security/DigestInputStream:read	([B)I
    //   31: istore 8
    //   33: iload 8
    //   35: ifge +42 -> 77
    //   38: aload 5
    //   40: ifnull +8 -> 48
    //   43: aload 5
    //   45: invokevirtual 110	java/security/DigestInputStream:close	()V
    //   48: aload_1
    //   49: ifnull +12 -> 61
    //   52: aload_1
    //   53: arraylength
    //   54: ifle +7 -> 61
    //   57: aload_1
    //   58: iconst_0
    //   59: lload_3
    //   60: lastore
    //   61: new 2	com/android/gallery3d/common/Fingerprint
    //   64: dup
    //   65: aload 5
    //   67: invokevirtual 114	java/security/DigestInputStream:getMessageDigest	()Ljava/security/MessageDigest;
    //   70: invokevirtual 118	java/security/MessageDigest:digest	()[B
    //   73: invokespecial 120	com/android/gallery3d/common/Fingerprint:<init>	([B)V
    //   76: areturn
    //   77: lload_3
    //   78: iload 8
    //   80: i2l
    //   81: ladd
    //   82: lstore_3
    //   83: goto -59 -> 24
    //   86: astore 6
    //   88: aload_2
    //   89: ifnull +7 -> 96
    //   92: aload_2
    //   93: invokevirtual 110	java/security/DigestInputStream:close	()V
    //   96: aload 6
    //   98: athrow
    //   99: astore 6
    //   101: aload 5
    //   103: astore_2
    //   104: goto -16 -> 88
    //
    // Exception table:
    //   from	to	target	type
    //   4	17	86	finally
    //   17	24	99	finally
    //   24	33	99	finally
  }

  public static Fingerprint fromStreamId(String paramString)
  {
    if ((paramString == null) || (!paramString.startsWith("cs_01_")) || (paramString.length() != STREAM_ID_CS_01_LENGTH))
      throw new IllegalArgumentException("bad streamId: " + paramString);
    byte[] arrayOfByte = new byte[FINGERPRINT_BYTE_LENGTH];
    int i = 0;
    int j = "cs_01_".length();
    while (j < STREAM_ID_CS_01_LENGTH)
    {
      int k = toDigit(paramString, j) << 4 | toDigit(paramString, j + 1);
      int l = i + 1;
      arrayOfByte[i] = (byte)(k & 0xFF);
      j += 2;
      i = l;
    }
    return new Fingerprint(arrayOfByte);
  }

  private static int toDigit(String paramString, int paramInt)
  {
    int i = Character.digit(paramString.charAt(paramInt), 16);
    if (i < 0)
      throw new IllegalArgumentException("illegal hex digit in " + paramString);
    return i;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject)
      return true;
    if (!paramObject instanceof Fingerprint)
      return false;
    Fingerprint localFingerprint = (Fingerprint)paramObject;
    return Arrays.equals(this.mMd5Digest, localFingerprint.mMd5Digest);
  }

  public boolean equals(byte[] paramArrayOfByte)
  {
    return Arrays.equals(this.mMd5Digest, paramArrayOfByte);
  }

  public byte[] getBytes()
  {
    return this.mMd5Digest;
  }

  public int hashCode()
  {
    return Arrays.hashCode(this.mMd5Digest);
  }

  public String toStreamId()
  {
    StringBuilder localStringBuilder = new StringBuilder("cs_01_");
    appendHexFingerprint(localStringBuilder, this.mMd5Digest);
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.Fingerprint
 * JD-Core Version:    0.5.4
 */