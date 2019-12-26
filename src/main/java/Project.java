public class Project {
    String        name;
    String        version;
    boolean       released;
    ProjectStatus status;

    public Project(String name, String version, ProjectStatus status, boolean released) {
        this.name = name;
        this.version = version;
        this.released = released;
        this.status = status;
    }
}
