package coco.man.recorder.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseAlterableAdapter<T> :
    RecyclerView.Adapter<BaseAlterableAdapter.ViewHolder<in ViewBinding>> {
    var onItemClick: ((view: View, position: Int) -> Unit)? = null

    protected var mDatas: List<T>? = null
    protected var context: android.content.Context
    fun getmDatas(): List<T> {
        return mDatas ?: java.util.ArrayList<T>().also {
            mDatas = it
        }
    }

    fun setmDatas(mDatas: List<T>?) {
        this.mDatas = mDatas
    }

    constructor(context: android.content.Context) {
        this.context = context
    }

    constructor(context: android.content.Context, mDatas: List<T>?) {
        this.mDatas = mDatas
        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<in ViewBinding> {
        val inflater = LayoutInflater.from(context)
        var viewHolder: ViewHolder<ViewBinding>? = null
        val binding = setBinding(inflater, parent, viewType)
        viewHolder = ViewHolder(binding.root)
        viewHolder.binding = binding;
        if (onItemClick != null) {
            viewHolder.onItemClick = onItemClick
        }
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: ViewHolder<in ViewBinding>,
        position: Int
    ) {

        val myholder =
            holder as ViewHolder<ViewBinding>
        onBindHolder(myholder, position)

    }

    protected abstract fun onBindHolder(
        holder: ViewHolder<ViewBinding>,
        position: Int
    )


    override fun getItemId(position: Int): kotlin.Long {
        return position.toLong()
    }

    protected abstract fun setBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        type: Int
    ): ViewBinding

    override fun getItemCount(): Int {
        return if (mDatas == null) 0 else mDatas!!.size
    }

    class ViewHolder<ViewBinding>(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var onItemClick: ((view: View, position: Int) -> Unit)? = null
        var binding: ViewBinding? = null


        fun setClick(vararg views: View) {
            onItemClick?.let {
                for (view in views) {
                    view.setOnClickListener(this)
                }
            }
        }


        override fun onClick(v: View) {
            onItemClick?.let {
                it(v, adapterPosition)
            }
        }
    }

}