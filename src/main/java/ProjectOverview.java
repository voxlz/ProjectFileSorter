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
        btnOpen.addActionListener(e -> {

        });
    }

    private void updateTable(List<Project> projects) {
        ProjectTableModel model = new ProjectTableModel(projects);
        model.addTableModelListener(this);
        tblProjects.setModel(model);
        TableColumn column = tblProjects.getColumnModel().getColumn(2);
        column.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectStatus.values())));
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        System.out.println(e);
        System.out.println("hi someting changed");
    }
}
