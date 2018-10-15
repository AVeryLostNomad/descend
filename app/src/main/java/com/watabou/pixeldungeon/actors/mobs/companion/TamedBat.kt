package com.watabou.pixeldungeon.actors.mobs.companion

import android.util.Log
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.sprites.BatSprite
import com.watabou.utils.Random

class TamedBat : Tamed() {

    init{
        COMPANION_DEFAULT_NAME = "Friendly Bat"
        name = "vampire bat"
        spriteClass = BatSprite::class.java
        HP = 15
        HT = 15
        defenseSkill = 10
        baseSpeed = 2f

        EXP = 0
        maxLvl = 15
        flying = true
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(6, 12)
    }

    override fun attackSkill(target: Char?): Int {
        return 16
    }

    override fun dr(): Int {
        return 4
    }

    override fun defenseVerb(): String {
        return "evaded"
    }

    override fun attackProc(enemy: Char?, damage: Int): Int {
        return damage
    }

    override fun description(): String {
        return "A small bat you managed to trap.."
    }

    override fun move(step: Int) {
        Log.e("HAH", "Moving bat")
        super.move(step)
    }

}