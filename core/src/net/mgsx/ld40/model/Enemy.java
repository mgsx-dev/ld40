package net.mgsx.ld40.model;

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
}
