import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Project {
    String        name;
    String        version;
    ProjectStatus status;
    ProjectRating rating;
    Date          lastModified;
    Date          created;

    public Project(String name, String version, ProjectStatus status, ProjectRating rating, Date lastModified, Date created) {
        this.name = name;
        this.version = version;
        this.status = status;
        this.rating = rating;
        this.lastModified = lastModified;
        this.created = created;
    }

    public Project(List<File> files, String key) {
        List<File> noAudioFiles = removeAudioFiles(files);
        Date       lastModified = getLastModifiedDate(noAudioFiles);
        Date       created      = getCreationDate(noAudioFiles);

        String ver = "1";
        if (noAudioFiles.size() > 1) {
            ver = getVersion(files, noAudioFiles);
        }

        this.name = key;
        this.version = ver;
        this.status = ProjectStatus.Prototype;
        this.rating = ProjectRating.Zero;
        this.lastModified = lastModified;
        this.created = created;
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
        return createdList.stream().min(Comparator.naturalOrder()).get();
    }

    private static Date getLastModifiedDate(List<File> files) {
        List<Date> lastModifiedList = new ArrayList<>();
        for (File file : files) lastModifiedList.add(new Date(file.lastModified()));
        return lastModifiedList.stream().max(Comparator.naturalOrder()).get();
    }

    private static String getVersion(List<File> files, List<File> projectFiles) {
        String[] strs    = files.stream().map(File::getName).toArray(String[]::new);
        String   name    = StringUtils.getCommonPrefix(strs);
        String   version = projectFiles.get(0).getName().substring(name.length());
        return ViewHandler.cleanUpName(version).replaceAll("_", "");
    }

    private static List<File> removeAudioFiles(List<File> files) {
        return files
                .stream()
                .filter(file -> {
                    String extension = FilenameUtils.getExtension(file.getName());
                    return !extension.equals("mp3") && !extension.equals("wav");
                }).collect(Collectors.toList());
    }
}
