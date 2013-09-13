package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

public abstract interface BoxParser
{
  public abstract Box parseBox(ReadableByteChannel paramReadableByteChannel, ContainerBox paramContainerBox)
    throws IOException;
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.BoxParser
 * JD-Core Version:    0.5.4
 */