package moe.leer.grain.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import moe.leer.grain.R
import moe.leer.grain.Util
import moe.leer.grain.model.ECard
import java.util.*

/**
 *
 * Created by leer on 1/22/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class ECardAdapter : PagedListAdapter<ECard, RecyclerView.ViewHolder>(ITEM_COMPARATOR) {

    val TAG = "ECardAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ECardAdapter.ECardViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ecard_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as ECardViewHolder).bind(item)
    }


    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<ECard>() {
            override fun areItemsTheSame(oldItem: ECard, newItem: ECard): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ECard, newItem: ECard): Boolean =
                oldItem == newItem
        }
    }

    class ECardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameText: TextView = view.findViewById(R.id.itemNameText)
        private val moneyText: TextView = view.findViewById(R.id.itemMoneyText)
        private val timeText: TextView = view.findViewById(R.id.itemTimeText)
        private val typeImage: AppCompatImageView = view.findViewById(R.id.itemTypeImageView)

        fun bind(item: ECard?) {
            val resources = itemView.resources
            if (item == null) {
                nameText.text = resources.getString(R.string.text_unknown_data)
                moneyText.text = resources.getString(R.string.text_unknown_data)
                timeText.text = resources.getString(R.string.text_unknown_data)
            } else {
                nameText.text = item.name

                if (item.time != null) {
                    timeText.text = Util.getTimeString(item.time)
                } else {
                    timeText.text = resources.getString(R.string.text_unknown_data)
                }
                if (item.money > 0.0) {
                    moneyText.text = String.format("+%.2f", item.money)
                } else {
                    moneyText.text = String.format("%.2f", item.money)
                }

                when (judgeType((item))) {
                    ItemType.WATER -> typeImage.setImageResource(R.drawable.water_24dp)
                    ItemType.SHOPPING -> typeImage.setImageResource(R.drawable.food_24dp)
                    ItemType.NOODLES -> typeImage.setImageResource(R.drawable.noodles_24dp)
                    ItemType.BREAKFAST -> typeImage.setImageResource(R.drawable.breakfast_24dp)
                    ItemType.MEAL -> typeImage.setImageResource(R.drawable.rice_24dp)
                    ItemType.UNKNOWN -> typeImage.setImageResource(R.drawable.unknown_24dp)
                    ItemType.TOP_UP -> typeImage.setImageResource(R.drawable.topup_24dp)
                }
            }
        }


        /**
         * Judge item type by its name, time and money
         */
        private fun judgeType(item: ECard): ItemType {
            val morning = Calendar.getInstance(TimeZone.getDefault())
                .apply {
                    time = item.time
                    set(Calendar.HOUR_OF_DAY, 6)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0);
                    set(Calendar.MILLISECOND, 0);
                }
            val noon = Calendar.getInstance(TimeZone.getDefault())
                .apply {
                    time = item.time
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0);
                    set(Calendar.MILLISECOND, 0);
                }

            val evening = Calendar.getInstance(TimeZone.getDefault())
                .apply {
                    time = item.time
                    set(Calendar.HOUR_OF_DAY, 20)
                    set(Calendar.MINUTE, 30)
                    set(Calendar.SECOND, 0);
                    set(Calendar.MILLISECOND, 0);
                }
            val now = Calendar.getInstance(TimeZone.getDefault())
                .apply {
                    time = item.time
                }
            return if (item.name.contains("宿舍")) {
                ItemType.WATER
            } else if (item.name.contains(Regex("拉面|牛百碗"))) {
                ItemType.NOODLES
            } else if (item.name.contains(Regex("铺子|超市|水果"))) {
                ItemType.SHOPPING
            } else if (item.money > 5.0) {
                ItemType.TOP_UP
            } else if (now.after(morning) && now.before(noon)) {
                ItemType.BREAKFAST
            } else if (now.after(noon) && now.before(evening) || item.name.contains(Regex("食堂|周末阳光|食"))) {
                ItemType.MEAL
            } else {
                ItemType.UNKNOWN
            }
        }

        enum class ItemType {
            BREAKFAST, // icon: breakfast
            MEAL,       //pizza, mushroom, rice
            NOODLES,
            WATER,
            SHOPPING,
            UNKNOWN, // unknown type consume
            TOP_UP, // + > 5 RMB is top up
        }
    }

}