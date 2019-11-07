import mistermusik.commons.Contact;
import mistermusik.commons.Goal;
import mistermusik.commons.budgeting.Budgeting;
import mistermusik.commons.budgeting.CostExceedsBudgetException;
import mistermusik.commons.events.eventtypes.Event;
import mistermusik.commons.events.eventtypes.eventsubclasses.Concert;
import mistermusik.commons.events.eventtypes.eventsubclasses.ToDo;
import mistermusik.commons.events.eventtypes.eventsubclasses.assessmentsubclasses.Exam;
import mistermusik.commons.events.eventtypes.eventsubclasses.assessmentsubclasses.Recital;
import mistermusik.commons.events.eventtypes.eventsubclasses.recurringeventsubclasses.Lesson;
import mistermusik.commons.events.eventtypes.eventsubclasses.recurringeventsubclasses.Practice;
import mistermusik.commons.events.formatting.DateStringValidator;
import mistermusik.commons.events.formatting.EventDate;
import mistermusik.logic.ClashException;
import mistermusik.logic.EndBeforeStartException;
import mistermusik.logic.EventList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {


    //@@author Ryan-Wong-Ren-Wei
    @Test
    /**
     * test clash handling for single event addition
     */
    public void clashTest(){
        ArrayList<String> readFromFile = new ArrayList<String>();
        String fileContent;
        fileContent = "XT/fawpeifwe/02-12-2019";
        readFromFile.add(fileContent);
        fileContent = "XP/apiejfpwiefw/03-12-2019 1500/03-12-2019 1800";
        readFromFile.add(fileContent);
        fileContent = "XC/halloween/04-12-2019 1600/04-12-2019 1930/13";
        readFromFile.add(fileContent);

        EventList eventListTest = new EventList(readFromFile);
        Event testEvent = new Practice("Horn practice", "3-12-2019 1400",
                "3-12-2019 1600");
        try {
            eventListTest.addEvent(testEvent);
        } catch (ClashException e){
            assertEquals(e.getClashEvent(), eventListTest.getEvent(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * Test clash handling for recurring events
     */
    public void clashTestRecurring() {
        ArrayList<String> readFromFile = new ArrayList<String>();
        String fileContent;
        fileContent = "XT/fawpeifwe/02-12-2019";
        readFromFile.add(fileContent);
        fileContent = "XP/apiejfpwiefw/03-12-2019 1500/03-12-2019 1800";
        readFromFile.add(fileContent);
        fileContent = "XC/halloween/04-12-2019 1600/04-12-2019 1930/3";
        readFromFile.add(fileContent);

        EventList eventListTest = new EventList(readFromFile);
        Event testEvent = new Practice("Horn practice", "28-11-2019 1400",
                "28-11-2019 1600");
        try {
            eventListTest.addRecurringEvent(testEvent, 4);
        } catch (ClashException e){
            assertEquals(e.getClashEvent(), eventListTest.getEvent(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSorting() throws Exception {
        ArrayList<String> readFromFile = new ArrayList<String>();
        String fileContent;
        fileContent = "XT/fawpeifwe/02-12-2019";
        readFromFile.add(fileContent);
        fileContent = "XP/apiejfpwiefw/03-12-2019 1500/03-12-2019 1800";
        readFromFile.add(fileContent);
        fileContent = "XC/halloween/04-12-2019 1600/04-12-2019 1930/5";
        readFromFile.add(fileContent);

        EventList eventListTest = new EventList(readFromFile);
        boolean succeeded = true;
        Event testEvent1 = new Practice("Horn practice", "05-12-2019 1400",
                "05-12-2019 1600");
        Event testEvent2 = new Lesson("Full Orchestra rehearsal", "03-12-2019 1400",
                "03-12-2019 1500");
        Event testEvent3 = new ToDo("Complete theory homework CS2113", "01-12-2019");

        eventListTest.addEvent(testEvent1);
        eventListTest.addEvent(testEvent2);
        eventListTest.addNewTodo(testEvent3);
        eventListTest.sortList();
        ArrayList<Event> eventListCompare = new ArrayList<>();

        eventListCompare.add(new ToDo("Complete theory homework CS2113", "01-12-2019"));
        eventListCompare.add(new ToDo("fawpeifwe", "02-12-2019"));
        eventListCompare.add(new Lesson("Full Orchestra rehearsal", "03-12-2019 1400", "03-12-2019 1500"));
        eventListCompare.add(new Practice("apiejfpwiefw", "03-12-2019 1500", "03-12-2019 1800"));
        eventListCompare.add(new Concert("halloween", "04-12-2019 1600", "04-12-2019 1930", 5));
        eventListCompare.add(new Practice("Horn practice", "05-12-2019 1400", "05-12-2019 1600"));

        int i = 0;
        for (Event currEvent : eventListTest.getEventArrayList()) {
//            System.out.println(currEvent.toString());
//            System.out.println(eventListCompare.get(i).toString());
            if (!currEvent.toString().equals(eventListCompare.get(i).toString())) {
                succeeded = false;
            }
            ++i;
        }

        assertEquals(true, succeeded);
    }

    @Test
    public void testBudget () {
        ArrayList<String> readFromFile = new ArrayList<String>();
        String fileContent;
        fileContent = "XT/fawpeifwe/02-12-2019";
        readFromFile.add(fileContent);
        fileContent = "XP/apiejfpwiefw/03-12-2019 1500/03-12-2019 1800";
        readFromFile.add(fileContent);
        fileContent = "XC/halloween/04-12-2019 1600/04-12-2019 1930/5";
        readFromFile.add(fileContent);

        EventList eventListTest = new EventList(readFromFile);
        boolean succeededInAddingConcert;
        try {
            eventListTest.addEvent(new Concert("good concert", "05-12-2019 1500",
                    "05-12-2019 1600",44));
            succeededInAddingConcert = true;
        } catch (CostExceedsBudgetException | EndBeforeStartException | ClashException e) {
            System.out.println("1");
            succeededInAddingConcert = false;
        }
        assertTrue(succeededInAddingConcert);

        boolean CostExceededBudget = false;
        try {
            eventListTest.addEvent(new Concert("good concert", "06-12-2019 1500",
                    "06-12-2019 1600",2));
        } catch (CostExceedsBudgetException e) { //entry should exceed cost
            CostExceededBudget = true;
        } catch (ClashException | EndBeforeStartException e) {
        }

        assertTrue(CostExceededBudget);
    }

    @Test
    public void testSetBudget() {
        Budgeting testBudgeting = new Budgeting(new ArrayList<Event>(), 5);
        assertEquals(5, testBudgeting.getBudget());

        try {
            testBudgeting.updateMonthlyCost(new Concert("test1", "2-12-2019 1500",
                    "2-12-2019 1600", 6));
            fail();
        } catch (CostExceedsBudgetException e) {
        }

        testBudgeting.setBudget(75);
        assertEquals(75, testBudgeting.getBudget());

        try {
            testBudgeting.updateMonthlyCost(new Concert("test1", "2-12-2019 1500",
                    "2-12-2019 1600", 5));
        } catch (CostExceedsBudgetException e) {
            fail();
        }

        try {
            testBudgeting.updateMonthlyCost(new Concert("test2", "2-12-2019 1500",
                    "2-12-2019 1600", 5));
        } catch (CostExceedsBudgetException e) {
            fail();
        }

        try {
            testBudgeting.updateMonthlyCost(new Concert("test3", "2-12-2019 1500",
                    "2-12-2019 1600", 5));
        } catch (CostExceedsBudgetException e) {
            fail();
        }

        try {
            testBudgeting.updateMonthlyCost(new Concert("test4", "2-12-2019 1500",
                    "2-12-2019 1600", 61));
            fail();
        } catch (CostExceedsBudgetException e) {
        }
    }

    @Test
    public void dateValidatorTestEvent() {
        String correctString1 = "14-12-2019 1500";
        String correctString2 = "12-05-4938 1800";
        String correctString3 = "05-05-2000 0800";
        String wrongString1 = "5-5-5-5-3513";
        String wrongString2 = "5-5-3 3301";
        String wrongString3 = "21-12-1900 6000 7000";
        String wrongString4 = "alkjawfwe";
        String wrongString5 = "21-12-2019 15awawer";

        assertTrue(DateStringValidator.isValidDateForEvent(correctString1));
        assertTrue(DateStringValidator.isValidDateForEvent(correctString2));
        assertTrue(DateStringValidator.isValidDateForEvent(correctString3));

        assertFalse(DateStringValidator.isValidDateForEvent(wrongString1));
        assertFalse(DateStringValidator.isValidDateForEvent(wrongString2));
        assertFalse(DateStringValidator.isValidDateForEvent(wrongString3));
        assertFalse(DateStringValidator.isValidDateForEvent(wrongString4));
        assertFalse(DateStringValidator.isValidDateForEvent(wrongString5));
    }

    @Test
    public void dateValidatorTestToDo() {
        String correctString1 = "14-12-2019";
        String correctString2 = "12-05-4938";
        String correctString3 = "5-5-2000";

        String wrongString1 = "5-5--3931-5-3513";
        String wrongString2 = "5-5dsafs-3 3301";
        String wrongString3 = "21-12 6000 7000";
        String wrongString4 = "alkjawfwe";
        String wrongString5 = "50-50-50";

        assertTrue(DateStringValidator.isValidDateForToDo(correctString1));
        assertTrue(DateStringValidator.isValidDateForToDo(correctString2));
        assertTrue(DateStringValidator.isValidDateForToDo(correctString3));

        assertFalse(DateStringValidator.isValidDateForToDo(wrongString1));
        assertFalse(DateStringValidator.isValidDateForToDo(wrongString2));
        assertFalse(DateStringValidator.isValidDateForToDo(wrongString3));
        assertFalse(DateStringValidator.isValidDateForToDo(wrongString4));
        assertFalse(DateStringValidator.isValidDateForToDo(wrongString5));
    }

    //@@author
    @Test
    public void goalsListTest() throws CostExceedsBudgetException, EndBeforeStartException, ClashException {
        ArrayList<String> testListString = new ArrayList<>();
        EventList testList = new EventList(testListString);
        Event practiceTest1 = new Practice("band rehearsal", "12-12-2019 1800", "12-12-2019 2100");
        testList.addEvent(practiceTest1);
        Goal practiceGoal1 = new Goal("Finish Flight of the Bumblebee");
        testList.getEvent(0).addGoal(practiceGoal1);
        int goalIndex = 1;
        String testOutput = "";
        for (Goal goalObject : practiceTest1.getGoalList()) {
            testOutput += goalIndex + ". " + goalObject.getGoal() + " - " + "Achieved: " + goalObject.getStatus();
            goalIndex += 1;
        }
        boolean isGoalFound = !testOutput.isEmpty();
        //testing if added successfully
        assertEquals(true, isGoalFound);

        Goal practiceGoal2 = new Goal("Finish Symphony No.9");
        testList.getEvent(0).editGoalList(practiceGoal2, 0);
        boolean isUpdated = false;
        if (testList.getEvent(0).getGoalList().get(0).getGoal().equals("Finish Symphony No.9")) {
            isUpdated = true;
        }
        //testing if edited successfully
        assertEquals(true, isUpdated);


    }

    //@@author
    @Test
    public void viewScheduleTest() throws CostExceedsBudgetException, EndBeforeStartException, ClashException {
        ArrayList<String> testListString = new ArrayList<>();
        EventList testList = new EventList(testListString);
        Event toDoTest = new ToDo("cheese", "19-09-2019");
        testList.addNewTodo(toDoTest);
        Event practiceTest1 = new Practice("individual practice", "19-09-2019 1900", "19-09-2019 2000");
        testList.addEvent(practiceTest1);
        Event practiceTest2 = new Practice("sectional practice", "19-09-2019 2100", "19-09-2019 2200");
        testList.addEvent(practiceTest2);
        Event practiceTest3 = new Practice("full band rehearsal", "19-09-2020 1000", "19-09-2020 1100");
        testList.addEvent(practiceTest3);
        Event eventTest = new Recital("band recital", "20-09-2019 2100", "20-09-2019 2200");
        testList.addEvent(eventTest);
        String dateToView = "19-09-2019";
        String foundTask = "";
        int viewIndex = 1;
        EventDate findDate = new EventDate(dateToView);
        for (Event testViewTask : testList.getEventArrayList()) {
            if (testViewTask.toString().contains(findDate.getFormattedDateString())) {
                foundTask += viewIndex + ". " + testViewTask.toString() + "\n";
                viewIndex++;
            }
        }
        boolean isTasksFound = !foundTask.isEmpty();
        assertEquals(true, isTasksFound);
    }

    //@@author YuanJiayi
    @Test
    public void addRecurringEventTest() throws ClashException {
        ArrayList<String> testListString = new ArrayList<>();
        EventList testList = new EventList(testListString);

        // test practice type
        Event practiceTest = new Practice("practice 1", "06-11-2019 1200", "06-11-2019 1400");
        testList.addRecurringEvent(practiceTest, 60);
        assertEquals(2, testList.getNumEvents());

        // test lesson type
        Event lessonTest = new Lesson("lesson 1", "13-08-2019 1000", "13-08-2019 1200");
        testList.addRecurringEvent(lessonTest, 35);
        assertEquals(6, testList.getNumEvents());

        // test the period larger than one semester
        Event largePeriodTest = new Practice("practice 2", "23-09-2019 0900", "23-09-2019 1000");
        testList.addRecurringEvent(largePeriodTest, 113);
        assertEquals(7, testList.getNumEvents());

        // test the period exactly one semester (112 days)
        Event exactOneSemesterPeriodTest = new Lesson("lesson 2", "07-10-2019 0800", "07-10-2019 0900");
        testList.addRecurringEvent(exactOneSemesterPeriodTest, 112);
        assertEquals(9, testList.getNumEvents());

        // test the period just shorter than 112 days
        Event smallPeriodTest = new Practice("practice 3", "14-12-2019 1800", "14-12-2019 1900");
        testList.addRecurringEvent(smallPeriodTest, 111);
        assertEquals(11, testList.getNumEvents());

        // test recurring lesson with "isDone"
        Event notDoneLessonTest = new Lesson("lesson", false,"01-01-2020 2200", "01-01-2020 2300");
        testList.addRecurringEvent(notDoneLessonTest, 120);
        assertEquals(12, testList.getNumEvents());

        // test clash
        Event clashTest = new Lesson("lesson 3", "14-12-2019 1800", "14-12-2019 1900");
        try {
            testList.addRecurringEvent(clashTest, 100);
        } catch (ClashException e) {
            assertEquals(e.getClashEvent().toString(), clashTest.toString());
        }
    }

    @Test
    public void rescheduleStartDateTest() {
        ArrayList<String> readFromFile = new ArrayList<>();
        String fileContent;
        fileContent = "XP/practice 1 /03-12-2019 1500/03-12-2019 1800";
        readFromFile.add(fileContent);
        EventList eventListTest = new EventList(readFromFile);
        // test reschedule start date and time of an event
        Event practiceTest = eventListTest.getEvent(0);
        EventDate newPracticeStartDate = new EventDate("09-11-2019 0000");
        practiceTest.rescheduleStartDate(newPracticeStartDate);
        assertEquals(newPracticeStartDate, practiceTest.getStartDate());
    }

    @Test
    public void rescheduleEndDateTest() {
        ArrayList<String> readFromFile = new ArrayList<>();
        String fileContent;
        fileContent = "XP/practice 1 /03-12-2019 1500/03-12-2019 1800";
        readFromFile.add(fileContent);
        EventList eventListTest = new EventList(readFromFile);
        Event practiceTest = eventListTest.getEvent(0);
        // test reschedule end date and time of an event
        EventDate newPracticeEndDate = new EventDate("09-11-2019 0100");
        practiceTest.rescheduleEndDate(newPracticeEndDate);
        assertEquals(newPracticeEndDate, practiceTest.getEndDate());
    }

    @Test
    public void addContactTest() throws CostExceedsBudgetException, EndBeforeStartException, ClashException {
        ArrayList<String> testListString = new ArrayList<>();
        EventList testList = new EventList(testListString);
        Event eventTest = new Practice("eventTest", "12-12-2019 1200", "12-12-2019 1300");
        testList.addEvent(eventTest);
        Contact normalContact = new Contact("name 1", "email 1", "phone 1");
        //test if contact is added
        testList.getEvent(0).addContact(normalContact);
        assertTrue(eventTest.getContactList().contains(normalContact));
    }

    @Test
    public void removeContactTest() throws CostExceedsBudgetException, EndBeforeStartException, ClashException {
        ArrayList<String> testListString = new ArrayList<>();
        EventList testList = new EventList(testListString);
        Event eventTest = new Lesson("eventTest", "12-12-2019 1200", "12-12-2019 1300");
        testList.addEvent(eventTest);
        Contact normalContact = new Contact("name 1", "email 1", "phone 1");
        testList.getEvent(0).addContact(normalContact);
        //test if contact is removed
        testList.getEvent(0).removeContact(0);
        assertFalse(eventTest.getContactList().contains(normalContact));
    }

    @Test
    public void viewContactTest() throws CostExceedsBudgetException, EndBeforeStartException, ClashException {
        ArrayList<String> testListString = new ArrayList<>();
        EventList testList = new EventList(testListString);
        Event eventTest = new Exam("eventTest", "12-12-2019 1200", "12-12-2019 1300");
        testList.addEvent(eventTest);
        Contact contact1 = new Contact("name 1", "email 1", "phone 1");
        Contact contact2 = new Contact("name 2", "", "phone 2");
        testList.getEvent(0).addContact(contact1);
        testList.getEvent(0).addContact(contact2);
        assertEquals("name 1", testList.getEvent(0).getContactList().get(0).getName());
        assertEquals("name 2", testList.getEvent(0).getContactList().get(1).getName());
        assertEquals("email 1", testList.getEvent(0).getContactList().get(0).getEmail());
        assertEquals("", testList.getEvent(0).getContactList().get(1).getEmail());
        assertEquals("phone 1", testList.getEvent(0).getContactList().get(0).getPhoneNo());
        assertEquals("phone 2", testList.getEvent(0).getContactList().get(1).getPhoneNo());
    }

    @Test
    public void editContactTest() throws CostExceedsBudgetException, EndBeforeStartException, ClashException {
        ArrayList<String> testListString = new ArrayList<>();
        EventList testList = new EventList(testListString);
        Event eventTest = new Recital("eventTest", "12-12-2019 1200", "12-12-2019 1300");
        testList.addEvent(eventTest);
        Contact contact1 = new Contact("name 1", "email 1", "phone 1");
        testList.getEvent(0).addContact(contact1);
        int contactIndex = 0;

        //test if name edited
        String newName = "name a";
        testList.getEvent(0).editContact(contactIndex, 'N', newName);
        assertEquals(newName, testList.getEvent(0).getContactList().get(contactIndex).getName());
        newName = "name";
        eventTest.getContactList().get(contactIndex).setName(newName);
        assertEquals(newName, eventTest.getContactList().get(contactIndex).getName());

        //test if email edited
        String newEmail = "email a";
        testList.getEvent(0).editContact(contactIndex, 'E', newEmail);
        assertEquals(newEmail, testList.getEvent(0).getContactList().get(contactIndex).getEmail());
        newEmail = "email";
        eventTest.getContactList().get(contactIndex).setEmail(newEmail);
        assertEquals(newEmail, eventTest.getContactList().get(contactIndex).getEmail());

        //test if phone number is edited
        String newPhone = "phone a";
        testList.getEvent(0).editContact(contactIndex, 'P', newPhone);
        assertEquals(newPhone, testList.getEvent(0).getContactList().get(contactIndex).getPhoneNo());
        newPhone = "phone";
        eventTest.getContactList().get(contactIndex).setPhoneNo(newPhone);
        assertEquals(newPhone, eventTest.getContactList().get(contactIndex).getPhoneNo());
    }

    //@@author
//
//
//    @Test
//    public void checkFreeDaysTest() {
//        ArrayList<String> taskListString = new ArrayList<>();
//        EventList testList = new EventList(taskListString);
//        Task toDoTest = new ToDo("B-extensions");
//        testList.addTask(toDoTest);
//        Task deadlineTest1 = new Deadline("finish extension", "21/09/2019 1900");
//        testList.addTask(deadlineTest1);
//        Task deadlineTest2 = new Deadline("submit report", "22/09/2019 2000");
//        testList.addTask(deadlineTest2);
//        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
//        EventDate today = new EventDate(f.format(new Date()));
//        Queue<String> daysFree = new LinkedList<String>();
//        int nextDays = 1;
//        while (daysFree.size() <= 3) {
//            boolean flagFree = true;
//            for (Task viewTask : testList.getTaskArrayList()) {
//                if (viewTask.toString().contains(today.toOutputString())) {
//                    flagFree = false;
//                    break;
//                }
//            }
//            if (flagFree) {
//                daysFree.add(today.toOutputString());
//            }
//            today.addDays(nextDays);
//        }
//        boolean checkFreeFlag = false;
//        if (daysFree.poll().equals("19 SEP 2019")) {
//            checkFreeFlag = true;
//        }
//        assertEquals(true, checkFreeFlag);
//    }
//
//    @Test
//    public void reminderTest () {
//
//    	ArrayList<String> testcase = new ArrayList<String>();
//    	ArrayList<String> all = new ArrayList<String>();
//    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HHmm");
//
//    	// case 1: task due long ago (printed)
//    	Task dueLongAgo = new Deadline("longAgo", "09/08/1965 0000");
//    	all.add(dueLongAgo.toString());
//    	testcase.add(dueLongAgo.toString());
//
//    	// case 2: task due now (printed)
//    	Date now = new Date();
//    	Calendar c = Calendar.getInstance();
//    	c.setTime(now);
//    	String nowStr = formatter.format(now);
//    	Task dueNow = new Deadline("now", nowStr);
//    	all.add(dueNow.toString());
//    	testcase.add(dueNow.toString());
//
//    	// case 3: task due 2 days later (printed)
//    	c.add(Calendar.DATE, 2);
//    	Date twoDays = c.getTime();
//    	String twoDaysStr = formatter.format(twoDays);
//    	Task dueTwoDays = new Deadline("twoDays", twoDaysStr);
//    	all.add(dueTwoDays.toString());
//    	testcase.add(dueTwoDays.toString());
//
//    	// case 4: task due 3 days later (printed)
//    	c.add(Calendar.DATE, 1);
//    	Date threeDays = c.getTime();
//    	String threeDaysStr = formatter.format(threeDays);
//    	Task dueThreeDays = new Deadline("threeDays", threeDaysStr);
//    	all.add(dueThreeDays.toString());
//    	testcase.add(dueThreeDays.toString());
//
//    	// case 5: task due 4 days later (not printed)
//    	c.add(Calendar.DATE, 1);
//    	Date fourDays = c.getTime();
//    	String fourDaysStr = formatter.format(fourDays);
//    	Task dueFourDays = new Deadline("fourDays", fourDaysStr);
//    	all.add(dueFourDays.toString());
//
//    	// case 6: task due 10 days later (not printed)
//    	c.add(Calendar.DATE, 6);
//    	Date tenDays = c.getTime();
//    	String tenDaysStr = formatter.format(tenDays);
//    	Task dueTenDays = new Deadline("tenDays", tenDaysStr);
//    	all.add(dueTenDays.toString());
//
//    	EventList expected = new EventList(testcase);
//    	EventList allitms = new EventList(all);
//
//    	EventDate limit = new EventDate();
//    	limit.addDays(4);
//    	limit.setMidnight();
//    	Predicate<Object> pred = new Predicate<>(limit, GREATER_THAN);
//    	String cmp = expected.listOfTasks_String();
//    	String result = allitms.filteredlist(pred, DATE);
//
//    	assertEquals(cmp, result);
//    }
}
