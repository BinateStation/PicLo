package rkr.binatestation.piclo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import rkr.binatestation.piclo.R;
import rkr.binatestation.piclo.network.VolleySingleTon;
import rkr.binatestation.piclo.utils.ZoomableImageView;

public class ImageFullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        ZoomableImageView imageView = (ZoomableImageView) findViewById(R.id.PI_image);
        imageView.setImageUrl(
                VolleySingleTon.getDomainUrlForImage() + getIntent().getStringExtra("IMAGE"),
                VolleySingleTon.getInstance(imageView.getContext()).getImageLoader()
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
