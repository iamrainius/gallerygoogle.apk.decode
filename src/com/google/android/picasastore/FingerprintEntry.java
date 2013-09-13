package com.google.android.picasastore;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Entry.Column;
import com.android.gallery3d.common.Entry.Table;
import com.android.gallery3d.common.EntrySchema;
import com.android.gallery3d.common.Fingerprint;

@Entry.Table("fingerprints")
public final class FingerprintEntry extends Entry
{
  public static final EntrySchema SCHEMA = new EntrySchema(FingerprintEntry.class);

  @Entry.Column(indexed=true, value="content_uri")
  public final String contentUri;

  @Entry.Column("fingerprint")
  public final byte[] rawFingerprint;

  public FingerprintEntry(String paramString, Fingerprint paramFingerprint)
  {
    this.contentUri = paramString;
    this.rawFingerprint = paramFingerprint.getBytes();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.FingerprintEntry
 * JD-Core Version:    0.5.4
 */