/*
    author M.S.I. Weerawansa
    04.09.2024

    all rights reserved

    functionality improved
    01.08.2025
 */

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static String[] getPDFLines(File file) throws IOException{
        PDDocument doc = Loader.loadPDF(file);
        PDFTextStripper stripper = new PDFTextStripper();

        String stripperText = stripper.getText(doc);

        return stripperText.split("\\r?\\n");
    }

    private static HashMap<String, Double> getResultMap(String path, HashSet<String> indexes) throws IOException{
        String[] lines = getPDFLines(new File(path));
        HashMap<String, Double> resultMap = new HashMap<>();
        for(String line:lines){
            String[] parts = line.split(" ");

            for(int i = 0; i < parts.length; i++){
                String trimmed = parts[i].strip();
                switch (trimmed) {
                    case "A+", "A" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 4.0);
                        }
                    }
                    case "A-" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 3.7);
                        }
                    }
                    case "B+" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 3.3);
                        }
                    }
                    case "B" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 3.0);
                        }
                    }
                    case "B-" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 2.7);
                        }
                    }
                    case "C+" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 2.3);
                        }
                    }
                    case "C" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 2.0);
                        }
                    }
                    case "C-" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 1.5);
                        }
                    }
                    case "D" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 1.0);
                        }
                    }
                    case "F", "I-we", "I-ca" -> {
                        if (i > 0 && indexes.contains(parts[i - 1].strip())) {
                            resultMap.put(parts[i - 1], 0.0);
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    private static HashSet<String> getDepartmentIndexes(String path) throws IOException{
        HashSet<String> indexes = new HashSet<>();
        String[] lines = getPDFLines(new File(path));

        for(String line: lines){
            String[] parts = line.split(" ");

            for(int i = 0; i < parts.length; i++){
                if(parts[i].strip().matches("\\b220\\d{3}[A-Z]\\b")){
                    indexes.add(parts[i]);
                }
            }
        }
        return indexes;
    }

    private static HashMap<String, List> getGPASet(List<HashMap<String, Double>> marksSet, List<String> departmentIndexes, ArrayList<Integer> credits){

        HashMap<String, List> gpaMap = new HashMap<>();
        for(String index: departmentIndexes){
            double totalCredits = 0.0;
            double GP = 0.0;
            int x = 0;
            for(HashMap<String, Double> marksSheet: marksSet){
                if(marksSheet.get(index) != null) {
                    int credit = credits.get(x);
                    GP += marksSheet.get(index) * credit;
                    totalCredits += credit;
                }
                x++;
            }
            double GPA = GP/totalCredits;
            ArrayList res_list = new ArrayList();
            res_list.add(GPA);
            res_list.add(totalCredits);
            gpaMap.put(index, res_list);
        }

        return gpaMap;
    }

    private static ArrayList<String> getFilePaths(String title){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(true);
        chooser.requestFocus();

        int returnValue = chooser.showOpenDialog(null);

        if(returnValue == JFileChooser.APPROVE_OPTION){
            File chosenFiles[] = chooser.getSelectedFiles();
            ArrayList<String> toReturn = new ArrayList<>();
            for(File file : chosenFiles){
                toReturn.add(file.getAbsolutePath());
            }
            return toReturn;
        }
        return null;
    }

    private static String getFilePath(String title){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(title);
        chooser.requestFocus();
        int returnValue = chooser.showOpenDialog(null);

        if(returnValue == JFileChooser.APPROVE_OPTION){
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    private static int getCredits(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Credits for the Module: ");
        return Integer.parseInt(scanner.nextLine());
    }

    private static boolean confirmNext(){
        System.out.println("\nAdd more modules? (Y/N):");

        while (true) {
            String result = new Scanner(System.in).nextLine().strip().toLowerCase();
            if (result.equals("y")) {
                return true;
            }else if (result.equals("n"))
                return false;
        }
    }

    private static boolean checkDuplicates(HashSet<String> modules, String line){
        String regex = "^([A-Z]{2}\\d{4}).*$";
        Pattern moduleCodePattern = Pattern.compile(regex);

        Matcher matcher = moduleCodePattern.matcher(line);
        String moduleCode = "";
        while (matcher.find()){
            moduleCode = matcher.group(1);
        }

        if(modules.contains(moduleCode)){
            return true;
        }
        modules.add(moduleCode);
        return false;
    }

    private static boolean getAndPrintModule(String path, HashSet<String> modules) throws IOException{
        String[] lines = getPDFLines(new File(path));
        for(String line: lines){
            if(line.strip().matches("^[A-Z]{2}\\d{4}.*$")){
                if(checkDuplicates(modules, line.strip())){
                    System.out.println("The module "+line.strip()+" is already added!\n");
                    return false;
                }
                System.out.println("Added module: "+line.strip());
                return true;
            }
        }
        System.out.println("Not a valid Result Sheet!\n");
        return false;
    }
    private static boolean addModules(ArrayList<String> paths, ArrayList<Integer> credits, HashSet<String> modules, String title) throws IOException{
        ArrayList<String> filePaths = getFilePaths(title);
        if(!filePaths.isEmpty()){
            System.out.println("\nChosen "+filePaths.size()+ " files.\n");
            int place = 1;
            for(String path:filePaths){
                System.out.println("Checking file "+place);
                place++;
                if(getAndPrintModule(path, modules)){
                    paths.add(path);
                    credits.add(getCredits());
                    System.out.println();
                }
            }
            return true;
        }
        return false;
    }
    private static boolean addModule(ArrayList<String> paths, ArrayList<Integer> credits, HashSet<String> modules, String title) throws IOException{
        String path = getFilePath(title);
        if(path != null){
            if(getAndPrintModule(path, modules)){
                paths.add(path);
                credits.add(getCredits());
                return true;
            }
        }
        return false;
    }

    private static void printResults(List<Map.Entry<String, List>> list) {
        // Extract the keys from the sorted list
        int pos = 0;
        int x = 0;
        int y = 1;
        double prev = 0;
        for (Map.Entry<String, List> entry : list) {
            if((double) entry.getValue().get(0) != prev){
                pos++;
                prev = (double)entry.getValue().get(0);
                x += y;
                y = 1;
            }else{
                y++;
            }
            System.out.printf("%2d (%3d)\t%-7s \t%.3f \t%.0f\n", pos, x, entry.getKey(), (double)entry.getValue().get(0),  (double) entry.getValue().get(1));
        }
    }

    private static void exportResults(List<Map.Entry<String, List>> list) throws IOException {
        FileWriter outputFile = new FileWriter("output.csv");
        outputFile.write("Rank, Position in Batch, Index Number, GPA, Total Credits\n");

        int pos = 0;
        int x = 0;
        int y = 1;
        double prev = 0;
        for (Map.Entry<String, List> entry : list) {
            if((double) entry.getValue().get(0) != prev){
                pos++;
                prev = (double)entry.getValue().get(0);
                x += y;
                y = 1;
            }else{
                y++;
            }
            outputFile.write(String.format("%d, %d, %s, %.3f, %.0f\n", pos, x, entry.getKey(), (double)entry.getValue().get(0),  (double) entry.getValue().get(1)));
        }
        outputFile.close();

        System.out.println("Successfully exported!");
    }

    public static void main(String[] args) {
        try {
            System.out.println("Choose first result sheet of a module only your batch took.\nPress enter to continue.");
            new Scanner(System.in).nextLine();

            ArrayList<String> paths = new ArrayList<>();
            ArrayList<Integer> credits = new ArrayList<>();
            HashSet<String> modules = new HashSet<>();

            while (true) {
                if(!addModule(paths, credits, modules, "Choose a Result Sheet of a module ONLY YOUR BATCH took.")){
                    System.out.println("This step cannot be skipped. Press enter to continue.");
                    new Scanner(System.in).nextLine();
                    continue;
                }
                break;
            }

            while (true){
                if(!confirmNext())
                    break;

                if(!addModules(paths, credits, modules, "Choose one or more Result Sheets"))
                    System.out.println("Cancelled\n");
            }

            System.out.println("Extracting department index numbers...");
            HashSet<String> departmentIndexes = getDepartmentIndexes(paths.get(0));
            List<String> departmentIndexList = new ArrayList<>(departmentIndexes);
            System.out.println(departmentIndexList.size());
            Collections.sort(departmentIndexList);

            System.out.println("Extracting results from all modules...");

            List<HashMap<String, Double>> marksSet = new ArrayList<>();
            for(int i =0; i < paths.size(); i++){
                marksSet.add(getResultMap(paths.get(i), departmentIndexes));
            }

            System.out.println("Calculating GPAs...");
            HashMap<String, List> gpaMap = getGPASet(marksSet, departmentIndexList, credits);

            List<Map.Entry<String, List>> list = new ArrayList<>(gpaMap.entrySet());

            // Sort the list based on the values
            list.sort(Comparator.comparing(
                    e -> (Double) e.getValue().get(0),
                    Comparator.reverseOrder()
            ));

            System.out.println("Displaying results...\n");
            printResults(list);

            System.out.println("\nWould you like to export these results to a CSV file? (Y/N)");
            String export = new Scanner(System.in).nextLine();
            if(export.strip().toLowerCase().equals("y"))
                exportResults(list);

        } catch (IOException e) {
            System.err.println("An error occurred when opening the file. Make sure you select a valid Result sheet PDF\nThe program will now exit");
        } catch (NumberFormatException e){
            System.err.println("An unexpected error occurred. Maybe you entered letters when we asked for numbers?");
        }
    }
}
