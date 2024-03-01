package project.social.whisper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoViewAttacher
import project.social.whisper.databinding.ActivityImageViewBinding


class ImageViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(b.root)
            
        val img = intent.getStringExtra("img")!!
        
        Glide.with(this).load(img).into(b.main)

    }
}