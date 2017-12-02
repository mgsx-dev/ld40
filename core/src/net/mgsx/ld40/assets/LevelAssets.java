package net.mgsx.ld40.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class LevelAssets {

	public static LevelAssets i;
	
	public static enum Dir{
		DOWN, RIGHT, UP, LEFT
	}
	
	public Array<Animation<Sprite>> hero;
	
	public LevelAssets() 
	{
		TextureAtlas heroAtlas = new TextureAtlas(Gdx.files.internal("hero.atlas"));
		Array<Sprite> heroSprites = heroAtlas.createSprites("hero");
		
		
		hero = createAnimation(heroSprites); 
		
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
	
}
