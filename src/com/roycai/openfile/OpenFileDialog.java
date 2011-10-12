package com.roycai.openfile;

import android.content.Intent;
import android.text.Editable;

import java.io.File;

import com.roycai.rememberword.R;

/**
 * Date: 2010-2-26
 * Time: 14:10:37
 *
 * @author tosmart@gmail.com
 */
public class OpenFileDialog extends FileDialog {

    protected void browse(File file) {

        if (file.isFile()) {
            fileName.setText(file.getName());
        }

        super.browse(file);
    }

    protected void confirm() {

        Editable text = fileName.getText();
        String name = text.toString().trim();
        if (name.length() == 0) return;

        String path = dir.getAbsolutePath();
        if (!path.endsWith("/")) path += '/';
        String pathName = path + name;

        Intent intent = new Intent();
        intent.putExtra(KEY_NAME, pathName);

        setResult(RESULT_OK, intent);

        finish();
    }

    protected void updateTitle(File file) {
        setTitle(getString(R.string.title_open_dialog) + file.getPath());
    }
}