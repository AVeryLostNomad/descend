package com.averylostnomad.sheep;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.items.Generator;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.SewerLevel;
import com.watabou.pixeldungeon.levels.features.Chasm;
import com.watabou.pixeldungeon.newscenes.NewStartScene;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TestMain {

    public static boolean ADMIN_MODE = false;

    public static void main(String[] args) throws IOException {
        Generator.reset();
        Dungeon.init();
        Level l = Dungeon.newLevelAdmin(SewerLevel.class, 1);

        try{
            HeadlessBundle b = new HeadlessBundle();
            b.put( "level", l );
            OutputStream output = new FileOutputStream("test.json");
            HeadlessBundle.write( b, output );
            output.close();
        }catch(Exception e){
            e.printStackTrace();
        }


//        int step = -1;
//        int curPos = l.entrance;
//
//        if (Level.adjacent( curPos, l.exit )) {
//            if (Level.passable[l.exit] || Level.avoid[l.exit]) {
//                step = l.exit;
//            }
//        } else {
//            int len = Level.LENGTH;
//            boolean[] passable = new boolean[len];
//            for (int i=0; i < len; i++) {
//                passable[i] = true;
//            }
//
//            step = PathFinder.getStep( curPos, l.exit, passable );
//            System.out.println(step);
//        }
    }

}
