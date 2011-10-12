package com.roycai.openfile;

import android.content.Context;

import java.io.*;

/**
 * User: tosmart
 * Date: 2010-4-28
 * Time: 13:13:24
 */
public class FileIO {

    public static boolean writeData(File file, String content) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(file);
            write(content, writer);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (writer != null) writer.close();
        }

        return true;
    }

    public static boolean writeAppData(Context context, String name, String content) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    context.openFileOutput(
                            name,
                            Context.MODE_PRIVATE
                    )
            );
            write(content, writer);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (writer != null) writer.close();
        }

        return true;
    }

    public static String readData(File file) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file)
                    )
            );

            return read(reader).toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (reader != null) reader.close();
            }
            catch (IOException ignored) {
            }
        }
    }

    public static String readAppData(Context context, String name) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            context.openFileInput(name)
                    )
            );

            return read(reader).toString().trim();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (reader != null) reader.close();
            }
            catch (IOException ignored) {
            }
        }
    }

    private static void write(String content, PrintWriter writer) {
        writer.println(content);
    }

    private static StringBuffer read(BufferedReader reader) throws IOException {

        StringBuffer buffer = new StringBuffer();

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }

        return buffer;
    }

    public static void deleteFile(File file) {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteFile(child);
                }
            }
        }

        file.delete();
    }
}
