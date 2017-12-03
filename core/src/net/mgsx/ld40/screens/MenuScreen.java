package net.mgsx.ld40.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.model.Rules;

public class MenuScreen extends ScreenAdapter
{
	private static boolean BACKDOOR = false;
	
	private Stage stage;
	private Skin skin;

	private TextButton btPlay;
	
	public MenuScreen() {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		LevelAssets.i.song1.setLooping(true);
		LevelAssets.i.song1.setVolume(0.8f);
		LevelAssets.i.song1.play();
		
		Rules.life = Rules.startLife;
		
		skin = LevelAssets.i.skin;
		
		Table table = new Table(skin);
		table.defaults().pad(4);

		table.setBackground(new TextureRegionDrawable(new TextureRegion(LevelAssets.i.cover)));
		
		if(BACKDOOR){
			table.add(btPlay = createMenuButton("Play", 1)).row();
			table.add(createMenuButton("Level 1", 1)).row();
			table.add(createMenuButton("Level 2", 2)).row();
			table.add(createMenuButton("Level 3", 3)).row();
			table.add(createMenuButton("Level 4", 4)).row();
			table.add(createMenuButton("Level 5", 5)).row();
		}else{
			table.add(btPlay = createMenuButton("Play", 1)).expand().bottom().padBottom(40);
		}
		btPlay.setTransform(true);
		btPlay.setOrigin(Align.center);
		
		Table root = new Table();
		root.add(table).expand().center();
		root.setFillParent(true);
		stage.addActor(root);
		
	}
	
	private TextButton createMenuButton(String name, final int mapId){
		TextButton bt = new TextButton(name, skin);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Rules.levelID = mapId;
				Rules.initLevel();
				((Game)Gdx.app.getApplicationListener()).setScreen(new LevelScreen());
				LevelAssets.i.song1.stop();
			}
		});
		return bt;
	}
	
	public void render(float delta)
	{
		if(!btPlay.hasActions()){
			btPlay.addAction(Actions.sequence(
				Actions.scaleTo(1.2f, 1.2f, .5f),
				Actions.scaleTo(1, 1, .5f)
					));
		}
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		super.dispose();
	}
}
