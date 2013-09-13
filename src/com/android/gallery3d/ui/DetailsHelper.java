package com.android.gallery3d.ui;

import android.content.Context;
import android.view.View.MeasureSpec;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.data.MediaDetails;

public class DetailsHelper
{
  private static DetailsAddressResolver sAddressResolver;
  private DetailsViewContainer mContainer;

  public DetailsHelper(AbstractGalleryActivity paramAbstractGalleryActivity, GLView paramGLView, DetailsSource paramDetailsSource)
  {
    this.mContainer = new DialogDetailsView(paramAbstractGalleryActivity, paramDetailsSource);
  }

  public static String getDetailsName(Context paramContext, int paramInt)
  {
    switch (paramInt)
    {
    default:
      return "Unknown key" + paramInt;
    case 1:
      return paramContext.getString(2131362258);
    case 2:
      return paramContext.getString(2131362259);
    case 3:
      return paramContext.getString(2131362260);
    case 4:
      return paramContext.getString(2131362261);
    case 200:
      return paramContext.getString(2131362262);
    case 5:
      return paramContext.getString(2131362263);
    case 6:
      return paramContext.getString(2131362264);
    case 7:
      return paramContext.getString(2131362265);
    case 8:
      return paramContext.getString(2131362266);
    case 9:
      return paramContext.getString(2131362267);
    case 10:
      return paramContext.getString(2131362268);
    case 100:
      return paramContext.getString(2131362269);
    case 101:
      return paramContext.getString(2131362270);
    case 102:
      return paramContext.getString(2131362271);
    case 105:
      return paramContext.getString(2131362272);
    case 103:
      return paramContext.getString(2131362273);
    case 104:
      return paramContext.getString(2131362274);
    case 107:
      return paramContext.getString(2131362275);
    case 108:
    }
    return paramContext.getString(2131362276);
  }

  public static void pause()
  {
    if (sAddressResolver == null)
      return;
    sAddressResolver.cancel();
  }

  public static String resolveAddress(AbstractGalleryActivity paramAbstractGalleryActivity, double[] paramArrayOfDouble, DetailsAddressResolver.AddressResolvingListener paramAddressResolvingListener)
  {
    if (sAddressResolver == null)
      sAddressResolver = new DetailsAddressResolver(paramAbstractGalleryActivity);
    while (true)
    {
      return sAddressResolver.resolveAddress(paramArrayOfDouble, paramAddressResolvingListener);
      sAddressResolver.cancel();
    }
  }

  public void hide()
  {
    this.mContainer.hide();
  }

  public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!this.mContainer instanceof GLView)
      return;
    GLView localGLView = (GLView)this.mContainer;
    localGLView.measure(0, View.MeasureSpec.makeMeasureSpec(paramInt4 - paramInt2, -2147483648));
    localGLView.layout(0, paramInt2, localGLView.getMeasuredWidth(), paramInt2 + localGLView.getMeasuredHeight());
  }

  public void reloadDetails()
  {
    this.mContainer.reloadDetails();
  }

  public void setCloseListener(CloseListener paramCloseListener)
  {
    this.mContainer.setCloseListener(paramCloseListener);
  }

  public void show()
  {
    this.mContainer.show();
  }

  public static abstract interface CloseListener
  {
    public abstract void onClose();
  }

  public static abstract interface DetailsSource
  {
    public abstract MediaDetails getDetails();

    public abstract int setIndex();

    public abstract int size();
  }

  public static abstract interface DetailsViewContainer
  {
    public abstract void hide();

    public abstract void reloadDetails();

    public abstract void setCloseListener(DetailsHelper.CloseListener paramCloseListener);

    public abstract void show();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.DetailsHelper
 * JD-Core Version:    0.5.4
 */