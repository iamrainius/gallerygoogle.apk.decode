package com.android.gallery3d.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

public class Wallpaper extends Activity
{
  private Uri mPickedItem;
  private int mState = 0;

  @TargetApi(13)
  private Point getDefaultDisplaySize(Point paramPoint)
  {
    Display localDisplay = getWindowManager().getDefaultDisplay();
    if (Build.VERSION.SDK_INT >= 13)
    {
      localDisplay.getSize(paramPoint);
      return paramPoint;
    }
    paramPoint.set(localDisplay.getWidth(), localDisplay.getHeight());
    return paramPoint;
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt2 != -1)
    {
      setResult(paramInt2);
      finish();
    }
    do
    {
      return;
      this.mState = paramInt1;
    }
    while (this.mState != 1);
    this.mPickedItem = paramIntent.getData();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle == null)
      return;
    this.mState = paramBundle.getInt("activity-state");
    this.mPickedItem = ((Uri)paramBundle.getParcelable("picked-item"));
  }

  protected void onResume()
  {
    super.onResume();
    Intent localIntent = getIntent();
    switch (this.mState)
    {
    default:
      return;
    case 0:
      this.mPickedItem = localIntent.getData();
      if (this.mPickedItem == null)
      {
        startActivityForResult(new Intent("android.intent.action.GET_CONTENT").setClass(this, DialogPicker.class).setType("image/*"), 1);
        return;
      }
      this.mState = 1;
    case 1:
    }
    int i = getWallpaperDesiredMinimumWidth();
    int j = getWallpaperDesiredMinimumHeight();
    Point localPoint = getDefaultDisplaySize(new Point());
    float f1 = localPoint.x / i;
    float f2 = localPoint.y / j;
    startActivity(new Intent("com.android.camera.action.CROP").setDataAndType(this.mPickedItem, "image/*").addFlags(33554432).putExtra("outputX", i).putExtra("outputY", j).putExtra("aspectX", i).putExtra("aspectY", j).putExtra("spotlightX", f1).putExtra("spotlightY", f2).putExtra("scale", true).putExtra("scaleUpIfNeeded", true).putExtra("noFaceDetection", true).putExtra("set-as-wallpaper", true));
    finish();
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putInt("activity-state", this.mState);
    if (this.mPickedItem == null)
      return;
    paramBundle.putParcelable("picked-item", this.mPickedItem);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.Wallpaper
 * JD-Core Version:    0.5.4
 */