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

import document.ObservableDocument;
import enshare.server.Server;
import enshare.server.ServerInterface;
import java.awt.Color;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;

/**
 *
 * @author Gwénolé Lecorvé
 */
public class ClientView extends javax.swing.JFrame implements Observer {

    /**
     * Contrôleur du client
     */
    protected AbstractClientController controller;

    /**
     * Classe interne pour l'affichage d'un message d'erreur
     */
    protected class ErrorDialog {

        protected Component parentComponent;
        protected String title;
        protected String content;

        /**
         * Constructeur
         *
         * @param _parentComponent Composant parent de la boîte de dialogue
         * @param _title Titre
         * @param _content Détail du message d'erreur
         */
        protected ErrorDialog(Component _parentComponent, String _title, String _content) {
            parentComponent = _parentComponent;
            title = _title;
            content = _content;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(parentComponent,
                            content,
                            title,
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            t.start();
        }
    }

    /**
     * Creates new form DocumentEditor
     *
     * @param _controller Le contrôleur associé au client
     */
    public ClientView(AbstractClientController _controller) {
        controller = _controller;
        controller.getObservedDocument().addObserver(this);
        initComponents();
        disableReading();
        disableWriting();
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbar = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        saveAsButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        lockToggleButton = new javax.swing.JToggleButton();
        scrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Aucun document ouvert");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        newButton.setText("Créer nouveau");
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        toolbar.add(newButton);

        loadButton.setText("Ouvrir");
        loadButton.setFocusable(false);
        loadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        toolbar.add(loadButton);

        closeButton.setText("Fermer");
        closeButton.setFocusable(false);
        closeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        toolbar.add(closeButton);

        saveAsButton.setText("Sauvegarder sous");
        saveAsButton.setFocusable(false);
        saveAsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveAsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });
        toolbar.add(saveAsButton);

        saveButton.setText("Sauvegarder");
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        toolbar.add(saveButton);

        lockToggleButton.setText("Verrouiller");
        lockToggleButton.setFocusable(false);
        lockToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lockToggleButton.setMaximumSize(new java.awt.Dimension(110, 27));
        lockToggleButton.setMinimumSize(new java.awt.Dimension(110, 27));
        lockToggleButton.setPreferredSize(new java.awt.Dimension(110, 27));
        lockToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lockToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockToggleButtonActionPerformed(evt);
            }
        });
        toolbar.add(lockToggleButton);

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        textArea.setEnabled(false);
        scrollPane.setViewportView(textArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Applique les actions à effectuer lorsque le bouton "Ouvrir" est cliqué
     *
     * @param evt L'évènement associé
     */
    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        // Mettre à jour le document d'après le contenu du textarea
        setDocumentFromTextArea();
        // Sélectionner un fichier
        List<String> list;
        String _fileName = "";
        try {
            list = controller.getDocumentList();
            DocumentSelectorView selector = new DocumentSelectorView(list);
            selector.setLocationRelativeTo(this);
            _fileName = selector.showAndSelect();
            if (_fileName != null) {
                controller.openDocument(_fileName);
                setTitle(_fileName);
                enableReading();
                disableWriting();
            }
        } catch (RemoteException ex) {
            printError("Erreur de l'ouverture de " + _fileName, ex.getMessage());
        } catch (FileNotFoundException ex) {
            printError("Erreur de l'ouverture de " + _fileName, ex.getMessage());
        } catch (MalformedURLException ex) {
            printError("Erreur de l'ouverture de " + _fileName, ex.getMessage());
        } catch (NotBoundException ex) {
            printError("Erreur de l'ouverture de " + _fileName, ex.getMessage());
        }
    }//GEN-LAST:event_loadButtonActionPerformed
    /**
     * Applique les actions à effectuer lorsque le bouton "Sauvegarder" est
     * cliqué
     *
     * @param evt L'évènement associé
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // Mettre à jour le document d'après le contenu du textarea
        setDocumentFromTextArea();
        // Si nom deja affecte, alors sauvegarde classique
        if (controller.hasFileName()) {
            try {
                controller.saveDocument();
            } catch (RemoteException ex) {
                printError("Erreur lors de la sauvergade", ex.getMessage());
            }
        } // Sinon sauvegarde sous un nom à preciser
        else {
            saveAsButtonActionPerformed(evt);
        }
    }//GEN-LAST:event_saveButtonActionPerformed
    /**
     * Applique les actions à effectuer lorsque le bouton
     * "Verrouiller/Déverrouiller" est cliqué
     *
     * @param evt L'évènement associé
     */
    private void lockToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockToggleButtonActionPerformed
        // Mettre à jour le document d'après le contenu du textarea
        setDocumentFromTextArea();
        lockToggleButton.setEnabled(false);
        // Non verrouillé -> verrouillé
        if (!controller.isLocked()) {
            lockToggleButton.setSelected(false);
            try {
                controller.tryLockDocument(controller.getUrl());

                while(!controller.isLocked());

                enableWriting();
            } catch (RemoteException ex) {
                printError("Erreur lors du verrouillage", ex.getMessage());
            } catch (MalformedURLException ex) {
                printError("Erreur lors du verrouillage", ex.getMessage());
            } catch (NotBoundException ex) {
                printError("Erreur lors du verrouillage", ex.getMessage());
            }
        } // Verrouillé -> non verrouillé
        else {
            try {
                controller.unlockDocument();
            } catch (RemoteException ex) {
                printError("Erreur lors du déverrouillage", ex.getMessage());
            } catch (MalformedURLException ex) {
                printError("Erreur lors du déverrouillage", ex.getMessage());
            } catch (NotBoundException ex) {
                printError("Erreur lors du déverrouillage", ex.getMessage());
            }
            disableWriting();
        }
        lockToggleButton.setEnabled(true);
    }//GEN-LAST:event_lockToggleButtonActionPerformed
    /**
     * Applique les actions à effectuer lorsque le bouton "Créer nouveau" est
     * cliqué
     *
     * @param evt L'évènement associé
     */
    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        // Mettre à jour le document d'après le contenu du textarea
        setDocumentFromTextArea();
        // Lire nom du fichier
        String _fileName = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez un nom de fichier:",
                "Créer nouveau...",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
        // Si nom OK alors sauvegarde
        if ((_fileName != null) && (_fileName.length() > 0)) {
            try {
                controller.newDocument(_fileName);
                setTitle(_fileName);
                enableReading();
                disableWriting();
            } catch (IOException ex) {
                printError("Erreur", ex.getMessage());
            }
            return;
        } // Sinon erreur
        else {
            printError("Erreur", "Nom de fichier invalide.");
        }
    }//GEN-LAST:event_newButtonActionPerformed
    /**
     * Applique les actions à effectuer lorsque le bouton "Sauvegarder sous" est
     * cliqué
     *
     * @param evt L'évènement associé
     */
    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        // Mettre à jour le document d'après le contenu du textarea
        setDocumentFromTextArea();
        // Lire nom du fichier
        String _fileName = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez un nom de fichier:",
                "Sauvegarder sous...",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
        // Si nom OK alors sauvegarde
        if ((_fileName != null) && (_fileName.length() > 0)) {
            try {
                controller.saveDocumentAs(_fileName);
                setTitle(_fileName);
            } catch (FileAlreadyExistsException ex) {
                printError("Erreur lors de la sauvegarde", ex.getMessage());
            } catch (IOException ex) {
                printError("Erreur lors de la sauvegarde", ex.getMessage());
            } catch (NotBoundException ex) {
                printError("Erreur lors de la sauvegarde", ex.getMessage());
            }
        } // Sinon erreur
        else {
            printError("Erreur", "Nom de fichier invalide.");
        }
    }//GEN-LAST:event_saveAsButtonActionPerformed
    /**
     * Applique les actions à effectuer lorsque le bouton "Fermer" est cliqué
     *
     * @param evt L'évènement associé
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // Mettre à jour le document d'après le contenu du textarea
        setDocumentFromTextArea();
        try {
            controller.closeDocument();
            disableWriting();
            disableReading();
        } catch (RemoteException ex) {
            printError("Erreur lors de la fermeture", ex.getMessage());
        } catch (FileNotFoundException ex) {
            printError("Erreur lors de la fermeture", ex.getMessage());
        } catch (MalformedURLException ex) {
            printError("Erreur lors de la fermeture", ex.getMessage());
        } catch (NotBoundException ex) {
            printError("Erreur lors de la fermeture", ex.getMessage());
        }
    }//GEN-LAST:event_closeButtonActionPerformed
    /**
     * Applique les actions à effectuer lors de la fermeture de la fenêtre
     *
     * @param evt L'évènement associé
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setDocumentFromTextArea();
        controller.finalize();
        System.exit(0);

    }//GEN-LAST:event_formWindowClosing

    /**
     * Active la lecture
     */
    public void enableReading() {
        textArea.setEnabled(true);
        textArea.setBackground(Color.WHITE);
        lockToggleButton.setEnabled(true);
        closeButton.setEnabled(true);
        saveAsButton.setEnabled(true);
        validate();
        repaint();
    }

    /**
     * Désactive la lecture
     */
    public void disableReading() {
        textArea.setEnabled(false);
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setText("");
        setTitle("Aucun document ouvert");
        lockToggleButton.setEnabled(false);
        closeButton.setEnabled(false);
        saveAsButton.setEnabled(false);
        validate();
        repaint();
    }

    /**
     * Active l'écriture
     */
    public void enableWriting() {
        textArea.setEditable(true);
        textArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        saveButton.setEnabled(true);
        lockToggleButton.setSelected(true);
        lockToggleButton.setText("Déverrouiller");
        validate();
        repaint();
    }

    /**
     * Désactive l'écriture
     */
    public void disableWriting() {
        textArea.setEditable(false);
        textArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        saveButton.setEnabled(false);
        lockToggleButton.setSelected(false);
        lockToggleButton.setText("Verrouiller");
        validate();
        repaint();
    }

    /**
     * Met à jour le document d'après le contenu de la zone de texte
     */
    public void setDocumentFromTextArea() {
        if (controller.hasDocument()) {
            controller.getObservedDocument().getDocument().fromString(textArea.getText());
        }
    }

    /**
     * Remplace le contenu de la zone de texte
     *
     * @param str Nouveau contenu
     */
    public void setTextArea(String str) {
        textArea.setText(str);
        validate();
        repaint();
    }

    /**
     * Change le titre de la fenêtre
     * <p>
     * Remarque : le titre commence toujours par "Enshare - "</p>
     *
     * @param title Nouveau titre
     */
    public void setTitle(String title) {
        super.setTitle("Enshare - " + title);
    }

    /**
     * Affiche une boîte de dialogue correspondant à une erreur
     *
     * @param title Titre de la boîte de dialogue
     * @param content Détail du message d'erreur
     */
    protected void printError(String title, String content) {
        new ErrorDialog(this, title, content);
    }

    /**
     * Rafraîchit l'affichage d'un objet observé (initialement le document)
     * <p>
     * Remarque : cette méthode est appelée par le document observé lorsqu'il
     * est modifié</p>
     *
     * @param observable Objet observable responsable de l'appel
     * @param o Paramètre optionnel fourni par l'objet observé
     */
    public void update(Observable observable, Object o) {
        ObservableDocument od = (ObservableDocument) observable;
        if (od.hasDocument()) {
            setTextArea(od.getDocument().toString());
            enableReading();
        } else {
            disableReading();
            disableWriting();
            printError("Erreur", "Aucun document accessible.\nLe serveur s'est peut-être interrompu.");
        }
        repaint();
    }

    /**
     * Méthode principale
     *
     * @param args Arguments de la ligne de commande
     * @throws RemoteException Si un problème en rapport avec RMI survient
     * @throws MalformedURLException Si l'URL du client est mal formée
     */
    public static void main(String args[]) throws RemoteException, MalformedURLException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        ServerInterface server = new Server("rmi://localhost:1099/enshare_server", ".");
        AbstractClientController controller = new CentralizedClientController("rmi://localhost:1099/enshare_client", server);
        ClientView editor = new ClientView(controller);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton closeButton;
    private javax.swing.JButton loadButton;
    private javax.swing.JToggleButton lockToggleButton;
    private javax.swing.JButton newButton;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JScrollPane scrollPane;
    protected javax.swing.JTextArea textArea;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

}
