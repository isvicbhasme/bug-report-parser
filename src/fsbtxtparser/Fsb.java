package fsbtxtparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Pattern fullDatePattern = Pattern.compile("(\\w+,*\\s*\\d+,*\\.*\\s*\\d{4}|\\d+,*\\s*\\w+,*\\s+\\d{4})$");
    private Pattern yearPattern = Pattern .compile("\\d{4}");
    private Pattern datePattern = Pattern.compile("(\\W\\d{1,2}\\W|^\\d{1,2}\\W)");
    private Pattern monthPattern = Pattern.compile("[a-zA-Z]+");

    String toStringCsv()
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

    List<String> toCsv()
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

    public void setNumber(String number) {
        this.number = number;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateCreated(String dateCreated) {
        Matcher matcher = fullDatePattern.matcher(dateCreated);
        if(matcher.find())
        {
            String fullDate = matcher.group();
            String year = "";
            String date = "";
            String month = "";
            matcher = yearPattern.matcher(fullDate);
            if(matcher.find())
            {
                year = matcher.group();
            }
            matcher = datePattern.matcher(fullDate);
            if(matcher.find())
            {
                date = matcher.group().replaceAll("[^\\d]", "");
            }
            matcher = monthPattern.matcher(fullDate);
            if(matcher.find())
            {
                month = matcher.group();
            }
            if((month + date + year).trim().length() > 0)
            {
                this.dateCreated = (month + " " + date + ", " + year).trim();
            }
        }
    }

    public void setDateRevised(String dateRevised) {
        Matcher matcher = fullDatePattern.matcher(dateRevised);
        if(matcher.find())
        {
            String fullDate = matcher.group();
            String year = "";
            String date = "";
            String month = "";
            matcher = yearPattern.matcher(fullDate);
            if(matcher.find())
            {
                year = matcher.group();
            }
            matcher = datePattern.matcher(fullDate);
            if(matcher.find())
            {
                date = matcher.group().replaceAll("[^\\d]", "");
            }
            matcher = monthPattern.matcher(fullDate);
            if(matcher.find())
            {
                month = matcher.group();
            }
            if((month + date + year).trim().length() > 0)
            {
                this.dateRevised = (month + " " + date + ", " + year).trim();
            }
        }
    }

    public void setProductsAffected(String productsAffected) {
        this.productsAffected = productsAffected;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setAffectedSystems(String affectedSystems) {
        this.affectedSystems = affectedSystems;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRecommendedActions(String recommendedActions) {
        this.recommendedActions = recommendedActions;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }
}
