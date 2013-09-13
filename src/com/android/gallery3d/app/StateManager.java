package com.android.gallery3d.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import com.android.gallery3d.anim.StateTransitionAnimation.Transition;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.ui.GLRoot;
import java.util.Iterator;
import java.util.Stack;

public class StateManager
{
  private AbstractGalleryActivity mActivity;
  private boolean mIsResumed = false;
  private ActivityState.ResultEntry mResult;
  private Stack<StateEntry> mStack = new Stack();

  public StateManager(AbstractGalleryActivity paramAbstractGalleryActivity)
  {
    this.mActivity = paramAbstractGalleryActivity;
  }

  public void clearActivityResult()
  {
    if (this.mStack.isEmpty())
      return;
    getTopState().clearStateResult();
  }

  public void clearTasks()
  {
    while (this.mStack.size() > 1)
      ((StateEntry)this.mStack.pop()).activityState.onDestroy();
  }

  public boolean createOptionsMenu(Menu paramMenu)
  {
    if (this.mStack.isEmpty())
      return false;
    return getTopState().onCreateActionBar(paramMenu);
  }

  public void destroy()
  {
    Log.v("StateManager", "destroy");
    while (!this.mStack.isEmpty())
      ((StateEntry)this.mStack.pop()).activityState.onDestroy();
    this.mStack.clear();
  }

  void finishState(ActivityState paramActivityState)
  {
    finishState(paramActivityState, true);
  }

  void finishState(ActivityState paramActivityState, boolean paramBoolean)
  {
    if (this.mStack.size() == 1)
    {
      Activity localActivity = (Activity)this.mActivity.getAndroidContext();
      if (this.mResult != null)
        localActivity.setResult(this.mResult.resultCode, this.mResult.resultData);
      localActivity.finish();
      if (!localActivity.isFinishing())
      {
        Log.w("StateManager", "finish is rejected, keep the last state");
        return;
      }
      Log.v("StateManager", "no more state, finish activity");
    }
    Log.v("StateManager", "finishState " + paramActivityState);
    if (paramActivityState != ((StateEntry)this.mStack.peek()).activityState)
    {
      if (paramActivityState.isDestroyed())
      {
        Log.d("StateManager", "The state is already destroyed");
        return;
      }
      throw new IllegalArgumentException("The stateview to be finished is not at the top of the stack: " + paramActivityState + ", " + ((StateEntry)this.mStack.peek()).activityState);
    }
    this.mStack.pop();
    paramActivityState.mIsFinishing = true;
    if (!this.mStack.isEmpty());
    for (ActivityState localActivityState = ((StateEntry)this.mStack.peek()).activityState; ; localActivityState = null)
    {
      if ((this.mIsResumed) && (paramBoolean))
      {
        if (localActivityState != null)
          paramActivityState.transitionOnNextPause(paramActivityState.getClass(), localActivityState.getClass(), StateTransitionAnimation.Transition.Outgoing);
        paramActivityState.onPause();
      }
      this.mActivity.getGLRoot().setContentPane(null);
      paramActivityState.onDestroy();
      if ((localActivityState != null) && (this.mIsResumed));
      localActivityState.resume();
      return;
    }
  }

  public int getStateCount()
  {
    return this.mStack.size();
  }

  public ActivityState getTopState()
  {
    if (!this.mStack.isEmpty());
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      return ((StateEntry)this.mStack.peek()).activityState;
    }
  }

  public boolean hasStateClass(Class<? extends ActivityState> paramClass)
  {
    Iterator localIterator = this.mStack.iterator();
    while (localIterator.hasNext())
      if (paramClass.isInstance(((StateEntry)localIterator.next()).activityState))
        return true;
    return false;
  }

  public boolean itemSelected(MenuItem paramMenuItem)
  {
    if (!this.mStack.isEmpty())
    {
      if (getTopState().onItemSelected(paramMenuItem));
      do
      {
        return true;
        if (paramMenuItem.getItemId() != 16908332)
          break label54;
      }
      while (this.mStack.size() <= 1);
      getTopState().onBackPressed();
      return true;
    }
    label54: return false;
  }

  public void notifyActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    getTopState().onStateResult(paramInt1, paramInt2, paramIntent);
  }

  public void onBackPressed()
  {
    if (this.mStack.isEmpty())
      return;
    getTopState().onBackPressed();
  }

  public void onConfigurationChange(Configuration paramConfiguration)
  {
    Iterator localIterator = this.mStack.iterator();
    while (localIterator.hasNext())
      ((StateEntry)localIterator.next()).activityState.onConfigurationChanged(paramConfiguration);
  }

  public void pause()
  {
    if (!this.mIsResumed);
    do
    {
      return;
      this.mIsResumed = false;
    }
    while (this.mStack.isEmpty());
    getTopState().onPause();
  }

  public void restoreFromState(Bundle paramBundle)
  {
    Log.v("StateManager", "restoreFromState");
    Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray("activity-state");
    int i = arrayOfParcelable.length;
    int j = 0;
    while (j < i)
    {
      Bundle localBundle1 = (Bundle)arrayOfParcelable[j];
      Class localClass = (Class)localBundle1.getSerializable("class");
      Bundle localBundle2 = localBundle1.getBundle("data");
      Bundle localBundle3 = localBundle1.getBundle("bundle");
      try
      {
        Log.v("StateManager", "restoreFromState " + localClass);
        ActivityState localActivityState = (ActivityState)localClass.newInstance();
        localActivityState.initialize(this.mActivity, localBundle2);
        localActivityState.onCreate(localBundle2, localBundle3);
        this.mStack.push(new StateEntry(localBundle2, localActivityState));
        ++j;
      }
      catch (Exception localException)
      {
        throw new AssertionError(localException);
      }
    }
  }

  public void resume()
  {
    if (this.mIsResumed);
    do
    {
      return;
      this.mIsResumed = true;
    }
    while (this.mStack.isEmpty());
    getTopState().resume();
  }

  public void saveState(Bundle paramBundle)
  {
    Log.v("StateManager", "saveState");
    Parcelable[] arrayOfParcelable = new Parcelable[this.mStack.size()];
    int i = 0;
    Iterator localIterator = this.mStack.iterator();
    while (localIterator.hasNext())
    {
      StateEntry localStateEntry = (StateEntry)localIterator.next();
      Bundle localBundle1 = new Bundle();
      localBundle1.putSerializable("class", localStateEntry.activityState.getClass());
      localBundle1.putBundle("data", localStateEntry.data);
      Bundle localBundle2 = new Bundle();
      localStateEntry.activityState.onSaveState(localBundle2);
      localBundle1.putBundle("bundle", localBundle2);
      Log.v("StateManager", "saveState " + localStateEntry.activityState.getClass());
      int j = i + 1;
      arrayOfParcelable[i] = localBundle1;
      i = j;
    }
    paramBundle.putParcelableArray("activity-state", arrayOfParcelable);
  }

  public void startState(Class<? extends ActivityState> paramClass, Bundle paramBundle)
  {
    Log.v("StateManager", "startState " + paramClass);
    try
    {
      ActivityState localActivityState1 = (ActivityState)paramClass.newInstance();
      if (!this.mStack.isEmpty())
      {
        ActivityState localActivityState2 = getTopState();
        localActivityState2.transitionOnNextPause(localActivityState2.getClass(), paramClass, StateTransitionAnimation.Transition.Incoming);
        if (this.mIsResumed)
          localActivityState2.onPause();
      }
      localActivityState1.initialize(this.mActivity, paramBundle);
      this.mStack.push(new StateEntry(paramBundle, localActivityState1));
      localActivityState1.onCreate(paramBundle, null);
      if (this.mIsResumed)
        localActivityState1.resume();
      return;
    }
    catch (Exception localException)
    {
      throw new AssertionError(localException);
    }
  }

  public void startStateForResult(Class<? extends ActivityState> paramClass, int paramInt, Bundle paramBundle)
  {
    Log.v("StateManager", "startStateForResult " + paramClass + ", " + paramInt);
    while (true)
    {
      ActivityState localActivityState1;
      try
      {
        localActivityState1 = (ActivityState)paramClass.newInstance();
        localActivityState1.initialize(this.mActivity, paramBundle);
        localActivityState1.mResult = new ActivityState.ResultEntry();
        localActivityState1.mResult.requestCode = paramInt;
        if (!this.mStack.isEmpty())
        {
          ActivityState localActivityState2 = getTopState();
          localActivityState2.transitionOnNextPause(localActivityState2.getClass(), paramClass, StateTransitionAnimation.Transition.Incoming);
          localActivityState2.mReceivedResults = localActivityState1.mResult;
          if (this.mIsResumed)
            localActivityState2.onPause();
          this.mStack.push(new StateEntry(paramBundle, localActivityState1));
          localActivityState1.onCreate(paramBundle, null);
          if (this.mIsResumed)
            localActivityState1.resume();
          return;
        }
      }
      catch (Exception localException)
      {
        throw new AssertionError(localException);
      }
      this.mResult = localActivityState1.mResult;
    }
  }

  public void switchState(ActivityState paramActivityState, Class<? extends ActivityState> paramClass, Bundle paramBundle)
  {
    Log.v("StateManager", "switchState " + paramActivityState + ", " + paramClass);
    if (paramActivityState != ((StateEntry)this.mStack.peek()).activityState)
      throw new IllegalArgumentException("The stateview to be finished is not at the top of the stack: " + paramActivityState + ", " + ((StateEntry)this.mStack.peek()).activityState);
    this.mStack.pop();
    if (!paramBundle.containsKey("app-bridge"))
      paramActivityState.transitionOnNextPause(paramActivityState.getClass(), paramClass, StateTransitionAnimation.Transition.Incoming);
    if (this.mIsResumed)
      paramActivityState.onPause();
    paramActivityState.onDestroy();
    try
    {
      ActivityState localActivityState = (ActivityState)paramClass.newInstance();
      localActivityState.initialize(this.mActivity, paramBundle);
      this.mStack.push(new StateEntry(paramBundle, localActivityState));
      localActivityState.onCreate(paramBundle, null);
      if (this.mIsResumed)
        localActivityState.resume();
      return;
    }
    catch (Exception localException)
    {
      throw new AssertionError(localException);
    }
  }

  private static class StateEntry
  {
    public ActivityState activityState;
    public Bundle data;

    public StateEntry(Bundle paramBundle, ActivityState paramActivityState)
    {
      this.data = paramBundle;
      this.activityState = paramActivityState;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.StateManager
 * JD-Core Version:    0.5.4
 */