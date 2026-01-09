package kg.projects.image.editor.imagekit.filesystem;

/*import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;*/

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LocalFileSystemImageManager implements FileSystemImageManager {
    private static final Set<String> SUPPORTED_FORMATS = Set.of("jpg", "jpeg", "png", "bmp");

    private static boolean isSupportedFormat(File file) {
        if (file == null) {
            return false;
        }
        for (String format : SUPPORTED_FORMATS) {
            if (file.getName().endsWith(format)) {
                return true;
            }
        }
        return false;
    }

    private static String getExtension(File imageFile) {
        return imageFile.getName().substring(imageFile.getName().lastIndexOf('.') + 1);
    }

    public LocalFileSystemImageManager() {
    }

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("imageFile cannot be null");
        }

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IOException("File does not exist or is not a regular file");
        }

        if (!isSupportedFormat(imageFile)) {
            throw new IOException("File has a unsupported image format");
        }

        BufferedImage originalImage = ImageIO.read(imageFile);
        if (originalImage == null) {
            throw new IOException("Failed to load image");
        }

        BufferedImage rgbImage = new BufferedImage(
                originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                rgbImage.setRGB(x, y, originalImage.getRGB(x, y));
            }
        }

        return rgbImage;
    }

    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }

        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("Directory does not exist or is not a directory" );
        }

        File[] files = imagesDirectory.listFiles();
        if (files == null) {
            throw new IOException("Failed to process files in directory");
        }

        List<BufferedImage> images = new ArrayList<>();

        for (File file : files) {
            if (file.isFile() && isSupportedFormat(file)) {
                BufferedImage img = ImageIO.read(file);
                if (img == null) {
                    throw new IOException("Failed to load image");
                }
                images.add(img);
            } else if  (file.isFile()) {
                throw new IOException("Directory contains unsupported file format");
            }
        }
        return images;
    }

    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (imageFile == null) {
            throw new IllegalArgumentException("ImageFile cannot be null");
        }

        if (imageFile.exists()) {
            throw new IOException("File already exists");
        }

        File parent = imageFile.getParentFile();
        if (parent != null && !parent.exists()) {
            throw new IOException("Parent directory does not exist");
        }

        if (!isSupportedFormat(imageFile)) {
            throw new IOException("Unsupported image format for saving");
        }

        boolean success = ImageIO.write(image, getExtension(imageFile), imageFile);
        if (!success) {
            throw new IOException("Failed to save image" );
        }
    }

    /*public static void main(String[] args) throws IOException {
        File inputFile = new File("week7/lab/resources/kitten.png");
        File outputFile = new File("week7/lab/resources/greyKitten.png");
        File outputFile2 = new File("week7/lab/resources/ghostKitten.png");
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        try {
            System.out.println("Attempting to load: " + inputFile.getAbsolutePath());

            BufferedImage img = manager.loadImage(inputFile);
            LuminosityGrayscale lg = new LuminosityGrayscale();
            img = lg.process(img);
            manager.saveImage(img, outputFile);

            BufferedImage img2 = manager.loadImage(inputFile);
            SobelEdgeDetection sed = new SobelEdgeDetection(lg);
            img2 = sed.process(img2);
            manager.saveImage(img2, outputFile2);

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }*/
}
