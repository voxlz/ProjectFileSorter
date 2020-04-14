import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.stream.Collectors;

public class Project {
    private String       name;
    public Integer       version;
    public ProjectStatus status = ProjectStatus.Idea;
    public ProjectRating rating = ProjectRating.Zero;
    public Date          lastModified;
    public Date          created;

    File latestExport  = null;
    File latestProject = null;

    List<File> files = new ArrayList<>();

    // Required for json parsing
    public Project() {
        super();
    }

    public Project(String name, List<File> files) {
        this.name = name;
        this.files = files;

        List<File> projectFiles = removeExportFiles(files);
        List<File> exportFiles   = onlyExportFiles(files);

        latestExport    = getLatestExport(exportFiles);
        latestProject   = getLatestProject(projectFiles);
        version         = projectFiles.size();
        lastModified    = getLastModifiedDate(projectFiles);
        created         = getCreationDate(files);

        ObjectMapper  objectMapper = new ObjectMapper();
        List<Project> projects     = null;

        try {
            projects = objectMapper.readValue(FileLoader.database, new TypeReference<List<Project>>() {});
            Optional<Project> optionalProject   = projects.stream().filter(x -> x.name.equals(name)).findFirst();
            if (optionalProject.isPresent()) {
                Project project = optionalProject.get();
                status = project.status;
                rating = project.rating;
            }
        }
        catch (IOException e) { }
    }

    public Project(String name, List<File> files, String dataPath) {
        this(name, files);


    }

    private static List<File> onlyExportFiles(List<File> files) {
        return files
                .stream()
                .filter(file -> {
                    String extension = FilenameUtils.getExtension(file.getName());
                    return extension.equals("mp3") || extension.equals("wav");
                }).collect(Collectors.toList());
    }

    private static Date getCreationDate(List<File> files) {
        List<Date> createdList = new ArrayList<>();
        for (File file : files) {
            try {
                FileTime creationTime = (FileTime) Files.getAttribute(file.toPath(), "creationTime");
                createdList.add(new Date(creationTime.toMillis()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Date oldestCreationDate = createdList.stream().min(Comparator.naturalOrder()).get();

        List<Date> lastModifiedList = new ArrayList<>();
        for (File file : files) lastModifiedList.add(new Date(file.lastModified()));
        Date oldestModifiedDate = lastModifiedList.stream().min(Comparator.naturalOrder()).get();

        return oldestCreationDate.compareTo(oldestModifiedDate) <= 0 ? oldestCreationDate : oldestModifiedDate;
    }

    private static Date getLastModifiedDate(List<File> files) {
        List<Date> lastModifiedList = new ArrayList<>();
        for (File file : files) lastModifiedList.add(new Date(file.lastModified()));
        return lastModifiedList.stream().max(Comparator.naturalOrder()).get();
    }

    private static File getLatestProject(List<File> files) {
        files.sort(Comparator.comparingLong(File::lastModified).reversed());
        if (files.isEmpty()) return null;
        return files.get(0);
    }

    private static String getVersion(List<File> files, List<File> projectFiles) {
        String[] strs    = files.stream().map(File::getName).toArray(String[]::new);
        String   name    = StringUtils.getCommonPrefix(strs);
        String   version = projectFiles.get(0).getName().substring(name.length());
        return FileLoader.cleanUpName(version).replaceAll("_", "");
    }

    private static List<File> removeExportFiles(List<File> files) {
        return files
                .stream()
                .filter(file -> {
                    String extension = FilenameUtils.getExtension(file.getName());
                    return !extension.equals("mp3") && !extension.equals("wav");
                }).collect(Collectors.toList());
    }

    private File getLatestExport(List<File> audioFiles) {
        return audioFiles.stream()
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .findFirst()
                .orElse(null);
    }

    public void setName(String newName) {
        try {
            files.replaceAll(file -> {
                // TODO: Stupid bug with [] see progressive files
                File    dest   = new File(file.getAbsolutePath().replaceFirst(name, newName));
                boolean result = file.renameTo(dest);
                if (result) {
                    System.out.println(file.getName() + " got renamed to " + dest.getName());
                }
                return dest;
            });
            name = FileLoader.normaliseFileName(newName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Rename failed");
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
