package it.polito.tdp.nobel.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.nobel.db.EsameDAO;

public class Model {
	private List<Esame> partenza; //tutti gli esami anche duplicati
	private Set<Esame> soluzioneMigliore; //solo alcuni e non ripetuti (ordine non conta)
											// migliore tra le parziali
	private double mediaSoluzioneMigliore;
	
	public Model() {
		EsameDAO dao = new EsameDAO();
		this.partenza = dao.getTuttiEsami();
	}
	
	public Set<Esame> calcolaSottoinsiemeEsami(int numeroCrediti) {
		Set<Esame> parziale = new HashSet<Esame>();
		soluzioneMigliore = new HashSet<Esame>();
		mediaSoluzioneMigliore = 0;
		
		//cerca1(parziale, 0, numeroCrediti);
		cerca2(parziale, 0, numeroCrediti);
		return soluzioneMigliore;
	}

	/*COMPLESSITA': 2^N*/
	private void cerca2(Set<Esame> parziale, int L, int m) {
		
		//**********casi terminali *********************
		
		// 1 : m crediti sforati
		int crediti = sommaCrediti(parziale);
		if(crediti > m) {
			return;
		}
			
		// 2 : sono precisamente a "m" e la media è > della migliore fino ad ora
		if(crediti == m) {
			double media = calcolaMedia(parziale);
			if(media > mediaSoluzioneMigliore) {
				soluzioneMigliore = new HashSet<>(parziale);
				mediaSoluzioneMigliore = media;
			}
			return; 
		}
				
		// 3
		//sicuramente, crediti < m
		// L = N -> non ci sono più esami da aggiungere
		if(L == partenza.size()) {
			return;
		}
		
		//********** generazione sottoproblemi*****************
		// FA LA DIFFERENZA IN COMPLESSITA' RISPETTO A PRIMA 
		//partenza[L] è da aggiungere oppure no? Provo entrambe le cose
		//
		parziale.add(partenza.get(L));
		cerca2(parziale, L+1, m);
		
		parziale.remove(partenza.get(L));
		cerca2(parziale, L+1,m);
	}

 

	/*COMPLESSITA': N!*/
	// quando aumento N non riesce a runnare neanche con 50
	// nel caso di prima di metto 39 ms
	private void cerca1(Set<Esame> parziale, int L, int m) {
		//casi terminali
		int crediti = sommaCrediti(parziale);
		if(crediti > m) {
			return;
		}
		
		if(crediti == m) {
			double media = calcolaMedia(parziale);
			if(media > mediaSoluzioneMigliore) {
				soluzioneMigliore = new HashSet<>(parziale);
				mediaSoluzioneMigliore = media;
			}
			return; 
		}
		
		//sicuramente, crediti < m
		// L = N -> non ci sono più esami da aggiungere
		if(L == partenza.size()) {
			return;
		}
		
		//generare i sotto-problemi
		/*for(Esame e : partenza) {
			if(!parziale.contains(e)) {
				//aggiungo
				parziale.add(e);
				//ricorsione
				cerca1(parziale, L+1, m);
				//backtracking
				parziale.remove(e);
			}
		}*/
		
		//N.B. : IMPLEMENTAZIONE: indice del for deve essere > del livello di ricorsione
		// non ha senso controllare indici precedenti al valore del livello, perché è già pieno
		for(int i=0;i<partenza.size();i++) {
			if(!parziale.contains(partenza.get(i))&& i>=L) {
				parziale.add(partenza.get(i));
				cerca1(parziale,L+1,m);
				parziale.remove(partenza.get(i));
			}
		}
		
		/*
		 * partenza = {e1,e2,e3}
		 * 
		 * LIV=0
		 * parziale={e1}
		 * 		L=1
		 * 		parziale={e1,e2}
		 * parziale={e2}
		 * 		L=1
		 * 		parziale={e2,e1} --> non considero più questa soluzione
		 * 							perché e1 viene prima di e1 ha indice 0
		 * 							e il liv è 1, quindi questo mi permette
		 * 							di evitare il duplicato della riga 124
		 * 							dove i valori erano solo invertiti
		 * parziale ={e3}
		 */
		
	}
	

	public double calcolaMedia(Set<Esame> esami) {
		
		int crediti = 0;
		int somma = 0;
		
		for(Esame e : esami){
			crediti += e.getCrediti();
			somma += (e.getVoto() * e.getCrediti());
		}
		
		return somma/crediti;
	}
	
	public int sommaCrediti(Set<Esame> esami) {
		int somma = 0;
		
		for(Esame e : esami)
			somma += e.getCrediti();
		
		return somma;
	}

}
