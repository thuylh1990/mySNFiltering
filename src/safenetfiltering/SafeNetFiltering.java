/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package safenetfiltering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import static javafx.application.Platform.exit;

/**
 *
 * @author thuylh
 */
public class SafeNetFiltering {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
        //Create printWriter
        String attached_file = new SimpleDateFormat("yyyyMMddhhmm'" + ".log" + "'").format(new Date());
        PrintWriter writer = new PrintWriter(attached_file);

        //Create HTTP Object
        HTTPURL res = new HTTPURL(writer);

        //Create Report Testcase
        Report.TestCase testcase = new Report.TestCase();
        testcase.testcasename = "Filtering domain Testing";
        testcase.summary = "To examine filtering function working correctly";
        testcase.isPassed = false;

        //Read domain from file
        List<String> domains = new ArrayList<String>();
        try{
            domains = readFromFile(args[0]);
        }catch(IOException e){
            System.out.println("Sai cu phap");
            writer.println(e.toString());
            exit();
        }

        //Create array to store result domain list
        List<String> failedDomains = new ArrayList<String>();
        final List<String> passedDomains = new ArrayList<String>();
        List<String> errorDomains = new ArrayList<String>();

        for (String domain : domains) {
            String buff = new String();

            //buff = res.getUnicodeContent("file:///E:/A-Work-FW/Tester/AutomationTest/SafeNetFiltering/vechai.info.html");
            buff = res.getContentBySocket(domain.trim());
            if (buff == null) {
                System.out.println("Cant read the content of domain " + domain);
                //writer.println("Cant read the content of domain" + domain);
                //testcase.note = "FAIL! Cant read the content of domain" + domain;
                errorDomains.add(domain);
            } else {
                //int found = buff.indexOf("đã bị chặn bởi <strong>SafeNet</strong> để bảo vệ bạn khỏi những nội dung không phù hợp.</p><p>Chặn theo nhóm:");
                //if (found > 0) {
                 System.out.println(domain + " has contained modifying content => PASS!");
                //    writer.println(domain + " has contained modifying content => PASS!");
                    passedDomains.add(domain);
                //} else {
                //    failedDomains.add(domain);
                //    System.out.println(domain + " has NOT contained modifying content => FAIL!");
                //    writer.println(domain + " has NOT contained modifying content => FAIL!");
               // }
            }
        }

        System.out.println("======================================");

//        if (failedDomains.size() == 0) {
//            testcase.isPassed = true;
//            testcase.note = "PASS!";
//            System.out.println("PASS!");
//        } else {
//            testcase.note = "FAIL!";
//            System.out.println("FAIL!");
//        }
//        
        int result = 0;
        
//        result = writeToFile(failedDomains, "failedDomains.txt");
//        if(result == -1){
//            writer.println("Ghi file failedDomain lỗi!");
//        }
        
        result = writeToFile(errorDomains, "errorDomains.txt");
        if(result == -1){
            writer.println("Ghi file errorDomain lỗi!");
        }
        
        result = writeToFile(passedDomains, "passedDomains.txt");
        if(result == -1){
            writer.println("Ghi file passedDomain lỗi!");
        }

        writer.close();

    }
    
//    private static void run() throws FileNotFoundException{
//        //Create printWriter
//        String attached_file = new SimpleDateFormat("yyyyMMddhhmm'" + ".log" + "'").format(new Date());
//        PrintWriter writer = new PrintWriter(attached_file);
//
//        //Create HTTP Object
//        HTTPURL res = new HTTPURL(writer);
//
//        //Create Report Testcase
//        Report.TestCase testcase = new Report.TestCase();
//        testcase.testcasename = "Filtering domain Testing";
//        testcase.summary = "To examine filtering function working correctly";
//        testcase.isPassed = false;
//
//        //Read domain from file
//        List<String> domains = new ArrayList<String>();
//        domains = readFromFile("list3.txt");
//
//        //Create array to store result domain list
//        List<String> failedDomains = new ArrayList<String>();
//        final List<String> passedDomains = new ArrayList<String>();
//        List<String> errorDomains = new ArrayList<String>();
//
//        for (String domain : domains) {
//            String buff = new String();
//
//            //buff = res.getUnicodeContent("file:///E:/A-Work-FW/Tester/AutomationTest/SafeNetFiltering/vechai.info.html");
//            buff = res.getContentBySocket(domain.trim());
//            if (buff == null) {
//                System.out.println("Cant read the content of domain " + domain);
//                writer.println("Cant read the content of domain" + domain);
//                testcase.note = "FAIL! Cant read the content of domain" + domain;
//                errorDomains.add(domain);
//            } else {
//                int found = buff.indexOf("đã bị chặn bởi <strong>SafeNet</strong> để bảo vệ bạn khỏi những nội dung không phù hợp.</p><p>Chặn theo nhóm:");
//                if (found > 0) {
//                    System.out.println(domain + " has contained modifying content => PASS!");
//                    writer.println(domain + " has contained modifying content => PASS!");
//                    passedDomains.add(domain);
//                } else {
//                    failedDomains.add(domain);
//                    System.out.println(domain + " has NOT contained modifying content => FAIL!");
//                    writer.println(domain + " has NOT contained modifying content => FAIL!");
//                }
//            }
//        }
//
//        System.out.println("======================================");
//
//        if (failedDomains.size() == 0) {
//            testcase.isPassed = true;
//            testcase.note = "PASS!";
//            System.out.println("PASS!");
//        } else {
//            testcase.note = "FAIL!";
//            System.out.println("FAIL!");
//        }
//        
//        int result = 0;
//        
//        result = writeToFile(failedDomains, "failedDomains.txt");
//        if(result == -1){
//            writer.println("Ghi file failedDomain lỗi!");
//        }
//        
//        result = writeToFile(errorDomains, "errorDomains.txt");
//        if(result == -1){
//            writer.println("Ghi file errorDomain lỗi!");
//        }
//        
//        result = writeToFile(passedDomains, "passedDomains.txt");
//        if(result == -1){
//            writer.println("Ghi file passedDomain lỗi!");
//        }
//
//        writer.close();
//    }

    private static List<String> readFromFile(String filename) throws FileNotFoundException {
        List<String> queries = new ArrayList<String>();

        BufferedReader br = null;

        try {
            String sCurrentLine;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                queries.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (queries != null) {
            return queries;
        }
        return null;
    }

    private static int writeToFile(List<String> domainlist, String outputFileName) {
        String outputFile = new SimpleDateFormat("yyyyMMddhhmm'_" + outputFileName + "'").format(new Date());
        try{
        
            PrintWriter os = new PrintWriter(outputFile, "UTF-8");
            for(String domain: domainlist){
                os.println(domain);
            }
            os.close();       
        
        }catch(IOException e){
            return -1;
        }
        
        return 0;
        
        

    }
}
