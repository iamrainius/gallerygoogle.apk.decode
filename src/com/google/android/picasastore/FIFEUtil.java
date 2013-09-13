package com.google.android.picasastore;

import android.net.Uri;
import android.net.Uri.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FIFEUtil
{
  private static final Pattern FIFE_HOSTED_IMAGE_URL_RE;
  private static final Joiner JOIN_ON_SLASH;
  private static final Splitter SPLIT_ON_EQUALS = Splitter.on("=").omitEmptyStrings();
  private static final Splitter SPLIT_ON_SLASH = Splitter.on("/").omitEmptyStrings();

  static
  {
    JOIN_ON_SLASH = Joiner.on("/");
    FIFE_HOSTED_IMAGE_URL_RE = Pattern.compile("^((http(s)?):)?\\/\\/((((lh[3-6]\\.((ggpht)|(googleusercontent)|(google)))|([1-4]\\.bp\\.blogspot)|(bp[0-3]\\.blogger))\\.com)|(www\\.google\\.com\\/visualsearch\\/lh))\\/");
  }

  private static String getContentImageUriOptions(Uri paramUri)
  {
    ArrayList localArrayList = newArrayList(SPLIT_ON_EQUALS.split(paramUri.getPath()));
    if (localArrayList.size() > 1)
      return (String)localArrayList.get(1);
    return "";
  }

  public static String getImageUriOptions(Uri paramUri)
  {
    ArrayList localArrayList = newArrayList(SPLIT_ON_SLASH.split(paramUri.getPath()));
    int i = localArrayList.size();
    if ((localArrayList.size() > 1) && (((String)localArrayList.get(0)).equals("image")))
      --i;
    if ((i >= 4) && (i <= 6))
      return getLegacyImageUriOptions(paramUri);
    if (i == 1)
      return getContentImageUriOptions(paramUri);
    return "";
  }

  public static String getImageUrlOptions(String paramString)
  {
    return getImageUriOptions(Uri.parse(paramString));
  }

  private static String getLegacyImageUriOptions(Uri paramUri)
  {
    int i = 1;
    String str = paramUri.getPath();
    ArrayList localArrayList = newArrayList(SPLIT_ON_SLASH.split(str));
    if ((localArrayList.size() > 0) && (((String)localArrayList.get(0)).equals("image")))
      localArrayList.remove(0);
    int j = localArrayList.size();
    int k;
    if ((!str.endsWith("/")) && (j == 5))
    {
      k = i;
      label79: if (j != 4)
        break label99;
    }
    while (k != 0)
    {
      return "";
      k = 0;
      break label79:
      label99: i = 0;
    }
    if (i == 0)
      return (String)localArrayList.get(4);
    return "";
  }

  public static boolean isFifeHostedUrl(String paramString)
  {
    if (paramString == null)
      return false;
    return FIFE_HOSTED_IMAGE_URL_RE.matcher(paramString).find();
  }

  private static <E> ArrayList<E> newArrayList(Iterable<? extends E> paramIterable)
  {
    if (paramIterable instanceof Collection)
    {
      localArrayList = new ArrayList((Collection)paramIterable);
      return localArrayList;
    }
    Iterator localIterator = paramIterable.iterator();
    ArrayList localArrayList = new ArrayList();
    while (true)
    {
      if (localIterator.hasNext());
      localArrayList.add(localIterator.next());
    }
  }

  private static Uri setContentImageUrlOptions(String paramString, Uri paramUri)
  {
    ArrayList localArrayList = newArrayList(SPLIT_ON_EQUALS.split(paramUri.getPath()));
    String str = (String)localArrayList.get(0) + "=" + paramString;
    return paramUri.buildUpon().path(str).build();
  }

  public static Uri setImageUriOptions(String paramString, Uri paramUri)
  {
    ArrayList localArrayList = newArrayList(SPLIT_ON_SLASH.split(paramUri.getPath()));
    int i = localArrayList.size();
    if ((localArrayList.size() > 1) && (((String)localArrayList.get(0)).equals("image")))
      --i;
    if ((i >= 4) && (i <= 6))
      return setLegacyImageUrlOptions(paramString, paramUri);
    if (i == 1)
      return setContentImageUrlOptions(paramString, paramUri);
    return paramUri;
  }

  public static Uri setImageUrlOptions(String paramString1, String paramString2)
  {
    return setImageUriOptions(paramString1, Uri.parse(paramString2));
  }

  private static Uri setLegacyImageUrlOptions(String paramString, Uri paramUri)
  {
    int i = 1;
    String str = paramUri.getPath();
    ArrayList localArrayList = newArrayList(SPLIT_ON_SLASH.split(str));
    int j = localArrayList.size();
    int k = 0;
    if (j > 0)
    {
      boolean bool2 = ((String)localArrayList.get(0)).equals("image");
      k = 0;
      if (bool2)
      {
        localArrayList.remove(0);
        k = 1;
      }
    }
    int l = localArrayList.size();
    boolean bool1 = str.endsWith("/");
    int i1;
    if ((!bool1) && (l == 5))
    {
      i1 = i;
      label105: if (l != 4)
        break label218;
      if (i1 != 0)
        label111: localArrayList.add(localArrayList.get(4));
      if (i == 0)
        break label223;
      localArrayList.add(paramString);
    }
    while (true)
    {
      if (k != 0)
        localArrayList.add(0, "image");
      if (bool1)
        localArrayList.add("");
      return paramUri.buildUpon().path("/" + JOIN_ON_SLASH.join(localArrayList)).build();
      i1 = 0;
      break label105:
      label218: i = 0;
      break label111:
      label223: localArrayList.set(4, paramString);
    }
  }

  private static class Joiner
  {
    private final String separator;

    private Joiner(String paramString)
    {
      this.separator = paramString;
    }

    public static Joiner on(String paramString)
    {
      return new Joiner(paramString);
    }

    public final StringBuilder appendTo(StringBuilder paramStringBuilder, Iterable<?> paramIterable)
    {
      Iterator localIterator = paramIterable.iterator();
      if (localIterator.hasNext())
      {
        paramStringBuilder.append(toString(localIterator.next()));
        while (localIterator.hasNext())
        {
          paramStringBuilder.append(this.separator);
          paramStringBuilder.append(toString(localIterator.next()));
        }
      }
      return paramStringBuilder;
    }

    public final String join(Iterable<?> paramIterable)
    {
      return appendTo(new StringBuilder(), paramIterable).toString();
    }

    CharSequence toString(Object paramObject)
    {
      if (paramObject instanceof CharSequence)
        return (CharSequence)paramObject;
      return paramObject.toString();
    }
  }

  static class Splitter
  {
    private final boolean omitEmptyStrings;
    private final Strategy strategy;

    private Splitter(Strategy paramStrategy)
    {
      this(paramStrategy, false);
    }

    private Splitter(Strategy paramStrategy, boolean paramBoolean)
    {
      this.strategy = paramStrategy;
      this.omitEmptyStrings = paramBoolean;
    }

    public static Splitter on(String paramString)
    {
      if ((paramString == null) || (paramString.length() == 0))
        throw new IllegalArgumentException("separator may not be empty or null");
      return new Splitter(new Strategy(paramString)
      {
        public FIFEUtil.Splitter.SplittingIterator iterator(FIFEUtil.Splitter paramSplitter, CharSequence paramCharSequence)
        {
          return new FIFEUtil.Splitter.SplittingIterator(paramSplitter, paramCharSequence)
          {
            public int separatorEnd(int paramInt)
            {
              return paramInt + FIFEUtil.Splitter.1.this.val$separator.length();
            }

            public int separatorStart(int paramInt)
            {
              int i = FIFEUtil.Splitter.1.this.val$separator.length();
              int j = paramInt;
              int k = this.toSplit.length() - i;
              if (j <= k)
                for (int l = 0; ; ++l)
                {
                  label26: if (l >= i)
                    break label83;
                  if (this.toSplit.charAt(l + j) == FIFEUtil.Splitter.1.this.val$separator.charAt(l))
                    continue;
                  ++j;
                  break label26:
                }
              j = -1;
              label83: return j;
            }
          };
        }
      });
    }

    public Splitter omitEmptyStrings()
    {
      return new Splitter(this.strategy, true);
    }

    public Iterable<String> split(CharSequence paramCharSequence)
    {
      return new Iterable(paramCharSequence)
      {
        public Iterator<String> iterator()
        {
          return FIFEUtil.Splitter.this.strategy.iterator(FIFEUtil.Splitter.this, this.val$sequence);
        }
      };
    }

    private static abstract class AbstractIterator<T>
      implements Iterator<T>
    {
      T next;
      State state = State.NOT_READY;

      protected abstract T computeNext();

      protected final T endOfData()
      {
        this.state = State.DONE;
        return null;
      }

      public final boolean hasNext()
      {
        if (this.state == State.FAILED)
          throw new IllegalStateException();
        switch (FIFEUtil.1.$SwitchMap$com$google$android$picasastore$FIFEUtil$Splitter$AbstractIterator$State[this.state.ordinal()])
        {
        default:
          return tryToComputeNext();
        case 1:
          return false;
        case 2:
        }
        return true;
      }

      public final T next()
      {
        if (!hasNext())
          throw new NoSuchElementException();
        this.state = State.NOT_READY;
        return this.next;
      }

      public void remove()
      {
        throw new UnsupportedOperationException();
      }

      boolean tryToComputeNext()
      {
        this.state = State.FAILED;
        this.next = computeNext();
        if (this.state != State.DONE)
        {
          this.state = State.READY;
          return true;
        }
        return false;
      }

      static enum State
      {
        static
        {
          NOT_READY = new State("NOT_READY", 1);
          DONE = new State("DONE", 2);
          FAILED = new State("FAILED", 3);
          State[] arrayOfState = new State[4];
          arrayOfState[0] = READY;
          arrayOfState[1] = NOT_READY;
          arrayOfState[2] = DONE;
          arrayOfState[3] = FAILED;
          $VALUES = arrayOfState;
        }
      }
    }

    private static abstract class SplittingIterator extends FIFEUtil.Splitter.AbstractIterator<String>
    {
      int offset = 0;
      final boolean omitEmptyStrings;
      final CharSequence toSplit;

      protected SplittingIterator(FIFEUtil.Splitter paramSplitter, CharSequence paramCharSequence)
      {
        super(null);
        this.omitEmptyStrings = paramSplitter.omitEmptyStrings;
        this.toSplit = paramCharSequence;
      }

      protected String computeNext()
      {
        if (this.offset != -1)
        {
          int i = this.offset;
          int j = separatorStart(this.offset);
          int k;
          if (j == -1)
            k = this.toSplit.length();
          for (this.offset = -1; ; this.offset = separatorEnd(j))
          {
            if ((!this.omitEmptyStrings) || (i != k));
            return this.toSplit.subSequence(i, k).toString();
            k = j;
          }
        }
        return (String)endOfData();
      }

      abstract int separatorEnd(int paramInt);

      abstract int separatorStart(int paramInt);
    }

    private static abstract interface Strategy
    {
      public abstract Iterator<String> iterator(FIFEUtil.Splitter paramSplitter, CharSequence paramCharSequence);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.FIFEUtil
 * JD-Core Version:    0.5.4
 */