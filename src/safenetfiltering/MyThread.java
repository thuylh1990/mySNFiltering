/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package safenetfiltering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.exit;

/**
 *
 * @author thuylh
 */
public class MyThread extends Thread {

    private String input;
    private int myThreadID;
    private PrintWriter log_filename;
    private PrintWriter result_filename;
    private PrintWriter passed_filename;
    private AtomicInteger count;
    //private CountDownLatch latch;

    public int getMyThreadID() {
        return myThreadID;
    }

    public void setMyThreadID(int myThreadID) {
        this.myThreadID = myThreadID;
    }

    public MyThread(String input, PrintWriter log_filename, PrintWriter result_filename, PrintWriter result_filename2, AtomicInteger count) {
        this.input = input;
        this.log_filename = log_filename;
        this.result_filename = result_filename;
        this.passed_filename = result_filename2;
        this.count = count;
        //this.latch = latch;
    }

    public static void main(String[] args) throws FileNotFoundException {
        String[] _args = new String[]{"10", "0", "domains_games_"};
        AtomicInteger count = new AtomicInteger(0);

        List<MyThread> mThread = new ArrayList<>();
        if (args.length < 1) {
            args = _args;
        }
        int number = Integer.parseInt(args[0]);
        int dec = Integer.parseInt(args[1]);

        if (number < 1) {
            System.out.println("Sai cu phap!!!");
            System.exit(-1);
        }

        //
        //Create printWriter
        PrintWriter log_writer = null;
        PrintWriter output_writer = null;
        PrintWriter passed_writer = null;

        String log_filename = new SimpleDateFormat("'Log_" + dec + "x_'yyMMddhhmm'" + ".log" + "'").format(new Date());
        log_writer = new PrintWriter(log_filename);
        try {
            // String log_filename = new SimpleDateFormat("'Log_" + dec + "x_'yyMMddhhmm'" + ".log" + "'").format(new Date());
            //log_writer = new PrintWriter(log_filename);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
            log_writer.println("Start at " + dateFormat.format(new Date()));

            String output_filename = new SimpleDateFormat("'ErrorDomains_" + dec + "x_'yyMMddhhmm'" + ".txt" + "'").format(new Date());
            output_writer = new PrintWriter(output_filename, "UTF-8");

            String passed_filename = new SimpleDateFormat("'PassedDomains_" + dec + "x_'yyMMddhhmm'" + ".txt" + "'").format(new Date());
            passed_writer = new PrintWriter(passed_filename, "UTF-8");

            int start = dec * 10 + 1;
            int stop = start + number;
            
            //CountDownLatch latch = new CountDownLatch(number);
            for (int i = start; i < stop; i++) {
                String input_file = args[2] + String.valueOf(i) + ".txt";
                MyThread mt = new MyThread(input_file, log_writer, output_writer, passed_writer, count);
                mt.setMyThreadID(i);
                mThread.add(mt);
            }
            
            for (int i = 0; i < number; i++) {
                mThread.get(i).start();
            }
            //latch.await();
            //System.out.println("All Done!");

            for (int i = 0; i < number; i++) {
                mThread.get(i).join();
            }

            

            System.out.println("======================================");

            output_writer.close();
            passed_writer.close();

        } catch (FileNotFoundException ex) {
            log_writer.println(ex.toString());
        } catch (IOException | InterruptedException ex) {
            log_writer.println(ex.toString());
        } finally {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
            System.out.println("Finish at " + dateFormat.format(new Date()));
            log_writer.println("Finish at " + dateFormat.format(new Date()));
            log_writer.close();
            System.out.println("OK!");
        }

    }

    @Override
    public void run() {
        try {

            HTTPURL res = new HTTPURL(this.log_filename);

//          //Read domain from file
            List<String> domains = new ArrayList<>();
            domains = readFromFile(this.input);

            for (String domain : domains) {
                String buff = new String();
                int current = count.addAndGet(1);

                buff = res.getContentBySocket(domain.trim());

                if (buff == null) {

                    System.out.println("Total domains = " + current + ". Cant read the content of domain " + domain);
                    synchronized (this) {
                        this.log_filename.println("Total domains = " + current + ". Cant read the content of domain = " + domain + " from file = " + this.input);
                        this.log_filename.flush();

                        this.result_filename.println(domain);
                        this.result_filename.flush();
                    }

                } else {
                    synchronized (this) {
                        System.out.println("Total domains = " + current + ". Domain has contained modifying content => PASS! = " + domain + " from file = " + this.input);
                        this.log_filename.println("Total domains = " + current + ". Domain has contained modifying content => PASS! = " + domain + " from file = " + this.input);
                        this.log_filename.flush();

                        this.passed_filename.println(domain);
                        this.passed_filename.flush();

                    }
                }

            }

        } catch (FileNotFoundException ex) {
            //Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            synchronized (this) {
                this.log_filename.println(ex.toString());
            }
        } catch (IOException ex) {
            synchronized (this) {
                //Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                this.log_filename.println(ex.toString());
            }
        } catch (InterruptedException | ExecutionException ex) {
            synchronized (this) {
                //Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                this.log_filename.println(ex.toString());
            }
        } finally{
            //this.latch.countDown();
        }
    }

    private static List<String> readFromFile(String filename) throws FileNotFoundException {
        List<String> queries = new ArrayList<>();

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
        String outputFile = new SimpleDateFormat(outputFileName + "_yyyyMMddhhmm'" + "'").format(new Date());
        try {

            PrintWriter os = new PrintWriter(outputFile, "UTF-8");
            for (String domain : domainlist) {
                os.println(domain);
            }
            os.close();

        } catch (IOException e) {
            return -1;
        }

        return 0;

    }
}
