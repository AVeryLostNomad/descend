package com.maypointstudios.descend

import android.content.pm.ActivityInfo
import com.watabou.noosa.Game

class DescendGame : Game(TitleSc) {

    companion object {
        fun landscape(value : Boolean){
            Game.instance.requestedOrientation = if(value) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            // TODO Preferences
        }

        fun landscape() : Boolean {
            return width > height
        }

        fun scaleUp(value : Boolean) {
            Preferences.INSTANCE.put(Preferences.KEY_SCALE_UP, value)
            switchScene(TitleScene.class) // Switch to this when we actually have it done
        }

        fun scaleUp() : Boolean {
            return Preferences.INSTANCE.getBoolean(Preferences.KEY_SCALE_UP, true)
        }
    }

}