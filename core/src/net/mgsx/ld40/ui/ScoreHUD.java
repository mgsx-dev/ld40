package net.mgsx.ld40.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.model.Rules;

public class ScoreHUD extends Table
{
	public ScoreHUD(Skin skin) 
	{
		super(skin);
		setBackground("panel");
		defaults().expandX().center().pad(12);
		
		add(Rules.levelTitle + " Completed !").pad(4).row();
		
		add(Rules.levelSubTitle).pad(4).row();
		add("Life Bonus").pad(4).row();

		Table tailsTable = new Table(getSkin());
		add(tailsTable).row();
		
		SequenceAction seq = new SequenceAction();
		
		for(int i=0 ; i<Rules.tailsWhenExit ; i++){
			
			Image image = new Image(LevelAssets.i.tailIdle.getKeyFrame(0));
			image.setOrigin(Align.center);
			image.setScale(0);
			image.setScaling(Scaling.none);
			tailsTable.add(image);
			Action a = Actions.sequence(
					Actions.run(new Runnable() {
						@Override
						public void run() {
							LevelAssets.i.sndBlup.play(Rules.SFX_VOLUME);
						}
					}),
					Actions.scaleTo(2, 2, 0.1f),
					Actions.scaleTo(1, 1, 0.1f)
					);
			a.setActor(image);
			seq.addAction(a);
			
			if((i+1)%8 == 0) tailsTable.row();
			
		}
		
		seq.addAction(Actions.delay(1));
		
		Table heartsTable = new Table(getSkin());
		
		final int lifeBonus = MathUtils.floor(Rules.tailsWhenExit / 3);
		add(heartsTable).row();
		
		for(int i=0 ; i<lifeBonus ; i++){
			
			Image image = new Image(getSkin().getDrawable("heart"));
			image.setScale(0);
			image.setOrigin(Align.center);
			heartsTable.add(image);
			image.setScaling(Scaling.none);
			Action a = Actions.sequence(
					Actions.run(new Runnable() {
						@Override
						public void run() {
							LevelAssets.i.sndGUI.play(Rules.SFX_VOLUME);
						}
					}),
					Actions.scaleTo(2, 2, 0.1f),
					Actions.scaleTo(.9f, .9f, 0.1f)
					);
			a.setActor(image);
			seq.addAction(a);
		}
		
		seq.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				Rules.life = Math.min(Rules.maxLife, Rules.life + lifeBonus);
			}
		}));
		
		addAction(seq);
		
		TextButton bt;
		add(bt = new TextButton("Next", getSkin())).row();
//		bt.addListener(new ChangeListener() {
//			@Override
//			public void changed(ChangeEvent event, Actor actor) {
//				((Game)Gdx.app.getApplicationListener()).setScreen(new LevelScreen());
//			}
//		});
	}
}
