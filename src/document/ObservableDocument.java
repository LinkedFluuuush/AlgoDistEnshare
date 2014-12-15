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
package document;

import java.util.Observable;

/**
 * Classe permettant à des observeurs d'observer un document
 * @author Gwénolé Lecorvé
 */
public class ObservableDocument extends Observable {

    /**
     * Document observable
     */
    protected DocumentInterface document;

    /**
     * Constructeur par défaut, aucun document
     */
    public ObservableDocument() {
        /* Nothing */
    }

    /**
     * Constructeur à partir d'un document
     * @param d Document observable
     */
    public ObservableDocument(DocumentInterface d) {
        document = d;
    }

    /**
     * Teste si un document est observable
     * @return Vrai si un document est observable, faux sinon
     */
    public boolean hasDocument() {
        return (document != null);
    }

    /**
     * Change le document observable/observé
     * <p>Remarque: Seule cette méthode prévient les observateurs d'un changement</p>
     * @param d Document observable/observé
     */
    public void setDocument(DocumentInterface d) {
        document = d;
        setChanged();
        notifyObservers();
    }

    /**
     * Retourne le document observable/observé
     * @return Interface d'un document
     */
    public DocumentInterface getDocument() {
        return document;
    }
}
