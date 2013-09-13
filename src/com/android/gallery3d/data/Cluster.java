package com.android.gallery3d.data;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import java.util.ArrayList;

class Cluster
{
  public boolean mGeographicallySeparatedFromPrevCluster = false;
  private ArrayList<SmallItem> mItems = new ArrayList();

  public void addItem(SmallItem paramSmallItem)
  {
    this.mItems.add(paramSmallItem);
  }

  public String generateCaption(Context paramContext)
  {
    int i = this.mItems.size();
    long l1 = 0L;
    long l2 = 0L;
    int j = 0;
    if (j < i)
    {
      label16: long l4 = ((SmallItem)this.mItems.get(j)).dateInMs;
      if (l4 == 0L);
      while (true)
      {
        ++j;
        break label16:
        if (l1 == 0L)
        {
          l2 = l4;
          l1 = l4;
        }
        l1 = Math.min(l1, l4);
        l2 = Math.max(l2, l4);
      }
    }
    String str3;
    if (l1 == 0L)
      str3 = "";
    String str1;
    String str2;
    do
    {
      return str3;
      str1 = DateFormat.format("MMddyy", l1).toString();
      str2 = DateFormat.format("MMddyy", l2).toString();
      if (!str1.substring(4).equals(str2.substring(4)))
        break label203;
      str3 = DateUtils.formatDateRange(paramContext, l1, l2, 524288);
    }
    while ((!str1.equals(str2)) || (DateUtils.formatDateTime(paramContext, l1, 65552).equals(DateUtils.formatDateTime(paramContext, l1, 65556))));
    long l3 = (l1 + l2) / 2L;
    return DateUtils.formatDateRange(paramContext, l3, l3, 65553);
    label203: return DateUtils.formatDateRange(paramContext, l1, l2, 65584);
  }

  public ArrayList<SmallItem> getItems()
  {
    return this.mItems;
  }

  public SmallItem getLastItem()
  {
    int i = this.mItems.size();
    if (i == 0)
      return null;
    return (SmallItem)this.mItems.get(i - 1);
  }

  public int size()
  {
    return this.mItems.size();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.Cluster
 * JD-Core Version:    0.5.4
 */