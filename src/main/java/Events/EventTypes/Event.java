package Events.EventTypes;

import Events.Formatting.DateObj;

/**
 * Model_Class.Event object inherits Model_Class.Task.
 * Is a type of task available for use.
 */
public abstract class Event {
    protected String description;
    protected boolean isDone;
    protected DateObj startDateObj;
    protected DateObj endDateObj;
    protected char eventType;

    /**
     * Creates event with one date input (e.g todo)
     *
     * @param description event description
     * @param isDone      boolean representing state of event completion
     * @param dateAndTime string representing date of event
     */
    public Event(String description, boolean isDone, String dateAndTime) {
        this.description = description;
        this.isDone = isDone;
        this.startDateObj = new DateObj(dateAndTime);
        this.endDateObj = null; //no end date, set to null
        this.eventType = 'T'; //event with no end date can only be todo type
    }

    /**
     * Creates event with two date input
     *
     * @param description event description
     * @param isDone      boolean representing state of event completion
     * @param startDateAndTime string representing start date of event
     * @param endDateAndTime string representing end date of event
     */
    public Event(String description, boolean isDone, String startDateAndTime, String endDateAndTime, char eventType) {
        this.description = description;
        this.isDone = isDone;
        this.startDateObj = new DateObj(startDateAndTime);
        this.endDateObj = new DateObj(endDateAndTime);
        this.eventType = eventType;
    }

    /**
     * Converts event type task to string format for printing.
     *
     * @return Formatted string representing the event, whether or not it is completed and its date.
     */
    public String toString() {
        if (getType() == 'T') { //if todo, then only one date entry
            return "[" + getDoneSymbol() + "][T]" + getDescription() + " BY: " + this.getStartDate().formatDate();
        } else { //multiple date entries
            return "[" + getDoneSymbol() + "][" + getType() + "]" +
                    getDescription() + " START: " + this.getStartDate().formatDate() +
                    " END: " + this.getEndDate().formatDate();
        }
    }

    public String toStringForFile() { //string that is to be saved to file.
        if (getEndDate() == null) {
            return getDoneSymbol() + getType() + " " + getDescription() + " " +
                    getStartDate().getSplitDate();
        }
        return getDoneSymbol() + getType() + " " + getDescription() + " " +
                getStartDate().getSplitDate() + " " + getEndDate().getSplitDate();
    }
    
    public char getType() {
    	return eventType;
    }

    public DateObj getStartDate() {
        return startDateObj;
    }

    public DateObj getEndDate() {
        return endDateObj;
    }

    public String getDescription(){
        return description;
    }

    public String getDoneSymbol() {
        return (isDone) ? "✓" : "✗";
    }

    public void markAsDone() {
        this.isDone = true;
    }
}
