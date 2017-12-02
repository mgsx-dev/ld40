package net.mgsx.ld40.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class LevelAssets {

	public static LevelAssets i;
	
	public static enum Dir{
		DOWN, RIGHT, UP, LEFT
	}
	
	public Array<Animation<Sprite>> hero;
	
	public Animation<Sprite> tailIdle, tailGrow, tailExplode, tailBump;
	
	private ObjectMap<String, TextureRegion> regions = new ObjectMap<String, TextureRegion>();

	private TextureAtlas enemiesAtlas;
	
	public Skin skin;
	
	public LevelAssets() 
	{
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
		
		TextureAtlas heroAtlas = new TextureAtlas(Gdx.files.internal("hero.atlas"));
		Array<Sprite> heroSprites = heroAtlas.createSprites("hero");
		
		hero = createAnimation(heroSprites); 
		
		Array<Sprite> tailSprites = heroAtlas.createSprites("tail");
		
		tailIdle = createAnimation(tailSprites, .5f, PlayMode.LOOP_PINGPONG, 1, 2);
		tailGrow = createAnimation(tailSprites, .2f, PlayMode.REVERSED, 5, 9);
		tailExplode = createAnimation(tailSprites, .2f, PlayMode.NORMAL, 10, 14);
		tailBump = createAnimation(tailSprites, .2f, PlayMode.LOOP_PINGPONG, 1, 4);
		
		enemiesAtlas = new TextureAtlas(Gdx.files.internal("enemies.atlas"));
	}
	
	private Animation<Sprite> createAnimation(Array<Sprite> sprites, float duration, PlayMode playMode, int start, int end) {
		Array<Sprite> frames = new Array<Sprite>();
		frames.addAll(sprites, start-1, end-start+1);
		return new Animation<Sprite>(duration, frames, playMode);
	}

	private Array<Animation<Sprite>> createAnimation(Array<Sprite> sprites){
		Array<Animation<Sprite>> anims = new Array<Animation<Sprite>>();
		for(Dir dir : Dir.values()){
			anims.add(createAnimation(sprites, dir));
		}
		return anims;
	}
	private Animation<Sprite> createAnimation(Array<Sprite> sprites, Dir dir){
		Array<Sprite> frames = new Array<Sprite>();
		int count = sprites.size / 4;
		frames.addAll(sprites, dir.ordinal() * count, count);
		return new Animation<Sprite>(.1f, frames, PlayMode.LOOP); // TODO pingpong
	}

	public Sprite getEnemySprite(String type) {
		return enemiesAtlas.createSprite(type);
	}
	public Animation<Sprite> getEnemyAnimation(String type) {
		return new Animation<Sprite>(.2f, enemiesAtlas.createSprites(type), PlayMode.LOOP_PINGPONG);
	}

}
