package me.kiras.aimwhere.customskinloader.profile;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.utils.HttpRequestUtil;
import me.kiras.aimwhere.customskinloader.utils.HttpTextureUtil;
import me.kiras.aimwhere.customskinloader.utils.HttpUtil0;
import me.kiras.aimwhere.customskinloader.utils.HttpRequestUtil.HttpRequest;
import me.kiras.aimwhere.customskinloader.utils.HttpRequestUtil.HttpResponce;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class DynamicSkullManager {
    public class SkullTexture {
        public Map textures;
        public String index;
        public ArrayList skins;
        public int interval;
        public boolean fromZero;
        public long startTime;
        public int period;
    }

    private Map dynamicTextures = new HashMap();
    private Map staticTextures = new HashMap();
    private List loadingList = new ArrayList();

    private void parseGameProfile(GameProfile profile) {
        Property textureProperty = (Property)Iterables.getFirst(profile.getProperties().get("textures"), (Object)null);
        if (textureProperty == null) {
            this.staticTextures.put(profile, new HashMap());
        } else {
            String value = textureProperty.getValue();
            if (StringUtils.isBlank(value)) {
                this.staticTextures.put(profile, new HashMap());
            } else {
                String json = new String(Base64.decodeBase64(value), Charsets.UTF_8);
                Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
                DynamicSkullManager.SkullTexture result = (DynamicSkullManager.SkullTexture)gson.fromJson(json, DynamicSkullManager.SkullTexture.class);
                if (result == null) {
                    this.staticTextures.put(profile, new HashMap());
                } else {
                    this.staticTextures.put(profile, result.textures != null && result.textures.containsKey(Type.SKIN) ? this.parseTextures(result.textures) : new HashMap());
                    String skin;
                    if (StringUtils.isNotEmpty(result.index)) {
                        File indexFile = new File(CustomSkinLoader.DATA_DIR, result.index);

                        try {
                            skin = FileUtils.readFileToString(indexFile, Charsets.UTF_8);
                            if (StringUtils.isNotEmpty(skin)) {
                                ArrayList skins = (ArrayList)CustomSkinLoader.GSON.fromJson(skin, ArrayList.class);
                                if (skins != null && !skins.isEmpty()) {
                                    result.skins = skins;
                                }
                            }
                        } catch (Exception var11) {
                            CustomSkinLoader.logger.warning("Exception occurs while parsing index file: " + var11.toString());
                        }
                    }

                    if (CustomSkinLoader.config.enableDynamicSkull && result.skins != null && !result.skins.isEmpty()) {
                        CustomSkinLoader.logger.info("Try to load Dynamic Skull: " + json);

                        for(int i = 0; i < result.skins.size(); ++i) {
                            skin = (String)result.skins.get(i);
                            if (HttpUtil0.isLocal(skin)) {
                                File skinFile = new File(CustomSkinLoader.DATA_DIR, skin);
                                if (skinFile.isFile() && skinFile.length() > 0L) {
                                    String fakeUrl = HttpTextureUtil.getLocalFakeUrl(skin);
                                    result.skins.set(i, fakeUrl);
                                } else {
                                    result.skins.remove(i--);
                                }
                            } else {
                                HttpResponce responce = HttpRequestUtil.makeHttpRequest((new HttpRequest(skin)).setCacheFile(HttpTextureUtil.getCacheFile(FilenameUtils.getBaseName(skin))).setCacheTime(0).setLoadContent(false));
                                if (!responce.success) {
                                    result.skins.remove(i--);
                                }
                            }
                        }

                        if (result.skins.isEmpty()) {
                            CustomSkinLoader.logger.info("Failed: Nothing loaded.");
                        } else {
                            result.interval = Math.max(result.interval, 50);
                            if (result.fromZero) {
                                result.startTime = System.currentTimeMillis();
                            }

                            result.period = result.interval * result.skins.size();
                            CustomSkinLoader.logger.info("Successfully loaded Dynamic Skull: " + (new Gson()).toJson(result));
                            this.dynamicTextures.put(profile, result);
                            this.staticTextures.remove(profile);
                        }
                    }
                }
            }
        }
    }

    public Map parseTextures(Map textures) {
        MinecraftProfileTexture skin = (MinecraftProfileTexture)textures.get(Type.SKIN);
        String skinUrl = skin.getUrl();
        if (!HttpUtil0.isLocal(skinUrl)) {
            return textures;
        } else {
            File skinFile = new File(CustomSkinLoader.DATA_DIR, skinUrl);
            if (!skinFile.isFile()) {
                return new HashMap();
            } else {
                textures.put(Type.SKIN, ModelManager0.getProfileTexture(HttpTextureUtil.getLocalFakeUrl(skinUrl), (Map)null));
                return textures;
            }
        }
    }

    public Map getTexture(GameProfile profile) {
        if (this.staticTextures.get(profile) != null) {
            return (Map)this.staticTextures.get(profile);
        } else if (this.loadingList.contains(profile)) {
            return new HashMap();
        } else if (this.dynamicTextures.containsKey(profile)) {
            DynamicSkullManager.SkullTexture texture = (DynamicSkullManager.SkullTexture)this.dynamicTextures.get(profile);
            long time = System.currentTimeMillis() - texture.startTime;
            int index = (int)Math.floor((double)(time % (long)texture.period / (long)texture.interval));
            Map map = Maps.newHashMap();
            map.put(Type.SKIN, ModelManager0.getProfileTexture((String)texture.skins.get(index), null));
            return map;
        } else {
            this.loadingList.add(profile);
//            Thread loadThread = new 1(this, profile);
            Thread loadThread = new Thread(() -> {
                DynamicSkullManager.access$0(DynamicSkullManager.this, profile);
                DynamicSkullManager.access$1(DynamicSkullManager.this).remove(profile);
            });
            loadThread.setName("Skull " + profile.hashCode());
            loadThread.start();
            return new HashMap();
        }
    }

    static void access$0(DynamicSkullManager var0, GameProfile var1) {
        var0.parseGameProfile(var1);
    }

    static List access$1(DynamicSkullManager var0) {
        return var0.loadingList;
    }
}
