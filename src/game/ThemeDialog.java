import javax.swing.JOptionPane;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;


public class ThemeDialog {

    public static final String[] themes = { "Flat Light", "Flat Dark", "Darcula", "Cobalt2", "Gruvbox Dark Medium" };
    
    public static void showPopUpGuiForTheme() {

        String themePicked = (String) JOptionPane.showInputDialog(null, 
        "Pick your theme?",
        "Pick Theme",
        JOptionPane.QUESTION_MESSAGE, 
        null, 
        themes, 
        themes[0]);
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
