package net.mgsx.ld40.utils;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MapIntersector {

	public float offset = 0;
	
	private TiledMapTileLayer layer;
	
	private Vector2 start = new Vector2();
	private Vector2 end = new Vector2();
	//private Vector2 point = new Vector2();
	private Vector2 displacement = new Vector2();
	private float tileWidth, tileHeight;
	
	public MapIntersector(TiledMapTileLayer layer) {
		super();
		this.layer = layer;
		tileWidth = layer.getTileWidth();
		tileHeight = layer.getTileHeight();
	}

	public boolean intersectCircle(Vector2 center, float radius){
		
		int ix1 = MathUtils.floor((center.x - radius - offset) / tileWidth);
		int ix2 = MathUtils.ceil((center.x + radius + offset) / tileWidth);
		
		int iy1 = MathUtils.floor((center.y - radius - offset) / tileHeight);
		int iy2 = MathUtils.ceil((center.y + radius + offset) / tileHeight);
		
		boolean colliding = false;
		for(int ix = ix1 ; ix <= ix2 ; ix++){
			for(int iy = iy1 ; iy <= iy2 ; iy++){
				Cell cell = layer.getCell(ix, iy);
				if(isSolid(cell)){
					colliding |= intersectSegment(center, radius, ix, iy, ix+1, iy);
					colliding |= intersectSegment(center, radius, ix, iy+1, ix+1, iy+1);
					colliding |= intersectSegment(center, radius, ix, iy, ix, iy+1);
					colliding |= intersectSegment(center, radius, ix+1, iy, ix+1, iy+1);
					
					colliding |= intersectPoint(center, radius, ix, iy);
					colliding |= intersectPoint(center, radius, ix+1, iy);
					colliding |= intersectPoint(center, radius, ix, iy+1);
					colliding |= intersectPoint(center, radius, ix+1, iy+1);
				}
			}
			
		}
		
		return colliding;
	}
	
	private boolean intersectSegment(Vector2 center, float radius, int ix1, int iy1, int ix2, int iy2){
		start.set(ix1 * tileWidth, iy1 * tileHeight);
		end.set(ix2 * tileWidth, iy2 * tileHeight);
		float len = Intersector.intersectSegmentCircleDisplace(start, end, center, radius + offset, displacement);
		if(len < Float.POSITIVE_INFINITY){
			center.mulAdd(displacement, radius + offset - len);
			return true;
		}
		return false;
	}
	
	private boolean intersectPoint(Vector2 center, float radius, int ix, int iy){
		float x = ix * tileWidth;
		float y = iy * tileHeight;
		displacement.set(x, y).sub(center);
		float len = displacement.len();
		if(len < radius+offset){
			center.mulAdd(displacement, -(radius + offset - len) / len);
			return true;
		}
		return false;
	}
	
	public boolean isSolid(Cell cell){
		return cell == null || isSolid(cell.getTile());
	}
	public boolean isSolid(TiledMapTile tile){
		return tile != null;
	}

	public Cell cellAt(Vector2 p) {
		int ix = MathUtils.floor(p.x / 64);
		int iy = MathUtils.floor(p.y / 64);
		return layer.getCell(ix, iy);
	}
	
}
