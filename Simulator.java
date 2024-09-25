import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Simulator {
    private static final int NUMMEMORY = 65536; // maximum number of words in memory
    private static final int NUMREGS = 8; // number of machine registers
    private static final int MAXLINELENGTH = 5000; // for testing default is 1000

    public static class stateStruct {
        int pc;
        int[] mem = new int[NUMMEMORY];
        int[] reg = new int[NUMREGS];
        int numMemory;
    }

    /*
         (address 0): 8454151 (hex 0x810007) lw 0 1 five :load reg1 with 5 (uses symbolic address)
         //0000000(25:24)0100000010000000000000111
         (address 1): 9043971 (hex 0x8a0003)
         //0000000       010 001 010 0000000000000011
         (address 2): 655361 (hex 0xa0001)
         (address 3): 16842754 (hex 0x1010002)
         //0000000 100 000 001 0000000000000010
         (address 4): 16842749 (hex 0x100fffd)
         //0000000 100 000 000 1111111111111101
         (address 5): 29360128 (hex 0x1c00000)
         //0001110000000000000000000000
         (address 6): 25165824 (hex 0x1800000)

         (address 7): 5 (hex 0x5)
         (address 8): -1 (hex 0xffffffff)
         (address 9): 2 (hex 0x2)
     */


    public static void printState(stateStruct state) {
        System.out.println("\n@@@\nstate:");
        System.out.println("\tpc " + state.pc);
        System.out.println("\tmemory:");
        for (int i = 0; i < state.numMemory; i++) {
            System.out.println("\t\tmem[ " + i + " ] " + state.mem[i]);
        }
        System.out.println("\tregisters:");
        for (int i = 0; i < NUMREGS; i++) {
            System.out.println("\t\treg[ " + i + " ] " + state.reg[i]);
        }
        System.out.println("end state");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("error: usage: java Simulator <machine-code file>");
            System.exit(1);
        }
        //int
        String fileName = args[0];
        stateStruct state = new stateStruct();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                state.mem[state.numMemory] = Integer.parseInt(line);
                System.out.println("memory[" + state.numMemory + "]=" + state.mem[state.numMemory]);
                state.numMemory++;
            }
        } catch (IOException e) {
            System.err.println("error: can't open file " + fileName);
            e.printStackTrace();
            System.exit(1);
        }


        String binaryString = Integer.toBinaryString(state.mem[0]);
        int originalNumber = Integer.parseInt(binaryString, 2);
        int extractedBits = (originalNumber >> 21) & 0b111;
        String X = Integer.toBinaryString(extractedBits);
        int[] op_args = new int[X.length()];
        for (int i = 0; i < X.length(); i++) {
            char digit = X.charAt(i);
            op_args[i] = (digit == '1') ? 1 : 0;
        }
        System.out.println();
        //System.out.println("Original string: " + X);
        //System.out.println("Split array: " + op_args[0] + op_args[1] + op_args[2]);

        state.pc = 0;
        int regA, regB;
        int offset = 0;
        int[] arg = new int[3];
        int total = 0;
        for (int i = 1; i != 0; i++){
            total++;
            printState(state);
            //System.out.println("NEXT-STATE");
            //System.out.println(state.mem[state.pc] >> 22);
            switch (state.mem[state.pc] >> 22) {
                case 0: //add 000
                    rFormat(state.mem[state.pc], arg);
                    regA = state.reg[arg[0]];
                    regB = state.reg[arg[1]];
                    state.reg[arg[2]] = regA + regB;

                    break;

                case 1://nand 001
                    rFormat(state.mem[state.pc], arg);
                    regA = state.reg[arg[0]];
                    regB = state.reg[arg[1]];
                    state.reg[arg[2]] = ~(regA & regB);

                    break;

                case 2://lw 010
                    // 0 = regA, 1 = regB, 2 = offsetField
                    iFormat(state.mem[state.pc], arg);
                    offset = arg[2] + state.reg[arg[0]];
                    state.reg[arg[1]] = state.mem[offset];

                    break;

                case 3://sw 011
                    iFormat(state.mem[state.pc], arg);
                    offset = arg[2] + state.reg[arg[0]];
                    state.mem[offset] = state.reg[arg[1]];

                    break;

                case 4: //beq 100
                    iFormat(state.mem[state.pc], arg);
                    regA = state.reg[arg[0]];
                    regB = state.reg[arg[1]];
                    if( regA == regB ) {
                        //System.out.println("BEFORE PC"+state.pc);
                        state.pc = state.pc + arg[2];
                        //System.out.println("AFTER PC"+state.pc);
                        // the +1 is handled by the regular increment.
                    }

                    break;

                case 5://jalr 101: เก็บค่า PC+1 ไว้ใน regB ซึ่ง PC คือ address ของ jalr instruction และกระโดดไปที่ address ที่ถูกเก็บไว้ใน regA แต่ถ้า regA และ regB คือ register ตัวเดียวกัน ให้เก็บ PC+1 ก่อน และค่อยกระโดดไปที่ PC+1
                    jFormat(state.mem[state.pc], arg);
                    regA = state.reg[arg[0]];
                    regB = state.reg[arg[1]];
                    if (regB == regA) {
                        //regB = state.pc; // เก็บค่า PC+1 ลงใน regB
                    } else {
                        state.reg[arg[1]] = state.pc+1; // เก็บค่า PC+1 ลงใน regB ก่อน
                    }
                    state.pc = regA;// กระโดดไปยัง address ที่ถูกเก็บไว้ใน regA
                    state.pc--;

                    break;

                case 6://bhalt : เพิ่มค่า PC เหมือน instructions อื่นๆ และ halt เครื่อง นั่นคือให้ simulator รู้ว่าเครื่องมีการ halted เกิดขึ้น
                    oFormat(state.mem[state.pc], arg);
                    i = -1;

                    break;

                case 7://noop
                    oFormat(state.mem[state.pc], arg);

                    break;
            }
            state.pc++;

            if( total > MAXLINELENGTH){
                i = -1;
                System.out.println("reached max length");
            }
        }
        System.out.println("machine halted\n" +
                "total of "+total+" instructions executed\n" +
                "final state of machine:");
        printState(state);
    }

    private static void rFormat(int bit, int[] arg){ //r-format
        arg[0] = (bit & (7 << 19 )) >> 19; // regA เอา bit ที่ 21-19
        arg[1] = (bit & (7 << 16 )) >> 16; // regB เอา bit ที่ 18-16
        arg[2] = bit & 7; // destReg เอา bit ที่ 2-0
    }
    private static void iFormat(int bit, int[] arg){
        arg[0] = (bit & (7 << 19 )) >> 19; // regA เอา bit ที่ 21-19
        arg[1] = (bit & (7 << 16 )) >> 16; // regB เอา bit ที่ 18-16
        arg[2] = bit & 0xFFFF;
        int temp = convertNum(arg[2]);
        arg[2] = temp;
        //System.out.println("LOWER"+arg[0]+":"+arg[1]+":"+arg[2]);
    }

    private static void jFormat(int bit, int[] arg){ //r-format
        arg[0] = (bit & (7 << 19 )) >> 19; // regA เอา bit ที่ 21-19
        arg[1] = (bit & (7 << 16 )) >> 16; // regB เอา bit ที่ 18-16
        arg[2] = bit & 0xFFFF; // destReg เอา bit ที่ 15-0
    }

    private static void oFormat(int bit, int[] arg) {
        arg[0] = bit & 0x3FFFFFF; // regA เอา 22 bit แรก (0x3FFFFFF คือ 1 22ตัว)
    }

    public static int convertNum(int num) {
        if ((num & (1 << 15)) != 0) {
            num -= (1 << 16);
        }
        return num;
    }
}