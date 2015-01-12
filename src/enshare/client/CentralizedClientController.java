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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            }
        }
    }

    @Override
    protected synchronized void newDocument(String _fileName, boolean _isLocked) throws IOException {
        observedDocument.setDocument(server.newDocument(url, _fileName, _isLocked));
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
        if(dernier.get(filename).equals(clientUrl)){
            dernier.put(filename, newDernier);
        }
    }

    @Override
    public void setNewSuivant (String filename, String clientUrl, String newSuivant)  throws RemoteException{
        if(suivant.get(filename).equals(clientUrl)){
            suivant.put(filename, newSuivant);
        }
    }
}
