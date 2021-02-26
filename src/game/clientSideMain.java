package game;

import com.formdev.flatlaf.intellijthemes.*;

public class clientSideMain {
    public static void main(String[] args) {

        FlatLightFlatIJTheme.install();

        ThemeDialog.showPopUpGuiForTheme(); 
        Controller controller = new Controller(10);
    }
}
    