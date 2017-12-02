package net.mgsx.ld40.utils;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MapPathfinder extends PathFinder<MapNode>
{
	private Object [] grid;
	private TiledMapTileLayer layer;
	private float SQRT2 = (float)Math.sqrt(2.0);
	private boolean wrapX, wrapY;
	private Array<MapNode> dsts = new Array<MapNode>();
	
	public MapPathfinder(TiledMapTileLayer layer, boolean allowDiagonal, boolean wrapX, boolean wrapY) 
	{
		this.layer = layer;
		this.wrapX = wrapX;
		this.wrapY = wrapY;
		
		grid = new Object[layer.getHeight() * layer.getWidth()];
		
		for(int y=0 ; y<layer.getHeight() ; y++){
			for(int x=0 ; x<layer.getWidth() ; x++){
				Cell cell = layer.getCell(x, y);
				if(cell != null){
					MapNode mapNode = new MapNode();
					mapNode.cell = cell;
					mapNode.x = x;
					mapNode.y = y;
					GraphNode node = new GraphNode();
					node.object = mapNode;
					grid[y * layer.getWidth() + x] = node;
					nodes.add(node);
					map.put(mapNode, node);
				}
			}
		}
		for(int y=0 ; y<layer.getHeight() ; y++){
			for(int x=0 ; x<layer.getWidth() ; x++){
				GraphNode node = (GraphNode)grid[y * layer.getWidth() + x];
				if(node != null){
					addEdge(node, x-1, y, 1);
					addEdge(node, x+1, y, 1);
					addEdge(node, x, y-1, 1);
					addEdge(node, x, y+1, 1);
					if(allowDiagonal){
						addEdge(node, x-1, y-1, SQRT2);
						addEdge(node, x+1, y-1, SQRT2);
						addEdge(node, x-1, y+1, SQRT2);
						addEdge(node, x+1, y+1, SQRT2);

					}
				}
			}
		}
	}

	private void addEdge(GraphNode node, int x, int y, float distance) {
		if(wrapX){
			x = x % layer.getWidth();
		}
		if(wrapY){
			y = y % layer.getHeight();
		}
		if(x>=0 && x<layer.getWidth() && y>=0 && y<layer.getHeight()){
			GraphNode adj = (GraphNode)grid[y * layer.getWidth() + x];
			if(adj != null && isEdge(node.object, adj.object)){
				GraphEdge edge = new GraphEdge();
				edge.target = adj;
				edge.distance = distance;
				node.edges.add(edge);
			}
		}
	}

	protected boolean isEdge(MapNode src, MapNode dst) {
		return true;
	}

	public void find(Array<MapNode> path, Vector2 src, Vector2 dst) {
		path.clear();
		
		int ix = MathUtils.floor(src.x / layer.getTileWidth());
		int iy = MathUtils.floor(src.y / layer.getTileHeight());
		if(ix<0 || ix>=layer.getWidth() || iy<0 || iy>=layer.getHeight()) return;
		GraphNode srcNode = (GraphNode)grid[iy * layer.getWidth() + ix];
		if(srcNode == null) return;
		
		ix = MathUtils.floor(dst.x / layer.getTileWidth());
		iy = MathUtils.floor(dst.y / layer.getTileHeight());
		if(ix<0 || ix>=layer.getWidth() || iy<0 || iy>=layer.getHeight()) return;
		GraphNode dstNode = (GraphNode)grid[iy * layer.getWidth() + ix];
		if(dstNode == null) return;
		
		dsts.clear();
		dsts.add(dstNode.object);
		super.find(path, srcNode.object, dsts);
	}
	
	@Override
	protected float heuristic(GraphNode src, GraphNode dst) {
		float dx = src.object.x - dst.object.x;
		float dy = src.object.y - dst.object.y;
		return (float)Math.sqrt(dx*dx+dy*dy);
	}
	
}
