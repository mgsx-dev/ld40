package net.mgsx.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.ld40.assets.LevelAssets;

public class MenuScreen extends ScreenAdapter
{
	private Stage stage;
	
	public MenuScreen() {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		Table table = new Table(LevelAssets.i.skin);
		table.setBackground("panel");
		table.add("Hello world !");
		
		Table root = new Table();
		root.add(table).expand().center();
		root.setFillParent(true);
		stage.addActor(root);
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
