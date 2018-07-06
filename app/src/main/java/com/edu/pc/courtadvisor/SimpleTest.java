//package com.edu.pc.courtadvisor;
//
//
//import android.arch.persistence.room.Room;
//import android.content.Context;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.hamcrest.core.IsEqual.equalTo;
//import static org.junit.Assert.assertThat;
//
//
//@RunWith(AndroidJUnit4.class)
//public class SimpleTest {
//
//    private UserDao mUserDao;
//    private AppDatabase mDb;
//
//    @Before
//    public void createDb() {
//        Context context = InstrumentationRegistry.getTargetContext();
//        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
//        mUserDao = mDb.getUserDao();
//    }
//
//    @After
//    public void closeDb() throws IOException {
//        mDb.close();
//    }
//
//    @Test
//    public void writeUserAndReadInList() throws Exception {
//        User user = TestUtil.createUser(3);
//        user.setName("George", "Micheal");
//        mUserDao.insertUsers(user);
//        List<User> byName = mUserDao.findUsersByName("George", "Michael");
//        assertThat(byName.get(0), equalTo(user));
//    }
//}