package com.maypointstudios.descend

import android.content.SharedPreferences
import com.watabou.noosa.Game

class Preferences {

    companion object {
        val INSTANCE = Preferences()

        val KEY_LANDSCAPE = "landscape"
        val KEY_SCALE_UP = "scaleup"
    }

    private var prefs : SharedPreferences? = null

    private fun get() : SharedPreferences {
        if(prefs == null)
            prefs = Game.instance.getPreferences(Game.MODE_PRIVATE)

        return prefs!!
    }

    fun getInt(key : String, defValue : Int) : Int {
        return get().getInt(key, defValue)
    }

    fun getBoolean(key : String, defValue : Boolean) : Boolean {
        return get().getBoolean(key, defValue)
    }

    fun getString(key : String, defValue : String) : String {
        return get().getString(key, defValue)
    }

    fun put(key : String, value : Int){
        get().edit().putInt(key, value).commit()
    }

    fun put(key : String, value : Boolean){
        get().edit().putBoolean(key, value).commit()
    }

    fun put(key : String, value : String){
        get().edit().putString(key, value).commit()
    }

}