package com.google.android.apps.lightcycle.gallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.apps.lightcycle.storage.SessionMetadata;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanoListAdapter extends BaseAdapter
{
  private Map<Object, Runnable> activeTasks = new HashMap();
  private Map<String, SoftReference<Bitmap>> cache = new HashMap();
  private final LayoutInflater inflater;
  private final Handler loadHandler = new Handler();
  private final java.text.DateFormat longDateFormat;
  private final GalleryPanoSource panoSource;
  private List<String> sessionIds = new ArrayList();
  private boolean showStitchedPanos = true;
  private final java.text.DateFormat timeFormat;

  public PanoListAdapter(GalleryPanoSource paramGalleryPanoSource, LayoutInflater paramLayoutInflater, boolean paramBoolean)
  {
    this.panoSource = paramGalleryPanoSource;
    this.inflater = paramLayoutInflater;
    this.showStitchedPanos = paramBoolean;
    this.longDateFormat = android.text.format.DateFormat.getLongDateFormat(paramLayoutInflater.getContext());
    this.timeFormat = android.text.format.DateFormat.getTimeFormat(paramLayoutInflater.getContext());
  }

  private void createStitchedPanoRow(int paramInt, LinearLayout paramLinearLayout, TextView paramTextView)
  {
    ImageView localImageView = (ImageView)paramLinearLayout.findViewById(1);
    if (localImageView == null)
    {
      localImageView = new ImageView(this.inflater.getContext());
      localImageView.setId(1);
      localImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      localImageView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
      paramLinearLayout.addView(localImageView);
    }
    localImageView.setTag(2131558400, "tag-" + paramInt + "-" + System.currentTimeMillis());
    SessionMetadata localSessionMetadata = this.panoSource.getSession((String)this.sessionIds.get(paramInt));
    String str1 = (String)this.sessionIds.get(paramInt);
    if (localSessionMetadata.stitchedPanoramaExists)
      paramTextView.setText(getI18NDateOfFile(this.panoSource.getStitchedFile(str1)));
    if (localSessionMetadata.thumbnailExists)
    {
      String str2 = this.panoSource.getThumbnailFile(str1);
      localImageView.setScaleType(ImageView.ScaleType.CENTER);
      localImageView.setImageResource(17301599);
      getThumbnail(str2, localImageView);
      return;
    }
    paramTextView.setText("");
    localImageView.setScaleType(ImageView.ScaleType.CENTER);
    localImageView.setImageResource(17301642);
  }

  private void createUnstitchedPanoRow(int paramInt, LinearLayout paramLinearLayout)
  {
    if ((LinearLayout)paramLinearLayout.findViewById(1) != null)
      return;
    LinearLayout localLinearLayout = (LinearLayout)this.inflater.inflate(2130968605, null);
    localLinearLayout.setId(1);
    paramLinearLayout.addView(localLinearLayout);
  }

  private String getI18NDateOfFile(String paramString)
  {
    File localFile = new File(paramString);
    if ((!localFile.exists()) || (!localFile.isFile()))
      return "";
    Date localDate = new Date(localFile.lastModified());
    String str = this.longDateFormat.format(localDate);
    return str + " - " + this.timeFormat.format(localDate);
  }

  @SuppressLint({"NewApi"})
  private void getThumbnail(String paramString, ImageView paramImageView)
  {
    monitorenter;
    Object localObject2;
    1 local1;
    try
    {
      localObject2 = paramImageView.getTag(2131558400);
      if (this.activeTasks.containsKey(localObject2))
      {
        this.loadHandler.removeCallbacks((Runnable)this.activeTasks.get(localObject2));
        this.activeTasks.remove(localObject2);
      }
      paramImageView.setTag(2131558401, paramString);
      if ((this.cache.containsKey(paramString)) && (((SoftReference)this.cache.get(paramString)).get() != null))
      {
        paramImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        paramImageView.setImageBitmap((Bitmap)((SoftReference)this.cache.get(paramString)).get());
        return;
      }
      local1 = new Runnable(paramString, paramImageView)
      {
        public void run()
        {
          if (Build.VERSION.SDK_INT >= 11)
          {
            new LoadThumbnailTask(this.val$fileName, this.val$thumbnailView, PanoListAdapter.this.cache).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            return;
          }
          new LoadThumbnailTask(this.val$fileName, this.val$thumbnailView, PanoListAdapter.this.cache).execute(new Void[0]);
        }
      };
      this.loadHandler.postDelayed(local1, 400L);
    }
    finally
    {
      monitorexit;
    }
  }

  private void refreshIds()
  {
    this.sessionIds.clear();
    if (this.showStitchedPanos)
      this.sessionIds.addAll(this.panoSource.getStitchedSessions());
    while (true)
    {
      Collections.reverse(this.sessionIds);
      return;
      this.sessionIds.addAll(this.panoSource.getUnstitchedSessions());
    }
  }

  public int getCount()
  {
    return this.sessionIds.size();
  }

  public Object getItem(int paramInt)
  {
    return null;
  }

  public long getItemId(int paramInt)
  {
    return paramInt;
  }

  public String getPanoFileName(int paramInt)
  {
    return this.panoSource.getStitchedFile((String)this.sessionIds.get(paramInt));
  }

  public String getSessionId(int paramInt)
  {
    return (String)this.sessionIds.get(paramInt);
  }

  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramView;
    if (localView == null)
      localView = this.inflater.inflate(2130968604, paramViewGroup, false);
    LinearLayout localLinearLayout = (LinearLayout)localView.findViewById(2131558408);
    if (this.showStitchedPanos)
    {
      createStitchedPanoRow(paramInt, localLinearLayout, (TextView)localView.findViewById(2131558497));
      return localView;
    }
    createUnstitchedPanoRow(paramInt, localLinearLayout);
    return localView;
  }

  public void notifyDataSetChanged()
  {
    refreshIds();
    super.notifyDataSetChanged();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.PanoListAdapter
 * JD-Core Version:    0.5.4
 */