package com.android.gallery3d.data;

import android.media.ExifInterface;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class MediaDetails
  implements Iterable<Map.Entry<Integer, Object>>
{
  private TreeMap<Integer, Object> mDetails = new TreeMap();
  private HashMap<Integer, Integer> mUnits = new HashMap();

  public static void extractExifInfo(MediaDetails paramMediaDetails, String paramString)
  {
    try
    {
      ExifInterface localExifInterface = new ExifInterface(paramString);
      setExifData(paramMediaDetails, localExifInterface, "Flash", 102);
      setExifData(paramMediaDetails, localExifInterface, "ImageWidth", 5);
      setExifData(paramMediaDetails, localExifInterface, "ImageLength", 6);
      setExifData(paramMediaDetails, localExifInterface, "Make", 100);
      setExifData(paramMediaDetails, localExifInterface, "Model", 101);
      setExifData(paramMediaDetails, localExifInterface, "FNumber", 105);
      setExifData(paramMediaDetails, localExifInterface, "ISOSpeedRatings", 108);
      setExifData(paramMediaDetails, localExifInterface, "WhiteBalance", 104);
      setExifData(paramMediaDetails, localExifInterface, "ExposureTime", 107);
      double d = localExifInterface.getAttributeDouble("FocalLength", 0.0D);
      if (d != 0.0D)
      {
        paramMediaDetails.addDetail(103, Double.valueOf(d));
        paramMediaDetails.setUnit(103, 2131362277);
      }
      return;
    }
    catch (IOException localIOException)
    {
      Log.w("MediaDetails", "", localIOException);
    }
  }

  private static void setExifData(MediaDetails paramMediaDetails, ExifInterface paramExifInterface, String paramString, int paramInt)
  {
    String str = paramExifInterface.getAttribute(paramString);
    if (str != null)
    {
      if (paramInt != 102)
        break label42;
      paramMediaDetails.addDetail(paramInt, new FlashState(Integer.valueOf(str.toString()).intValue()));
    }
    return;
    label42: paramMediaDetails.addDetail(paramInt, str);
  }

  public void addDetail(int paramInt, Object paramObject)
  {
    this.mDetails.put(Integer.valueOf(paramInt), paramObject);
  }

  public Object getDetail(int paramInt)
  {
    return this.mDetails.get(Integer.valueOf(paramInt));
  }

  public int getUnit(int paramInt)
  {
    return ((Integer)this.mUnits.get(Integer.valueOf(paramInt))).intValue();
  }

  public boolean hasUnit(int paramInt)
  {
    return this.mUnits.containsKey(Integer.valueOf(paramInt));
  }

  public Iterator<Map.Entry<Integer, Object>> iterator()
  {
    return this.mDetails.entrySet().iterator();
  }

  public void setUnit(int paramInt1, int paramInt2)
  {
    this.mUnits.put(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
  }

  public int size()
  {
    return this.mDetails.size();
  }

  public static class FlashState
  {
    private static int FLASH_FIRED_MASK = 1;
    private static int FLASH_FUNCTION_MASK;
    private static int FLASH_MODE_MASK;
    private static int FLASH_RED_EYE_MASK;
    private static int FLASH_RETURN_MASK = 6;
    private int mState;

    static
    {
      FLASH_MODE_MASK = 24;
      FLASH_FUNCTION_MASK = 32;
      FLASH_RED_EYE_MASK = 64;
    }

    public FlashState(int paramInt)
    {
      this.mState = paramInt;
    }

    public boolean isFlashFired()
    {
      return (this.mState & FLASH_FIRED_MASK) != 0;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MediaDetails
 * JD-Core Version:    0.5.4
 */