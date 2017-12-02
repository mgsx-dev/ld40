package net.mgsx.ld40;

import com.badlogic.gdx.Game;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.screens.LevelScreen;

public class LD40 extends Game {
	
	
	@Override
	public void create () {
		
		LevelAssets.i = new LevelAssets();
		
		setScreen(new LevelScreen());
	}
	
}
