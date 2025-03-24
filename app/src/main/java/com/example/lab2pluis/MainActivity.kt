package com.example.lab2pluis

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2pluis.databinding.ActivityMainBinding
import com.example.lab2pluis.databinding.DialogEditTaskBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var bankAdapter: BankTaskAdapter

    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskRecyclerView = binding.rsBank
        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        val taskList = mutableListOf(
            BankTask("10 марта", "Стипендия", "+3300", true),
            BankTask("11 марта", "Оплата мобильной связи", "-490", true),
            BankTask("12 марта", "Подать заявку на кредит", "", true),
            BankTask("13 марта", "Какой-то долг", "-1000", false),
            BankTask("13 марта", "Задача 2", "Тип 2", true),
            BankTask("14 марта", "Задача 2", "Тип 2", true),
        )

        bankAdapter = BankTaskAdapter(taskList)
        taskRecyclerView.adapter = bankAdapter



        imageRecyclerView = binding.rsImage
        imageRecyclerView.layoutManager = GridLayoutManager(this, 3)

        val galleryList: MutableList<GalleryImage> = mutableListOf()

        galleryAdapter = GalleryAdapter(galleryList)
        imageRecyclerView.adapter = galleryAdapter

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val imageUri: Uri? = data?.data
                imageUri?.let {
                    galleryList.add(GalleryImage(it))
                    galleryAdapter.notifyItemInserted(galleryList.size - 1)
                }
            }
        }



        with(binding) {
            btnAdd.setOnClickListener {
                addTask(taskList)
            }

            btnSwitch.setOnClickListener {
                switchMods()
            }

            btnAddImage.setOnClickListener {
                addImage()
            }
        }

        updateBalance()
    }

    private fun switchMods() {
        if (taskRecyclerView.isVisible) {
            taskRecyclerView.visibility = View.GONE
            with(binding) {
                btnAdd.visibility = View.GONE
                btnAddImage.visibility = View.VISIBLE
                tvBalance.visibility = View.GONE
                tvTitle.text = getString(R.string.eeee2)
                btnSwitch.text = getString(R.string.into_task)
            }
            imageRecyclerView.visibility = View.VISIBLE
        } else {
            taskRecyclerView.visibility = View.VISIBLE
            with(binding) {
                btnAdd.visibility = View.VISIBLE
                btnAddImage.visibility = View.GONE
                tvBalance.visibility = View.VISIBLE
                tvTitle.text = getString(R.string.eeee)
                btnSwitch.text = getString(R.string.into_gallery)
            }
            imageRecyclerView.visibility = View.GONE
        }

    }

    private fun addImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }


    private fun addTask(taskList: MutableList<BankTask>) {
        val dialogBinding = DialogEditTaskBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setTitle("Добавить задачу")
            .setPositiveButton("Добавить") { _, _ ->
                val name = dialogBinding.etTaskName.text.toString()
                val type = dialogBinding.etTaskCoast.text.toString()

                val calendar = Calendar.getInstance().apply {
                    set(dialogBinding.dtTask.year, dialogBinding.dtTask.month, dialogBinding.dtTask.dayOfMonth)
                }
                val year = calendar.get(Calendar.YEAR)

                val dateFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
                val date = if (year == CURRENT_YEAR) dateFormat.format(calendar.time)
                else dateFormat.format(calendar.time) + " " +  year.toString()

                val newTask = BankTask(date, name, type)
                taskList.add(newTask)
                bankAdapter.notifyItemInserted(taskList.size - 1)
                updateBalance()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    fun updateBalance() {
        val balance = bankAdapter.calculateTotalSum()
        binding.tvBalance.text = "Текущий баланс: $balance"
    }

}