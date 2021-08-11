package org.tech.repos.base.lib.cache

import androidx.room.*
import org.tech.repos.base.lib.cache.Cache

/**
 * Author: xuan
 * Created on 2021/7/9 10:10.
 *
 * Describe:
 */
@Dao
interface CacheDao {

    @Insert(entity = Cache::class,onConflict = OnConflictStrategy.REPLACE)
    fun saveCache(cache: Cache): Long

    @Query("select * from cache where `key`=:key")
    fun getCache(key: String): Cache?
    
    @Delete(entity = Cache::class)
    fun delete(cache: Cache)

}