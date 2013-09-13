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

public class PhotoController extends PieController
  implements ListPrefSettingPopup.Listener, MoreSettingPopup.Listener
{
  private static float FLOAT_PI_DIVIDED_BY_TWO;
  private static String TAG = "CAM_photocontrol";
  private PhotoModule mModule;
  private String[] mOtherKeys;
  private MoreSettingPopup mPopup;
  private AbstractSettingPopup mSecondPopup;
  private final String mSettingOff;

  static
  {
    FLOAT_PI_DIVIDED_BY_TWO = 1.570796F;
  }

  public PhotoController(CameraActivity paramCameraActivity, PhotoModule paramPhotoModule, PieRenderer paramPieRenderer)
  {
    super(paramCameraActivity, paramPieRenderer);
    this.mModule = paramPhotoModule;
    this.mSettingOff = paramCameraActivity.getString(2131361909);
  }

  private static boolean notSame(ListPreference paramListPreference, String paramString1, String paramString2)
  {
    return (paramString1.equals(paramListPreference.getKey())) && (!paramString2.equals(paramListPreference.getValue()));
  }

  private void setPreference(String paramString1, String paramString2)
  {
    ListPreference localListPreference = this.mPreferenceGroup.findPreference(paramString1);
    if ((localListPreference == null) || (paramString2.equals(localListPreference.getValue())))
      return;
    localListPreference.setValue(paramString2);
    reloadPreferences();
  }

  public void initialize(PreferenceGroup paramPreferenceGroup)
  {
    super.initialize(paramPreferenceGroup);
    this.mPopup = null;
    this.mSecondPopup = null;
    float f = FLOAT_PI_DIVIDED_BY_TWO / 2.0F;
    addItem("pref_camera_flashmode_key", FLOAT_PI_DIVIDED_BY_TWO - f, f);
    addItem("pref_camera_exposure_key", 3.0F * FLOAT_PI_DIVIDED_BY_TWO - f, f);
    addItem("pref_camera_whitebalance_key", f + 3.0F * FLOAT_PI_DIVIDED_BY_TWO, f);
    if (paramPreferenceGroup.findPreference("pref_camera_id_key") != null)
    {
      PieItem localPieItem3 = makeItem(2130837754);
      localPieItem3.setFixedSlice(f + FLOAT_PI_DIVIDED_BY_TWO, f);
      localPieItem3.setOnClickListener(new PieItem.OnClickListener()
      {
        public void onClick(PieItem paramPieItem)
        {
          ListPreference localListPreference = PhotoController.this.mPreferenceGroup.findPreference("pref_camera_id_key");
          if (localListPreference == null)
            return;
          int i = localListPreference.findIndexOfValue(localListPreference.getValue());
          CharSequence[] arrayOfCharSequence = localListPreference.getEntryValues();
          int j = Integer.parseInt((String)arrayOfCharSequence[((i + 1) % arrayOfCharSequence.length)]);
          PhotoController.this.mListener.onCameraPickerClicked(j);
        }
      });
      this.mRenderer.addItem(localPieItem3);
    }
    if (paramPreferenceGroup.findPreference("pref_camera_hdr_key") != null)
    {
      PieItem localPieItem2 = makeItem(2130837680);
      localPieItem2.setFixedSlice(FLOAT_PI_DIVIDED_BY_TWO, f);
      localPieItem2.setOnClickListener(new PieItem.OnClickListener()
      {
        public void onClick(PieItem paramPieItem)
        {
          ListPreference localListPreference = PhotoController.this.mPreferenceGroup.findPreference("pref_camera_hdr_key");
          if (localListPreference == null)
            return;
          localListPreference.setValueIndex((1 + localListPreference.findIndexOfValue(localListPreference.getValue())) % 2);
          PhotoController.this.onSettingChanged(localListPreference);
        }
      });
      this.mRenderer.addItem(localPieItem2);
    }
    this.mOtherKeys = new String[] { "pref_camera_scenemode_key", "pref_camera_recordlocation_key", "pref_camera_picturesize_key", "pref_camera_focusmode_key" };
    PieItem localPieItem1 = makeItem(2130837745);
    localPieItem1.setFixedSlice(3.0F * FLOAT_PI_DIVIDED_BY_TWO, f);
    localPieItem1.setOnClickListener(new PieItem.OnClickListener()
    {
      public void onClick(PieItem paramPieItem)
      {
        if (PhotoController.this.mPopup == null)
          PhotoController.this.initializePopup();
        PhotoController.this.mModule.showPopup(PhotoController.this.mPopup);
      }
    });
    this.mRenderer.addItem(localPieItem1);
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
    if ((this.mPopup != null) && (this.mSecondPopup != null))
    {
      this.mModule.dismissPopup(true);
      this.mPopup.reloadPreference();
    }
    onSettingChanged(paramListPreference);
  }

  public void onPreferenceClicked(ListPreference paramListPreference)
  {
    if (this.mSecondPopup != null)
      return;
    ListPrefSettingPopup localListPrefSettingPopup = (ListPrefSettingPopup)((LayoutInflater)this.mActivity.getSystemService("layout_inflater")).inflate(2130968610, null, false);
    localListPrefSettingPopup.initialize(paramListPreference);
    localListPrefSettingPopup.setSettingChangedListener(this);
    this.mModule.dismissPopup(true);
    this.mSecondPopup = localListPrefSettingPopup;
    this.mModule.showPopup(this.mSecondPopup);
  }

  public void onSettingChanged(ListPreference paramListPreference)
  {
    if (notSame(paramListPreference, "pref_camera_hdr_key", this.mSettingOff))
      setPreference("pref_camera_scenemode_key", "auto");
    while (true)
    {
      super.onSettingChanged(paramListPreference);
      return;
      if (!notSame(paramListPreference, "pref_camera_scenemode_key", "auto"))
        continue;
      setPreference("pref_camera_hdr_key", this.mSettingOff);
    }
  }

  public void overrideSettings(String[] paramArrayOfString)
  {
    super.overrideSettings(paramArrayOfString);
    if (this.mPopup == null)
      initializePopup();
    this.mPopup.overrideSettings(paramArrayOfString);
  }

  public void popupDismissed(boolean paramBoolean)
  {
    if (this.mSecondPopup == null)
      return;
    this.mSecondPopup = null;
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
 * Qualified Name:     com.android.camera.PhotoController
 * JD-Core Version:    0.5.4
 */