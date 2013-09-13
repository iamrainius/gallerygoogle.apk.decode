package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public class GenericMediaHeaderTextAtom extends AbstractBox
{
  int unknown_1 = 65536;
  int unknown_2;
  int unknown_3;
  int unknown_4;
  int unknown_5 = 65536;
  int unknown_6;
  int unknown_7;
  int unknown_8;
  int unknown_9 = 1073741824;

  public GenericMediaHeaderTextAtom()
  {
    super("text");
  }

  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.unknown_1 = paramByteBuffer.getInt();
    this.unknown_2 = paramByteBuffer.getInt();
    this.unknown_3 = paramByteBuffer.getInt();
    this.unknown_4 = paramByteBuffer.getInt();
    this.unknown_5 = paramByteBuffer.getInt();
    this.unknown_6 = paramByteBuffer.getInt();
    this.unknown_7 = paramByteBuffer.getInt();
    this.unknown_8 = paramByteBuffer.getInt();
    this.unknown_9 = paramByteBuffer.getInt();
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.putInt(this.unknown_1);
    paramByteBuffer.putInt(this.unknown_2);
    paramByteBuffer.putInt(this.unknown_3);
    paramByteBuffer.putInt(this.unknown_4);
    paramByteBuffer.putInt(this.unknown_5);
    paramByteBuffer.putInt(this.unknown_6);
    paramByteBuffer.putInt(this.unknown_7);
    paramByteBuffer.putInt(this.unknown_8);
    paramByteBuffer.putInt(this.unknown_9);
  }

  protected long getContentSize()
  {
    return 36L;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.apple.GenericMediaHeaderTextAtom
 * JD-Core Version:    0.5.4
 */