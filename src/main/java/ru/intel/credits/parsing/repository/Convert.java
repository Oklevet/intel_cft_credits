package ru.intel.credits.parsing.repository;

import org.aspectj.util.FileUtil;
import java.io.*;

public class Convert {

    public static void convert(
            String infile,
            String outfile,
            String from,   // UTF-8/windows-1251, etc
            String to)     //UTF-8/windows-1251, etc
            throws IOException, UnsupportedEncodingException
    {

        File source = new File(infile);
        File dest = new File(outfile);
        FileUtil.copyFile(source, dest);
        InputStream in;
        if(infile != null)
            in=new FileInputStream(infile);
        else
            in=System.in;
        OutputStream out;
        if(outfile != null)
            out=new FileOutputStream(outfile);
        else
            out=System.out;

        if(from == null) from=System.getProperty("file.encoding");
        if(to == null) to=System.getProperty("file.encoding");

        Reader r=new BufferedReader(new InputStreamReader(in, from));
        Writer w=new BufferedWriter(new OutputStreamWriter(out, to));

        char[] buffer=new char[4096];
        int len;
        while((len=r.read(buffer)) != -1)
            w.write(buffer, 0, len);
        r.close();
        w.flush();
        w.close();
    }

    public static void main(String[] args) throws IOException {
        convert("src/main/resources/TAKE_IN_DEBT.json","db/TAKE_IN_DEBT_copied","UTF-8","windows-1251");
    }
}