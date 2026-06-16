package mcxtzhang.swipedelmenu.FullDemo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import mcxtzhang.listswipemenudemo.R
import mcxtzhang.swipedelmenu.SwipeBean

class FullDelDemoAdapter(
    private val mContext: Context,
    private val mDatas: List<SwipeBean>
) : RecyclerView.Adapter<FullDelDemoAdapter.FullDelDemoVH>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    var onSwipeListener: OnSwipeListener? = null

    interface OnSwipeListener {
        fun onDel(pos: Int)
        fun onTop(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullDelDemoVH =
        FullDelDemoVH(mInflater.inflate(R.layout.item_cst_swipe, parent, false))

    override fun onBindViewHolder(holder: FullDelDemoVH, position: Int) {
        (holder.itemView as SwipeMenuLayout).setIos(false)
            .setLeftSwipe(position % 2 == 0)

        holder.content.text = mDatas[position].name + if (position % 2 == 0) "我右白虎" else "我左青龙"

        holder.content.setOnLongClickListener {
            Toast.makeText(mContext, "longclig", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "onLongClick() called with: v = [$it]")
            false
        }

        holder.btnUnRead.visibility = if (position % 3 == 0) View.GONE else View.VISIBLE

        holder.btnDelete.setOnClickListener {
            onSwipeListener?.onDel(holder.adapterPosition)
        }

        holder.content.setOnClickListener {
            Toast.makeText(mContext, "onClick:${mDatas[holder.adapterPosition].name}", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "onClick() called with: v = [$it]")
        }

        holder.btnTop.setOnClickListener {
            onSwipeListener?.onTop(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = mDatas.size

    inner class FullDelDemoVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.content)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val btnUnRead: Button = itemView.findViewById(R.id.btnUnRead)
        val btnTop: Button = itemView.findViewById(R.id.btnTop)
    }
}
