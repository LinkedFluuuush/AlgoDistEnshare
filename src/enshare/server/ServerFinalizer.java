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

import enshare.server.Server;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Classe permettant de gérer l'interruption d'un serveur
 * @author Gwénolé Lecorvé
 */
public class ServerFinalizer implements SignalHandler {

    /**
     * Serveur surveillé
     */
    protected Server server;

    /**
     * Constructeur
     * @param _server Serveur à surveiller
     */
    public ServerFinalizer(Server _server) {
        server = _server;
    }

    /**
     * Définit le comportement à avoir lors de l'interception d'un signal
     * @param sig Signal intercepté
     */
    @Override
    public void handle(Signal sig) {
        server.finalize();
        System.exit(0);
    }

}
