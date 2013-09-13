package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import java.util.List;

public abstract interface ContainerBox extends Box
{
  public abstract List<Box> getBoxes();

  public abstract <T extends Box> List<T> getBoxes(Class<T> paramClass);

  public abstract <T extends Box> List<T> getBoxes(Class<T> paramClass, boolean paramBoolean);

  public abstract IsoFile getIsoFile();
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.ContainerBox
 * JD-Core Version:    0.5.4
 */