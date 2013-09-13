package com.google.android.picasasync;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Entry.Column;
import com.android.gallery3d.common.Entry.Table;
import com.android.gallery3d.common.EntrySchema;

@Entry.Table("users")
public final class UserEntry extends Entry
{
  public static final EntrySchema SCHEMA = new EntrySchema(UserEntry.class);

  @Entry.Column(indexed=true, value="account")
  public String account;

  @Entry.Column("albums_etag")
  public String albumsEtag;

  @Entry.Column("sync_states")
  public int syncStates;

  public UserEntry()
  {
  }

  public UserEntry(String paramString)
  {
    this.account = paramString;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.UserEntry
 * JD-Core Version:    0.5.4
 */