package com.google.android.apps.lightcycle.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Dialogs
{
  private static AlertDialog createOkDialog(int paramInt, CharSequence paramCharSequence, Context paramContext, Callback<Void> paramCallback)
  {
    AlertDialog localAlertDialog = new AlertDialog.Builder(paramContext).create();
    if (paramInt != -1)
      localAlertDialog.setTitle(paramInt);
    localAlertDialog.setMessage(paramCharSequence);
    localAlertDialog.setButton(-3, paramContext.getText(2131361809), new DialogInterface.OnClickListener(paramCallback)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        paramDialogInterface.dismiss();
        if (this.val$closedCallback == null)
          return;
        this.val$closedCallback.onCallback(null);
      }
    });
    return localAlertDialog;
  }

  public static ProgressDialog createProgressDialog(int paramInt, Context paramContext)
  {
    ProgressDialog localProgressDialog = new ProgressDialog(paramContext);
    localProgressDialog.setCancelable(false);
    localProgressDialog.setTitle(paramInt);
    return localProgressDialog;
  }

  public static void showDialog(int paramInt1, int paramInt2, Context paramContext, Callback<Void> paramCallback)
  {
    showDialog(paramInt1, paramContext.getText(paramInt2), paramContext, paramCallback);
  }

  public static void showDialog(int paramInt, CharSequence paramCharSequence, Context paramContext, Callback<Void> paramCallback)
  {
    createOkDialog(paramInt, paramCharSequence, paramContext, paramCallback).show();
  }

  public static void showOkCancelDialog(int paramInt, String paramString, Context paramContext, Callback<Void> paramCallback)
  {
    AlertDialog localAlertDialog = createOkDialog(paramInt, paramString, paramContext, paramCallback);
    localAlertDialog.setButton(-2, paramContext.getText(2131361888), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        paramDialogInterface.dismiss();
      }
    });
    localAlertDialog.show();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.Dialogs
 * JD-Core Version:    0.5.4
 */