package com.averylostnomad.sheep;

import com.watabou.pixeldungeon.levels.Room;

import java.util.ArrayList;

public class GeneratorSpec {

    public static ArrayList<MobSpawn> mobs = new ArrayList<>();

    public static boolean forceGenerateShop = false;
    public static boolean forceBossLevel = false;

    public static ArrayList<Room.Type> roomsMustcontain = new ArrayList<>();
    public static ArrayList<Room.Type> roomsMustNotcontain = new ArrayList<>();

    public static class MobSpawn {


    }

    public static class SignInfo {
        public Room.Type roomTypeToSpawnIn = null;
        // The sign text itself will have to be streamed, also.
        // Perhaps into the LiveDungeon class, where it would store these
        public int signPos; // To be updated on actual runtime.
        public String signText = "";

        SignInfo(Room.Type room){
            this.roomTypeToSpawnIn = room;
        }
    }

    public static ArrayList<SignInfo> signsToGenerate = new ArrayList<>();

    static{
        roomsMustcontain.add(Room.Type.ARMORY);

        // Spawn a sign in the entrance
        signsToGenerate.add(new SignInfo(Room.Type.ENTRANCE));
    }

}
