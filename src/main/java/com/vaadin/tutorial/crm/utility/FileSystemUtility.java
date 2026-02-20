package com.vaadin.tutorial.crm.utility;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.*;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import oshi.util.FileUtil;

@Service
public class FileSystemUtility {

    public boolean isFileLocked(File p_fi) {
        boolean bLocked = false;
        try (RandomAccessFile fis = new RandomAccessFile(p_fi, "rw")) {
            FileLock lck = fis.getChannel().lock();
            lck.release();
        } catch (Exception ex) {
            bLocked = true;
        }
        if (bLocked)
            return bLocked;
        String parent = p_fi.getParent();
        String rnd = UUID.randomUUID().toString();
        File newName = new File(parent + "/" + rnd);
        if (p_fi.renameTo(newName)) {
            newName.renameTo(p_fi);
        } else
            bLocked = true;
        return bLocked;
    }

    public boolean moveFile(String src, String dest) {
        Path result = null;
        boolean moved = false;

        try {
            result = Files.move(Paths.get(src), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            //System.out.println("Exception while moving file: " + e.getMessage());
            moved = false;
        }
        if (result != null) {
            //System.out.println("File moved successfully.");
            moved = true;
            try {
                Files.deleteIfExists(Paths.get(src));
            } catch (IOException e) {
                moved = false;
            }
        } else {
            //System.out.println("File movement failed.");
            moved = false;
        }

        return moved;
    }

    public boolean copyFile(String src, String dest) {
        Path result = null;
        boolean copied = true;

        try {
            File scrFile = new File(Paths.get(src).toString());
            File desFile = new File(Paths.get(dest).toString());

            //result = Files.copy(Paths.get(src), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
            FileUtils.copyFile(scrFile, desFile);
        } catch (IOException e) {
            //System.out.println("Exception while copyng file: " + e.getMessage());
            copied = false;
        }
        /*
        if (result != null) {
            //System.out.println("File copied successfully.");
            copied = true;
            try {
                Files.deleteIfExists(Paths.get(src));
            } catch (IOException e) {
                copied = false;
            }
        } else {
            //System.out.println("File copied failed.");
            copied = false;
        }
        */

        return copied;
    }
    public ByteArrayInputStream firmwareToByteArray(final String fileName) {
        final Path path = Path.of(fileName);
        try (
                final var bin = new BufferedInputStream(Files.newInputStream(path));
                final var baos = new FastByteArrayOutputStream()) {
            bin.transferTo(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return new ByteArrayInputStream(new byte[0]);
    }
}
