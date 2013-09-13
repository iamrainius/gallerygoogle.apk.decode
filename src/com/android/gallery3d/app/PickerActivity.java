package com.android.gallery3d.app;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.gallery3d.ui.GLRootView;

public class PickerActivity extends AbstractGalleryActivity
  implements View.OnClickListener
{
  public void onClick(View paramView)
  {
    if (paramView.getId() != 2131558424)
      return;
    finish();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    boolean bool = getResources().getBoolean(2131689473);
    if (!bool)
    {
      requestWindowFeature(8);
      requestWindowFeature(9);
    }
    setContentView(2130968592);
    if (!bool)
      return;
    View localView = findViewById(2131558424);
    localView.setOnClickListener(this);
    localView.setVisibility(0);
    ((GLRootView)findViewById(2131558501)).setZOrderOnTop(true);
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(2131886093, paramMenu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 2131558677)
    {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.PickerActivity
 * JD-Core Version:    0.5.4
 */