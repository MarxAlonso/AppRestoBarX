package com.example.apprestobarx.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apprestobarx.R
import com.example.apprestobarx.models.Message
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class ChatAdapter(private val onOptionClicked: (String) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<Message>()

    // Constantes para los tipos de vista
    private val TYPE_USER = 1
    private val TYPE_BOT = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) TYPE_USER else TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = if (viewType == TYPE_USER) R.layout.item_message_user else R.layout.item_message_bot
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position], onOptionClicked)
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    // El ViewHolder genérico
    inner class MessageViewHolder(itemView: View, private val viewType: Int) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessageText)

        // Elementos específicos del Bot
        private var optionsRecyclerView: RecyclerView? = null

        init {
            if (viewType == TYPE_BOT) {
                // Se recomienda usar FlexboxLayout para envolver los botones
                optionsRecyclerView = itemView.findViewById(R.id.recyclerOptions)
                val flexLayoutManager = FlexboxLayoutManager(itemView.context)
                flexLayoutManager.flexDirection = FlexDirection.ROW
                flexLayoutManager.justifyContent = JustifyContent.FLEX_START
                optionsRecyclerView?.layoutManager = flexLayoutManager
            }
        }

        fun bind(message: Message, onOptionClicked: (String) -> Unit) {
            // Reemplazar **texto** con negritas HTML
            val formattedText = message.text.replace("**", "<b>").replace("</b>", "</b>")
            tvMessage.text = android.text.Html.fromHtml(formattedText, android.text.Html.FROM_HTML_MODE_LEGACY)


            if (viewType == TYPE_BOT && !message.options.isNullOrEmpty()) {
                optionsRecyclerView?.visibility = View.VISIBLE
                // Asignar un adaptador interno para manejar la lista de botones de opciones
                optionsRecyclerView?.adapter = OptionsAdapter(message.options) { option ->
                    // Llamar a la función lambda cuando se hace clic en una opción
                    onOptionClicked(option)
                }
            } else if (viewType == TYPE_BOT) {
                optionsRecyclerView?.visibility = View.GONE
            }
        }
    }
}

// Adaptador Interno para los botones de opciones (Mejor usando un RecyclerView)
class OptionsAdapter(private val options: List<String>, private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        // Debes crear un layout simple para un botón de opción: item_option_button.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option_button, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position], onClick)
    }

    override fun getItemCount(): Int = options.size

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnOption: Button = itemView.findViewById(R.id.btnOption)

        fun bind(option: String, onClick: (String) -> Unit) {
            btnOption.text = option
            btnOption.setOnClickListener { onClick(option) }
        }
    }
}