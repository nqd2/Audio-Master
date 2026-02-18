package vn.io.ducnq.AudioMaster.audio

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer

class AudioEffectManager {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    /**
     * @param audioSessionId Target audio session ID
     * @output Initializes and enables audio effects for the given session
     */
    fun initEffects(audioSessionId: Int) {
        try {
            equalizer = Equalizer(0, audioSessionId).apply { enabled = true }
            bassBoost = BassBoost(0, audioSessionId).apply { enabled = true }
            virtualizer = Virtualizer(0, audioSessionId).apply { enabled = true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param strength Range 0 to 1000
     * @output Applies bass boost strength if supported
     */
    fun setBassStrength(strength: Short) {
        bassBoost?.takeIf { it.strengthSupported }?.setStrength(strength.coerceIn(0, 1000))
    }

    /**
     * @param strength Range 0 to 1000
     * @output Applies virtualizer strength if supported
     */
    fun setVirtualizerStrength(strength: Short) {
        virtualizer?.takeIf { it.strengthSupported }?.setStrength(strength.coerceIn(0, 1000))
    }

    /**
     * @output Disables and releases all audio effect resources to prevent memory leaks
     */
    fun release() {
        equalizer?.apply { enabled = false; release() }
        bassBoost?.apply { enabled = false; release() }
        virtualizer?.apply { enabled = false; release() }
        
        equalizer = null
        bassBoost = null
        virtualizer = null
    }
}