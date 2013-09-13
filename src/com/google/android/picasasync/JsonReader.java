package com.google.android.picasasync;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class JsonReader
  implements Closeable
{
  private final char[] buffer = new char[1024];
  private int bufferStartColumn = 1;
  private int bufferStartLine = 1;
  private final Reader in;
  private boolean lenient = false;
  private int limit = 0;
  private String name;
  private int pos = 0;
  private boolean skipping;
  private final List<JsonScope> stack = new ArrayList();
  private JsonToken token;
  private String value;
  private int valueLength;
  private int valuePos;

  public JsonReader(Reader paramReader)
  {
    push(JsonScope.EMPTY_DOCUMENT);
    this.skipping = false;
    if (paramReader == null)
      throw new NullPointerException("in == null");
    this.in = paramReader;
  }

  private JsonToken advance()
    throws IOException
  {
    peek();
    JsonToken localJsonToken = this.token;
    this.token = null;
    this.value = null;
    this.name = null;
    return localJsonToken;
  }

  private void checkLenient()
    throws IOException
  {
    if (this.lenient)
      return;
    throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
  }

  private JsonToken decodeLiteral()
  {
    if (this.valuePos == -1)
      return JsonToken.STRING;
    if ((this.valueLength == 4) && ((('n' == this.buffer[this.valuePos]) || ('N' == this.buffer[this.valuePos]))) && ((('u' == this.buffer[(1 + this.valuePos)]) || ('U' == this.buffer[(1 + this.valuePos)]))) && ((('l' == this.buffer[(2 + this.valuePos)]) || ('L' == this.buffer[(2 + this.valuePos)]))) && ((('l' == this.buffer[(3 + this.valuePos)]) || ('L' == this.buffer[(3 + this.valuePos)]))))
    {
      this.value = "null";
      return JsonToken.NULL;
    }
    if ((this.valueLength == 4) && ((('t' == this.buffer[this.valuePos]) || ('T' == this.buffer[this.valuePos]))) && ((('r' == this.buffer[(1 + this.valuePos)]) || ('R' == this.buffer[(1 + this.valuePos)]))) && ((('u' == this.buffer[(2 + this.valuePos)]) || ('U' == this.buffer[(2 + this.valuePos)]))) && ((('e' == this.buffer[(3 + this.valuePos)]) || ('E' == this.buffer[(3 + this.valuePos)]))))
    {
      this.value = "true";
      return JsonToken.BOOLEAN;
    }
    if ((this.valueLength == 5) && ((('f' == this.buffer[this.valuePos]) || ('F' == this.buffer[this.valuePos]))) && ((('a' == this.buffer[(1 + this.valuePos)]) || ('A' == this.buffer[(1 + this.valuePos)]))) && ((('l' == this.buffer[(2 + this.valuePos)]) || ('L' == this.buffer[(2 + this.valuePos)]))) && ((('s' == this.buffer[(3 + this.valuePos)]) || ('S' == this.buffer[(3 + this.valuePos)]))) && ((('e' == this.buffer[(4 + this.valuePos)]) || ('E' == this.buffer[(4 + this.valuePos)]))))
    {
      this.value = "false";
      return JsonToken.BOOLEAN;
    }
    this.value = new String(this.buffer, this.valuePos, this.valueLength);
    return decodeNumber(this.buffer, this.valuePos, this.valueLength);
  }

  private JsonToken decodeNumber(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    int j = paramArrayOfChar[i];
    if (j == 45)
      j = paramArrayOfChar[(++i)];
    int k;
    int l;
    if (j == 48)
    {
      k = i + 1;
      l = paramArrayOfChar[k];
      if (l != 46)
        break label142;
      for (l = paramArrayOfChar[(++k)]; ; l = paramArrayOfChar[(++k)])
        if ((l < 48) || (l > 57))
          break label142;
    }
    if ((j >= 49) && (j <= 57))
    {
      k = i + 1;
      for (l = paramArrayOfChar[k]; ; l = paramArrayOfChar[(++k)])
        if ((l < 48) || (l > 57));
    }
    return JsonToken.STRING;
    if ((l == 101) || (l == 69))
    {
      label142: int i1 = k + 1;
      int i2 = paramArrayOfChar[i1];
      if ((i2 == 43) || (i2 == 45))
        i2 = paramArrayOfChar[(++i1)];
      if ((i2 >= 48) && (i2 <= 57))
      {
        k = i1 + 1;
        for (int i3 = paramArrayOfChar[k]; ; i3 = paramArrayOfChar[(++k)])
          if ((i3 < 48) || (i3 > 57))
            break label247;
      }
      return JsonToken.STRING;
    }
    if (k == paramInt1 + paramInt2)
      label247: return JsonToken.NUMBER;
    return JsonToken.STRING;
  }

  private void expect(JsonToken paramJsonToken)
    throws IOException
  {
    peek();
    if (this.token != paramJsonToken)
      throw new IllegalStateException("Expected " + paramJsonToken + " but was " + peek());
    advance();
  }

  private boolean fillBuffer(int paramInt)
    throws IOException
  {
    int i = 0;
    if (i < this.pos)
    {
      if (this.buffer[i] == '\n')
        label2: this.bufferStartLine = (1 + this.bufferStartLine);
      for (this.bufferStartColumn = 1; ; this.bufferStartColumn = (1 + this.bufferStartColumn))
      {
        ++i;
        break label2:
      }
    }
    if (this.limit != this.pos)
    {
      this.limit -= this.pos;
      System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.limit);
    }
    while (true)
    {
      this.pos = 0;
      while (true)
      {
        int j = this.in.read(this.buffer, this.limit, this.buffer.length - this.limit);
        if (j == -1)
          break label217;
        this.limit = (j + this.limit);
        if ((this.bufferStartLine == 1) && (this.bufferStartColumn == 1) && (this.limit > 0) && (this.buffer[0] == 65279))
        {
          this.pos = (1 + this.pos);
          this.bufferStartColumn = (-1 + this.bufferStartColumn);
        }
        if (this.limit >= paramInt)
          return true;
      }
      this.limit = 0;
    }
    label217: return false;
  }

  private int getColumnNumber()
  {
    int i = this.bufferStartColumn;
    int j = 0;
    if (j < this.pos)
    {
      label7: if (this.buffer[j] == '\n');
      for (i = 1; ; ++i)
      {
        ++j;
        break label7:
      }
    }
    return i;
  }

  private int getLineNumber()
  {
    int i = this.bufferStartLine;
    for (int j = 0; j < this.pos; ++j)
    {
      if (this.buffer[j] != '\n')
        continue;
      ++i;
    }
    return i;
  }

  private CharSequence getSnippet()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = Math.min(this.pos, 20);
    localStringBuilder.append(this.buffer, this.pos - i, i);
    int j = Math.min(this.limit - this.pos, 20);
    localStringBuilder.append(this.buffer, this.pos, j);
    return localStringBuilder;
  }

  private JsonToken nextInArray(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
      replaceTop(JsonScope.NONEMPTY_ARRAY);
    while (true)
      switch (nextNonWhitespace())
      {
      default:
        this.pos = (-1 + this.pos);
        return nextValue();
        switch (nextNonWhitespace())
        {
        case 44:
        default:
          throw syntaxError("Unterminated array");
        case 93:
          pop();
          JsonToken localJsonToken3 = JsonToken.END_ARRAY;
          this.token = localJsonToken3;
          return localJsonToken3;
        case 59:
        }
        checkLenient();
      case 93:
      case 44:
      case 59:
      }
    if (paramBoolean)
    {
      pop();
      JsonToken localJsonToken2 = JsonToken.END_ARRAY;
      this.token = localJsonToken2;
      return localJsonToken2;
    }
    checkLenient();
    this.pos = (-1 + this.pos);
    this.value = "null";
    JsonToken localJsonToken1 = JsonToken.NULL;
    this.token = localJsonToken1;
    return localJsonToken1;
  }

  private JsonToken nextInObject(boolean paramBoolean)
    throws IOException
  {
    int i;
    if (paramBoolean)
    {
      switch (nextNonWhitespace())
      {
      default:
        this.pos = (-1 + this.pos);
        i = nextNonWhitespace();
        switch (i)
        {
        default:
          checkLenient();
          this.pos = (-1 + this.pos);
          this.name = nextLiteral(false);
          if (this.name.length() != 0)
            break label206;
          throw syntaxError("Expected name");
        case 39:
        case 34:
        }
      case 125:
      }
      pop();
      JsonToken localJsonToken3 = JsonToken.END_OBJECT;
      this.token = localJsonToken3;
      return localJsonToken3;
    }
    switch (nextNonWhitespace())
    {
    case 44:
    case 59:
    default:
      throw syntaxError("Unterminated object");
    case 125:
    }
    pop();
    JsonToken localJsonToken1 = JsonToken.END_OBJECT;
    this.token = localJsonToken1;
    return localJsonToken1;
    checkLenient();
    this.name = nextString((char)i);
    label206: replaceTop(JsonScope.DANGLING_NAME);
    JsonToken localJsonToken2 = JsonToken.NAME;
    this.token = localJsonToken2;
    return localJsonToken2;
  }

  private String nextLiteral(boolean paramBoolean)
    throws IOException
  {
    StringBuilder localStringBuilder = null;
    this.valuePos = -1;
    this.valueLength = 0;
    for (int i = 0; ; ++i)
    {
      if (i + this.pos >= this.limit)
        break label228;
      switch (this.buffer[(i + this.pos)])
      {
      default:
      case '#':
      case '/':
      case ';':
      case '=':
      case '\\':
      case '\t':
      case '\n':
      case '\f':
      case '\r':
      case ' ':
      case ',':
      case ':':
      case '[':
      case ']':
      case '{':
      case '}':
      }
    }
    checkLenient();
    if ((paramBoolean) && (localStringBuilder == null))
      label186: this.valuePos = this.pos;
    for (String str = null; ; str = localStringBuilder.toString())
    {
      while (true)
      {
        this.valueLength = (i + this.valueLength);
        this.pos = (i + this.pos);
        return str;
        if (i < this.buffer.length)
        {
          label228: if (!fillBuffer(i + 1));
          this.buffer[this.limit] = '\000';
        }
        if (localStringBuilder == null)
          localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.buffer, this.pos, i);
        this.valueLength = (i + this.valueLength);
        this.pos = (i + this.pos);
        boolean bool = fillBuffer(1);
        i = 0;
        if (!bool);
        i = 0;
        break label186:
        if (this.skipping)
          str = "skipped!";
        if (localStringBuilder != null)
          break;
        str = new String(this.buffer, this.pos, i);
      }
      localStringBuilder.append(this.buffer, this.pos, i);
    }
  }

  private int nextNonWhitespace()
    throws IOException
  {
    while ((this.pos < this.limit) || (fillBuffer(1)))
    {
      while (true)
      {
        char[] arrayOfChar = this.buffer;
        int i = this.pos;
        this.pos = (i + 1);
        int j = arrayOfChar[i];
        switch (j)
        {
        case 9:
        case 10:
        case 13:
        case 32:
        default:
        case 47:
          do
            return j;
          while ((this.pos == this.limit) && (!fillBuffer(1)));
          checkLenient();
          switch (this.buffer[this.pos])
          {
          default:
            return j;
          case '*':
            this.pos = (1 + this.pos);
            if (!skipTo("*/"))
              throw syntaxError("Unterminated comment");
            this.pos = (2 + this.pos);
            break;
          case '/':
          }
          this.pos = (1 + this.pos);
          skipToEndOfLine();
        case 35:
        }
      }
      checkLenient();
      skipToEndOfLine();
    }
    throw new EOFException("End of input");
  }

  private String nextString(char paramChar)
    throws IOException
  {
    StringBuilder localStringBuilder = null;
    do
    {
      for (int i = this.pos; this.pos < this.limit; i = this.pos)
      {
        char c;
        do
        {
          char[] arrayOfChar = this.buffer;
          int j = this.pos;
          this.pos = (j + 1);
          c = arrayOfChar[j];
          if (c != paramChar)
            continue;
          if (this.skipping)
            return "skipped!";
          if (localStringBuilder == null)
            return new String(this.buffer, i, -1 + (this.pos - i));
          localStringBuilder.append(this.buffer, i, -1 + (this.pos - i));
          return localStringBuilder.toString();
        }
        while (c != '\\');
        if (localStringBuilder == null)
          localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.buffer, i, -1 + (this.pos - i));
        localStringBuilder.append(readEscapeCharacter());
      }
      if (localStringBuilder == null)
        localStringBuilder = new StringBuilder();
      localStringBuilder.append(this.buffer, i, this.pos - i);
    }
    while (fillBuffer(1));
    throw syntaxError("Unterminated string");
  }

  private JsonToken nextValue()
    throws IOException
  {
    int i = nextNonWhitespace();
    switch (i)
    {
    default:
      this.pos = (-1 + this.pos);
      return readLiteral();
    case 123:
      push(JsonScope.EMPTY_OBJECT);
      JsonToken localJsonToken3 = JsonToken.BEGIN_OBJECT;
      this.token = localJsonToken3;
      return localJsonToken3;
    case 91:
      push(JsonScope.EMPTY_ARRAY);
      JsonToken localJsonToken2 = JsonToken.BEGIN_ARRAY;
      this.token = localJsonToken2;
      return localJsonToken2;
    case 39:
      checkLenient();
    case 34:
    }
    this.value = nextString((char)i);
    JsonToken localJsonToken1 = JsonToken.STRING;
    this.token = localJsonToken1;
    return localJsonToken1;
  }

  private JsonToken objectValue()
    throws IOException
  {
    switch (nextNonWhitespace())
    {
    case 59:
    case 60:
    default:
      throw syntaxError("Expected ':'");
    case 61:
      checkLenient();
      if ((((this.pos < this.limit) || (fillBuffer(1)))) && (this.buffer[this.pos] == '>'))
        this.pos = (1 + this.pos);
    case 58:
    }
    replaceTop(JsonScope.NONEMPTY_OBJECT);
    return nextValue();
  }

  private JsonScope peekStack()
  {
    return (JsonScope)this.stack.get(-1 + this.stack.size());
  }

  private JsonScope pop()
  {
    return (JsonScope)this.stack.remove(-1 + this.stack.size());
  }

  private void push(JsonScope paramJsonScope)
  {
    this.stack.add(paramJsonScope);
  }

  private char readEscapeCharacter()
    throws IOException
  {
    if ((this.pos == this.limit) && (!fillBuffer(1)))
      throw syntaxError("Unterminated escape sequence");
    char[] arrayOfChar = this.buffer;
    int i = this.pos;
    this.pos = (i + 1);
    int j = arrayOfChar[i];
    switch (j)
    {
    default:
      return j;
    case 117:
      if ((4 + this.pos > this.limit) && (!fillBuffer(4)))
        throw syntaxError("Unterminated escape sequence");
      String str = new String(this.buffer, this.pos, 4);
      this.pos = (4 + this.pos);
      return (char)Integer.parseInt(str, 16);
    case 116:
      return '\t';
    case 98:
      return '\b';
    case 110:
      return '\n';
    case 114:
      return '\r';
    case 102:
    }
    return '\f';
  }

  private JsonToken readLiteral()
    throws IOException
  {
    this.value = nextLiteral(true);
    if (this.valueLength == 0)
      throw syntaxError("Expected literal value");
    this.token = decodeLiteral();
    if (this.token == JsonToken.STRING)
      checkLenient();
    return this.token;
  }

  private void replaceTop(JsonScope paramJsonScope)
  {
    this.stack.set(-1 + this.stack.size(), paramJsonScope);
  }

  private boolean skipTo(String paramString)
    throws IOException
  {
    if ((this.pos + paramString.length() <= this.limit) || (fillBuffer(paramString.length())))
    {
      for (int i = 0; i < paramString.length(); ++i)
      {
        label0: if (this.buffer[(i + this.pos)] == paramString.charAt(i))
          continue;
        this.pos = (1 + this.pos);
        break label0:
      }
      return true;
    }
    return false;
  }

  private void skipToEndOfLine()
    throws IOException
  {
    int j;
    do
    {
      if ((this.pos >= this.limit) && (!fillBuffer(1)))
        return;
      char[] arrayOfChar = this.buffer;
      int i = this.pos;
      this.pos = (i + 1);
      j = arrayOfChar[i];
    }
    while ((j != 13) && (j != 10));
  }

  private IOException syntaxError(String paramString)
    throws IOException
  {
    throw new IOException(paramString + " at line " + getLineNumber() + " column " + getColumnNumber());
  }

  public void beginArray()
    throws IOException
  {
    expect(JsonToken.BEGIN_ARRAY);
  }

  public void beginObject()
    throws IOException
  {
    expect(JsonToken.BEGIN_OBJECT);
  }

  public void close()
    throws IOException
  {
    this.value = null;
    this.token = null;
    this.stack.clear();
    this.stack.add(JsonScope.CLOSED);
    this.in.close();
  }

  public void endArray()
    throws IOException
  {
    expect(JsonToken.END_ARRAY);
  }

  public void endObject()
    throws IOException
  {
    expect(JsonToken.END_OBJECT);
  }

  public boolean hasNext()
    throws IOException
  {
    peek();
    return (this.token != JsonToken.END_OBJECT) && (this.token != JsonToken.END_ARRAY);
  }

  public double nextDouble()
    throws IOException
  {
    peek();
    if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER))
      throw new IllegalStateException("Expected a double but was " + this.token);
    double d = Double.parseDouble(this.value);
    advance();
    return d;
  }

  public int nextInt()
    throws IOException
  {
    peek();
    if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER))
      throw new IllegalStateException("Expected an int but was " + this.token);
    int i;
    try
    {
      int j = Integer.parseInt(this.value);
      i = j;
      advance();
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      double d = Double.parseDouble(this.value);
      i = (int)d;
      if (i != d);
      throw new NumberFormatException(this.value);
    }
  }

  public long nextLong()
    throws IOException
  {
    peek();
    if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER))
      throw new IllegalStateException("Expected a long but was " + this.token);
    long l1;
    try
    {
      long l2 = Long.parseLong(this.value);
      l1 = l2;
      advance();
      return l1;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      double d = Double.parseDouble(this.value);
      l1 = ()d;
      if (l1 != d);
      throw new NumberFormatException(this.value);
    }
  }

  public String nextName()
    throws IOException
  {
    peek();
    if (this.token != JsonToken.NAME)
      throw new IllegalStateException("Expected a name but was " + peek());
    String str = this.name;
    advance();
    return str;
  }

  public String nextString()
    throws IOException
  {
    peek();
    if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER))
      throw new IllegalStateException("Expected a string but was " + peek());
    String str = this.value;
    advance();
    return str;
  }

  public JsonToken peek()
    throws IOException
  {
    JsonToken localJsonToken3;
    if (this.token != null)
      localJsonToken3 = this.token;
    do
    {
      return localJsonToken3;
      switch (1.$SwitchMap$com$google$android$picasasync$JsonScope[peekStack().ordinal()])
      {
      default:
        throw new AssertionError();
      case 1:
        replaceTop(JsonScope.NONEMPTY_DOCUMENT);
        localJsonToken3 = nextValue();
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      }
    }
    while ((this.lenient) || (this.token == JsonToken.BEGIN_ARRAY) || (this.token == JsonToken.BEGIN_OBJECT));
    throw new IOException("Expected JSON document to start with '[' or '{' but was " + this.token);
    return nextInArray(true);
    return nextInArray(false);
    return nextInObject(true);
    return objectValue();
    return nextInObject(false);
    try
    {
      JsonToken localJsonToken2 = nextValue();
      if (this.lenient)
        return localJsonToken2;
      throw syntaxError("Expected EOF");
    }
    catch (EOFException localEOFException)
    {
      JsonToken localJsonToken1 = JsonToken.END_DOCUMENT;
      this.token = localJsonToken1;
      return localJsonToken1;
    }
    throw new IllegalStateException("JsonReader is closed");
  }

  public void skipValue()
    throws IOException
  {
    this.skipping = true;
    int i = 0;
    try
    {
      JsonToken localJsonToken1 = advance();
      if (localJsonToken1 != JsonToken.BEGIN_ARRAY)
      {
        JsonToken localJsonToken2 = JsonToken.BEGIN_OBJECT;
        if (localJsonToken1 != localJsonToken2)
          break label43;
      }
      ++i;
      label43: JsonToken localJsonToken3;
      do
      {
        if (i == 0);
        return;
        if (localJsonToken1 == JsonToken.END_ARRAY)
          break;
        localJsonToken3 = JsonToken.END_OBJECT;
      }
      while (localJsonToken1 != localJsonToken3);
    }
    finally
    {
      this.skipping = false;
    }
  }

  public String toString()
  {
    return super.getClass().getSimpleName() + " near " + getSnippet();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.JsonReader
 * JD-Core Version:    0.5.4
 */