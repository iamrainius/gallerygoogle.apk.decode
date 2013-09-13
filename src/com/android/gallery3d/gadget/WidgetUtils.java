package com.android.gallery3d.gadget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;

public class WidgetUtils
{
  private static int sStackPhotoHeight;
  private static int sStackPhotoWidth = 220;

  static
  {
    sStackPhotoHeight = 170;
  }

  public static Bitmap createWidgetBitmap(Bitmap paramBitmap, int paramInt)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float f;
    if ((0x1 & paramInt / 90) == 0)
      f = Math.max(sStackPhotoWidth / i, sStackPhotoHeight / j);
    while (true)
    {
      Bitmap localBitmap = Bitmap.createBitmap(sStackPhotoWidth, sStackPhotoHeight, Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      localCanvas.translate(sStackPhotoWidth / 2, sStackPhotoHeight / 2);
      localCanvas.rotate(paramInt);
      localCanvas.scale(f, f);
      Paint localPaint = new Paint(6);
      localCanvas.drawBitmap(paramBitmap, -i / 2, -j / 2, localPaint);
      return localBitmap;
      f = Math.max(sStackPhotoWidth / j, sStackPhotoHeight / i);
    }
  }

  public static Bitmap createWidgetBitmap(MediaItem paramMediaItem)
  {
    Bitmap localBitmap = (Bitmap)paramMediaItem.requestImage(1).run(ThreadPool.JOB_CONTEXT_STUB);
    if (localBitmap == null)
    {
      Log.w("WidgetUtils", "fail to get image of " + paramMediaItem.toString());
      return null;
    }
    return createWidgetBitmap(localBitmap, paramMediaItem.getRotation());
  }

  public static void initialize(Context paramContext)
  {
    Resources localResources = paramContext.getResources();
    sStackPhotoWidth = localResources.getDimensionPixelSize(2131624012);
    sStackPhotoHeight = localResources.getDimensionPixelSize(2131624013);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.WidgetUtils
 * JD-Core Version:    0.5.4
 */