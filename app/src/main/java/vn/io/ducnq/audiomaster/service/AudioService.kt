package vn.io.ducnq.AudioMaster.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import vn.io.ducnq.AudioMaster.audio.AudioEffectManager

class AudioService : Service() {

    private val activeEffects = HashMap<Int, AudioEffectManager>()

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * @param intent Intent containing actions and extra data
     * @param flags Additional data about this start request
     * @param startId A unique integer representing this specific request
     * @output Handles foreground state and manages audio effect lifecycles
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val sessionId = intent?.getIntExtra("SESSION_ID", -1) ?: -1

        when (action) {
            "ACTION_START_SERVICE" -> startForegroundService()
            "ACTION_STOP_SERVICE" -> stopSelf()
            "ACTION_INIT_SESSION" -> if (sessionId != -1) initEffect(sessionId)
            "ACTION_RELEASE_SESSION" -> if (sessionId != -1) releaseEffect(sessionId)
        }
        return START_STICKY
    }

    /**
     * @output Creates a notification channel and promotes the service to foreground
     */
    private fun startForegroundService() {
        val channelId = "AudioMasterChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "AudioMaster Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("AudioMaster")
            .setContentText("Audio engine is running...")
            .setSmallIcon(android.R.drawable.ic_media_play) 
            .build()

        startForeground(1, notification)
    }

    /**
     * @param sessionId Target audio session ID
     * @output Creates and stores a new AudioEffectManager for the session
     */
    private fun initEffect(sessionId: Int) {
        if (!activeEffects.containsKey(sessionId)) {
            val manager = AudioEffectManager()
            manager.initEffects(sessionId)
            
            // Set default boost values for testing
            manager.setBassStrength(500)
            manager.setVirtualizerStrength(500)
            
            activeEffects[sessionId] = manager
        }
    }

    /**
     * @param sessionId Target audio session ID
     * @output Releases and removes the AudioEffectManager for the session
     */
    private fun releaseEffect(sessionId: Int) {
        activeEffects[sessionId]?.release()
        activeEffects.remove(sessionId)
    }

    /**
     * @output Cleans up all active effects when service is destroyed
     */
    override fun onDestroy() {
        activeEffects.values.forEach { it.release() }
        activeEffects.clear()
        super.onDestroy()
    }
}