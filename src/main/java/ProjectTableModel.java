import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProjectTableModel extends AbstractTableModel {
    private List<Project> projects;

    ProjectTableModel(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public int getRowCount() {
        return projects.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
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
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 2:
                return ProjectStatus.class;
            case 3:
                return ProjectRating.class;
            default:
                return String.class;
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
            default:
                System.out.println("Something broke at set value");
        }
    }


}
