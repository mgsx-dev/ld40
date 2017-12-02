package net.mgsx.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.assets.LevelAssets.Dir;
import net.mgsx.ld40.model.Enemy;
import net.mgsx.ld40.model.Tail;
import net.mgsx.ld40.utils.MapIntersector;

public class LevelScreen extends ScreenAdapter
{
	private static boolean debug = false;
	
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
	
	private ShapeRenderer shapeRenderer;
	
	private int mapWidth, mapHeight;
	
	private TiledMapTileLayer groundLayer;
	
	private static final int [] FG_LAYERS = {2};
	private static final int [] BG_LAYERS = {0,1};
	
	private Array<Enemy> enemies = new Array<Enemy>();
	
	private Array<Tail> tails = new Array<Tail>();
	
	private Vector2 hurtEnd = new Vector2();
	private Vector2 hurtStart = new Vector2();
	private Enemy hurtingEnemy = null;
	private float actionTime;
	
	private Enemy heroTarget = null;
	private float eatTime;
	
	public LevelScreen() {
		shapeRenderer = new ShapeRenderer();
		map = new TmxMapLoader().load("level1.tmx");
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
		
		for(MapObject object : map.getLayers().get("objects").getObjects()){
			String type = object.getProperties().get("type", null, String.class);
			if(type != null){
				
				Enemy enemy = new Enemy(type);
				
				enemy.animation = LevelAssets.i.getEnemyAnimation(type);
				enemy.sprite = new Sprite(enemy.animation.getKeyFrame(0));
				
				if(object instanceof PolylineMapObject){
					Polyline polyline = ((PolylineMapObject) object).getPolyline();
					float [] verts = polyline.getTransformedVertices();
					for(int i=0 ; i<verts.length ; i+=2){
						enemy.path.add(new Vector2(verts[i], verts[i+1]));
					}
				}else if(object instanceof CircleMapObject){
					Circle c = ((CircleMapObject) object).getCircle();
					enemy.path.add(new Vector2(c.x, c.y)); // TODO
				}else if(object instanceof EllipseMapObject){
					Ellipse e = ((EllipseMapObject) object).getEllipse();
					enemy.path.add(new Vector2(e.x, e.y)); // TODO
					
				}else if(object instanceof RectangleMapObject){
					enemy.path.add(((RectangleMapObject) object).getRectangle().getCenter(new Vector2()));// TODO
				}
				enemy.time = 0;
				enemy.speed = 1; // TODO
				enemy.position.set(enemy.path.first().x, enemy.path.first().y);
				
				enemies.add(enemy);
			}
		}
		
	}
	
	private Vector2 p = new Vector2();
	
	
	private void updateHeroMove(){
		float speed = 2 + tails.size / 2;
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
		
	}
	private void checkHeroWalls(){
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
		if(!isSolid(nx, ny)){
			collide = false;
		}
		
		if(collide){
			dir = Dir.values()[(dir.ordinal()+1)%4];
			playerPosition.x = ix * 64;
			playerPosition.y = iy * 64;
		}
	}
	
	@Override
	public void render(float delta) {
		
		time += delta;
		
		float playerRadius = 64 * .5f * 0.7f;
		float enemyRadius = 64 * .5f;
		
		if(heroTarget != null){
			float eatSpeed = 2;
			eatTime += delta * eatSpeed;
			
			p.set(heroTarget.sprite.getX(), heroTarget.sprite.getY());
			
			p.lerp(playerPosition, eatTime * eatTime);
			heroTarget.position.set(p);
			
			if(eatTime > 1){
				enemies.removeValue(heroTarget, true);
				// TODO inc hero stats and explosion or something ...
				heroTarget = null;
				
				// TODO offset to tail
				Vector2 nextPosition = playerPosition;
				if(tails.size > 0){
					nextPosition = tails.peek().position;
				}
				Tail tail = new Tail();
				tail.setCreate(nextPosition); 
				tails.add(tail);
				
			}
			
		}
		else if(hurtingEnemy != null){
			actionTime += delta * 10;
			if(actionTime > 1){
				playerPosition.set(hurtEnd);
				hurtingEnemy = null;
			}else{
				
				float it = 1 - actionTime;
				playerPosition.set(hurtStart).lerp(hurtEnd, 1 - it*it);
			}
			
		}
		else{
			updateHeroMove();
			checkHeroWalls();
		}
		
		
		
		// update enemies
		for(Enemy enemy : enemies){
			if(!enemy.locked && enemy.path.size > 1){
				Vector2 a = enemy.path.get(enemy.pathIndex);
				Vector2 b = enemy.path.get(enemy.pathIndex + enemy.pathDir);
				float len = a.dst(b);
				
				enemy.time += 1 * enemy.speed / len; // XXX
				if(enemy.time > 1){
					enemy.time = 0;
					enemy.pathIndex += enemy.pathDir;
					int nextPathIndex = enemy.pathIndex + enemy.pathDir;
					if(nextPathIndex >= enemy.path.size || nextPathIndex < 0){
						enemy.pathDir = -enemy.pathDir;
					}
				}
				a = enemy.path.get(enemy.pathIndex);
				b = enemy.path.get(enemy.pathIndex + enemy.pathDir);
				p.set(a).lerp(b, enemy.time);
				enemy.position.set(p);
			}
			
			if(enemy.isEatable){
				if(heroTarget == null){
					float dst2 = playerPosition.dst2(enemy.position);
					float radius = playerRadius + enemyRadius; // TODO extra range ?
					if(dst2 < radius*radius){
						heroTarget = enemy;
						eatTime = 0;
						enemy.locked = true;
					}
				}
			}else if(enemy.isHurting && hurtingEnemy == null){
				float dst2 = playerPosition.dst2(enemy.position);
				float radius = playerRadius + enemyRadius;
				if(dst2 < radius*radius)
				{
					hurtingEnemy = enemy;
					actionTime = 0;
					
					// TODO ray cast collider !
					int dx = -(dir == Dir.RIGHT ? 1 : (dir == Dir.LEFT ? -1 : 0));
					int dy = -(dir == Dir.UP ? 1 : (dir == Dir.DOWN ? -1 : 0));
					int ix =  MathUtils.floor(playerPosition.x / 64);
					int iy =  MathUtils.floor(playerPosition.y / 64);
					hurtStart.set(playerPosition);
					hurtEnd.set(playerPosition);
					for(int i=0 ; i<2 ; i++){
						if(isSolid(ix+dx, iy+dy)){
							break;
						}
						ix += dx;
						iy += dy;
						hurtEnd.add(dx * 64, dy * 64);
					}
				}
				// TODO check with tails as well !
			}
			enemy.update(delta);
		}
		
		// update tails
		Vector2 head = playerPosition;
		float distance = playerRadius;
		for(Tail tail : tails){
			tail.update(delta, head, distance);
			head = tail.position;
			distance = tail.radius;
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
		
		for(Enemy enemy : enemies){
			enemy.sprite.draw(batch);
		}
		
		for(Tail tail : tails){
			tail.sprite.draw(batch);
		}
		
		Sprite hero = LevelAssets.i.hero.get(dir.ordinal()).getKeyFrame(time);
		hero.setPosition(playerPosition.x, playerPosition.y);
		hero.draw(batch);
		
		
		batch.end();
		
		for(int i : FG_LAYERS){
			map.getLayers().get(i).setOpacity(1f);
		}
		
		renderer.render(FG_LAYERS);

		if(debug){
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.circle(playerPosition.x, playerPosition.y, playerRadius, 16);
			for(Enemy enemy : enemies){
				shapeRenderer.circle(enemy.sprite.getX(), enemy.sprite.getY(), enemyRadius, 16);
			}
			shapeRenderer.end();
		}
		
	}
	
	/**
	 * @param nx
	 * @param ny
	 * @return true if can't go here
	 */
	private boolean isSolid(int nx, int ny) {
		if(nx>=0 && nx<mapWidth && ny>=0 && ny<mapHeight){
			Cell cell = groundLayer.getCell(nx, ny);
			if(cell != null){
				if(!cell.getTile().getProperties().get("solid", false, Boolean.class)){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		camera.position.x = playerPosition.x;
		camera.position.y = playerPosition.y;

	}
}
