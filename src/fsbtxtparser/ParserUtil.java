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
class ParserUtil {
    public static final String END_OF_PARSING = "For More Information";

    public enum Token {
        fsbNumber("^\\W*FSB Number", "number", 50),
        fsbTitle("^\\W*FSB Title", "title", 200),
        fsbDateCreated("^\\W*Date Created", "dateCreated", 50),
        fsbDateRevised("^\\W*(Date Revised|Date Last Revised)", "dateRevised", 50),
        fsbProductsAffected("^\\W*(Product\\(s\\) Affected|Product\\(s\\)$|Affected$)", "productsAffected", 400),
        fsbSynopsis("^\\W*Synopsis", "synopsis", 1000),
        fsbAffectedSystems("^\\W*(Systems Affected|Products / Systems Affected)", "affectedSystems", 400),
        fsbProblemDescription("^\\W*Problem Description", "description", 1000),
        fsbRecomendedActions("^\\W*Recommended Actions", "recommendedActions", 1000),
        fsbResolution("^\\W*Resolution", "resolution", 400),
        fsbReferences("^\\W*References", "references", 400),
        fsbProcedure("^\\W*(Procedure|Procedures)", "procedure", 15000);

        public String attributeName = "";
        private String regexString = "";
        private Pattern pattern = null;
        private int numOfCharsToPeek = 0;
        Token(String regexString, String attributeName, int numOfCharsToPeek)
        {
            this.regexString = regexString;
            this.attributeName = attributeName;
            this.pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
            this.numOfCharsToPeek = numOfCharsToPeek;
        }

        public static String[] getTokenNames()
        {
            String[] result = new String[Token.values().length];
            int i=0;
            for(Token token: Token.values())
            {
                result[i++] = token.regexString;
            }
            return result;
        }

        public static Token doesStringStartWithToken(String line)
        {
            for(Token token: Token.values())
            {
                Matcher matcher = token.pattern.matcher(line);
                if(matcher.find() && matcher.start() == 0)
                {
                    return token;
                }
            }
            return null;
        }

        public int getNumOfCharsToPeek()
        {
            return numOfCharsToPeek;
        }

        public String getPatternString()
        {
            return regexString;
        }
    }

    public static <T> String toCsvForm(T attribute)
    {
        return encloseWithDoubleQuotes(attribute) + ",";
    }

    public static List<String> getHeaders()
    {
        List<String> headers = new ArrayList<>();
        headers.add("FSB Number");
        headers.add("FSB Title");
        headers.add("Date Created");
        headers.add("Date Revised");
        headers.add("Products Affected");
        headers.add("Synopsis");
        headers.add("Systems Affected");
        headers.add("Problem Description");
        headers.add("Recommended Actions");
        headers.add("Resolution");
        headers.add("References");
        headers.add("Procedure");
        return headers;
    }

    public static <T> String encloseWithDoubleQuotes(T text)
    {
        return "\""+text+"\"";
    }
}
