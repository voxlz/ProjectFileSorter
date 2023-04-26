import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectzView {
    public JPanel mainPanel;
    private JTable table;
    private JButton btnOption; // need to be here
    private JScrollPane scrollPanel; // need to be here
    private JButton btnTogglePlay;
    private JButton btnOpen;
    public JButton btnRefresh;
    private ProjectTableModel model;

    ProjectzView() throws Exception {
        createUIComponents();
        updateModel();

        // Create main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Create table component
        scrollPanel = new JScrollPane(table);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Create buttons
        btnTogglePlay = new JButton("Play");
        btnOpen = new JButton("Open");
        btnRefresh = new JButton("Refresh");
        btnOption = new JButton("Option");

        // Add buttons to panel
        buttonPanel.add(btnTogglePlay);
        buttonPanel.add(btnOpen);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnOption);

        // Add components to main panel
        mainPanel.add(scrollPanel);
        mainPanel.add(buttonPanel);

        btnTogglePlay.addActionListener(e -> {
            Project project = getProject(); // Needs to be here
            try {
                Desktop.getDesktop().open(project.latestExport);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        btnOpen.addActionListener(e -> {
            Project project = getProject(); // Needs to be here
            try {
                Desktop.getDesktop().open(project.latestProject);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        btnRefresh.addActionListener(e -> {
            try {
                updateModel();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
    }

    private void updateModel() throws Exception {
        List<Project> projects = FileLoader.getProjects();
        model = new ProjectTableModel(projects);
        table.setModel(model);
        TableColumn column2 = table.getColumnModel().getColumn(2);
        TableColumn column3 = table.getColumnModel().getColumn(3);
        column2.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectStatus.values())));
        column3.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectRating.values())));

        // for (Project project : projects) {
        //     if (project.latestProject != null) {

        //         System.out.println(project.latestProject.getParent());
        //         for (File file : project.files) {
        //             if (!file.getAbsolutePath().equals(project.latestProject.getAbsolutePath())) {
        //                 System.out.println(file.getAbsolutePath());
        //                 // Join parent path and file name together
        //                 String path = project.latestProject.getParent() + "\\" + file.getName();
        //                 // Move file to path
        //                 file.renameTo(new File(path));
        //             }

        //         }
        //     }
        // }

        // Create a sorter and sort the table first based on production status, then
        // star rating, then name
        TableRowSorter<ProjectTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();

        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));

        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private Project getProject() {
        int row = table.getSelectedRow();
        int actualRow = table.convertRowIndexToModel(row);
        return model.getProjectAt(actualRow);
    }

    private Project getProject(int row) {
        int actualRow = table.convertRowIndexToModel(row);
        return model.getProjectAt(actualRow);
    }

    private void createUIComponents() {
        table = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                boolean isSelected = isCellSelected(row, column);
                if (isSelected) {
                    Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
                    Border inside = new EmptyBorder(0, 1, 0, 1);
                    Border highlight = new CompoundBorder(outside, inside);

                    JComponent jc = (JComponent) c;
                    jc.setBorder(highlight);
                }

                Project project = getProject(row);
                File latestExport = project.latestExport;
                if (latestExport != null) {
                    // TODO: Fix so that it's marked if latest version is exported or not
                    /*
                     * if (latestExport.getName().endsWith(project.version)) {
                     * c.setBackground(Color.CYAN);
                     * } else {
                     * c.setBackground(Color.lightGray);
                     * }
                     */
                    c.setBackground(Color.lightGray);
                } else {
                    c.setBackground(Color.white);
                }
                return c;
            }
        };
    }
}
