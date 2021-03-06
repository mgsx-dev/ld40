package net.mgsx.ld40;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.screens.MenuScreen;

public class LD40 extends Game {
	
	private Array<Screen> screensToDispose = new Array<Screen>();
	
	@Override
	public void create () {
		
		LevelAssets.i = new LevelAssets();
		
		setScreen(new MenuScreen());
	}
	
	@Override
	public void render() {
		while(screensToDispose.size > 0){
			screensToDispose.pop().dispose();
		}
		super.render();
	}
	
	@Override
	public void setScreen(Screen screen) {
		if(this.screen != null){
			screensToDispose.add(this.screen);
		}
		super.setScreen(screen);
	}
	
}
