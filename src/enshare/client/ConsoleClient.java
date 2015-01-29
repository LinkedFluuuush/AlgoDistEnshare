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
import java.util.List;

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

        List<String> documentList = controller.getDocumentList();
        if(documentList.size() != 0){
            String fileName = documentList.get(0);

            System.out.println("Ouverture du document " + fileName);

            controller.openDocument(fileName);

            System.out.println("Tentative de verrouillage du document " + fileName);

            controller.tryLockDocument(my_url);

            while (!controller.isLocked());

            System.out.println("Document verrouillé");

            DocumentInterface doc = controller.observedDocument.getDocument();

            doc.selectLine(controller.observedDocument.getDocument().size() - 1);
            doc.insertLine();
            doc.getLine().setText("This is a test");

            controller.saveDocument();

            controller.unlockDocument();

            controller.closeDocument();

            controller.openDocument(fileName);

            System.out.println(controller.observedDocument.getDocument().toString());
        }

        controller.finalize();
        System.exit(0);
    }
}
