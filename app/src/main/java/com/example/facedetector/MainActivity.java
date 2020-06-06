package com.example.facedetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView i;
    Button b;
    TextView t;
    public static final int Pic_image=121;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        i=(ImageView)findViewById(R.id.image);
        b=(Button)findViewById(R.id.choose);
        t=(TextView)findViewById(R.id.text);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),Pic_image);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Pic_image) {
            i.setImageURI(data.getData());
            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                FirebaseVisionFaceDetectorOptions options =
                        new FirebaseVisionFaceDetectorOptions.Builder()
                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                .build();
                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(options);

                Task<List<FirebaseVisionFace>> result =
                        detector.detectInImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                                            @Override
                                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                                // Task completed successfully
                                                // ...
                                                for (FirebaseVisionFace face : faces) {
                                                    Rect bounds = face.getBoundingBox();
                                                    float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                                    float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                                    // nose available):
                                                    FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                                    if (leftEar != null) {
                                                        FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                                    }

                                                    // If contour detection was enabled:
                                                    List<FirebaseVisionPoint> leftEyeContour =
                                                            face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                                    List<FirebaseVisionPoint> upperLipBottomContour =
                                                            face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();

                                                    // If classification was enabled:
                                                    if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                        float smileProb = face.getSmilingProbability();
                                                    }
                                                    if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                        float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                    }

                                                    // If face tracking was enabled:
                                                    if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                        int id = face.getTrackingId();
                                                    }
                                                }
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
