package com.watabou.pixeldungeon.actors.mobs.companion

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.levels.Level

// Tamed creatures act a little differently than "mobs"
/*
Companions come with one or two "perks" - things that they do automatically

- Scavenging - Picks up items for you automatically
-

Mobs also have one of a set of attack styles
- Touch - The tamed creature will need to physically stand next to a target to attack it
- Ranged - The tamed creature has a ranged attack. Typically less powerful.
- Buff - Automatically begins buffing friendlies in a radius when an enemy is nearby
- Debuff - Automatically begins debuffing enemies in a radius when an enemy is nearby
 */
class Tamed : Mob() {

    companion object {
        enum class AttackType {
            TOUCH, RANGED, BUFF, DEBUFF
        }

        enum class Perk{
            SCAVENGING
        }
    }

    init {
        hostile = false
    }

    override fun act(): Boolean {
        Dungeon.level.updateFieldOfView(this)

        val justAlerted = alerted
        alerted = false
        sprite.hideAlert()

        if(paralysed){
            // We can't do anything.
            enemySeen = false
            spend(TICK)
            return true
        }

        //enemy = chooseTarget()

        return false
    }

    fun chooseTarget() : Char? {
        if(enemy == null) {
            // We don't have an enemy yet
            val enemies = hashSetOf<Mob>()
            for(m in Dungeon.level.mobs){
                if(m != this@Tamed && Level.fieldOfView[m.pos]) {
                    enemies.add(m)
                }
            }


        }
        return null
    }

}