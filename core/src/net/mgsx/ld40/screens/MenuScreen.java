package net.mgsx.ld40.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.ld40.assets.LevelAssets;

public class MenuScreen extends ScreenAdapter
{
	private Stage stage;
	private Skin skin;
	
	public MenuScreen() {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		skin = LevelAssets.i.skin;
		
		Table table = new Table(skin);
		table.defaults().pad(4);
		table.setBackground("panel");
		table.add("CRAZY SNAKE").row();
		table.add("The Greedy Adventure").row();
		table.add(createMenuButton("Play", "level0.tmx")).row();
		table.add(createMenuButton("Level 0", "level0.tmx")).row();
		table.add(createMenuButton("Level 1", "level1.tmx")).row();
		
		Table root = new Table();
		root.add(table).expand().center();
		root.setFillParent(true);
		stage.addActor(root);
		
	}
	
	private Actor createMenuButton(String name, final String mapName){
		TextButton bt = new TextButton(name, skin);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new LevelScreen(mapName));
			}
		});
		return bt;
	}
	
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
}
