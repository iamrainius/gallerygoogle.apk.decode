package com.android.gallery3d.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaDetails.FlashState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class DialogDetailsView
  implements DetailsHelper.DetailsViewContainer
{
  private final AbstractGalleryActivity mActivity;
  private DetailsAdapter mAdapter;
  private MediaDetails mDetails;
  private Dialog mDialog;
  private int mIndex;
  private DetailsHelper.CloseListener mListener;
  private final DetailsHelper.DetailsSource mSource;

  public DialogDetailsView(AbstractGalleryActivity paramAbstractGalleryActivity, DetailsHelper.DetailsSource paramDetailsSource)
  {
    this.mActivity = paramAbstractGalleryActivity;
    this.mSource = paramDetailsSource;
  }

  private void setDetails(MediaDetails paramMediaDetails)
  {
    this.mAdapter = new DetailsAdapter(paramMediaDetails);
    String str1 = this.mActivity.getAndroidContext().getString(2131362222);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Integer.valueOf(1 + this.mIndex);
    arrayOfObject[1] = Integer.valueOf(this.mSource.size());
    String str2 = String.format(str1, arrayOfObject);
    ListView localListView = (ListView)LayoutInflater.from(this.mActivity.getAndroidContext()).inflate(2130968591, null, false);
    localListView.setAdapter(this.mAdapter);
    this.mDialog = new AlertDialog.Builder(this.mActivity).setView(localListView).setTitle(str2).setPositiveButton(2131362223, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        DialogDetailsView.this.mDialog.dismiss();
      }
    }).create();
    this.mDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramDialogInterface)
      {
        if (DialogDetailsView.this.mListener == null)
          return;
        DialogDetailsView.this.mListener.onClose();
      }
    });
  }

  public void hide()
  {
    this.mDialog.hide();
  }

  public void reloadDetails()
  {
    int i = this.mSource.setIndex();
    if (i == -1);
    MediaDetails localMediaDetails;
    do
    {
      return;
      localMediaDetails = this.mSource.getDetails();
    }
    while ((localMediaDetails == null) || ((this.mIndex == i) && (this.mDetails == localMediaDetails)));
    this.mIndex = i;
    this.mDetails = localMediaDetails;
    setDetails(localMediaDetails);
  }

  public void setCloseListener(DetailsHelper.CloseListener paramCloseListener)
  {
    this.mListener = paramCloseListener;
  }

  public void show()
  {
    reloadDetails();
    this.mDialog.show();
  }

  private class DetailsAdapter extends BaseAdapter
    implements DetailsAddressResolver.AddressResolvingListener
  {
    private final ArrayList<String> mItems;
    private int mLocationIndex;

    public DetailsAdapter(MediaDetails arg2)
    {
      Context localContext = DialogDetailsView.this.mActivity.getAndroidContext();
      MediaDetails localMediaDetails;
      this.mItems = new ArrayList(localMediaDetails.size());
      this.mLocationIndex = -1;
      setDetails(localContext, localMediaDetails);
    }

    private void setDetails(Context paramContext, MediaDetails paramMediaDetails)
    {
      Iterator localIterator = paramMediaDetails.iterator();
      if (!localIterator.hasNext())
        label5: return;
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1;
      label143: int j;
      Object[] arrayOfObject3;
      switch (((Integer)localEntry.getKey()).intValue())
      {
      default:
        Object localObject = localEntry.getValue();
        if (localObject == null)
        {
          Object[] arrayOfObject5 = new Object[1];
          arrayOfObject5[0] = DetailsHelper.getDetailsName(paramContext, ((Integer)localEntry.getKey()).intValue());
          Utils.fail("%s's value is Null", arrayOfObject5);
        }
        str1 = localObject.toString();
        j = ((Integer)localEntry.getKey()).intValue();
        if (paramMediaDetails.hasUnit(j))
        {
          arrayOfObject3 = new Object[3];
          arrayOfObject3[0] = DetailsHelper.getDetailsName(paramContext, j);
          arrayOfObject3[1] = str1;
          arrayOfObject3[2] = paramContext.getString(paramMediaDetails.getUnit(j));
        }
      case 4:
      case 10:
      case 104:
      case 102:
      case 107:
      }
      Object[] arrayOfObject2;
      for (String str2 = String.format("%s: %s %s", arrayOfObject3); ; str2 = String.format("%s: %s", arrayOfObject2))
      {
        this.mItems.add(str2);
        break label5:
        double[] arrayOfDouble = (double[])(double[])localEntry.getValue();
        this.mLocationIndex = this.mItems.size();
        str1 = DetailsHelper.resolveAddress(DialogDetailsView.this.mActivity, arrayOfDouble, this);
        break label143:
        str1 = Formatter.formatFileSize(paramContext, ((Long)localEntry.getValue()).longValue());
        break label143:
        if ("1".equals(localEntry.getValue()));
        for (str1 = paramContext.getString(2131362278); ; str1 = paramContext.getString(2131362279))
          break label143:
        if (((MediaDetails.FlashState)localEntry.getValue()).isFlashFired())
          str1 = paramContext.getString(2131362280);
        str1 = paramContext.getString(2131362281);
        break label143:
        double d1 = Double.valueOf((String)localEntry.getValue()).doubleValue();
        if (d1 < 1.0D)
        {
          Object[] arrayOfObject4 = new Object[1];
          arrayOfObject4[0] = Integer.valueOf((int)(0.5D + 1.0D / d1));
          str1 = String.format("1/%d", arrayOfObject4);
        }
        int i = (int)d1;
        double d2 = d1 - i;
        str1 = String.valueOf(i) + "''";
        if (d2 > 0.0001D);
        StringBuilder localStringBuilder = new StringBuilder().append(str1);
        Object[] arrayOfObject1 = new Object[1];
        arrayOfObject1[0] = Integer.valueOf((int)(0.5D + 1.0D / d2));
        str1 = String.format(" 1/%d", arrayOfObject1);
        break label143:
        arrayOfObject2 = new Object[2];
        arrayOfObject2[0] = DetailsHelper.getDetailsName(paramContext, j);
        arrayOfObject2[1] = str1;
      }
    }

    public boolean areAllItemsEnabled()
    {
      return false;
    }

    public int getCount()
    {
      return this.mItems.size();
    }

    public Object getItem(int paramInt)
    {
      return DialogDetailsView.this.mDetails.getDetail(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null);
      for (TextView localTextView = (TextView)LayoutInflater.from(DialogDetailsView.this.mActivity.getAndroidContext()).inflate(2130968590, paramViewGroup, false); ; localTextView = (TextView)paramView)
      {
        localTextView.setText((CharSequence)this.mItems.get(paramInt));
        return localTextView;
      }
    }

    public boolean isEnabled(int paramInt)
    {
      return false;
    }

    public void onAddressAvailable(String paramString)
    {
      this.mItems.set(this.mLocationIndex, paramString);
      notifyDataSetChanged();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.DialogDetailsView
 * JD-Core Version:    0.5.4
 */