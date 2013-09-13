package com.android.gallery3d.exif;

import java.io.IOException;
import java.io.InputStream;

public class ExifReader
{
  public ExifData read(InputStream paramInputStream)
    throws ExifInvalidFormatException, IOException
  {
    ExifParser localExifParser = ExifParser.parse(paramInputStream);
    ExifData localExifData = new ExifData(localExifParser.getByteOrder());
    int i = localExifParser.next();
    if (i != 5)
    {
      label23: switch (i)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      }
      while (true)
      {
        i = localExifParser.next();
        break label23:
        localExifData.addIfdData(new IfdData(localExifParser.getCurrentIfd()));
        continue;
        ExifTag localExifTag2 = localExifParser.getTag();
        if (!localExifTag2.hasValue())
          localExifParser.registerForTagValue(localExifTag2);
        localExifData.getIfdData(localExifTag2.getIfd()).setTag(localExifTag2);
        continue;
        ExifTag localExifTag1 = localExifParser.getTag();
        if (localExifTag1.getDataType() == 7)
        {
          byte[] arrayOfByte3 = new byte[localExifTag1.getComponentCount()];
          localExifParser.read(arrayOfByte3);
          localExifTag1.setValue(arrayOfByte3);
        }
        localExifData.getIfdData(localExifTag1.getIfd()).setTag(localExifTag1);
        continue;
        byte[] arrayOfByte2 = new byte[localExifParser.getCompressedImageSize()];
        localExifParser.read(arrayOfByte2);
        localExifData.setCompressedThumbnail(arrayOfByte2);
        continue;
        byte[] arrayOfByte1 = new byte[localExifParser.getStripSize()];
        localExifParser.read(arrayOfByte1);
        localExifData.setStripBytes(localExifParser.getStripIndex(), arrayOfByte1);
      }
    }
    return localExifData;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.exif.ExifReader
 * JD-Core Version:    0.5.4
 */