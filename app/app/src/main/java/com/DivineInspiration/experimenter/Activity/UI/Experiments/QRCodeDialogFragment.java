package com.DivineInspiration.experimenter.Activity.UI.Experiments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.DivineInspiration.experimenter.R;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class QRCodeDialogFragment extends DialogFragment {
    ImageView qrImage;
    String message;
    Bitmap bitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // create view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.qr_code_dialog_fragment, null);

        qrImage = view.findViewById(R.id.qrImage);

        // TODO: set the string message
        message = "1253453452";
        // factory method + generate
        QRFactory qrFactory = new QRFactory();
        QRGEncoder qrgEncoder = qrFactory.generate(message);

        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v("Error Render", e.toString());
        }


        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.dialogColor)
                .setView(view)
                .setMessage("QR Code")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveImage();
                    }

                })
                .setNegativeButton("Ok", null)
                .create();

        // on save click, save QR code
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // saves qr image
//                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "QR-code" , String.format("QR code for %s", message));
////                QRGSaver qrgSaver = new QRGSaver();
////                qrgSaver.save("", message, qrgEncoder.getBitmap(), QRGContents.ImageType.IMAGE_JPEG);
//                dialog.dismiss();
//            }
//        });

        dialog.show();

        return dialog;
    }

    private void saveImage() {
        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/Experimenter");
        dir.mkdirs();

        String filename = String.format("Experimenter-%d.png", System.currentTimeMillis());
        File outFile = new File(dir,filename);
        try {
            outputStream = new FileOutputStream(outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100, outputStream);
        try {
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}