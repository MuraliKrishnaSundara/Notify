package dev.kingbond.notify.ui.calendar

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kingbond.notify.R
import dev.kingbond.notify.data.database.RoomDataBaseClass
import dev.kingbond.notify.databinding.ActivityGoalDetailsBinding
import dev.kingbond.notify.databinding.FragmentCalendarBinding
import dev.kingbond.notify.repository.RepositoryClass
import dev.kingbond.notify.ui.event.EventAdapter
import dev.kingbond.notify.ui.event.EventClickListener
import dev.kingbond.notify.ui.event.EventModel
import dev.kingbond.notify.ui.goal.model.GoalModel
import dev.kingbond.notify.ui.goal.recyclerView.GoalAdapter
import dev.kingbond.notify.ui.goal.recyclerView.GoalClickListener
import dev.kingbond.notify.ui.task.model.TaskModel
import dev.kingbond.notify.ui.task.recyclerView.TaskAdapter
import dev.kingbond.notify.ui.task.recyclerView.TaskClickListener
import dev.kingbond.notify.viewmodel.ViewModelClass
import dev.kingbond.notify.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CalendarFragment : Fragment(R.layout.fragment_calendar), DateClickListener, GoalClickListener,
    TaskClickListener, EventClickListener {

    private var selectedDate: LocalDate? = null
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var goalAdapter: GoalAdapter
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var eventAdapter: EventAdapter

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var itemViewModel: ViewModelClass

    private var listGoal = arrayListOf<GoalModel>()
    private var listTask = arrayListOf<TaskModel>()
    private var listEvent = arrayListOf<EventModel>()
    private lateinit var daysInMonth: ArrayList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCalendarBinding.bind(view)

        val roomDatabase = RoomDataBaseClass.getDataBaseObject(requireContext())
        val dao = roomDatabase.getDao()
        val repo = RepositoryClass(dao)
        val viewModelFactory = ViewModelFactory(repo)
        itemViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ViewModelClass::class.java)

        selectedDate = LocalDate.now()
        Toast.makeText(context, selectedDate.toString(), Toast.LENGTH_SHORT).show()
        setMonthView()
        jumpToDate()

        rcvGoals(selectedDate.toString())

    }

    private fun rcvGoals(today: String) {

        itemViewModel.getDataFromGoal().observe(viewLifecycleOwner, Observer {
            listGoal.clear()
            listGoal.addAll(it)
            if(listGoal.isNotEmpty()) {
                tvGoalsCalendar.visibility = View.VISIBLE
            }
            if(listEvent.isEmpty() && listTask.isEmpty() && listGoal.isEmpty()) {
                nothingToShow.visibility = View.VISIBLE
            } else {
                nothingToShow.visibility = View.GONE
            }
            goalAdapter.notifyDataSetChanged()
        })

        itemViewModel.getTasksByDate(today).observe(viewLifecycleOwner, Observer {
            listTask.clear()
            listTask.addAll(it)
            if(listTask.isNotEmpty()) {
                tvTasksCalendar.visibility = View.VISIBLE
            }
            if(listEvent.isEmpty() && listTask.isEmpty() && listGoal.isEmpty()) {
                nothingToShow.visibility = View.VISIBLE
            } else {
                nothingToShow.visibility = View.GONE
            }
            taskAdapter.notifyDataSetChanged()
        })

        itemViewModel.getDataFromEventTable().observe(viewLifecycleOwner, Observer {
            listEvent.clear()
            listEvent.addAll(it)
            if(listEvent.isNotEmpty()) {
                tvEventsCalendar.visibility = View.VISIBLE
            }
            if(listEvent.isEmpty() && listTask.isEmpty() && listGoal.isEmpty()) {
                nothingToShow.visibility = View.VISIBLE
            } else {
                nothingToShow.visibility = View.GONE
            }
            eventAdapter.notifyDataSetChanged()
        })


        goalAdapter = GoalAdapter(listGoal, this, itemViewModel,this)
        taskAdapter = TaskAdapter(listTask, this,itemViewModel,this)
        eventAdapter = EventAdapter(listEvent, this)
        val linearLayoutManagerGoal = LinearLayoutManager(requireContext())
        val linearLayoutManagerTask = LinearLayoutManager(requireContext())
        val linearLayoutManagerEvent = LinearLayoutManager(requireContext())
        binding.apply {
            //Goal
            rcvGoalsCalendar.adapter = goalAdapter
            rcvGoalsCalendar.layoutManager = linearLayoutManagerGoal
            rcvGoalsCalendar.isNestedScrollingEnabled = false

            //Task
            rcvTasksCalendar.adapter = taskAdapter
            rcvTasksCalendar.layoutManager = linearLayoutManagerTask
            rcvTasksCalendar.isNestedScrollingEnabled = false

            //Event
            rcvEventsCalendar.adapter = eventAdapter
            rcvEventsCalendar.layoutManager = linearLayoutManagerEvent
            rcvEventsCalendar.isNestedScrollingEnabled = false
        }
    }

    private fun jumpToDate() {
//        val dateDialog = Dialog
    }

    private fun setMonthView() {

        previousMonthAction(view)
        nextMonthAction(view)

        val currentDate = selectedDate.toString()
        daysInMonth = daysInMonthArray(selectedDate!!)
        calendarAdapter = CalendarAdapter(daysInMonth, this, currentDate, viewLifecycleOwner, itemViewModel)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 7)

        binding.apply {
            monthYearTV.text = monthYearFromDate(selectedDate!!)
//            monthYearTV.text = currentDate
            calendarRecyclerView.layoutManager = layoutManager
            calendarRecyclerView.adapter = calendarAdapter
        }
//        currentdate.text = selectedDate.toString()
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray: ArrayList<String> = ArrayList()
        val yearMonth: YearMonth = YearMonth.from(date)
        val daysInMonth: Int = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate!!.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                if (i > daysInMonth + dayOfWeek) break
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate): String? {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    private fun previousMonthAction(view: View?) {
        binding.previousMonth.setOnClickListener {
            selectedDate = selectedDate!!.minusMonths(1)
            setMonthView()
        }
    }

    private fun nextMonthAction(view: View?) {
        binding.nextMonth.setOnClickListener {
            selectedDate = selectedDate!!.plusMonths(1)
            setMonthView()
        }
    }

//    override fun onItemClick(position: Int, dayText: String?) {
//        if (!dayText.equals("")) {
//            val message =
//                "Selected Date " + dayText.toString() + " " + monthYearFromDate(selectedDate!!)
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//        } else {
//            Toast.makeText(this, "null", Toast.LENGTH_LONG).show()
//        }
//    }

    override fun onDateClicked(date: String, position: Int, today: String) {

        rcvGoals(today)

        if (date != "") {
            val message =
                "Selected Date " + date.toString() + " " + monthYearFromDate(selectedDate!!)
//            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
//            Toast.makeText(context, "null", Toast.LENGTH_LONG).show()
        }
    }

    override fun goalItemClicked(goalModel: GoalModel) {
//        Toast.makeText(context, "Goal Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun taskItemClicked(taskModel: TaskModel) {
//        Toast.makeText(context, "Task Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun taskCompletedClicked(taskModel: TaskModel) {
        taskModel.status = 1
        itemViewModel.updateDataInTaskTable(taskModel)
    }

    override fun taskNotCompletedClicked(taskModel: TaskModel) {
        taskModel.status = 0
        itemViewModel.updateDataInTaskTable(taskModel)
    }

    override fun eventItemClicked(eventModel: EventModel) {
//        Toast.makeText(context, "Item Clicked", Toast.LENGTH_SHORT).show()
    }

}