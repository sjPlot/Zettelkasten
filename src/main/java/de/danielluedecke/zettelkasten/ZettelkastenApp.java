/*
 * Zettelkasten - nach Luhmann
 * Copyright (C) 2001-2015 by Daniel Lüdecke (http://www.danielluedecke.de)
 * 
 * Homepage: http://zettelkasten.danielluedecke.de
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation veröffentlicht, weitergeben
 * und/oder modifizieren, entweder gemäß Version 3 der Lizenz oder (wenn Sie möchten)
 * jeder späteren Version.
 * 
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein 
 * wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder 
 * der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der 
 * GNU General Public License.
 * 
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm 
 * erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 */

package de.danielluedecke.zettelkasten;

import de.danielluedecke.zettelkasten.database.Settings;
import de.danielluedecke.zettelkasten.database.AutoKorrektur;
import de.danielluedecke.zettelkasten.database.AcceleratorKeys;
import de.danielluedecke.zettelkasten.database.StenoData;
import de.danielluedecke.zettelkasten.database.Synonyms;
import de.danielluedecke.zettelkasten.database.TasksData;
import de.danielluedecke.zettelkasten.util.Constants;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;

/**
 * The main class of the application.
 */
public class ZettelkastenApp extends SingleFrameApplication {

    // we load the settings just after startup
    Settings settings;
    // and so we do with the user defined accelerator keys
    AcceleratorKeys accKeys;
    // and so we do with the user defined accelerator keys
    AutoKorrektur autoKorrekt;
    Synonyms synonyms;
    StenoData steno;
    TasksData taskdata;

    private String[] params;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        // prepare the class which stores the accelerator keys. this is needed here,
        // because the CSettings-class loads and saves this information
        accKeys = new AcceleratorKeys();
        // prepare the class which stores the auto-correction. this is needed here,
        // because the CSettings-class loads and saves this information
        autoKorrekt = new AutoKorrektur();
        // prepare the class which stores the synonyms. this is needed here,
        // because the CSettings-class loads and saves this information
        synonyms = new Synonyms();
        // prepare the class which stores the synonyms. this is needed here,
        // because the CSettings-class loads and saves this information
        steno = new StenoData();
        // prepare the class which stores information that are returned
        // from several tasks
        taskdata = new TasksData();
        // create new instance of the settings-class here,
        // so we can load and save settings directly on startup and just before
        // shutdown
        settings = new Settings(accKeys,autoKorrekt,synonyms,steno);
        // load settings
        settings.loadSettings();
        // retrieve the current default language
        String deflang = settings.getLanguage();
        // get country-coded
        String englishCountryCode = new Locale("en","","").getLanguage();
        String germanCountryCode = new Locale("de","","").getLanguage();
        String spanishCountryCode = new Locale("es","","").getLanguage();
        String portugueseCountryCode = new Locale("pt", "", "").getLanguage();
        // create locale-variable
        Locale newLocale = new Locale("en","GB");
        // check for default language and overwrite default-language-setting (which is UK)
        if (deflang.equals(englishCountryCode)) newLocale = new Locale("en","GB");
        if (deflang.equals(germanCountryCode)) newLocale = new Locale("de","DE");
        if (deflang.equals(spanishCountryCode)) newLocale = new Locale("es","ES");
        if (deflang.equals(portugueseCountryCode)) newLocale = new Locale("pt","BR");


        // set default locale
        Locale.setDefault(newLocale);
        // check parameters for filepath of loaded file
        for (String par : params) {
            // check whether one of the params is a path-description to
            // a zettelkasten-data-file. in this case, we would find the extension ".zkn3".
            if (par.toLowerCase().endsWith(Constants.ZKN_FILEEXTENSION)) {
                // create a dummy-file out of the parameter to check whether file exists or not
                File dummyfile = new File(par);
                // file file (param) exists, set is as new default filepath for the data file
                if (dummyfile.exists()) {
                    settings.setFilePath(dummyfile);
                    break;
                }
            }
        }
        // check parameters for entry-number of loaded file
        for (String par : params) {
            try {
                int initalZettellNr = Integer.parseInt(par);
                if (initalZettellNr>0) {
                    settings.setInitialParamZettel(initalZettellNr);
                    break;
                }
            }
            catch (NumberFormatException ex) {

            }
        }
        try {
            show(new ZettelkastenView(this, settings, accKeys, autoKorrekt,synonyms,steno,taskdata));
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    
    /**
     * A convenient static getter for the application instance.
     * @return the instance of ZettelkastenApp
     */
    public static ZettelkastenApp getApplication() {
        return Application.getInstance(ZettelkastenApp.class);
    }

    /**
     * Main method launching the application.
     * @param args
     */
    public static void main(String[] args) {
        launch(ZettelkastenApp.class, args);
    }

    @Override
    protected void initialize(String[] args) {
        this.params = args;
        super.initialize(args);
    }
}
