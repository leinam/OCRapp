package com.leina.ocrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    // initialize layout objects
    ImageView imageView;
    TextView txt_detected;
    Button btn_capture;
    Button btn_detect;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // map to layout objects in xml layout
        imageView = findViewById(R.id.imageView_thumbnail);
        txt_detected = findViewById(R.id.textView_detected);
        btn_detect = findViewById(R.id.btn_detect);
        btn_capture = findViewById(R.id.btn_snap);

        btn_detect.setEnabled(false);


        //when you click capture function to take photo is called
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(); //call function to use camera app to add
                btn_detect.setEnabled(true); // allow text detection
            }
        });

        //
        btn_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectImageText(); //
            }
        });



    }


    //retrieved from google dev documentation on site
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //load thumbnail onto our activity's image view for preview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");//convert captured image to bitmap
            imageView.setImageBitmap(imageBitmap);// preview image in app
        }
    }


    private void detectImageText(){
        //generate from bitmap object created from photo taken
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        btn_detect.setEnabled(false);// while processing

        recognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                btn_detect.setEnabled(true);

                processTextResult(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btn_capture.setEnabled(true);
                e.printStackTrace(); // show error in logs
            }
        });
    }

    private void processTextResult(FirebaseVisionText text){
        // list with text capture areas
        List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();

        if (blocks.size() == 0) {
            //short alert to show result message
            Toast.makeText(this,"No text found",Toast.LENGTH_SHORT).show();
            return;
        }

        //iterate through text blocks
        for(FirebaseVisionText.TextBlock block : blocks){
            String detected_text = block.getText();
            txt_detected.setText(detected_text);

        }

    }
}
