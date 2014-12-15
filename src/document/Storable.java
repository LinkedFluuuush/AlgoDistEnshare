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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Interface qui définit les fonctionnalités d'un objet qui peut être stocké sur le disque
 * @author Gwénolé Lecorvé
 */
public interface Storable {

    /**
     * Renvoie le chemin de l'objet sur le disque
     * @return Chaîne de caractères
     */
    public String getPath();

    /**
     * Renvoie l'objet stocké
     * @return Objet quelconque
     */
    public Object getStored();

    /**
     * Sauvegarde l'objet sur le disque
     * @throws IOException Si une erreur survient lors de l'écriture sur le disque.
     */
    public void save() throws IOException;

    /**
     * Sauvegarde l'objet dans un fichier de nom donné
     * <p>Remarque: le fichier est écrasé s'il existe déjà.</p>
     * @param fileName Nom du fichier
     * @throws IOException Si une erreur survient lors de la lecture sur le disque.
     */
    public void saveAs(String fileName) throws IOException;

    /**
     * Charge l'objet depuis le disque
     * @throws FileNotFoundException Si le nom de fichier n'a pas été trouvé.
     * @throws IOException Si une erreur survient lors de la lecture sur le disque.
     */
    public void load() throws FileNotFoundException, IOException;

}
