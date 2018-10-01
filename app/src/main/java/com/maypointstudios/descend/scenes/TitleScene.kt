package com.maypointstudios.descend.scenes

import com.maypointstudios.descend.Assets
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Music

/*
Changes:
BADGES -> Achievement system
 */
class TitleScene : BaseScene() {

    companion object {
        val TXT_PLAY = "Play"
        val TXT_HIGHSCORES = "Rankings"
        val TXT_ACHIEVEMENTS = "Achievements"
        val TXT_ABOUT = "About"
    }

    override fun create() {
        super.create()

        Music.INSTANCE.play(Assets.THEME, true)
        Music.INSTANCE.volume(1f)

        uiCamera.visible = false

        val w = Camera.main.width
        val h = Camera.main.height

        Archs
    }

}