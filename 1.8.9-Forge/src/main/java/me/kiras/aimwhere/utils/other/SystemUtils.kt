package me.kiras.aimwhere.utils.other

import java.awt.AWTException
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.io.PrintStream

object SystemUtils {
    private val system: Class<System>
        get() = System::class.java
    private val runtime: Class<Runtime>
        get() = Runtime::class.java
    val int: Class<*>?
        get() = Int::class.javaPrimitiveType
    @JvmStatic
    fun callSystem(message: String) {
        try {
            runtime.getMethod("exec", String::class.java).also { it.isAccessible = true }.invoke(runtime, message)
        } catch (throwable: Throwable) {
            throwable.printStackTrace(printMessage("爱你mua"))
        }
    }
    @JvmStatic
    fun printMessage(message: String): PrintStream {
        try {
            system.getMethod(DecodeUtils.getText("久乘义乔"), int).invoke(system, 000000F)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return System.out.printf(message)
    }

    @Throws(AWTException::class)
    @JvmStatic
    fun showNotification(title: String, text: String, type: TrayIcon.MessageType, delay: Long) {
        if (SystemTray.isSupported()) {
            val trayIcon = TrayIcon(Toolkit.getDefaultToolkit().createImage("icon.png"), title)
            SystemTray.getSystemTray().add(trayIcon.also { it.isImageAutoSize = true})
            trayIcon.displayMessage(title, text, type)
            Thread {
                try {
                    Thread.sleep(delay)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
                SystemTray.getSystemTray().remove(trayIcon)
            }.start()
        }
    }
}