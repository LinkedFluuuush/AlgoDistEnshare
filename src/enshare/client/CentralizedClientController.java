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
import java.rmi.RemoteException;
import java.util.List;

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
     * Constructeur
     *
     * @param _url URL du contrôleur
     * @param _server Serveur à qui est délégué le contrôle
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws MalformedURLException Si l'URL est mal formée
     */
    public CentralizedClientController(String _url, ServerInterface _server) throws RemoteException, MalformedURLException {
        super(_url);
        _server.connectNotepad(url);
        server = _server;
    }

    @Override
    public synchronized void finalize() {
        super.finalize();
        try {
            server.disconnectNotepad(url);
            Naming.unbind(url);
        } catch (RemoteException ex) {
            /* Nothing */
        } catch (NotBoundException ex) {
            /* Nothing */
        } catch (MalformedURLException ex) {
            /* Nothing */
        }
    }

    ;

    @Override
    public synchronized List<String> getDocumentList() throws RemoteException {
        return server.getDocumentList();
    }

    @Override
    public synchronized void openDocument(String _fileName) throws RemoteException, FileNotFoundException {
        closeDocument();
        observedDocument.setDocument(server.getDocument(url, _fileName));
        setFileName(_fileName);
    }

    @Override
    public synchronized void closeDocument() throws RemoteException, FileNotFoundException {
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
    public synchronized boolean tryLockDocument() throws RemoteException, FileNotFoundException {
        if (hasDocument() && !isLocked()) {
            locked = server.tryLockDocument(url, fileName);
            return locked;
        }
        return false;
    }

    @Override
    public synchronized void unlockDocument() throws RemoteException, FileNotFoundException {
        if (hasDocument()) {
            server.unlockDocument(url, fileName, getDocument());
            locked = false;
        }
    }

    @Override
    protected synchronized void newDocument(String _fileName, boolean _isLocked) throws FileAlreadyExistsException, IOException {
        observedDocument.setDocument(server.newDocument(url, _fileName, _isLocked));
        setFileName(_fileName);
    }

    @Override
    public synchronized void notifyDisconnection(String sourceUrl) {
        fileName = null;
        observedDocument.setDocument(null);
        locked = false;
    }

}
