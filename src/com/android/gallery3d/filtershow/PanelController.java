package com.android.gallery3d.filtershow;

import android.content.Context;
import android.text.Html;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import com.android.gallery3d.filtershow.filters.ImageFilter;
import com.android.gallery3d.filtershow.filters.ImageFilterBwFilter;
import com.android.gallery3d.filtershow.filters.ImageFilterContrast;
import com.android.gallery3d.filtershow.filters.ImageFilterCurves;
import com.android.gallery3d.filtershow.filters.ImageFilterExposure;
import com.android.gallery3d.filtershow.filters.ImageFilterHue;
import com.android.gallery3d.filtershow.filters.ImageFilterRedEye;
import com.android.gallery3d.filtershow.filters.ImageFilterSaturated;
import com.android.gallery3d.filtershow.filters.ImageFilterShadows;
import com.android.gallery3d.filtershow.filters.ImageFilterSharpen;
import com.android.gallery3d.filtershow.filters.ImageFilterTinyPlanet;
import com.android.gallery3d.filtershow.filters.ImageFilterVibrance;
import com.android.gallery3d.filtershow.filters.ImageFilterVignette;
import com.android.gallery3d.filtershow.filters.ImageFilterWBalance;
import com.android.gallery3d.filtershow.imageshow.ImageCrop;
import com.android.gallery3d.filtershow.imageshow.ImageShow;
import com.android.gallery3d.filtershow.presets.ImagePreset;
import com.android.gallery3d.filtershow.ui.FramedTextButton;
import com.android.gallery3d.filtershow.ui.ImageCurves;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class PanelController
  implements View.OnClickListener
{
  private static int COMPONENT;
  private static int HORIZONTAL_MOVE;
  private static int PANEL = 0;
  private static int VERTICAL_MOVE;
  private FilterShowActivity mActivity = null;
  private ImageShow mCurrentImage = null;
  private View mCurrentPanel = null;
  private final Vector<View> mImageViews = new Vector();
  private ImageShow mMasterImage = null;
  private final HashMap<View, Panel> mPanels = new HashMap();
  private View mRowPanel = null;
  private UtilityPanel mUtilityPanel = null;
  private final HashMap<View, ViewType> mViews = new HashMap();

  static
  {
    COMPONENT = 1;
    VERTICAL_MOVE = 0;
    HORIZONTAL_MOVE = 1;
  }

  private void showCropPopupMenu(FramedTextButton paramFramedTextButton)
  {
    PopupMenu localPopupMenu = new PopupMenu(this.mCurrentImage.getContext(), paramFramedTextButton);
    localPopupMenu.getMenuInflater().inflate(2131886085, localPopupMenu.getMenu());
    localPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(paramFramedTextButton)
    {
      public boolean onMenuItemClick(MenuItem paramMenuItem)
      {
        PanelController.this.mUtilityPanel.setAspectButton(this.val$anchor, paramMenuItem.getItemId());
        return true;
      }
    });
    localPopupMenu.show();
  }

  private void showCurvesPopupMenu(ImageCurves paramImageCurves, FramedTextButton paramFramedTextButton)
  {
    PopupMenu localPopupMenu = new PopupMenu(this.mCurrentImage.getContext(), paramFramedTextButton);
    localPopupMenu.getMenuInflater().inflate(2131886086, localPopupMenu.getMenu());
    localPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(paramImageCurves, paramFramedTextButton)
    {
      public boolean onMenuItemClick(MenuItem paramMenuItem)
      {
        this.val$curves.setChannel(paramMenuItem.getItemId());
        this.val$anchor.setTextFrom(paramMenuItem.getItemId());
        return true;
      }
    });
    localPopupMenu.show();
  }

  public void addComponent(View paramView1, View paramView2)
  {
    Panel localPanel = (Panel)this.mPanels.get(paramView1);
    if (localPanel == null)
      return;
    localPanel.addView(paramView2);
    paramView2.setOnClickListener(this);
    this.mViews.put(paramView2, new ViewType(paramView2, COMPONENT));
  }

  public void addImageView(View paramView)
  {
    this.mImageViews.add(paramView);
    ((ImageShow)paramView).setPanelController(this);
  }

  public void addPanel(View paramView1, View paramView2, int paramInt)
  {
    this.mPanels.put(paramView1, new Panel(paramView1, paramView2, paramInt));
    paramView1.setOnClickListener(this);
    this.mViews.put(paramView1, new ViewType(paramView1, PANEL));
  }

  public void addView(View paramView)
  {
    paramView.setOnClickListener(this);
    this.mViews.put(paramView, new ViewType(paramView, COMPONENT));
  }

  public void ensureFilter(String paramString)
  {
    ImageFilter localImageFilter = getImagePreset().getFilter(paramString);
    if (localImageFilter != null)
    {
      ImagePreset localImagePreset = new ImagePreset(getImagePreset());
      localImagePreset.setHistoryName(paramString);
      this.mMasterImage.setImagePreset(localImagePreset);
      localImageFilter = localImagePreset.getFilter(paramString);
    }
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362127))))
      localImageFilter = setImagePreset(new ImageFilterCurves(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362117))))
      localImageFilter = setImagePreset(new ImageFilterTinyPlanet(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362128))))
      localImageFilter = setImagePreset(new ImageFilterVignette(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362119))))
      localImageFilter = setImagePreset(new ImageFilterSharpen(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362120))))
      localImageFilter = setImagePreset(new ImageFilterContrast(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362122))))
      localImageFilter = setImagePreset(new ImageFilterSaturated(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362123))))
      localImageFilter = setImagePreset(new ImageFilterBwFilter(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362125))))
      localImageFilter = setImagePreset(new ImageFilterHue(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362118))))
      localImageFilter = setImagePreset(new ImageFilterExposure(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362121))))
      localImageFilter = setImagePreset(new ImageFilterVibrance(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362126))))
      localImageFilter = setImagePreset(new ImageFilterShadows(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362129))))
      localImageFilter = setImagePreset(new ImageFilterRedEye(), paramString);
    if ((localImageFilter == null) && (paramString.equalsIgnoreCase(this.mCurrentImage.getContext().getString(2131362124))))
      localImageFilter = setImagePreset(new ImageFilterWBalance(), paramString);
    this.mMasterImage.setCurrentFilter(localImageFilter);
  }

  public ImagePreset getImagePreset()
  {
    return this.mMasterImage.getImagePreset();
  }

  public boolean onBackPressed()
  {
    if ((this.mUtilityPanel == null) || (!this.mUtilityPanel.selected()))
      return true;
    int i = this.mMasterImage.getHistory().undo();
    this.mMasterImage.onItemClick(i);
    showPanel(this.mCurrentPanel);
    this.mCurrentImage.select();
    return false;
  }

  public void onClick(View paramView)
  {
    ViewType localViewType = (ViewType)this.mViews.get(paramView);
    if (localViewType.type() == PANEL)
      showPanel(paramView);
    do
      return;
    while (localViewType.type() != COMPONENT);
    showComponent(paramView);
  }

  public void onNewValue(int paramInt)
  {
    this.mUtilityPanel.onNewValue(paramInt);
  }

  public void resetParameters()
  {
    showPanel(this.mCurrentPanel);
    if (this.mCurrentImage == null)
      return;
    this.mCurrentImage.resetParameter();
    this.mCurrentImage.select();
  }

  public void setActivity(FilterShowActivity paramFilterShowActivity)
  {
    this.mActivity = paramFilterShowActivity;
  }

  public void setCurrentPanel(View paramView)
  {
    showPanel(paramView);
  }

  public ImageFilter setImagePreset(ImageFilter paramImageFilter, String paramString)
  {
    ImagePreset localImagePreset = new ImagePreset(getImagePreset());
    localImagePreset.add(paramImageFilter);
    localImagePreset.setHistoryName(paramString);
    localImagePreset.setIsFx(false);
    this.mMasterImage.setImagePreset(localImagePreset);
    return paramImageFilter;
  }

  public void setMasterImage(ImageShow paramImageShow)
  {
    this.mMasterImage = paramImageShow;
  }

  public void setRowPanel(View paramView)
  {
    this.mRowPanel = paramView;
  }

  public void setUtilityPanel(Context paramContext, View paramView1, View paramView2, View paramView3, View paramView4)
  {
    this.mUtilityPanel = new UtilityPanel(paramContext, paramView1, paramView2, paramView3, paramView4);
  }

  public void showComponent(View paramView)
  {
    if ((this.mUtilityPanel != null) && (!this.mUtilityPanel.selected()))
    {
      ((Panel)this.mPanels.get(this.mCurrentPanel)).unselect(-1, VERTICAL_MOVE).start();
      if (this.mUtilityPanel != null)
        this.mUtilityPanel.select().start();
    }
    if (paramView.getId() == 2131558453)
    {
      showCurvesPopupMenu((ImageCurves)showImageView(2131558445), (FramedTextButton)paramView);
      return;
    }
    if (paramView.getId() == 2131558452)
    {
      showCropPopupMenu((FramedTextButton)paramView);
      return;
    }
    if (this.mCurrentImage != null)
      this.mCurrentImage.unselect();
    this.mUtilityPanel.hideAspectButtons();
    this.mUtilityPanel.hideCurvesButtons();
    switch (paramView.getId())
    {
    case 2131558453:
    case 2131558455:
    case 2131558456:
    case 2131558457:
    case 2131558458:
    case 2131558459:
    case 2131558460:
    case 2131558466:
    case 2131558467:
    default:
    case 2131558468:
    case 2131558461:
    case 2131558462:
    case 2131558463:
    case 2131558464:
    case 2131558471:
    case 2131558476:
    case 2131558475:
    case 2131558472:
    case 2131558478:
    case 2131558479:
    case 2131558469:
    case 2131558477:
    case 2131558470:
    case 2131558474:
    case 2131558473:
    case 2131558465:
    case 2131558452:
    case 2131558454:
    }
    while (true)
    {
      this.mCurrentImage.select();
      return;
      this.mCurrentImage = showImageView(2131558448).setShowControls(true);
      String str17 = this.mCurrentImage.getContext().getString(2131362117);
      this.mUtilityPanel.setEffectName(str17);
      ensureFilter(str17);
      continue;
      this.mCurrentImage = showImageView(2131558441);
      String str16 = this.mCurrentImage.getContext().getString(2131362130);
      this.mUtilityPanel.setEffectName(str16);
      continue;
      this.mCurrentImage = showImageView(2131558442);
      String str15 = this.mCurrentImage.getContext().getString(2131362131);
      this.mUtilityPanel.setEffectName(str15);
      this.mUtilityPanel.setShowParameter(false);
      if ((this.mCurrentImage instanceof ImageCrop) && (this.mUtilityPanel.firstTimeCropDisplayed))
      {
        ((ImageCrop)this.mCurrentImage).applyOriginal();
        this.mUtilityPanel.firstTimeCropDisplayed = false;
      }
      this.mUtilityPanel.showAspectButtons();
      continue;
      this.mCurrentImage = showImageView(2131558443);
      String str14 = this.mCurrentImage.getContext().getString(2131362132);
      this.mUtilityPanel.setEffectName(str14);
      continue;
      this.mCurrentImage = showImageView(2131558444);
      String str13 = this.mCurrentImage.getContext().getString(2131362133);
      this.mUtilityPanel.setEffectName(str13);
      this.mUtilityPanel.setShowParameter(false);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str12 = this.mCurrentImage.getContext().getString(2131362128);
      this.mUtilityPanel.setEffectName(str12);
      ensureFilter(str12);
      continue;
      ImageCurves localImageCurves = (ImageCurves)showImageView(2131558445);
      String str11 = localImageCurves.getContext().getString(2131362127);
      this.mUtilityPanel.setEffectName(str11);
      this.mUtilityPanel.setShowParameter(false);
      this.mUtilityPanel.showCurvesButtons();
      this.mCurrentImage = localImageCurves;
      ensureFilter(str11);
      continue;
      this.mCurrentImage = showImageView(2131558447).setShowControls(true);
      String str10 = this.mCurrentImage.getContext().getString(2131362119);
      this.mUtilityPanel.setEffectName(str10);
      ensureFilter(str10);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str9 = this.mCurrentImage.getContext().getString(2131362120);
      this.mUtilityPanel.setEffectName(str9);
      ensureFilter(str9);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str8 = this.mCurrentImage.getContext().getString(2131362122);
      this.mUtilityPanel.setEffectName(str8);
      ensureFilter(str8);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str7 = this.mCurrentImage.getContext().getString(2131362123);
      this.mUtilityPanel.setEffectName(str7);
      ensureFilter(str7);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(false);
      String str6 = this.mCurrentImage.getContext().getString(2131362124);
      this.mUtilityPanel.setEffectName(str6);
      this.mUtilityPanel.setShowParameter(false);
      ensureFilter(str6);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str5 = this.mCurrentImage.getContext().getString(2131362125);
      this.mUtilityPanel.setEffectName(str5);
      ensureFilter(str5);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str4 = this.mCurrentImage.getContext().getString(2131362118);
      this.mUtilityPanel.setEffectName(str4);
      ensureFilter(str4);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str3 = this.mCurrentImage.getContext().getString(2131362121);
      this.mUtilityPanel.setEffectName(str3);
      ensureFilter(str3);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str2 = this.mCurrentImage.getContext().getString(2131362126);
      this.mUtilityPanel.setEffectName(str2);
      ensureFilter(str2);
      continue;
      this.mCurrentImage = showImageView(2131558440).setShowControls(true);
      String str1 = this.mCurrentImage.getContext().getString(2131362129);
      this.mUtilityPanel.setEffectName(str1);
      ensureFilter(str1);
      continue;
      this.mUtilityPanel.showAspectButtons();
      continue;
      if (this.mMasterImage.getCurrentFilter() instanceof ImageFilterTinyPlanet)
        this.mActivity.saveImage();
      showPanel(this.mCurrentPanel);
    }
  }

  public void showDefaultImageView()
  {
    showImageView(2131558440).setShowControls(false);
    this.mMasterImage.setCurrentFilter(null);
  }

  public ImageShow showImageView(int paramInt)
  {
    ImageShow localImageShow = null;
    Iterator localIterator = this.mImageViews.iterator();
    while (localIterator.hasNext())
    {
      View localView = (View)localIterator.next();
      if (localView.getId() == paramInt)
      {
        localView.setVisibility(0);
        localImageShow = (ImageShow)localView;
      }
      localView.setVisibility(8);
    }
    return localImageShow;
  }

  public void showPanel(View paramView)
  {
    paramView.setVisibility(0);
    Panel localPanel1 = (Panel)this.mPanels.get(this.mCurrentPanel);
    UtilityPanel localUtilityPanel = this.mUtilityPanel;
    int i = 0;
    if (localUtilityPanel != null)
    {
      boolean bool = this.mUtilityPanel.selected();
      i = 0;
      if (bool)
      {
        ViewPropertyAnimator localViewPropertyAnimator = this.mUtilityPanel.unselect();
        i = 1;
        localViewPropertyAnimator.start();
        if (this.mCurrentPanel == paramView)
        {
          localPanel1.select(-1, VERTICAL_MOVE).start();
          showDefaultImageView();
        }
      }
    }
    if (this.mCurrentPanel == paramView)
      return;
    Panel localPanel2 = (Panel)this.mPanels.get(paramView);
    if (i == 0)
    {
      int j = -1;
      if (localPanel1 != null)
        j = localPanel1.getPosition();
      localPanel2.select(j, HORIZONTAL_MOVE).start();
      if (localPanel1 != null)
        localPanel1.unselect(localPanel2.getPosition(), HORIZONTAL_MOVE).start();
    }
    while (true)
    {
      showDefaultImageView();
      this.mCurrentPanel = paramView;
      return;
      localPanel2.select(-1, VERTICAL_MOVE).start();
    }
  }

  class Panel
  {
    private final View mContainer;
    private int mPosition = 0;
    private final Vector<View> mSubviews = new Vector();
    private final View mView;

    public Panel(View paramView1, View paramInt, int arg4)
    {
      this.mView = paramView1;
      this.mContainer = paramInt;
      int i;
      this.mPosition = i;
    }

    public void addView(View paramView)
    {
      this.mSubviews.add(paramView);
    }

    public int getPosition()
    {
      return this.mPosition;
    }

    public ViewPropertyAnimator select(int paramInt1, int paramInt2)
    {
      this.mView.setSelected(true);
      this.mContainer.setVisibility(0);
      this.mContainer.setX(0.0F);
      this.mContainer.setY(0.0F);
      ViewPropertyAnimator localViewPropertyAnimator = this.mContainer.animate();
      int i = PanelController.this.mRowPanel.getWidth();
      int j = PanelController.this.mRowPanel.getHeight();
      if (paramInt2 == PanelController.HORIZONTAL_MOVE)
        if (paramInt1 < this.mPosition)
        {
          this.mContainer.setX(i);
          label89: localViewPropertyAnimator.x(0.0F);
        }
      while (true)
      {
        localViewPropertyAnimator.setDuration(200L).withLayer();
        return localViewPropertyAnimator;
        this.mContainer.setX(-i);
        break label89:
        if (paramInt2 != PanelController.VERTICAL_MOVE)
          continue;
        this.mContainer.setY(j);
        localViewPropertyAnimator.y(0.0F);
      }
    }

    public ViewPropertyAnimator unselect(int paramInt1, int paramInt2)
    {
      ViewPropertyAnimator localViewPropertyAnimator = this.mContainer.animate();
      this.mView.setSelected(false);
      this.mContainer.setX(0.0F);
      this.mContainer.setY(0.0F);
      int i = PanelController.this.mRowPanel.getWidth();
      int j = PanelController.this.mRowPanel.getHeight();
      int k;
      if (paramInt2 == PanelController.HORIZONTAL_MOVE)
        if (paramInt1 > this.mPosition)
        {
          k = -i;
          label76: localViewPropertyAnimator.x(k);
        }
      while (true)
      {
        localViewPropertyAnimator.setDuration(200L).withLayer().withEndAction(new Runnable()
        {
          public void run()
          {
            PanelController.Panel.this.mContainer.setVisibility(8);
          }
        });
        return localViewPropertyAnimator;
        k = i;
        break label76:
        if (paramInt2 != PanelController.VERTICAL_MOVE)
          continue;
        localViewPropertyAnimator.y(j);
      }
    }
  }

  class UtilityPanel
  {
    boolean firstTimeCropDisplayed = true;
    private View mAspectButton = null;
    private final Context mContext;
    private View mCurvesButton = null;
    private String mEffectName = null;
    private int mParameterValue = 0;
    private boolean mSelected = false;
    private boolean mShowParameterValue = false;
    private final TextView mTextView;
    private final View mView;

    public UtilityPanel(Context paramView1, View paramView2, View paramView3, View paramView4, View arg6)
    {
      this.mContext = paramView1;
      this.mView = paramView2;
      this.mTextView = ((TextView)paramView3);
      this.mAspectButton = paramView4;
      Object localObject;
      this.mCurvesButton = localObject;
    }

    public void hideAspectButtons()
    {
      if (this.mAspectButton == null)
        return;
      this.mAspectButton.setVisibility(8);
    }

    public void hideCurvesButtons()
    {
      if (this.mCurvesButton == null)
        return;
      this.mCurvesButton.setVisibility(8);
    }

    public void onNewValue(int paramInt)
    {
      this.mParameterValue = paramInt;
      updateText();
    }

    public ViewPropertyAnimator select()
    {
      this.mView.setVisibility(0);
      int i = PanelController.this.mRowPanel.getHeight();
      this.mView.setX(0.0F);
      this.mView.setY(-i);
      updateText();
      ViewPropertyAnimator localViewPropertyAnimator = this.mView.animate();
      localViewPropertyAnimator.y(0.0F);
      localViewPropertyAnimator.setDuration(200L).withLayer();
      this.mSelected = true;
      return localViewPropertyAnimator;
    }

    public boolean selected()
    {
      return this.mSelected;
    }

    public void setAspectButton(FramedTextButton paramFramedTextButton, int paramInt)
    {
      ImageCrop localImageCrop = (ImageCrop)PanelController.this.mCurrentImage;
      switch (paramInt)
      {
      case 2131558643:
      case 2131558648:
      default:
      case 2131558642:
      case 2131558644:
      case 2131558645:
      case 2131558646:
      case 2131558647:
      case 2131558649:
      case 2131558650:
      }
      while (true)
      {
        localImageCrop.invalidate();
        return;
        paramFramedTextButton.setText(this.mContext.getString(2131362108));
        localImageCrop.apply(1.0F, 1.0F);
        continue;
        paramFramedTextButton.setText(this.mContext.getString(2131362109));
        localImageCrop.apply(4.0F, 3.0F);
        continue;
        paramFramedTextButton.setText(this.mContext.getString(2131362110));
        localImageCrop.apply(3.0F, 4.0F);
        continue;
        paramFramedTextButton.setText(this.mContext.getString(2131362112));
        localImageCrop.apply(5.0F, 7.0F);
        continue;
        paramFramedTextButton.setText(this.mContext.getString(2131362113));
        localImageCrop.apply(7.0F, 5.0F);
        continue;
        paramFramedTextButton.setText(this.mContext.getString(2131362115));
        localImageCrop.applyClear();
        continue;
        paramFramedTextButton.setText(this.mContext.getString(2131362116));
        localImageCrop.applyOriginal();
      }
    }

    public void setEffectName(String paramString)
    {
      this.mEffectName = paramString;
      setShowParameter(true);
    }

    public void setShowParameter(boolean paramBoolean)
    {
      this.mShowParameterValue = paramBoolean;
      updateText();
    }

    public void showAspectButtons()
    {
      if (this.mAspectButton == null)
        return;
      this.mAspectButton.setVisibility(0);
    }

    public void showCurvesButtons()
    {
      if (this.mCurvesButton == null)
        return;
      this.mCurvesButton.setVisibility(0);
    }

    public ViewPropertyAnimator unselect()
    {
      ViewPropertyAnimator localViewPropertyAnimator = this.mView.animate();
      this.mView.setX(0.0F);
      this.mView.setY(0.0F);
      localViewPropertyAnimator.y(-PanelController.this.mRowPanel.getHeight());
      localViewPropertyAnimator.setDuration(200L).withLayer().withEndAction(new Runnable()
      {
        public void run()
        {
          PanelController.UtilityPanel.this.mView.setVisibility(8);
        }
      });
      this.mSelected = false;
      return localViewPropertyAnimator;
    }

    public void updateText()
    {
      String str = this.mContext.getString(2131362105);
      if (this.mShowParameterValue)
      {
        this.mTextView.setText(Html.fromHtml(str + " " + this.mEffectName + " " + this.mParameterValue));
        return;
      }
      this.mTextView.setText(Html.fromHtml(str + " " + this.mEffectName));
    }
  }

  class ViewType
  {
    private final int mType;
    private final View mView;

    public ViewType(View paramInt, int arg3)
    {
      this.mView = paramInt;
      int i;
      this.mType = i;
    }

    public int type()
    {
      return this.mType;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.PanelController
 * JD-Core Version:    0.5.4
 */