import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ProjectOverview {
    public  JPanel      panel;
    private JTable      tblProjects;
    private MediaPlayer mediaPlayer;
    private JButton     btnOption;
    private JScrollPane scrollPane;
    boolean playing = false;
    private JButton btnTogglePlay;
    ProjectTableModel model;

    ProjectOverview(List<Project> projects) {
        model = new ProjectTableModel(projects);
        tblProjects.setModel(model);
        TableColumn column2 = tblProjects.getColumnModel().getColumn(2);
        TableColumn column3 = tblProjects.getColumnModel().getColumn(3);
        column2.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectStatus.values())));
        column3.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectRating.values())));

        new JFXPanel(); // Prepares for audio playback for some reason.

        btnOption.addActionListener(e -> {
        });

        btnTogglePlay.addActionListener(e -> {
            Project project = getProject();
            if (project.latestExport != null || playing) {
                if (playing) {
                    System.out.println("pause");
                    mediaPlayer.pause();
                    playing = false;
                    btnTogglePlay.setText("Play Selected");
                } else {
                    System.out.println("play");
                    Media song = new Media(project.latestExport.toURI().toString());
                    mediaPlayer = new MediaPlayer(song);
                    mediaPlayer.play();
                    playing = true;
                    btnTogglePlay.setText("Pause");
                }
            }
        });
    }

    private Project getProject() {
        int row       = tblProjects.getSelectedRow();
        int actualRow = tblProjects.convertRowIndexToModel(row);
        return model.getProjectAt(actualRow);
    }

    private Project getProject(int row) {
        int actualRow = tblProjects.convertRowIndexToModel(row);
        return model.getProjectAt(actualRow);
    }

    private void createUIComponents() {
        tblProjects = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                boolean isSelected = isCellSelected(row, column);
                if (isSelected) {
                    Border outside   = new MatteBorder(1, 0, 1, 0, Color.RED);
                    Border inside    = new EmptyBorder(0, 1, 0, 1);
                    Border highlight = new CompoundBorder(outside, inside);

                    JComponent jc = (JComponent) c;
                    jc.setBorder(highlight);

                }


                Project project      = getProject(row);
                File    latestExport = project.latestExport;
                if (latestExport != null) {
                    if (latestExport.getName().endsWith(project.version)) {
                        c.setBackground(Color.CYAN);
                    } else {
                        c.setBackground(Color.lightGray);
                    }
                } else {
                    c.setBackground(Color.white);
                }

                return c;
            }
        };
    }
}
