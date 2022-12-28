package com.aro.jcalendar.util;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.aro.jcalendar.data.CalendarDao;
import com.aro.jcalendar.data.CategoryDao;
import com.aro.jcalendar.data.CounterDao;
import com.aro.jcalendar.data.TaskDao;
import com.aro.jcalendar.model.Calendar;
import com.aro.jcalendar.model.Category;
import com.aro.jcalendar.model.Counter;
import com.aro.jcalendar.model.Task;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        version = 8,
        entities = {Calendar.class, Category.class, Task.class, Counter.class}

)
@TypeConverters({Converter.class})
public abstract class CalendarRoomDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "calendar_database";
    public static final int NUMBER_OF_THREADS = 4;
    public static volatile CalendarRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public abstract CalendarDao calendarDao();
    public abstract CategoryDao categoryDao();
    public abstract TaskDao taskDao();
    public abstract CounterDao counterDao();

    //this is called by addCallback when the database is first created
    public static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    databaseWriterExecutor.execute(()->{
                        // invoke Dao and write
                        CalendarDao calendarDao = INSTANCE.calendarDao();
                        calendarDao.deleteAll(); //clear the database when it is initially created

                        CategoryDao categoryDao = INSTANCE.categoryDao();
                        categoryDao.deleteAll(); //clear the database when it is initially created

                        TaskDao taskDao = INSTANCE.taskDao();
                        taskDao.deleteAll(); //clear the database when it is initially created

                        CounterDao counterDao = INSTANCE.counterDao();
                        counterDao.deleteAll(); //clear the database when it is initially created

                    });
                }
            };

    //constructor (singleton)
    public static CalendarRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (CalendarRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CalendarRoomDatabase.class, DATABASE_NAME)
                            .addCallback(sRoomDatabaseCallback)
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .addMigrations(MIGRATION_4_5)
                            .addMigrations(MIGRATION_5_6)
                            .addMigrations(MIGRATION_6_7)
                            .addMigrations(MIGRATION_7_8)
                            .build();
                }
            }
        }

        return INSTANCE;
    }


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS task_table " +
                    "(`task_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`task` TEXT, " +
                    "`priority` TEXT, " +
                    "`due_date` INTEGER, " +
                    "`created_at` INTEGER, " +
                    "`is_done` INTEGER, " +
                    "`category` TEXT)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS category_table " +
                    "(`categoryId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`categoryName` TEXT)");

        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE note_table");

        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE task_table " +
                    " ADD COLUMN `image_url` TEXT "
            );
            database.execSQL("ALTER TABLE task_table " +
                    " ADD COLUMN `color` TEXT"
            );

        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE task_table " +
                            " ADD COLUMN `color_int` INTEGER"
                    );
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE task_table " +
                    " ADD COLUMN `text_color_int` INTEGER"
            );
        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS counter_table " +
                    "(`counterId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`date_started` INTEGER, " +
                    "`counter_title` TEXT, " +
                    "`counter_additional_info` TEXT, " +
                    "`is_active` INTEGER, " +
                    "`date` INTEGER, " +
                    "`value` INTEGER)");

        }
    };
}
