package com.sherryyuan.wordy.database

import androidx.room.TypeConverter
import com.sherryyuan.wordy.entitymodels.Goal
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Date


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

    private val adapter: JsonAdapter<Goal> = moshi.adapter(Goal::class.java)

    @TypeConverter
    fun fromGoal(goal: Goal?): String? {
        return goal?.let { adapter.toJson(it) }
    }

    @TypeConverter
    fun toGoal(json: String?): Goal? {
        return json?.let { adapter.fromJson(it) }
    }
}

class DateJsonAdapter {
    @ToJson
    fun toJson(date: Date): Long {
        return date.time
    }

    @FromJson
    fun fromJson(timestamp: Long): Date {
        return Date(timestamp)
    }
}
