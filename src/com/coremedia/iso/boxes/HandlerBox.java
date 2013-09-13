package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HandlerBox extends AbstractFullBox
{
  public static final Map<String, String> readableTypes;
  private long a;
  private long b;
  private long c;
  private String handlerType;
  private String name = null;
  private long shouldBeZeroButAppleWritesHereSomeValue;
  private boolean zeroTerm = true;

  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("odsm", "ObjectDescriptorStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("crsm", "ClockReferenceStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("sdsm", "SceneDescriptionStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("m7sm", "MPEG7Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("ocsm", "ObjectContentInfoStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("ipsm", "IPMP Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("mjsm", "MPEG-J Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("mdir", "Apple Meta Data iTunes Reader");
    localHashMap.put("mp7b", "MPEG-7 binary XML");
    localHashMap.put("mp7t", "MPEG-7 XML");
    localHashMap.put("vide", "Video Track");
    localHashMap.put("soun", "Sound Track");
    localHashMap.put("hint", "Hint Track");
    localHashMap.put("appl", "Apple specific");
    localHashMap.put("meta", "Timed Metadata track - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    readableTypes = Collections.unmodifiableMap(localHashMap);
  }

  public HandlerBox()
  {
    super("hdlr");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.shouldBeZeroButAppleWritesHereSomeValue = IsoTypeReader.readUInt32(paramByteBuffer);
    this.handlerType = IsoTypeReader.read4cc(paramByteBuffer);
    this.a = IsoTypeReader.readUInt32(paramByteBuffer);
    this.b = IsoTypeReader.readUInt32(paramByteBuffer);
    this.c = IsoTypeReader.readUInt32(paramByteBuffer);
    if (paramByteBuffer.remaining() > 0)
    {
      this.name = IsoTypeReader.readString(paramByteBuffer, paramByteBuffer.remaining());
      if (this.name.endsWith(""))
      {
        this.name = this.name.substring(0, -1 + this.name.length());
        this.zeroTerm = true;
        return;
      }
      this.zeroTerm = false;
      return;
    }
    this.zeroTerm = false;
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.shouldBeZeroButAppleWritesHereSomeValue);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.handlerType));
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.a);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.b);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.c);
    if (this.name != null)
      paramByteBuffer.put(Utf8.convert(this.name));
    if (!this.zeroTerm)
      return;
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    if (this.zeroTerm)
      return 25 + Utf8.utf8StringLengthInBytes(this.name);
    return 24 + Utf8.utf8StringLengthInBytes(this.name);
  }

  public String getHandlerType()
  {
    return this.handlerType;
  }

  public String getName()
  {
    return this.name;
  }

  public void setHandlerType(String paramString)
  {
    this.handlerType = paramString;
  }

  public String toString()
  {
    return "HandlerBox[handlerType=" + getHandlerType() + ";name=" + getName() + "]";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.HandlerBox
 * JD-Core Version:    0.5.4
 */