package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Util {
    public static void main(String[] args) throws IOException {
        String zipFilePath = "/Users/ashutosh/Documents/NEU/SEM2/Cloud/function-source.zip"; // The path for the output ZIP file.
        // Paths to the src folder and pom.xml file.
        String srcFolderPath = "src";
        String pomFilePath = "pom.xml";

        FileOutputStream fos = new FileOutputStream(zipFilePath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        // Zip the src directory.
        File srcDir = new File(srcFolderPath);
        zipFile(srcDir, srcDir.getName(), zipOut);

        // Zip the pom.xml file.
        File pomFile = new File(pomFilePath);
        zipFile(pomFile, pomFile.getName(), zipOut);

        System.out.println("Zipped to " + zipFilePath);

        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
