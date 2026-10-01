package io.github.joxebus.mockapi.util

final class FileUtil {

    static String getTextFromFile(String filename) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader()
        File file = new File(classLoader.getResource(filename).getFile());
        file.text
    }

    static void cleanFolder(String folderLocation) {
        File folder = new File(folderLocation)
        folder.eachFileRecurse {file -> file.delete() }

    }
}
