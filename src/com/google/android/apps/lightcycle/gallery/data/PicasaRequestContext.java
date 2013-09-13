package com.google.android.apps.lightcycle.gallery.data;

import android.content.Context;

public class PicasaRequestContext
{
  public final String accountName;
  public final Context androidContext;
  public final String authToken;

  public PicasaRequestContext(String paramString1, String paramString2, Context paramContext)
  {
    this.accountName = paramString1;
    this.authToken = paramString2;
    this.androidContext = paramContext;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.data.PicasaRequestContext
 * JD-Core Version:    0.5.4
 */