package me.kiras.aimwhere.customskinloader.utils;
import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;


public class HttpRequestUtil
{
    public static final File CACHE_DIR;
    private static final Pattern MAX_AGE_PATTERN;

    static {
        CACHE_DIR = new File(CustomSkinLoader.DATA_DIR, "caches");
        MAX_AGE_PATTERN = Pattern.compile(".*?max-age=(\\d+).*?");
    }
    public static class CacheInfo {
        public String url;
        public String etag = null;
        public long lastModified = -1L;
        public long expire = -1L;
    }

    public static class HttpResponce {
        public String content = null;
        public int responceCode = -1;
        public boolean success = false;
        public boolean fromCache = false;
    }

    public static class HttpRequest {
        public String url;
        public String userAgent = null;
        public String payload = null;
        public boolean loadContent = true;
        public boolean checkPNG = false;
        public int cacheTime = 600;
        public File cacheFile = null;

        public HttpRequest(String url) {
            this.url = url;
        }

        public HttpRequest setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public HttpRequest setPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public HttpRequest setLoadContent(boolean loadContent) {
            this.loadContent = loadContent;
            return this;
        }

        public HttpRequest setCheckPNG(boolean checkPNG) {
            this.checkPNG = checkPNG;
            return this;
        }

        public HttpRequest setCacheTime(int cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }

        public HttpRequest setCacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }
    }





    public static HttpRequestUtil.HttpResponce makeHttpRequest(final HttpRequestUtil.HttpRequest request) {
        try {
            CustomSkinLoader.logger.info("Try to request '" + request.url + ((request.userAgent == null) ? "'." : ("' with user agent '" + request.userAgent + "'.")));
            if (StringUtils.isNotEmpty((CharSequence)request.payload)) {
                request.cacheTime = -1;
            }
            File cacheInfoFile = null;
            HttpRequestUtil.CacheInfo cacheInfo = new HttpRequestUtil.CacheInfo();
            if (request.cacheFile == null && request.cacheTime >= 0) {
                final String hash = DigestUtils.sha1Hex(request.url);
                request.cacheFile = new File(HttpRequestUtil.CACHE_DIR, hash);
                cacheInfoFile = new File(HttpRequestUtil.CACHE_DIR, String.valueOf(hash) + ".json");
            }
            if (request.cacheTime == 0 && request.cacheFile.isFile()) {
                return loadFromCache(request, new HttpRequestUtil.HttpResponce());
            }
            if (cacheInfoFile != null && cacheInfoFile.isFile()) {
                final String json = FileUtils.readFileToString(cacheInfoFile);
                if (StringUtils.isNotEmpty((CharSequence)json)) {
                    cacheInfo = CustomSkinLoader.GSON.fromJson(json, HttpRequestUtil.CacheInfo.class);
                }
                if (cacheInfo == null) {
                    cacheInfo = new HttpRequestUtil.CacheInfo();
                }
                if (cacheInfo.expire >= TimeUtil.getCurrentUnixTimestamp()) {
                    return loadFromCache(request, new HttpRequestUtil.HttpResponce());
                }
            }
            final String url = new URI(request.url).toASCIIString();
            if (!url.equalsIgnoreCase(request.url)) {
                CustomSkinLoader.logger.info("Encoded URL: " + url);
            }
            final HttpURLConnection c = (HttpURLConnection)new URL(url).openConnection();
            c.setReadTimeout(10000);
            c.setConnectTimeout(10000);
            c.setDoInput(true);
            c.setUseCaches(false);
            c.setInstanceFollowRedirects(true);
            if (cacheInfo.lastModified >= 0L) {
                c.setIfModifiedSince(cacheInfo.lastModified);
            }
            if (cacheInfo.etag != null) {
                c.setRequestProperty("If-None-Match", cacheInfo.etag);
            }
            c.setRequestProperty("Accept-Encoding", "gzip");
            if (request.userAgent != null) {
                c.setRequestProperty("User-Agent", request.userAgent);
            }
            if (StringUtils.isNotEmpty((CharSequence)request.payload)) {
                CustomSkinLoader.logger.info("Payload: " + request.payload);
                c.setDoOutput(true);
                final OutputStream os = c.getOutputStream();
                IOUtils.write(request.payload, os);
                IOUtils.closeQuietly(os);
            }
            c.connect();
            final HttpRequestUtil.HttpResponce responce = new HttpRequestUtil.HttpResponce();
            responce.responceCode = c.getResponseCode();
            final int res = c.getResponseCode() / 100;
            if (res == 4 || res == 5) {
                CustomSkinLoader.logger.info("Failed to request (Response Code: " + c.getResponseCode() + ")");
                return responce;
            }
            responce.success = true;
            CustomSkinLoader.logger.info("Successfully request (Response Code: " + c.getResponseCode() + " , Content Length: " + c.getContentLength() + ")");
            if (responce.responceCode == 304) {
                return loadFromCache(request, responce);
            }
            final InputStream is = "gzip".equals(c.getContentEncoding()) ? new GZIPInputStream(c.getInputStream()) : c.getInputStream();
            final byte[] bytes = IOUtils.toByteArray(is);
            if (request.checkPNG && (bytes.length <= 4 || bytes[1] != 80 || bytes[2] != 78 || bytes[3] != 71)) {
                CustomSkinLoader.logger.info("Failed to request (Not Standard PNG)");
                responce.success = false;
                return responce;
            }
            if (request.cacheFile != null) {
                FileUtils.writeByteArrayToFile(request.cacheFile, bytes);
                if (cacheInfoFile != null) {
                    cacheInfo.url = request.url;
                    cacheInfo.etag = c.getHeaderField("ETag");
                    cacheInfo.lastModified = c.getLastModified();
                    cacheInfo.expire = getExpire(c, request.cacheTime);
                    FileUtils.write(cacheInfoFile, CustomSkinLoader.GSON.toJson(cacheInfo));
                }
                CustomSkinLoader.logger.info("Saved to cache (Length: " + request.cacheFile.length() + " , Path: '" + request.cacheFile.getAbsolutePath() + "' , Expire: " + cacheInfo.expire + ")");
            }
            if (!request.loadContent) {
                return responce;
            }
            responce.content = new String(bytes, Charsets.UTF_8);
            CustomSkinLoader.logger.info("Content: " + responce.content);
            return responce;
        }
        catch (Exception e) {
            CustomSkinLoader.logger.info("Failed to request (Exception: " + e.toString() + ")");
            return loadFromCache(request, new HttpRequestUtil.HttpResponce());
        }
    }

    public static File getCacheFile(final String hash) {
        return new File(HttpRequestUtil.CACHE_DIR, hash);
    }

    private static HttpRequestUtil.HttpResponce loadFromCache(final HttpRequestUtil.HttpRequest request, final HttpRequestUtil.HttpResponce responce) {
        if (request.cacheFile == null || !request.cacheFile.isFile()) {
            return responce;
        }
        CustomSkinLoader.logger.info("Cache file found (Length: " + request.cacheFile.length() + " , Path: '" + request.cacheFile.getAbsolutePath() + "')");
        responce.fromCache = true;
        responce.success = true;
        if (!request.loadContent) {
            return responce;
        }
        CustomSkinLoader.logger.info("Try to load from cache '" + request.cacheFile.getAbsolutePath() + "'.");
        try {
            responce.content = FileUtils.readFileToString(request.cacheFile);
            CustomSkinLoader.logger.info("Successfully to load from cache");
        }
        catch (IOException e) {
            CustomSkinLoader.logger.info("Failed to load from cache (Exception: " + e.toString() + ")");
            responce.success = false;
        }
        return responce;
    }

    private static long getExpire(final HttpURLConnection connection, final int cacheTime) {
        final String cacheControl = connection.getHeaderField("Cache-Control");
        if (StringUtils.isNotEmpty(cacheControl)) {
            final Matcher m = HttpRequestUtil.MAX_AGE_PATTERN.matcher(cacheControl);
            if (m != null && m.matches()) {
                return TimeUtil.getUnixTimestamp(Long.parseLong(m.group(m.groupCount())));
            }
        }
        final long expires = connection.getExpiration();
        if (expires > 0L) {
            return expires / 1000L;
        }
        return TimeUtil.getUnixTimestampRandomDelay((cacheTime == 0) ? 86400L : ((long)cacheTime));
    }
}
