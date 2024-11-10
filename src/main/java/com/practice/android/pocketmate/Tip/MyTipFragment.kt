package com.practice.android.pocketmate.Tip

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.practice.android.pocketmate.Adapter.BoardAdapter
import com.practice.android.pocketmate.Model.BoardModel
import com.practice.android.pocketmate.databinding.FragmentMyTipBinding
import com.practice.android.pocketmate.util.FBRef
import kotlinx.coroutines.NonCancellable.children

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyTipFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMyTipBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyTipBinding.inflate(inflater, container, false)

        val itemList = mutableListOf<BoardModel>()

        FBRef.tipRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                itemList.clear()

                for (data in dataSnapshot.children) {
                    val item = data.getValue(BoardModel::class.java)
                    itemList.add(item!!)
                }
                binding.recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                //읽기 실패
            }
        })

        binding.recycler.adapter = BoardAdapter(itemList)
        binding.recycler.layoutManager = LinearLayoutManager(context)

        return binding.root
    }
}