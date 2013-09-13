package com.google.android.gms.common;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender.SendIntentException;

public final class ConnectionResult
{
  public static final ConnectionResult RESULT_SUCCESS = new ConnectionResult(0, null);
  private final PendingIntent mPendingIntent;
  private final int mStatusCode;

  public ConnectionResult(int paramInt, PendingIntent paramPendingIntent)
  {
    this.mStatusCode = paramInt;
    this.mPendingIntent = paramPendingIntent;
  }

  public int getErrorCode()
  {
    return this.mStatusCode;
  }

  public boolean hasResolution()
  {
    return (this.mStatusCode != 0) && (this.mPendingIntent != null);
  }

  public void startResolutionForResult(Activity paramActivity, int paramInt)
    throws IntentSender.SendIntentException
  {
    if (!hasResolution())
      return;
    paramActivity.startIntentSenderForResult(this.mPendingIntent.getIntentSender(), paramInt, null, 0, 0, 0);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.gms.common.ConnectionResult
 * JD-Core Version:    0.5.4
 */