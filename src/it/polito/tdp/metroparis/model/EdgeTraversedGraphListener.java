package it.polito.tdp.metroparis.model;

//import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
//import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

public class EdgeTraversedGraphListener implements TraversalListener<Fermata, DefaultWeightedEdge> {

	Graph<Fermata,DefaultWeightedEdge> grafo;
	
	//è una mappa che punta dal basso verso l'alto.
	//dal nodo figlio al nodo padre
	
	Map<Fermata,Fermata> back;
	
	public EdgeTraversedGraphListener(Graph<Fermata,DefaultWeightedEdge> grafo,Map<Fermata, Fermata> back) {
		this.grafo=grafo;
		this.back = back;
	}

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
		
		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
		
		
	}

	@Override
	//ev è un evento
	public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> ev) {
		//dato un arco devo estrarre i 2 vertici
		//di cui lo dovrei conoscere e l'altro no
		
		//l'arco fa parte dell'albero di visita se soddisfa delle condizioni
		
		/*
		 * back codifica relazioni del tipo child->parent
		 * 
		 * per un nuovo vertice 'child'scoperto
		 * devo avere che:
		 * -child è ancora sconosciuto (non ancora trovato)
		 * -parent è già stato visitato
		 */
		
		Fermata sourceVertex= grafo.getEdgeSource(ev.getEdge());
		
		Fermata targetVertex =grafo.getEdgeTarget(ev.getEdge());
		
		
		/*se il grafo è orientato, allora source==parent;target==child
		 * se il grafo non è orientato, potrebbe essere al contrario...		
		 */
		//devo essere sicura che nella mappa non sia presente l'id del figlio
		
		if(!back.containsKey(targetVertex)&&back.containsKey(sourceVertex)) {
			back.put(targetVertex, sourceVertex);
		}
		//per i grafi non orientati
		else if(!back.containsKey(sourceVertex)&&back.containsKey(targetVertex)) {
			back.put(sourceVertex,targetVertex);
		}
		//back.put(ev.getEdge().destinationVertex(), ev.getEdge().sourceVertex());
		
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
		
		
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
		
		
	}

}
