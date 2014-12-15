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

import document.ObservableDocument;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface qui définit toutes les fonctions appelable localement sur le
 * contrôleur (notamment par la GUI)
 *
 * @author Gwénolé Lecorvé
 */
public interface LocalControllerInterface {

    /**
     * Définit les actions à effectuer avant la destruction du contrôleur
     */
    public void finalize();

    /**
     * Retourne le document observable actuel
     *
     * @return Document observable
     */
    public ObservableDocument getObservedDocument();

    /**
     * Retourne la liste des noms des documents disponibles
     *
     * @return Une liste de chaînes de caractères
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public List<String> getDocumentList() throws RemoteException;

    /**
     * Ouvre un document
     *
     * @param _fileName Nom du document à ouvrir
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws FileNotFoundException Si le nom de fichier n'a pas été trouvé
     */
    public void openDocument(String _fileName) throws RemoteException, FileNotFoundException;

    /**
     * Ferme le document courant
     *
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws FileNotFoundException Si le document actuel n'a pas été trouvé
     */
    public void closeDocument() throws RemoteException, FileNotFoundException;

    /**
     * Sauvegarde la copie du document courant
     * <p>
     * Remarque: Nécessite d'avoir préalablement verrouillé le document</p>
     *
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public void saveDocument() throws RemoteException;

    /**
     * Sauvegarde la copie du document courant dans un nouveau fichier
     *
     * @param _fileName Nom du nouveau document
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws FileAlreadyExistsException Si le nom demandé existe déjà pour un
     * autre fichier
     * @throws IOException Si une erreur survient lors de l'écriture du document
     * sur le disque
     */
    public void saveDocumentAs(String _fileName) throws RemoteException, FileAlreadyExistsException, IOException;

    /**
     * Crée et ouvre un nouveau document
     * <p>Remarque: le document sera mis en lecture seule initialement.</p>
     *
     * @param _fileName Nom du nouveau document
     * @throws FileAlreadyExistsException Si le nom demandé existe déjà pour un
     * autre fichier
     * @throws IOException Si une erreur survient lors de la création du nouveau
     * document sur le disque
     */
    public void newDocument(String _fileName) throws FileAlreadyExistsException, IOException;

    /**
     * Teste si l'état actuel est "verrouillé"'
     *
     * @return Vrai si l'état est "verrouillé", faux sinon
     */
    public boolean isLocked();

    /**
     * Essaie de verrouiller le document courant
     *
     * @return L'état du verrouillage après la demande
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws FileNotFoundException Si le document n'a pas été trouvé
     */
    public boolean tryLockDocument() throws RemoteException, FileNotFoundException;

    /**
     * Déverrouille le document courant
     *
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws FileNotFoundException Si le document actuel n'a pas été trouvé
     */
    public void unlockDocument() throws RemoteException, FileNotFoundException;

}
