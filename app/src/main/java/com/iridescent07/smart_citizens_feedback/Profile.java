package com.iridescent07.smart_citizens_feedback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.theartofdev.edmodo.cropper.CropImage;
import android.Manifest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    PostDetails postDetails;
    private TextView greeting, locationText,caption;
    private String type;
    private RatingBar ratingBar;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private String latitude, longitude;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;

    private Uri mainImageURI = null;
    private ImageView imageView;
    private double random_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post Details");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ratingBar = findViewById(R.id.ratingBar);
        imageView = findViewById(R.id.image);
        greeting = findViewById(R.id.usergreeting);
        locationText = findViewById(R.id.locationText);
        caption = findViewById(R.id.caption);
        postDetails = new PostDetails();

        //random_image = Math.random()*(1000-1)+1;

        getProfileData();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                imageView.setImageURI(mainImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void getProfileData(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            String email = firebaseUser.getEmail();
            Toast.makeText(this, "Welcome "+email+"!", Toast.LENGTH_SHORT).show();
            greeting.setText(email);
        }
    }

    public void signOut(View view){
        firebaseAuth.signOut();
        Intent intent = new Intent(Profile.this, Login.class);
        startActivity(intent);
        Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void takePic(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(Profile.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else{
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(Profile.this);
            }
        }
    }

    public void submitPic(View view){
        if(mainImageURI!=null){
            final String user_id = firebaseAuth.getUid();
            random_image = Math.random()*(1000-1)+1;
            final StorageReference image_path = storageReference.child("Post Images").child((random_image)+".jpg");
            image_path.putFile(mainImageURI).addOnCompleteListener((task) -> {
                if(task.isSuccessful()){
                    Map<String,String> userMap = new HashMap<>();
                    String downloadUrl = image_path.getDownloadUrl().toString();
                    userMap.put("image",downloadUrl);

                    firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener((tasks) ->{
                        if(tasks.isSuccessful()){
                            Toast.makeText(this, "Data Updated!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String error = tasks.getException().getMessage();
                            Toast.makeText(this, "Error : "+error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(this, "Image upload successful!", Toast.LENGTH_SHORT).show();
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(this, "Error : "+error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getType(){
        RadioGroup rg = findViewById(R.id.type);
        int radiotypeID = rg.getCheckedRadioButtonId();
        RadioButton radio_type = findViewById(radiotypeID);
        type = radio_type.getText().toString();
    }

    public void getUserLocation(){
        if(ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else{
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            double location_lat, location_long ;

            if(location!=null){
                location_lat = location.getLatitude();
                location_long = location.getLongitude();
                latitude = String.valueOf(location_lat);
                longitude = String.valueOf(location_long);
                locationText.setText("");
                locationText.setText(locationText.getText() + "\nLatitude : "+latitude+"\nLongitude : "+longitude);
            }

            else if(locationNetwork!=null){
                location_lat = locationNetwork.getLatitude();
                location_long = locationNetwork.getLongitude();
                latitude = String.valueOf(location_lat);
                longitude = String.valueOf(location_long);
                locationText.setText("");
                locationText.setText(locationText.getText() + "\nLatitude : "+latitude+"\nLongitude : "+longitude);
            }

            else if(locationPassive!=null){
                location_lat = locationPassive.getLatitude();
                location_long = locationPassive.getLongitude();
                latitude = String.valueOf(location_lat);
                longitude = String.valueOf(location_long);
                locationText.setText("");
                locationText.setText(locationText.getText() + "\nLatitude : "+latitude+"\nLongitude : "+longitude);
            }

            else{
                Toast.makeText(this, "Sorry! Couldn't access your location!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", (dialog, which) -> {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }).setNegativeButton("NO", (dialog, which) -> {dialog.cancel(); });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void getLocation(View view){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGPS();
        }
        else{
            getUserLocation();
        }
    }

    public void submitFeedback(View view){
        getType();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        postDetails.setLatitude(latitude);
        postDetails.setLongitude(longitude);
        postDetails.setSeverity(ratingBar.getRating());
        postDetails.setCaption(caption.getText().toString().trim());
        postDetails.setDone(false);
        postDetails.setUserID(firebaseUser.getUid());
        postDetails.setType(type);
        postDetails.setImageID(Double.toString(random_image)+".jpg");;

        databaseReference.push().setValue(postDetails);
        Toast.makeText(this, "Data uploaded!", Toast.LENGTH_SHORT).show();
    }

    public void uploadedFeedback(View view){
        Intent intent = new Intent(Profile.this,UploadedFeedback.class);
        startActivity(intent);
    }
}
