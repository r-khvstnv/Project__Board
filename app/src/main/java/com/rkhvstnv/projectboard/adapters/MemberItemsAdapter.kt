package com.rkhvstnv.projectboard.adapters

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rkhvstnv.projectboard.OnItemClicked
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.models.UserDataClass
import de.hdodenhof.circleimageview.CircleImageView

class MemberItemsAdapter(
    private val context: Context,
    private val userList: ArrayList<UserDataClass>,
    private val boardCreatorId: String,
    private val onItemClicked: OnItemClicked):
    RecyclerView.Adapter<MemberItemsAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
            val civMemberImage: CircleImageView = view.findViewById(R.id.civ_item_member_image)
            val tvMemberName: TextView = view.findViewById(R.id.tv_item_member_name)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater
            .from(context).inflate(R.layout.item_member, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]

        Glide.with(context)
            .load(user.imageProfile).fitCenter()
            .placeholder(R.drawable.ic_profile).into(holder.civMemberImage)

        holder.tvMemberName.text = user.name
        if (user.id == boardCreatorId){
            holder.tvMemberName.setTextColor(ContextCompat.getColor(context, R.color.mEditTextColor))
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}