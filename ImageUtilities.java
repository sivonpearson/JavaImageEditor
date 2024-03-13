import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class ImageUtilities {
    public static final File SOURCE_DIR = new File("./SourceFiles");
    public static final File DESTINATION_DIR = new File("./ResultFiles");
    public static final String IMAGE_TYPE = "png";
    public static final FilenameFilter FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.matches(".*" + IMAGE_TYPE + "$");
        }
    };

    // Reads an image from the given file.  The file is typically located
    // in the source directory.
    public static BufferedImage readImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Writes an image to a file in the destination directory.  Before writing
    // the file, the file name has the tag appeneded to it after the file name
    // and before the file extention.
    public static void writeImage(BufferedImage image, File file, String tag) {
        if(!tag.matches("^\\w+$")) {
            throw new RuntimeException("Tag must be non-empty alphanumeric: " + tag);
        }

        if(!DESTINATION_DIR.isDirectory()) {
            throw new RuntimeException("Cannot find destination directory");
        }

        try {
            String fn = file.getName();
            String newFn = fn.substring(0,fn.length()-(IMAGE_TYPE.length()+1)) + "_" + tag + "." + IMAGE_TYPE;
            File destinationFile = new File(DESTINATION_DIR, newFn);
            ImageIO.write(image, IMAGE_TYPE, destinationFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Returns an array of file objects for the image files located in the 
    // source directory.
    public static File[] getImageFiles() {
        if(!SOURCE_DIR.isDirectory()) {
            throw new RuntimeException("Cannot find source directory");
        }
        return SOURCE_DIR.listFiles(FILTER);
    }
}
