package com.roycai.openfile;

import com.roycai.rememberword.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * User: tosmart
 * Date: 2010-5-6
 * Time: 9:29:26
 */
public class MessageBox {

    public static void showAbout(Context context) {

        String message = context.getString(R.string.message_about);
        message = message.replaceAll("\\n", "\n");

        new AlertDialog.Builder(context)
                .setIcon(R.drawable.icon)
                .setTitle(R.string.title_about)
                .setMessage(message)
                .setPositiveButton(R.string.label_ok, justClose)
                .show();
    }

    public static void showMessage(Context context, String message) {

        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.label_ok, justClose)
                .show();
    }

    public static void showMessage(Context context, String title, String message) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.label_ok, justClose)
                .show();
    }

    public static void showMessage(Context context, int messageResourceId) {

        new AlertDialog.Builder(context)
                .setMessage(messageResourceId)
                .setPositiveButton(R.string.label_ok, justClose)
                .show();
    }

    public static void showMessage(
            Context context, int titleResourceId, int messageResourceId) {

        new AlertDialog.Builder(context)
                .setTitle(titleResourceId)
                .setMessage(messageResourceId)
                .setPositiveButton(R.string.label_ok, justClose)
                .show();
    }

    public static void listSelect(
            Context context, int titleResourceId,
            String[] items, DialogInterface.OnClickListener onSelect) {

        new AlertDialog.Builder(context)
                .setTitle(titleResourceId)
                .setItems(items, onSelect)
                .setNegativeButton(R.string.label_cancel, justClose)
                .show();
    }

    public static void listSelect(
            Context context, String title,
            String[] items, DialogInterface.OnClickListener onSelect) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, onSelect)
                .setNegativeButton(R.string.label_cancel, justClose)
                .show();
    }

    public static void listSelect(
            Context context, int titleResourceId,
            int arrayResourceId, DialogInterface.OnClickListener onSelect) {

        new AlertDialog.Builder(context)
                .setTitle(titleResourceId)
                .setItems(arrayResourceId, onSelect)
                .setNegativeButton(R.string.label_cancel, justClose)
                .show();
    }

    public static void listSelect(
            Context context, String title,
            int arrayResourceId, DialogInterface.OnClickListener onSelect) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(arrayResourceId, onSelect)
                .setNegativeButton(R.string.label_cancel, justClose)
                .show();
    }

    public static void shortToast(Context context, String message) {
        Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    public static void longToast(Context context, String message) {
        Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    public static void shortToast(Context context, int messageResourceId) {
        Toast.makeText(
                context,
                messageResourceId,
                Toast.LENGTH_SHORT
        ).show();
    }

    public static void longToast(Context context, int messageResourceId) {
        Toast.makeText(
                context,
                messageResourceId,
                Toast.LENGTH_LONG
        ).show();
    }

    public static void showConfirm(
            Context context,
            String title,
            String message,
            DialogInterface.OnClickListener onOk) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.label_ok, onOk)
                .setNegativeButton(R.string.label_cancel, justClose)
                .show();
    }

    public static void showConfirm(
            Context context,
            int titleResouceId,
            int messageResourceId,
            DialogInterface.OnClickListener onOk) {

        new AlertDialog.Builder(context)
                .setTitle(titleResouceId)
                .setMessage(messageResourceId)
                .setPositiveButton(R.string.label_ok, onOk)
                .setNegativeButton(R.string.label_cancel, justClose)
                .show();
    }

    public static DialogInterface.OnClickListener
            justClose = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        }
    };
}
