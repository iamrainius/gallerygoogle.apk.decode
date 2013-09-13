package com.android.gallery3d.data;

import com.android.gallery3d.util.MediaSetUtils;

public final class DataSourceType
{
  private static final Path LOCAL_ROOT;
  private static final Path MTP_ROOT;
  private static final Path PICASA_ROOT = Path.fromString("/picasa");

  static
  {
    LOCAL_ROOT = Path.fromString("/local");
    MTP_ROOT = Path.fromString("/mtp");
  }

  public static int identifySourceType(MediaSet paramMediaSet)
  {
    if (paramMediaSet == null);
    Path localPath2;
    do
    {
      return 0;
      Path localPath1 = paramMediaSet.getPath();
      if (MediaSetUtils.isCameraSource(localPath1))
        return 4;
      localPath2 = localPath1.getPrefixPath();
      if (localPath2 == PICASA_ROOT)
        return 2;
      if (localPath2 == MTP_ROOT)
        return 3;
    }
    while (localPath2 != LOCAL_ROOT);
    return 1;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.DataSourceType
 * JD-Core Version:    0.5.4
 */