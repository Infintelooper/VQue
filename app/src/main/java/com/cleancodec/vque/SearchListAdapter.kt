package com.cleancodec.vque

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_single_item.view.*


class SearchListAdapter(var searchList: List<SearchModel>): RecyclerView.Adapter<SearchListAdapter.SearchListViewholder>() {



    class SearchListViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        fun bind(searchModel: SearchModel){
            itemView.single_item_search.text = searchModel.title
        }
    }

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ): SearchListAdapter.SearchListViewholder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.search_single_item,
            parent,
            false
        )
        return SearchListViewholder(view)
    }

    override fun onBindViewHolder(holder: SearchListAdapter.SearchListViewholder, position: Int) {
        holder.bind(searchList[position])
        holder.itemView.setOnClickListener {

            var thiscontext: Context = landing()
            var data = holder.itemView.single_item_search.text.toString()
            val intent = Intent("custom-message")
            intent.putExtra("data_pass", data)
            LocalBroadcastManager.getInstance(thiscontext).sendBroadcast(intent);

            //code for text change in shop_search_editText

        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }


}