package com.android.gallery3d.app;

import android.content.Intent;
import android.os.Bundle;
import com.android.gallery3d.data.DataManager;

public class AlbumPicker extends PickerActivity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setTitle(2131362204);
    Bundle localBundle1 = getIntent().getExtras();
    if (localBundle1 == null);
    for (Bundle localBundle2 = new Bundle(); ; localBundle2 = new Bundle(localBundle1))
    {
      localBundle2.putBoolean("get-album", true);
      localBundle2.putString("media-path", getDataManager().getTopSetPath(1));
      getStateManager().startState(AlbumSetPage.class, localBundle2);
      return;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.AlbumPicker
 * JD-Core Version:    0.5.4
 */