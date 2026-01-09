package kg.projects.image.editor.imagekit.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalFileSystemImageManagerTest {
    private final LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                testImage.setRGB(x, y, x+y);
            }
        }
    }

    @Test
    void testLoadImageNull(){
        assertThrows(IllegalArgumentException.class, () -> manager.loadImage(null));
    }

    @Test
    void testLoadImageNoFile() throws IOException {
        File testFile = new File("noFile.jpg");
        if (testFile.exists() && testFile.isFile()) {
            assertNotNull(manager.loadImage(testFile));
        }
        assertThrows(IOException.class, () -> manager.loadImage(testFile));
    }

    @Test
    void testLoadImageWrongFormat() throws IOException {
        try {
            File testFile = new File("test.text");
            //testFile.delete();
            if (!testFile.createNewFile()){
                throw new IOException("Could not create a test file");
            }
            assertThrows(IOException.class, () -> manager.loadImage(testFile));
            testFile.delete();
        }
        catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    @Test
    void testLoadImageValidImage() throws IOException {
        File testFile = new File("test.png");
        //testFile.delete();
        if (testFile.exists() && testFile.isFile()) {
            throw new IOException("Test file already exists");
        }

        if (!ImageIO.write(testImage, "png", testFile)){
            throw new IOException("Could not write an image to test file");
        }

        assertNotNull(manager.loadImage(testFile));
        BufferedImage testImage2 = manager.loadImage(testFile);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                assertEquals(testImage.getRGB(x, y), testImage2.getRGB(x, y));
            }
        }

        testFile.delete();
    }

    @Test
    void testLoadImagesFromDirectoryNull() throws IOException{
        assertThrows(IllegalArgumentException.class, () -> manager.loadImagesFromDirectory(null));
    }

    @Test
    void testLoadImagesFromDirectoryNoDir() throws IOException{
        File dir = new File("testDir");
        if (dir.exists() && dir.isDirectory()) {
            throw new IOException("Test directory already exists");
        }
        assertThrows(IOException.class, () -> manager.loadImagesFromDirectory(dir));
    }

    /*@Test
    void clean() throws IOException{
        File dir = new File("testDir");
        File testFile1 = new File("testDir/test1.png");
        File testFile2 = new File("testDir/test2.png");
        testFile1.delete();
        testFile2.delete();
        Files.deleteIfExists(dir.toPath());
    }*/

    @Test
    void testLoadImagesFromDirectoryValidDir() throws IOException {
        File dir = new File("testDir");
        if (dir.exists() && dir.isDirectory()) {
            throw new IOException("Test directory already exists");
        }
        if (!dir.mkdirs()){
            throw new IOException("Could not create test directory");
        }
        File testFile1 = Path.of("testDir", "test1.png").toFile();
        File testFile2 = Path.of("testDir", "test2.png").toFile();
        //File testFile1 = new File("testDir/test1.png");
        //File testFile2 = new File("testDir/test2.png");
        if (!ImageIO.write(testImage, "png", testFile1)){
            Files.deleteIfExists(dir.toPath());
            throw new IOException("Could not write an image to test file");
        }
        if (!ImageIO.write(testImage, "png", testFile2)){
            testFile1.delete();
            Files.deleteIfExists(dir.toPath());
            throw new IOException("Could not write an image to test file");
        }

        List<BufferedImage> images = manager.loadImagesFromDirectory(dir);
        for (BufferedImage image : images) {
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    assertEquals(testImage.getRGB(x, y), image.getRGB(x, y));
                }
            }
        }
        testFile1.delete();
        testFile2.delete();
        Files.deleteIfExists(dir.toPath());
    }

    @Test
    void testSaveImageNull() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> manager.saveImage(testImage, null));
        File testFile = new File("test.png");
        assertThrows(IllegalArgumentException.class, () -> manager.saveImage(null, testFile));
    }

    @Test
    void testSaveImageAlreadyExists() throws IOException {
        File testFile = new File("test.png");
        if (testFile.exists() && testFile.isFile()) {
            assertThrows(IOException.class, () -> manager.saveImage(testImage, testFile));
        } else if (testFile.exists()) {
            throw new IOException("Test file already exists and it is not regular file");
        } else {
            if (!testFile.createNewFile()){
                throw new IOException("Could not create test file");
            }
            assertThrows(IOException.class, () -> manager.saveImage(testImage, testFile));
            testFile.delete();
        }
    }

    @Test
    void testSaveImageWrongFormat() throws IOException {
        File testFile = new File("test.fake");
        assertThrows(IOException.class, () -> manager.saveImage(testImage, testFile));
    }

    @Test
    void testSaveImageNoParentDir() throws IOException {
        File testFile = Path.of("there", "is", "no", "way", "it", "has", "directory", "test.png")
                .toFile();
        //File testFile = new File("there/is/no/way/it/has/such/a/directory/test.png");
        assertThrows(IOException.class, () -> manager.saveImage(testImage, testFile));
    }

    @Test
    void testSaveImageValidImage() throws IOException {
        File testFile = new File("test.png");
        //testFile.delete();
        if (testFile.exists() && testFile.isFile()) {
            throw new IOException("Test file already exists");
        }
        if (!ImageIO.write(testImage, "png", testFile)) {
            throw new IOException("Failed to save test image" );
        }

        BufferedImage testImage2 = manager.loadImage(testFile);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                assertEquals(testImage.getRGB(x, y), testImage2.getRGB(x, y));
            }
        }

        testFile.delete();
    }
}
