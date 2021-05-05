package me.kiras.aimwhere.customskinloader.renderer;

import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import net.minecraft.client.renderer.IImageBuffer;

public class SkinBuffer implements IImageBuffer {

   private int[] imageData;
   private int ratio = 1;
   private static final int A = 16777215;
   private static final int WHITE = getARGB(255, 255, 255, 255);
   private static final int B = -16777216;
   private static final int BLACK = getARGB(255, 0, 0, 0);


   public BufferedImage parseUserSkin(BufferedImage image) {
      if(image == null) {
         return null;
      } else {
         this.ratio = image.getWidth() / 64;
         BufferedImage bufferedimage = new BufferedImage(64 * this.ratio, 64 * this.ratio, 2);
         Graphics graphics = bufferedimage.getGraphics();
         graphics.setColor(new Color(0, 0, 0, 0));
         graphics.fillRect(0, 0, 64, 64);
         graphics.drawImage(image, 0, 0, (ImageObserver)null);
         if(image.getHeight() == 32 * this.ratio) {
            graphics.drawImage(bufferedimage, 24 * this.ratio, 48 * this.ratio, 20 * this.ratio, 52 * this.ratio, 4 * this.ratio, 16 * this.ratio, 8 * this.ratio, 20 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 28 * this.ratio, 48 * this.ratio, 24 * this.ratio, 52 * this.ratio, 8 * this.ratio, 16 * this.ratio, 12 * this.ratio, 20 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 20 * this.ratio, 52 * this.ratio, 16 * this.ratio, 64 * this.ratio, 8 * this.ratio, 20 * this.ratio, 12 * this.ratio, 32 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 24 * this.ratio, 52 * this.ratio, 20 * this.ratio, 64 * this.ratio, 4 * this.ratio, 20 * this.ratio, 8 * this.ratio, 32 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 28 * this.ratio, 52 * this.ratio, 24 * this.ratio, 64 * this.ratio, 0 * this.ratio, 20 * this.ratio, 4 * this.ratio, 32 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 32 * this.ratio, 52 * this.ratio, 28 * this.ratio, 64 * this.ratio, 12 * this.ratio, 20 * this.ratio, 16 * this.ratio, 32 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 40 * this.ratio, 48 * this.ratio, 36 * this.ratio, 52 * this.ratio, 44 * this.ratio, 16 * this.ratio, 48 * this.ratio, 20 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 44 * this.ratio, 48 * this.ratio, 40 * this.ratio, 52 * this.ratio, 48 * this.ratio, 16 * this.ratio, 52 * this.ratio, 20 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 36 * this.ratio, 52 * this.ratio, 32 * this.ratio, 64 * this.ratio, 48 * this.ratio, 20 * this.ratio, 52 * this.ratio, 32 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 40 * this.ratio, 52 * this.ratio, 36 * this.ratio, 64 * this.ratio, 44 * this.ratio, 20 * this.ratio, 48 * this.ratio, 32 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 44 * this.ratio, 52 * this.ratio, 40 * this.ratio, 64 * this.ratio, 40 * this.ratio, 20 * this.ratio, 44 * this.ratio, 32 * this.ratio, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 48 * this.ratio, 52 * this.ratio, 44 * this.ratio, 64 * this.ratio, 52 * this.ratio, 20 * this.ratio, 56 * this.ratio, 32 * this.ratio, (ImageObserver)null);
         }

         graphics.dispose();
         this.imageData = ((DataBufferInt)bufferedimage.getRaster().getDataBuffer()).getData();
         this.setAreaDueToConfig(0 * this.ratio, 0 * this.ratio, 32 * this.ratio, 16 * this.ratio);
         this.setAreaTransparent(32 * this.ratio, 0 * this.ratio, 64 * this.ratio, 16 * this.ratio);
         this.setAreaDueToConfig(0 * this.ratio, 16 * this.ratio, 16 * this.ratio, 32 * this.ratio);
         this.setAreaDueToConfig(16 * this.ratio, 16 * this.ratio, 40 * this.ratio, 32 * this.ratio);
         this.setAreaDueToConfig(40 * this.ratio, 16 * this.ratio, 56 * this.ratio, 32 * this.ratio);
         this.setAreaTransparent(0 * this.ratio, 32 * this.ratio, 16 * this.ratio, 48 * this.ratio);
         this.setAreaTransparent(16 * this.ratio, 32 * this.ratio, 40 * this.ratio, 48 * this.ratio);
         this.setAreaTransparent(40 * this.ratio, 32 * this.ratio, 56 * this.ratio, 48 * this.ratio);
         this.setAreaTransparent(0 * this.ratio, 48 * this.ratio, 16 * this.ratio, 64 * this.ratio);
         this.setAreaDueToConfig(16 * this.ratio, 48 * this.ratio, 32 * this.ratio, 64 * this.ratio);
         this.setAreaDueToConfig(32 * this.ratio, 48 * this.ratio, 48 * this.ratio, 64 * this.ratio);
         this.setAreaTransparent(48 * this.ratio, 48 * this.ratio, 64 * this.ratio, 64 * this.ratio);
         return bufferedimage;
      }
   }

   private boolean isFilled(int x0, int y0, int x1, int y1) {
      int data = this.imageData[this.getPosition(x0, y0)];
      if(data != WHITE && data != BLACK) {
         return false;
      } else {
         for(int x = x0; x < x1; ++x) {
            for(int y = y0; y < y1; ++y) {
               if(this.imageData[this.getPosition(x, y)] != data) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private void setAreaTransparent(int x0, int y0, int x1, int y1) {
      if(this.isFilled(x0, y0, x1, y1)) {
         for(int x = x0; x < x1; ++x) {
            for(int y = y0; y < y1; ++y) {
               this.imageData[this.getPosition(x, y)] &= 16777215;
            }
         }

      }
   }

   private void setAreaOpaque(int x0, int y0, int x1, int y1) {
      for(int x = x0; x < x1; ++x) {
         for(int y = y0; y < y1; ++y) {
            this.imageData[this.getPosition(x, y)] |= -16777216;
         }
      }

   }

   private void setAreaDueToConfig(int x0, int y0, int x1, int y1) {
      if(CustomSkinLoader.config.enableTransparentSkin) {
         this.setAreaTransparent(x0, y0, x1, y1);
      } else {
         this.setAreaOpaque(x0, y0, x1, y1);
      }

   }

   public void skinAvailable() {}

   private static int getARGB(int a, int r, int g, int b) {
      return a << 24 | r << 16 | g << 8 | b;
   }

   private int getPosition(int x, int y) {
      return x + y * 64 * this.ratio;
   }
}
