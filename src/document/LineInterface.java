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

import java.io.Serializable;

/**
 * Interface qui définit les fonctionnalités d'une ligne
 * @author Gwénolé Lecorvé
 */
public interface LineInterface extends Serializable {

    /**
     * Change le contenu de la ligne
     * @param text Nouveau contenu
     */
    public void setText(String text);

    /**
     * Retourne le contenu la ligne
     * @return Une chaîne de caractères
     */
    public String getText();

    /**
     * Renvoie la longueur de la ligne
     * @return Entier
     */
    public int length();

    /**
     * Retourne une représentation textuelle de la ligne
     * @return Chaîne de caractères
     */
    @Override
    public String toString();

}
