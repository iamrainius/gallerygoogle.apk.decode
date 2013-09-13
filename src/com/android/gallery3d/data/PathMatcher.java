package com.android.gallery3d.data;

import java.util.ArrayList;
import java.util.HashMap;

public class PathMatcher
{
  private Node mRoot = new Node(null);
  private ArrayList<String> mVariables = new ArrayList();

  public void add(String paramString, int paramInt)
  {
    String[] arrayOfString = Path.split(paramString);
    Node localNode = this.mRoot;
    for (int i = 0; i < arrayOfString.length; ++i)
      localNode = localNode.addChild(arrayOfString[i]);
    localNode.setKind(paramInt);
  }

  public int getIntVar(int paramInt)
  {
    return Integer.parseInt((String)this.mVariables.get(paramInt));
  }

  public long getLongVar(int paramInt)
  {
    return Long.parseLong((String)this.mVariables.get(paramInt));
  }

  public String getVar(int paramInt)
  {
    return (String)this.mVariables.get(paramInt);
  }

  public int match(Path paramPath)
  {
    String[] arrayOfString = paramPath.split();
    this.mVariables.clear();
    Object localObject = this.mRoot;
    for (int i = 0; ; ++i)
    {
      if (i >= arrayOfString.length)
        break label78;
      Node localNode = ((Node)localObject).getChild(arrayOfString[i]);
      if (localNode == null)
      {
        localNode = ((Node)localObject).getChild("*");
        if (localNode == null)
          break;
        this.mVariables.add(arrayOfString[i]);
      }
      localObject = localNode;
    }
    return -1;
    label78: return ((Node)localObject).getKind();
  }

  private static class Node
  {
    private int mKind = -1;
    private HashMap<String, Node> mMap;

    Node addChild(String paramString)
    {
      if (this.mMap == null)
        this.mMap = new HashMap();
      Node localNode1;
      do
      {
        Node localNode2 = new Node();
        this.mMap.put(paramString, localNode2);
        return localNode2;
        localNode1 = (Node)this.mMap.get(paramString);
      }
      while (localNode1 == null);
      return localNode1;
    }

    Node getChild(String paramString)
    {
      if (this.mMap == null)
        return null;
      return (Node)this.mMap.get(paramString);
    }

    int getKind()
    {
      return this.mKind;
    }

    void setKind(int paramInt)
    {
      this.mKind = paramInt;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.PathMatcher
 * JD-Core Version:    0.5.4
 */