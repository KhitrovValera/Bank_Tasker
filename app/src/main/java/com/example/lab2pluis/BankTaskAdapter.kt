package com.example.lab2pluis

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale


class BankTaskAdapter(
    private val taskList: MutableList<BankTask>,
): RecyclerView.Adapter<BankTaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTv: TextView = itemView.findViewById(R.id.tvDate)
        val nameTv: TextView = itemView.findViewById(R.id.tvName)
        val typeTv: TextView = itemView.findViewById(R.id.tvType)
        val completingCb: CheckBox = itemView.findViewById(R.id.cbDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bank, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun getItemCount() = taskList.size


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = taskList[position]

        val type = currentTask.type.toDoubleOrNull()
        val context = holder.itemView.context
        val drawable = holder.itemView.background.mutate() as? GradientDrawable

        val borderColor = when {
            type == null -> ContextCompat.getColor(context, R.color.xz)
            type > 0 -> ContextCompat.getColor(context, R.color.plus)
            type < 0 -> ContextCompat.getColor(context, R.color.minus)
            else -> ContextCompat.getColor(context, R.color.xz)
        }

        drawable?.setStroke(8, borderColor)

        with(holder) {
            dateTv.text = currentTask.date
            nameTv.text = currentTask.name
            typeTv.text = if (type != null) currentTask.type + " руб" else currentTask.type
            completingCb.isChecked = currentTask.completing

            completingCb.setOnCheckedChangeListener { _, isChecked ->
                currentTask.completing = isChecked
                (context as? MainActivity)?.updateBalance()
            }

            itemView.setOnLongClickListener {
                showDeleteConfirmationDialog(holder.itemView, position)
                true
            }

            itemView.setOnClickListener {
                showEditDialog(holder.itemView.context, taskList[position], position)
                (context as? MainActivity)?.updateBalance()
            }
        }
    }

    private fun showEditDialog(context: Context, task: BankTask, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_task, null)
        val etTaskName = dialogView.findViewById<EditText>(R.id.etTaskName)
        val etTaskCoast = dialogView.findViewById<EditText>(R.id.etTaskCoast)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.dtTask)

        etTaskName.setText(task.name)
        etTaskCoast.setText(task.type)



        AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Редактирование задачи")
            .setPositiveButton("Сохранить") { _, _ ->
                val updateName = etTaskName.text.toString()
                val updatedType = etTaskCoast.text.toString()

                val calendar = Calendar.getInstance()
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)

                val year = calendar.get(Calendar.YEAR)

                val dateFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
                val formattedDate = if (year == CURRENT_YEAR) dateFormat.format(calendar.time)
                else dateFormat.format(calendar.time) + " " +  year.toString()

                with(task) {
                    name = updateName
                    type = updatedType
                    date = formattedDate

                    notifyItemChanged(position)
                    (context as? MainActivity)?.updateBalance()
                }


            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()



    }


    private fun showDeleteConfirmationDialog(view: View, position: Int) {
        AlertDialog.Builder(view.context)
            .setTitle("Удалить задачу")
            .setMessage("Вы уверены, что хотите удалить задачу «${taskList[position].name}»?")
            .setPositiveButton("Да") { _, _ ->
                removeTask(position)
                (view.context as? MainActivity)?.updateBalance()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun removeTask(position: Int) {
        if (position in taskList.indices) {
            taskList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, taskList.size)

        }
    }

    fun calculateTotalSum(): Double {
        return taskList.sumOf { if (it.completing) it.type.toDoubleOrNull() ?: 0.0 else 0.0 }
    }
}