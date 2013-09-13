package com.android.gallery3d.util;

import android.os.Environment;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import java.io.File;
import java.util.Comparator;

public class MediaSetUtils
{
  public static final int CAMERA_BUCKET_ID;
  private static final Path[] CAMERA_PATHS;
  public static final int DOWNLOAD_BUCKET_ID;
  public static final int EDITED_ONLINE_PHOTOS_BUCKET_ID;
  public static final int IMPORTED_BUCKET_ID;
  public static final Comparator<MediaSet> NAME_COMPARATOR = new NameComparator();
  public static final int SNAPSHOT_BUCKET_ID;

  static
  {
    CAMERA_BUCKET_ID = GalleryUtils.getBucketId(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera");
    DOWNLOAD_BUCKET_ID = GalleryUtils.getBucketId(Environment.getExternalStorageDirectory().toString() + "/" + "download");
    EDITED_ONLINE_PHOTOS_BUCKET_ID = GalleryUtils.getBucketId(Environment.getExternalStorageDirectory().toString() + "/" + "EditedOnlinePhotos");
    IMPORTED_BUCKET_ID = GalleryUtils.getBucketId(Environment.getExternalStorageDirectory().toString() + "/" + "Imported");
    SNAPSHOT_BUCKET_ID = GalleryUtils.getBucketId(Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots");
    Path[] arrayOfPath = new Path[3];
    arrayOfPath[0] = Path.fromString("/local/all/" + CAMERA_BUCKET_ID);
    arrayOfPath[1] = Path.fromString("/local/image/" + CAMERA_BUCKET_ID);
    arrayOfPath[2] = Path.fromString("/local/video/" + CAMERA_BUCKET_ID);
    CAMERA_PATHS = arrayOfPath;
  }

  public static boolean isCameraSource(Path paramPath)
  {
    if ((CAMERA_PATHS[0] != paramPath) && (CAMERA_PATHS[1] != paramPath))
    {
      Path localPath = CAMERA_PATHS[2];
      i = 0;
      if (localPath != paramPath)
        break label33;
    }
    int i = 1;
    label33: return i;
  }

  public static class NameComparator
    implements Comparator<MediaSet>
  {
    public int compare(MediaSet paramMediaSet1, MediaSet paramMediaSet2)
    {
      int i = paramMediaSet1.getName().compareToIgnoreCase(paramMediaSet2.getName());
      if (i != 0)
        return i;
      return paramMediaSet1.getPath().toString().compareTo(paramMediaSet2.getPath().toString());
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.MediaSetUtils
 * JD-Core Version:    0.5.4
 */