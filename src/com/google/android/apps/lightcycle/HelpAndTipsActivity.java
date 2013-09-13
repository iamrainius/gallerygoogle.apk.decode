package com.google.android.apps.lightcycle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.apps.lightcycle.util.AnalyticsHelper;
import com.google.android.apps.lightcycle.util.AnalyticsHelper.Page;
import com.google.android.apps.lightcycle.util.UiUtil;

public class HelpAndTipsActivity extends Activity
{
  private static final int[] IMAGE_IDS = { 2130837642, 2130837643, 2130837644, 2130837645, 2130837646, 2130837647, 2130837648, 2130837649 };
  private int currentIndex = 0;
  private Button dismissButton;
  private ImageView illustration;
  private Button nextButton;
  private Button previousButton;

  private void dismiss()
  {
    finish();
  }

  private void next()
  {
    if (this.currentIndex < -1 + IMAGE_IDS.length)
      this.currentIndex = (1 + this.currentIndex);
    showIllustration(this.currentIndex);
  }

  private void previous()
  {
    if (this.currentIndex > 0)
      this.currentIndex = (-1 + this.currentIndex);
    showIllustration(this.currentIndex);
  }

  @SuppressLint({"NewApi"})
  private void setButtonEnabled(Button paramButton, boolean paramBoolean)
  {
    paramButton.setClickable(paramBoolean);
    float f;
    if (Build.VERSION.SDK_INT >= 11)
    {
      if (!paramBoolean)
        break label25;
      f = 1.0F;
    }
    while (true)
    {
      paramButton.setAlpha(f);
      return;
      label25: f = 0.5F;
    }
  }

  private void showIllustration(int paramInt)
  {
    boolean bool1 = true;
    this.illustration.setImageResource(IMAGE_IDS[paramInt]);
    Button localButton1 = this.previousButton;
    boolean bool2;
    label29: Button localButton2;
    if (this.currentIndex > 0)
    {
      bool2 = bool1;
      setButtonEnabled(localButton1, bool2);
      localButton2 = this.nextButton;
      if (this.currentIndex >= -1 + IMAGE_IDS.length)
        break label69;
    }
    while (true)
    {
      setButtonEnabled(localButton2, bool1);
      return;
      bool2 = false;
      break label29:
      label69: bool1 = false;
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968607);
    UiUtil.setDisplayHomeAsUpEnabled(this, true);
    this.illustration = ((ImageView)findViewById(2131558503));
    this.previousButton = ((Button)findViewById(2131558504));
    this.previousButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        HelpAndTipsActivity.this.previous();
      }
    });
    this.nextButton = ((Button)findViewById(2131558505));
    this.nextButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        HelpAndTipsActivity.this.next();
      }
    });
    this.dismissButton = ((Button)findViewById(2131558506));
    this.dismissButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        HelpAndTipsActivity.this.dismiss();
      }
    });
    showIllustration(0);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 16908332:
    }
    finish();
    return true;
  }

  protected void onResume()
  {
    super.onResume();
    AnalyticsHelper.getInstance(this).trackPage(AnalyticsHelper.Page.HELP);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.HelpAndTipsActivity
 * JD-Core Version:    0.5.4
 */