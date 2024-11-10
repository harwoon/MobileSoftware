import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.secondproject.databinding.ActivityPocketBinding

class PocketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityPocketBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarPocket)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
