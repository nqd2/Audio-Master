package vn.io.ducnq.audiomaster.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import vn.io.ducnq.audiomaster.service.AudioService

class AudioSessionReceiver : BroadcastReceiver() {

    /**
     * @param context System context
     * @param intent Broadcasted intent containing session state and ID
     * @output Forwards session ID to AudioService for processing
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
        
        if (sessionId == -1 || context == null) return

        val serviceIntent = Intent(context, AudioService::class.java).apply {
            putExtra("SESSION_ID", sessionId)
        }

        when (action) {
            AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> {
                serviceIntent.action = "ACTION_INIT_SESSION"
                context.startService(serviceIntent)
            }
            
            AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> {
                serviceIntent.action = "ACTION_RELEASE_SESSION"
                context.startService(serviceIntent)
            }
        }
    }
}