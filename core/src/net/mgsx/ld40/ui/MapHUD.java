package net.mgsx.ld40.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.model.Rules;
import net.mgsx.ld40.screens.LevelScreen;
import net.mgsx.ld40.screens.MenuScreen;

public class MapHUD extends Table
{
	private int life;
	private Array<Image> heats = new Array<Image>();
	private LevelScreen levelScreen;
	
	public MapHUD(LevelScreen levelScreen, Skin skin) {
		super(skin);
		this.levelScreen = levelScreen;
		
		Table hud = new Table(skin);
		
		add(hud).expand().top().fillX();
		
		Table heartTable = new Table(skin);
		heartTable.defaults().pad(2);
		for(int i=0 ; i<Rules.maxLife ; i++){
			Image image = new Image(skin.getDrawable("heart"));
			image.setScaling(Scaling.fit);
			image.setColor(Color.GRAY);
			heartTable.add(image).size(16);
			heats.add(image);
		}
		
		hud.add(Rules.levelTitle).padRight(20);
		hud.add(heartTable);
		
		life = 0;
	}
	
	@Override
	public void act(float delta) {
		if(life != Rules.life){
			life = Rules.life;
			for(int i=0 ; i<Rules.maxLife ; i++){
				Image image = heats.get(i);
				if(i < life){
					
				}
				image.setColor(i < life ? Color.WHITE : Color.GRAY);
			}
		}
		super.act(delta);
	}
	
	public void setGetReady() 
	{
		
		Table panel = new Table(getSkin());
		panel.setBackground("panel");
		
		final Table popup = popup(panel);
		
		panel.add("GET READY").row();
		panel.add("Crazy snake is ready !\n press any key to start").row();
		
		TextButton bt;
		panel.add(bt = new TextButton("Start", getSkin()){
			@Override
			public void act(float delta) {
				if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){
					levelScreen.isPaused = false;
					popup.remove();
				}
				super.act(delta);
			}
		}).row();
		
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				levelScreen.isPaused = false;
				popup.remove();
			}
		});
		
	}

	public void setGameOver() 
	{
		
		Table panel = new Table(getSkin());
		panel.setBackground("panel");
		
		final Table popup = popup(panel);
		
		panel.add("GAME OVER").row();
		panel.add("Crazy snake is dead ... \nhe was certainly not cautious enough ...\n or too greedy !").row();
		
		TextButton bt;
		panel.add(bt = new TextButton("Back to main menu", getSkin())).row();
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
			}
		});
		
	}

	public void setLevelComplete() {
		
		Table panel = new Table(getSkin());
		panel.setBackground("panel");
		
		final Table popup = popup(panel);
		
		
		Rules.levelID++;
		if(Rules.initLevel()){
			panel.add("LEVEL COMPLETE").row();
			panel.add("Crazy snake is fine ... \nnow he need to digest !").row();
			TextButton bt;
			panel.add(bt = new TextButton("Go to next level", getSkin())).row();
			bt.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					((Game)Gdx.app.getApplicationListener()).setScreen(new LevelScreen());
				}
			});
		}else{
			panel.add("GAME COMPLETE").row();
			panel.add("You finished the game!\nCongratulation!\nThanks for playing!").row();
			TextButton bt;
			panel.add(bt = new TextButton("Game to main menu", getSkin())).row();
			bt.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
				}
			});
		}
		
		
	}
	
	private Table popup(Table table){
		LevelAssets.i.sndGUI.play();
		Table popup = new Table(getSkin());
		popup.setFillParent(true);
		popup.add(table).expand().center();
		getStage().addActor(popup);
		popup.setTouchable(Touchable.enabled);
		return popup;
	}
}
