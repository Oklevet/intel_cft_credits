package ru.intel.credits.parsing;

import ru.intel.credits.configuration.DataSource;
import ru.intel.credits.parsing.repository.CFTInsert10k;
import ru.intel.credits.parsing.repository.CFTInsert10kReadSplit;

public class MainParse {

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        System.out.println("startTime: " + startTime);

        MainParse mainParse = new MainParse();
        DataSource dataSourceCFT = new DataSource(
                "org.postgresql.Driver",
                "jdbc:postgresql://127.0.0.1:5432/intel_cft_credits",
                "postgres",
                "asd1");

        CFTInsert10k cftInsert10k = new CFTInsert10k();

        try {
            cftInsert10k.insertParseData("vid_debt.json", dataSourceCFT, "public");
            cftInsert10k.insertParseData("take_in_debt.json", dataSourceCFT, "public");
            cftInsert10k.insertParseData("vid_oper_dog.json", dataSourceCFT, "public");
            cftInsert10k.insertParseData("pr_cred.json", dataSourceCFT, "public");
            cftInsert10k.insertParseData("fact_oper.json", dataSourceCFT, "public");
            cftInsert10k.insertParseData("plan_oper.json", dataSourceCFT, "public");
//            cftInsert10k.insertParseData("pr_cred_test.json", dataSourceCFT, "public");
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000 / 1000;  //divide by 1000000 to get milliseconds.
        System.out.println(System.lineSeparator() + "endTime: " + endTime);
        System.out.println("duration: " + duration);
    }
}
