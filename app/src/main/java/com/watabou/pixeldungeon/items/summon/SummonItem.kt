package com.watabou.pixeldungeon.items.summon

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.companion.TamedBat
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import java.util.ArrayList

class SummonItem : Item() {

    companion object {
        val AC_USE = "SUMMON"
        val TIME_TO_SUMMON = 1f
    }

    init{
        stackable = false
        defaultAction = AC_USE
    }

    override fun actions(hero: Hero?): ArrayList<String> {
        return arrayListOf(AC_USE)
    }

    override fun execute(hero: Hero?, action: String?) {
        if(action.equals(AC_USE)){
            val mob = TamedBat()
            mob.state = mob.WANDERING
            mob.pos = Dungeon.hero.pos + 1
            GameScene.add(mob, 2f)
        }else{
            super.execute(hero, action)
        }
    }

}