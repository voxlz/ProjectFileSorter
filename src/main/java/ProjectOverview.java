import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProjectOverview extends JFrame {
    public  JPanel      panel;
    private JTable      tblProjects;
    private JButton     btnOpen;
    private JButton     btnOption;
    private JScrollPane scrollPane;

    public ProjectOverview(List<Project> projects) {
        updateTable(projects);

        btnOption.addActionListener(e -> System.out.println("hi"));
        btnOpen.addActionListener(e -> {

        });
    }

    public void updateTable(List<Project> projects) {
        AbstractTableModel model = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return projects.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return projects.get(rowIndex).name;
                    case 1:
                        return projects.get(rowIndex).version;
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
                    default:
                        return "";
                }
            }
        };
        tblProjects.setModel(model);
    }
}
