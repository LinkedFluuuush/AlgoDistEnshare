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
 * Interface qui définit les fonctionnalités d'un document
 * @author Gwénolé Lecorvé
 */
public interface DocumentInterface extends Serializable {

    /**
     * Renvoie la forme textuelle d'un document sous la forme d'une chaîne de caractères
     * @return Chaîne de caractères (éventuellement vide)
     */
    public String toString();

    /**
     * Remplit le document à partir d'un texte sous la forme d'une chaîne de caractères
     * @param text Chaîne de caractère, éventuellement avec plusieurs lignes
     */
    public void fromString(String text);

    /**
     * Sélectionne une ligne du document
     * <p>Remarque: si le numéro de ligne demandé déborde du document, ce numéro est borné par 0 et N-1</p>
     * @param i Le numéro de la ligne à sélectionner (de 0 à N-1)
     * @return Ligne courante après sélection
     */
    public LineInterface selectLine(int i);

    /**
     * Insère une ligne après la ligne courante et sélectionne la nouvelle ligne
     * @return Nouvelle ligne
     */
    public LineInterface insertLine();

    /**
     * Récupère la ligne courante
     * @return
     */
    public LineInterface getLine();

    /**
     * Retourne le numéro de ligne actuel
     * @return Entier
     */
    public int getLineNumber();

    /**
     * Supprime la ligne courante
     * <p>Remarque: si la ligne concernée est l'unique ligne du document, une ligne vierge est insérée après la suppression</p>
     * @return Ligne courante après la suppression
     */
    public LineInterface removeLine();

    /**
     * Retourne le nombre de lignes
     * @return Entier
     */
    public int size();

    /**
     * Retourne le nombre de caractères dans le documents (hors caractères de retour à la ligne)
     * @return Entier
     */
    public int length();

}
