package fsbtxtparser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.beans.Statement;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses FSB txt files to csv data
 */
public class Parser {
    private final static String FSB_DOC_EXTN = ".txt";
    private Pattern numberedListPattern = Pattern.compile("^\\d+\\.");

    public static void main(String[] args)
    {
        System.out.println(new File("").getAbsolutePath());
        File directory = new File("./resources");
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s)
            {
                return s.endsWith(FSB_DOC_EXTN);
            }
        });
        Parser parser = new Parser();
        ArrayList<Fsb> fsbList = new ArrayList<>();
        String csvFileName = "output.csv";
        try
        {
            for(File file: files)
            {
                try
                {
                    fsbList.add(parser.createFsbFromFile(file));
                }
                catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
            File outputFile = new File(directory.getAbsolutePath()+"/"+csvFileName);
            parser.writeToCsv(fsbList, outputFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private Fsb createFsbFromFile(File file) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        String fsbClassName = "fsbtxtparser.Fsb";
        Class fsbClass = Class.forName(fsbClassName);
        Fsb fsb = (Fsb) fsbClass.newInstance();
        while((line = bufferedReader.readLine()) != null && !line.trim().startsWith(ParserUtil.END_OF_PARSING))
        {
            ParserUtil.Token token;
            line = line.trim();
            if((token = ParserUtil.Token.doesStringStartWithToken(line)) != null)
            {
                String fieldValue = replaceNumberedListStartWithBullet(line.replaceFirst(token.getPatternString(), ""))
                        + getNextLineIfMultilineValue(bufferedReader, token);
                try {
                    String setterMethodName = "set" + Character.toUpperCase(token.attributeName.charAt(0)) + token.attributeName.substring(1);
                    Method method = fsbClass.getDeclaredMethod(setterMethodName, new Class[]{String.class});
                    method.invoke(fsb, removeNonPrintableChars(fieldValue));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Object:"+fsb.toString());
        bufferedReader.close();
        return fsb;
    }

    private String getNextLineIfMultilineValue(BufferedReader bufferedReader, ParserUtil.Token token) throws IOException
    {
        String value = "";
        final int MAX_NUM_OF_CHARS = token.getNumOfCharsToPeek();
        bufferedReader.mark(MAX_NUM_OF_CHARS); // Hard-coded, meaning if value is multi-lined it should not be longer than this, counting from 2nd line
        String line;
        while( (line = bufferedReader.readLine()) != null       // readLine into 'line' string is successful
                && (ParserUtil.Token.doesStringStartWithToken(line) == null  // 'line' does not start with a token
                && !line.startsWith(ParserUtil.END_OF_PARSING))  // 'line' is not the END_OF_PARSING DELIMITER
                && value.length() + line.length() < MAX_NUM_OF_CHARS) // value cannot be more than marked value
        {
            value = value + " " + replaceNumberedListStartWithBullet(line); //TODO: If line startsWith a bullet then line should be prefixed with \n
            bufferedReader.mark(MAX_NUM_OF_CHARS - value.length());
        }
        bufferedReader.reset();
        return value.trim();
    }

    private void writeToCsv(ArrayList<Fsb> fsbList, File file) throws IOException
    {
        FileWriter fileWriter = new FileWriter(file);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
        try {
            csvPrinter.printRecord(ParserUtil.getHeaders());
            for (Fsb fsb : fsbList) {
                if(fsb.number != null && fsb.number.trim().length() > 0)
                {
                    csvPrinter.printRecord(fsb.toCsv());
                }
            }
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvPrinter.close();
            } catch (IOException e)
            {
                System.out.println("Error closing objects - "+e);
            }
        }
    }

    private String removeNonPrintableChars(String phrase)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(byte character: phrase.getBytes())
        {
            if(character >= 32 || character == '\n' || character == '\r')
            {
                stringBuilder.append((char)character);
            }
            else if(character == -30)
            {
                stringBuilder.append("<bullet>");
            }
        }
        return stringBuilder.toString().trim();
    }

    private String replaceNumberedListStartWithBullet(String phrase)
    {
        Matcher matcher = numberedListPattern.matcher(phrase);
        if(matcher.find())
        {
            phrase = "<numbered-list>"+phrase;
        }
        return phrase;
    }
}
