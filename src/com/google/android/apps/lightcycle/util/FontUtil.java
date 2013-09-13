package com.google.android.apps.lightcycle.util;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtil
{
  private final Context context;
  private Typeface mainMenuFont;

  public FontUtil(Context paramContext)
  {
    this.context = paramContext;
  }

  public Typeface getMainMenuFont()
  {
    if (this.mainMenuFont == null)
      this.mainMenuFont = Typeface.createFromAsset(this.context.getAssets(), "cafenerom54.ttf");
    return this.mainMenuFont;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.FontUtil
 * JD-Core Version:    0.5.4
 */