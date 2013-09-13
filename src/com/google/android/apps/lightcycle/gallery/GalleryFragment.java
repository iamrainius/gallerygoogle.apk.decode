package com.google.android.apps.lightcycle.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.apps.lightcycle.PanoramaViewActivity;
import com.google.android.apps.lightcycle.gallery.data.PhotoUrls;
import com.google.android.apps.lightcycle.gallery.data.PicasaRequestContext;
import com.google.android.apps.lightcycle.panorama.DeviceManager;
import com.google.android.apps.lightcycle.panorama.LightCycleNative;
import com.google.android.apps.lightcycle.panorama.StitchingServiceManager;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.storage.LocalSessionStorage;
import com.google.android.apps.lightcycle.storage.StorageManager;
import com.google.android.apps.lightcycle.storage.StorageManagerFactory;
import com.google.android.apps.lightcycle.storage.ZippableSession;
import com.google.android.apps.lightcycle.util.AnalyticsHelper;
import com.google.android.apps.lightcycle.util.AnalyticsHelper.Page;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.Dialogs;
import com.google.android.apps.lightcycle.util.PanoMetadata;
import com.google.android.apps.lightcycle.util.ProgressCallback;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;

public class GalleryFragment extends Fragment
{
  private static final String TAG = GalleryFragment.class.getSimpleName();
  private AccountsUtil accountsUtil;
  private AnalyticsHelper analyticsHelper;
  private float currentPitchDegrees = 0.0F;
  private Thread glassRenderThread;
  private boolean ignoreNextDpadEvent = true;
  private PanoListAdapter panoListAdapterStitched;
  private GalleryPanoSource panoSource;
  private ListView panoramaList;
  private int selectedItem = 0;
  private SensorReader sensorReader;
  private StitchingServiceManager stitchingServiceManager;
  private StorageManager storageManager;
  private ProgressDialog uploadProgressDialog;

  private ProgressDialog buildProgressDialog(int paramInt)
  {
    ProgressDialog localProgressDialog = new ProgressDialog(getActivity());
    localProgressDialog.setCancelable(false);
    localProgressDialog.setTitle(paramInt);
    return localProgressDialog;
  }

  private void createLittlePlanetProjection(int paramInt)
  {
    StereographicProjectionTask localStereographicProjectionTask = new StereographicProjectionTask(getActivity());
    LocalSessionStorage[] arrayOfLocalSessionStorage = new LocalSessionStorage[1];
    arrayOfLocalSessionStorage[0] = getSessionStorageForPano(paramInt);
    localStereographicProjectionTask.execute(arrayOfLocalSessionStorage);
  }

  private void deletePano(int paramInt)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
    localBuilder.setTitle(2131361853);
    localBuilder.setPositiveButton(getString(2131361815), new DialogInterface.OnClickListener(paramInt)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        GalleryFragment.this.analyticsHelper.trackPage(AnalyticsHelper.Page.DELETE_SESSION);
        String str = GalleryFragment.this.panoListAdapterStitched.getSessionId(this.val$which);
        GalleryFragment.this.storageManager.deleteSession(str);
        GalleryFragment.this.refresh();
        paramDialogInterface.dismiss();
      }
    });
    localBuilder.setNegativeButton(getString(2131361816), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        paramDialogInterface.dismiss();
      }
    });
    localBuilder.create().show();
  }

  private static String getFileNameForPath(String paramString)
  {
    return new File(paramString).getName();
  }

  private LocalSessionStorage getSessionStorageForPano(int paramInt)
  {
    String str = this.panoListAdapterStitched.getSessionId(paramInt);
    return this.storageManager.getExistingLocalSessionStorage(str);
  }

  private boolean isCroppedHorizontally(String paramString)
  {
    PanoMetadata localPanoMetadata = PanoMetadata.parse(paramString);
    return (localPanoMetadata != null) && (localPanoMetadata.croppedAreaWidth < localPanoMetadata.fullPanoWidth);
  }

  private void onUploadCompleted(PhotoUrls paramPhotoUrls)
  {
    if (paramPhotoUrls == null)
    {
      this.uploadProgressDialog.dismiss();
      Log.w(TAG, "Upload failed. Not sharing.");
      return;
    }
    7 local7 = new ProgressCallback()
    {
      public void onDone(Void paramVoid)
      {
        GalleryFragment.this.analyticsHelper.trackPage(AnalyticsHelper.Page.UPLOAD_SUCCESSFUL);
        GalleryFragment.this.uploadProgressDialog.dismiss();
      }

      public void onNewProgressMessage(String paramString)
      {
        GalleryFragment.this.uploadProgressDialog.setMessage(paramString);
      }
    };
    Log.d(TAG, "Upload done.");
    SharingUtil.sharePano(paramPhotoUrls, getActivity(), local7);
  }

  private void refresh()
  {
    this.panoSource.refresh();
    this.panoListAdapterStitched.notifyDataSetChanged();
  }

  private void renderForGlass()
  {
    float f = this.sensorReader.getAndResetGyroData()[0];
    this.currentPitchDegrees = (float)(this.currentPitchDegrees + Math.toDegrees(f));
    this.selectedItem = (int)(-this.currentPitchDegrees / 4.0F);
    this.panoramaList.setSelection(this.selectedItem);
  }

  private void sharePano(int paramInt, boolean paramBoolean)
  {
    String str1 = this.panoListAdapterStitched.getPanoFileName(paramInt);
    if ((str1 == null) || (str1.isEmpty()))
      Log.w(TAG, "No stitched pano at position " + paramInt);
    if ((paramBoolean) && (isCroppedHorizontally(str1)))
    {
      Toast.makeText(getActivity(), 2131361863, 1).show();
      return;
    }
    String str2 = getFileNameForPath(str1);
    if (paramBoolean)
    {
      sharePanoViaIntent(new File(str1));
      return;
    }
    uploadAndSharePano(str2, str1);
  }

  private void sharePanoViaIntent(File paramFile)
  {
    Intent localIntent = new Intent("android.intent.action.SEND");
    localIntent.setType("application/vnd.google.panorama360+jpg");
    localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(paramFile));
    startActivity(Intent.createChooser(localIntent, getResources().getString(2131361841)));
  }

  private void showActionsDialog(Callback<Integer> paramCallback, boolean paramBoolean)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
    localBuilder.setTitle(2131361842);
    if (paramBoolean);
    for (int i = 2131427328; ; i = 2131427329)
    {
      localBuilder.setItems(i, new DialogInterface.OnClickListener(paramCallback)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          this.val$action.onCallback(Integer.valueOf(paramInt));
        }
      });
      localBuilder.create().show();
      return;
    }
  }

  private void startPhotoUpload(String paramString, HttpEntity paramHttpEntity)
  {
    this.uploadProgressDialog.show();
    this.uploadProgressDialog.setMessage(getString(2131361843));
    this.accountsUtil.getAuthToken(new Callback(paramString, paramHttpEntity)
    {
      public void onCallback(String paramString)
      {
        if ((paramString == null) || (paramString.isEmpty()))
        {
          GalleryFragment.this.uploadProgressDialog.dismiss();
          Dialogs.showDialog(2131361838, 2131361840, GalleryFragment.this.getActivity(), null);
          return;
        }
        GalleryFragment.this.uploadPhoto(this.val$fileName, this.val$picture, paramString);
      }
    });
  }

  private void stitchPano(int paramInt)
  {
    LightCycleNative.ResetForCapture();
    LocalSessionStorage localLocalSessionStorage = getSessionStorageForPano(paramInt);
    if ((localLocalSessionStorage.sessionDir == null) || (!new File(localLocalSessionStorage.sessionDir).exists()))
    {
      Dialogs.showDialog(2131361855, 2131361856, getActivity(), null);
      return;
    }
    this.stitchingServiceManager.newTask(localLocalSessionStorage);
  }

  private void stitchUnstitchedPanos(List<String> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Log.d(TAG, "About to stitch " + str);
      LocalSessionStorage localLocalSessionStorage = this.storageManager.getExistingLocalSessionStorage(str);
      if (localLocalSessionStorage == null)
        Log.e(TAG, "Unstitched session not found in storage manager: " + str);
      this.stitchingServiceManager.newTask(localLocalSessionStorage);
    }
  }

  private void uploadAndSharePano(String paramString1, String paramString2)
  {
    AnalyticsHelper.getInstance(getActivity()).trackPage(AnalyticsHelper.Page.UPLOAD_START);
    startPhotoUpload(paramString1, new FileEntity(new File(paramString2), "image/jpeg"));
  }

  private void uploadPhoto(String paramString1, HttpEntity paramHttpEntity, String paramString2)
  {
    6 local6 = new ProgressCallback()
    {
      public void onDone(PhotoUrls paramPhotoUrls)
      {
        GalleryFragment.this.onUploadCompleted(paramPhotoUrls);
      }

      public void onNewProgressMessage(String paramString)
      {
        GalleryFragment.this.uploadProgressDialog.setMessage(paramString);
      }
    };
    UploadPhotoUtil.uploadPhoto(paramString1, paramHttpEntity, new PicasaRequestContext(this.accountsUtil.getActiveAccountName(), paramString2, getActivity()), local6);
  }

  private void viewPano(int paramInt)
  {
    String str = this.panoListAdapterStitched.getPanoFileName(paramInt);
    if ((str == null) || (str.isEmpty()))
    {
      Log.w(TAG, "No stitched pano at position " + paramInt);
      return;
    }
    Intent localIntent = new Intent(getActivity(), PanoramaViewActivity.class);
    localIntent.putExtra("filename", str);
    startActivity(localIntent);
  }

  private void zipAndSendPano(int paramInt)
  {
    File localFile = new File(this.storageManager.getTempDirectory(), "session.zip");
    ProgressDialog localProgressDialog = buildProgressDialog(2131361854);
    localProgressDialog.show();
    String str = this.panoListAdapterStitched.getSessionId(paramInt);
    this.storageManager.getZippableSession(str).saveAs(localFile, new Callback(localProgressDialog, localFile)
    {
      public void onCallback(Boolean paramBoolean)
      {
        this.val$progressDialog.dismiss();
        if (!paramBoolean.booleanValue())
          return;
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("application/zip");
        localIntent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + this.val$tempZipFile.getAbsolutePath()));
        localIntent.putExtra("android.intent.extra.SUBJECT", "Panorama for debugging");
        localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "lightcycle-debug-dump@google.com" });
        localIntent.putExtra("android.intent.extra.TEXT", "(Explain what is wrong with the panorama.)");
        GalleryFragment.this.startActivity(Intent.createChooser(localIntent, "Send using:"));
      }
    });
  }

  public void onAuthenticationActivityResult(boolean paramBoolean)
  {
    this.accountsUtil.onAuthenticationActivityResult(paramBoolean);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.accountsUtil = new AccountsUtil(getActivity());
    this.storageManager = StorageManagerFactory.getStorageManager();
    this.storageManager.init(getActivity());
    this.panoSource = new GalleryPanoSource(this.storageManager);
    LayoutInflater localLayoutInflater = LayoutInflater.from(getActivity());
    this.panoListAdapterStitched = new PanoListAdapter(this.panoSource, localLayoutInflater, true);
    this.uploadProgressDialog = buildProgressDialog(2131361836);
    this.analyticsHelper = AnalyticsHelper.getInstance(getActivity());
    this.stitchingServiceManager = StitchingServiceManager.getStitchingServiceManager(getActivity());
    if (this.panoSource.getUnstitchedSessions().size() <= 0)
      return;
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968603, null);
    this.panoramaList = ((ListView)localView.findViewById(2131558496));
    if (DeviceManager.isWingman())
      this.panoramaList.setSelector(2130837804);
    this.panoramaList.setAdapter(this.panoListAdapterStitched);
    if (DeviceManager.isWingman())
      this.panoramaList.setOnKeyListener(new View.OnKeyListener()
      {
        public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
        {
          if (paramInt == 23)
          {
            if (GalleryFragment.this.ignoreNextDpadEvent)
            {
              GalleryFragment.access$002(GalleryFragment.this, false);
              return true;
            }
            Log.d(GalleryFragment.TAG, "Show Panorama.");
            GalleryFragment.this.viewPano(GalleryFragment.this.panoramaList.getSelectedItemPosition());
            return true;
          }
          return false;
        }
      });
    this.panoramaList.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
      {
        GalleryFragment.this.showActionsDialog(new Callback(paramInt)
        {
          public void onCallback(Integer paramInteger)
          {
            GalleryFragment.StitchedAction localStitchedAction = GalleryFragment.StitchedAction.values()[paramInteger.intValue()];
            switch (GalleryFragment.12.$SwitchMap$com$google$android$apps$lightcycle$gallery$GalleryFragment$StitchedAction[localStitchedAction.ordinal()])
            {
            case 2:
            default:
              GalleryFragment.this.sharePano(this.val$position, false);
              return;
            case 1:
              GalleryFragment.this.viewPano(this.val$position);
              return;
            case 3:
              GalleryFragment.this.sharePano(this.val$position, true);
              return;
            case 4:
              GalleryFragment.this.createLittlePlanetProjection(this.val$position);
              return;
            case 5:
              GalleryFragment.this.deletePano(this.val$position);
              return;
            case 6:
              GalleryFragment.this.zipAndSendPano(this.val$position);
              return;
            case 7:
            }
            GalleryFragment.this.stitchPano(this.val$position);
          }
        }
        , true);
      }
    });
    return localView;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 2131558656)
    {
      List localList = this.panoSource.getUnstitchedSessions();
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(localList.size());
      Dialogs.showOkCancelDialog(2131361857, getString(2131361858, arrayOfObject), getActivity(), new Callback(localList)
      {
        public void onCallback(Void paramVoid)
        {
          GalleryFragment.this.stitchUnstitchedPanos(this.val$unstitchedPanos);
        }
      });
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }

  public void onPause()
  {
    super.onPause();
    if (this.sensorReader != null)
      this.sensorReader.stop();
    if (this.glassRenderThread == null)
      return;
    this.glassRenderThread.interrupt();
  }

  public void onResume()
  {
    super.onResume();
    refresh();
    if (!DeviceManager.isWingman())
      return;
    this.sensorReader = new SensorReader();
    this.sensorReader.enableEkf(true);
    this.sensorReader.start(getActivity());
    this.glassRenderThread = new Thread()
    {
      public void run()
      {
        while (!isInterrupted())
          try
          {
            sleep(16L);
            FragmentActivity localFragmentActivity = GalleryFragment.this.getActivity();
            if (localFragmentActivity != null);
            localFragmentActivity.runOnUiThread(new Runnable()
            {
              public void run()
              {
                GalleryFragment.this.renderForGlass();
              }
            });
          }
          catch (InterruptedException localInterruptedException)
          {
          }
      }
    };
    this.glassRenderThread.start();
  }

  static enum StitchedAction
  {
    static
    {
      SHARE = new StitchedAction("SHARE", 1);
      SHARE_ON_MAPS = new StitchedAction("SHARE_ON_MAPS", 2);
      CREATE_LITTLE_PLANET = new StitchedAction("CREATE_LITTLE_PLANET", 3);
      DELETE = new StitchedAction("DELETE", 4);
      ZIP = new StitchedAction("ZIP", 5);
      STITCH = new StitchedAction("STITCH", 6);
      StitchedAction[] arrayOfStitchedAction = new StitchedAction[7];
      arrayOfStitchedAction[0] = VIEW;
      arrayOfStitchedAction[1] = SHARE;
      arrayOfStitchedAction[2] = SHARE_ON_MAPS;
      arrayOfStitchedAction[3] = CREATE_LITTLE_PLANET;
      arrayOfStitchedAction[4] = DELETE;
      arrayOfStitchedAction[5] = ZIP;
      arrayOfStitchedAction[6] = STITCH;
      $VALUES = arrayOfStitchedAction;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.GalleryFragment
 * JD-Core Version:    0.5.4
 */