package dev.kingbond.notify.repository

import androidx.lifecycle.LiveData
import dev.kingbond.notify.data.database.ClassDao
import dev.kingbond.notify.ui.event.EventModel
import dev.kingbond.notify.ui.goal.model.GoalModel
import dev.kingbond.notify.ui.task.model.TaskModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepositoryClass(val classDao: ClassDao) {

    fun addDataToGoal(goalModel: GoalModel) {
        CoroutineScope(Dispatchers.IO).launch {
            classDao.insertDataInGoal(goalModel)
        }
    }

    fun getAllGoals(): LiveData<List<GoalModel>> {
        return classDao.fetchDataFromGoal()
    }

    fun addDataToTask(taskModel: TaskModel) {
        CoroutineScope(Dispatchers.IO).launch {
            classDao.insertDataInTask(taskModel)
        }
    }

    fun updateToTask(taskModel: TaskModel){
        CoroutineScope(Dispatchers.IO).launch {
            classDao.updateDataInTask(taskModel)
        }
    }

    fun getCompletedTaskGoal(goalName: String):LiveData<List<TaskModel>>{
        return classDao.getCountOfCompletedTasks(goalName)
    }

    fun getAllTasks(): LiveData<List<TaskModel>> {
        return classDao.fetchDataFromTask()
    }

    fun getGoalTask(goalName: String): LiveData<List<TaskModel>> {
        return classDao.getTasksWithGoal(goalName)
    }

    fun addDataToEvent(eventModel: EventModel) {
        CoroutineScope(Dispatchers.IO).launch {
            classDao.insertDataInEvent(eventModel)
        }
    }

    fun getAllEvents(): LiveData<List<EventModel>> {
        return classDao.fetchDataFromEvent()
    }

    fun getOneGoal(goalName:String):LiveData<GoalModel>{
        return classDao.getGoalModel(goalName)
    }

    fun getCompletedTask():LiveData<List<TaskModel>>{
        return classDao.getCompletedTask()
    }

    fun getTasksByDate(date:String):LiveData<List<TaskModel>>{
        return classDao.getTaskByDate(date)
    }

}