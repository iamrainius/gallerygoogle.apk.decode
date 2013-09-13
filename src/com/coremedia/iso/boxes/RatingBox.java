package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class RatingBox extends AbstractFullBox
{
  private String language;
  private String ratingCriteria;
  private String ratingEntity;
  private String ratingInfo;

  public RatingBox()
  {
    super("rtng");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.ratingEntity = IsoTypeReader.read4cc(paramByteBuffer);
    this.ratingCriteria = IsoTypeReader.read4cc(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.ratingInfo = IsoTypeReader.readString(paramByteBuffer);
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.ratingEntity));
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.ratingCriteria));
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.ratingInfo));
    paramByteBuffer.put(0);
  }

  protected long getContentSize()
  {
    return 15 + Utf8.utf8StringLengthInBytes(this.ratingInfo);
  }

  public String getLanguage()
  {
    return this.language;
  }

  public String getRatingCriteria()
  {
    return this.ratingCriteria;
  }

  public String getRatingEntity()
  {
    return this.ratingEntity;
  }

  public String getRatingInfo()
  {
    return this.ratingInfo;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("RatingBox[language=").append(getLanguage());
    localStringBuilder.append("ratingEntity=").append(getRatingEntity());
    localStringBuilder.append(";ratingCriteria=").append(getRatingCriteria());
    localStringBuilder.append(";language=").append(getLanguage());
    localStringBuilder.append(";ratingInfo=").append(getRatingInfo());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.RatingBox
 * JD-Core Version:    0.5.4
 */