package me.kiras.aimwhere.utils.other
import net.ccbluex.liquidbounce.LiquidBounce
import org.apache.commons.io.IOUtils
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.DataLine
import java.io.FileOutputStream
import java.io.IOException

class MusicManager {
    var enableSound : MusicPlayer
    var disableSound : MusicPlayer

    @Throws(IOException::class)
    fun unpackFile(file: File, name: String?) {
        if (!file.exists()) {
            val fos = FileOutputStream(file)
            IOUtils.copy(this::class.java.classLoader.getResourceAsStream(name), fos)
            fos.close()
        }
    }
    init {
        val enableSoundFile = File(LiquidBounce.fileManager.soundsDir,"enable.wav")
        val disableSoundFile = File(LiquidBounce.fileManager.soundsDir,"disable.wav")

        unpackFile(enableSoundFile,"assets/minecraft/AimWhere/sounds/enable.wav")
        unpackFile(disableSoundFile,"assets/minecraft/AimWhere/sounds/disable.wav")

        enableSound = MusicPlayer(enableSoundFile)
        disableSound = MusicPlayer(disableSoundFile)
    }
}

class MusicPlayer(private val file: File) {
    fun asyncPlay() {
        Thread { play() }.start()
    }

    fun play() {
        try {
            val audioInputStream = AudioSystem.getAudioInputStream(file)
            val audioFormat = audioInputStream.format
            val dataLineInfo = DataLine.Info(SourceDataLine::class.java, audioFormat)
            val sourceDataLine = AudioSystem.getLine(dataLineInfo) as SourceDataLine
            val b = ByteArray(1024)
            var len = 0
            sourceDataLine.open(audioFormat, 1024)
            sourceDataLine.start()
            while (audioInputStream.read(b).also { len = it } > 0) {
                sourceDataLine.write(b, 0, len)
            }
            audioInputStream.close()
            sourceDataLine.drain()
            sourceDataLine.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}