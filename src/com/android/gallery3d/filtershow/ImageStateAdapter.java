package com.android.gallery3d.filtershow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.gallery3d.filtershow.filters.ImageFilter;

public class ImageStateAdapter extends ArrayAdapter<ImageFilter>
{
  public ImageStateAdapter(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
  }

  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramView;
    if (localView == null)
      localView = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130968599, null);
    ImageFilter localImageFilter = (ImageFilter)getItem(paramInt);
    if (localImageFilter != null)
    {
      ((TextView)localView.findViewById(2131558491)).setText(localImageFilter.getName());
      ((TextView)localView.findViewById(2131558492)).setText("" + localImageFilter.getParameter());
    }
    return localView;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.ImageStateAdapter
 * JD-Core Version:    0.5.4
 */