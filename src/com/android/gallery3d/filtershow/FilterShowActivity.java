package com.android.gallery3d.filtershow;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.widget.Toast;
import com.android.gallery3d.data.LocalAlbum;
import com.android.gallery3d.filtershow.cache.ImageLoader;
import com.android.gallery3d.filtershow.filters.ImageFilter;
import com.android.gallery3d.filtershow.filters.ImageFilterBorder;
import com.android.gallery3d.filtershow.filters.ImageFilterBwFilter;
import com.android.gallery3d.filtershow.filters.ImageFilterContrast;
import com.android.gallery3d.filtershow.filters.ImageFilterExposure;
import com.android.gallery3d.filtershow.filters.ImageFilterFx;
import com.android.gallery3d.filtershow.filters.ImageFilterHue;
import com.android.gallery3d.filtershow.filters.ImageFilterParametricBorder;
import com.android.gallery3d.filtershow.filters.ImageFilterRS;
import com.android.gallery3d.filtershow.filters.ImageFilterSaturated;
import com.android.gallery3d.filtershow.filters.ImageFilterShadows;
import com.android.gallery3d.filtershow.filters.ImageFilterTinyPlanet;
import com.android.gallery3d.filtershow.filters.ImageFilterVibrance;
import com.android.gallery3d.filtershow.filters.ImageFilterVignette;
import com.android.gallery3d.filtershow.filters.ImageFilterWBalance;
import com.android.gallery3d.filtershow.imageshow.ImageBorder;
import com.android.gallery3d.filtershow.imageshow.ImageCrop;
import com.android.gallery3d.filtershow.imageshow.ImageFlip;
import com.android.gallery3d.filtershow.imageshow.ImageRotate;
import com.android.gallery3d.filtershow.imageshow.ImageShow;
import com.android.gallery3d.filtershow.imageshow.ImageSmallBorder;
import com.android.gallery3d.filtershow.imageshow.ImageSmallFilter;
import com.android.gallery3d.filtershow.imageshow.ImageStraighten;
import com.android.gallery3d.filtershow.imageshow.ImageTinyPlanet;
import com.android.gallery3d.filtershow.imageshow.ImageWithIcon;
import com.android.gallery3d.filtershow.imageshow.ImageZoom;
import com.android.gallery3d.filtershow.presets.ImagePreset;
import com.android.gallery3d.filtershow.provider.SharedImageProvider;
import com.android.gallery3d.filtershow.tools.SaveCopyTask;
import com.android.gallery3d.filtershow.ui.FramedTextButton;
import com.android.gallery3d.filtershow.ui.ImageButtonTitle;
import com.android.gallery3d.filtershow.ui.ImageCurves;
import com.android.gallery3d.filtershow.ui.Spline;
import com.android.gallery3d.util.GalleryUtils;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Vector;

@TargetApi(16)
public class FilterShowActivity extends Activity
  implements AdapterView.OnItemClickListener, ShareActionProvider.OnShareTargetSelectedListener
{
  private static int mImageBorderSize = 4;
  private ImageButton mBorderButton = null;
  private final Vector<ImageButton> mBottomPanelButtons = new Vector();
  private ImageButton mColorsButton = null;
  private ImageSmallFilter mCurrentImageSmallFilter = null;
  private ImageButton mFxButton = null;
  private ImageButton mGeometryButton = null;
  private ImageBorder mImageBorders = null;
  private ImageCrop mImageCrop = null;
  private ImageCurves mImageCurves = null;
  private ImageFlip mImageFlip = null;
  private ImageLoader mImageLoader = null;
  private ImageRotate mImageRotate = null;
  private ImageShow mImageShow = null;
  private ImageStraighten mImageStraighten = null;
  private ImageTinyPlanet mImageTinyPlanet = null;
  private final Vector<ImageShow> mImageViews = new Vector();
  private ImageZoom mImageZoom = null;
  private View mListBorders = null;
  private View mListColors = null;
  private View mListFilterButtons = null;
  private View mListFx = null;
  private View mListGeometry = null;
  private final Vector<View> mListViews = new Vector();
  private LoadBitmapTask mLoadBitmapTask;
  private final PanelController mPanelController = new PanelController();
  private WeakReference<ProgressDialog> mSavingProgressDialog;
  private ShareActionProvider mShareActionProvider;
  private File mSharedOutputFile = null;
  private boolean mSharingImage = false;
  private boolean mShowingHistoryPanel = false;
  private boolean mShowingImageStatePanel = false;

  static
  {
    System.loadLibrary("jni_filtershow_filters");
  }

  private View.OnClickListener createOnClickResetOperationsButton()
  {
    return new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        FilterShowActivity.this.resetHistory();
      }
    };
  }

  private void fillListBorders(LinearLayout paramLinearLayout)
  {
    ImageFilter[] arrayOfImageFilter = new ImageFilter[7];
    arrayOfImageFilter[0] = new ImageFilterBorder(null);
    arrayOfImageFilter[1] = new ImageFilterBorder(getResources().getDrawable(2130837583));
    arrayOfImageFilter[2] = new ImageFilterBorder(getResources().getDrawable(2130837585));
    arrayOfImageFilter[3] = new ImageFilterParametricBorder(-16777216, mImageBorderSize, 0);
    arrayOfImageFilter[4] = new ImageFilterParametricBorder(-16777216, mImageBorderSize, mImageBorderSize);
    arrayOfImageFilter[5] = new ImageFilterParametricBorder(-1, mImageBorderSize, 0);
    arrayOfImageFilter[6] = new ImageFilterParametricBorder(-1, mImageBorderSize, mImageBorderSize);
    for (int i = 0; i < 7; ++i)
    {
      ImageSmallBorder localImageSmallBorder = new ImageSmallBorder(this);
      arrayOfImageFilter[i].setName(getString(2131362091));
      localImageSmallBorder.setImageFilter(arrayOfImageFilter[i]);
      localImageSmallBorder.setController(this);
      localImageSmallBorder.setBorder(true);
      localImageSmallBorder.setImageLoader(this.mImageLoader);
      localImageSmallBorder.setShowTitle(false);
      paramLinearLayout.addView(localImageSmallBorder);
    }
  }

  private void fillListImages(LinearLayout paramLinearLayout)
  {
    ImageFilterFx[] arrayOfImageFilterFx = new ImageFilterFx[18];
    int[] arrayOfInt1 = { 2130837617, 2130837612, 2130837616, 2130837614, 2130837613, 2130837619, 2130837615, 2130837620, 2130837618 };
    int[] arrayOfInt2 = { 2131362288, 2131362283, 2131362287, 2131362285, 2131362284, 2131362290, 2131362286, 2131362291, 2131362289 };
    ImagePreset localImagePreset = new ImagePreset(getString(2131362102));
    localImagePreset.setImageLoader(this.mImageLoader);
    ImageSmallFilter localImageSmallFilter1 = new ImageSmallFilter(this);
    localImageSmallFilter1.setSelected(true);
    this.mCurrentImageSmallFilter = localImageSmallFilter1;
    localImageSmallFilter1.setImageFilter(new ImageFilterFx(null, getString(2131362134)));
    localImageSmallFilter1.setController(this);
    localImageSmallFilter1.setImageLoader(this.mImageLoader);
    paramLinearLayout.addView(localImageSmallFilter1);
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inScaled = false;
    int i = 0;
    int l;
    int i1;
    for (int j = 0; ; j = i1)
    {
      int k = arrayOfInt1.length;
      l = 0;
      if (i >= k)
        break;
      Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), arrayOfInt1[i], localOptions);
      i1 = j + 1;
      arrayOfImageFilterFx[j] = new ImageFilterFx(localBitmap, getString(arrayOfInt2[i]));
      ++i;
    }
    while (l < j)
    {
      ImageSmallFilter localImageSmallFilter2 = new ImageSmallFilter(this);
      localImageSmallFilter2.setImageFilter(arrayOfImageFilterFx[l]);
      localImageSmallFilter2.setController(this);
      localImageSmallFilter2.setImageLoader(this.mImageLoader);
      paramLinearLayout.addView(localImageSmallFilter2);
      ++l;
    }
    this.mImageShow.setImagePreset(localImagePreset);
  }

  private Intent getDefaultShareIntent()
  {
    Intent localIntent = new Intent("android.intent.action.SEND");
    localIntent.addFlags(524288);
    localIntent.addFlags(1);
    localIntent.setType("image/jpeg");
    this.mSharedOutputFile = SaveCopyTask.getNewFile(this, this.mImageLoader.getUri());
    localIntent.putExtra("android.intent.extra.STREAM", Uri.withAppendedPath(SharedImageProvider.CONTENT_URI, Uri.encode(this.mSharedOutputFile.getAbsolutePath())));
    return localIntent;
  }

  private int getScreenImageSize()
  {
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    Display localDisplay = getWindowManager().getDefaultDisplay();
    Point localPoint = new Point();
    localDisplay.getSize(localPoint);
    localDisplay.getMetrics(localDisplayMetrics);
    return 133 * Math.min(localPoint.x, localPoint.y) / localDisplayMetrics.densityDpi;
  }

  private void hideSavingProgress()
  {
    if (this.mSavingProgressDialog == null)
      return;
    ProgressDialog localProgressDialog = (ProgressDialog)this.mSavingProgressDialog.get();
    if (localProgressDialog == null)
      return;
    localProgressDialog.dismiss();
  }

  private void resetHistory()
  {
    HistoryAdapter localHistoryAdapter = this.mImageShow.getHistory();
    localHistoryAdapter.reset();
    ImagePreset localImagePreset = new ImagePreset((ImagePreset)localHistoryAdapter.getItem(0));
    this.mImageShow.setImagePreset(localImagePreset);
    this.mPanelController.resetParameters();
    invalidateViews();
  }

  private void showSavingProgress(String paramString)
  {
    if (this.mSavingProgressDialog != null)
    {
      ProgressDialog localProgressDialog = (ProgressDialog)this.mSavingProgressDialog.get();
      if (localProgressDialog != null)
      {
        localProgressDialog.show();
        return;
      }
    }
    if (paramString == null);
    for (String str = getString(2131362196); ; str = getString(2131362197, new Object[] { paramString }))
    {
      this.mSavingProgressDialog = new WeakReference(ProgressDialog.show(this, "", str, true, false));
      return;
    }
  }

  private void startLoadBitmap(Uri paramUri)
  {
    View localView = findViewById(2131558468);
    if (localView != null)
      localView.setVisibility(8);
    this.mLoadBitmapTask = new LoadBitmapTask(localView);
    this.mLoadBitmapTask.execute(new Uri[] { paramUri });
  }

  private void toggleImageStatePanel()
  {
    View localView1 = findViewById(2131558439);
    View localView2 = findViewById(2131558437);
    if (this.mShowingHistoryPanel)
    {
      findViewById(2131558484).setVisibility(4);
      this.mShowingHistoryPanel = false;
    }
    int i = translateMainPanel(localView2);
    if (!this.mShowingImageStatePanel)
    {
      this.mShowingImageStatePanel = true;
      localView1.animate().setDuration(200L).x(i).withLayer().withEndAction(new Runnable(localView2)
      {
        public void run()
        {
          this.val$viewList.setAlpha(0.0F);
          this.val$viewList.setVisibility(0);
          this.val$viewList.animate().setDuration(100L).alpha(1.0F).start();
        }
      }).start();
    }
    while (true)
    {
      invalidateOptionsMenu();
      return;
      this.mShowingImageStatePanel = false;
      localView2.setVisibility(4);
      localView1.animate().setDuration(200L).x(0.0F).withLayer().start();
    }
  }

  private int translateMainPanel(View paramView)
  {
    int i = paramView.getWidth();
    if (findViewById(2131558436).getWidth() - this.mImageShow.getDisplayedImageBounds().width() - i < 0)
      return -i;
    return 0;
  }

  public void cannotLoadImage()
  {
    Toast.makeText(this, getString(2131362088), 0).show();
    finish();
  }

  public void completeSaveImage(Uri paramUri)
  {
    if ((this.mSharingImage) && (this.mSharedOutputFile != null))
    {
      Uri localUri = Uri.withAppendedPath(SharedImageProvider.CONTENT_URI, Uri.encode(this.mSharedOutputFile.getAbsolutePath()));
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("prepare", Boolean.valueOf(false));
      getContentResolver().insert(localUri, localContentValues);
    }
    setResult(-1, new Intent().setData(paramUri));
    hideSavingProgress();
    finish();
  }

  public float getPixelsFromDip(float paramFloat)
  {
    return TypedValue.applyDimension(1, paramFloat, getResources().getDisplayMetrics());
  }

  public void invalidateViews()
  {
    Iterator localIterator = this.mImageViews.iterator();
    while (localIterator.hasNext())
    {
      ImageShow localImageShow = (ImageShow)localIterator.next();
      localImageShow.invalidate();
      localImageShow.updateImage();
    }
  }

  public boolean isShowingHistoryPanel()
  {
    return this.mShowingHistoryPanel;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    Log.v("FilterShowActivity", "onActivityResult");
    if ((paramInt2 != -1) || (paramInt1 != 1))
      return;
    startLoadBitmap(paramIntent.getData());
  }

  public void onBackPressed()
  {
    if (!this.mPanelController.onBackPressed())
      return;
    saveImage();
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (!this.mShowingHistoryPanel)
      return;
    toggleHistoryPanel();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ImageFilterRS.setRenderScriptContext(this);
    ImageShow.setDefaultBackgroundColor(getResources().getColor(2131296298));
    ImageSmallFilter.setDefaultBackgroundColor(getResources().getColor(2131296300));
    FramedTextButton.setTextSize((int)getPixelsFromDip(14.0F));
    ImageShow.setTextSize((int)getPixelsFromDip(12.0F));
    ImageShow.setTextPadding((int)getPixelsFromDip(10.0F));
    ImageShow.setOriginalTextMargin((int)getPixelsFromDip(4.0F));
    ImageShow.setOriginalTextSize((int)getPixelsFromDip(18.0F));
    ImageShow.setOriginalText(getResources().getString(2131362089));
    ImageButtonTitle.setTextSize((int)getPixelsFromDip(12.0F));
    ImageButtonTitle.setTextPadding((int)getPixelsFromDip(10.0F));
    ImageSmallFilter.setMargin((int)getPixelsFromDip(3.0F));
    ImageSmallFilter.setTextMargin((int)getPixelsFromDip(4.0F));
    Spline.setCurveHandle(getResources().getDrawable(2130837567), (int)getResources().getDimension(2131624049));
    Spline.setCurveWidth((int)getPixelsFromDip(3.0F));
    setContentView(2130968597);
    ActionBar localActionBar = getActionBar();
    localActionBar.setDisplayOptions(16);
    localActionBar.setCustomView(2130968596);
    localActionBar.getCustomView().setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        FilterShowActivity.this.saveImage();
      }
    });
    this.mImageLoader = new ImageLoader(this, getApplicationContext());
    LinearLayout localLinearLayout1 = (LinearLayout)findViewById(2131558456);
    LinearLayout localLinearLayout2 = (LinearLayout)findViewById(2131558458);
    LinearLayout localLinearLayout3 = (LinearLayout)findViewById(2131558467);
    this.mImageShow = ((ImageShow)findViewById(2131558440));
    this.mImageCurves = ((ImageCurves)findViewById(2131558445));
    this.mImageBorders = ((ImageBorder)findViewById(2131558446));
    this.mImageStraighten = ((ImageStraighten)findViewById(2131558441));
    this.mImageZoom = ((ImageZoom)findViewById(2131558447));
    this.mImageCrop = ((ImageCrop)findViewById(2131558442));
    this.mImageRotate = ((ImageRotate)findViewById(2131558443));
    this.mImageFlip = ((ImageFlip)findViewById(2131558444));
    this.mImageTinyPlanet = ((ImageTinyPlanet)findViewById(2131558448));
    ImageCrop.setTouchTolerance((int)getPixelsFromDip(25.0F));
    this.mImageViews.add(this.mImageShow);
    this.mImageViews.add(this.mImageCurves);
    this.mImageViews.add(this.mImageBorders);
    this.mImageViews.add(this.mImageStraighten);
    this.mImageViews.add(this.mImageZoom);
    this.mImageViews.add(this.mImageCrop);
    this.mImageViews.add(this.mImageRotate);
    this.mImageViews.add(this.mImageFlip);
    this.mImageViews.add(this.mImageTinyPlanet);
    this.mListFx = findViewById(2131558455);
    this.mListBorders = findViewById(2131558457);
    this.mListGeometry = findViewById(2131558459);
    this.mListFilterButtons = findViewById(2131558451);
    this.mListColors = findViewById(2131558466);
    this.mListViews.add(this.mListFx);
    this.mListViews.add(this.mListBorders);
    this.mListViews.add(this.mListGeometry);
    this.mListViews.add(this.mListFilterButtons);
    this.mListViews.add(this.mListColors);
    this.mFxButton = ((ImageButton)findViewById(2131558480));
    this.mBorderButton = ((ImageButton)findViewById(2131558481));
    this.mGeometryButton = ((ImageButton)findViewById(2131558482));
    this.mColorsButton = ((ImageButton)findViewById(2131558483));
    this.mImageShow.setImageLoader(this.mImageLoader);
    this.mImageCurves.setImageLoader(this.mImageLoader);
    this.mImageCurves.setMaster(this.mImageShow);
    this.mImageBorders.setImageLoader(this.mImageLoader);
    this.mImageBorders.setMaster(this.mImageShow);
    this.mImageStraighten.setImageLoader(this.mImageLoader);
    this.mImageStraighten.setMaster(this.mImageShow);
    this.mImageZoom.setImageLoader(this.mImageLoader);
    this.mImageZoom.setMaster(this.mImageShow);
    this.mImageCrop.setImageLoader(this.mImageLoader);
    this.mImageCrop.setMaster(this.mImageShow);
    this.mImageRotate.setImageLoader(this.mImageLoader);
    this.mImageRotate.setMaster(this.mImageShow);
    this.mImageFlip.setImageLoader(this.mImageLoader);
    this.mImageFlip.setMaster(this.mImageShow);
    this.mImageTinyPlanet.setImageLoader(this.mImageLoader);
    this.mImageTinyPlanet.setMaster(this.mImageShow);
    this.mPanelController.setActivity(this);
    this.mPanelController.addImageView(findViewById(2131558440));
    this.mPanelController.addImageView(findViewById(2131558445));
    this.mPanelController.addImageView(findViewById(2131558446));
    this.mPanelController.addImageView(findViewById(2131558441));
    this.mPanelController.addImageView(findViewById(2131558442));
    this.mPanelController.addImageView(findViewById(2131558443));
    this.mPanelController.addImageView(findViewById(2131558444));
    this.mPanelController.addImageView(findViewById(2131558447));
    this.mPanelController.addImageView(findViewById(2131558448));
    this.mPanelController.addPanel(this.mFxButton, this.mListFx, 0);
    this.mPanelController.addPanel(this.mBorderButton, this.mListBorders, 1);
    this.mPanelController.addPanel(this.mGeometryButton, this.mListGeometry, 2);
    this.mPanelController.addComponent(this.mGeometryButton, findViewById(2131558461));
    this.mPanelController.addComponent(this.mGeometryButton, findViewById(2131558462));
    this.mPanelController.addComponent(this.mGeometryButton, findViewById(2131558463));
    this.mPanelController.addComponent(this.mGeometryButton, findViewById(2131558464));
    this.mPanelController.addPanel(this.mColorsButton, this.mListColors, 3);
    int[] arrayOfInt1 = { 2131558468, 2131558471, 2131558474, 2131558472, 2131558478, 2131558479, 2131558469, 2131558477, 2131558470, 2131558473 };
    ImageFilter[] arrayOfImageFilter = new ImageFilter[10];
    arrayOfImageFilter[0] = new ImageFilterTinyPlanet();
    arrayOfImageFilter[1] = new ImageFilterVignette();
    arrayOfImageFilter[2] = new ImageFilterVibrance();
    arrayOfImageFilter[3] = new ImageFilterContrast();
    arrayOfImageFilter[4] = new ImageFilterSaturated();
    arrayOfImageFilter[5] = new ImageFilterBwFilter();
    arrayOfImageFilter[6] = new ImageFilterWBalance();
    arrayOfImageFilter[7] = new ImageFilterHue();
    arrayOfImageFilter[8] = new ImageFilterExposure();
    arrayOfImageFilter[9] = new ImageFilterShadows();
    for (int i = 0; ; ++i)
    {
      int j = arrayOfImageFilter.length;
      if (i >= j)
        break;
      ImageSmallFilter localImageSmallFilter = new ImageSmallFilter(this);
      View localView1 = localLinearLayout3.findViewById(arrayOfInt1[i]);
      int k = localLinearLayout3.indexOfChild(localView1);
      localLinearLayout3.removeView(localView1);
      arrayOfImageFilter[i].setParameter(arrayOfImageFilter[i].getPreviewParameter());
      if (localView1 instanceof ImageButtonTitle)
        arrayOfImageFilter[i].setName(((ImageButtonTitle)localView1).getText());
      localImageSmallFilter.setImageFilter(arrayOfImageFilter[i]);
      localImageSmallFilter.setController(this);
      localImageSmallFilter.setImageLoader(this.mImageLoader);
      localImageSmallFilter.setId(arrayOfInt1[i]);
      this.mPanelController.addComponent(this.mColorsButton, localImageSmallFilter);
      localLinearLayout3.addView(localImageSmallFilter, k);
    }
    int[] arrayOfInt2 = { 2131558475, 2131558476 };
    int[] arrayOfInt3 = { 2130837596, 2130837595 };
    int[] arrayOfInt4 = { 2131362119, 2131362127 };
    for (int l = 0; ; ++l)
    {
      int i1 = arrayOfInt2.length;
      if (l >= i1)
        break;
      ImageWithIcon localImageWithIcon = new ImageWithIcon(this);
      View localView2 = localLinearLayout3.findViewById(arrayOfInt2[l]);
      int i2 = localLinearLayout3.indexOfChild(localView2);
      localLinearLayout3.removeView(localView2);
      2 local2 = new ImageFilterExposure(arrayOfInt4[l])
      {
      };
      local2.setParameter(-300);
      localImageWithIcon.setIcon(BitmapFactory.decodeResource(getResources(), arrayOfInt3[l]));
      localImageWithIcon.setImageFilter(local2);
      localImageWithIcon.setController(this);
      localImageWithIcon.setImageLoader(this.mImageLoader);
      localImageWithIcon.setId(arrayOfInt2[l]);
      this.mPanelController.addComponent(this.mColorsButton, localImageWithIcon);
      localLinearLayout3.addView(localImageWithIcon, i2);
    }
    this.mPanelController.addView(findViewById(2131558454));
    this.mPanelController.addView(findViewById(2131558453));
    this.mPanelController.addView(findViewById(2131558452));
    findViewById(2131558486).setOnClickListener(createOnClickResetOperationsButton());
    ListView localListView = (ListView)findViewById(2131558485);
    localListView.setAdapter(this.mImageShow.getHistory());
    localListView.setOnItemClickListener(this);
    ((ListView)findViewById(2131558438)).setAdapter(this.mImageShow.getImageStateAdapter());
    this.mImageLoader.setAdapter(this.mImageShow.getHistory());
    fillListImages(localLinearLayout1);
    fillListBorders(localLinearLayout2);
    SeekBar localSeekBar = (SeekBar)findViewById(2131558449);
    localSeekBar.setMax(600);
    this.mImageShow.setSeekBar(localSeekBar);
    this.mImageZoom.setSeekBar(localSeekBar);
    this.mImageTinyPlanet.setSeekBar(localSeekBar);
    this.mPanelController.setRowPanel(findViewById(2131558450));
    this.mPanelController.setUtilityPanel(this, findViewById(2131558451), findViewById(2131558454), findViewById(2131558452), findViewById(2131558453));
    this.mPanelController.setMasterImage(this.mImageShow);
    this.mPanelController.setCurrentPanel(this.mFxButton);
    Intent localIntent = getIntent();
    if (localIntent.getBooleanExtra("launch-fullscreen", false))
      getWindow().addFlags(1024);
    label1992: String str;
    if (localIntent.getData() != null)
    {
      startLoadBitmap(localIntent.getData());
      str = localIntent.getAction();
      if (!str.equalsIgnoreCase("com.android.camera.action.CROP"))
        break label2032;
      this.mPanelController.showComponent(findViewById(2131558462));
    }
    do
    {
      return;
      pickImage();
      label2032: break label1992:
    }
    while (!str.equalsIgnoreCase("com.android.camera.action.TINY_PLANET"));
    this.mPanelController.showComponent(findViewById(2131558468));
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(2131886084, paramMenu);
    MenuItem localMenuItem1 = paramMenu.findItem(2131558639);
    label38: MenuItem localMenuItem2;
    if (this.mShowingHistoryPanel)
    {
      localMenuItem1.setTitle(2131362096);
      localMenuItem2 = paramMenu.findItem(2131558640);
      if (!this.mShowingImageStatePanel)
        break label171;
      localMenuItem2.setTitle(2131362098);
    }
    while (true)
    {
      this.mShareActionProvider = ((ShareActionProvider)paramMenu.findItem(2131558635).getActionProvider());
      this.mShareActionProvider.setShareIntent(getDefaultShareIntent());
      this.mShareActionProvider.setOnShareTargetSelectedListener(this);
      MenuItem localMenuItem3 = paramMenu.findItem(2131558636);
      MenuItem localMenuItem4 = paramMenu.findItem(2131558637);
      MenuItem localMenuItem5 = paramMenu.findItem(2131558638);
      this.mImageShow.getHistory().setMenuItems(localMenuItem3, localMenuItem4, localMenuItem5);
      return true;
      localMenuItem1.setTitle(2131362095);
      break label38:
      label171: localMenuItem2.setTitle(2131362097);
    }
  }

  protected void onDestroy()
  {
    if (this.mLoadBitmapTask != null)
      this.mLoadBitmapTask.cancel(false);
    super.onDestroy();
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    this.mImageShow.onItemClick(paramInt);
    invalidateViews();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 2131558636:
      int j = this.mImageShow.getHistory().undo();
      this.mImageShow.onItemClick(j);
      this.mImageShow.showToast("Undo");
      invalidateViews();
      return true;
    case 2131558637:
      int i = this.mImageShow.getHistory().redo();
      this.mImageShow.onItemClick(i);
      this.mImageShow.showToast("Redo");
      invalidateViews();
      return true;
    case 2131558638:
      resetHistory();
      return true;
    case 2131558640:
      toggleImageStatePanel();
      return true;
    case 2131558639:
      toggleHistoryPanel();
      return true;
    case 16908332:
    }
    saveImage();
    return true;
  }

  public void onPause()
  {
    super.onPause();
    if (this.mShareActionProvider == null)
      return;
    this.mShareActionProvider.setOnShareTargetSelectedListener(null);
  }

  public void onResume()
  {
    super.onResume();
    if (this.mShareActionProvider == null)
      return;
    this.mShareActionProvider.setOnShareTargetSelectedListener(this);
  }

  public boolean onShareTargetSelected(ShareActionProvider paramShareActionProvider, Intent paramIntent)
  {
    Uri localUri = Uri.withAppendedPath(SharedImageProvider.CONTENT_URI, Uri.encode(this.mSharedOutputFile.getAbsolutePath()));
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("prepare", Boolean.valueOf(true));
    getContentResolver().insert(localUri, localContentValues);
    this.mSharingImage = true;
    showSavingProgress(null);
    this.mImageShow.saveImage(this, this.mSharedOutputFile);
    return true;
  }

  public void pickImage()
  {
    Intent localIntent = new Intent();
    localIntent.setType("image/*");
    localIntent.setAction("android.intent.action.GET_CONTENT");
    startActivityForResult(Intent.createChooser(localIntent, getString(2131362201)), 1);
  }

  public void saveImage()
  {
    if (this.mImageShow.hasModifications())
    {
      int i = GalleryUtils.getBucketId(SaveCopyTask.getFinalSaveDirectory(this, this.mImageLoader.getUri()).getPath());
      showSavingProgress(LocalAlbum.getLocalizedName(getResources(), i, null));
      this.mImageShow.saveImage(this, null);
      return;
    }
    finish();
  }

  public void toggleHistoryPanel()
  {
    View localView1 = findViewById(2131558439);
    View localView2 = findViewById(2131558484);
    if (this.mShowingImageStatePanel)
    {
      findViewById(2131558437).setVisibility(4);
      this.mShowingImageStatePanel = false;
    }
    int i = translateMainPanel(localView2);
    if (!this.mShowingHistoryPanel)
    {
      this.mShowingHistoryPanel = true;
      localView1.animate().setDuration(200L).x(i).withLayer().withEndAction(new Runnable(localView2)
      {
        public void run()
        {
          this.val$viewList.setAlpha(0.0F);
          this.val$viewList.setVisibility(0);
          this.val$viewList.animate().setDuration(100L).alpha(1.0F).start();
        }
      }).start();
    }
    while (true)
    {
      invalidateOptionsMenu();
      return;
      this.mShowingHistoryPanel = false;
      localView2.setVisibility(4);
      localView1.animate().setDuration(200L).x(0.0F).withLayer().start();
    }
  }

  public void useImageFilter(ImageSmallFilter paramImageSmallFilter, ImageFilter paramImageFilter, boolean paramBoolean)
  {
    if (paramImageFilter == null)
      return;
    if (this.mCurrentImageSmallFilter != null)
      this.mCurrentImageSmallFilter.setSelected(false);
    this.mCurrentImageSmallFilter = paramImageSmallFilter;
    this.mCurrentImageSmallFilter.setSelected(true);
    ImagePreset localImagePreset = new ImagePreset(this.mImageShow.getImagePreset());
    localImagePreset.add(paramImageFilter);
    this.mImageShow.setImagePreset(localImagePreset);
    invalidateViews();
  }

  public void useImagePreset(ImageSmallFilter paramImageSmallFilter, ImagePreset paramImagePreset)
  {
    if (paramImagePreset == null)
      return;
    if (this.mCurrentImageSmallFilter != null)
      this.mCurrentImageSmallFilter.setSelected(false);
    this.mCurrentImageSmallFilter = paramImageSmallFilter;
    this.mCurrentImageSmallFilter.setSelected(true);
    ImagePreset localImagePreset = new ImagePreset(paramImagePreset);
    this.mImageShow.setImagePreset(localImagePreset);
    if (paramImagePreset.isFx())
      this.mImageCurves.resetCurve();
    invalidateViews();
  }

  private class LoadBitmapTask extends AsyncTask<Uri, Void, Boolean>
  {
    int mBitmapSize;
    View mTinyPlanetButton;

    public LoadBitmapTask(View arg2)
    {
      Object localObject;
      this.mTinyPlanetButton = localObject;
      this.mBitmapSize = FilterShowActivity.this.getScreenImageSize();
    }

    protected Boolean doInBackground(Uri[] paramArrayOfUri)
    {
      FilterShowActivity.this.mImageLoader.loadBitmap(paramArrayOfUri[0], this.mBitmapSize);
      return Boolean.valueOf(FilterShowActivity.this.mImageLoader.queryLightCycle360());
    }

    protected void onPostExecute(Boolean paramBoolean)
    {
      if (isCancelled())
        return;
      if (paramBoolean.booleanValue())
        this.mTinyPlanetButton.setVisibility(0);
      FilterShowActivity.access$202(FilterShowActivity.this, null);
      super.onPostExecute(paramBoolean);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.FilterShowActivity
 * JD-Core Version:    0.5.4
 */