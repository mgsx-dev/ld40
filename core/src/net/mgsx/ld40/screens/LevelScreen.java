package net.mgsx.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.assets.LevelAssets.Dir;
import net.mgsx.ld40.utils.MapIntersector;

public class LevelScreen extends ScreenAdapter
{
	private TiledMap map;
	private TiledMapRenderer renderer;
	private OrthographicCamera camera;
	private Vector2 playerPosition;
	private Vector2 playerVelocity = new Vector2();
	private TiledMapTile playerTile;
	private Batch batch;
	private MapIntersector mapIntersector;
	private float time;
	private Dir dir = Dir.DOWN;
	
	private int mapWidth, mapHeight;
	
	private TiledMapTileLayer groundLayer;
	
	private static final int [] FG_LAYERS = {2};
	private static final int [] BG_LAYERS = {0,1};
	
	public LevelScreen() {
		map = new TmxMapLoader().load("level0.tmx");
		mapWidth = map.getProperties().get("width", Integer.class);
		mapHeight = map.getProperties().get("height", Integer.class);
		groundLayer = (TiledMapTileLayer) map.getLayers().get(0);
		mapIntersector = new MapIntersector((TiledMapTileLayer) map.getLayers().get(0)){
			@Override
			public boolean isSolid(TiledMapTile tile) {
				return tile.getProperties().get("collider", false, Boolean.class);
			}
		};
		mapIntersector.offset = 4;
		
		renderer = new OrthogonalTiledMapRenderer(map, 1f);
		
		camera = new OrthographicCamera();
		
		MapLayer objectLayer = map.getLayers().get(1);
		MapObject playerObject = objectLayer.getObjects().get("player");
		if(playerObject instanceof RectangleMapObject){
			playerPosition = ((RectangleMapObject) playerObject).getRectangle().getCenter(new Vector2());
		}
		
		for(TiledMapTile tile : map.getTileSets().getTileSet(0)){
			if("player".equals(tile.getProperties().get("name"))){
				playerTile = tile;
				break;
			}
		}
		
		batch = new SpriteBatch();
		
		camera.position.x = playerPosition.x;
		camera.position.y = playerPosition.y;
		
	}
	
	@Override
	public void render(float delta) {
		
		time += delta;
		
		float playerRadius = .25f * 64;
		
		float speed = 2;
		
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			dir = Dir.RIGHT;
		}else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			dir = Dir.LEFT;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			dir = Dir.UP;
		}else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			dir = Dir.DOWN;
		}
		
		playerVelocity.x = (dir == Dir.RIGHT ? 1 : (dir == Dir.LEFT ? -1 : 0)) * speed;
		playerVelocity.y = (dir == Dir.UP ? 1 : (dir == Dir.DOWN ? -1 : 0)) * speed;
		
		playerPosition.add(playerVelocity);
		
		
		// check border collision (TODO fix the rounding depends on direction...)
		int ix =  MathUtils.round(playerPosition.x / 64);
		int iy =  MathUtils.round(playerPosition.y / 64);
		int nx = ix;
		int ny = iy;
		if(dir == Dir.RIGHT){
			nx++;
		}else if(dir == Dir.LEFT){
			nx--;
		}else if(dir == Dir.UP){
			ny++;
		}else if(dir == Dir.DOWN){
			ny--;
		}
		
		boolean collide = true;
		if(nx>=0 && nx<mapWidth && ny>=0 && ny<mapHeight){
			Cell cell = groundLayer.getCell(nx, ny);
			if(cell != null){
				if(!cell.getTile().getProperties().get("solid", false, Boolean.class)){
					collide = false;
				}
			}
		}
		
		if(collide){
			dir = Dir.values()[(dir.ordinal()+1)%4];
			playerPosition.x = ix * 64;
			playerPosition.y = iy * 64;
		}
		

		float progress = 1f * delta;
		if(playerPosition.dst(camera.position.x, camera.position.y) > 0){
			
			camera.position.x = MathUtils.lerp(camera.position.x, playerPosition.x, progress);
			camera.position.y = MathUtils.lerp(camera.position.y, playerPosition.y, progress);
			
			// clip to map
			
			float mapWidth = map.getProperties().get("width", Integer.class) * 64;
			float mapHeight = map.getProperties().get("height", Integer.class) * 64;
			
			camera.position.x = MathUtils.clamp(camera.position.x, Gdx.graphics.getWidth()/2, mapWidth - Gdx.graphics.getWidth()/2);
			camera.position.y = MathUtils.clamp(camera.position.y, Gdx.graphics.getHeight()/2, mapHeight - Gdx.graphics.getHeight()/2);
		}
		
		camera.update();
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.setView(camera);
		renderer.render(BG_LAYERS);
		
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		Sprite hero = LevelAssets.i.hero.get(dir.ordinal()).getKeyFrame(time);
		hero.setPosition(playerPosition.x, playerPosition.y);
		hero.draw(batch);
		batch.end();
		
		for(int i : FG_LAYERS){
			map.getLayers().get(i).setOpacity(1f);
		}
		
		renderer.render(FG_LAYERS);

	}
	
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		camera.position.x = playerPosition.x;
		camera.position.y = playerPosition.y;

	}
}
