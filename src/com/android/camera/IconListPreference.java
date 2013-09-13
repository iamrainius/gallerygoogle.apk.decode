package com.android.camera;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import java.util.List;

public class IconListPreference extends ListPreference
{
  private int[] mIconIds;
  private int[] mImageIds;
  private int[] mLargeIconIds;
  private int mSingleIconId;
  private boolean mUseSingleIcon;

  public IconListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.IconListPreference, 0, 0);
    Resources localResources = paramContext.getResources();
    this.mSingleIconId = localTypedArray.getResourceId(1, 0);
    this.mIconIds = getIds(localResources, localTypedArray.getResourceId(0, 0));
    this.mLargeIconIds = getIds(localResources, localTypedArray.getResourceId(2, 0));
    this.mImageIds = getIds(localResources, localTypedArray.getResourceId(3, 0));
    localTypedArray.recycle();
  }

  private int[] getIds(Resources paramResources, int paramInt)
  {
    if (paramInt == 0)
      return null;
    TypedArray localTypedArray = paramResources.obtainTypedArray(paramInt);
    int i = localTypedArray.length();
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; ++j)
      arrayOfInt[j] = localTypedArray.getResourceId(j, 0);
    localTypedArray.recycle();
    return arrayOfInt;
  }

  public void filterUnsupported(List<String> paramList)
  {
    CharSequence[] arrayOfCharSequence = getEntryValues();
    IntArray localIntArray1 = new IntArray();
    IntArray localIntArray2 = new IntArray();
    IntArray localIntArray3 = new IntArray();
    int i = 0;
    int j = arrayOfCharSequence.length;
    while (i < j)
    {
      if (paramList.indexOf(arrayOfCharSequence[i].toString()) >= 0)
      {
        if (this.mIconIds != null)
          localIntArray1.add(this.mIconIds[i]);
        if (this.mLargeIconIds != null)
          localIntArray2.add(this.mLargeIconIds[i]);
        if (this.mImageIds != null)
          localIntArray3.add(this.mImageIds[i]);
      }
      ++i;
    }
    if (this.mIconIds != null)
      this.mIconIds = localIntArray1.toArray(new int[localIntArray1.size()]);
    if (this.mLargeIconIds != null)
      this.mLargeIconIds = localIntArray2.toArray(new int[localIntArray2.size()]);
    if (this.mImageIds != null)
      this.mImageIds = localIntArray3.toArray(new int[localIntArray3.size()]);
    super.filterUnsupported(paramList);
  }

  public int[] getImageIds()
  {
    return this.mImageIds;
  }

  public int[] getLargeIconIds()
  {
    return this.mLargeIconIds;
  }

  public int getSingleIcon()
  {
    return this.mSingleIconId;
  }

  public boolean getUseSingleIcon()
  {
    return this.mUseSingleIcon;
  }

  public void setLargeIconIds(int[] paramArrayOfInt)
  {
    this.mLargeIconIds = paramArrayOfInt;
  }

  public void setUseSingleIcon(boolean paramBoolean)
  {
    this.mUseSingleIcon = paramBoolean;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.IconListPreference
 * JD-Core Version:    0.5.4
 */