package com.google.android.apps.lightcycle.panorama;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MessageSender
{
  protected List<MessageSubscriber> mSubscribers = new ArrayList();

  public void notifyAll(int paramInt, float paramFloat, String paramString)
  {
    Iterator localIterator = this.mSubscribers.iterator();
    while (localIterator.hasNext())
      ((MessageSubscriber)localIterator.next()).message(paramInt, paramFloat, paramString);
  }

  public void subscribe(MessageSubscriber paramMessageSubscriber)
  {
    this.mSubscribers.add(paramMessageSubscriber);
  }

  public static abstract interface MessageSubscriber
  {
    public abstract void message(int paramInt, float paramFloat, String paramString);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.MessageSender
 * JD-Core Version:    0.5.4
 */