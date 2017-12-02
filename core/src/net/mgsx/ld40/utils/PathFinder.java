package net.mgsx.ld40.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.BinaryHeap.Node;
import com.badlogic.gdx.utils.ObjectMap;

public class PathFinder<T> {

	protected int visitIndex;
	
	protected class GraphNode extends Node {
		protected T object;
		protected float distance, h;
		protected boolean visited;
		protected boolean head;
		protected GraphNode parent;
		protected Array<GraphEdge> edges = new Array<GraphEdge>();
		public GraphNode() {
			super(0);
		}
	}
	
	protected class GraphEdge {
		protected GraphNode target;
		protected float distance;
	}
	protected Array<GraphNode> nodes = new Array<GraphNode>();
	
	protected ObjectMap<T, GraphNode> map = new ObjectMap<T, GraphNode>();
	protected BinaryHeap<GraphNode> heads = new BinaryHeap<GraphNode>();
	
	public void find(Array<T> path, T src, Array<T> dsts)
	{
		GraphNode srcNode = map.get(src);
		
		for(GraphNode node : nodes){
			node.visited = false;
			node.parent = null;
			node.head = false;
		}
		
		
		heads.clear();
		for(T dst : dsts){
			GraphNode node = map.get(dst);
			node.distance = 0;
			node.h = heuristic(srcNode, node);
			node.head = true;
			node.visited = true;
			heads.add(node, node.h);
		}
		
		while(heads.size > 0 && heads.peek() != srcNode){
			GraphNode head = heads.pop();
			head.head = false;
			for(GraphEdge edge : head.edges){
				GraphNode adj = edge.target;
				if(adj.head && head.distance + edge.distance < adj.distance){
					adj.parent = head;
					adj.distance = head.distance + edge.distance;
					heads.setValue(adj, adj.distance + adj.h);
				}else if(!adj.visited){
					adj.parent = head;
					adj.distance = head.distance + edge.distance;
					adj.h = heuristic(srcNode, adj);
					adj.head = true;
					adj.visited = true;
					heads.add(adj, adj.distance + adj.h);
				}
			}
		}
		
		path.clear();
		while(srcNode != null){
			path.add(srcNode.object);
			srcNode = srcNode.parent;
		}
		
		return;
	}

	protected float heuristic(GraphNode src, GraphNode dst) {
		return 0;
	}
	
}
