/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enshare.server;

import document.Document;
import document.DocumentInterface;
import document.StorableDocument;
import enshare.AbstractIdentifiable;
import enshare.client.RemoteControllerInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Signal;

/**
 * Classe définissant un serveur de gestion de documents et d'exclusion mutuelle
 *
 * @author Gwénolé Lecorvé
 */
public class Server extends AbstractIdentifiable implements ServerInterface {

    /**
     * Séparateur de fichier dans un chemin d'accès
     */
    protected final String separator = System.getProperty("file.separator");

    /**
     * Répertoire où sont stockés les documents
     */
    protected String dirName;

    /**
     * Table associant chaque <b>nom de fichier</b> à son <b>document
     * stockable</b>
     * <p>
     * Remarque: Le nom est différent du chemin d'accès au fichier.</p>
     */
    protected Map<String, StorableDocument> storedDocuments;

    /**
     * Table associant chaque <b>URL d'un client</b> à son <b>contrôleur
     * distant</b>
     */
    protected Map<String, RemoteControllerInterface> connectedNotepads;

    /**
     * Table associant chaque <b>nom de fichier</b> à l'<b>ensemble des
     * contrôleurs distants</b>
     */
    protected Map<String, Set<RemoteControllerInterface>> readers;

    /**
     * Table associant chaque <b>nom de fichier</b> à la <b>file des
     * écrivains</b>
     */
    protected Map<String, BlockingQueue<RemoteControllerInterface>> writers;

    /**
     * Constructeur
     *
     * @param _url URL du serveur
     * @param _dirName Répertoire contenant les documents à partager
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws MalformedURLException Si l'URL est mal formée
     */
    public Server(String _url, String _dirName) throws RemoteException, MalformedURLException {
        super(_url);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        connectedNotepads = new HashMap();
        dirName = _dirName;
        storedDocuments = new HashMap();
        writers = new HashMap();
        readers = new HashMap();
        loadDirectory();
        ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);
        Naming.rebind(_url, stub);
        url = _url;
        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Serveur enregistré comme " + url);
    }

    /**
     * Applique les actions permettant de terminer proprement un serveur avant
     * sa destruction/terminaison
     */
    @Override
    public synchronized void finalize() {
        Map<String, RemoteControllerInterface> copy = new HashMap(connectedNotepads);
        for (Map.Entry<String, RemoteControllerInterface> entry : copy.entrySet()) {
            disconnectNotepad(entry.getKey());
            try {
                entry.getValue().notifyDisconnection(url);
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Naming.unbind(url);
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Charge tous les documents à partager
     */
    protected void loadDirectory() {
        File folder = new File(dirName);
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                try {
                    storedDocuments.put(fileEntry.getName(), new StorableDocument(fileEntry.getPath()));
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Document " + fileEntry.getName() + " chargé");
                    writers.put(fileEntry.getName(), new ArrayBlockingQueue(1));
                    readers.put(fileEntry.getName(), new HashSet());
                } catch (IOException ex) {
                    /* Nothing */
                }
            }
        }
    }

    /**
     * Renvoie l'URL d'un contrôleur distant d'un client
     * <p>
     * Remarque: la méthode renvoie null si le contrôleur n'est pas connu du
     * serveur.</p>
     *
     * @param controller Contrôleur distant recherché
     * @return URL (chaîne de caractères)
     */
    protected String getClientUrl(RemoteControllerInterface controller) {
        for (Map.Entry<String, RemoteControllerInterface> entry : connectedNotepads.entrySet()) {
            if (entry.getValue() == controller) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public synchronized boolean connectNotepad(String clientUrl) throws RemoteException {
        try {
            RemoteControllerInterface client = (RemoteControllerInterface) Naming.lookup(clientUrl);
            connectedNotepads.put(clientUrl, client);
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Connexion du notepad " + clientUrl);
            return true;
        } catch (NotBoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public synchronized void disconnectNotepad(String clientUrl) {
        closeAllDocuments(clientUrl);
        connectedNotepads.remove(clientUrl);
        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Déconnexion du notepad " + clientUrl);
    }

    @Override
    public synchronized List<String> getDocumentList() {
        List<String> fileNames = new ArrayList();
        for (Map.Entry<String, StorableDocument> entry : storedDocuments.entrySet()) {
            fileNames.add(entry.getKey());
        }
        return fileNames;
    }

    /**
     * Teste si un nom de fichier est connu du serveur
     *
     * @param targetFileName Nom du fichier recherché
     * @return Vrai si le fichier est connu, faux sinon
     */
    public synchronized boolean existingFileName(String targetFileName) {
        for (Map.Entry<String, StorableDocument> entry : storedDocuments.entrySet()) {
            if (entry.getKey().equals(targetFileName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized DocumentInterface getDocument(String clientUrl, String targetFileName) throws RemoteException, FileNotFoundException {
        StorableDocument sd = storedDocuments.get(targetFileName);
        if (sd != null) {
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Ouverture du document " + targetFileName + " par notepad " + clientUrl);
            closeAllDocuments(clientUrl);
            RemoteControllerInterface controller = connectedNotepads.get(clientUrl);
            readers.get(targetFileName).add(controller);
            return sd.getDocument();
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public synchronized void closeDocument(String clientUrl, String targetFileName, DocumentInterface d) throws RemoteException, FileNotFoundException {
        StorableDocument sd = storedDocuments.get(targetFileName);
        if (sd != null) {
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Fermeture du document " + targetFileName + " par notepad " + clientUrl);
            RemoteControllerInterface controller = connectedNotepads.get(clientUrl);
            // Retirer de la liste des lecteurs
            readers.get(targetFileName).remove(controller);
            // Essaie de supprimer de la liste des écrivains
            unlockDocument(clientUrl, targetFileName, d);
            if (readers.get(targetFileName).isEmpty()) {
                try {
                    // Recharger la dernière version sauvegardée (-> perte des changements non sauvegardés)
                    sd.load();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     * Ferme tous les documents d'un client
     *
     * @param clientUrl URL du client
     */
    protected synchronized void closeAllDocuments(String clientUrl) {
        RemoteControllerInterface controller = connectedNotepads.get(clientUrl);
        for (String fileName : storedDocuments.keySet()) {
            // Retirer le notepad de toutes les listes de lecteurs
            Set<RemoteControllerInterface> s = readers.get(fileName);
            if (s.contains(controller)) {
                readers.get(fileName).remove(controller);
                // Retirer aussi de la liste des écrivant si besoin
                if (writers.get(fileName).contains(controller)) {
                    writers.get(fileName).poll();
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Déverrouillage du document " + fileName + " pour notepad " + clientUrl);
                }
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Fermeture du document " + fileName + " pour notepad " + clientUrl);
            }
        }
    }

    @Override
    public synchronized DocumentInterface newDocument(String clientUrl, String targetFileName) throws RemoteException, FileAlreadyExistsException, IOException {
        return newDocument(clientUrl, targetFileName, false);
    }

    @Override
    public synchronized DocumentInterface newDocument(String clientUrl, String targetFileName, boolean isLocked) throws RemoteException, FileAlreadyExistsException, IOException {
        if (existingFileName(targetFileName)) {
            throw new FileAlreadyExistsException("Le nom " + targetFileName + " est déjà utilisé par un autre fichier.");
        }
        StorableDocument sd = new StorableDocument(dirName + separator + targetFileName, new Document());
        sd.save();
        storedDocuments.put(targetFileName, sd);
        writers.put(targetFileName, new ArrayBlockingQueue(1));
        readers.put(targetFileName, new HashSet());
        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Nouveau document " + targetFileName);
        // Ouverture en mode lecture
        DocumentInterface returned_document = getDocument(clientUrl, targetFileName);
        if (isLocked) {
            // Ouverture en mode écriture
            tryLockDocument(clientUrl, targetFileName);
        }
        return returned_document;
    }

    @Override
    public synchronized boolean saveDocument(String clientUrl, String targetFileName, DocumentInterface d) throws RemoteException {
        StorableDocument sd = storedDocuments.get(targetFileName);
        if (sd != null) {
            RemoteControllerInterface controller = connectedNotepads.get(clientUrl);
            if (writers.get(targetFileName).contains(controller)) {
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Modification du document " + targetFileName);
                sd.setDocument(d);
                notifyModificationToClients(targetFileName, controller);
                try {
                    sd.save();
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Document " + targetFileName + " sauvergardé");
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean tryLockDocument(String clientUrl, String targetFileName) throws RemoteException, FileNotFoundException {
        if (storedDocuments.containsKey(targetFileName)) {
            RemoteControllerInterface controller = connectedNotepads.get(clientUrl);
            if (readers.get(targetFileName).contains(controller) && writers.get(targetFileName).offer(controller)) {
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Notepad " + clientUrl + " verrouille document " + targetFileName);
                return true;
            } else {
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Notepad " + clientUrl + " échoue à verrouiller document " + targetFileName);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public synchronized void unlockDocument(String clientUrl, String targetFileName, DocumentInterface d) throws RemoteException, FileNotFoundException {
        StorableDocument sd = storedDocuments.get(targetFileName);
        if (sd != null) {
            RemoteControllerInterface controller = connectedNotepads.get(clientUrl);
            // Nothing happens if controller was not in the blocking queue
            if (writers.get(targetFileName).remove(controller)) {
                sd.setDocument(d);
                notifyModificationToClients(targetFileName, controller);
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Notepad " + clientUrl + " déverrouille document " + targetFileName);
            }
        }
    }

    /**
     * Propage la nouvelle version d'un document à tous ses lecteurs, sauf le
     * client responsable de la modification
     *
     * @param targetFileName Nom du fichier modifié
     * @param exceptedController Client à l'origine de la modification
     */
    protected synchronized void notifyModificationToClients(String targetFileName, RemoteControllerInterface exceptedController) {
        if (storedDocuments.containsKey(targetFileName)) {
            for (RemoteControllerInterface controller : readers.get(targetFileName)) {
                if (controller != exceptedController) {
                    String clientUrl = getClientUrl(controller);
                    if (clientUrl != null) {
                        try {
                            System.err.println("Notification du controleur " + clientUrl + " pour le document " + targetFileName);
                            controller.updateDocument(url, storedDocuments.get(targetFileName).getDocument());
                        } catch (ConnectException ex) {
                            // Supprimer le client s'il n'existe plus
                            disconnectNotepad(clientUrl);
                        } catch (RemoteException ex) {
                            System.err.println("ERREUR: Impossible de notifier le controleur " + clientUrl);
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * Méthode principale
     *
     * @param args Arguments de la ligne de commande
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws MalformedURLException Si l'URL du serveur est mal formée
     */
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        if (args.length != 2) {
            System.err.println("Erreur: mauvais nombre d'arguments.");
            System.err.println("Usage: java <RMI_options> EnshareServer <rmi_url> <directory>");
            System.err.println("       rmi_url: URL du serveur qui va être lancé.");
            System.err.println("       directory: Répertoire contenant les documents partagés.");
        } else {
            Server server = new Server(args[0], args[1]);
            Signal.handle(new Signal("INT"), new ServerFinalizer(server));
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Le serveur est prêt");
        }
    }

}
