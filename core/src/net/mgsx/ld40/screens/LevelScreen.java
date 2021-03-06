package net.mgsx.ld40.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
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
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTile.BlendMode;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.ld40.assets.LevelAssets;
import net.mgsx.ld40.assets.LevelAssets.Dir;
import net.mgsx.ld40.model.Enemy;
import net.mgsx.ld40.model.Rules;
import net.mgsx.ld40.model.Tail;
import net.mgsx.ld40.ui.MapHUD;
import net.mgsx.ld40.utils.MapIntersector;

public class LevelScreen extends ScreenAdapter
{
	private static final boolean devMode = false;
	private static final boolean debugMove = false;
	
	private static boolean debug = false;
	
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private Vector2 playerPosition;
	private Vector2 velocity = new Vector2();
	private Vector2 nextPosition = new Vector2();
	private Vector2 clipPosition = new Vector2();
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

	private Vector2 exitPosition;
	
	private Stage stage;
	private MapHUD hud;
	
	public LevelScreen() {
		
		stage = new Stage(new ScreenViewport());
		hud = new MapHUD(this, LevelAssets.i.skin);
		hud.setFillParent(true);
		stage.addActor(hud);
		Gdx.input.setInputProcessor(stage);
		
		hud.setGetReady();
		
		shapeRenderer = new ShapeRenderer();
		map = new TmxMapLoader().load(Rules.levelMap);
		
		for(TiledMapTileSet ts : map.getTileSets()){
			ts.getTile(1).getTextureRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			int imageWidth = ts.getProperties().get("imagewidth", Integer.class);
			int tileWidth = ts.getProperties().get("tilewidth", Integer.class);
			int width = imageWidth / tileWidth;
			for(TiledMapTile tile : ts){
				int tx = tile.getId() % width;
				int ty = tile.getId() / width;
				if(ty >= 12 && tx < 9)
					tile.setBlendMode(BlendMode.ALPHA);
				else
					tile.setBlendMode(BlendMode.NONE);
			}
		}
		
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
		
		playerPosition = ((RectangleMapObject) objectLayer.getObjects().get("player")).getRectangle().getCenter(new Vector2()).add(-32, -32);
		
		exitPosition = ((RectangleMapObject)objectLayer.getObjects().get("exit")).getRectangle().getCenter(new Vector2()).add(-32, -32);
		
		batch = new SpriteBatch();
		
		camera.position.x = playerPosition.x;
		camera.position.y = playerPosition.y;
		
		clipCameraToMap();
		
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
						enemy.path.add(new Vector2(verts[i], verts[i+1]).add(-32, -32));
					}
				}else if(object instanceof CircleMapObject){
					Circle c = ((CircleMapObject) object).getCircle();
					enemy.path.add(new Vector2(c.x, c.y)); // TODO
				}else if(object instanceof EllipseMapObject){
					Ellipse e = ((EllipseMapObject) object).getEllipse();
					enemy.path.add(new Vector2(e.x, e.y)); // TODO
					
				}else if(object instanceof RectangleMapObject){
					enemy.path.add(((RectangleMapObject) object).getRectangle().getCenter(new Vector2()).add(-32, -32));// TODO
				}
				enemy.time = 0;
				enemy.speed = 1; // TODO
				enemy.position.set(enemy.path.first().x, enemy.path.first().y);
				
				enemies.add(enemy);
			}
		}
		
	}
	
	private Vector2 p = new Vector2();

	private boolean heroExiting;

	private boolean isDying;

	public boolean isPaused = true;
	
	
	private void updateHeroControl(float delta){
		
		Dir newDir = null;
		if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			newDir = Dir.RIGHT;
		}else if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			newDir = Dir.LEFT;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.Z) || Gdx.input.isKeyPressed(Input.Keys.UP)){
			newDir = Dir.UP;
		}else if(Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			newDir = Dir.DOWN;
		}
		
		if(newDir != null)
		{
			nextPosition.set(playerPosition);
			getNextHeroPosition(nextPosition, newDir, getHeroSpeed(delta));
			
			if(!clipWall(nextPosition, newDir)){
				dir = newDir;
			}
		}
	}
	
	private float getHeroSpeed(float delta){
		return 2 + tails.size * .5f * delta * 60;
	}
	
	private void updateHeroMove(float delta){
		
		float speed = getHeroSpeed(delta);
		
		if(debugMove) speed = Gdx.input.isKeyPressed(Input.Keys.SPACE) ? 2 : 0;
		
		getNextHeroPosition(playerPosition, dir, speed);
	}
	
	private void getNextHeroPosition(Vector2 position, Dir direction, float speed){
		
		float dx = (direction == Dir.RIGHT ? 1 : (direction == Dir.LEFT ? -1 : 0)) * speed;
		float dy = (direction == Dir.UP ? 1 : (direction == Dir.DOWN ? -1 : 0)) * speed;

		position.add(dx, dy);
	}
	
	private boolean clipWall(Vector2 position, Dir dir){
		int ix, iy;
		
		int margin = 1;
		
		boolean col = false;
		if(dir == Dir.LEFT){
			ix = MathUtils.floor(position.x / 64);
			iy = MathUtils.floor(position.y / 64);
			if(isSolid(ix, iy) || isSolid(ix, iy+1)){
				position.x = (ix+1)  * 64 + margin;
				col = true;
			}
		}
		else if(dir == Dir.RIGHT){
			ix = MathUtils.ceil(position.x / 64);
			iy = MathUtils.floor(position.y / 64);
			if(isSolid(ix, iy) || isSolid(ix, iy+1)){
				position.x = (ix-1)  * 64 - margin;
				col = true;
			}
		}
		else if(dir == Dir.DOWN){
			ix = MathUtils.floor(position.x / 64);
			iy = MathUtils.floor(position.y / 64);
			if(isSolid(ix, iy) || isSolid(ix+1, iy)){
				position.y = (iy+1)  * 64 + margin;
				col = true;
			}
		}else{
			ix = MathUtils.floor(position.x / 64);
			iy = MathUtils.ceil(position.y / 64);
			if(isSolid(ix, iy) || isSolid(ix+1, iy)){
				position.y = (iy-1)  * 64 - margin;
				col = true;
			}
		}
		
		return col;
	}
	
	private void checkHeroWalls(){
		// check border collision
		
		int ix, iy;
		
		boolean col = clipWall(playerPosition, dir);
		
		if(col){
			ix = MathUtils.round(playerPosition.x / 64);
			iy = MathUtils.round(playerPosition.y / 64);
			if(dir == Dir.LEFT || dir == Dir.RIGHT){
				// lookups
				if(isSolid(ix, iy-1)){
					dir = Dir.UP;
				}else if(isSolid(ix, iy+1)){
					dir = Dir.DOWN;
				}else{
					dir = MathUtils.randomBoolean() ? Dir.UP : Dir.DOWN;
				}
			}else{
				// lookups
				if(isSolid(ix-1, iy))
					dir = Dir.RIGHT;
				else if(isSolid(ix+1, iy))
					dir = Dir.LEFT;
				else{
					dir = MathUtils.randomBoolean() ? Dir.RIGHT : Dir.LEFT;
				}
			}
		}
	}
	
	@Override
	public void render(float delta) {
		
		// TODO pause screen but hide all !!!
		
//		if(!isPaused && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
//			isPaused = true;
//			hud.setGetReady();
//		}else{
//			stage.act(delta);
//		}
		
		if(devMode){
			if(Gdx.input.isKeyJustPressed(Input.Keys.P)) isPaused = !isPaused;
		}
		
		stage.act(delta);
		
		if(isPaused){
			delta = 0;
		}
		
		time += delta;
		
		float playerRadius = 64 * .5f * 0.7f;
		
		if(isPaused){
			// DO nothing !
		}
		else if(isDying){
			if(tails.size == 0){
				actionTime += delta * 1.5f;
				if(actionTime > 1){
					isPaused  = true;
					hud.setGameOver();
				}
			}else{
				if(!tails.peek().isDying) tails.peek().setDying();
				if(tails.peek().animation == null){
					tails.pop();
//					if(tails.size > 0)
//						LevelAssets.i.sndHurt.play(Rules.SFX_VOLUME);
				}
			}
			
		}
		else if(heroTarget != null){
			float eatSpeed = 2;
			eatTime += delta * eatSpeed;
			
			p.set(heroTarget.sprite.getX(), heroTarget.sprite.getY());
			
			p.lerp(playerPosition, eatTime * eatTime);
			heroTarget.position.set(p);
			
			if(eatTime > 1){
				enemies.removeValue(heroTarget, true);
				// TODO inc hero stats and explosion or something ...
				
				// TODO offset to tail
				Vector2 nextPosition = playerPosition;
				if(tails.size > 0){
					nextPosition = tails.peek().position;
				}
				Tail tail = new Tail();
				tail.setCreate(nextPosition); 
				tails.add(tail);
				
				if(heroTarget.isLife){
					if(Rules.life < Rules.maxLife){
						Rules.life++;
						LevelAssets.i.sndGUI.play(Rules.SFX_VOLUME);
					}
				}
					
				heroTarget = null;
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
		else if(heroExiting){
			if(tails.size <= 0){
				isPaused = true;
				hud.setLevelComplete();
			}else{
				velocity.set(exitPosition).sub(playerPosition);
				float len = velocity.len();
				if(len < 10){ // XXX
					playerPosition.y -= playerRadius; //.set(tails.first().position);
					tails.removeIndex(0);
				}else{
					velocity.nor().scl(Math.min(4 * delta * 60, len));
					playerPosition.add(velocity);
				}
			}
		}
		else{
			int ix = MathUtils.round(playerPosition.x / 64);
			int iy = MathUtils.round(playerPosition.y / 64);
			Cell cell = groundLayer.getCell(ix, iy);
			boolean exitCell = cell != null && cell.getTile() != null && cell.getTile().getId() == 94;
			if(exitCell){ // XXX maybe use cell
				heroExiting = true;
				dir = Dir.UP; // XXX force direction to prevent a bug
				actionTime = 0;
				// playerPosition.set(exitPosition); // Quick fix
				Rules.tailsWhenExit = tails.size;
				LevelAssets.i.sndWin.play(Rules.SFX_VOLUME);
			}else{
				updateHeroControl(delta);
				updateHeroMove(delta);
				checkHeroWalls();
			}
		}
		
		
		
		// update enemies
		for(Enemy enemy : enemies){
			if(!enemy.locked && enemy.path.size > 1){
				Vector2 a = enemy.path.get(enemy.pathIndex);
				Vector2 b = enemy.path.get(enemy.pathIndex + enemy.pathDir);
				float len = a.dst(b);
				
				enemy.time += delta * 60 * enemy.speed / len; // XXX
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
					float radius = playerRadius + enemy.radius;
					if(dst2 < radius*radius){
						heroTarget = enemy;
						eatTime = 0;
						enemy.locked = true;
						LevelAssets.i.sndBlup.play(Rules.SFX_VOLUME);
					}
				}
			}else if(enemy.isHurting && hurtingEnemy == null){
				float dst2 = playerPosition.dst2(enemy.position);
				float radius = playerRadius + enemy.radius;
				if(dst2 < radius*radius)
				{
					hurtingEnemy = enemy;
					actionTime = 0;
					
					Rules.life--;
					if(Rules.life <= 0){
						isDying = true;
						LevelAssets.i.sndLoose.play(Rules.SFX_VOLUME);
						for(Tail tail : tails){
							tail.animSpeed = tails.size / 5f;
						}
						
					}else{
						LevelAssets.i.sndHurt.play(Rules.SFX_VOLUME);
					}
					
					Dir bounceDir = Dir.values()[(dir.ordinal() + 2)%4];
					
					int dx = -(dir == Dir.RIGHT ? 1 : (dir == Dir.LEFT ? -1 : 0));
					int dy = -(dir == Dir.UP ? 1 : (dir == Dir.DOWN ? -1 : 0));
					hurtStart.set(playerPosition);
					hurtEnd.set(playerPosition);
					for(int i=0 ; i<2 ; i++){
						if(clipWall(hurtEnd, bounceDir)){
							break;
						}
						hurtEnd.add(dx * 64, dy * 64);
					}
				}
			}
			enemy.update(delta);
		}
		
		// update tails
		Vector2 head = playerPosition;
		float distance = playerRadius;
		for(Tail tail : tails){
			tail.update(delta, head, distance);
			clipPosition.set(tail.position);
			clipWall(clipPosition, Dir.LEFT);
			clipWall(clipPosition, Dir.RIGHT);
			clipWall(clipPosition, Dir.DOWN);
			clipWall(clipPosition, Dir.UP);
			tail.position.lerp(clipPosition, 0.1f * delta * 60);
			
			head = tail.position;
			distance = tail.radius;
		}
		

		float progress = 2f * delta;
		if(playerPosition.dst(camera.position.x, camera.position.y) > 0){
			
			camera.position.x = MathUtils.lerp(camera.position.x, playerPosition.x, progress);
			camera.position.y = MathUtils.lerp(camera.position.y, playerPosition.y, progress);
			
			// clip to map
			clipCameraToMap();
		}
		
		camera.position.x = MathUtils.round(camera.position.x);
		camera.position.y = MathUtils.round(camera.position.y);
		camera.update();
		
		Gdx.gl.glClearColor(.5f, .5f, .5f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.setView(camera);
		renderer.render(BG_LAYERS);
		
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		
		for(Tail tail : tails){
			tail.sprite.draw(batch);
		}
		
		Sprite hero;
		if(isDying){
			hero = LevelAssets.i.heroEating.getKeyFrame(1);
			hero.setRotation(time * 180);
			hero.setScale(1.5f + .1f * MathUtils.sin(time * 5));
		}else if(heroTarget != null){
			hero = LevelAssets.i.heroEating.getKeyFrame(eatTime * 1.5f);
			hero.setScale(1.5f);
			hero.setRotation(0);
		}else{
			hero = LevelAssets.i.hero.get(dir.ordinal()).getKeyFrame(time);
			hero.setScale(1);
			hero.setRotation(0);
		}
		hero.setPosition(playerPosition.x, playerPosition.y);
		hero.draw(batch);
		
		enemies.sort();
		
		for(Enemy enemy : enemies){
			enemy.sprite.draw(batch);
		}
		
		batch.end();
		
		for(int i : FG_LAYERS){
			map.getLayers().get(i).setOpacity(1f);
		}
		
		renderer.render(FG_LAYERS);

		stage.draw();
		
		if(debug){
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.circle(playerPosition.x, playerPosition.y, playerRadius, 16);
			for(Enemy enemy : enemies){
				shapeRenderer.circle(enemy.sprite.getX(), enemy.sprite.getY(), enemy.radius, 16);
			}
			shapeRenderer.end();
		}
		
	}
	
	private void clipCameraToMap() {
		float mapWidth = this.mapWidth * 64;
		float mapHeight = this.mapHeight * 64;

		camera.position.x = MathUtils.clamp(camera.position.x, Gdx.graphics.getWidth()/2, mapWidth - Gdx.graphics.getWidth()/2);
		camera.position.y = MathUtils.clamp(camera.position.y, Gdx.graphics.getHeight()/2, mapHeight - Gdx.graphics.getHeight()/2);

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
		clipCameraToMap();
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose() {
		
		batch.dispose();
		map.dispose();
		shapeRenderer.dispose();
		stage.dispose();
		
		super.dispose();
	}
}
