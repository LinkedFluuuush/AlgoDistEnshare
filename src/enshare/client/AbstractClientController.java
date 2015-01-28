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

import document.DocumentInterface;
import document.ObservableDocument;
import enshare.AbstractIdentifiable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector;
import java.util.Timer;

/**
 * Classe abstraite du contrôleur d'un client
 *
 * @author Gwénolé Lecorvé
 */
public abstract class AbstractClientController extends AbstractIdentifiable implements LocalControllerInterface, RemoteControllerInterface {

    /**
     * Document observable (notamment par une interface graphique)
     */
    protected ObservableDocument observedDocument;

    /**
     * Nom du document actuellement ouvert
     */
    protected String fileName;

    /**
     * Adresse des derniers demandeurs
     */
    protected HashMap<String, String> dernier;

    /**
     * Adresse des demandeurs suivants
     */
    protected HashMap<String, String> suivant;

    /**
     * Etats de demande du client
     */
    protected boolean demandeur;

    /**
     * État actuel du verrou
     */
    protected boolean locked;
    /**
     * Position dans la chaine des next
     */
    protected int pos;
    
    /**
     * Tableau des prédécesseurs
     */
    protected HashMap<String, HashMap<String,Integer>> pred;
    
    /**
     * Temporisateur pour détecter la perte d'une requête
     */
    protected Timer CommitTimer;
    
    /**
     * Temporisateur pour détecter la perte du jeton
     */
    protected Timer TokenTimer;
    
    /**
     * Temporisateur pour le recouvrement
     */
    protected Timer ReconnectionTimer;

    /**
     * Constructeur
     *
     * @param _url URL à affecter au contrôleur pour des dialogues futurs
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws MalformedURLException Si l'URL est malformée
     */
    public AbstractClientController(String _url) throws RemoteException, MalformedURLException {
        super(_url);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        CommitTimer = new Timer();
        TokenTimer = new Timer();
        ReconnectionTimer = new Timer();
        pred = new HashMap<String, HashMap<String,Integer>>();
        locked = false;
        demandeur = false;
        dernier = null;
        suivant = null;
        url = _url;
        observedDocument = new ObservableDocument();
        RemoteControllerInterface stub = (RemoteControllerInterface) UnicastRemoteObject.exportObject(this, 0);
        Naming.rebind(_url, stub);
    }

    @Override
    public synchronized void finalize() {
        try {
            try {
                closeDocument();
            } catch (RemoteException ex) {
                /* Nothing */
            } catch (FileNotFoundException ex) {
                /* Nothing */
            } catch (MalformedURLException e) {
                /* Nothing */
            } catch (NotBoundException e) {
                /* Nothing */
            }
            if (hasDocument()) {
                try {
                    unlockDocument();
                } catch (RemoteException ex) {
                    Logger.getLogger(AbstractClientController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(AbstractClientController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(AbstractClientController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            try {
                super.finalize();
            } catch (Throwable ex) {
                Logger.getLogger(AbstractClientController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Définit le nom du document courant
     *
     * @param _fileName Chaîne de caractères
     */
    protected synchronized void setFileName(String _fileName) {
        fileName = _fileName;
    }

    /**
     * Teste si un nom de document est actuellement connu
     *
     * @return Vrai si un nom de document est connu, faux sinon
     */
    public synchronized boolean hasFileName() {
        return (fileName != null && !fileName.equals(""));
    }

    /**
     * Teste si un document est actuellement ouvert
     *
     * @return Vrai si un document est actuellement ouvert, faux sinon
     */
    public synchronized boolean hasDocument() {
        return (observedDocument != null && observedDocument.hasDocument());
    }

    /**
     * Retourne le document observé
     *
     * @return Le document observé
     */
    protected synchronized DocumentInterface getDocument() {
        if (hasDocument()) {
            return getObservedDocument().getDocument();
        } else {
            return null;
        }
    }

    @Override
    public synchronized ObservableDocument getObservedDocument() {
        return observedDocument;
    }

    @Override
    public synchronized boolean isLocked() {
        return locked;
    }

    @Override
    public synchronized void saveDocumentAs(String _fileName) throws IOException, NotBoundException {
        // Mémorise le document actuel
        DocumentInterface d = observedDocument.getDocument();
        // Mémorise l'état du verrouillage
        boolean wasLocked = isLocked();
        // Crée un nouveau documenten verrouillant dès sa création pour permettre une sauvegarde future en exclusion mutuelle
        newDocument(_fileName, true);
        // Écrase le nouveau document par l'ancien (mémorisé)
        observedDocument.setDocument(d);
        // Demande la sauvegarde (OK le document est verrouillé)
        saveDocument();
        // Remise du verrouillage à son état d'origine
        if (!wasLocked) {
            unlockDocument();
        }
        locked = wasLocked;
    }

    @Override
    public synchronized void newDocument(String _fileName) throws IOException {
        newDocument(_fileName, false);
    }

    /**
     * Crée et ouvre un nouveau document, verrouille éventuellement le document créé
     * <p>Remarque: cette méthode permet éventuellement de verrouiller le nouveau document dès sa création.</p>
     *
     * @param _fileName Nom du fichier à créer
     * @param _isLocked Vrai si le nouveau fichier doit être verrouillé après sa création, faux sinon
     * @throws IOException Si une erreur survient lors de l'écriture du fichier sur le disque
     */
    protected abstract void newDocument(String _fileName, boolean _isLocked) throws IOException;

    @Override
    public synchronized void updateDocument(String sourceUrl, DocumentInterface d) throws RemoteException {
        observedDocument.setDocument(d);
    }

}
