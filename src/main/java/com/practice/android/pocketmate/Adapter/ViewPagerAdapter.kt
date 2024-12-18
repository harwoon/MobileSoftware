package com.practice.android.pocketmate.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.practice.android.pocketmate.Model.BoardModel
import com.practice.android.pocketmate.Pocket.PocketActivity
import com.practice.android.pocketmate.Pocket.PocketBoardActivity
import com.practice.android.pocketmate.Tip.TipActivity
import com.practice.android.pocketmate.Tip.TipBoardActivity
import com.practice.android.pocketmate.databinding.ItemRecyclerMainActivityBinding
import com.practice.android.pocketmate.util.ScreenUtils.Companion.switchScreen

class ViewPagerViewHolder(val binding: ItemRecyclerMainActivityBinding) : RecyclerView.ViewHolder(binding.root)

class ViewPagerAdapter(val context: AppCompatActivity,
                       private val itemList: MutableList<BoardModel>,
                       private val itemKeyList: MutableList<String>, private val isTip: Boolean)
    : RecyclerView.Adapter<ViewPagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        return ViewPagerViewHolder(ItemRecyclerMainActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val binding = holder.binding
        val key = itemKeyList[position]
        val item = itemList[position]
        binding.itemTitle.text = item.title
        binding.itemContent.text = item.content
        binding.root.setOnClickListener {
            if (isTip) {
                if (key.isBlank()) {
                    switchScreen(context, TipBoardActivity::class.java)
                } else {
                    val intent: Intent = Intent(context, TipActivity::class.java)
                    intent.putExtra("key", key)
                    context.startActivity(intent)
                }
            }
            else {
                if (key.isBlank()) {
                    switchScreen(context, PocketBoardActivity::class.java)
                } else {
                    val intent: Intent = Intent(context, PocketActivity::class.java)
                    intent.putExtra("key", key)
                    context.startActivity(intent)
                }
            }
        }
    }
}