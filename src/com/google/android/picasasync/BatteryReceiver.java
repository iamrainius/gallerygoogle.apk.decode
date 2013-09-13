package com.google.android.picasasync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (!PicasaFacade.get(paramContext).isMaster())
      return;
    boolean bool = "android.intent.action.ACTION_POWER_CONNECTED".equals(paramIntent.getAction());
    PicasaSyncManager.get(paramContext).onBatteryStateChanged(bool);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.BatteryReceiver
 * JD-Core Version:    0.5.4
 */