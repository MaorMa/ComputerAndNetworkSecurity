import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * This class responsible for decryption
 */
public class Decrypt {
    private String keyLocation;
    private String inputLocation;
    private String outputLocation;
    private LinkedList<byte[][]> byteMsg; //include all the message in format of [4][4] matrix
    private LinkedList<byte[][]> keys;//include all the keys in format of [4][4] matrix
    private byte[] plainText;

    public Decrypt(String keyLocation, String inputLocation, String outputLocation){
        this.keyLocation = keyLocation;
        this.inputLocation = inputLocation;
        this.outputLocation = outputLocation;
        this.keys = new LinkedList<>();
        this.byteMsg = new LinkedList<>();
        getMatrix(keyLocation);
        getMatrix(inputLocation);
        this.plainText = new byte[this.byteMsg.size()*16];
        this.AesDecryption();
    }


    /**
     * This method implement add round key scenario
     * @param key - given key
     * @param message - given message
     * result = message XOR key
     */
    public void addRoundKey(byte[][] key, byte[][] message) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                byte value = message[i][j];
                byte value2 = key[i][j];
                message[i][j] = (byte) (value ^ value2);
            }
        }
    }

    /**
     * This method decrypet using Aes algorithm 3 times
     * Using shiftRows and addRoundKey
     */
    private void AesDecryption(){
        //iterate over all the messages
        int index = 0;
        for(byte[][] msg : this.byteMsg){
            //iterate over all the keys
            for(int i=2 ; i>=0; i--){
                this.addRoundKey(this.keys.get(i),msg);
                this.shiftRows(msg); //shift rows
            }
            //setCypher
            for(int i=0;i<msg.length;i++){
                for(int j=0;j<msg.length;j++){
                    this.plainText[index] = msg[j][i];
                    index++;
                }
            }
        }
        writeToDisk();
    }

    /**
     * This method write plain text to disk
     */
    private void writeToDisk() {
        try {
            Files.write(Paths.get(this.outputLocation), this.plainText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method set byteMsg LinkedList and keys LinkedList
     * @param location - location of key / message
     */
    private void getMatrix(String location) {
        File file = new File(location);
        byte[][] array;
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            int startIndex = 0;
            for (int j = 1; j <= fileContent.length / 16; j++) {
                int endIndex = 16 * j;
                array = SwithDirection(fileContent, startIndex);
                startIndex = endIndex;
                //if inputLocation than add to byteMsg
                if (location.equals(this.inputLocation)) {
                    this.byteMsg.add(array);
                    //if keyLocation than add to keys
                } else {
                    this.keys.add(array);
                }
            }
        } catch (IOException e1) {
            //e1.printStackTrace();
        }
    }

    /**
     * This method get original byte array and switch direction into 4X4 matrix
     * @param fileContent - the original byte array
     * @param startIndex - if the message is more than 16 byte, thant start index will be at first 0, then 16, then 32 etc.
     * @return 4X4 inverted matrix
     */
    private static byte[][] SwithDirection(byte[] fileContent, int startIndex) {
        byte[][] messageMatrix = new byte[4][4];
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                messageMatrix[i][j] = fileContent[startIndex];
                startIndex++;
            }
        }
        return messageMatrix;
    }


    /**
     * This method implement shift rows scenario
     * @param message - to shift
     */
    public void shiftRows(byte[][] message) {
        //second row
        byte a = message[1][0];
        byte b = message[1][1];
        byte c = message[1][2];
        byte d = message[1][3];
        message[1][1] = a;
        message[1][2] = b;
        message[1][3] = c;
        message[1][0] = d;
        //third row
        a = message[2][0];
        b = message[2][1];
        c = message[2][2];
        d = message[2][3];
        message[2][2] = a;
        message[2][3] = b;
        message[2][0] = c;
        message[2][1] = d;
        //fourth row
        a = message[3][1];
        b = message[3][2];
        c = message[3][3];
        d = message[3][0];
        message[3][3] = d;
        message[3][0] = a;
        message[3][1] = b;
        message[3][2] = c;
    }

    /**
     * Internal use.
     * print our cypher text
     */
    private void printDecyper(){
        int index = 0;
        for(int i=0;i<this.plainText.length;i++){
            System.out.print(this.plainText[index] + ", ");
            index++;
        }
    }

    public static void main(String[] args) {
        new Decrypt("C:\\Users\\Maor\\Desktop\\files\\newkeys", "C:\\Users\\Maor\\Desktop\\files\\cipher_long", "C:\\Users\\Maor\\Desktop\\files\\decrypted_text");
    }
}
