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
		
		panel.defaults().pad(4);
		
		panel.add(Rules.levelTitle).row();
		panel.add(Rules.levelSubTitle).padBottom(30).row();
		
		panel.add(Rules.levelHints).row();
		panel.add("(" + Rules.levelWords + ")").row();
		
		
		panel.add("GET READY and press any key to start").padTop(30).row();
		
		TextButton bt;
		panel.add(bt = new TextButton("Start\n(press any key)", getSkin()){
			@Override
			public void act(float delta) {
				if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){
					playGame();
					popup.remove();
				}
				super.act(delta);
			}
		}).row();
		
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				playGame();
				popup.remove();
			}
		});
		
	}
	
	private void playGame(){
		levelScreen.isPaused = false;
		LevelAssets.i.song2.setLooping(true);
		LevelAssets.i.song2.play();
	}

	public void setGameOver() 
	{
		LevelAssets.i.song2.stop();
		
		Table panel = new Table(getSkin());
		panel.setBackground("panel");
		
		final Table popup = popup(panel);
		panel.defaults().pad(4);
		
		panel.add("GAME OVER").row();
		panel.add("Crazy snake is dead ...").row();
		panel.add("he was certainly not cautious enough").row();
		panel.add("... or too greedy !").row();
		
		TextButton bt;
		panel.add(bt = new TextButton("Back to main menu\n(press enter)", getSkin()){
			@Override
			public void act(float delta) {
				if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
					((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
				}
				super.act(delta);
			}
		}).row();
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
			}
		});
		
	}

	public void setLevelComplete() {
		
		LevelAssets.i.song2.stop();
		
		ScoreHUD score = new ScoreHUD(getSkin());
		
		final Table popup = popup(score);
		
		score.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				popup.remove();
				setNextPanel();
			}
		});
	}
	
	private void setNextPanel(){
		Table panel = new Table(getSkin());
		panel.setBackground("panel");
		
		final Table popup = popup(panel);
		
		
		Rules.levelID++;
		if(Rules.initLevel()){
			((Game)Gdx.app.getApplicationListener()).setScreen(new LevelScreen());
		}else{
			panel.add("GAME COMPLETE").row();
			panel.add("You finished the game!\nCongratulation!\nThanks for playing!").row();
			TextButton bt;
			panel.add(bt = new TextButton("Back to main menu", getSkin())).row();
			bt.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
				}
			});
		}
		
		
	}
	
	private Table popup(Table table){
		Table popup = new Table(getSkin());
		popup.setFillParent(true);
		popup.add(table).expand().center();
		getStage().addActor(popup);
		popup.setTouchable(Touchable.enabled);
		return popup;
	}
}
