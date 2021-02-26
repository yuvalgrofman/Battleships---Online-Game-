package game;

import javax.swing.JOptionPane;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;


public class ThemeDialog {

    public static final String[] pizzas = { "Flat Light", "Flat Dark", "Darcula", "Cobalt2", "Gruvbox Dark Medium" };
    
    public static void showPopUpGuiForTheme() {

        String themePicked = (String) JOptionPane.showInputDialog(null, 
        "Pick your theme?",
        "Pick Theme",
        JOptionPane.QUESTION_MESSAGE, 
        null, 
        pizzas, 
        pizzas[0]);
        if (themePicked == null) {
            FlatHighContrastIJTheme.install();

        }else if (themePicked.equals("Flat Light")) {
            
            FlatLightLaf.install();
        }else if (themePicked.equals("Flat Dark")){

            FlatDarkLaf.install();
        } else if (themePicked.equals("Darcula")) {
            
            FlatDarculaLaf.install();
        } else if (themePicked.equals("Cobalt2")) {

            FlatCobalt2IJTheme.install();
        } else {
            
            FlatGruvboxDarkMediumIJTheme.install();
        }

    }
}
