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
package enshare.server;

import document.DocumentInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

/**
 * Interface qui définit toutes les méthodes d'un serveur appelables à distance
 *
 * @author Gwénolé Lecorvé
 */
public interface ServerInterface extends Remote {

    /**
     * Tente de connecter un client
     *
     * @param clientUrl URL du client demandeur
     * @return L'adresse du dernier à la création sur chaque fichier
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public String connectNotepad(String clientUrl) throws RemoteException;

    /**
     * Déconnecte un client
     *
     * @param clientUrl URL du client demandeur
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public void disconnectNotepad(String clientUrl, HashMap<String, String> clientDernier,  HashMap<String, String> clientSuivant) throws RemoteException;

    /**
     * Retourne la liste des noms des documents disponibles
     *
     * @return Une liste de chaînes de caractères
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public List<String> getDocumentList() throws RemoteException;

    /**
     * Retourne la version actuellement en mémoire d'un document demandé en
     * ouverture (en lecture uniquement initialement)
     *
     * @param clientUrl URL du client demandeur
     * @param targetFileName nom du fichier à récupérer
     * @return Interface vers le document
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws FileNotFoundException Si le nom de fichier demandé n'existe pas
     */
    public DocumentInterface getDocument(String clientUrl, String targetFileName) throws RemoteException, FileNotFoundException;

    /**
     * Ferme un document par un client, c'est-à-dire retire le client de la
     * liste des lecteurs et écrivains de ce fichier, et met également à jour
     * l'image mémoire du document
     *
     * @param clientUrl URL du client demandeur
     * @param targetFileName Nom du document à fermer
     * @param d Copie du document chez le client
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws FileNotFoundException Si le nom de fichier demandé n'existe pas
     */
    public void closeDocument(String clientUrl, String targetFileName, DocumentInterface d) throws RemoteException, FileNotFoundException;

    /**
     * Crée un nouveau document vide et l'ouvre en écriture pour le client
     * demandeur de la création
     * <p>
     *
     * @param clientUrl URL du client demandeur
     * @param targetFileName Nom du document à créer
     * @return L'interface du nouveau document vierge
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws FileAlreadyExistsException Si le nom de fichier existe déjà
     * @throws IOException Si une erreur est rencontré lors des entrées/sorties
     * sur le disque (notamment l'écriture du nouveau fichier)
     */
    public DocumentInterface newDocument(String clientUrl, String targetFileName) throws RemoteException, FileAlreadyExistsException, IOException;

    /**
     * Met à jour l'image mémoire d'un document et la sauvegarde sur le disque
     *
     * @param clientUrl URL du client demandeur
     * @param targetFileName Nom du document à sauvegarder
     * @param d Copie du document d'un client
     * @return Vrai si la sauvegarde a réussi, faux sinon
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public boolean saveDocument(String clientUrl, String targetFileName, DocumentInterface d) throws RemoteException;
}
