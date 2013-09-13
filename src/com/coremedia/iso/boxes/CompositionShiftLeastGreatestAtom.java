package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;

public class CompositionShiftLeastGreatestAtom extends AbstractFullBox
{
  int compositionOffsetToDisplayOffsetShift;
  int displayEndTime;
  int displayStartTime;
  int greatestDisplayOffset;
  int leastDisplayOffset;

  public CompositionShiftLeastGreatestAtom()
  {
    super("cslg");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.compositionOffsetToDisplayOffsetShift = paramByteBuffer.getInt();
    this.leastDisplayOffset = paramByteBuffer.getInt();
    this.greatestDisplayOffset = paramByteBuffer.getInt();
    this.displayStartTime = paramByteBuffer.getInt();
    this.displayEndTime = paramByteBuffer.getInt();
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.putInt(this.compositionOffsetToDisplayOffsetShift);
    paramByteBuffer.putInt(this.leastDisplayOffset);
    paramByteBuffer.putInt(this.greatestDisplayOffset);
    paramByteBuffer.putInt(this.displayStartTime);
    paramByteBuffer.putInt(this.displayEndTime);
  }

  protected long getContentSize()
  {
    return 24L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.CompositionShiftLeastGreatestAtom
 * JD-Core Version:    0.5.4
 */