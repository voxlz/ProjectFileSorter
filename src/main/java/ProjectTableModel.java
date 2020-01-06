import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.List;

public class ProjectTableModel extends AbstractTableModel {
    private List<Project> projects;

    ProjectTableModel(List<Project> projects) {
        this.projects = projects;
    }

    public Project getProjectAt(int rowIndex) {
        return projects.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return projects.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Project project = projects.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return project.name;
            case 1:
                return project.version;
            case 2:
                return project.status;
            case 3:
                return project.rating;
            case 4:
                return project.lastModified;
            case 5:
                return project.created;
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Name";
            case 1:
                return "Version";
            case 2:
                return "Status";
            case 3:
                return "Rating";
            case 4:
                return "Last Modified";
            case 5:
                return "Created";
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 2:
            case 3:
                return true;
            default:
                return false;
        }
    }

    // Seems like this affects sorting, and display to some sort
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 4:
            case 5:
                return Date.class;
            default:
                return String.class;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Project project = projects.get(rowIndex);
        switch (columnIndex) {
            case 0:
                project.name = (String) aValue;
                break;
            case 1:
                project.version = (String) aValue;
                break;
            case 2:
                project.status = (ProjectStatus) aValue;
                break;
            case 3:
                project.rating = (ProjectRating) aValue;
                break;
            case 4:
                project.lastModified = (Date) aValue;
                break;
            case 5:
                project.created = (Date) aValue;
                break;
            default:
                System.out.println("Something broke at set value");
        }
    }


}
