package me.kiras.aimwhere.customskinloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BufferedWriter writer = null;

    public Logger() {
    }

    public Logger(File logFile) {
        try {
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }

            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8"));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void close() {
        if (this.writer != null) {
            try {
                this.writer.close();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

    }

    public void log(Level level, String msg) {
        if (level.display() || this.writer != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(Thread.currentThread().getName()).append(" ").append(level.getName()).append("] ");
            sb.append(msg);
            if (level.display) {
                System.out.println(sb.toString());
            }

            if (this.writer != null) {
                try {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("[").append(DATE_FORMAT.format(new Date())).append("] ");
                    sb2.append(sb.toString()).append("\r\n");
                    this.writer.write(sb2.toString());
                    this.writer.flush();
                } catch (Exception var5) {
                    var5.printStackTrace();
                }

            }
        }
    }

    public void debug(String msg) {
        this.log(Level.DEBUG, msg);
    }

    public void info(String msg) {
        this.log(Level.INFO, msg);
    }

    public void warning(String msg) {
        this.log(Level.WARNING, msg);
    }

    public void warning(Exception e) {
        this.log(Level.WARNING, "Exception: " + e.toString());
        StackTraceElement[] stes = e.getStackTrace();
        StackTraceElement[] var6 = stes;
        int var5 = stes.length;

        for(int var4 = 0; var4 < var5; ++var4) {
            StackTraceElement ste = var6[var4];
            this.log(Level.WARNING, ste.toString());
        }

    }
    public enum Level {
        DEBUG("DEBUG", false),
        INFO("INFO", true),
        WARNING("WARNING", true);

        String name;
        boolean display;

        private Level(String name, boolean display) {
            this.name = name;
            this.display = display;
        }

        public String getName() {
            return this.name;
        }

        public boolean display() {
            return this.display;
        }
    }

}
