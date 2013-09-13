package com.google.android.apps.lightcycle.util;

import android.content.Context;

public final class AnalyticsHelper
{
  private static AnalyticsHelper helper;

  private AnalyticsHelper(Context paramContext)
  {
  }

  public static AnalyticsHelper getInstance(Context paramContext)
  {
    if (helper == null)
      helper = new AnalyticsHelper(paramContext);
    return helper;
  }

  public void trackEvent(String paramString1, String paramString2, String paramString3, int paramInt)
  {
  }

  public void trackPage(Page paramPage)
  {
  }

  public static enum Page
  {
    static
    {
      MAIN_MENU = new Page("MAIN_MENU", 3);
      GALLERY = new Page("GALLERY", 4);
      HELP = new Page("HELP", 5);
      LAST_HELP_PAGE = new Page("LAST_HELP_PAGE", 6);
      STITCH_COMPLETE = new Page("STITCH_COMPLETE", 7);
      UPLOAD_START = new Page("UPLOAD_START", 8);
      UPLOAD_SUCCESSFUL = new Page("UPLOAD_SUCCESSFUL", 9);
      DELETE_SESSION = new Page("DELETE_SESSION", 10);
      Page[] arrayOfPage = new Page[11];
      arrayOfPage[0] = BEGIN_CAPTURE;
      arrayOfPage[1] = END_CAPTURE;
      arrayOfPage[2] = VIEW_PANORAMA;
      arrayOfPage[3] = MAIN_MENU;
      arrayOfPage[4] = GALLERY;
      arrayOfPage[5] = HELP;
      arrayOfPage[6] = LAST_HELP_PAGE;
      arrayOfPage[7] = STITCH_COMPLETE;
      arrayOfPage[8] = UPLOAD_START;
      arrayOfPage[9] = UPLOAD_SUCCESSFUL;
      arrayOfPage[10] = DELETE_SESSION;
      $VALUES = arrayOfPage;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.AnalyticsHelper
 * JD-Core Version:    0.5.4
 */