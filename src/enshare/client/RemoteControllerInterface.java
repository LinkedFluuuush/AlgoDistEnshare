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

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface qui définit toutes les méthodes appelables à distance sur un
 * contrôleur de client
 *
 * @author Gwénolé Lecorvé
 */
public interface RemoteControllerInterface extends Remote {

    /**
     * Met à jour la copie du document courant
     *
     * @param d Nouvelle version du document
     * @param sourceUrl URL du processus expéditeur
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public void updateDocument(String sourceUrl, DocumentInterface d) throws RemoteException;

    /**
     * Réceptionne une notification de déconnexion
     *
     * @param sourceUrl URL du processus notifiant
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public void notifyDisconnection(String sourceUrl) throws RemoteException;

    /**
     * Envoie la demande de verrouillage sur le document courant
     *
     * @param url L'url du client demandeur initial
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws java.net.MalformedURLException Si l'url de source est mal formée
     * @throws java.rmi.NotBoundException Si l'url de source est erronnée
     */
    public void tryLockDocument(String url) throws RemoteException, MalformedURLException, NotBoundException;

    /**
     * Verrouille le document courant
     */
    public void lockDocument();
}
