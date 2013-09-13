package com.android.camera.ui;

import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PopupManager
{
  private static HashMap<Context, PopupManager> sMap = new HashMap();
  private ArrayList<OnOtherPopupShowedListener> mListeners = new ArrayList();

  public static PopupManager getInstance(Context paramContext)
  {
    PopupManager localPopupManager = (PopupManager)sMap.get(paramContext);
    if (localPopupManager == null)
    {
      localPopupManager = new PopupManager();
      sMap.put(paramContext, localPopupManager);
    }
    return localPopupManager;
  }

  public static void removeInstance(Context paramContext)
  {
    ((PopupManager)sMap.get(paramContext));
    sMap.remove(paramContext);
  }

  public void notifyShowPopup(View paramView)
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext())
    {
      OnOtherPopupShowedListener localOnOtherPopupShowedListener = (OnOtherPopupShowedListener)localIterator.next();
      if ((View)localOnOtherPopupShowedListener == paramView)
        continue;
      localOnOtherPopupShowedListener.onOtherPopupShowed();
    }
  }

  public static abstract interface OnOtherPopupShowedListener
  {
    public abstract void onOtherPopupShowed();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.PopupManager
 * JD-Core Version:    0.5.4
 */