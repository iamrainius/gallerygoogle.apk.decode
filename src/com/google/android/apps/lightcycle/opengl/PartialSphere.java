package com.google.android.apps.lightcycle.opengl;

import android.graphics.Bitmap;
import android.util.FloatMath;
import android.util.Log;
import com.google.android.apps.lightcycle.viewer.PanoramaImage;
import com.google.android.apps.lightcycle.viewer.Tile;
import com.google.android.apps.lightcycle.viewer.TileProvider;
import java.lang.reflect.Array;
import java.util.Vector;

public class PartialSphere extends DrawableGL
{
  private static final String TAG = PartialSphere.class.getSimpleName();
  private CurvedTile[][] curvedTiles;
  private final PanoramaImage image;
  private final TileProvider tileProvider;

  public PartialSphere(PanoramaImage paramPanoramaImage, float paramFloat)
  {
    this.image = paramPanoramaImage;
    this.tileProvider = paramPanoramaImage.getTileProvider();
    loadTextures();
    paramPanoramaImage.init();
    generateGeometry(paramFloat);
  }

  private void generateGeometry(float paramFloat)
  {
    int i = (int)FloatMath.ceil(this.image.getTileSizeRad() / 0.12F);
    Log.d(TAG, "tesselation factor: " + i);
    int j = 1 + i * this.tileProvider.getTileCountY();
    int k = 1 + i * this.tileProvider.getTileCountX();
    initGeometry(j * k, 6 * ((j - 1) * (k - 1)), true);
    float f1 = this.image.getTileSizeRad() / i;
    float f2 = this.image.getTileSizeRad() / i;
    float f3 = f1 * (this.image.getLastRowHeightRad() / this.image.getTileSizeRad());
    float f4 = f2 * (this.image.getLastColumnWidthRad() / this.image.getTileSizeRad());
    float f5 = this.image.getOffsetTopRad() + this.image.getPanoHeightRad() - 1.570796F;
    float f6 = -this.image.getOffsetLeftRad() - 3.141593F;
    int l = -1 + (k - i);
    Vertex[][] arrayOfVertex = (Vertex[][])Array.newInstance(Vertex.class, new int[] { k, j });
    for (int i1 = 0; i1 < j; ++i1)
    {
      float f7;
      if (i1 < i)
        f7 = f3 * i1 - f5;
      while (true)
      {
        for (int i12 = 0; ; ++i12)
        {
          if (i12 >= k)
            break label414;
          float f8 = f2 * i12;
          if (i12 > l)
            f8 = f2 * l + f4 * (i12 - l);
          float f9 = f8 - 1.570796F - f6;
          float f10 = FloatMath.sin(f7);
          float f11 = FloatMath.sin(f9);
          float f12 = FloatMath.cos(f7);
          float f13 = paramFloat * (f12 * FloatMath.cos(f9));
          float f14 = f10 * paramFloat;
          float f15 = paramFloat * (f12 * f11);
          Vertex[] arrayOfVertex1 = arrayOfVertex[i12];
          Vertex localVertex = new Vertex(f13, f14, f15);
          arrayOfVertex1[i1] = localVertex;
        }
        label414: f7 = f1 * i1 - f5 - (this.image.getTileSizeRad() - this.image.getLastRowHeightRad());
      }
    }
    int i2 = this.tileProvider.getTileCountX();
    int i3 = this.tileProvider.getTileCountY();
    this.curvedTiles = ((CurvedTile[][])Array.newInstance(CurvedTile.class, new int[] { i2, i3 }));
    int i4 = 0;
    for (int i5 = 0; i5 < i3; ++i5)
    {
      int i6 = 0;
      for (int i7 = 0; i7 < i2; ++i7)
      {
        CurvedTile[] arrayOfCurvedTile = this.curvedTiles[i7];
        CurvedTile localCurvedTile = new CurvedTile(i5 + i7 * i3, i);
        arrayOfCurvedTile[i5] = localCurvedTile;
        for (int i8 = 0; ; ++i8)
        {
          int i9 = i + 1;
          if (i8 >= i9)
            break;
          for (int i10 = 0; ; ++i10)
          {
            int i11 = i + 1;
            if (i10 >= i11)
              break;
            this.curvedTiles[i7][i5].putVertex(arrayOfVertex[(i6 + i10)][(i4 + i8)]);
          }
        }
        i6 += i;
      }
      i4 += i;
    }
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    this.mShader.bind();
    for (int i = 0; i < this.curvedTiles.length; ++i)
    {
      CurvedTile[] arrayOfCurvedTile = this.curvedTiles[i];
      for (int j = 0; j < arrayOfCurvedTile.length; ++j)
      {
        CurvedTile localCurvedTile = arrayOfCurvedTile[j];
        if (this.mTextures.size() > localCurvedTile.textureId)
          ((GLTexture)this.mTextures.get(localCurvedTile.textureId)).bind(this.mShader);
        this.mShader.setTransform(paramArrayOfFloat);
        localCurvedTile.draw(this.mShader);
      }
    }
  }

  public boolean loadTextures()
  {
    if (this.tileProvider == null)
    {
      Log.e(TAG, "tile provider is null. Cannot load textures");
      return false;
    }
    this.mTextures.clear();
    for (int i = 0; i < this.tileProvider.getTileCountX(); ++i)
    {
      int j = 0;
      while (j < this.tileProvider.getTileCountY())
      {
        Tile localTile = this.tileProvider.getTile(i, -1 + (this.tileProvider.getTileCountY() - j));
        if ((localTile.bitmap.getWidth() >= 0) && (localTile.bitmap.getHeight() >= 0));
        GLTexture localGLTexture = new GLTexture(GLTexture.TextureType.Standard);
        try
        {
          localGLTexture.loadBitmap(localTile.bitmap);
          this.mTextures.add(localGLTexture);
          ++j;
        }
        catch (OpenGLException localOpenGLException)
        {
          Log.e(TAG, "Could not load texture (" + i + "," + j + ")", localOpenGLException);
          return false;
        }
      }
    }
    return true;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.PartialSphere
 * JD-Core Version:    0.5.4
 */