package com.android.gallery3d.common;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class Entry
{
  public static final String[] ID_PROJECTION = { "_id" };

  @Column("_id")
  public long id = 0L;

  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.FIELD})
  public static @interface Column
  {
    public abstract String defaultValue();

    public abstract boolean fullText();

    public abstract boolean indexed();

    public abstract boolean unique();

    public abstract String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.TYPE})
  public static @interface Table
  {
    public abstract String value();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.Entry
 * JD-Core Version:    0.5.4
 */