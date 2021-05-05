package me.kiras.aimwhere.customskinloader.tweaker;

import me.kiras.aimwhere.customskinloader.tweaker.ModSystemTweaker;
import me.kiras.aimwhere.customskinloader.utils.MinecraftUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.io.IOUtils;

public class ClassTransformer implements IClassTransformer {

   private ZipFile zipFile = null;
   private ArrayList<String> classes = new ArrayList();


   public ClassTransformer() {
      ModSystemTweaker.logger.info("ClassTransformer Begin");

      try {
         URLClassLoader e = (URLClassLoader)this.getClass().getClassLoader();
         URL[] urls = e.getURLs();
         URL[] var6 = urls;
         int var5 = urls.length;

         for(int var4 = 0; var4 < var5; ++var4) {
            URL url = var6[var4];
            if(MinecraftUtil.isCoreFile(url)) {
               ModSystemTweaker.logger.info(url.toString() + " : SKIP (core file).");
            } else if(MinecraftUtil.isLibraryFile(url)) {
               ModSystemTweaker.logger.info(url.toString() + " : SKIP (library file).");
            } else {
               File file = new File(url.toURI());
               if(file != null && file.isFile()) {
                  ZipFile tempZipFile = getZipFile(file);
                  if(tempZipFile == null) {
                     ModSystemTweaker.logger.info(url.toString() + " : EXCEPTION (^ Message ^).");
                  } else {
                     if(tempZipFile.getEntry("customskinloader/tweaker/ClassTransformer.class") != null) {
                        this.zipFile = tempZipFile;
                        Enumeration entries = this.zipFile.entries();
                        StringBuilder sb = new StringBuilder();

                        while(entries.hasMoreElements()) {
                           ZipEntry entry = (ZipEntry)entries.nextElement();
                           String name = entry.getName();
                           if(name.endsWith(".class") && (!name.contains("/") || name.startsWith("net"))) {
                              this.classes.add(name);
                              sb.append(" ");
                              sb.append(name);
                           }
                        }

                        ModSystemTweaker.logger.info(url.toString() + " : CHOOSE.");
                        ModSystemTweaker.logger.info("Classes:" + sb.toString());
                        break;
                     }

                     tempZipFile.close();
                     ModSystemTweaker.logger.info(url.toString() + " : FINISH (not target).");
                  }
               } else {
                  ModSystemTweaker.logger.info(url.toString() + " : EXCEPTION (file not found).");
               }
            }
         }
      } catch (Exception var13) {
         ModSystemTweaker.logger.warning(var13);
      }

      if(this.zipFile == null) {
         ModSystemTweaker.logger.info("Can not find JAR in the classpath.");
      }

   }

   private static ZipFile getZipFile(File file) {
      ZipFile zipFile0 = null;

      try {
         zipFile0 = new ZipFile(file);
         return zipFile0;
      } catch (Exception var3) {
         ModSystemTweaker.logger.warning(var3);
         return null;
      }
   }

   public byte[] transform(String name, String transformedName, byte[] bytes) {
      if(this.zipFile == null) {
         return bytes;
      } else {
         String fullName = (name.startsWith("net")?name.replaceAll("\\.", "\\/"):name) + ".class";
         if(!this.classes.contains(fullName)) {
            return bytes;
         } else {
            ZipEntry ze = this.zipFile.getEntry(fullName);
            if(ze == null) {
               return bytes;
            } else {
               byte[] diBytes = this.getClass(ze);
               if(diBytes != null) {
                  ModSystemTweaker.logger.info("Class \'" + name + "\'(" + transformedName + ") transformed.");
                  return diBytes;
               } else {
                  return bytes;
               }
            }
         }
      }
   }

   private byte[] getClass(ZipEntry ze) {
      try {
         InputStream e = this.zipFile.getInputStream(ze);
         byte[] bytes = IOUtils.toByteArray(e);
         if((long)bytes.length == ze.getSize()) {
            return bytes;
         }

         ModSystemTweaker.logger.info("Failed: " + ze.getName() + " " + bytes.length + " / " + ze.getSize());
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return null;
   }
}
