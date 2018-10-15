/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon;

import java.util.HashMap;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.newscenes.NewStartScene;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundle;

public class GamesInProgress {

	private static HashMap<Integer, Info> state = new HashMap<Integer, Info>();
	
	public static Info check() {
		
		if (state.containsKey(NewStartScene.Companion.getSaveIndex())) {
			
			return state.get( NewStartScene.Companion.getSaveIndex() );
			
		} else {
			
			Info info;
			try {
				
				Bundle bundle = Dungeon.gameBundle(Dungeon.getGamefile());
				info = new Info();
				Dungeon.preview( info, bundle );

			} catch (Exception e) {
				info = null;
				e.printStackTrace();
			}
			
			state.put( NewStartScene.Companion.getSaveIndex(), info );
			return info;
			
		}
	}
	
	public static void set(int depth, int level, boolean challenges ) {
		Info info = new Info();
		info.depth = depth;
		info.level = level;
		info.challenges = challenges;
		state.put( NewStartScene.Companion.getSaveIndex(), info );
	}
	
	public static void setUnknown() {
		state.remove( NewStartScene.Companion.getSaveIndex() );
	}
	
	public static void delete() {
		state.put( NewStartScene.Companion.getSaveIndex(), null );
	}
	
	public static class Info {
		public int depth;
		public int level;
		public HeroClass heroClass;
		public boolean challenges;
	}
}
