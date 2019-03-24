/**
 * main class - Interface
 */
public class main {
    public static void main(String[] args){
        /**
         * args
         */
        String encryptDycryptBreak = args[0];
        String messageOrKey = args[1];
        String messageOrKeyPath = args[2];
        String iOrC = args[3];
        String path = args[4];
        String o = args[5];
        String outputPath = args[6];


        /**
         * Conditions
         */
        //if encrypt
        if(encryptDycryptBreak.equals("-e")){ //encrypt
            new Encrypt(messageOrKeyPath,path,outputPath);
        }else if(encryptDycryptBreak.equals("-d")){ //decrypt
            new Decrypt(messageOrKeyPath,path,outputPath);
        }else{ //break
            new Break(path,messageOrKeyPath,outputPath);
        }
    }
}
