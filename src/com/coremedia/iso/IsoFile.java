package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MovieBox;
import com.googlecode.mp4parser.AbstractContainerBox;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.List;

public class IsoFile extends AbstractContainerBox
  implements Closeable
{
  protected BoxParser boxParser = new PropertyBoxParserImpl(new String[0]);
  ReadableByteChannel byteChannel;

  static
  {
    if (!IsoFile.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public IsoFile()
  {
    super("");
  }

  public IsoFile(ReadableByteChannel paramReadableByteChannel)
    throws IOException
  {
    super("");
    this.byteChannel = paramReadableByteChannel;
    this.boxParser = createBoxParser();
    parse();
  }

  public static String bytesToFourCC(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = { 0, 0, 0, 0 };
    if (paramArrayOfByte != null)
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, Math.min(paramArrayOfByte.length, 4));
    try
    {
      String str = new String(arrayOfByte, "ISO-8859-1");
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new Error("Required character encoding is missing", localUnsupportedEncodingException);
    }
  }

  public static byte[] fourCCtoBytes(String paramString)
  {
    byte[] arrayOfByte = new byte[4];
    if (paramString != null)
      for (int i = 0; i < Math.min(4, paramString.length()); ++i)
        arrayOfByte[i] = (byte)paramString.charAt(i);
    return arrayOfByte;
  }

  private void parse()
    throws IOException
  {
    label45: for (int i = 0; i == 0; i = 1)
      while (true)
        try
        {
          Box localBox = this.boxParser.parseBox(this.byteChannel, this);
          if (localBox == null)
            break label45;
          this.boxes.add(localBox);
        }
        catch (EOFException localEOFException)
        {
          i = 1;
        }
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
  }

  public void close()
    throws IOException
  {
    this.byteChannel.close();
  }

  protected BoxParser createBoxParser()
  {
    return new PropertyBoxParserImpl(new String[0]);
  }

  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
    {
      Box localBox = (Box)localIterator.next();
      if (paramWritableByteChannel instanceof FileChannel)
      {
        long l1 = ((FileChannel)paramWritableByteChannel).position();
        localBox.getBox(paramWritableByteChannel);
        long l2 = ((FileChannel)paramWritableByteChannel).position() - l1;
        if (($assertionsDisabled) || (l2 == localBox.getSize()))
          continue;
        throw new AssertionError();
      }
      localBox.getBox(paramWritableByteChannel);
    }
  }

  public IsoFile getIsoFile()
  {
    return this;
  }

  public MovieBox getMovieBox()
  {
    Iterator localIterator = this.boxes.iterator();
    Box localBox;
    while (localIterator.hasNext())
    {
      localBox = (Box)localIterator.next();
      if (localBox instanceof MovieBox)
        return (MovieBox)localBox;
    }
    return null;
  }

  public long getSize()
  {
    long l = 0L;
    Iterator localIterator = this.boxes.iterator();
    while (localIterator.hasNext())
      l += ((Box)localIterator.next()).getSize();
    return l;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("IsoFile[");
    if (this.boxes == null)
    {
      localStringBuilder.append("unparsed");
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    for (int i = 0; ; ++i)
    {
      if (i < this.boxes.size());
      if (i > 0)
        localStringBuilder.append(";");
      localStringBuilder.append(((Box)this.boxes.get(i)).toString());
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.IsoFile
 * JD-Core Version:    0.5.4
 */