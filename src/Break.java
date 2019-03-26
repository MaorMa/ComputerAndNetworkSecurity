import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.LinkedList;

/**
 * This class responsible for break the algorithm
 */
public class Break {
    private String cypherLocation;
    private String messageLocation;
    private String outputLocation;
    private LinkedList<byte[][]> cypher; //include all the message in format of [4][4] matrix
    private LinkedList<byte[][]> keys;//include all the keys in format of [4][4] matrix
    private LinkedList<byte[][]> byteMsg; //include all the message in format of [4][4] matrix
    private byte[] newKeys;


    public Break(String cypherLocation, String messageLocation, String outputLocation) {
        this.messageLocation = messageLocation;
        this.cypherLocation = cypherLocation;
        this.outputLocation = outputLocation;
        this.keys = new LinkedList<>();
        this.cypher = new LinkedList<>();
        this.byteMsg = new LinkedList<>();
        getMatrix(getRandomKey());
        getMatrix(getRandomKey());
        getMatrix(cypherLocation);
        getMatrix(messageLocation);
        AesBreak();
    }

    /**
     * This method create random key
     *
     * @return random key
     */
    public byte[] getRandomKey() {
        SecureRandom random = new SecureRandom();
        byte[] randomKey = new byte[16];
        random.nextBytes(randomKey);
        return randomKey;
    }


    /**
     * get matrix of cypher text
     * @param location
     */
    private void getMatrix(String location) {
        File file = new File(location);
        byte[][] array;
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int startIndex = 0;
        for (int j = 1; j <= fileContent.length / 16; j++) {
            int endIndex = 16 * j;
            array = SwithDirection(fileContent, startIndex);
            startIndex = endIndex;
            //if inputLocation than add to byteMsg
            if (location.equals(this.cypherLocation)) {
                this.cypher.add(array);
                //if keyLocation than add to keys
            }
            if (location.equals(this.messageLocation)) {
                this.byteMsg.add(array);
            }
        }
    }

    /**
     * This method set byteMsg LinkedList and keys LinkedList
     */
    private void getMatrix(byte[] key) {
        byte[][] array;
        int startIndex = 0;
        for (int j = 1; j <= key.length / 16; j++) {
            int endIndex = 16 * j;
            array = SwithDirection(key, startIndex);
            startIndex = endIndex;
            this.keys.add(array);
        }
    }


    /**
     * This method implement add round key scenario
     *
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
     * This method get original byte array and switch direction into 4X4 matrix
     *
     * @param fileContent - the original byte array
     * @param startIndex  - if the message is more than 16 byte, thant start index will be at first 0, then 16, then 32 etc.
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
     *
     * @param message - to shift
     */
    public void regulerShiftRows(byte[][] message) {
        //second row
        byte a = message[1][0];
        byte b = message[1][1];
        byte c = message[1][2];
        byte d = message[1][3];
        message[1][0] = b;
        message[1][1] = c;
        message[1][2] = d;
        message[1][3] = a;
        //third row
        a = message[2][0];
        b = message[2][1];
        c = message[2][2];
        d = message[2][3];
        message[2][0] = c;
        message[2][1] = d;
        message[2][2] = a;
        message[2][3] = b;
        //fourth row
        a = message[3][1];
        b = message[3][2];
        c = message[3][3];
        d = message[3][0];
        message[3][0] = c;
        message[3][1] = d;
        message[3][2] = a;
        message[3][3] = b;
    }

    /**
     * This method implement shift rows scenario
     *
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
     * This method decrypet using Aes algorithm 3 times
     * Using shiftRows and addRoundKey
     */
    private void AesBreak() {
        int index;
        for(byte[][] msg : this.byteMsg) {
            //iterate over all the keys
            for (byte[][] key : this.keys) {
                this.regulerShiftRows(msg); //shif rows
                this.addRoundKey(key, msg);
            }
        }

        for (byte[][] msg : this.byteMsg) {
            this.regulerShiftRows(msg);
        }

        for (byte[][] msg : this.byteMsg) {
            for(byte[][] cyp: this.cypher) {
                this.addRoundKey(cyp, msg);
            }
        }

        this.keys.add(this.byteMsg.get(0));
        //setCypher
        index = 0;
        this.newKeys = new byte[16*3];
        for(byte[][]key:keys) {
            for (int i = 0; i < key.length; i++) {
                for (int j = 0; j < key[0].length; j++) {
                    this.newKeys[index] = key[j][i];
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
            Files.write(Paths.get(this.outputLocation), this.newKeys);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
       Break b =  new Break("C:\\Users\\Maor\\Desktop\\files\\cipher_long","C:\\Users\\Maor\\Desktop\\files\\message_long", "C:\\Users\\Maor\\Desktop\\files\\newkeys");
    }

}
