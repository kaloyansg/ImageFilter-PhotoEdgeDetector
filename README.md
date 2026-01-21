# Photo Edge Detector

Библиотека за обработка на изображения, която може да работи с различни графични формати (JPEG, PNG, BMP) и прилага различни трансформации върху тях. За момента съдържа функционалност за конвертиране на цветно изображение в черно-бяло и възможност за откриване на ръбовете в изображение.

![Maserati Edge Detected](./resources/banner.png)

Библиотеката има два основни компонента:

- Алгоритми за обработка на изображения
- Мениджър за файловата система за зареждане и съхраняване на изображения.

## Основни интерфейси и класове

### ImageAlgorithm

Интерфейсът `ImageAlgorithm` представлява алгоритъм за обработка на изображения.

```java
package kg.projects.image.editor.imagekit.algorithm;

import java.awt.image.BufferedImage;

/**
 * Represents an algorithm that processes images.
 */
public interface ImageAlgorithm {

    /**
     * Applies the image processing algorithm to the given image.
     *
     * @param image the image to be processed
     * @return BufferedImage the processed image of type (TYPE_INT_RGB)
     * @throws IllegalArgumentException if the image is null
     */
    BufferedImage process(BufferedImage image);
}
```

### GrayscaleAlgorithm

Интерфейсът `GrayscaleAlgorithm` е маркерен интерфейс за алгоритми за конвертиране в черно-бяло изображение.

```java
package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import algorithm.kg.projects.imageEditor.imagekit.ImageAlgorithm;

public interface GrayscaleAlgorithm extends ImageAlgorithm {
}
```

### EdgeDetectionAlgorithm

Интерфейсът `EdgeDetectionAlgorithm` е друг маркерен интерфейс, този път за алгоритми за откриване на ръбове.

```java
package kg.projects.image.editor.imagekit.algorithm.detection;

import algorithm.kg.projects.imageEditor.imagekit.ImageAlgorithm;

public interface EdgeDetectionAlgorithm extends ImageAlgorithm {
}
```

### FileSystemImageManager

Интерфейсът `FileSystemImageManager` управлява зареждането и съхраняването на изображения от файловата система.

```java
package kg.projects.image.editor.imagekit.filesystem;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * An interface for loading images from the file system.
 * The supported image formats are JPEG, PNG, and BMP.
 */
public interface FileSystemImageManager {

    /**
     * Loads a single image from the given file path.
     *
     * @param imageFile the file containing the image.
     * @return the loaded BufferedImage.
     * @throws IllegalArgumentException if the file is null
     * @throws IOException              if the file does not exist, is not a regular file,
     *                                  or is not in one of the supported formats.
     */
    BufferedImage loadImage(File imageFile) throws IOException;

    /**
     * Loads all images from the specified directory.
     *
     * @param imagesDirectory the directory containing the images.
     * @return A list of BufferedImages representing the loaded images.
     * @throws IllegalArgumentException if the directory is null.
     * @throws IOException              if the directory does not exist, is not a directory,
     *                                  or contains files that are not in one of the supported formats.
     */
    List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException;

    /**
     * Saves the given image to the specified file path.
     *
     * @param image     the image to save.
     * @param imageFile the file to save the image to.
     * @throws IllegalArgumentException if the image or file is null.
     * @throws IOException              if the file already exists or the parent directory does not exist.
     */
    void saveImage(BufferedImage image, File imageFile) throws IOException;
}
```

### LocalFileSystemImageManager

Класът `LocalFileSystemImageManager` има публичен конструктор по подразбиране, имплементира интерфейса `FileSystemImageManager` и предоставя методи за зареждане и съхраняване на изображения.


### LuminosityGrayscale

Класът `LuminosityGrayscale` също има публичен конструктор по подразбиране, имплементира интерфейса `GrayscaleAlgorithm` и прилага черно-бяло конвертиране, използвайки [*Метода на осветеност (Luminosity method)*](https://www.johndcook.com/blog/2009/08/24/algorithms-convert-color-grayscale/).
В литературата се срещат и други стойности на коефициентите във формулата, но тук са използвани класическите: `0.21 R + 0.72 G + 0.07 B`.

### SobelEdgeDetection

Класът `SobelEdgeDetection` има публичен конструктор `SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm)`, имплементира интерфейса `EdgeDetectionAlgorithm` и прилага един от класическите алгоритми за откриване на ръбове в изображения, [*Оператор на Sobel*](https://en.wikipedia.org/wiki/Sobel_operator), известен също като *Оператор на Sobel–Feldman* и *Филтър на Sobel*.

За да се запознаете с алгоритъма, може да разгледате [тази статия](https://cse442-17f.github.io/Sobel-Laplacian-and-Canny-Edge-Detection-Algorithms/).

#### Детайлна спецификация на Sobel оператора

**Ядра на Sobel оператора:**

Хоризонтално ядро (Gx):

| | | |
|:---:|:---:|:---:|
| -1 | 0 | +1 |
| -2 | 0 | +2 |
| -1 | 0 | +1 |

Вертикално ядро (Gy):

| | | |
|:---:|:---:|:---:|
| -1 | -2 | -1 |
| 0 | 0 | 0 |
| +1 | +2 | +1 |

**Алгоритъм:**

Методът `process()` първо преобразува входното изображение в черно-бяло чрез подадения `grayscaleAlgorithm`, след което прилага Sobel оператора върху получената grayscale версия.

За всеки пиксел (x, y) в черно-бялото изображение:

1. **Прилагане на конволюция с хоризонталното ядро Gx:**
   За пиксел на координати (x, y), стойността Gx се изчислява като сума от произведенията на стойностите на пикселите в околността 3x3 и съответните коефициенти от ядрото Gx.

2. **Прилагане на конволюция с вертикалното ядро Gy:**
   Аналогично, стойността Gy се изчислява със съответните коефициенти от ядрото Gy.

3. **Изчисляване на магнитуда на градиента:**
   Използва се формулата: $$G = \sqrt{G_x^2 + G_y^2}$$

4. **Закръгляне и ограничаване резултата:**
   `int pixelValue = Math.min(255, (int) Math.round(G));`

5. **Запишете като RGB пиксел:**
   Тъй като резултатът е grayscale изображение, трите канала (R, G, B) имат еднаква стойност:
   `int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;`

**Обработка на граничните пиксели:**

За пикселите на ръбовете на изображението (x = 0, x = width - 1, y = 0, y = height - 1), където съседните пиксели излизат извън границите:
- Третираме липсващите пиксели като имащи стойност **0** (*zero padding*)
- Изходното изображение трябва да има същите размери като входното

**Резултат:**

Изходното изображение показва:
- **Ръбове в бяло** (стойност близо до 255) - области с голям градиент
- **Хомогенни области в черно** (стойност близо до 0) - области с малък градиент

### Пример за използване

Ето един прост пример, как може да се използва библиотеката:

```java
FileSystemImageManager fsImageManager = new LocalFileSystemImageManager();

BufferedImage image = fsImageManager.loadImage(new File("kitten.png"));

ImageAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
BufferedImage grayscaleImage = grayscaleAlgorithm.process(image);

ImageAlgorithm sobelEdgeDetection = new SobelEdgeDetection(grayscaleAlgorithm);
        BufferedImage edgeDetectedImage = sobelEdgeDetection.process(image);

fsImageManager.saveImage(grayscaleImage, new File("kitten-grayscale.png"));
fsImageManager.saveImage(edgeDetectedImage, new File("kitten-edge-detected.png"));
```

## Пакети

Спазвайте имената на пакетите на всички по-горе описани класове, тъй като в противен случай решението ви няма да може да бъде тествано от грейдъра.

```
src
└── kg.projects.image.editor.imagekit
    ├── algorithm
    │   ├── detection
    │   │   ├── EdgeDetectionAlgorithm.java
    │   │   └── SobelEdgeDetection.java
    │   ├── grayscale
    │   │   ├── GrayscaleAlgorithm.java
    │   │   └── LuminosityGrayscale.java
    │   ├── ImageAlgorithm.java
    │   └── (...)
    ├── filesystem
    │   ├── FileSystemImageManager.java
    │   ├── LocalFileSystemImageManager.java
    │   └── (...)
    └── (...)

test
└── kg.projects.image.editor.imagekit
     └── (...)
```
