package com.sherryyuan.wordy.database

import androidx.room.TypeConverter
import com.sherryyuan.wordy.entitymodels.Goal
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDate


class GoalTypeConverter {
    private val moshi: Moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(Goal::class.java, "type")
                .withSubtype(Goal.DeadlineGoal::class.java, "DeadlineGoal")
                .withSubtype(Goal.DailyWordCountGoal::class.java, "DailyWordCountGoal")
        )
        .add(DateJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val goalAdapter: JsonAdapter<Goal> = moshi.adapter(Goal::class.java)
    private val dateAdapter: JsonAdapter<LocalDate> = moshi.adapter(LocalDate::class.java)

    @TypeConverter
    fun fromGoal(goal: Goal?): String? {
        return goal?.let { goalAdapter.toJson(it) }
    }

    @TypeConverter
    fun toGoal(json: String?): Goal? {
        return json?.let { goalAdapter.fromJson(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.let { dateAdapter.toJson(it) }
    }

    @TypeConverter
    fun toLocalDate(json: String?): LocalDate? {
        return json?.let { dateAdapter.fromJson(it) }
    }
}

class DateJsonAdapter {
    @ToJson
    fun toJson(date: LocalDate): String {
        return date.toString()
    }

    @FromJson
    fun fromJson(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }
}
