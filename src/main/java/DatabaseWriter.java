import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DatabaseWriter {
    private static final int backupAmount = 100;
    private static final int changesPerBackup = 10;
    static int changeCounter = 0;

    public static void saveToDatabase(List<Project> projectList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File database = FileLoader.getDatabase();
            System.out.println(database.canWrite());
            objectMapper.writeValue(database, projectList);
            changeCounter += 1;
            if (changeCounter >= changesPerBackup) {
                createBackup();
                changeCounter = 0;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not write to database. Error: " + e);
            e.printStackTrace();
        }
    }

    public static void createBackup() {
        String backupFolderPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator
                + "Projectz Backups";
        String backupPath = backupFolderPath + File.separator + new Date().toString().replaceAll(":", "-");
        try {
            FileUtils.copyFile(FileLoader.getDatabase(), new File(backupPath));
            System.out.println("Saved backup to " + backupPath);
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(null, "Failed to create backup");
            e.printStackTrace();
        }

        File[] files = new File(backupFolderPath).listFiles();
        while (files.length >= backupAmount) {
            File file = Arrays.stream(files).sorted(Comparator.comparingLong(File::lastModified)).findFirst().get();
            file.delete();
            files = new File(backupFolderPath).listFiles();
        }
    }
}