package com.android.gallery3d.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.util.Log;
import java.util.Locale;

public class HelpUtils
{
  private static final String TAG = HelpUtils.class.getName();
  private static String sCachedVersionCode = null;

  public static Intent getHelpIntent(Context paramContext, int paramInt)
  {
    String str = paramContext.getString(paramInt);
    if (TextUtils.isEmpty(str))
      return null;
    Intent localIntent = new Intent("android.intent.action.VIEW", uriWithAddedParameters(paramContext, Uri.parse(str)));
    localIntent.setFlags(276824064);
    return localIntent;
  }

  private static Uri uriWithAddedParameters(Context paramContext, Uri paramUri)
  {
    Uri.Builder localBuilder = paramUri.buildUpon();
    localBuilder.appendQueryParameter("hl", Locale.getDefault().toString());
    if (sCachedVersionCode == null);
    try
    {
      sCachedVersionCode = Integer.toString(paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionCode);
      localBuilder.appendQueryParameter("version", sCachedVersionCode);
      label55: return localBuilder.build();
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.wtf(TAG, "Invalid package name for context", localNameNotFoundException);
      break label55:
      localBuilder.appendQueryParameter("version", sCachedVersionCode);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.HelpUtils
 * JD-Core Version:    0.5.4
 */