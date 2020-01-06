import java.util.Date;

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
}
