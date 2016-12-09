
import java.net.*;
import java.io.*;
import java.math.*;
import java.util.*;

class Client {
    public static void main(String[] args) {
        new Client().run();
    }

    void run() {
        String serverName = "eitn41.eit.lth.se";
        int port = 1337;
        Random rnd = new Random();
        // the p shall be the one given in the manual
        BigInteger p = new BigInteger("1234567890abcdef", 16);
        BigInteger g = new BigInteger("2");

        try {
            Socket client = new Socket(serverName, port);
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // receive g**x1 and convert to a number
            String g_x1_str = in.readLine();
            System.out.println("g**x1: " + g_x1_str);
            BigInteger g_x1 = new BigInteger(g_x1_str, 16);

            // generate g**x2, x2 shall be a random number
            BigInteger x2 = new BigInteger("0");
            // calculate g**x2 mod p
            BigInteger g_x2 = g.modPow(x2, p);
            // convert to hex-string and send.
            out.println(g_x2.toString(16));
            // read the ack/nak. This should yield a nak due to x2 being 0
            System.out.println("\nsent g_x2: " + in.readLine());

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BigInteger kak = new BigInteger("ffffffffffffffffc90fdaa22168c234c4c6628b80dc1cd1" +
                "29024e088a67cc74020bbea63b139b22514a08798e3404dd" +
                "ef9519b3cd3a431b302b0a6df25f14374fe1356d6d51c245" +
                "e485b576625e7ec6f44c42e9a637ed6b0bff5cb6f406b7ed" +
                "ee386bfb5a899fa5ae9f24117c4b1fe649286651ece45b3d" +
                "c2007cb8a163bf0598da48361c55d39a69163fa8fd24cf5f" +
                "83655f23fca3ad91c62f356208552bb9ed529077096966d" +
                "670c354e4abc9804f1746c08ca237327ffffffffffffffff", 16);
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            int k = rand.nextInt();
            BigInteger b = BigInteger.valueOf(k);
            BigInteger inverse = b.modInverse(kak);

            if (b.multiply(inverse).mod(kak).compareTo(BigInteger.valueOf(1)) != 0) {
                System.out.println("Denna: " + b);
                System.out.println("fick inversen " + inverse);
                System.out.println("FEEEEL!");

            } else {

                System.out.println("rätt");
            }
        }

    }

    private BigInteger inverseMod(BigInteger a, BigInteger b) {
        BigInteger d;
        if (b == BigInteger.ZERO) {
            d = a;
            return d;
        }
        BigInteger x1 = new BigInteger("0");
        BigInteger x2 = new BigInteger("1");
        BigInteger y1 = new BigInteger("1");
        BigInteger y2 = new BigInteger("0");

        while (b.compareTo(BigInteger.ZERO) > 0) {

        }
        return BigInteger.ZERO;
    }
}
