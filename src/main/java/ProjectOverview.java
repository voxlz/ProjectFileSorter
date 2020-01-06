import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class ProjectOverview {
    public  JPanel      panel;
    private JTable      tblProjects;
    private JButton     btnOpen;
    private JButton     btnOption;
    private JScrollPane scrollPane;

    ProjectOverview(List<Project> projects) {
        ProjectTableModel model = new ProjectTableModel(projects);
        tblProjects.setModel(model);
        TableColumn column2 = tblProjects.getColumnModel().getColumn(2);
        TableColumn column3 = tblProjects.getColumnModel().getColumn(3);
        column2.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectStatus.values())));
        column3.setCellEditor(new DefaultCellEditor(new JComboBox<>(ProjectRating.values())));

        new JFXPanel(); // Prepares the thing or whatnot
        AtomicReference<MediaPlayer> mediaPlayer = new AtomicReference<>(new MediaPlayer(new Media("")));

        btnOption.addActionListener(e -> {
            System.out.println("hi");
        });

        btnOpen.addActionListener(e -> {
            new JFXPanel(); // Prepares the thing or whatnot

            System.out.println("bye");
            int  row          = tblProjects.getSelectedRow();
            File latestExport = model.getProjectAt(row).latestExport;

            if (latestExport != null) {
                Media song = new Media(latestExport.toURI().toString());
                mediaPlayer.set(new MediaPlayer(song));
                mediaPlayer.get().play();
            }
        });
    }

}
