package net.mgsx.ld40.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.ld40.assets.LevelAssets;

public class Tail {
	
	private static Vector2 v = new Vector2();
	
	public Sprite sprite = new Sprite();
	public float time;
	public Vector2 position = new Vector2();
	public Animation<Sprite> animation, nextAnimation;
	public float radius = 16;
	public boolean isDying;

	public float animSpeed = 1;
	
	public void setCreate(Vector2 head){
		animation = LevelAssets.i.tailGrow;
		nextAnimation = LevelAssets.i.tailIdle;
		position.set(head);
		time = 0;
	}
	
	public void update(float deltaTime, Vector2 head, float distance){
		time += deltaTime * animSpeed;
		
		float dst = position.dst(head);
		float maxDst = distance + radius;
		if(dst > maxDst){
			v.set(head).sub(position);
			if(v.len2() > 1){
				v.nor().scl(dst - maxDst);
				position.add(v);
			}
		}
		
		if(animation.isAnimationFinished(time)){
			animation = nextAnimation;
			time = 0;
		}
		if(animation != null){
			sprite.set(animation.getKeyFrame(time));
		}
		sprite.setPosition(position.x, position.y);
	}

	public void setDying() {
		isDying = true;
		animation = LevelAssets.i.tailExplode;
		time = 0;
		nextAnimation = null;
	}
	
}
