import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import java.util.List;


public class ProjectOverview extends JFrame implements TableModelListener {
    public  JPanel      panel;
    private JTable      tblProjects;
    private JButton     btnOpen;
    private JButton     btnOption;
    private JScrollPane scrollPane;

    ProjectOverview(List<Project> projects) {
        updateTable(projects);
        btnOption.addActionListener(e -> System.out.println("hi"));
        btnOpen.addActionListener(e -> System.out.println("bye"));
    }

    private void updateTable(List<Project> projects) {
        ProjectTableModel model = new ProjectTableModel(projects);
        tblProjects.setModel(model);
        TableColumn column2 = tblProjects.getColumnModel().getColumn(2);
        TableColumn column3 = tblProjects.getColumnModel().getColumn(3);
        column2.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectStatus.values())));
        column3.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectRating.values())));
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        System.out.println(e);
        System.out.println("hi someting changed");
    }
}
