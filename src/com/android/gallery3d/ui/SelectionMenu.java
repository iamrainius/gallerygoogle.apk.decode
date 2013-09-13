package com.android.gallery3d.ui;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectionMenu
  implements View.OnClickListener
{
  private final Button mButton;
  private final Context mContext;
  private final PopupList mPopupList;

  public SelectionMenu(Context paramContext, Button paramButton, PopupList.OnPopupItemClickListener paramOnPopupItemClickListener)
  {
    this.mContext = paramContext;
    this.mButton = paramButton;
    this.mPopupList = new PopupList(paramContext, this.mButton);
    this.mPopupList.addItem(2131558403, paramContext.getString(2131362218));
    this.mPopupList.setOnPopupItemClickListener(paramOnPopupItemClickListener);
    this.mButton.setOnClickListener(this);
  }

  public void onClick(View paramView)
  {
    this.mPopupList.show();
  }

  public void setTitle(CharSequence paramCharSequence)
  {
    this.mButton.setText(paramCharSequence);
  }

  public void updateSelectAllMode(boolean paramBoolean)
  {
    PopupList.Item localItem = this.mPopupList.findItem(2131558403);
    Context localContext;
    if (localItem != null)
    {
      localContext = this.mContext;
      if (!paramBoolean)
        break label38;
    }
    for (int i = 2131362219; ; i = 2131362218)
    {
      localItem.setTitle(localContext.getString(i));
      label38: return;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.SelectionMenu
 * JD-Core Version:    0.5.4
 */