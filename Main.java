import java.io.File;

class Main {
 //107, 195
 //303, 259
  public static void main(String[] args) {
    File[] files = ImageUtilities.getImageFiles();
    ImageFilter[] images = new ImageFilter[files.length];


    for(int i = 0; i < files.length; i++) {
        images[i] = new ImageFilter(files[i], "NEW");
        // images[i].applySepia();
        // images[i].split3x3(0, 60, 670);
        // images[i].write();

        images[i].split3x3(0, 0, 403-1);
        // images[i].write();
        // images[i].applySepia();
        // images[i].write();
    }
    
    // System.out.println(files[0].getName().substring(0, files[0].getName().indexOf(".")));


  }
}