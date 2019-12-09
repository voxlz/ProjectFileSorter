import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewHandler {
    private static File      sort_directory;
    private static JTextArea errorArea;

    public static void main(String[] args) {
        createWindow();
    }

    // Create an window with options
    private static JFrame createWindow() {
        JFrame frame = new JFrame("File Sorter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JFileChooser fc = createFileChooser();

        Button runBtn = new Button("Select Folder");
        runBtn.setPreferredSize(new Dimension(100, 40));
        runBtn.addActionListener(e -> {
            if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                sort_directory = fc.getSelectedFile();
            }
        });

        Button startBtn = new Button("Sort Files");
        startBtn.setPreferredSize(new Dimension(100, 40));
        startBtn.addActionListener(e -> run());

        errorArea = new JTextArea("Error text");
        errorArea.setPreferredSize(new Dimension(100, 40));

        frame.getContentPane().add(BorderLayout.WEST, startBtn);
        frame.add(BorderLayout.EAST, runBtn);
        frame.add(BorderLayout.NORTH, errorArea);
        frame.setLocationRelativeTo(null); // Set window centered
        frame.pack(); // Automatically size the window to fit it's contents
        frame.setVisible(true);

        return frame;
    }

    private static void run() {
        try {
            Multimap<String, File> filesMap = createMapOfFiles(sort_directory);

            List<File> latestVersions       = new ArrayList<>();
            List<File> olderVersions        = new ArrayList<>();
            List<File> latestExportVersions = new ArrayList<>();
            List<File> olderExportVersions  = new ArrayList<>();

            // For every array of files for a given key
            for (String key : filesMap.keySet()) {

                List<File> files = new ArrayList<>(filesMap.get(key));
                files.sort(Comparator.comparing(File::lastModified).reversed());

                boolean foundLatVer = false;
                boolean foundLatExp = false;

                for (File file : files) {
                    if (FilenameUtils.getExtension(file.getName()).equals("flp")) {
                        if (!foundLatVer) {
                            latestVersions.add(file);
                            foundLatVer = true;
                        } else {
                            olderVersions.add(file);
                        }
                    } else {
                        if (!foundLatExp) {
                            latestExportVersions.add(file);
                            foundLatExp = true;
                        } else {
                            olderExportVersions.add(file);
                        }
                    }
                }
            }

            System.out.println("Files sorted");

            moveFilesToFolder(latestVersions, "Project Files");
            moveFilesToFolder(olderVersions, "Project Files (Old)");
            moveFilesToFolder(latestExportVersions, "Exports");
            moveFilesToFolder(olderExportVersions, "Exports (Old)");

            System.out.println("Success!");
        } catch (Exception e) {
            errorArea.setText(e.getMessage());
        }
    }

    private static void moveFilesToFolder(List<File> files, String folderName) throws IOException {
        File folderPath = Paths.get(sort_directory.getAbsolutePath(), "/" + folderName).toFile();
        folderPath.mkdir();

        for (File file : files) {
            Files.move(file.toPath(), Paths.get(folderPath.getAbsolutePath(), file.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Create a map of all files based of their normalised name
    static Multimap<String, File> createMapOfFiles(File folder) throws Exception {
        Multimap<String, File> map = ArrayListMultimap.create();
        return createMapOfFiles(folder, map);
    }

    // Create a map of all files based of their normalised name, recursively
    private static Multimap<String, File> createMapOfFiles(File folder, Multimap<String, File> map) throws Exception {
        if (folder.exists()) {
            File[] allFiles = folder.listFiles();
            assert allFiles != null;
            for (File file : allFiles) {
                if (file.isDirectory()) {
                    createMapOfFiles(file, map);
                } else {
                    String name = normaliseFileName(file.getName());
                    map.put(name, file);
                }
            }
            return map;
        } else throw new Exception("Tried to sort an directory that no longer exist");
    }

    static String normaliseFileName(String name) {
        return name
                .replaceAll("\\s*\\([^)]*\\)\\s*", "")
                .replaceAll("_.*", "")
                .replaceAll("\\..*", "")
                .replaceAll("v\\d", "")
                .replaceAll("\\d$", "")
                .trim();
    }

    //Create a file chooser
    private static JFileChooser createFileChooser() {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File("."));
        fc.setDialogTitle("Select the folder containing project files and audio exports");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File("D:/Music Production/FL Projects"));
        return fc;
    }
}