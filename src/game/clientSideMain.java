package game;

import javax.swing.UIManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;

public class clientSideMain {
    public static void main(String[] args) {

        FlatLightFlatIJTheme.install();

        ThemeDialog.showPopUpGuiForTheme(); 
        Controller controller = new Controller(10);
    }
}
    