package com.android.gallery3d.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;

public class PopupList
{
  private final View mAnchorView;
  private ListView mContentList;
  private final Context mContext;
  private final ArrayList<Item> mItems = new ArrayList();
  private final PopupWindow.OnDismissListener mOnDismissListener = new PopupWindow.OnDismissListener()
  {
    public void onDismiss()
    {
      if (PopupList.this.mPopupWindow == null);
      ViewTreeObserver localViewTreeObserver;
      do
      {
        return;
        PopupList.access$002(PopupList.this, null);
        localViewTreeObserver = PopupList.this.mAnchorView.getViewTreeObserver();
      }
      while (!localViewTreeObserver.isAlive());
      localViewTreeObserver.removeGlobalOnLayoutListener(PopupList.this.mOnGLobalLayoutListener);
    }
  };
  private final ViewTreeObserver.OnGlobalLayoutListener mOnGLobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
  {
    public void onGlobalLayout()
    {
      if (PopupList.this.mPopupWindow == null)
        return;
      PopupList.this.updatePopupLayoutParams();
      PopupList.this.mPopupWindow.update(PopupList.this.mAnchorView, PopupList.this.mPopupOffsetX, PopupList.this.mPopupOffsetY, PopupList.this.mPopupWidth, PopupList.this.mPopupHeight);
    }
  };
  private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      if (PopupList.this.mPopupWindow == null);
      do
      {
        return;
        PopupList.this.mPopupWindow.dismiss();
      }
      while (PopupList.this.mOnPopupItemClickListener == null);
      PopupList.this.mOnPopupItemClickListener.onPopupItemClick((int)paramLong);
    }
  };
  private OnPopupItemClickListener mOnPopupItemClickListener;
  private int mPopupHeight;
  private int mPopupOffsetX;
  private int mPopupOffsetY;
  private int mPopupWidth;
  private PopupWindow mPopupWindow;

  public PopupList(Context paramContext, View paramView)
  {
    this.mContext = paramContext;
    this.mAnchorView = paramView;
  }

  private PopupWindow createPopupWindow()
  {
    PopupWindow localPopupWindow = new PopupWindow(this.mContext);
    localPopupWindow.setOnDismissListener(this.mOnDismissListener);
    localPopupWindow.setBackgroundDrawable(this.mContext.getResources().getDrawable(2130837811));
    this.mContentList = new ListView(this.mContext, null, 16842861);
    this.mContentList.setAdapter(new ItemDataAdapter(null));
    this.mContentList.setOnItemClickListener(this.mOnItemClickListener);
    localPopupWindow.setContentView(this.mContentList);
    localPopupWindow.setFocusable(true);
    localPopupWindow.setOutsideTouchable(true);
    return localPopupWindow;
  }

  private void updatePopupLayoutParams()
  {
    ListView localListView = this.mContentList;
    PopupWindow localPopupWindow = this.mPopupWindow;
    Rect localRect = new Rect();
    localPopupWindow.getBackground().getPadding(localRect);
    int i = this.mPopupWindow.getMaxAvailableHeight(this.mAnchorView) - localRect.top - localRect.bottom;
    this.mContentList.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(i, -2147483648));
    this.mPopupWidth = (localListView.getMeasuredWidth() + localRect.top + localRect.bottom);
    this.mPopupHeight = Math.min(i, localListView.getMeasuredHeight() + localRect.left + localRect.right);
    this.mPopupOffsetX = (-localRect.left);
    this.mPopupOffsetY = (-localRect.top);
  }

  public void addItem(int paramInt, String paramString)
  {
    this.mItems.add(new Item(paramInt, paramString));
  }

  public Item findItem(int paramInt)
  {
    Iterator localIterator = this.mItems.iterator();
    Item localItem;
    while (localIterator.hasNext())
    {
      localItem = (Item)localIterator.next();
      if (localItem.id == paramInt)
        return localItem;
    }
    return null;
  }

  public void setOnPopupItemClickListener(OnPopupItemClickListener paramOnPopupItemClickListener)
  {
    this.mOnPopupItemClickListener = paramOnPopupItemClickListener;
  }

  public void show()
  {
    if (this.mPopupWindow != null)
      return;
    this.mAnchorView.getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGLobalLayoutListener);
    this.mPopupWindow = createPopupWindow();
    updatePopupLayoutParams();
    this.mPopupWindow.setWidth(this.mPopupWidth);
    this.mPopupWindow.setHeight(this.mPopupHeight);
    this.mPopupWindow.showAsDropDown(this.mAnchorView, this.mPopupOffsetX, this.mPopupOffsetY);
  }

  public static class Item
  {
    public final int id;
    public String title;

    public Item(int paramInt, String paramString)
    {
      this.id = paramInt;
      this.title = paramString;
    }

    public void setTitle(String paramString)
    {
      this.title = paramString;
    }
  }

  private class ItemDataAdapter extends BaseAdapter
  {
    private ItemDataAdapter()
    {
    }

    public int getCount()
    {
      return PopupList.this.mItems.size();
    }

    public Object getItem(int paramInt)
    {
      return PopupList.this.mItems.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return ((PopupList.Item)PopupList.this.mItems.get(paramInt)).id;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
        paramView = LayoutInflater.from(PopupList.this.mContext).inflate(2130968646, null);
      ((TextView)paramView.findViewById(16908308)).setText(((PopupList.Item)PopupList.this.mItems.get(paramInt)).title);
      return paramView;
    }
  }

  public static abstract interface OnPopupItemClickListener
  {
    public abstract boolean onPopupItemClick(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.PopupList
 * JD-Core Version:    0.5.4
 */