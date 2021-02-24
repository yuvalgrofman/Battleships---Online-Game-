package game;

import javax.swing.UIManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;

public class clientSideMain {
    public static void main(String[] args) {


        try {
            UIManager.setLookAndFeel( new FlatCobalt2IJTheme() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        Controller controller = new Controller(10);
    }
}
    