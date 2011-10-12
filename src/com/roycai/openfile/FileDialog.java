package com.roycai.openfile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.roycai.rememberword.R;

/**
 * Date: 2010-2-25
 * Time: 15:58:10
 *
 * @author tosmart@gmail.com
 */
public abstract class FileDialog extends Activity {

    public static final int IdAboutApp = Menu.FIRST + 1;
    public static final String DATA_PATH = "/sdcard";

    public static final String KEY_PATH = "path";
    public static final String KEY_NAME = "name";
    public static final String KEY_EXT_NAME = "ext-name";

    public static final int MID_DELETE_FILE = Menu.FIRST + 1;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_dialog);

        findViews();

        ensureDataDir();

        Intent intent = getIntent();

        extName = intent.getStringExtra(KEY_EXT_NAME);
        if (extName != null) extName = extName.toLowerCase();

        String path = intent.getStringExtra(KEY_PATH);
        if (path == null) path = DATA_PATH;

        String name = intent.getStringExtra(FileDialog.KEY_NAME);
        if (name != null) fileName.setText(name);

        browse(new File(path));
    }

    private void findViews() {

        dirList = (ListView) findViewById(R.id.dir_list);
        fileName = (EditText) findViewById(R.id.file_name);

        findViewById(R.id.save_file).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        confirm();
                    }
                }
        );
        dirList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(
                            AdapterView<?> parent, View view, int pos, long id) {
                        clickListItem(pos);
                    }
                }
        );
        dirList.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    public boolean onItemLongClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        if (position < 1) return true;
                        selectedPosition = position;
                        return false;
                    }
                }
        );
        dirList.setOnCreateContextMenuListener(
                new View.OnCreateContextMenuListener() {
                    public void onCreateContextMenu(
                            ContextMenu menu, View view,
                            ContextMenu.ContextMenuInfo menuInfo) {
                        createContextMenu(menu);
                    }
                }
        );
    }

    private void ensureDataDir() {
        File dataDir = new File(DATA_PATH);
        if (!dataDir.exists()) dataDir.mkdirs();
    }

    protected void createContextMenu(ContextMenu menu) {
        menu.add(0, MID_DELETE_FILE, 0, R.string.label_delete);
    }

    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case MID_DELETE_FILE:
                deleteSelectedFile();
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void deleteSelectedFile() {

        DialogInterface.OnClickListener onOk =
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedPosition < 1) return;
                        FileIO.deleteFile(new File(dir, names.get(selectedPosition)));
                        browse(dir);
                    }
                };

        MessageBox.showConfirm(
                this,
                R.string.label_delete,
                R.string.message_delete_confirm,
                onOk
        );
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        menu.add(
                0,
                IdAboutApp,
                0,
                R.string.title_about
        ).setIcon(
                R.drawable.ic_app
        );

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case IdAboutApp:
                MessageBox.showAbout(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getRepeatCount() == 0) {

            if (dir.getParent() != null &&
                    !dir.getPath().equals("/sdcard")) {

                upLevel();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    protected void clickListItem(int pos) {

        String selected = names.get(pos);

        if (selected.equals("..")) {
            upLevel();
        }
        else {
            browse(new File(dir, names.get(pos)));
        }
    }

    protected void browse(File file) {

        if (file.isDirectory()) {

            dir = file;
            updateTitle(file);

            FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory() || extName == null ||
                            file.getName().toLowerCase().endsWith(extName);
                }
            };

            fillList(file.listFiles(fileFilter));
        }
    }

    protected void updateTitle(File file) {
        setTitle(file.getPath());
    }

    protected void upLevel() {
        if (dir.getParent() != null) {
            browse(dir.getParentFile());
        }
    }

    protected void fillList(File[] files) {

        names.clear();

        if (dir.getParent() != null) {
            names.add("..");
        }

        if (files != null) {

            for (File file : files) {

                String absolutePath = file.getAbsolutePath();

                int pos = absolutePath.lastIndexOf('/');
                String name = absolutePath.substring(pos + 1);
                if (file.isDirectory()) name += "/";

                names.add(name);
            }

            Collections.sort(names);
        }

        dirList.setAdapter(
                new FileItemsAdapter(this, names)
        );
    }

    protected abstract void confirm();

    protected ListView dirList;
    protected EditText fileName;

    protected File dir;
    protected String extName;
    protected List<String> names = new ArrayList<String>();
    protected int selectedPosition;
}
