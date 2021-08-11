package org.tech.repos.base.lib.cache

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: xuan
 * Created on 2021/7/9 09:57.
 *
 * Describe:
 */
@Entity(tableName = "cache")
class Cache {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    var key: String = ""

    var data: ByteArray? = null
}