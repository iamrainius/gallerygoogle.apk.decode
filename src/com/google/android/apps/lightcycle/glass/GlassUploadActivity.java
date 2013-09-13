package com.google.android.apps.lightcycle.glass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.apps.lightcycle.gallery.AccountsUtil;
import com.google.android.apps.lightcycle.gallery.SharingUtil;
import com.google.android.apps.lightcycle.gallery.UploadPhotoUtil;
import com.google.android.apps.lightcycle.gallery.data.PhotoUrls;
import com.google.android.apps.lightcycle.gallery.data.PicasaRequestContext;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.ProgressCallback;
import java.io.File;
import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;

public class GlassUploadActivity extends Activity
{
  private static final String TAG = GlassUploadActivity.class.getSimpleName();
  private AccountsUtil accountsUtil;
  private TextView actionTextView;

  private void onUploadCompleted(PhotoUrls paramPhotoUrls)
  {
    if (paramPhotoUrls == null)
    {
      Log.e(TAG, "Upload failed. Not sharing.");
      finish();
      return;
    }
    3 local3 = new ProgressCallback()
    {
      public void onDone(Void paramVoid)
      {
        GlassUploadActivity.this.finish();
      }

      public void onNewProgressMessage(String paramString)
      {
        GlassUploadActivity.this.actionTextView.setText(paramString);
      }
    };
    Log.d(TAG, "Upload done. Initiating sharing...");
    SharingUtil.sharePano(paramPhotoUrls, this, local3);
  }

  private void uploadPhoto(String paramString, HttpEntity paramHttpEntity)
  {
    this.actionTextView.setText(2131361843);
    this.accountsUtil.getAuthToken(new Callback(paramString, paramHttpEntity)
    {
      public void onCallback(String paramString)
      {
        if ((paramString == null) || (paramString.isEmpty()))
        {
          Log.e(GlassUploadActivity.TAG, "Could not get authtoken.");
          GlassUploadActivity.this.finish();
          return;
        }
        GlassUploadActivity.this.uploadPhoto(this.val$fileName, this.val$picture, paramString);
      }
    });
  }

  private void uploadPhoto(String paramString1, HttpEntity paramHttpEntity, String paramString2)
  {
    2 local2 = new ProgressCallback()
    {
      public void onDone(PhotoUrls paramPhotoUrls)
      {
        GlassUploadActivity.this.onUploadCompleted(paramPhotoUrls);
      }

      public void onNewProgressMessage(String paramString)
      {
        GlassUploadActivity.this.actionTextView.setText(paramString);
      }
    };
    UploadPhotoUtil.uploadPhoto(paramString1, paramHttpEntity, new PicasaRequestContext(this.accountsUtil.getActiveAccountName(), paramString2, this), local2);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968600);
    this.accountsUtil = new AccountsUtil(this);
    this.actionTextView = ((TextView)findViewById(2131558493));
    Intent localIntent = getIntent();
    uploadPhoto(localIntent.getStringExtra("filename_extra"), new FileEntity(new File(localIntent.getStringExtra("pathname_extra")), "image/jpeg"));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.glass.GlassUploadActivity
 * JD-Core Version:    0.5.4
 */