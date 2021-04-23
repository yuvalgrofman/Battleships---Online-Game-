import javax.swing.JOptionPane;

public class IPPanel {

    /**
     * Static method which opens an input dialog that gets an ip from the user
     * @return said ip
     */
    public static String getIP() {

        return JOptionPane.showInputDialog(null, "Server IP:", "00.000.000.000");

    }
    
    /**
     * Static method which opens an input dialog that gets a port and makes sure it valid  
     * @return said port as an int
     */
    public static int getPortNumber() {

        boolean inputValid = false;
        int portNumber = 11000;
        String textOnPanel = "Port number:";

        while (!inputValid) {

            try{
                String portNumberString = JOptionPane.showInputDialog(null, textOnPanel, "00000");
                portNumber = Integer.parseInt(portNumberString); 
                inputValid = true;

            }catch(Exception e){
                textOnPanel = "Invalid Port Number, please try again";
                inputValid = false;
            }
        }

        return portNumber;
    }
}
