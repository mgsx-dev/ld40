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
import net.mgsx.ld40.model.Rules;

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
		table.add(createMenuButton("Play", 0)).row();
		table.add(createMenuButton("Level 1", 0)).row();
		table.add(createMenuButton("Level 2", 1)).row();
		table.add(createMenuButton("Level 3", 2)).row();
		table.add(createMenuButton("Level 4", 4)).row();
		table.add(createMenuButton("Level 5", 5)).row();
		
		Table root = new Table();
		root.add(table).expand().center();
		root.setFillParent(true);
		stage.addActor(root);
		
	}
	
	private Actor createMenuButton(String name, final int mapId){
		TextButton bt = new TextButton(name, skin);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Rules.levelID = mapId;
				Rules.initLevel();
				((Game)Gdx.app.getApplicationListener()).setScreen(new LevelScreen());
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
