package net.mgsx.ld40.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {

	public Array<Vector2> path = new Array<Vector2>();
	public int pathIndex = 0, pathDir = 1;
	
	public Sprite sprite;
	
	public float time;
	public float speed;
	public boolean locked;
	public Animation<Sprite> animation;
	public float animTime = 0;
	public boolean isEatable;
	public boolean isHurting;
	public Vector2 position = new Vector2();
	public float radius;
	public boolean isLife;
	
	public Enemy(String type) {
		if(type.equals("mushroom")){
			isEatable = true;
			radius = 32;
		}
		else if(type.equals("balls")){
			isHurting = true;
			radius = 16;
		}
		else if(type.equals("life")){
			isEatable = true;
			isLife = true;
			radius = 32;
		}
	}
	
	public void update(float deltaTime){
		animTime += deltaTime;
		sprite.set(animation.getKeyFrame(animTime));
		sprite.setPosition(position.x, position.y);
	}
}
