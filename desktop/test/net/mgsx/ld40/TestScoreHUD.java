package net.mgsx.ld40;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.model.Rules;
import net.mgsx.ld40.ui.ScoreHUD;

public class TestScoreHUD extends Game{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Rules.WIDTH;
		config.height = Rules.HEIGHT;
		new LwjglApplication(new TestScoreHUD(), config);
	}

	@Override
	public void create() {
		setScreen(new ScreenAdapter(){
			private Stage stage;
			{{
				stage = new Stage(new ScreenViewport());
				LevelAssets.i = new LevelAssets();
				Table root = new Table();
				root.setFillParent(true);
				stage.addActor(root);
				
				Rules.levelTitle = "Level 1";
				Rules.levelSubTitle = "The demon forest hidden";
				Rules.tailsWhenExit = 24;
				
				ScoreHUD hud = new ScoreHUD(LevelAssets.i.skin);
				root.add(hud).expand().center();
			}}
			@Override
			public void render(float delta) {
				stage.act();
				stage.draw();
			}
			@Override
			public void resize(int width, int height) {
				stage.getViewport().update(width, height, true);
			}
		});
	}
}
