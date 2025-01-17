package dev.kingbond.notify.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.kingbond.notify.ui.event.EventModel
import dev.kingbond.notify.ui.goal.model.GoalModel
import dev.kingbond.notify.ui.task.model.TaskModel

@Dao
interface ClassDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDataInGoal(goalModel: GoalModel)

    @Query("select * from goal_table")
    fun fetchDataFromGoal():LiveData<List<GoalModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDataInTask(taskModel: TaskModel)

    @Update
    fun updateDataInTask(taskModel: TaskModel)

    @Query("select * from task_table where category = :goalName and status = 1")
    fun getCountOfCompletedTasks(goalName:String):LiveData<List<TaskModel>>

    @Query("select * from task_table where status = 1")
    fun getCompletedTask():LiveData<List<TaskModel>>

    @Query("select * from task_table")
    fun fetchDataFromTask():LiveData<List<TaskModel>>

    @Query("select * from task_table where category = :goalName")
    fun getTasksWithGoal(goalName: String): LiveData<List<TaskModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDataInEvent(eventModel: EventModel)

    @Query("select * from event_table")
    fun fetchDataFromEvent(): LiveData<List<EventModel>>

    @Query("select * from goal_table where name = :goalName")
    fun getGoalModel(goalName:String):LiveData<GoalModel>

    @Query("select * from task_table where date = :date")
    fun getTaskByDate(date:String):LiveData<List<TaskModel>>


}