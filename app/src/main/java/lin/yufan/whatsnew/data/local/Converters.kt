package lin.yufan.whatsnew.data.local

import androidx.room.TypeConverter
import lin.yufan.whatsnew.data.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source) = source.name

    @TypeConverter
    fun toSource(name: String) = Source(name, name)
}