import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.io.File;
import java.util.List;


public class ProjectOverview {
    public  JPanel      panel;
    private JTable      tblProjects;
    MediaPlayer mediaPlayer;
    private JButton     btnOption;
    private JScrollPane scrollPane;
    boolean playing = false;
    private JButton btnTogglePlay;

    ProjectOverview(List<Project> projects) {
        ProjectTableModel model = new ProjectTableModel(projects);
        tblProjects.setModel(model);
        TableColumn column2 = tblProjects.getColumnModel().getColumn(2);
        TableColumn column3 = tblProjects.getColumnModel().getColumn(3);
        column2.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectStatus.values())));
        column3.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectRating.values())));

        new JFXPanel(); // Prepares the thing or whatnot

        btnOption.addActionListener(e -> {
        });

        btnTogglePlay.addActionListener(e -> {
            if (playing) {
                System.out.println("pause");
                mediaPlayer.pause();
                playing = false;
                btnTogglePlay.setText("Play Selected");
            } else {
                System.out.println("play");

                int  row          = tblProjects.getSelectedRow();
                File latestExport = model.getProjectAt(row).latestExport;

                if (latestExport != null) {
                    Media song = new Media(latestExport.toURI().toString());
                    mediaPlayer = new MediaPlayer(song);
                    mediaPlayer.play();
                }
                playing = true;
                btnTogglePlay.setText("Pause");
            }
        });
    }
}
