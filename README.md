# University of Moratuwa Rank and GPA Calculator
M.S.I. Weerawansa  
In-22 BSc. Eng. (Hons.) Computer Science and Engineering  

## Description
This is a simple program that can calculate the Rank and GPA of End-Semester exams of students with respect to their batch
studying in the University of Moratuwa, Sri Lanka.  
It makes use of the `PDFBox` library to read and extract data from Provisional Exam Results PDFs.
It can then display the results in the command line and export them into a CSV file. 

## How to use it
1. First, you choose a PDF containing the results for a module **only** your batch took.
2. You have to then specify the number of credits for that module. 
3. Then you can select the remaining results PDFs of the modules relevant to your exam which you wish to extract results from.
4. You will then be prompted to enter the number of credits for each module you have chosen.
5. The program will calculate and display the Rank and GPA of the students whose index numbers were in the PDF chosen in the first step.
6. You may then choose if you need a CSV file exported. The output will be saved to `output.csv` in the root folder.
