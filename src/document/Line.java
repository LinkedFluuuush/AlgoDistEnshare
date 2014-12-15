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

/**
 * Classe implémentant les fonctionnalité d'une ligne
 * @author Gwénolé Lecorvé
 */
public class Line implements LineInterface {

    /**
     * Contenu de la ligne
     */
    protected String text;

    /**
     * Constructeur par défaut, création d'une ligne vierge
     */
    public Line() {
        text = "";
    }

    /**
     * Constructeur, création d'une ligne à partir d'un contenu initial
     * @param _text Contenu initial
     */
    public Line(String _text) {
        text = _text;
    }

    @Override
    public void setText(String _text) {
        text = _text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public String toString() {
        return getText();
    }

}
