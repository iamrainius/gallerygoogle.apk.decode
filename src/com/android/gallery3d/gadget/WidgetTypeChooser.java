package com.android.gallery3d.gadget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class WidgetTypeChooser extends Activity
{
  private RadioGroup.OnCheckedChangeListener mListener = new RadioGroup.OnCheckedChangeListener()
  {
    public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt)
    {
      Intent localIntent = new Intent().putExtra("widget-type", paramInt);
      WidgetTypeChooser.this.setResult(-1, localIntent);
      WidgetTypeChooser.this.finish();
    }
  };

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setTitle(2131362310);
    setContentView(2130968588);
    ((RadioGroup)findViewById(2131558420)).setOnCheckedChangeListener(this.mListener);
    ((Button)findViewById(2131558424)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        WidgetTypeChooser.this.setResult(0);
        WidgetTypeChooser.this.finish();
      }
    });
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.WidgetTypeChooser
 * JD-Core Version:    0.5.4
 */