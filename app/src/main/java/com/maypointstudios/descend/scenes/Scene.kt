package com.maypointstudios.descend.scenes

import android.opengl.GLES20
import com.maypointstudios.descend.Assets
import com.maypointstudios.descend.DescendGame
import com.watabou.input.Touchscreen
import com.watabou.noosa.*
import com.watabou.utils.BitmapCache
import javax.microedition.khronos.opengles.GL10
import kotlin.math.ceil
import kotlin.math.max

private class PixelCamera(zoom: Float) : Camera(
        (Game.width - Math.ceil((Game.width / zoom).toDouble()) * zoom).toInt() / 2,
        (Game.height - Math.ceil((Game.height / zoom).toDouble()) * zoom).toInt() / 2,
        ceil(Game.width / zoom).toInt(),
        ceil(Game.height / zoom).toInt(),
        zoom) {

    override fun updateMatrix() {
        val sx = BaseScene.align(this, scroll.x + shakeX)
        val sy = BaseScene.align(this, scroll.y + shakeY)

        matrix[0] = +zoom * Camera.invW2
        matrix[5] = -zoom * Camera.invH2

        matrix[12] = -1 + x * Camera.invW2 - sx * matrix[0]
        matrix[13] = +1f - y * Camera.invH2 - sy * matrix[5]

    }
}

class GameScene : BaseScene() {

    companion object {
        var scene : GameScene? = null
    }

}

open class BaseScene : Scene() {

    companion object {
        val MIN_WIDTH_P = 128
        val MIN_HEIGHT_P = 224

        val MIN_WIDTH_L = 224
        val MIN_HEIGHT_L = 160

        var defaultZoom = 0f
        var minZoom : Float = 0f
        var maxZoom : Float = 0f

        lateinit var uiCamera : Camera

        var font1x : BitmapText.Font? = null
        var font15x : BitmapText.Font? = null
        var font2x : BitmapText.Font? = null
        var font25x : BitmapText.Font? = null
        var font3x : BitmapText.Font? = null

        fun align(camera : Camera, pos : Float) : Float {
            return (pos * camera.zoom).toInt() / camera.zoom
        }

        var font : BitmapText.Font? = null
        var scale : Float = 1f

        fun chooseFont(size : Float) {
            chooseFont(size, defaultZoom)
        }

        fun chooseFont(size : Float, zoom : Float){
            val pt = size * zoom

            if(pt >= 19){
                scale = pt / 19f
                if(1.5 <= scale && scale < 2){
                    font = font25x
                    scale = (pt / 14).toInt().toFloat()
                }else{
                    font = font3x
                    scale = scale.toInt().toFloat()
                }
            }else if(pt >= 14){
                scale = pt / 14
                if(1.8 <= scale && scale < 2){
                    font = font2x
                    scale = (pt / 12).toInt().toFloat()
                }else{
                    font = font25x
                    scale = scale.toInt().toFloat()
                }
            }else if(pt >= 12){
                scale = pt / 12
                if(1.7 <= scale && scale < 2){
                    font = font15x
                    scale = (pt / 10).toInt().toFloat()
                }else{
                    font = font2x
                    scale = scale.toInt().toFloat()
                }
            }else if(pt >= 10){
                scale = pt / 10
                if(1.4 <= scale && scale < 2){
                    font = font1x
                    scale = (pt / 7).toInt().toFloat()
                }else{
                    font = font15x
                    scale = scale.toInt().toFloat()
                }
            }else{
                font = font1x
                scale = max(1f, (pt / 7).toInt().toFloat())
            }

            scale /= zoom
        }

        fun createText(size : Float) : BitmapText {
            return createText(null, size)
        }

        fun createText(text : String? , size : Float) : BitmapText {
            chooseFont(size)

            val result = BitmapText(text, font)
            result.scale.set(scale)

            return result
        }

        fun createMultiline(size : Float) : BitmapTextMultiline {
            return createMultiline(null, size)
        }

        fun createMultiline(text : String?, size : Float) : BitmapTextMultiline{
            chooseFont(size)
            val result = BitmapTextMultiline(text, font)
            result.scale.set(scale)

            return result
        }

        fun align(v : Visual){
            val c = v.camera
            v.x = align(c, v.x)
            v.y = align(c, v.y)
        }

        var noFade = false

        // TODO BADGES, or maybe we should do this first??? NAH

        class Fader(color : Int, light : Boolean) : ColorBlock(uiCamera.width.toFloat(), uiCamera.height.toFloat(), color) {
            var light : Boolean = light
            var FADE_TIME : Float = 1f
            var time : Float

            init {
                camera = uiCamera

                alpha(1f)
                time = FADE_TIME
            }

            override fun update() {
                super.update()

                time -= Game.elapsed
                if(time <= 0){
                    alpha(0f)
                    parent.remove(this)
                }else{
                    alpha(time / FADE_TIME)
                }
            }

            override fun draw() {
                if(light){
                    GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
                    super.draw()
                    GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
                }else{
                    super.draw()
                }
            }
        }
    }

    fun fadeIn() {
        if(noFade){
            noFade = false
        }else{
            fadeIn(0xFF000000.toInt(), false)
        }
    }

    fun fadeIn(color : Int, light : Boolean) {
        add(Fader(color, light))
    }

    override fun create() {
        super.create()

        GameScene.scene = null

        val minWidth : Int
        val minHeight : Int

        if(DescendGame.landscape()){
            minWidth = MIN_WIDTH_L
            minHeight = MIN_HEIGHT_L
        }else{
            minWidth = MIN_WIDTH_P
            minHeight = MIN_HEIGHT_P
        }

        defaultZoom = ceil(Game.density * 2.5).toFloat()
        while((Game.width / defaultZoom < minWidth || Game.height / defaultZoom < minHeight) && defaultZoom > 1){
            defaultZoom --
        }

        if(DescendGame.scaleUp()){
            while(Game.width / (defaultZoom + 1) > minWidth && Game.height / (defaultZoom + 1) >= minHeight){
                defaultZoom ++
            }
        }
        minZoom = 1f
        maxZoom = defaultZoom * 2f

        Camera.reset(PixelCamera(defaultZoom))

        val uiZoom = defaultZoom
        uiCamera = Camera.createFullscreen(uiZoom)
        Camera.add(uiCamera)

        if(font1x == null){
            font1x = BitmapText.Font.colorMarked(
                    BitmapCache.get(Assets.FONTS1X), 0x00000000, BitmapText.Font.LATIN_FULL
            )
            font1x!!.baseLine = 6f
            font1x!!.tracking = -1f

            font15x = BitmapText.Font.colorMarked(
                    BitmapCache.get(Assets.FONTS15X),
                    0x00000000,
                    BitmapText.Font.LATIN_FULL
            )
            font15x!!.baseLine = 9f
            font15x!!.tracking = -1f

            font2x = BitmapText.Font.colorMarked(
                    BitmapCache.get(Assets.FONTS2X),
                    0x00000000,
                    BitmapText.Font.LATIN_FULL
            )
            font2x!!.baseLine = 11f
            font2x!!.tracking = -1f

            font25x = BitmapText.Font.colorMarked(
                    BitmapCache.get(Assets.FONTS25X),
                    0x00000000,
                    BitmapText.Font.LATIN_FULL
            )
            font25x!!.baseLine = 13f
            font25x!!.tracking = -1f

            font3x = BitmapText.Font.colorMarked(
                    BitmapCache.get(Assets.FONTS3X),
                    0x00000000,
                    BitmapText.Font.LATIN_FULL
            )
            font3x!!.baseLine = 17f
            font3x!!.tracking = -1f
        }
    }

    override fun destroy() {
        super.destroy()
        Touchscreen.event.removeAll()
    }

}