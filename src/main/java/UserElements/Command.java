package UserElements;

import Events.EventTypes.Event;
import Events.EventTypes.EventSubclasses.Concert;
import Events.EventTypes.EventSubclasses.RecurringEventSubclasses.Practice;
import Events.EventTypes.EventSubclasses.ToDo;
import Events.Formatting.DateObj;
import Events.Storage.Storage;
import Events.Storage.EventList;
import Events.EventTypes.EventSubclasses.RecurringEventSubclasses.Lesson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Represents a command that is passed via user input.
 * Multiple types of commands are possible, executed using switch case method.
 */
public class Command {

    /**
     * The String representing the type of command e.g add/delete event
     */
    protected String command;

    /**
     * The String representing the continuation of the command, if it exists.
     * Contains further specific instructions about the command passed e.g which event to add or delete
     */
    protected String continuation;

    /**
     * Creates a new command with the command type and specific instructions
     *
     * @param command      The Model_Class.Command type
     * @param continuation The Model_Class.Command specific instructions
     */
    public Command(String command, String continuation) {
        this.command = command;
        this.continuation = continuation;
    }

    /**
     * Creates a new command where only command param is passed.
     * Specific instructions not necessary for these types of commands.
     *
     * @param command The Model_Class.Command type
     */
    public Command(String command) {
        this.command = command;
        this.continuation = "";
    }

    /**
     * Executes the command stored.
     *
     * @param events  Class containing the list of events and all relevant methods.
     * @param ui      Class containing all relevant user interface instructions.
     * @param storage Class containing access to the storage file and related instructions.
     */
    public void execute(EventList events, UI ui, Storage storage) {
        boolean changesMade = true;
        switch (command) {
            case "list":
                listEvents(events, ui);
                changesMade = false;
                break;

            case "reminder":
                remindEvents(events, ui);
                changesMade = false;
                break;

            case "done":
                markEventAsDone(events, ui);
                break;

            case "delete":
                deleteEvent(events, ui);
                break;

            case "find":
                searchEvents(events, ui);
                changesMade = false;
                break;

            case "todo":
                createNewTodo(events, ui);
                break;

            case "lesson":
                createNewEvent(events, ui, 'L');
                break;

            case "concert":
                createNewEvent(events, ui, 'C');
                break;

            case "practice":
                createNewEvent(events, ui, 'P');
                break;

            case "view":
                viewEvents(events, ui);
                changesMade = false;
                break;

            case "check":
                checkFreeDays(events, ui);
                changesMade = false;
                break;

            default:
                ui.printInvalidCommand();
                changesMade = false;
                break;
        }
        if (changesMade) {
            storage.saveToFile(events, ui);
        }
    }

    private void searchEvents(EventList events, UI ui) {
        if (continuation.isEmpty()) {
            ui.eventDescriptionEmpty();
        } else {
            String searchKeyWords = continuation;
            String foundEvent = "";
            int viewIndex = 1;
            for (Event viewEvent : events.getEventArrayList()) {
                if (viewEvent.toString().contains(searchKeyWords)) {
                    foundEvent += viewIndex + ". " + viewEvent.toString() + "\n";
                    viewIndex++;
                }
            }
            boolean isEventsFound = !foundEvent.isEmpty();
            ui.printFoundEvents(foundEvent, isEventsFound);
        }
    }

    public void checkFreeDays(EventList events, UI ui) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar dayToCheckIfFree = Calendar.getInstance();
        DateObj dayToCheckIfFreeObject = new DateObj(formatter.format(dayToCheckIfFree.getTime()));
        Queue<String> daysFree = new LinkedList<String>();
        int nextDays = 1;
        while (daysFree.size() <= 3) {
            boolean flagFree = true;
            for (Event viewEvent : events.getEventArrayList()) {
                dayToCheckIfFreeObject.formatDate();
                if (viewEvent.toString().contains(dayToCheckIfFreeObject.getFormattedDateString())) {
                    flagFree = false;
                    break;
                }
            }
            if (flagFree) {
                dayToCheckIfFreeObject.formatDate();
                daysFree.add(dayToCheckIfFreeObject.getFormattedDateString());
            }
            dayToCheckIfFreeObject.addDaysAndSetMidnight(nextDays);
            nextDays++;
        }
        ui.printFreeDays(daysFree);
    }

    public void viewEvents(EventList events, UI ui) {
        if (continuation.isEmpty()) {
            ui.eventDescriptionEmpty();
        } else {
            String dateToView = continuation;
            String foundEvent = "";
            int viewIndex = 1;
            DateObj findDate = new DateObj(dateToView);
            for (Event viewEvent : events.getEventArrayList()) {
                findDate.formatDate();
                if (viewEvent.toString().contains(findDate.getFormattedDateString())) {
                    foundEvent += viewIndex + ". " + viewEvent.toString() + "\n";
                    viewIndex++;
                }
            }
            boolean isEventsFound = !foundEvent.isEmpty();
            ui.printFoundEvents(foundEvent, isEventsFound);
        }
    }

    public void createNewEvent(EventList events, UI ui, char eventType) {
        if (continuation.isEmpty()) {
            ui.eventDescriptionEmpty();
        } else {
            int NO_PERIOD = -1;

            try {
                EntryForEvent entryForEvent = new EntryForEvent().invoke(); //separate all info into relevant details
                Event newEvent = null;
                switch (eventType) {
                    case 'L':
                        newEvent = new Lesson(entryForEvent.getDescription(), false, entryForEvent.getStartDate(),
                                entryForEvent.getEndDate());
                        break;
                    case 'C':
                        newEvent = new Concert(entryForEvent.getDescription(), false, entryForEvent.getStartDate(),
                                entryForEvent.getEndDate());
                        break;
                    case 'P':
                        newEvent = new Practice(entryForEvent.getDescription(), false, entryForEvent.getStartDate(),
                                entryForEvent.getEndDate());
                        break;
                    case 'E':


                    case 'R':


                }
                boolean succeeded;

                if (entryForEvent.getPeriod() == NO_PERIOD) { //add non-recurring event
                    succeeded = events.addEvent(newEvent);
                } else { //add recurring event
                    succeeded = events.addRecurringEvent(newEvent, entryForEvent.getPeriod());
                }

                if (succeeded) {
                    if (entryForEvent.getPeriod() == NO_PERIOD) {
                        ui.eventAdded(newEvent, events.getNumEvents());
                    } else {
                        ui.recurringEventAdded(newEvent, events.getNumEvents(), entryForEvent.getPeriod());
                    }
                } else {
                    ui.scheduleClash(newEvent);
                }
            } catch (StringIndexOutOfBoundsException outOfBoundsE) {
                ui.eventFormatWrong();
            }
        }
    }

    public void createNewTodo(EventList events, UI ui) {
        if (continuation.isEmpty()) {
            ui.eventDescriptionEmpty();
            return;
        }
        EntryForEvent entryForEvent = new EntryForEvent().invoke(); //separate all info into relevant details
        Event newEvent = new ToDo(entryForEvent.getDescription(), entryForEvent.getStartDate());
        events.addEvent(newEvent);
        ui.eventAdded(newEvent, events.getNumEvents());
    }
//
//    public void searchEvents(EventList events, UI ui) {
//        String searchFor = continuation;
//        String allEventsFound = "";
//        int index = 1;
//        for (Event eventFound : events.getEventArrayList()) {
//            if (eventFound.getDescription().contains(searchFor)) {
//                allEventsFound += index + ". " + eventFound.toString() + "\n";
//            }
//            index++;
//        }
//
//        boolean eventsFound = !allEventsFound.isEmpty();
//        ui.searchEvents(allEventsFound, eventsFound);
//    }

    public void deleteEvent(EventList events, UI ui) {
        try {
            int eventNo = Integer.parseInt(continuation);
            Event currEvent = events.getEvent(eventNo - 1);
            events.deleteEvent(eventNo - 1);
            ui.eventDeleted(currEvent);
        } catch (IndexOutOfBoundsException outOfBoundsE) {
            ui.noSuchEvent();
        } catch (NumberFormatException notInteger) {
            ui.notAnInteger();
        }
    }

    public void markEventAsDone(EventList events, UI ui) {
        try {
            int eventNo = Integer.parseInt(continuation);
            if (events.getEvent(eventNo - 1) instanceof ToDo) {
                events.getEvent(eventNo - 1).markAsDone();
                ui.eventDone(events.getEvent(eventNo - 1));
            } else {
                ui.noSuchEvent();
            }
        } catch (IndexOutOfBoundsException outOfBoundsE) {
            ui.noSuchEvent();
        } catch (NumberFormatException notInteger) {
            ui.notAnInteger();
        }
    }

    public void remindEvents(EventList events, UI ui) {
        ui.printReminder(events);
    }

    public void listEvents(EventList events, UI ui) {
        ui.printListOfEvents(events);
    }

    /**
     * Contains all info concerning a new entry for a recurring event.
     */
    private class EntryForEvent {
        private String description;
        private String startDate;
        private String endDate;
        private int period;

        public String getDescription() {
            return description;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public int getPeriod() {
            return period;
        }

        /**
         * contains all info regarding an entry for a non-recurring event
         *
         * @return
         */
        public EntryForEvent invoke() {
            int NON_RECURRING = -1;
            String[] splitEvent = continuation.split("/");
            description = splitEvent[0];

            String date = splitEvent[1];
            String[] splitDate = date.split(" ");

            if (splitDate.length == 3) {
                startDate = splitDate[0] + " " + splitDate[1];
                endDate = splitDate[0] + " " + splitDate[2];
            } else if (splitDate.length == 2){
                startDate = splitDate[0] + " " + splitDate[1];
                endDate = "";
            } else {
                startDate = splitDate[0];
                endDate = "";
            }

            if (splitEvent.length == 2) {//cant find period extension of command, event is non-recurring
                period = NON_RECURRING;
            } else {
                period = Integer.parseInt(splitEvent[2]);
            }

            return this;
        }
    }
}