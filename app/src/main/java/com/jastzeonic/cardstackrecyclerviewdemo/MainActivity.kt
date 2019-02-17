package com.jastzeonic.cardstackrecyclerviewdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.jastzeonic.cardstackrecyclerviewdemo.model.ImageModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val list = getImageList()

        val adapter = RecycleViewAdapter(list)


        recycler_view.layoutManager = CardStackLayoutManager()

        recycler_view.adapter = adapter

    }


    private fun getImageList() = mutableListOf(
        ImageModel("Sword Guard 1", R.drawable.sword_guard_1),
        ImageModel("Sword Guard 2", R.drawable.sword_guard_2),
        ImageModel("Sword Guard 3", R.drawable.sword_guard_3),
        ImageModel("Sword Guard 4", R.drawable.sword_guard_4),
        ImageModel("Sword Guard 5", R.drawable.sword_guard_5),
        ImageModel("Sword Guard 6", R.drawable.sword_guard_6),
        ImageModel("Sword Guard 7", R.drawable.sword_guard_7),
        ImageModel("Sword Guard 8", R.drawable.sword_guard_8),
        ImageModel("Sword Guard 9", R.drawable.sword_guard_9)

    )

}
