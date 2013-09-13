package android.support.v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

final class FragmentManagerImpl extends FragmentManager
{
  static final Interpolator ACCELERATE_CUBIC;
  static final Interpolator ACCELERATE_QUINT;
  static final int ANIM_DUR = 220;
  public static final int ANIM_STYLE_CLOSE_ENTER = 3;
  public static final int ANIM_STYLE_CLOSE_EXIT = 4;
  public static final int ANIM_STYLE_FADE_ENTER = 5;
  public static final int ANIM_STYLE_FADE_EXIT = 6;
  public static final int ANIM_STYLE_OPEN_ENTER = 1;
  public static final int ANIM_STYLE_OPEN_EXIT = 2;
  static boolean DEBUG = false;
  static final Interpolator DECELERATE_CUBIC;
  static final Interpolator DECELERATE_QUINT;
  static final boolean HONEYCOMB = false;
  static final String TAG = "FragmentManager";
  static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
  static final String TARGET_STATE_TAG = "android:target_state";
  static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
  static final String VIEW_STATE_TAG = "android:view_state";
  ArrayList<Fragment> mActive;
  FragmentActivity mActivity;
  ArrayList<Fragment> mAdded;
  ArrayList<Integer> mAvailBackStackIndices;
  ArrayList<Integer> mAvailIndices;
  ArrayList<BackStackRecord> mBackStack;
  ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
  ArrayList<BackStackRecord> mBackStackIndices;
  FragmentContainer mContainer;
  ArrayList<Fragment> mCreatedMenus;
  int mCurState = 0;
  boolean mDestroyed;
  Runnable mExecCommit = new Runnable()
  {
    public void run()
    {
      FragmentManagerImpl.this.execPendingActions();
    }
  };
  boolean mExecutingActions;
  boolean mHavePendingDeferredStart;
  boolean mNeedMenuInvalidate;
  String mNoTransactionsBecause;
  Fragment mParent;
  ArrayList<Runnable> mPendingActions;
  SparseArray<Parcelable> mStateArray = null;
  Bundle mStateBundle = null;
  boolean mStateSaved;
  Runnable[] mTmpActions;

  static
  {
    int i = Build.VERSION.SDK_INT;
    int j = 0;
    if (i >= 11)
      j = 1;
    HONEYCOMB = j;
    DECELERATE_QUINT = new DecelerateInterpolator(2.5F);
    DECELERATE_CUBIC = new DecelerateInterpolator(1.5F);
    ACCELERATE_QUINT = new AccelerateInterpolator(2.5F);
    ACCELERATE_CUBIC = new AccelerateInterpolator(1.5F);
  }

  private void checkStateLoss()
  {
    if (this.mStateSaved)
      throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
    if (this.mNoTransactionsBecause == null)
      return;
    throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
  }

  static Animation makeFadeAnimation(Context paramContext, float paramFloat1, float paramFloat2)
  {
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
    localAlphaAnimation.setInterpolator(DECELERATE_CUBIC);
    localAlphaAnimation.setDuration(220L);
    return localAlphaAnimation;
  }

  static Animation makeOpenCloseAnimation(Context paramContext, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    AnimationSet localAnimationSet = new AnimationSet(false);
    ScaleAnimation localScaleAnimation = new ScaleAnimation(paramFloat1, paramFloat2, paramFloat1, paramFloat2, 1, 0.5F, 1, 0.5F);
    localScaleAnimation.setInterpolator(DECELERATE_QUINT);
    localScaleAnimation.setDuration(220L);
    localAnimationSet.addAnimation(localScaleAnimation);
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat3, paramFloat4);
    localAlphaAnimation.setInterpolator(DECELERATE_CUBIC);
    localAlphaAnimation.setDuration(220L);
    localAnimationSet.addAnimation(localAlphaAnimation);
    return localAnimationSet;
  }

  public static int reverseTransit(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return 0;
    case 4097:
      return 8194;
    case 8194:
      return 4097;
    case 4099:
    }
    return 4099;
  }

  private void throwException(RuntimeException paramRuntimeException)
  {
    Log.e("FragmentManager", paramRuntimeException.getMessage());
    Log.e("FragmentManager", "Activity state:");
    PrintWriter localPrintWriter = new PrintWriter(new LogWriter("FragmentManager"));
    if (this.mActivity != null);
    try
    {
      this.mActivity.dump("  ", null, localPrintWriter, new String[0]);
      label59: throw paramRuntimeException;
    }
    catch (Exception localException2)
    {
      while (true)
      {
        Log.e("FragmentManager", "Failed dumping state", localException2);
        break label59:
        try
        {
          dump("  ", null, localPrintWriter, new String[0]);
        }
        catch (Exception localException1)
        {
          Log.e("FragmentManager", "Failed dumping state", localException1);
        }
      }
    }
  }

  public static int transitToStyleIndex(int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default:
      return -1;
    case 4097:
      if (paramBoolean)
        return 1;
      return 2;
    case 8194:
      if (paramBoolean)
        return 3;
      return 4;
    case 4099:
    }
    if (paramBoolean)
      return 5;
    return 6;
  }

  void addBackStackState(BackStackRecord paramBackStackRecord)
  {
    if (this.mBackStack == null)
      this.mBackStack = new ArrayList();
    this.mBackStack.add(paramBackStackRecord);
    reportBackStackChanged();
  }

  public void addFragment(Fragment paramFragment, boolean paramBoolean)
  {
    if (this.mAdded == null)
      this.mAdded = new ArrayList();
    if (DEBUG)
      Log.v("FragmentManager", "add: " + paramFragment);
    makeActive(paramFragment);
    if (paramFragment.mDetached)
      return;
    if (this.mAdded.contains(paramFragment))
      throw new IllegalStateException("Fragment already added: " + paramFragment);
    this.mAdded.add(paramFragment);
    paramFragment.mAdded = true;
    paramFragment.mRemoving = false;
    if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
      this.mNeedMenuInvalidate = true;
    if (!paramBoolean)
      return;
    moveToState(paramFragment);
  }

  public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners == null)
      this.mBackStackChangeListeners = new ArrayList();
    this.mBackStackChangeListeners.add(paramOnBackStackChangedListener);
  }

  public int allocBackStackIndex(BackStackRecord paramBackStackRecord)
  {
    monitorenter;
    try
    {
      if ((this.mAvailBackStackIndices == null) || (this.mAvailBackStackIndices.size() <= 0))
      {
        if (this.mBackStackIndices == null)
          this.mBackStackIndices = new ArrayList();
        int i = this.mBackStackIndices.size();
        if (DEBUG)
          Log.v("FragmentManager", "Setting back stack index " + i + " to " + paramBackStackRecord);
        this.mBackStackIndices.add(paramBackStackRecord);
        return i;
      }
      int j = ((Integer)this.mAvailBackStackIndices.remove(-1 + this.mAvailBackStackIndices.size())).intValue();
      if (DEBUG)
        Log.v("FragmentManager", "Adding back stack index " + j + " with " + paramBackStackRecord);
      this.mBackStackIndices.set(j, paramBackStackRecord);
      return j;
    }
    finally
    {
      monitorexit;
    }
  }

  public void attachActivity(FragmentActivity paramFragmentActivity, FragmentContainer paramFragmentContainer, Fragment paramFragment)
  {
    if (this.mActivity != null)
      throw new IllegalStateException("Already attached");
    this.mActivity = paramFragmentActivity;
    this.mContainer = paramFragmentContainer;
    this.mParent = paramFragment;
  }

  public void attachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG)
      Log.v("FragmentManager", "attach: " + paramFragment);
    if (!paramFragment.mDetached)
      return;
    paramFragment.mDetached = false;
    if (paramFragment.mAdded)
      return;
    if (this.mAdded == null)
      this.mAdded = new ArrayList();
    if (this.mAdded.contains(paramFragment))
      throw new IllegalStateException("Fragment already added: " + paramFragment);
    if (DEBUG)
      Log.v("FragmentManager", "add from attach: " + paramFragment);
    this.mAdded.add(paramFragment);
    paramFragment.mAdded = true;
    if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
      this.mNeedMenuInvalidate = true;
    moveToState(paramFragment, this.mCurState, paramInt1, paramInt2, false);
  }

  public FragmentTransaction beginTransaction()
  {
    return new BackStackRecord(this);
  }

  public void detachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG)
      Log.v("FragmentManager", "detach: " + paramFragment);
    if (paramFragment.mDetached)
      return;
    paramFragment.mDetached = true;
    if (!paramFragment.mAdded)
      return;
    if (this.mAdded != null)
    {
      if (DEBUG)
        Log.v("FragmentManager", "remove from detach: " + paramFragment);
      this.mAdded.remove(paramFragment);
    }
    if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
      this.mNeedMenuInvalidate = true;
    paramFragment.mAdded = false;
    moveToState(paramFragment, 1, paramInt1, paramInt2, false);
  }

  public void dispatchActivityCreated()
  {
    this.mStateSaved = false;
    moveToState(2, false);
  }

  public void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    if (this.mAdded == null)
      return;
    for (int i = 0; i < this.mAdded.size(); ++i)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment == null)
        continue;
      localFragment.performConfigurationChanged(paramConfiguration);
    }
  }

  public boolean dispatchContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mAdded != null)
      for (int i = 0; i < this.mAdded.size(); ++i)
      {
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        if ((localFragment != null) && (localFragment.performContextItemSelected(paramMenuItem)))
          return true;
      }
    return false;
  }

  public void dispatchCreate()
  {
    this.mStateSaved = false;
    moveToState(1, false);
  }

  public boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    ArrayList localArrayList1 = this.mAdded;
    ArrayList localArrayList2 = null;
    int i = 0;
    if (localArrayList1 != null)
      for (int k = 0; k < this.mAdded.size(); ++k)
      {
        Fragment localFragment2 = (Fragment)this.mAdded.get(k);
        if ((localFragment2 == null) || (!localFragment2.performCreateOptionsMenu(paramMenu, paramMenuInflater)))
          continue;
        i = 1;
        if (localArrayList2 == null)
          localArrayList2 = new ArrayList();
        localArrayList2.add(localFragment2);
      }
    if (this.mCreatedMenus != null)
      for (int j = 0; j < this.mCreatedMenus.size(); ++j)
      {
        Fragment localFragment1 = (Fragment)this.mCreatedMenus.get(j);
        if ((localArrayList2 != null) && (localArrayList2.contains(localFragment1)))
          continue;
        localFragment1.onDestroyOptionsMenu();
      }
    this.mCreatedMenus = localArrayList2;
    return i;
  }

  public void dispatchDestroy()
  {
    this.mDestroyed = true;
    execPendingActions();
    moveToState(0, false);
    this.mActivity = null;
    this.mContainer = null;
    this.mParent = null;
  }

  public void dispatchDestroyView()
  {
    moveToState(1, false);
  }

  public void dispatchLowMemory()
  {
    if (this.mAdded == null)
      return;
    for (int i = 0; i < this.mAdded.size(); ++i)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment == null)
        continue;
      localFragment.performLowMemory();
    }
  }

  public boolean dispatchOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (this.mAdded != null)
      for (int i = 0; i < this.mAdded.size(); ++i)
      {
        Fragment localFragment = (Fragment)this.mAdded.get(i);
        if ((localFragment != null) && (localFragment.performOptionsItemSelected(paramMenuItem)))
          return true;
      }
    return false;
  }

  public void dispatchOptionsMenuClosed(Menu paramMenu)
  {
    if (this.mAdded == null)
      return;
    for (int i = 0; i < this.mAdded.size(); ++i)
    {
      Fragment localFragment = (Fragment)this.mAdded.get(i);
      if (localFragment == null)
        continue;
      localFragment.performOptionsMenuClosed(paramMenu);
    }
  }

  public void dispatchPause()
  {
    moveToState(4, false);
  }

  public boolean dispatchPrepareOptionsMenu(Menu paramMenu)
  {
    ArrayList localArrayList = this.mAdded;
    int i = 0;
    if (localArrayList != null)
      for (int j = 0; j < this.mAdded.size(); ++j)
      {
        Fragment localFragment = (Fragment)this.mAdded.get(j);
        if ((localFragment == null) || (!localFragment.performPrepareOptionsMenu(paramMenu)))
          continue;
        i = 1;
      }
    return i;
  }

  public void dispatchReallyStop()
  {
    moveToState(2, false);
  }

  public void dispatchResume()
  {
    this.mStateSaved = false;
    moveToState(5, false);
  }

  public void dispatchStart()
  {
    this.mStateSaved = false;
    moveToState(4, false);
  }

  public void dispatchStop()
  {
    this.mStateSaved = true;
    moveToState(3, false);
  }

  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = paramString + "    ";
    if (this.mActive != null)
    {
      int i7 = this.mActive.size();
      if (i7 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Active Fragments in ");
        paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
        paramPrintWriter.println(":");
        for (int i8 = 0; i8 < i7; ++i8)
        {
          Fragment localFragment3 = (Fragment)this.mActive.get(i8);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i8);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment3);
          if (localFragment3 == null)
            continue;
          localFragment3.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        }
      }
    }
    if (this.mAdded != null)
    {
      int i5 = this.mAdded.size();
      if (i5 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Added Fragments:");
        for (int i6 = 0; i6 < i5; ++i6)
        {
          Fragment localFragment2 = (Fragment)this.mAdded.get(i6);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i6);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment2.toString());
        }
      }
    }
    if (this.mCreatedMenus != null)
    {
      int i3 = this.mCreatedMenus.size();
      if (i3 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Fragments Created Menus:");
        for (int i4 = 0; i4 < i3; ++i4)
        {
          Fragment localFragment1 = (Fragment)this.mCreatedMenus.get(i4);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i4);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment1.toString());
        }
      }
    }
    if (this.mBackStack != null)
    {
      int i1 = this.mBackStack.size();
      if (i1 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Back Stack:");
        for (int i2 = 0; i2 < i1; ++i2)
        {
          BackStackRecord localBackStackRecord2 = (BackStackRecord)this.mBackStack.get(i2);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i2);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localBackStackRecord2.toString());
          localBackStackRecord2.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        }
      }
    }
    monitorenter;
    int j;
    try
    {
      if (this.mBackStackIndices != null)
      {
        int k = this.mBackStackIndices.size();
        if (k > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Back Stack Indices:");
          for (int l = 0; l < k; ++l)
          {
            BackStackRecord localBackStackRecord1 = (BackStackRecord)this.mBackStackIndices.get(l);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(l);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localBackStackRecord1);
          }
        }
      }
      if ((this.mAvailBackStackIndices != null) && (this.mAvailBackStackIndices.size() > 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mAvailBackStackIndices: ");
        paramPrintWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
      }
      monitorexit;
      if (this.mPendingActions == null)
        break label694;
      int i = this.mPendingActions.size();
      if (i <= 0)
        break label694;
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Pending Actions:");
      j = 0;
      if (j >= i)
        break label694;
      Runnable localRunnable = (Runnable)this.mPendingActions.get(j);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  #");
      paramPrintWriter.print(j);
      paramPrintWriter.print(": ");
      paramPrintWriter.println(localRunnable);
    }
    finally
    {
      monitorexit;
    }
    label694: paramPrintWriter.print(paramString);
    paramPrintWriter.println("FragmentManager misc state:");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mActivity=");
    paramPrintWriter.println(this.mActivity);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mContainer=");
    paramPrintWriter.println(this.mContainer);
    if (this.mParent != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mParent=");
      paramPrintWriter.println(this.mParent);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mCurState=");
    paramPrintWriter.print(this.mCurState);
    paramPrintWriter.print(" mStateSaved=");
    paramPrintWriter.print(this.mStateSaved);
    paramPrintWriter.print(" mDestroyed=");
    paramPrintWriter.println(this.mDestroyed);
    if (this.mNeedMenuInvalidate)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNeedMenuInvalidate=");
      paramPrintWriter.println(this.mNeedMenuInvalidate);
    }
    if (this.mNoTransactionsBecause != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNoTransactionsBecause=");
      paramPrintWriter.println(this.mNoTransactionsBecause);
    }
    if ((this.mAvailIndices == null) || (this.mAvailIndices.size() <= 0))
      return;
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mAvailIndices: ");
    paramPrintWriter.println(Arrays.toString(this.mAvailIndices.toArray()));
  }

  public void enqueueAction(Runnable paramRunnable, boolean paramBoolean)
  {
    if (!paramBoolean)
      checkStateLoss();
    monitorenter;
    try
    {
      throw new IllegalStateException("Activity has been destroyed");
    }
    finally
    {
      monitorexit;
    }
    if (this.mPendingActions == null)
      this.mPendingActions = new ArrayList();
    this.mPendingActions.add(paramRunnable);
    if (this.mPendingActions.size() == 1)
    {
      this.mActivity.mHandler.removeCallbacks(this.mExecCommit);
      this.mActivity.mHandler.post(this.mExecCommit);
    }
    monitorexit;
  }

  public boolean execPendingActions()
  {
    if (this.mExecutingActions)
      throw new IllegalStateException("Recursive entry to executePendingTransactions");
    if (Looper.myLooper() != this.mActivity.mHandler.getLooper())
      throw new IllegalStateException("Must be called from main thread of process");
    boolean bool;
    for (int i = 0; ; i = 1)
    {
      monitorenter;
      int l;
      try
      {
        if ((this.mPendingActions == null) || (this.mPendingActions.size() == 0))
        {
          monitorexit;
          if (!this.mHavePendingDeferredStart)
            break label276;
          int j = 0;
          bool = false;
          while (true)
          {
            if (j >= this.mActive.size())
              break label262;
            Fragment localFragment = (Fragment)this.mActive.get(j);
            if ((localFragment != null) && (localFragment.mLoaderManager != null))
              bool |= localFragment.mLoaderManager.hasRunningLoaders();
            ++j;
          }
        }
        int k = this.mPendingActions.size();
        if ((this.mTmpActions == null) || (this.mTmpActions.length < k))
          this.mTmpActions = new Runnable[k];
        this.mPendingActions.toArray(this.mTmpActions);
        this.mPendingActions.clear();
        this.mActivity.mHandler.removeCallbacks(this.mExecCommit);
        monitorexit;
        this.mExecutingActions = true;
        l = 0;
        if (l >= k)
          break label252;
        this.mTmpActions[l].run();
        this.mTmpActions[l] = null;
      }
      finally
      {
        monitorexit;
      }
      label252: this.mExecutingActions = false;
    }
    if (!bool)
    {
      label262: this.mHavePendingDeferredStart = false;
      startPendingDeferredFragments();
    }
    label276: return i;
  }

  public boolean executePendingTransactions()
  {
    return execPendingActions();
  }

  public Fragment findFragmentById(int paramInt)
  {
    Fragment localFragment;
    if (this.mAdded != null)
      for (int j = -1 + this.mAdded.size(); j >= 0; --j)
      {
        localFragment = (Fragment)this.mAdded.get(j);
        if ((localFragment != null) && (localFragment.mFragmentId == paramInt))
          return localFragment;
      }
    if (this.mActive != null)
      for (int i = -1 + this.mActive.size(); i >= 0; --i)
      {
        localFragment = (Fragment)this.mActive.get(i);
        if ((localFragment != null) && (localFragment.mFragmentId == paramInt));
      }
    return null;
  }

  public Fragment findFragmentByTag(String paramString)
  {
    Fragment localFragment;
    if ((this.mAdded != null) && (paramString != null))
      for (int j = -1 + this.mAdded.size(); j >= 0; --j)
      {
        localFragment = (Fragment)this.mAdded.get(j);
        if ((localFragment != null) && (paramString.equals(localFragment.mTag)))
          return localFragment;
      }
    if ((this.mActive != null) && (paramString != null))
      for (int i = -1 + this.mActive.size(); i >= 0; --i)
      {
        localFragment = (Fragment)this.mActive.get(i);
        if ((localFragment != null) && (paramString.equals(localFragment.mTag)));
      }
    return null;
  }

  public Fragment findFragmentByWho(String paramString)
  {
    if ((this.mActive != null) && (paramString != null))
      for (int i = -1 + this.mActive.size(); i >= 0; --i)
      {
        Fragment localFragment1 = (Fragment)this.mActive.get(i);
        if (localFragment1 == null)
          continue;
        Fragment localFragment2 = localFragment1.findFragmentByWho(paramString);
        if (localFragment2 != null)
          return localFragment2;
      }
    return null;
  }

  public void freeBackStackIndex(int paramInt)
  {
    monitorenter;
    try
    {
      this.mBackStackIndices.set(paramInt, null);
      if (this.mAvailBackStackIndices == null)
        this.mAvailBackStackIndices = new ArrayList();
      if (DEBUG)
        Log.v("FragmentManager", "Freeing back stack index " + paramInt);
      this.mAvailBackStackIndices.add(Integer.valueOf(paramInt));
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public FragmentManager.BackStackEntry getBackStackEntryAt(int paramInt)
  {
    return (FragmentManager.BackStackEntry)this.mBackStack.get(paramInt);
  }

  public int getBackStackEntryCount()
  {
    if (this.mBackStack != null)
      return this.mBackStack.size();
    return 0;
  }

  public Fragment getFragment(Bundle paramBundle, String paramString)
  {
    int i = paramBundle.getInt(paramString, -1);
    Fragment localFragment;
    if (i == -1)
      localFragment = null;
    do
    {
      return localFragment;
      if (i >= this.mActive.size())
        throwException(new IllegalStateException("Fragement no longer exists for key " + paramString + ": index " + i));
      localFragment = (Fragment)this.mActive.get(i);
    }
    while (localFragment != null);
    throwException(new IllegalStateException("Fragement no longer exists for key " + paramString + ": index " + i));
    return localFragment;
  }

  public void hideFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG)
      Log.v("FragmentManager", "hide: " + paramFragment);
    if (paramFragment.mHidden)
      return;
    paramFragment.mHidden = true;
    if (paramFragment.mView != null)
    {
      Animation localAnimation = loadAnimation(paramFragment, paramInt1, true, paramInt2);
      if (localAnimation != null)
        paramFragment.mView.startAnimation(localAnimation);
      paramFragment.mView.setVisibility(8);
    }
    if ((paramFragment.mAdded) && (paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
      this.mNeedMenuInvalidate = true;
    paramFragment.onHiddenChanged(true);
  }

  Animation loadAnimation(Fragment paramFragment, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    Animation localAnimation1 = paramFragment.onCreateAnimation(paramInt1, paramBoolean, paramFragment.mNextAnim);
    if (localAnimation1 != null)
      return localAnimation1;
    if (paramFragment.mNextAnim != 0)
    {
      Animation localAnimation2 = AnimationUtils.loadAnimation(this.mActivity, paramFragment.mNextAnim);
      if (localAnimation2 != null)
        return localAnimation2;
    }
    if (paramInt1 == 0)
      return null;
    int i = transitToStyleIndex(paramInt1, paramBoolean);
    if (i < 0)
      return null;
    switch (i)
    {
    default:
      if ((paramInt2 == 0) && (this.mActivity.getWindow() != null))
        paramInt2 = this.mActivity.getWindow().getAttributes().windowAnimations;
      if (paramInt2 == 0)
        return null;
    case 1:
      return makeOpenCloseAnimation(this.mActivity, 1.125F, 1.0F, 0.0F, 1.0F);
    case 2:
      return makeOpenCloseAnimation(this.mActivity, 1.0F, 0.975F, 1.0F, 0.0F);
    case 3:
      return makeOpenCloseAnimation(this.mActivity, 0.975F, 1.0F, 0.0F, 1.0F);
    case 4:
      return makeOpenCloseAnimation(this.mActivity, 1.0F, 1.075F, 1.0F, 0.0F);
    case 5:
      return makeFadeAnimation(this.mActivity, 0.0F, 1.0F);
    case 6:
    }
    return makeFadeAnimation(this.mActivity, 1.0F, 0.0F);
    return null;
  }

  void makeActive(Fragment paramFragment)
  {
    if (paramFragment.mIndex >= 0)
      return;
    if ((this.mAvailIndices == null) || (this.mAvailIndices.size() <= 0))
    {
      if (this.mActive == null)
        this.mActive = new ArrayList();
      paramFragment.setIndex(this.mActive.size(), this.mParent);
      this.mActive.add(paramFragment);
    }
    while (true)
    {
      if (DEBUG);
      Log.v("FragmentManager", "Allocated fragment index " + paramFragment);
      return;
      paramFragment.setIndex(((Integer)this.mAvailIndices.remove(-1 + this.mAvailIndices.size())).intValue(), this.mParent);
      this.mActive.set(paramFragment.mIndex, paramFragment);
    }
  }

  void makeInactive(Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0)
      return;
    if (DEBUG)
      Log.v("FragmentManager", "Freeing fragment index " + paramFragment);
    this.mActive.set(paramFragment.mIndex, null);
    if (this.mAvailIndices == null)
      this.mAvailIndices = new ArrayList();
    this.mAvailIndices.add(Integer.valueOf(paramFragment.mIndex));
    this.mActivity.invalidateSupportFragment(paramFragment.mWho);
    paramFragment.initState();
  }

  void moveToState(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if ((this.mActivity == null) && (paramInt1 != 0))
      throw new IllegalStateException("No activity");
    if ((!paramBoolean) && (this.mCurState == paramInt1));
    do
    {
      do
      {
        return;
        this.mCurState = paramInt1;
      }
      while (this.mActive == null);
      boolean bool = false;
      for (int i = 0; i < this.mActive.size(); ++i)
      {
        Fragment localFragment = (Fragment)this.mActive.get(i);
        if (localFragment == null)
          continue;
        moveToState(localFragment, paramInt1, paramInt2, paramInt3, false);
        if (localFragment.mLoaderManager == null)
          continue;
        bool |= localFragment.mLoaderManager.hasRunningLoaders();
      }
      if (bool)
        continue;
      startPendingDeferredFragments();
    }
    while ((!this.mNeedMenuInvalidate) || (this.mActivity == null) || (this.mCurState != 5));
    this.mActivity.supportInvalidateOptionsMenu();
    this.mNeedMenuInvalidate = false;
  }

  void moveToState(int paramInt, boolean paramBoolean)
  {
    moveToState(paramInt, 0, 0, paramBoolean);
  }

  void moveToState(Fragment paramFragment)
  {
    moveToState(paramFragment, this.mCurState, 0, 0, false);
  }

  void moveToState(Fragment paramFragment, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if ((((!paramFragment.mAdded) || (paramFragment.mDetached))) && (paramInt1 > 1))
      paramInt1 = 1;
    if ((paramFragment.mRemoving) && (paramInt1 > paramFragment.mState))
      paramInt1 = paramFragment.mState;
    if ((paramFragment.mDeferStart) && (paramFragment.mState < 4) && (paramInt1 > 3))
      paramInt1 = 3;
    if (paramFragment.mState < paramInt1)
    {
      if ((paramFragment.mFromLayout) && (!paramFragment.mInLayout))
        return;
      if (paramFragment.mAnimatingAway != null)
      {
        paramFragment.mAnimatingAway = null;
        moveToState(paramFragment, paramFragment.mStateAfterAnimating, 0, 0, true);
      }
      switch (paramFragment.mState)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      }
    }
    while (true)
    {
      label148: paramFragment.mState = paramInt1;
      return;
      if (DEBUG)
        Log.v("FragmentManager", "moveto CREATED: " + paramFragment);
      if (paramFragment.mSavedFragmentState != null)
      {
        paramFragment.mSavedViewState = paramFragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
        paramFragment.mTarget = getFragment(paramFragment.mSavedFragmentState, "android:target_state");
        if (paramFragment.mTarget != null)
          paramFragment.mTargetRequestCode = paramFragment.mSavedFragmentState.getInt("android:target_req_state", 0);
        paramFragment.mUserVisibleHint = paramFragment.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
        if (!paramFragment.mUserVisibleHint)
        {
          paramFragment.mDeferStart = true;
          if (paramInt1 > 3)
            paramInt1 = 3;
        }
      }
      paramFragment.mActivity = this.mActivity;
      paramFragment.mParentFragment = this.mParent;
      if (this.mParent != null);
      for (FragmentManagerImpl localFragmentManagerImpl = this.mParent.mChildFragmentManager; ; localFragmentManagerImpl = this.mActivity.mFragments)
      {
        paramFragment.mFragmentManager = localFragmentManagerImpl;
        paramFragment.mCalled = false;
        paramFragment.onAttach(this.mActivity);
        if (paramFragment.mCalled)
          break;
        throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onAttach()");
      }
      if (paramFragment.mParentFragment == null)
        this.mActivity.onAttachFragment(paramFragment);
      if (!paramFragment.mRetaining)
        paramFragment.performCreate(paramFragment.mSavedFragmentState);
      paramFragment.mRetaining = false;
      if (paramFragment.mFromLayout)
      {
        paramFragment.mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(paramFragment.mSavedFragmentState), null, paramFragment.mSavedFragmentState);
        if (paramFragment.mView == null)
          break label899;
        paramFragment.mInnerView = paramFragment.mView;
        paramFragment.mView = NoSaveStateFrameLayout.wrap(paramFragment.mView);
        if (paramFragment.mHidden)
          paramFragment.mView.setVisibility(8);
        paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
      }
      if (paramInt1 > 1)
      {
        if (DEBUG)
          label495: Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + paramFragment);
        if (!paramFragment.mFromLayout)
        {
          int j = paramFragment.mContainerId;
          ViewGroup localViewGroup = null;
          if (j != 0)
          {
            localViewGroup = (ViewGroup)this.mContainer.findViewById(paramFragment.mContainerId);
            if ((localViewGroup == null) && (!paramFragment.mRestored))
              throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(paramFragment.mContainerId) + " (" + paramFragment.getResources().getResourceName(paramFragment.mContainerId) + ") for fragment " + paramFragment));
          }
          paramFragment.mContainer = localViewGroup;
          paramFragment.mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(paramFragment.mSavedFragmentState), localViewGroup, paramFragment.mSavedFragmentState);
          if (paramFragment.mView == null)
            break label907;
          paramFragment.mInnerView = paramFragment.mView;
          paramFragment.mView = NoSaveStateFrameLayout.wrap(paramFragment.mView);
          if (localViewGroup != null)
          {
            Animation localAnimation2 = loadAnimation(paramFragment, paramInt2, true, paramInt3);
            if (localAnimation2 != null)
              paramFragment.mView.startAnimation(localAnimation2);
            localViewGroup.addView(paramFragment.mView);
          }
          if (paramFragment.mHidden)
            paramFragment.mView.setVisibility(8);
          paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
        }
      }
      while (true)
      {
        paramFragment.performActivityCreated(paramFragment.mSavedFragmentState);
        if (paramFragment.mView != null)
          paramFragment.restoreViewState(paramFragment.mSavedFragmentState);
        paramFragment.mSavedFragmentState = null;
        if (paramInt1 > 3)
        {
          if (DEBUG)
            Log.v("FragmentManager", "moveto STARTED: " + paramFragment);
          paramFragment.performStart();
        }
        if (paramInt1 > 4);
        if (DEBUG)
          Log.v("FragmentManager", "moveto RESUMED: " + paramFragment);
        paramFragment.mResumed = true;
        paramFragment.performResume();
        paramFragment.mSavedFragmentState = null;
        paramFragment.mSavedViewState = null;
        break label148:
        label899: paramFragment.mInnerView = null;
        break label495:
        label907: paramFragment.mInnerView = null;
      }
      if (paramFragment.mState <= paramInt1)
        continue;
      switch (paramFragment.mState)
      {
      default:
        break;
      case 1:
      case 5:
      case 4:
      case 3:
      case 2:
      }
      while (true)
      {
        if (paramInt1 < 1);
        if ((this.mDestroyed) && (paramFragment.mAnimatingAway != null))
        {
          View localView = paramFragment.mAnimatingAway;
          paramFragment.mAnimatingAway = null;
          localView.clearAnimation();
        }
        if (paramFragment.mAnimatingAway == null)
          break;
        paramFragment.mStateAfterAnimating = paramInt1;
        paramInt1 = 1;
        break label148:
        if (paramInt1 < 5)
        {
          if (DEBUG)
            Log.v("FragmentManager", "movefrom RESUMED: " + paramFragment);
          paramFragment.performPause();
          paramFragment.mResumed = false;
        }
        if (paramInt1 < 4)
        {
          if (DEBUG)
            Log.v("FragmentManager", "movefrom STARTED: " + paramFragment);
          paramFragment.performStop();
        }
        if (paramInt1 < 3)
        {
          if (DEBUG)
            Log.v("FragmentManager", "movefrom STOPPED: " + paramFragment);
          paramFragment.performReallyStop();
        }
        if (paramInt1 >= 2)
          continue;
        if (DEBUG)
          Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + paramFragment);
        if ((paramFragment.mView != null) && (!this.mActivity.isFinishing()) && (paramFragment.mSavedViewState == null))
          saveFragmentViewState(paramFragment);
        paramFragment.performDestroyView();
        if ((paramFragment.mView != null) && (paramFragment.mContainer != null))
        {
          int i = this.mCurState;
          Animation localAnimation1 = null;
          if (i > 0)
          {
            boolean bool = this.mDestroyed;
            localAnimation1 = null;
            if (!bool)
              localAnimation1 = loadAnimation(paramFragment, paramInt2, false, paramInt3);
          }
          if (localAnimation1 != null)
          {
            paramFragment.mAnimatingAway = paramFragment.mView;
            paramFragment.mStateAfterAnimating = paramInt1;
            localAnimation1.setAnimationListener(new Animation.AnimationListener(paramFragment)
            {
              public void onAnimationEnd(Animation paramAnimation)
              {
                if (this.val$fragment.mAnimatingAway == null)
                  return;
                this.val$fragment.mAnimatingAway = null;
                FragmentManagerImpl.this.moveToState(this.val$fragment, this.val$fragment.mStateAfterAnimating, 0, 0, false);
              }

              public void onAnimationRepeat(Animation paramAnimation)
              {
              }

              public void onAnimationStart(Animation paramAnimation)
              {
              }
            });
            paramFragment.mView.startAnimation(localAnimation1);
          }
          paramFragment.mContainer.removeView(paramFragment.mView);
        }
        paramFragment.mContainer = null;
        paramFragment.mView = null;
        paramFragment.mInnerView = null;
      }
      if (DEBUG)
        Log.v("FragmentManager", "movefrom CREATED: " + paramFragment);
      if (!paramFragment.mRetaining)
        paramFragment.performDestroy();
      paramFragment.mCalled = false;
      paramFragment.onDetach();
      if (!paramFragment.mCalled)
        throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onDetach()");
      if (paramBoolean)
        continue;
      if (!paramFragment.mRetaining)
        makeInactive(paramFragment);
      paramFragment.mActivity = null;
      paramFragment.mFragmentManager = null;
    }
  }

  public void noteStateNotSaved()
  {
    this.mStateSaved = false;
  }

  public void performPendingDeferredStart(Fragment paramFragment)
  {
    if (paramFragment.mDeferStart)
    {
      if (!this.mExecutingActions)
        break label20;
      this.mHavePendingDeferredStart = true;
    }
    return;
    label20: paramFragment.mDeferStart = false;
    moveToState(paramFragment, this.mCurState, 0, 0, false);
  }

  public void popBackStack()
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, null, -1, 0);
      }
    }
    , false);
  }

  public void popBackStack(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Bad id: " + paramInt1);
    enqueueAction(new Runnable(paramInt1, paramInt2)
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, null, this.val$id, this.val$flags);
      }
    }
    , false);
  }

  public void popBackStack(String paramString, int paramInt)
  {
    enqueueAction(new Runnable(paramString, paramInt)
    {
      public void run()
      {
        FragmentManagerImpl.this.popBackStackState(FragmentManagerImpl.this.mActivity.mHandler, this.val$name, -1, this.val$flags);
      }
    }
    , false);
  }

  public boolean popBackStackImmediate()
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(this.mActivity.mHandler, null, -1, 0);
  }

  public boolean popBackStackImmediate(int paramInt1, int paramInt2)
  {
    checkStateLoss();
    executePendingTransactions();
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Bad id: " + paramInt1);
    return popBackStackState(this.mActivity.mHandler, null, paramInt1, paramInt2);
  }

  public boolean popBackStackImmediate(String paramString, int paramInt)
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(this.mActivity.mHandler, paramString, -1, paramInt);
  }

  boolean popBackStackState(Handler paramHandler, String paramString, int paramInt1, int paramInt2)
  {
    if (this.mBackStack == null);
    int i2;
    do
    {
      return false;
      if ((paramString != null) || (paramInt1 >= 0) || ((paramInt2 & 0x1) != 0))
        break label62;
      i2 = -1 + this.mBackStack.size();
    }
    while (i2 < 0);
    ((BackStackRecord)this.mBackStack.remove(i2)).popFromBackStack(true);
    reportBackStackChanged();
    while (true)
    {
      return true;
      label62: int i = -1;
      if ((paramString != null) || (paramInt1 >= 0))
      {
        for (int j = -1 + this.mBackStack.size(); ; --j)
        {
          BackStackRecord localBackStackRecord3;
          if (j >= 0)
          {
            localBackStackRecord3 = (BackStackRecord)this.mBackStack.get(j);
            if ((paramString == null) || (!paramString.equals(localBackStackRecord3.getName())))
              break label188;
          }
          do
          {
            if (j >= 0);
            if ((paramInt2 & 0x1) == 0)
              break label207;
            --j;
            while (true)
            {
              if (j < 0)
                break label207;
              BackStackRecord localBackStackRecord2 = (BackStackRecord)this.mBackStack.get(j);
              if ((((paramString == null) || (!paramString.equals(localBackStackRecord2.getName())))) && (((paramInt1 < 0) || (paramInt1 != localBackStackRecord2.mIndex))))
                break label207;
              label188: --j;
            }
          }
          while ((paramInt1 >= 0) && (paramInt1 == localBackStackRecord3.mIndex));
        }
        label207: i = j;
      }
      if (i != -1 + this.mBackStack.size());
      ArrayList localArrayList = new ArrayList();
      for (int k = -1 + this.mBackStack.size(); k > i; --k)
        localArrayList.add(this.mBackStack.remove(k));
      int l = -1 + localArrayList.size();
      int i1 = 0;
      if (i1 <= l)
      {
        if (DEBUG)
          label285: Log.v("FragmentManager", "Popping back stack state: " + localArrayList.get(i1));
        BackStackRecord localBackStackRecord1 = (BackStackRecord)localArrayList.get(i1);
        if (i1 == l);
        for (boolean bool = true; ; bool = false)
        {
          localBackStackRecord1.popFromBackStack(bool);
          ++i1;
          break label285:
        }
      }
      reportBackStackChanged();
    }
  }

  public void putFragment(Bundle paramBundle, String paramString, Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0)
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    paramBundle.putInt(paramString, paramFragment.mIndex);
  }

  public void removeFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG)
      Log.v("FragmentManager", "remove: " + paramFragment + " nesting=" + paramFragment.mBackStackNesting);
    int i;
    if (!paramFragment.isInBackStack())
    {
      i = 1;
      if ((!paramFragment.mDetached) || (i != 0))
      {
        if (this.mAdded != null)
          label55: this.mAdded.remove(paramFragment);
        if ((paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
          this.mNeedMenuInvalidate = true;
        paramFragment.mAdded = false;
        paramFragment.mRemoving = true;
        if (i == 0)
          break label137;
      }
    }
    for (int j = 0; ; j = 1)
    {
      moveToState(paramFragment, j, paramInt1, paramInt2, false);
      return;
      i = 0;
      label137: break label55:
    }
  }

  public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (this.mBackStackChangeListeners == null)
      return;
    this.mBackStackChangeListeners.remove(paramOnBackStackChangedListener);
  }

  void reportBackStackChanged()
  {
    if (this.mBackStackChangeListeners == null)
      return;
    for (int i = 0; i < this.mBackStackChangeListeners.size(); ++i)
      ((FragmentManager.OnBackStackChangedListener)this.mBackStackChangeListeners.get(i)).onBackStackChanged();
  }

  void restoreAllState(Parcelable paramParcelable, ArrayList<Fragment> paramArrayList)
  {
    if (paramParcelable == null);
    FragmentManagerState localFragmentManagerState;
    do
    {
      return;
      localFragmentManagerState = (FragmentManagerState)paramParcelable;
    }
    while (localFragmentManagerState.mActive == null);
    if (paramArrayList != null)
      for (int i1 = 0; i1 < paramArrayList.size(); ++i1)
      {
        Fragment localFragment4 = (Fragment)paramArrayList.get(i1);
        if (DEBUG)
          Log.v("FragmentManager", "restoreAllState: re-attaching retained " + localFragment4);
        FragmentState localFragmentState2 = localFragmentManagerState.mActive[localFragment4.mIndex];
        localFragmentState2.mInstance = localFragment4;
        localFragment4.mSavedViewState = null;
        localFragment4.mBackStackNesting = 0;
        localFragment4.mInLayout = false;
        localFragment4.mAdded = false;
        localFragment4.mTarget = null;
        if (localFragmentState2.mSavedFragmentState == null)
          continue;
        localFragmentState2.mSavedFragmentState.setClassLoader(this.mActivity.getClassLoader());
        localFragment4.mSavedViewState = localFragmentState2.mSavedFragmentState.getSparseParcelableArray("android:view_state");
      }
    this.mActive = new ArrayList(localFragmentManagerState.mActive.length);
    if (this.mAvailIndices != null)
      this.mAvailIndices.clear();
    int i = 0;
    if (i < localFragmentManagerState.mActive.length)
    {
      label203: FragmentState localFragmentState1 = localFragmentManagerState.mActive[i];
      if (localFragmentState1 != null)
      {
        Fragment localFragment3 = localFragmentState1.instantiate(this.mActivity, this.mParent);
        if (DEBUG)
          Log.v("FragmentManager", "restoreAllState: active #" + i + ": " + localFragment3);
        this.mActive.add(localFragment3);
        localFragmentState1.mInstance = null;
      }
      while (true)
      {
        ++i;
        break label203:
        this.mActive.add(null);
        if (this.mAvailIndices == null)
          this.mAvailIndices = new ArrayList();
        if (DEBUG)
          Log.v("FragmentManager", "restoreAllState: avail #" + i);
        this.mAvailIndices.add(Integer.valueOf(i));
      }
    }
    if (paramArrayList != null)
    {
      int l = 0;
      if (l < paramArrayList.size())
      {
        label391: Fragment localFragment2 = (Fragment)paramArrayList.get(l);
        if (localFragment2.mTargetIndex >= 0)
          if (localFragment2.mTargetIndex >= this.mActive.size())
            break label460;
        for (localFragment2.mTarget = ((Fragment)this.mActive.get(localFragment2.mTargetIndex)); ; localFragment2.mTarget = null)
        {
          ++l;
          break label391:
          label460: Log.w("FragmentManager", "Re-attaching retained fragment " + localFragment2 + " target no longer exists: " + localFragment2.mTargetIndex);
        }
      }
    }
    if (localFragmentManagerState.mAdded != null)
    {
      this.mAdded = new ArrayList(localFragmentManagerState.mAdded.length);
      for (int k = 0; ; ++k)
      {
        if (k >= localFragmentManagerState.mAdded.length)
          break label701;
        Fragment localFragment1 = (Fragment)this.mActive.get(localFragmentManagerState.mAdded[k]);
        if (localFragment1 == null)
          throwException(new IllegalStateException("No instantiated fragment for index #" + localFragmentManagerState.mAdded[k]));
        localFragment1.mAdded = true;
        if (DEBUG)
          Log.v("FragmentManager", "restoreAllState: added #" + k + ": " + localFragment1);
        if (this.mAdded.contains(localFragment1))
          throw new IllegalStateException("Already added!");
        this.mAdded.add(localFragment1);
      }
    }
    this.mAdded = null;
    if (localFragmentManagerState.mBackStack != null)
    {
      label701: this.mBackStack = new ArrayList(localFragmentManagerState.mBackStack.length);
      for (int j = 0; ; ++j)
      {
        if (j < localFragmentManagerState.mBackStack.length);
        BackStackRecord localBackStackRecord = localFragmentManagerState.mBackStack[j].instantiate(this);
        if (DEBUG)
        {
          Log.v("FragmentManager", "restoreAllState: back stack #" + j + " (index " + localBackStackRecord.mIndex + "): " + localBackStackRecord);
          localBackStackRecord.dump("  ", new PrintWriter(new LogWriter("FragmentManager")), false);
        }
        this.mBackStack.add(localBackStackRecord);
        if (localBackStackRecord.mIndex < 0)
          continue;
        setBackStackIndex(localBackStackRecord.mIndex, localBackStackRecord);
      }
    }
    this.mBackStack = null;
  }

  ArrayList<Fragment> retainNonConfig()
  {
    ArrayList localArrayList1 = this.mActive;
    ArrayList localArrayList2 = null;
    if (localArrayList1 != null)
    {
      int i = 0;
      if (i < this.mActive.size())
      {
        label13: Fragment localFragment = (Fragment)this.mActive.get(i);
        if ((localFragment != null) && (localFragment.mRetainInstance))
        {
          if (localArrayList2 == null)
            localArrayList2 = new ArrayList();
          localArrayList2.add(localFragment);
          localFragment.mRetaining = true;
          if (localFragment.mTarget == null)
            break label139;
        }
        for (int j = localFragment.mTarget.mIndex; ; j = -1)
        {
          localFragment.mTargetIndex = j;
          if (DEBUG)
            Log.v("FragmentManager", "retainNonConfig: keeping retained " + localFragment);
          ++i;
          label139: break label13:
        }
      }
    }
    return localArrayList2;
  }

  Parcelable saveAllState()
  {
    execPendingActions();
    if (HONEYCOMB)
      this.mStateSaved = true;
    if ((this.mActive == null) || (this.mActive.size() <= 0))
      return null;
    int i = this.mActive.size();
    FragmentState[] arrayOfFragmentState = new FragmentState[i];
    int j = 0;
    int k = 0;
    label54: Fragment localFragment;
    FragmentState localFragmentState;
    if (j < i)
    {
      localFragment = (Fragment)this.mActive.get(j);
      if (localFragment == null)
        break label736;
      if (localFragment.mIndex < 0)
        throwException(new IllegalStateException("Failure saving state: active " + localFragment + " has cleared index: " + localFragment.mIndex));
      localFragmentState = new FragmentState(localFragment);
      arrayOfFragmentState[j] = localFragmentState;
      if ((localFragment.mState > 0) && (localFragmentState.mSavedFragmentState == null))
      {
        localFragmentState.mSavedFragmentState = saveFragmentBasicState(localFragment);
        if (localFragment.mTarget != null)
        {
          if (localFragment.mTarget.mIndex < 0)
            throwException(new IllegalStateException("Failure saving state: " + localFragment + " has target not in fragment manager: " + localFragment.mTarget));
          if (localFragmentState.mSavedFragmentState == null)
            localFragmentState.mSavedFragmentState = new Bundle();
          putFragment(localFragmentState.mSavedFragmentState, "android:target_state", localFragment.mTarget);
          if (localFragment.mTargetRequestCode != 0)
            localFragmentState.mSavedFragmentState.putInt("android:target_req_state", localFragment.mTargetRequestCode);
        }
        if (DEBUG)
          label301: Log.v("FragmentManager", "Saved state of " + localFragment + ": " + localFragmentState.mSavedFragmentState);
      }
    }
    for (int i4 = 1; ; i4 = k)
    {
      ++j;
      k = i4;
      break label54:
      localFragmentState.mSavedFragmentState = localFragment.mSavedFragmentState;
      break label301:
      if (k == 0)
      {
        if (DEBUG);
        Log.v("FragmentManager", "saveAllState: no fragments!");
        return null;
      }
      if (this.mAdded != null)
      {
        int i2 = this.mAdded.size();
        if (i2 > 0)
        {
          arrayOfInt = new int[i2];
          for (int i3 = 0; ; ++i3)
          {
            if (i3 >= i2)
              break label574;
            arrayOfInt[i3] = ((Fragment)this.mAdded.get(i3)).mIndex;
            if (arrayOfInt[i3] < 0)
              throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i3) + " has cleared index: " + arrayOfInt[i3]));
            if (!DEBUG)
              continue;
            Log.v("FragmentManager", "saveAllState: adding fragment #" + i3 + ": " + this.mAdded.get(i3));
          }
        }
      }
      int[] arrayOfInt = null;
      label574: ArrayList localArrayList = this.mBackStack;
      BackStackState[] arrayOfBackStackState = null;
      if (localArrayList != null)
      {
        int l = this.mBackStack.size();
        arrayOfBackStackState = null;
        if (l > 0)
        {
          arrayOfBackStackState = new BackStackState[l];
          for (int i1 = 0; i1 < l; ++i1)
          {
            arrayOfBackStackState[i1] = new BackStackState(this, (BackStackRecord)this.mBackStack.get(i1));
            if (!DEBUG)
              continue;
            Log.v("FragmentManager", "saveAllState: adding back stack #" + i1 + ": " + this.mBackStack.get(i1));
          }
        }
      }
      FragmentManagerState localFragmentManagerState = new FragmentManagerState();
      localFragmentManagerState.mActive = arrayOfFragmentState;
      localFragmentManagerState.mAdded = arrayOfInt;
      localFragmentManagerState.mBackStack = arrayOfBackStackState;
      label736: return localFragmentManagerState;
    }
  }

  Bundle saveFragmentBasicState(Fragment paramFragment)
  {
    if (this.mStateBundle == null)
      this.mStateBundle = new Bundle();
    paramFragment.performSaveInstanceState(this.mStateBundle);
    boolean bool = this.mStateBundle.isEmpty();
    Bundle localBundle = null;
    if (!bool)
    {
      localBundle = this.mStateBundle;
      this.mStateBundle = null;
    }
    if (paramFragment.mView != null)
      saveFragmentViewState(paramFragment);
    if (paramFragment.mSavedViewState != null)
    {
      if (localBundle == null)
        localBundle = new Bundle();
      localBundle.putSparseParcelableArray("android:view_state", paramFragment.mSavedViewState);
    }
    if (!paramFragment.mUserVisibleHint)
    {
      if (localBundle == null)
        localBundle = new Bundle();
      localBundle.putBoolean("android:user_visible_hint", paramFragment.mUserVisibleHint);
    }
    return localBundle;
  }

  public Fragment.SavedState saveFragmentInstanceState(Fragment paramFragment)
  {
    if (paramFragment.mIndex < 0)
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    int i = paramFragment.mState;
    Fragment.SavedState localSavedState = null;
    if (i > 0)
    {
      Bundle localBundle = saveFragmentBasicState(paramFragment);
      localSavedState = null;
      if (localBundle != null)
        localSavedState = new Fragment.SavedState(localBundle);
    }
    return localSavedState;
  }

  void saveFragmentViewState(Fragment paramFragment)
  {
    if (paramFragment.mInnerView == null)
      return;
    if (this.mStateArray == null)
      this.mStateArray = new SparseArray();
    while (true)
    {
      paramFragment.mInnerView.saveHierarchyState(this.mStateArray);
      if (this.mStateArray.size() > 0);
      paramFragment.mSavedViewState = this.mStateArray;
      this.mStateArray = null;
      return;
      this.mStateArray.clear();
    }
  }

  public void setBackStackIndex(int paramInt, BackStackRecord paramBackStackRecord)
  {
    monitorenter;
    try
    {
      if (this.mBackStackIndices == null)
        this.mBackStackIndices = new ArrayList();
      int i = this.mBackStackIndices.size();
      if (paramInt < i)
      {
        if (DEBUG)
          Log.v("FragmentManager", "Setting back stack index " + paramInt + " to " + paramBackStackRecord);
        this.mBackStackIndices.set(paramInt, paramBackStackRecord);
        return;
      }
      while (i < paramInt)
      {
        this.mBackStackIndices.add(null);
        if (this.mAvailBackStackIndices == null)
          this.mAvailBackStackIndices = new ArrayList();
        if (DEBUG)
          Log.v("FragmentManager", "Adding available back stack index " + i);
        this.mAvailBackStackIndices.add(Integer.valueOf(i));
        ++i;
      }
      if (DEBUG)
        Log.v("FragmentManager", "Adding back stack index " + paramInt + " with " + paramBackStackRecord);
    }
    finally
    {
      monitorexit;
    }
  }

  public void showFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG)
      Log.v("FragmentManager", "show: " + paramFragment);
    if (!paramFragment.mHidden)
      return;
    paramFragment.mHidden = false;
    if (paramFragment.mView != null)
    {
      Animation localAnimation = loadAnimation(paramFragment, paramInt1, true, paramInt2);
      if (localAnimation != null)
        paramFragment.mView.startAnimation(localAnimation);
      paramFragment.mView.setVisibility(0);
    }
    if ((paramFragment.mAdded) && (paramFragment.mHasMenu) && (paramFragment.mMenuVisible))
      this.mNeedMenuInvalidate = true;
    paramFragment.onHiddenChanged(false);
  }

  void startPendingDeferredFragments()
  {
    if (this.mActive == null)
      return;
    for (int i = 0; ; ++i)
    {
      if (i < this.mActive.size());
      Fragment localFragment = (Fragment)this.mActive.get(i);
      if (localFragment == null)
        continue;
      performPendingDeferredStart(localFragment);
    }
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("FragmentManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    if (this.mParent != null)
      DebugUtils.buildShortClassTag(this.mParent, localStringBuilder);
    while (true)
    {
      localStringBuilder.append("}}");
      return localStringBuilder.toString();
      DebugUtils.buildShortClassTag(this.mActivity, localStringBuilder);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     android.support.v4.app.FragmentManagerImpl
 * JD-Core Version:    0.5.4
 */