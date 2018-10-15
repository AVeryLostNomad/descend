package com.watabou.pixeldungeon.actors.mobs.companion

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Level.distance
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Random

// Tamed creatures act a little differently than "mobs"
/*
Companions come with one or two "perks" - things that they do automatically

- Scavenging - Picks up items for you automatically
- Vampiric - The dungeon hero is healed when the creature deals damage

Mobs also have one of a set of attack styles
- Touch - The tamed creature will need to physically stand next to a target to attack it
- Ranged - The tamed creature has a ranged attack. Typically less powerful.
- Buff - Automatically begins buffing friendlies in a radius when an enemy is nearby
- Debuff - Automatically begins debuffing enemies in a radius when an enemy is nearby
 */
open class Tamed : Mob() {

    companion object {
        enum class AttackType {
            TOUCH, RANGED, BUFF, DEBUFF
        }

        enum class Perk{
            SCAVENGING
        }
    }

    val DEATH_MSG = "Your companion %s has died"
    var COMPANION_DEFAULT_NAME = "Creature"

    var FOLLOWING : AiState
    val TAG_FOLLOW = "following"

    var HUNTING_DISTANCE = 3f

    override fun storeInBundle(bundle: Bundle?) {
        if(state == FOLLOWING){
            bundle!!.put("state", TAG_FOLLOW)
        }
        super.storeInBundle(bundle)
    }

    override fun restoreFromBundle(bundle: Bundle?) {
        val st = bundle!!.getString("state")
        if(st.equals(TAG_FOLLOW)){
            state = FOLLOWING
        }
    }

    init {
        hostile = false
        IS_TAMED = true

        class Following : AiState {

            override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
                if(enemyInFOV && (justAlerted || Random.Int(distance(enemy) / 2 + enemy.stealth()) == 0)){
                    enemySeen = true

                    notice()

                    state = HUNTING
                    target = enemy.pos
                }else{
                    enemySeen = false

                    val oldPos = pos

                    if(target != -1 && getCloser(target)) {
                        spend(1 / speed())
                        return moveSprite(oldPos, pos)
                    }else{
                        target = Dungeon.hero.pos
                        spend(TICK)
                    }
                }
                return true
            }

            override fun status(): String {
                return Utils.format("This %s is following you", name)
            }

        }

        FOLLOWING = Following()

        class NewWander : AiState {

            override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
                state = FOLLOWING
                spend(TICK)
                return true
            }

            override fun status(): String {
                return Utils.format("%s is thinking...", name)
            }

        }

        WANDERING = NewWander()
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

        enemy = chooseTarget()

        val enemyInFOV = enemy != null && enemy.isAlive &&
                Level.fieldOfView[enemy.pos] && enemy.invisible <= 0 &&
                distance(enemy) <= HUNTING_DISTANCE

        return state.act(enemyInFOV, justAlerted)
    }

    override fun destroy() {
        HP = 0
        Actor.remove(this)
        Actor.freeCell(pos)

        Dungeon.level.mobs.remove(this)

        if(Dungeon.hero.isAlive){
            GLog.n(Utils.format(DEATH_MSG, COMPANION_DEFAULT_NAME))
        }
    }

    fun chooseTarget() : Char? {
        if(enemy == null) {
            // We don't have an enemy yet
            val enemies = hashMapOf<Mob, Int>()
            for(m in Dungeon.level.mobs){
                if(m != this@Tamed && Level.fieldOfView[m.pos]) {
                    enemies.put(m, distance(m))
                }
            }

            if(enemies.size == 0) return null

            return enemies.minBy { it.value }!!.key
        }
        return null
    }

}