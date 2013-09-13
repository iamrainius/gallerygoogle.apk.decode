package com.android.camera;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.camera.ui.Rotatable;
import com.android.camera.ui.RotateLayout;

public class RotateDialogController
  implements Rotatable
{
  private Activity mActivity;
  private View mDialogRootLayout;
  private Animation mFadeInAnim;
  private Animation mFadeOutAnim;
  private int mLayoutResourceID;
  private RotateLayout mRotateDialog;
  private TextView mRotateDialogButton1;
  private TextView mRotateDialogButton2;
  private View mRotateDialogButtonLayout;
  private ProgressBar mRotateDialogSpinner;
  private TextView mRotateDialogText;
  private TextView mRotateDialogTitle;
  private View mRotateDialogTitleLayout;

  public RotateDialogController(Activity paramActivity, int paramInt)
  {
    this.mActivity = paramActivity;
    this.mLayoutResourceID = paramInt;
  }

  private void fadeInDialog()
  {
    this.mDialogRootLayout.startAnimation(this.mFadeInAnim);
    this.mDialogRootLayout.setVisibility(0);
  }

  private void fadeOutDialog()
  {
    this.mDialogRootLayout.startAnimation(this.mFadeOutAnim);
    this.mDialogRootLayout.setVisibility(8);
  }

  private void inflateDialogLayout()
  {
    if (this.mDialogRootLayout != null)
      return;
    ViewGroup localViewGroup = (ViewGroup)this.mActivity.getWindow().getDecorView();
    View localView = this.mActivity.getLayoutInflater().inflate(this.mLayoutResourceID, localViewGroup);
    this.mDialogRootLayout = localView.findViewById(2131558599);
    this.mRotateDialog = ((RotateLayout)localView.findViewById(2131558600));
    this.mRotateDialogTitleLayout = localView.findViewById(2131558601);
    this.mRotateDialogButtonLayout = localView.findViewById(2131558605);
    this.mRotateDialogTitle = ((TextView)localView.findViewById(2131558602));
    this.mRotateDialogSpinner = ((ProgressBar)localView.findViewById(2131558603));
    this.mRotateDialogText = ((TextView)localView.findViewById(2131558604));
    this.mRotateDialogButton1 = ((Button)localView.findViewById(2131558607));
    this.mRotateDialogButton2 = ((Button)localView.findViewById(2131558606));
    this.mFadeInAnim = AnimationUtils.loadAnimation(this.mActivity, 17432576);
    this.mFadeOutAnim = AnimationUtils.loadAnimation(this.mActivity, 17432577);
    this.mFadeInAnim.setDuration(150L);
    this.mFadeOutAnim.setDuration(150L);
  }

  public void dismissDialog()
  {
    if ((this.mDialogRootLayout == null) || (this.mDialogRootLayout.getVisibility() == 8))
      return;
    fadeOutDialog();
  }

  public void resetRotateDialog()
  {
    inflateDialogLayout();
    this.mRotateDialogTitleLayout.setVisibility(8);
    this.mRotateDialogSpinner.setVisibility(8);
    this.mRotateDialogButton1.setVisibility(8);
    this.mRotateDialogButton2.setVisibility(8);
    this.mRotateDialogButtonLayout.setVisibility(8);
  }

  public void setOrientation(int paramInt, boolean paramBoolean)
  {
    inflateDialogLayout();
    this.mRotateDialog.setOrientation(paramInt, paramBoolean);
  }

  public void showAlertDialog(String paramString1, String paramString2, String paramString3, Runnable paramRunnable1, String paramString4, Runnable paramRunnable2)
  {
    resetRotateDialog();
    if (paramString1 != null)
    {
      this.mRotateDialogTitle.setText(paramString1);
      this.mRotateDialogTitleLayout.setVisibility(0);
    }
    this.mRotateDialogText.setText(paramString2);
    if (paramString3 != null)
    {
      this.mRotateDialogButton1.setText(paramString3);
      this.mRotateDialogButton1.setContentDescription(paramString3);
      this.mRotateDialogButton1.setVisibility(0);
      this.mRotateDialogButton1.setOnClickListener(new View.OnClickListener(paramRunnable1)
      {
        public void onClick(View paramView)
        {
          if (this.val$r1 != null)
            this.val$r1.run();
          RotateDialogController.this.dismissDialog();
        }
      });
      this.mRotateDialogButtonLayout.setVisibility(0);
    }
    if (paramString4 != null)
    {
      this.mRotateDialogButton2.setText(paramString4);
      this.mRotateDialogButton2.setContentDescription(paramString4);
      this.mRotateDialogButton2.setVisibility(0);
      this.mRotateDialogButton2.setOnClickListener(new View.OnClickListener(paramRunnable2)
      {
        public void onClick(View paramView)
        {
          if (this.val$r2 != null)
            this.val$r2.run();
          RotateDialogController.this.dismissDialog();
        }
      });
      this.mRotateDialogButtonLayout.setVisibility(0);
    }
    fadeInDialog();
  }

  public void showWaitingDialog(String paramString)
  {
    resetRotateDialog();
    this.mRotateDialogText.setText(paramString);
    this.mRotateDialogSpinner.setVisibility(0);
    fadeInDialog();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.RotateDialogController
 * JD-Core Version:    0.5.4
 */