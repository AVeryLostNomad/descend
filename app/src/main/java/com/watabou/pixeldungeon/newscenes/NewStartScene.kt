package com.watabou.pixeldungeon.newscenes

import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.GamesInProgress
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.effects.BannerSprites
import com.watabou.pixeldungeon.scenes.InterlevelScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.ExitButton
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndOptions

class NewStartScene : PixelScene() {

    companion object {
        val BUTTON_HEIGHT = 24f
        val GAP = 2f

        val TXT_LOAD = "Load Game"
        val TXT_NEW = "New Game"

        val TXT_ERASE = "Erase current game"
        val TXT_STATUS = "L%d F%d %s"

        val TXT_REALLY = "Do you really want to start a new game?"
        val TXT_WARNING = "Your current game's progress will be erased."
        val TXT_YES = "Yes, start a new game"
        val TXT_NO = "No, return to main menu"

        val TXT_INDEX = "Save Slot %d"

        val WIDTH_P = 116f
        val HEIGHT_P = 220f
        val WIDTH_L = 224f
        val HEIGHT_L = 124f

        var buttonX : Float = 0f
        var buttonY : Float = 0f

        var curClass : HeroClass = HeroClass.MAGE

        var saveIndex : Int = 0 // Which file we're working with here

        open class GameButton(primary : String) : RedButton(primary) {
            companion object {
                val SECONDARY_COLOR_N = 0xCACFC2
                val SECONDARY_COLOR_H = 0xFFFF88
            }

            lateinit var secondary : BitmapText

            init{
                this.secondary.text(null)
            }

            override fun createChildren() {
                super.createChildren()

                secondary = createText(6f)
                secondary.text(null)
                add(secondary)
            }

            override fun layout() {
                super.layout()

                if(secondary.text() != null){
                    text.y = align(y + (height - text.height() - secondary.baseLine()) / 2)

                    secondary.x = align(x + (width - secondary.width()) / 2)
                    secondary.y = align(text.y + text.height() )
                }else{
                    text.y = align(y + (height - text.baseLine()) / 2)
                }
            }

            fun secondary(text : String?, highlighted : Boolean){
                secondary.text(text)
                secondary.measure()

                secondary.hardlight(if(highlighted) SECONDARY_COLOR_H else SECONDARY_COLOR_N)
            }
        }
    }

    lateinit var newGameButton : GameButton
    lateinit var loadGameButton : GameButton
    lateinit var rightIndexButton : GameButton
    lateinit var leftIndexButton : GameButton

    lateinit var message : BitmapText

    override fun create() {
        super.create()

        Badges.loadGlobal()

        uiCamera.visible = false

        val w = Camera.main.width.toFloat()
        val h = Camera.main.height.toFloat()

        val width : Float
        val height : Float
        if(PixelDungeon.landscape()){
            width = WIDTH_L
            height = HEIGHT_L
        }else{
            width = WIDTH_P
            height = HEIGHT_P
        }

        val left = (w - width) / 2
        val top = (h - height) / 2
        val bottom = h - top

        val titleImage = BannerSprites.get(BannerSprites.Type.SELECT_YOUR_HERO)
        titleImage.x = align((w - titleImage.width) / 2)
        titleImage.y = align(top)
        add(titleImage)

        buttonX = left
        buttonY = bottom - BUTTON_HEIGHT

        newGameButton = object : GameButton(TXT_NEW){
            override fun onClick() {
                if(GamesInProgress.check() != null){
                    // There is a game, in progress, on this particular save slot.
                    this@NewStartScene.add(object : WndOptions(TXT_REALLY, TXT_WARNING, TXT_YES, TXT_NO){
                        override fun onSelect(index: Int) {
                            if(index == 0){
                                startNewGame()
                            }
                        }
                    })
                }else{
                    startNewGame()
                }
            }
        }
        add(newGameButton)

        loadGameButton = object : GameButton(TXT_LOAD) {
            override fun onClick() {
                InterlevelScene.mode = InterlevelScene.Mode.CONTINUE
                Game.switchScene(InterlevelScene::class.java)
            }
        }
        add(loadGameButton)

        leftIndexButton = object : GameButton("<") {
            override fun onClick() {
                saveIndex -= 1
                if(saveIndex <= 0){
                    saveIndex = 0
                    leftIndexButton.enable(false)
                }
                updateIndex(saveIndex)
            }
        }
        leftIndexButton.setRect(0f, (Camera.main.height / 2f) - (BUTTON_HEIGHT / 2), BUTTON_HEIGHT, BUTTON_HEIGHT)
        if(saveIndex == 0) leftIndexButton.enable(false)
        add(leftIndexButton)

        rightIndexButton = object : GameButton(">"){
            override fun onClick() {
                saveIndex += 1
                updateIndex(saveIndex)
                if(saveIndex > 0){
                    leftIndexButton.enable(true)
                }
            }
        }
        rightIndexButton.setRect(Camera.main.width - BUTTON_HEIGHT, (Camera.main.height / 2f) - (BUTTON_HEIGHT / 2), BUTTON_HEIGHT, BUTTON_HEIGHT)
        add(rightIndexButton)

        message = PixelScene.createText(Utils.format(TXT_INDEX, saveIndex), 12f)
        message.measure()
        message.x = (Camera.main.width - message.width()) / 2
        message.y = (Camera.main.height - message.height()) / 4
        add(message)

        val btnExit = ExitButton()
        btnExit.setPos(Camera.main.width - btnExit.width(), 0f)

        updateIndex()

        fadeIn()
    }

    override fun destroy() {
        super.destroy()
    }

    fun updateIndex() {
        updateIndex(0)
    }

    fun updateIndex(newPos : Int) {
        val info = GamesInProgress.check()
        if(info != null){
            loadGameButton.visible = true
            loadGameButton.secondary(Utils.format(TXT_STATUS, info.level, info.depth, info.heroClass.title()), info.challenges)

            newGameButton.visible = true
            newGameButton.secondary(TXT_ERASE, false)

            val w = (Camera.main.width - GAP) / 2 - buttonX
            loadGameButton.setRect(buttonX, buttonY, w, BUTTON_HEIGHT)
            newGameButton.setRect(loadGameButton.right() + GAP, buttonY, w, BUTTON_HEIGHT)
        }else{
            loadGameButton.visible = false

            newGameButton.visible = true
            newGameButton.secondary.text(null)
            newGameButton.setRect(buttonX, buttonY, Camera.main.width - buttonX * 2, BUTTON_HEIGHT)
        }
        message.text(Utils.format(TXT_INDEX, saveIndex))
    }

    fun startNewGame() {
        Dungeon.hero = null
        InterlevelScene.mode = InterlevelScene.Mode.DESCEND

        // TODO handle an intro here, if we'd like.
        Game.switchScene(InterlevelScene::class.java)
    }

}