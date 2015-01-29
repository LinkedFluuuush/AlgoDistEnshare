/*
 * Copyright 2014 Gwénolé Lecorvé.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package enshare.client;

import enshare.server.ServerInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

/**
 * Classe qui définit un contrôleur centralisé
 *
 * @author Gwénolé Lecorvé
 */
public class CentralizedClientController extends AbstractClientController {

    /**
     * Serveur à qui est délégué le contrôle
     */
    protected ServerInterface server;

    /**
     * Client initial pour dernier
     */
    protected String idDernier;

    /**
     * Constructeur
     *
     * @param _url URL du contrôleur
     * @param _server Serveur à qui est délégué le contrôle
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws MalformedURLException Si l'URL est mal formée
     */
    public CentralizedClientController(String _url, ServerInterface _server) throws RemoteException, MalformedURLException {
        super(_url);
        do {
            idDernier = _server.connectNotepad(url);
        } while(idDernier == null);
        server = _server;
    }

    @Override
    public synchronized void finalize() {
        super.finalize();
        try {
            server.disconnectNotepad(url, dernier, suivant);
            Naming.unbind(url);
        } catch (RemoteException ex) {
            /* Nothing */
        } catch (NotBoundException ex) {
            /* Nothing */
        } catch (MalformedURLException ex) {
            /* Nothing */
        }
    }

    @Override
    public synchronized List<String> getDocumentList() throws RemoteException {
        try {
            unlockDocument();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        List<String> documentList = server.getDocumentList();
        dernier = new HashMap<String, String>();
        suivant = new HashMap<String, String>();

        if(idDernier.equals(this.getUrl())){
            for(String name : documentList){
                dernier.put(name, null);
                suivant.put(name, null);
            }
        } else {
            for(String name : documentList){
                dernier.put(name, idDernier);
                suivant.put(name, null);
            }
        }

        return documentList;
    }

    @Override
    public synchronized void openDocument(String _fileName) throws RemoteException, FileNotFoundException, MalformedURLException, NotBoundException {
        closeDocument();
        observedDocument.setDocument(server.getDocument(url, _fileName));
        setFileName(_fileName);
    }

    @Override
    public synchronized void closeDocument() throws RemoteException, FileNotFoundException, MalformedURLException, NotBoundException {
        if (hasDocument()) {
            unlockDocument();
            server.closeDocument(url, fileName, getDocument());
        }
    }

    @Override
    public synchronized void saveDocument() throws RemoteException {
        server.saveDocument(url, fileName, observedDocument.getDocument());
    }

    @Override
    public void tryLockDocument(String url) throws RemoteException, MalformedURLException, NotBoundException {
        Logger.getLogger(CentralizedClientController.class.getName()).log(Level.INFO, "Tentative de lock de " + fileName + " par " + url);
        if(!url.equals(this.getUrl())) {

            Logger.getLogger(CentralizedClientController.class.getName()).log(Level.INFO, "Demande reçue de " + url);

            if (dernier.get(fileName) != null) {
                try {
                    Logger.getLogger(CentralizedClientController.class.getName()).log(Level.INFO, "Demande envoyée à " + dernier.get(fileName));
                    RemoteControllerInterface r = (RemoteControllerInterface) Naming.lookup(dernier.get(fileName));

                    r.tryLockDocument(url);
                } catch (NotBoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            dernier.put(fileName, url);

            if (suivant.get(fileName) == null) {
                if (demandeur) {
                    Logger.getLogger(CentralizedClientController.class.getName()).log(Level.INFO, "Demande en attente");
                    suivant.put(fileName, url);
                } else {
                    Logger.getLogger(CentralizedClientController.class.getName()).log(Level.INFO, "Demande acceptée");
                    RemoteControllerInterface r = (RemoteControllerInterface) Naming.lookup(dernier.get(fileName));
                    r.lockDocument();
                    
                }
            }
        } else {
            demandeur = true;

            if(dernier.get(fileName) == null){
                Logger.getLogger(CentralizedClientController.class.getName()).log(Level.INFO, "Réussite automatique");
                lockDocument();
            } else {
                Logger.getLogger(CentralizedClientController.class.getName()).log(Level.INFO, "Demande envoyée à " + dernier.get(fileName));
                RemoteControllerInterface r = (RemoteControllerInterface)Naming.lookup(dernier.get(fileName));
                r.tryLockDocument(url);
            }
        }
    }

    /**
     * M�thode qui se d�clenche lorsque l'on re�oit un message COMMIT de la racine
     * @param r
     * @param v
     * @param _pos
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void ReceiveCommit(String _fileName, String _url, HashMap<String,LinkedList<String[]>> _pred, int _pos) throws RemoteException, MalformedURLException, NotBoundException{
    	String premierPred[] = new String[2];
    	premierPred[0] = _url;
    	premierPred[1] = String.valueOf(_pos);
    	this.pred.put(_fileName, this.pred.get().addFirst(premierPred));
    	Iterator<String[]> itPred = _pred.get(_fileName).iterator();
    	String Pred[] = new String[2];
    	while(itPred.hasNext())
		{
    		Pred = (String[]) itPred.next();
    		this.pred.get(_fileName).add(Pred);
		}
    	 if(this.pos == -1){
    		this.pos = 1 + _pos; 
    		if(suivant.get(fileName) != null){
    			RemoteControllerInterface R = (RemoteControllerInterface) Naming.lookup(suivant.get(fileName));
    			R.ReceiveCommit(_fileName, this.getUrl(), this.pred, this.pos);
    		}
    	 }
    	 TokenTimer.schedule(TokenTimeout(_fileName), 1000);
    }
    
    /**
     * M�thode qui se d�clenche lorsque l'on a pas re�u le jeton � temps 
     * @return
     * @throws NotBoundException 
     * @throws RemoteException 
     * @throws MalformedURLException 
     */
    protected TimerTask TokenTimeout(String _fileName) throws MalformedURLException, RemoteException, NotBoundException{
    	if(this.pred.get(_fileName).getFirst() == null){
    		Iterator<String[]> itPred = this.pred.get(_fileName).iterator();
        	String Pred[] = new String[2];
    			if(itPred.hasNext()){
    				Pred = (String[]) itPred.next();
    				RemoteControllerInterface R = (RemoteControllerInterface) Naming.lookup(Pred[0]);
    				R.ReceiveConnection(this, _fileName, this.pos);
    				TokenTimer.schedule(TokenTimeout(_fileName), 1000);
    				
    			} else {
    				
    	        	
    				ReconnectionTimer.schedule(TimeoutReconnection(), 1000);
    			}
    		
    	} else {
    		TokenTimer.schedule(TokenTimeout(_fileName), 1000);
    	}
    }
    
    
    protected TimerTask TimeoutReconnection(){
    	
    }
    
    public void ReceiveConnection(CentralizedClientController s, String _fileName, int _pos){
    	if(this.pos == _pos){
    		suivant.put(_fileName, s.getUrl());
    		try {
				s.ReceiveCommit(_fileName, this.getUrl(), this.pred, this.pos);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
    	} else {
    		try {
				s.lockDocument();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void ReceiveSearchPosition(CentralizedClientController s, int _pos, HashMap<String,LinkedList<String[]>> faultyPred, String _fileName){
    	if((this.pos != -1) && (this.pos < _pos)){
    		s.ReceivePosition(this, this.pos, this.suivant.get(_fileName), _fileName);
    	}
    	Iterator<String[]> itPred = faultyPred.get(_fileName).iterator();
    	String Pred[] = new String[2];
    	if(itPred.hasNext()){
			Pred = (String[]) itPred.next();
			if((this.suivant.get(_fileName) == Pred[0]) && !demandeur){
				this.suivant.put(_fileName, s.getUrl());
			}
    	}
    }
    
    public void ReceivePosition(CentralizedClientController s, int _pos, String suiv, String _fileName){
    	if(this.pred.get(_fileName).){
    		
    	}
    }
    
    public void ReceiveSearchQueue(RemoteControllerInterface s){
    	
    }
    
    protected TimerTask TimeoutCommit(){
    	
    	suivant = null;
    	dernier = null;
    	ReconnectionTimer.schedule(TimeoutReconnection(), 1000);
    	
    }
    
    
    @Override
    public void lockDocument()  throws RemoteException {
        locked = true;
        dernier.put(fileName, null);
        suivant.put(fileName, null);
    }

    @Override
    public synchronized void unlockDocument() throws RemoteException, MalformedURLException, NotBoundException {
        if (hasDocument()) {
            demandeur = false;
            locked = false;

            if(suivant.get(fileName) != null){
                ((RemoteControllerInterface) Naming.lookup(suivant.get(fileName))).lockDocument();
                pos = -1;
                suivant = null;
            }
        }
    }

    @Override
    protected synchronized void newDocument(String _fileName, boolean _isLocked) throws IOException {
        observedDocument.setDocument(server.newDocument(url, _fileName));

        dernier.put(_fileName, null);
        suivant.put(_fileName, null);

        if(_isLocked){
            demandeur = true;
            locked = true;
        }

        setFileName(_fileName);
    }

    @Override
    public synchronized void notifyDisconnection(String sourceUrl) {
        fileName = null;
        observedDocument.setDocument(null);
        locked = false;
    }

    @Override
    public void setNewDernier (String filename, String clientUrl, String newDernier)  throws RemoteException{
        if(clientUrl.equals(dernier.get(filename))){
            dernier.put(filename, newDernier);
        }
    }

    @Override
    public void setNewSuivant (String filename, String clientUrl, String newSuivant)  throws RemoteException{
        if(clientUrl.equals(suivant.get(filename))){
            suivant.put(filename, newSuivant);
        }
    }
}
