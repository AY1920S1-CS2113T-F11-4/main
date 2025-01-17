//@@author Ryan-Wong-Ren-Wei

package mistermusik.commons.events.formatting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Interface that handles the validation of user input date strings.
 */
public interface DateStringValidator {
    /**
     * Checks if a string is valid entry for dd-MM-yyyy format.
     */
    static boolean isValidDateForToDo(String dateString) {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        format.setLenient(false);
        try {
            format.parse(dateString);
        } catch (ParseException | NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a string is valid entry for dd-MM-yyyy HHmm format.
     */
    static boolean isValidDateForEvent(String dateString) {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HHmm");
        format.setLenient(false);
        try {
            format.parse(dateString);
        } catch (ParseException | NullPointerException e) {
            return false;
        }
        return true;
    }
}
