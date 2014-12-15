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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe implémentant l'interface d'un document
 *
 * @author Gwénolé Lecorvé
 */
public class Document implements DocumentInterface {

    /**
     * Chaîne de fin de ligne
     */
    static protected final String eol = System.getProperty("line.separator");

    /**
     * Liste des lignes
     */
    protected List<LineInterface> lines;

    /**
     * Ligne actuellement active, par défaut la première ligne
     */
    protected LineInterface currentLine;

    /**
     * Numéro de ligne actuel
     */
    protected int currentLineNumber;

    /**
     * Constructeur par défaut, crée un document vierge (une ligne vide)
     */
    public Document() {
        lines = new ArrayList();
        currentLine = new Line();
        lines.add(currentLine);
        currentLineNumber = 0;
    }

    @Override
    public String toString() {
        String str = "";
        int n = lines.size();
        for (int i = 0; i < n; i++) {
            str += lines.get(i).toString();
            if (i < n - 1) {
                str += eol;
            }
        }
        return str;
    }

    @Override
    public void fromString(String text) {
        String[] new_lines = text.split(eol);
        int n_old = size();
        int n_new = new_lines.length;
        // Replace common lines
        for (int i = 0; i < Math.min(n_old, n_new); i++) {
            lines.get(i).setText(new_lines[i]);
        }
        // Remove extra lines from the old version
        for (int i = n_new; i < n_old; i++) {
            lines.remove(n_new);
        }
        // Add new lines for the new version
        for (int i = n_old; i < n_new; i++) {
            lines.add(i, new Line(new_lines[i]));
        }
        // Shift to the last line if currently out of the document
        if (currentLineNumber >= n_new) {
            selectLine(n_new - 1);
        }
    }

    @Override
    public LineInterface selectLine(int i) {
        i = Math.max(0, Math.min(size() - 1, i));
        currentLineNumber = i;
        currentLine = lines.get(i);
        return currentLine;
    }

    @Override
    public LineInterface insertLine() {
        lines.add(currentLineNumber + 1, new Line());
        return selectLine(currentLineNumber + 1);
    }

    @Override
    public LineInterface getLine() {
        return currentLine;
    }

    @Override
    public int getLineNumber() {
        return currentLineNumber;
    }

    @Override
    public LineInterface removeLine() {
        // Make sure there is at least one line in the document
        if (size() == 1) {
            lines.set(0, new Line());
        } else {
            lines.remove(currentLineNumber);
        }
        return selectLine(currentLineNumber - 1);
    }

    @Override
    public int size() {
        return lines.size();
    }

    @Override
    public int length() {
        int n = 0;
        for (LineInterface l : lines) {
            n += l.length();
        }
        return n;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.lines);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Document other = (Document) obj;
        if (!Objects.equals(this.lines, other.lines)) {
            return false;
        }
        return true;
    }

    /**
     * Méthode principale (test)
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        Document d = new Document();
        System.out.println("1/ Document = " + d.toString());
        System.out.println("1/ Size = " + d.size());
        System.out.println("1/ Length = " + d.length());

        LineInterface l = d.getLine();
        l.setText("toto titi");
        System.out.println("2/ Line = " + l.toString());
        System.out.println("2/ Document = " + d.toString());
        System.out.println("2/ Size = " + d.size());
        System.out.println("2/ Length = " + d.length());

        l = d.insertLine();
        l.setText("a b c d");
        System.out.println("3/ Line = " + l.toString());
        System.out.println("3/ Document = " + d.toString());
        System.out.println("3/ Size = " + d.size());
        System.out.println("3/ Length = " + d.length());

        l = d.selectLine(0);
        l.setText("1 2 3");
        System.out.println("4/ Document = " + d.toString());

        d.removeLine();
        System.out.println("5/ Document = " + d.toString());

        l = d.removeLine();
        l.setText("Alpha Beta Gamma");
        System.out.println("6/ Document = " + d.toString());

        d.fromString("a\nb\nc\nd\ne");
        System.out.println("7/ Document = " + d.toString());

        d.fromString("f\ng");
        System.out.println("8/ Document = " + d.toString());

        d.fromString("h\ni\nj");
        System.out.println("9/ Document = " + d.toString());
    }

}
