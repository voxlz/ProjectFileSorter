import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Multimap;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ViewHandlerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createMapOfFiles() {
        try {
            File folder = new File("C:\\ProjectSorterTest");
            if (folder.exists()) {
                FileUtils.deleteDirectory(folder);
            }

            folder.mkdir();
            new File("C:\\ProjectSorterTest\\Project").createNewFile();
            new File("C:\\ProjectSorterTest\\Project_2").createNewFile();
            new File("C:\\ProjectSorterTest\\HappyLife").createNewFile();

            Multimap<String, File> map = ViewHandler.createMapOfFiles(new File("C:\\ProjectSorterTest"));

            assertTrue(map.size() == 3);
            assertTrue(map.keySet().size() == 2);
            assertTrue(map.get("Project").size() == 2);

            new File("C:\\ProjectSorterTest\\Folder").mkdir();
            new File("C:\\ProjectSorterTest\\Folder\\Poop").createNewFile();
            new File("C:\\ProjectSorterTest\\Folder\\HappyLife_3").createNewFile();
            new File("C:\\ProjectSorterTest\\Folder\\HappyLife_2").createNewFile();

            map = ViewHandler.createMapOfFiles(new File("C:\\ProjectSorterTest"));

            assertTrue(map.size() == 6);
            assertTrue(map.keySet().size() == 3);
            assertTrue(map.get("Project").size() == 2);
            assertTrue(map.get("HappyLife").size() == 3);
            assertTrue(map.get("Poop").size() == 1);

            new File("C:\\ProjectSorterTest").deleteOnExit();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void normaliseFileName() {
        String s = "file name v2 (before horrible)_3";
        assertEquals(ViewHandler.normaliseFileName(s), "file name v2");

        String s2 = "something-hell0(no balls)-byby(still no)_23v";
        assertEquals(ViewHandler.normaliseFileName(s2), "something-hell0-byby");

        String s3 = "no(please (oh god no))";
        assertEquals(ViewHandler.normaliseFileName(s3), "no");
    }
}