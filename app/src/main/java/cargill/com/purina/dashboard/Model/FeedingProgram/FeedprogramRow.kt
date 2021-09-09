package cargill.com.purina.dashboard.Model.FeedingProgram

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "stages")
data class FeedprogramRow(
    @ColumnInfo(name = "age_days")
    val age_days: Int,
    @ColumnInfo(name = "comments")
    val comments: String,
    @ColumnInfo(name = "expected_wt")
    val expected_wt: Double,
    @ColumnInfo(name = "feed_norms")
    val feed_norms: Double,
    @ColumnInfo(name = "feedprogram_id")
    val feedprogram_id: Int,
    @ColumnInfo(name = "image_url")
    val image_url: String,
    @ColumnInfo(name = "inclusion_rate")
    val inclusion_rate: Int,
    @ColumnInfo(name = "mortality_rate")
    val mortality_rate: Double,
    @ColumnInfo(name = "recipe_code")
    val recipe_code: String? = "",
    @ColumnInfo(name = "recipe_name")
    val recipe_name: String? = "",
    @PrimaryKey
    @ColumnInfo(name = "stage_no")
    val stage_no: Int,
    @ColumnInfo(name = "numberOfAnimals")
    var numberOfAnimals:Double,
    @ColumnInfo(name = "feed_required")
    var feed_required: Double,
    @ColumnInfo(name = "additional_feed")
    var additional_feed: Int,
    @ColumnInfo(name = "bag_price")
    var bag_price: Int,
    @ColumnInfo(name = "feed_cost")
    var feed_cost: Int,
    @ColumnInfo(name = "accumulated_cost_kg")
    var accumulated_cost_kg: Double,
    @ColumnInfo(name = "accumulated_cost_head")
    var accumulated_cost_head: Double,
    @ColumnInfo(name = "completed_feed_equivalent")
    var completed_feed_equivalent: Int

):Parcelable