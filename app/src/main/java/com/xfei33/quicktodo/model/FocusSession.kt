import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey val id: UUID = UUID.randomUUID(), // 主键
    val userId: Long, // 外键，用户ID
    val startTime: LocalDateTime, // 起始时间
    val endTime: LocalDateTime,   // 终止时间
    val duration: Long              // 时长（毫秒）
) 