package com.tatara.journalapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tatara.journalapp.databinding.JournalRowBinding

class JournalRecyclerAdapter(val context: Context, val journalList: List<Journal>) :
    RecyclerView.Adapter<JournalRecyclerAdapter.MyViewHolder>() {

    lateinit var binding: JournalRowBinding

    class MyViewHolder(var binding: JournalRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(journal: Journal) {
            binding.journal = journal

            Glide.with(binding.root)
                .load(journal.img)
                .into(binding.journalImage)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = JournalRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = journalList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val journal = journalList[position]
        holder.bind(journal)
    }
}