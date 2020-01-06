import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewHandler {

    private static String projectExtension = "flp";

    public static void main(String[] args) {
        JFileChooser fileChooser = createFileChooser();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) try {
            Multimap<String, File> mapOfAllFiles = createMapOfFiles(fileChooser.getSelectedFile());
            Multimap<String, File> mapOfFiles = removeEntriesWithoutProjectFile(mapOfAllFiles);
            List<Project> projects = extractMapToList(mapOfFiles);
            createWindow(projects);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<Project> extractMapToList(Multimap<String, File> map) {
        List<Project> projects = new ArrayList<>();

        // For every array of files for a given key
        for (String key : map.keySet()) {
            List<File> files = (List<File>) map.get(key);
            projects.add(new Project(files, key));
        }
        return projects;
    }

    // Create an window with options
    private static void createWindow(List<Project> projects) {
        JFrame frame = new JFrame("ProjectOverview");
        frame.setContentPane(new ProjectOverview(projects).panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);                      // Set window centered
        frame.pack();                                           // Automatically size the window to fit it's contents
        frame.setVisible(true);
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

    /**
     * Removes all entries in a map which do not have a least one DAW project file associated
     * with them.
     *
     * @param map the map to check
     * @return the new map
     */
    private static Multimap<String, File> removeEntriesWithoutProjectFile(Multimap<String, File> map) {
        Set<String> keys = new HashSet<>(map.keySet()); // Make a copy of keys
        for (String key : keys) {
            long count = map.get(key)
                    .stream()
                    .filter(f -> {
                        String extension = FilenameUtils.getExtension(f.getName());
                        return extension.equals(projectExtension);
                    })
                    .count();
            if (count < 1) {
                map.removeAll(key);
            }
        }
        return map;
    }

    static String cleanUpName(String name) {
        return name
                .replaceAll("\\..+$", "") //Removes file endings
                .trim();
    }

    // Normalizes the file name to get the "pure" project name
    static String normaliseFileName(String name) {
        return cleanUpName(name)
                .replaceAll("_.*", "") // Removes
                .replaceAll("\\(.*?\\)", "") // Remove parenthesis
                .replaceAll("v\\d+\\s*$", "")
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

    // Moves list of files to given folder
    private static void moveFilesToFolder(List<File> files, String strFolderPath) throws IOException {
        File folderPath = Paths.get(strFolderPath).toFile();
        //noinspection ResultOfMethodCallIgnored
        folderPath.mkdir();

        for (File file : files) {
            Path dst = Paths.get(folderPath.getAbsolutePath(), file.getName());
            Path src = file.toPath();
            if (dst.toFile().exists() && !src.equals(dst)) {
                String dstStr  = dst.toString();
                String fileExt = FilenameUtils.getExtension(dstStr);
                String newPath = FilenameUtils.removeExtension(dstStr) + " (Copy)." + fileExt;
                Files.move(src, new File(newPath).toPath(), StandardCopyOption.ATOMIC_MOVE);
            } else {
                Files.move(src, dst, StandardCopyOption.ATOMIC_MOVE);
            }
        }
    }
}