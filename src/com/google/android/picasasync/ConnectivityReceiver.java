package com.google.android.picasasync;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext.startService(new Intent(paramContext, AsyncService.class));
  }

  public static class AsyncService extends IntentService
  {
    public AsyncService()
    {
      super("PicasaSyncConnectivityAsync");
    }

    protected void onHandleIntent(Intent paramIntent)
    {
      if (!PicasaFacade.get(this).isMaster())
        return;
      PicasaSyncManager.get(this).onEnvironmentChanged();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.ConnectivityReceiver
 * JD-Core Version:    0.5.4
 */