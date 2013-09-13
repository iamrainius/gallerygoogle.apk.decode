package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.List<Lcom.googlecode.mp4parser.boxes.piff.PlayReadyHeader.PlayReadyRecord;>;

public class PlayReadyHeader extends ProtectionSpecificHeader
{
  private long length;
  private List<PlayReadyRecord> records;

  public ByteBuffer getData()
  {
    int i = 6;
    Iterator localIterator1 = this.records.iterator();
    while (localIterator1.hasNext())
    {
      PlayReadyRecord localPlayReadyRecord2 = (PlayReadyRecord)localIterator1.next();
      i = i + 4 + localPlayReadyRecord2.getValue().rewind().limit();
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocate(i);
    IsoTypeWriter.writeUInt32BE(localByteBuffer, i);
    IsoTypeWriter.writeUInt16BE(localByteBuffer, this.records.size());
    Iterator localIterator2 = this.records.iterator();
    while (localIterator2.hasNext())
    {
      PlayReadyRecord localPlayReadyRecord1 = (PlayReadyRecord)localIterator2.next();
      IsoTypeWriter.writeUInt16BE(localByteBuffer, localPlayReadyRecord1.type);
      IsoTypeWriter.writeUInt16BE(localByteBuffer, localPlayReadyRecord1.getValue().limit());
      localByteBuffer.put(localPlayReadyRecord1.getValue());
    }
    return localByteBuffer;
  }

  public void parse(ByteBuffer paramByteBuffer)
  {
    this.length = IsoTypeReader.readUInt32BE(paramByteBuffer);
    this.records = PlayReadyRecord.createFor(paramByteBuffer, IsoTypeReader.readUInt16BE(paramByteBuffer));
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PlayReadyHeader");
    localStringBuilder.append("{length=").append(this.length);
    localStringBuilder.append(", recordCount=").append(this.records.size());
    localStringBuilder.append(", records=").append(this.records);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }

  public static abstract class PlayReadyRecord
  {
    int type;

    public PlayReadyRecord(int paramInt)
    {
      this.type = paramInt;
    }

    public static List<PlayReadyRecord> createFor(ByteBuffer paramByteBuffer, int paramInt)
    {
      ArrayList localArrayList = new ArrayList(paramInt);
      int i = 0;
      if (i < paramInt)
      {
        label11: int j = IsoTypeReader.readUInt16BE(paramByteBuffer);
        int k = IsoTypeReader.readUInt16BE(paramByteBuffer);
        switch (j)
        {
        default:
        case 1:
        case 2:
        case 3:
        }
        for (Object localObject = new DefaulPlayReadyRecord(j); ; localObject = new EmeddedLicenseStore())
          while (true)
          {
            ((PlayReadyRecord)localObject).parse((ByteBuffer)paramByteBuffer.slice().limit(k));
            paramByteBuffer.position(k + paramByteBuffer.position());
            localArrayList.add(localObject);
            ++i;
            break label11:
            localObject = new RMHeader();
            continue;
            localObject = new DefaulPlayReadyRecord(2);
          }
      }
      return (List<PlayReadyRecord>)localArrayList;
    }

    public abstract ByteBuffer getValue();

    public abstract void parse(ByteBuffer paramByteBuffer);

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("PlayReadyRecord");
      localStringBuilder.append("{type=").append(this.type);
      localStringBuilder.append(", length=").append(getValue().limit());
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }

    public static class DefaulPlayReadyRecord extends PlayReadyHeader.PlayReadyRecord
    {
      ByteBuffer value;

      public DefaulPlayReadyRecord(int paramInt)
      {
        super(paramInt);
      }

      public ByteBuffer getValue()
      {
        return this.value;
      }

      public void parse(ByteBuffer paramByteBuffer)
      {
        this.value = paramByteBuffer.duplicate();
      }
    }

    public static class EmeddedLicenseStore extends PlayReadyHeader.PlayReadyRecord
    {
      ByteBuffer value;

      public EmeddedLicenseStore()
      {
        super(3);
      }

      public ByteBuffer getValue()
      {
        return this.value;
      }

      public void parse(ByteBuffer paramByteBuffer)
      {
        this.value = paramByteBuffer.duplicate();
      }

      public String toString()
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("EmeddedLicenseStore");
        localStringBuilder.append("{length=").append(getValue().limit());
        localStringBuilder.append('}');
        return localStringBuilder.toString();
      }
    }

    public static class RMHeader extends PlayReadyHeader.PlayReadyRecord
    {
      String header;

      public RMHeader()
      {
        super(1);
      }

      public ByteBuffer getValue()
      {
        try
        {
          byte[] arrayOfByte = this.header.getBytes("UTF-16LE");
          return ByteBuffer.wrap(arrayOfByte);
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          throw new RuntimeException(localUnsupportedEncodingException);
        }
      }

      public void parse(ByteBuffer paramByteBuffer)
      {
        try
        {
          byte[] arrayOfByte = new byte[paramByteBuffer.slice().limit()];
          paramByteBuffer.get(arrayOfByte);
          this.header = new String(arrayOfByte, "UTF-16LE");
          return;
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          throw new RuntimeException(localUnsupportedEncodingException);
        }
      }

      public String toString()
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("RMHeader");
        localStringBuilder.append("{length=").append(getValue().limit());
        localStringBuilder.append(", header='").append(this.header).append('\'');
        localStringBuilder.append('}');
        return localStringBuilder.toString();
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.piff.PlayReadyHeader
 * JD-Core Version:    0.5.4
 */