package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SampleGroupDescriptionBox extends AbstractFullBox
{
  private int defaultLength;
  private int descriptionLength;
  private List<GroupEntry> groupEntries = new LinkedList();
  private String groupingType;

  public SampleGroupDescriptionBox()
  {
    super("sgpd");
  }

  private GroupEntry parseGroupEntry(ByteBuffer paramByteBuffer, String paramString)
  {
    if ("roll".equals(paramString));
    for (Object localObject = new RollRecoveryEntry(); ; localObject = new UnknownEntry())
      while (true)
      {
        ((GroupEntry)localObject).parse(paramByteBuffer);
        return localObject;
        if ("rash".equals(paramString))
          localObject = new RateShareEntry();
        if ("seig".equals(paramString))
          localObject = new CencSampleEncryptionInformationGroupEntry();
        if ("rap ".equals(paramString))
          localObject = new VisualRandomAccessEntry();
        if (!"tele".equals(paramString))
          break;
        localObject = new TemporalLevelEntry();
      }
  }

  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() != 1)
      throw new RuntimeException("SampleGroupDescriptionBox are only supported in version 1");
    this.groupingType = IsoTypeReader.read4cc(paramByteBuffer);
    if (getVersion() == 1)
      this.defaultLength = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    long l1 = IsoTypeReader.readUInt32(paramByteBuffer);
    while (true)
    {
      long l2 = l1 - 1L;
      if (l1 <= 0L)
        return;
      int i = this.defaultLength;
      if (getVersion() != 1)
        break;
      if (this.defaultLength == 0)
      {
        this.descriptionLength = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
        i = this.descriptionLength;
      }
      int j = i + paramByteBuffer.position();
      ByteBuffer localByteBuffer = paramByteBuffer.slice();
      localByteBuffer.limit(i);
      this.groupEntries.add(parseGroupEntry(localByteBuffer, this.groupingType));
      paramByteBuffer.position(j);
      l1 = l2;
    }
    throw new RuntimeException("This should be implemented");
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    SampleGroupDescriptionBox localSampleGroupDescriptionBox;
    do
    {
      return true;
      if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
        return false;
      localSampleGroupDescriptionBox = (SampleGroupDescriptionBox)paramObject;
      if (this.defaultLength != localSampleGroupDescriptionBox.defaultLength)
        return false;
      if (this.groupEntries != null)
        if (this.groupEntries.equals(localSampleGroupDescriptionBox.groupEntries))
          break label72;
      do
        return false;
      while (localSampleGroupDescriptionBox.groupEntries != null);
      label72: if (this.groupingType == null)
        break;
    }
    while (this.groupingType.equals(localSampleGroupDescriptionBox.groupingType));
    while (true)
    {
      return false;
      if (localSampleGroupDescriptionBox.groupingType == null);
    }
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(this.groupingType.getBytes());
    if (getVersion() == 1)
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.defaultLength);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.groupEntries.size());
    Iterator localIterator = this.groupEntries.iterator();
    while (localIterator.hasNext())
    {
      GroupEntry localGroupEntry = (GroupEntry)localIterator.next();
      if ((getVersion() == 1) && (this.defaultLength == 0))
        IsoTypeWriter.writeUInt32(paramByteBuffer, localGroupEntry.get().limit());
      paramByteBuffer.put(localGroupEntry.get());
    }
  }

  protected long getContentSize()
  {
    long l1 = 8L;
    if (getVersion() == 1)
      l1 += 4L;
    long l2 = l1 + 4L;
    Iterator localIterator = this.groupEntries.iterator();
    while (localIterator.hasNext())
    {
      GroupEntry localGroupEntry = (GroupEntry)localIterator.next();
      if ((getVersion() == 1) && (this.defaultLength == 0))
        l2 += 4L;
      l2 += localGroupEntry.size();
    }
    return l2;
  }

  public int hashCode()
  {
    if (this.groupingType != null);
    for (int i = this.groupingType.hashCode(); ; i = 0)
    {
      int j = 31 * (i * 31 + this.defaultLength);
      List localList = this.groupEntries;
      int k = 0;
      if (localList != null)
        k = this.groupEntries.hashCode();
      return j + k;
    }
  }

  public String toString()
  {
    return "SampleGroupDescriptionBox{groupingType='" + this.groupingType + '\'' + ", defaultLength=" + this.defaultLength + ", groupEntries=" + this.groupEntries + '}';
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox
 * JD-Core Version:    0.5.4
 */