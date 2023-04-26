import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
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
import java.util.prefs.Preferences;

public class FileLoader {
    private static final String databasePath = System.getProperty("user.home") + File.separator + ".projectzDatabase";

    private static String projectExtension = "flp";
    private static File database;

    /**
     * Prompts user to select a folder and returns all the projects in said folder.
     * 
     * @throws Exception
     */
    public static List<Project> getProjects() throws Exception {
        Multimap<String, File> mapOfAllFiles = null;
        mapOfAllFiles = createMapOfFiles(getProjectsPath());
        mapOfAllFiles = removeEntriesWithoutProjectFile(mapOfAllFiles);
        return convertMapToProjects(mapOfAllFiles);
    }

    /**
     * Prompts user to select a projects folder and returns the projects path.
     * 
     * @return the projects directory
     */
    private static File getProjectsPath() {
        File projectsPath = loadFilePath();
        if (projectsPath == null) {
            JFileChooser fc = createFileChooser();
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                return fc.getSelectedFile();
            }
        }
        return projectsPath;
    }

    public static File getDatabase() {

        if (database != null) {
            return database;
        }

        File file = new File(databasePath);
        if (!file.exists()) {
            int result = JOptionPane.showConfirmDialog(null, "Can't find a database, do you want to create a new one?");
            if (result == JOptionPane.OK_OPTION) {
                try {
                    if (file.createNewFile()) {
                        Files.setAttribute(file.toPath(), "dos:hidden", true); // Hidden for Win
                    } else {
                        JOptionPane.showMessageDialog(null, "Could not create a new database");
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error occurred while creating a new database");
                    e.printStackTrace();
                }
            }
        }

        if (!file.canRead()) {
            file.setReadable(true);
        }

        if (!file.canWrite()) {
            file.setWritable(true);
        }

        return file;
    }

    /**
     * Converts the map of files to a list of projects.
     * 
     * @param map
     * @return
     * @throws IOException
     * @throws DatabindException
     * @throws StreamReadException
     */
    private static List<Project> convertMapToProjects(Multimap<String, File> map)
            throws StreamReadException, DatabindException, IOException {

        List<Project> projects = new ArrayList<>();

        // For every array of files for a given key
        for (String key : map.keySet()) {
            List<File> files = (List<File>) map.get(key);
            projects.add(new Project(key, files));
        }
        return projects;
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
        } else
            throw new Exception("Tried to sort an directory that no longer exist");
    }

    /**
     * Removes all entries in a map which do not have a least one DAW project file
     * associated
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
                .replaceAll("\\..+$", "") // Removes file endings
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

    /** Create and configures a file chooser. */
    private static JFileChooser createFileChooser() {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File("."));
        fc.setDialogTitle("Select the folder containing project files and audio exports");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(loadFilePath());
        return fc;
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public static File loadFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public static void saveFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());
        } else {
            prefs.remove("filePath");
        }
    }

    // Moves list of files to given folder
    private static void moveFilesToFolder(List<File> files, String strFolderPath) throws IOException {
        File folderPath = Paths.get(strFolderPath).toFile();
        folderPath.mkdir();

        for (File file : files) {
            Path dst = Paths.get(folderPath.getAbsolutePath(), file.getName());
            Path src = file.toPath();
            if (dst.toFile().exists() && !src.equals(dst)) {
                String dstStr = dst.toString();
                String fileExt = FilenameUtils.getExtension(dstStr);
                String newPath = FilenameUtils.removeExtension(dstStr) + " (Copy)." + fileExt;
                Files.move(src, new File(newPath).toPath(), StandardCopyOption.ATOMIC_MOVE);
            } else {
                Files.move(src, dst, StandardCopyOption.ATOMIC_MOVE);
            }
        }
    }
}
