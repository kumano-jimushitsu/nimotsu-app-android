package com.example.top;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.regex.Pattern;

public class Touroku_Others_Dialog extends DialogFragment {
    Pattern p = Pattern.compile("(\\p{InBasicLatin}+|"
            + " \\p{InHiragana}+|" + " \\p{InKatakana}+|"
            + " \\p{InCJKUnifiedIdeographs}+)", Pattern.COMMENTS);

    @NonNull
    @Override

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //テキスト入力を受け付けるビューを作成します。
        final EditText editView = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("その他の内容を入力してください。")
                //setViewにてビューを設定します。
                .setView(editView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //入力した文字をトースト出力する
                        Toast.makeText(getActivity(),
                                editView.getText().toString(),
                                Toast.LENGTH_LONG).show();
                        insert_parcels_shearingstatus();
                        update_ryosei_shearingstatus();
                        insert_event_shearingstatus();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
        return builder.create();

    }
    public void insert_parcels_shearingstatus (){

    }
    public void update_ryosei_shearingstatus (){

    }
    public void insert_event_shearingstatus (){

    }
}
