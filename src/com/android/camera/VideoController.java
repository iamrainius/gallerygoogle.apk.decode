package com.android.camera;

import android.view.LayoutInflater;
import com.android.camera.ui.AbstractSettingPopup;
import com.android.camera.ui.ListPrefSettingPopup;
import com.android.camera.ui.ListPrefSettingPopup.Listener;
import com.android.camera.ui.MoreSettingPopup;
import com.android.camera.ui.MoreSettingPopup.Listener;
import com.android.camera.ui.PieItem;
import com.android.camera.ui.PieItem.OnClickListener;
import com.android.camera.ui.PieRenderer;
import com.android.camera.ui.TimeIntervalPopup;
import com.android.camera.ui.TimeIntervalPopup.Listener;

public class VideoController extends PieController
  implements ListPrefSettingPopup.Listener, MoreSettingPopup.Listener, TimeIntervalPopup.Listener
{
  private static float FLOAT_PI_DIVIDED_BY_TWO;
  private static String TAG = "CAM_videocontrol";
  private VideoModule mModule;
  private String[] mOtherKeys;
  private AbstractSettingPopup mPopup;
  private int mPopupStatus;

  static
  {
    FLOAT_PI_DIVIDED_BY_TWO = 1.570796F;
  }

  public VideoController(CameraActivity paramCameraActivity, VideoModule paramVideoModule, PieRenderer paramPieRenderer)
  {
    super(paramCameraActivity, paramPieRenderer);
    this.mModule = paramVideoModule;
  }

  public void initialize(PreferenceGroup paramPreferenceGroup)
  {
    super.initialize(paramPreferenceGroup);
    this.mPopup = null;
    this.mPopupStatus = 0;
    float f = FLOAT_PI_DIVIDED_BY_TWO / 2.0F;
    addItem("pref_camera_video_flashmode_key", FLOAT_PI_DIVIDED_BY_TWO - f, f);
    addItem("pref_camera_whitebalance_key", f + 3.0F * FLOAT_PI_DIVIDED_BY_TWO, f);
    PieItem localPieItem1 = makeItem(2130837760);
    localPieItem1.setFixedSlice(f + FLOAT_PI_DIVIDED_BY_TWO, f);
    localPieItem1.setOnClickListener(new PieItem.OnClickListener()
    {
      public void onClick(PieItem paramPieItem)
      {
        ListPreference localListPreference = VideoController.this.mPreferenceGroup.findPreference("pref_camera_id_key");
        if (localListPreference == null)
          return;
        int i = localListPreference.findIndexOfValue(localListPreference.getValue());
        CharSequence[] arrayOfCharSequence = localListPreference.getEntryValues();
        int j = Integer.parseInt((String)arrayOfCharSequence[((i + 1) % arrayOfCharSequence.length)]);
        VideoController.this.mListener.onCameraPickerClicked(j);
      }
    });
    this.mRenderer.addItem(localPieItem1);
    this.mOtherKeys = new String[] { "pref_video_effect_key", "pref_video_time_lapse_frame_interval_key", "pref_video_quality_key", "pref_camera_recordlocation_key" };
    PieItem localPieItem2 = makeItem(2130837745);
    localPieItem2.setFixedSlice(3.0F * FLOAT_PI_DIVIDED_BY_TWO, f);
    localPieItem2.setOnClickListener(new PieItem.OnClickListener()
    {
      public void onClick(PieItem paramPieItem)
      {
        if ((VideoController.this.mPopup == null) || (VideoController.this.mPopupStatus != 1))
        {
          VideoController.this.initializePopup();
          VideoController.access$102(VideoController.this, 1);
        }
        VideoController.this.mModule.showPopup(VideoController.this.mPopup);
      }
    });
    this.mRenderer.addItem(localPieItem2);
  }

  protected void initializePopup()
  {
    MoreSettingPopup localMoreSettingPopup = (MoreSettingPopup)((LayoutInflater)this.mActivity.getSystemService("layout_inflater")).inflate(2130968617, null, false);
    localMoreSettingPopup.setSettingChangedListener(this);
    localMoreSettingPopup.initialize(this.mPreferenceGroup, this.mOtherKeys);
    this.mPopup = localMoreSettingPopup;
  }

  public void onListPrefChanged(ListPreference paramListPreference)
  {
    if ((this.mPopup != null) && (this.mPopupStatus == 2))
      this.mModule.dismissPopup(true);
    super.onSettingChanged(paramListPreference);
  }

  public void onPreferenceClicked(ListPreference paramListPreference)
  {
    if (this.mPopupStatus != 1)
      return;
    LayoutInflater localLayoutInflater = (LayoutInflater)this.mActivity.getSystemService("layout_inflater");
    TimeIntervalPopup localTimeIntervalPopup;
    if ("pref_video_time_lapse_frame_interval_key".equals(paramListPreference.getKey()))
    {
      localTimeIntervalPopup = (TimeIntervalPopup)localLayoutInflater.inflate(2130968659, null, false);
      localTimeIntervalPopup.initialize((IconListPreference)paramListPreference);
      localTimeIntervalPopup.setSettingChangedListener(this);
      this.mModule.dismissPopup(true);
    }
    ListPrefSettingPopup localListPrefSettingPopup;
    for (this.mPopup = localTimeIntervalPopup; ; this.mPopup = localListPrefSettingPopup)
    {
      this.mModule.showPopup(this.mPopup);
      this.mPopupStatus = 2;
      return;
      localListPrefSettingPopup = (ListPrefSettingPopup)localLayoutInflater.inflate(2130968610, null, false);
      localListPrefSettingPopup.initialize(paramListPreference);
      localListPrefSettingPopup.setSettingChangedListener(this);
      this.mModule.dismissPopup(true);
    }
  }

  public void overrideSettings(String[] paramArrayOfString)
  {
    super.overrideSettings(paramArrayOfString);
    if ((this.mPopup == null) || (this.mPopupStatus != 1))
    {
      this.mPopupStatus = 1;
      initializePopup();
    }
    ((MoreSettingPopup)this.mPopup).overrideSettings(paramArrayOfString);
  }

  public void popupDismissed(boolean paramBoolean)
  {
    if (this.mPopupStatus != 2)
      return;
    initializePopup();
    this.mPopupStatus = 1;
    if (!paramBoolean)
      return;
    this.mModule.showPopup(this.mPopup);
  }

  public void reloadPreferences()
  {
    super.reloadPreferences();
    if (this.mPopup == null)
      return;
    this.mPopup.reloadPreference();
  }

  protected void setCameraId(int paramInt)
  {
    this.mPreferenceGroup.findPreference("pref_camera_id_key").setValue("" + paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.VideoController
 * JD-Core Version:    0.5.4
 */