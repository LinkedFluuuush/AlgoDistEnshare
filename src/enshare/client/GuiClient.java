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
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import sun.misc.Signal;

/**
 * Classe qui associe un contrôleur de client à une interface graphique
 *
 * @author Gwénolé Lecorvé
 */
public class GuiClient {

    /**
     * Contrôleur du client
     */
    protected CentralizedClientController controller;

    /**
     * Interface graphique
     */
    protected ClientView editor;

    /**
     * Constructeur
     *
     * @param url URL à affecter au contrôleur
     * @param _server Serveur avec qui le contrôleur doit dialoguer
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws MalformedURLException Si l'URL est mal formée
     */
    public GuiClient(String url, ServerInterface _server) throws RemoteException, MalformedURLException {
        controller = new CentralizedClientController(url, _server);
        editor = new ClientView(controller);

    }

    /**
     * Applique les actions à réaliser avant la fermeture du client
     */
    @Override
    public void finalize() {
        controller.finalize();
    }

    /**
     * Méthode principale
     *
     * @param args Arguments de la ligne de commande
     * @throws NotBoundException Si le serveur distant n'est pas trouvé à l'URL
     * indiquée
     * @throws MalformedURLException Si l'URL du client ou du serveur est mal
     * formée
     * @throws RemoteException Si un problème en rapport avec RMI survient
     */
    public static void main(String[] args) throws NotBoundException, MalformedURLException, RemoteException {
        if (args.length != 2) {
            System.err.println("Erreur: mauvais nombre d'arguments.");
            System.err.println("Usage: java <RMI_options> EnshareClient <rmi_client_url> <rmi_server_url>");
            System.err.println("       rmi_client_url: URL désirée pour le client.");
            System.err.println("       rmi_server_url: URL du serveur.");
        } else {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            ServerInterface server = (ServerInterface) Naming.lookup(args[1]);
            GuiClient client = new GuiClient(args[0], server);
            Signal.handle(new Signal("INT"), new GuiClientFinalizer(client));
        }
    }

}
