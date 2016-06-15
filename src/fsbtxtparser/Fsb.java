package fsbtxtparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ibhasme
 * Date: 9/6/16
 * Time: 6:05 PM
 */
class Fsb {
    public String number;
    public String title;
    public String dateCreated;
    public String dateRevised;
    public String productsAffected;
    public String synopsis;
    public String affectedSystems;
    public String description;
    public String recommendedActions;
    public String resolution;
    public String references;
    public String procedure;

    public String toStringCsv()
    {
        StringBuilder builder = new StringBuilder()
            .append(getDisplayValue(number))
            .append(getDisplayValue(title))
            .append(getDisplayValue(dateCreated))
            .append(getDisplayValue(dateRevised))
            .append(getDisplayValue(productsAffected))
            .append(getDisplayValue(synopsis))
            .append(getDisplayValue(affectedSystems))
            .append(getDisplayValue(description))
            .append(getDisplayValue(recommendedActions))
            .append(getDisplayValue(resolution))
            .append(getDisplayValue(references))
            .append(getDisplayValue(procedure));
        builder.deleteCharAt(builder.lastIndexOf(","));
        return builder.toString();
    }

    public List<String> toCsv()
    {
        List<String> list = new ArrayList<>();
        list.add(number);
        list.add(title);
        list.add(dateCreated);
        list.add(dateRevised);
        list.add(productsAffected);
        list.add(synopsis);
        list.add(affectedSystems);
        list.add(description);
        list.add(recommendedActions);
        list.add(resolution);
        list.add(references);
        list.add(procedure);
        return list;
    }
    
    private <T> String getDisplayValue(T value)
    {
        return value == null? "," : ParserUtil.encloseWithDoubleQuotes(value) + ",";
    }
}
