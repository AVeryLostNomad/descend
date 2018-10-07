package com.averylostnomad.sheep;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.SewerLevel;

public class TestMain {

    public static boolean ADMIN_MODE = false;

    public static void main(String[] args){
        Dungeon.init();
        Level l = Dungeon.newLevelAdmin(SewerLevel.class);
    }

}
