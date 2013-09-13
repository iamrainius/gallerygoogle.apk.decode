package com.adobe.xmp.impl;

import java.io.UnsupportedEncodingException;

public class Latin1Converter
{
  public static ByteBuffer convert(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte;
    int i;
    int j;
    ByteBuffer localByteBuffer;
    int k;
    int l;
    label44: int i2;
    if ("UTF-8".equals(paramByteBuffer.getEncoding()))
    {
      arrayOfByte = new byte[8];
      i = 0;
      j = 0;
      localByteBuffer = new ByteBuffer(4 * paramByteBuffer.length() / 3);
      k = 0;
      l = 0;
      if (l < paramByteBuffer.length())
      {
        i2 = paramByteBuffer.charAt(l);
        switch (k)
        {
        default:
          if (i2 >= 127)
            break label100;
          localByteBuffer.append((byte)i2);
        case 11:
        }
      }
    }
    while (true)
    {
      ++l;
      break label44:
      if (i2 >= 192)
      {
        label100: j = -1;
        int i4 = i2;
        while ((j < 8) && ((i4 & 0x80) == 128))
        {
          ++j;
          i4 <<= 1;
        }
        int i5 = i + 1;
        arrayOfByte[i] = (byte)i2;
        k = 11;
        i = i5;
      }
      localByteBuffer.append(convertToUTF8((byte)i2));
      continue;
      int i3;
      if ((j > 0) && ((i2 & 0xC0) == 128))
      {
        i3 = i + 1;
        arrayOfByte[i] = (byte)i2;
        if (--j != 0)
          break label293;
        localByteBuffer.append(arrayOfByte, 0, i3);
        i = 0;
        k = 0;
      }
      localByteBuffer.append(convertToUTF8(arrayOfByte[0]));
      l -= i;
      i = 0;
      k = 0;
      continue;
      if (k == 11)
      {
        for (int i1 = 0; ; ++i1)
        {
          if (i1 >= i)
            break label291;
          localByteBuffer.append(convertToUTF8(arrayOfByte[i1]));
        }
        localByteBuffer = paramByteBuffer;
      }
      label291: return localByteBuffer;
      label293: i = i3;
    }
  }

  private static byte[] convertToUTF8(byte paramByte)
  {
    // Byte code:
    //   0: iload_0
    //   1: sipush 255
    //   4: iand
    //   5: istore_1
    //   6: iload_1
    //   7: sipush 128
    //   10: if_icmplt +72 -> 82
    //   13: iload_1
    //   14: sipush 129
    //   17: if_icmpeq +31 -> 48
    //   20: iload_1
    //   21: sipush 141
    //   24: if_icmpeq +24 -> 48
    //   27: iload_1
    //   28: sipush 143
    //   31: if_icmpeq +17 -> 48
    //   34: iload_1
    //   35: sipush 144
    //   38: if_icmpeq +10 -> 48
    //   41: iload_1
    //   42: sipush 157
    //   45: if_icmpne +12 -> 57
    //   48: iconst_1
    //   49: newarray byte
    //   51: dup
    //   52: iconst_0
    //   53: bipush 32
    //   55: bastore
    //   56: areturn
    //   57: new 20	java/lang/String
    //   60: dup
    //   61: iconst_1
    //   62: newarray byte
    //   64: dup
    //   65: iconst_0
    //   66: iload_0
    //   67: bastore
    //   68: ldc 53
    //   70: invokespecial 56	java/lang/String:<init>	([BLjava/lang/String;)V
    //   73: ldc 12
    //   75: invokevirtual 60	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   78: astore_3
    //   79: aload_3
    //   80: areturn
    //   81: astore_2
    //   82: iconst_1
    //   83: newarray byte
    //   85: dup
    //   86: iconst_0
    //   87: iload_0
    //   88: bastore
    //   89: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   48	57	81	java/io/UnsupportedEncodingException
    //   57	79	81	java/io/UnsupportedEncodingException
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.Latin1Converter
 * JD-Core Version:    0.5.4
 */