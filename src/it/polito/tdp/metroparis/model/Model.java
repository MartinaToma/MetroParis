package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	
	
	//internamente gestisce il grafo	
	
	private Graph<Fermata,DefaultWeightedEdge> grafo;
	private List<Fermata> fermate;
	private Map<Integer,Fermata> fermateIdMap;
	Map<Fermata,Fermata> backVisit;
	
	public void creaGrafo() {
		//creo l'oggetto grafo
		//grafo orientato, pesato
		this.grafo=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungi i vertici
		MetroDAO dao=new MetroDAO();
		this.fermate=dao.getAllFermate();
		this.fermateIdMap=new HashMap<>();
		for( Fermata f: this.fermate)
			fermateIdMap.put(f.getIdFermata(), f);
		Graphs.addAllVertices(this.grafo, this.fermate);
		
		//Aggiungi gli archi
		//c'è almeno una connessione tra due nodi? 
		//se si metto l'arco
		
		//OPZIONE 1
		/*for(Fermata partenza:this.grafo.vertexSet()) {
			
			for(Fermata arrivo:this.grafo.vertexSet()) {
				
				if(dao.esisteConnessione(partenza,arrivo)) {
					this.grafo.addEdge(partenza, arrivo);
				}
			}
		}*/
		
		//Aggiungi gli archi (OPZIONE 2)
		/*for(Fermata partenza:this.grafo.vertexSet()) {
			List<Fermata> arrivi =dao.stazioniArrivo(partenza,fermateIdMap);
			
			for(Fermata arrivo:arrivi)
				this.grafo.addEdge(partenza, arrivo);
		}
		*/
		
		//Aggiungi i pesi agli archi
		List<ConnessioneVelocita> archipesati=dao.getConnessioneVelocita();		
		for(ConnessioneVelocita cp:archipesati) {
			//aggiunge il peso a un arco che ESISTE
			Fermata partenza=fermate.get(cp.getStazP());
			Fermata arrivo =fermate.get(cp.getStazA());
			double distanza=LatLngTool.distance(partenza.getCoords(), arrivo.getCoords(),LengthUnit.KILOMETER);
			double peso=distanza/cp.getVelocita()*3600;
			//tempo in ore per peso *3600 ottengo i secondi
						
			grafo.setEdgeWeight(partenza, arrivo,peso);
			
			//oppure aggiungo archi e vertici insieme
			//Graphs.addEdgeWithVertices(grafo, partenza, arrivo,peso);
		}
		//OPZIONE 3
		//Aggiungi gli archi
		
		
	}
	
	public List<Fermata> fermateRaggiungibili(Fermata source){
		List<Fermata> result=new ArrayList<Fermata>();
		
		
		backVisit=new HashMap<>();
		//devo creare un iteratore per la visita in ampiezza
		//crea un iteratore e lo associa a questo grafo, poi lo inizializza
		//se non lo inizializzo, sceglie lui il vertice da cui partire
		//lo voglio decidere io
		//GraphIterator<Fermata,DefaultEdge> it= new BreadthFirstIterator<>(this.grafo,source);
		
		// se volessi fare una visita in profondità
		GraphIterator<Fermata,DefaultWeightedEdge> it= new DepthFirstIterator<>(this.grafo,source);
		//devo agganciare il LISTENER
		//AGGIUNGO UN ASCOLTATORE ALL'ITERATORE A CUI PASSO I PARAMETRI
		
		//la classe EdgeTraversedGraphListener serve solo qua, la rendo privata
		it.addTraversalListener(new EdgeTraversedGraphListener());
		
		backVisit.put(source, null);//non ha il padre
		while(it.hasNext()) {
			result.add(it.next());
		}
		
		System.out.println(backVisit);
		//In entrambi i casi non ottengo un cammino
		//sono più percorsi posibili che partono dalla stessa sorgente
		//se voglio sapere l'albero di visita devo chiedere all'iteratore come 
		//lavora al suo interno
		return result;
	}

	public List<Fermata> percorsoFinoA(Fermata target){
		if(!backVisit.containsKey(target)) {
			return null;
		}
		
		List<Fermata> percorso =new LinkedList<>();
		Fermata f=target;
		//trovo i PADRI
		while(f!=null) {
			//lo aggiungo sempre nella prima posizione
		percorso.add(0,f);
		f=backVisit.get(f);
		}
			return percorso;	
		
	}
	
	public Graph<Fermata, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}

	public List<Fermata> trovaCamminoMinimo(Fermata partenza, Fermata arrivo) {
		DijkstraShortestPath<Fermata,DefaultWeightedEdge> dijkstra=new DijkstraShortestPath<>(this.grafo);
		GraphPath<Fermata,DefaultWeightedEdge>path= dijkstra.getPath(partenza, arrivo);
		return path.getVertexList();
	}
	
	
}
