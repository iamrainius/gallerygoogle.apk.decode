package com.google.android.apps.lightcycle.panorama;

import android.view.MotionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class GuiManager
{
  private HashMap<Integer, GuiElement> elements = new HashMap();
  private Integer idCount = Integer.valueOf(0);

  public int addElement(GuiElement paramGuiElement)
  {
    Integer localInteger = this.idCount;
    this.idCount = Integer.valueOf(1 + this.idCount.intValue());
    int i = localInteger.intValue();
    this.elements.put(Integer.valueOf(i), paramGuiElement);
    return i;
  }

  public void draw(float[] paramArrayOfFloat)
  {
    Iterator localIterator = this.elements.entrySet().iterator();
    while (localIterator.hasNext())
      ((GuiElement)((Map.Entry)localIterator.next()).getValue()).draw(paramArrayOfFloat);
  }

  public boolean handleEvent(MotionEvent paramMotionEvent)
  {
    Iterator localIterator = this.elements.entrySet().iterator();
    while (localIterator.hasNext())
      if (((GuiElement)((Map.Entry)localIterator.next()).getValue()).handleEvent(paramMotionEvent))
        return true;
    return false;
  }

  public static abstract interface GuiElement
  {
    public abstract void draw(float[] paramArrayOfFloat);

    public abstract boolean handleEvent(MotionEvent paramMotionEvent);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.GuiManager
 * JD-Core Version:    0.5.4
 */