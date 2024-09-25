import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class main {

    static String Input_path = "Input/";
    static String fileExtension = ".s";
    static String Output_path = "Output/";

    /**
     *  outExtension[0] for file.bin
     *  outExtension[1] for file.txt
     */
    static String[] outExtension = {
            ".bin",".txt"
    };

    static String[] inputFile = {
            "TEST" , "COMBINATION" , "MULTIPLIER" , "SUM"
    };
    public static void main(String[] args) {
        System.out.print("Select Files [ " + Arrays.toString(inputFile) + " ] (1-"+ inputFile.length + ") : " );
        Scanner input = new Scanner(System.in);
        int i = Integer.parseInt(input.nextLine()) - 1;

        printAssembly(inputFile[i]);

        System.out.print("Press Enter to compute to machine code : ");
        input.nextLine();

        // main function !!!
        compute(inputFile[i]);

        // show output
        // print(inputFile[i] , outExtension[0]);
        print(inputFile[i] , outExtension[1]);
    }
    public static void compute(String File){
        Assembler Encoder = new Assembler(
                FileOperator.FileToString(Input_path + File + fileExtension)
        );

        List<String> binaryCodes = Encoder.computeToMachineCode();
        List<String> decimalCodes = Assembler.binaryToDecimal(binaryCodes);

        //  FileOperator.StringToFile(Output_path + File + outExtension[0], binaryCodes);
        FileOperator.StringToFile(Output_path + File + outExtension[1],  decimalCodes);
    }

    public static void print(String File , String fileExtension){
        String Input = Output_path + File + fileExtension;
        System.out.println("\n");
        if(Objects.equals(fileExtension, outExtension[1])){
            System.out.println("print in DecimalCode -> " + Input);
        } else if(Objects.equals(fileExtension, outExtension[0])){
            System.out.println("print in BinaryCode -> " + Input);
        }
        System.out.println(" " + FileOperator.FileToString(Input));
    }

    public static void printAssembly(String File){
        String Input = Input_path + File + fileExtension;
        System.out.println("\n");
        System.out.println("Print : " + Input + "\n");
        System.out.println(" " + FileOperator.FileToString(Input));
    }
}