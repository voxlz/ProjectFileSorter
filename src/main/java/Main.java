import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("ProjectOverview");
        JPanel panel = new ProjectzView().mainPanel;
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Set window centered
        frame.pack(); // Automatically size the window to fit it's contents
        frame.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Backup());
    }

    static class Backup extends Thread {
        public void run() {
            DatabaseWriter.createBackup();
        }
    }
}