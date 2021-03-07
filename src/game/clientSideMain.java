package game;

import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;

public class clientSideMain {
    public static void main(String[] args) {
        try {

            FlatLightFlatIJTheme.install();

            ThemeDialog.showPopUpGuiForTheme(); 
        }catch (Exception e){
            View.unexpectedErrorHasOccurred("An unexpected error has occurred while trying to load the look and feel", "Error While Loading Look And Feel");
        }        

        try {
            Controller controller = new Controller(10,11000);
        } catch (Exception e) {
            View.unexpectedErrorHasOccurred("An Error has occurred while running the controller", "Error");
        }
    }
}
    