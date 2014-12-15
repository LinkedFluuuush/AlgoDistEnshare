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
import document.DocumentInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe de test automatique en console d'un client
 *
 * @author Gwénolé Lecorvé
 */
public class ConsoleClient {

    /**
     * Méthode principale
     *
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) throws RemoteException, FileNotFoundException, NotBoundException, MalformedURLException {
        String my_url = args[0];
        String server_url = args[1];
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        ServerInterface server = (ServerInterface) Naming.lookup(server_url);
        CentralizedClientController controller = new CentralizedClientController(my_url, server);

        if (server.connectNotepad(my_url)) {
            System.out.println("Connexion réussie");

            DocumentInterface d;
            try {
                d = server.newDocument(my_url, "f.txt");
            } catch (IOException ex) {
                System.err.println("Failed: " + ex.getMessage());
            }
            d = server.getDocument(my_url, "f.txt");
            System.out.println("Retrieved document f.txt = " + d.toString());
            d.selectLine(0);
            d.insertLine().setText(sdfDate.format(new Date()));
            System.out.println("Document = " + d.toString());
            if (server.tryLockDocument(my_url, "f.txt")) {
                System.out.println("Verrouillé");
                server.saveDocument(my_url, "f.txt", d);
                System.out.println("Sauvegardé");
                server.unlockDocument(my_url, "f.txt", d);
                System.out.println("Déverrouillé");
            } else {
                System.out.println("Échec du verrouillage");
            }
            server.closeDocument(my_url, "f.txt", d);
            System.out.println("Fermé");
            server.disconnectNotepad(my_url);
            System.out.println("Déconnecté");
            System.exit(0);

        } else {
            System.out.println("Échec de la connexion");
        }
    }

}
