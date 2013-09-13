package com.android.gallery3d.exif;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class IfdData
{
  private final Map<Short, ExifTag> mExifTags = new HashMap();
  private final int mIfdId;
  private int mOffsetToNextIfd = 0;

  public IfdData(int paramInt)
  {
    this.mIfdId = paramInt;
  }

  public boolean equals(Object paramObject)
  {
    int j;
    label46: ExifTag localExifTag;
    if (paramObject instanceof IfdData)
    {
      IfdData localIfdData = (IfdData)paramObject;
      if ((localIfdData.getId() == this.mIfdId) && (localIfdData.getTagCount() == getTagCount()))
      {
        ExifTag[] arrayOfExifTag = localIfdData.getAllTags();
        int i = arrayOfExifTag.length;
        j = 0;
        if (j >= i)
          break label106;
        localExifTag = arrayOfExifTag[j];
        if (!ExifTag.isOffsetTag(localExifTag.getTagId()));
      }
    }
    while (true)
    {
      ++j;
      break label46:
      if (!localExifTag.equals((ExifTag)this.mExifTags.get(Short.valueOf(localExifTag.getTagId()))))
        return false;
    }
    label106: return true;
  }

  public ExifTag[] getAllTags()
  {
    return (ExifTag[])this.mExifTags.values().toArray(new ExifTag[this.mExifTags.size()]);
  }

  public int getId()
  {
    return this.mIfdId;
  }

  int getOffsetToNextIfd()
  {
    return this.mOffsetToNextIfd;
  }

  public ExifTag getTag(short paramShort)
  {
    return (ExifTag)this.mExifTags.get(Short.valueOf(paramShort));
  }

  public int getTagCount()
  {
    return this.mExifTags.size();
  }

  void setOffsetToNextIfd(int paramInt)
  {
    this.mOffsetToNextIfd = paramInt;
  }

  public void setTag(ExifTag paramExifTag)
  {
    this.mExifTags.put(Short.valueOf(paramExifTag.getTagId()), paramExifTag);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.IfdData
 * JD-Core Version:    0.5.4
 */