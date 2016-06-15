package fsbtxtparser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Parses FSB txt files to csv data
 */
public class Parser {
    private final static String FSB_DOC_EXTN = ".txt";

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
            parser.writeToCsv(fsbList, directory.getAbsolutePath()+"/"+csvFileName);
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
        String fsbClass = "fsbtxtparser.Fsb";
        Fsb fsb = (Fsb) Class.forName(fsbClass).newInstance();
        while((line = bufferedReader.readLine()) != null && !line.trim().startsWith(ParserUtil.END_OF_PARSING))
        {
            ParserUtil.Token token;
            line = line.trim();
            if((token = ParserUtil.Token.doesStringStartWithToken(line)) != null)
            {
                String fieldValue = line.replaceFirst(token.getPatternString(), "") + getNextLineIfMultilineValue(bufferedReader, token);
                try {
                    Field field = fsb.getClass().getDeclaredField(token.attributeName);
                    field.set(fsb, removeNonPrintableChars(fieldValue));
                } catch (NoSuchFieldException e) {
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
            value = value + " " + line; //TODO: If line startsWith a bullet then line should be prefixed with \n
            bufferedReader.mark(MAX_NUM_OF_CHARS - value.length());
        }
        bufferedReader.reset();
        return value.trim();
    }

    private void writeToCsv(ArrayList<Fsb> fsbList, String fileName) throws IOException
    {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
        String header = ParserUtil.getHeadersAsCsv() + "\n";
        bufferedWriter.write(header, 0, header.length());
        for(Fsb fsb: fsbList)
        {
            String content = fsb.toStringCsv() + "\n";
            bufferedWriter.write(content, 0, content.length());
        }
        bufferedWriter.close();
    }

    private void writeToCsv(ArrayList<Fsb> fsbList, File file) throws IOException
    {
        FileWriter fileWriter = new FileWriter(file);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
        try {
            csvPrinter.print(ParserUtil.getHeaders());
            for (Fsb fsb : fsbList) {
                csvPrinter.print(fsb.toCsv());
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
        }
        return stringBuilder.toString().trim();
    }
}
