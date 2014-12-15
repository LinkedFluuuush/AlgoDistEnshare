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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Gwénolé Lecorvé
 */
public class StorableDocument implements Storable {

    /**
     *
     */
    protected String path;

    private DocumentInterface storedDocument;

    /**
     * Constructeur à partir d'un chemin de fichier
     * <p>Remarque: charge le document.</p>
     * @param _path Chemin du fichier
     * @throws FileNotFoundException Si le nom de fichier n'a pas été trouvé.
     * @throws IOException Si une erreur survient lors de la lecture sur le disque.
     */
    public StorableDocument(String _path) throws FileNotFoundException, IOException {
        path = _path;
        storedDocument = new Document();
        load();
    }

    /**
     * Constructeur à partir d'un chemin de fichier et d'un document
     * <p>Remarque: sauvegarde le document dans le fichier.</p>
     * @param _path Chemin du fichier
     * @param doc
     * @throws IOException IOException Si une erreur survient lors de l'écriture sur le disque.
     */
    public StorableDocument(String _path, DocumentInterface doc) throws IOException {
        path = _path;
        storedDocument = doc;
        save();
    }

    @Override
    public Object getStored() {
        return storedDocument;
    }

    /**
     * Retourne le document (aucun chargement)
     * @return Interface du document
     */
    public DocumentInterface getDocument() {
        return (DocumentInterface) getStored();
    }

    /**
     * Change le document (aucune sauvegarde)
     * @param d Interface d'un document
     */
    public void setDocument(DocumentInterface d) {
        storedDocument = d;
    }

    @Override
    public String getPath() {
        return path;
    }

    /**
     * Fixe le chemin du fichier
     * @param _path Chaîne de caractères
     */
    protected void setPath(String _path) {
        path = _path;
    }

    @Override
    public void save() throws IOException {
        FileWriter fw = new FileWriter(getPath());
        System.out.println("Save ->" + storedDocument.toString());
        fw.write(storedDocument.toString());
        fw.flush();
        fw.close();
    }

    @Override
    public void saveAs(String _path) throws IOException {
        setPath(_path);
        save();
    }

    @Override
    public void load() throws FileNotFoundException, IOException {
        String str = "";
        File file = new File(getPath()); //for ex foo.txt
        FileReader reader = new FileReader(file);
        char[] chars = new char[(int) file.length()];
        reader.read(chars);
        str = new String(chars);
        reader.close();
        storedDocument.fromString(str);
    }

    /**
     * Méthode principale (test)
     * @param args Arguments de la ligne de commande
     * @throws IOException Si une erreur est survenu lors des entrées/sorties avec le disques.
     */
    public static void main(String[] args) throws IOException {

        DocumentInterface d = new Document();
        d.fromString("bla\nbla\ndelta\ngamma");

        StorableDocument sd = new StorableDocument("./f.txt", d);
        sd.save();

        LineInterface l = sd.getDocument().getLine();
        l = d.removeLine();
        l.setText("Alpha Beta Gamma");
        System.out.println("Document = " + d.toString());
        sd.save();

        StorableDocument sd2 = new StorableDocument("./f.txt");
        DocumentInterface d2 = sd2.getDocument();
        System.out.println("Document 2 = " + d2.toString());
    }

}
