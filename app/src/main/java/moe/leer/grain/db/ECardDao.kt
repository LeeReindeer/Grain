package moe.leer.grain.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.leer.grain.model.ECard
import java.util.*

/**
 *
 * Created by leer on 1/19/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
@Dao
interface ECardDao {

    //TODO when connected to network, fetch last 30 days's data(about 200 items) to db
    // So with local data growing up, we can query items by date, because server's API not support this
    // Todo Support data export to file
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(items: ArrayList<ECard>)

    /**
     * Select items with paging, pageIndex start from 1
     * @see moe.leer.grain.FuckSchoolApi.DEFAULT_PAGE_SIZE
     */
//    @Query("SELECT * FROM card_items ORDER BY time LIMIT :pageSize OFFSET ((:pageIndex - 1 ) * :pageSize)")
//    fun get(pageIndex: Int, pageSize: Int): LiveData<ArrayList<ECard>>

    @Query("SELECT * FROM card_items ORDER BY time DESC")
    fun get(): DataSource.Factory<Int, ECard>

    // fixme Select items within a day
    @Query("SELECT * FROM card_items WHERE time BETWEEN :from AND :to ORDER BY time DESC")
    fun getItemsByDate(from: Date, to: Date): DataSource.Factory<Int, ECard>
}