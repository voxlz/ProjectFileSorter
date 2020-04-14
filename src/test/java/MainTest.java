import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

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

            Multimap<String, File> map = FileLoader.createMapOfFiles(new File("C:\\ProjectSorterTest"));

            assertTrue(map.size() == 3);
            assertTrue(map.keySet().size() == 2);
            assertTrue(map.get("Project").size() == 2);

            new File("C:\\ProjectSorterTest\\Folder").mkdir();
            new File("C:\\ProjectSorterTest\\Folder\\Poop").createNewFile();
            new File("C:\\ProjectSorterTest\\Folder\\HappyLife_3").createNewFile();
            new File("C:\\ProjectSorterTest\\Folder\\HappyLife_2").createNewFile();

            map = FileLoader.createMapOfFiles(new File("C:\\ProjectSorterTest"));

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
        String s2 = "something-hell0(no balls)-byby(still no)_23v";
        assertEquals("something-hell0-byby", FileLoader.normaliseFileName(s2));

        String s = "file name v2 (before horrible)_3";
        assertEquals("file name", FileLoader.normaliseFileName(s));

        String s3 = "12-13-1223";
        assertEquals("12-13-1223", FileLoader.normaliseFileName(s3));
    }
}