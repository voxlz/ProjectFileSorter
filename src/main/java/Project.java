public class Project {
    String        name;
    String        version;
    ProjectStatus status;
    ProjectRating rating;

    public Project(String name, String version, ProjectStatus status, ProjectRating rating) {
        this.name = name;
        this.version = version;
        this.status = status;
        this.rating = rating;
    }
}
