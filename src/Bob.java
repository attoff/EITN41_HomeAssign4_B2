import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;

public class Bob {
    private String serverName;
    private int port;
    private BigInteger key;

    private BigInteger p;
    private BigInteger x2;
    private BigInteger b_3;
    private BigInteger g_1;
    private BigInteger g_2;
    private BigInteger g_3;
    private BigInteger P_a;
    private BigInteger P_b;
    private BigInteger Q_a;
    private BigInteger Q_b;
    private BigInteger R_a;
    private BigInteger R_b;
    private BigInteger R_ab;
    private Random rand;

    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    public Bob() {
        serverName = "eitn41.eit.lth.se";
        port = 1337;
        p = new BigInteger("ffffffffffffffffc90fdaa22168c234c4c6628b80dc1cd1" +
                "29024e088a67cc74020bbea63b139b22514a08798e3404dd" +
                "ef9519b3cd3a431b302b0a6df25f14374fe1356d6d51c245" +
                "e485b576625e7ec6f44c42e9a637ed6b0bff5cb6f406b7ed" +
                "ee386bfb5a899fa5ae9f24117c4b1fe649286651ece45b3d" +
                "c2007cb8a163bf0598da48361c55d39a69163fa8fd24cf5f" +
                "83655f23fca3ad91c62f356208552bb9ed529077096966d" +
                "670c354e4abc9804f1746c08ca237327ffffffffffffffff", 16);

        g_1 = new BigInteger("2");
        rand = new Random();
    }

    public void setupConnection() {
        try {
            client = new Socket(serverName, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("Connection to server accomplished");
        } catch (Exception e) {
            System.out.println("Connection Error");
        }
    }

    public BigInteger negotiateKey() {


        String g_x1_str = null;
        try {
            //retrieve g_x1
            g_x1_str = in.readLine();

            System.out.println("g^x_1 from server: " + g_x1_str);
            //convert it into a number.
            BigInteger g_x1 = new BigInteger(g_x1_str, 16);

            // generate g**x2, x2 shall be a random number
            x2 = p.multiply(BigInteger.valueOf(rand.nextInt(10) + 1));
            // calculate g**x2 mod p
            BigInteger g_x2 = g_1.modPow(x2, p);
            // convert to hex-string and send.
            System.out.println("g^x_2 to server: " + g_x2.toString(16));
            out.println(g_x2.toString(16));
            // read the ack/nak.
            String ack = in.readLine();

            if (ack.compareTo("ack") == 0) {
                key = g_x1.modPow(x2, p);
            }
        } catch (IOException e) {
            System.out.println("Error with retrieving correct response from server. Please try again.");
            e.printStackTrace();
        }

        System.out.println("g^xy or 'shared key' is: " + key);
        return key;
    }

    public void startNegotiatingGenerators() {
        try {
            //retrieve g_1^a_2 from server and do it into a number
            String g_2a_str = in.readLine();
            BigInteger g_2a = new BigInteger(g_2a_str, 16);

            //create random number b_2 and create g_2b
            BigInteger b_2 = p.multiply(BigInteger.valueOf(rand.nextInt(10) + 1));
            BigInteger g_2b = g_1.modPow(b_2, p);

            //send g_2b to Alice.
            out.println(g_2b.toString(16));

            String response = in.readLine();
            if (response.compareTo("ack") != 0) {
                System.out.println("Something wrong with a_2 and b_2 exchange.");
                System.exit(0);
            } else {
                g_2 = g_2a.modPow(b_2, p);
                System.out.println("Second generator created.");
                System.out.println(g_2.toString(16));
            }

            //retrieve g_3a and convert to number
            String g_3a_str = in.readLine();
            BigInteger g_3a = new BigInteger(g_3a_str, 16);

            //create g_3b and send to Alice
            b_3 = p.multiply(BigInteger.valueOf(rand.nextInt(10) + 1));
            BigInteger g_3b = g_1.modPow(b_3, p);
            out.println(g_3b.toString(16));

            response = in.readLine();
            if (response.compareTo("ack") != 0) {
                System.out.println("Something wrong with a_3 and b_3 exchange.");
                System.exit(0);
            } else {
                g_3 = g_3a.modPow(b_3, p);
                System.out.println("Third generator created.");
                System.out.println(g_3.toString(16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startNegotiatingPandQ() {
        try {
            //get P_a from Alice
            String P_a_str = in.readLine();
            P_a = new BigInteger(P_a_str, 16);

            //create P_b and send.
            BigInteger r = p.multiply(BigInteger.valueOf(rand.nextInt(10) + 1));
            P_b = g_3.modPow(r, p);
            out.println(P_b.toString(16));

            String response = in.readLine();
            if (response.compareTo("ack") != 0) {
                System.out.println("Problem with agreeing on P_a and P_b");
                System.exit(0);
            } else {
                System.out.println("P_b was ack'ed by the server");
            }

            //retrieve Q_a
            String Q_a_str = in.readLine();
            Q_a = new BigInteger(Q_a_str, 16);
            System.out.println("Q_a is received from server.");

            //KANSKE PROBLEM
            Q_b = (g_1.modPow(r, p).multiply(g_2.modPow(x2, p)));
            out.println(Q_b.toString(16));

            response = in.readLine();
            if (response.compareTo("ack") != 0) {
                System.out.println("Problem with agreeing on Q_a and Q_b");
                System.exit(0);
            } else {
                System.out.println("Q_b was ack'ed by the server");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startComputingR() {

        try {
            String R_a_str = in.readLine();
            R_a = new BigInteger(R_a_str, 16);
            System.out.println("R_a reveived from server");
            System.out.println(R_a.toString(16));

            //KANSKE PROBLEM
            R_b = (Q_a.multiply(Q_b.modInverse(p))).modPow(b_3, p);
            out.println(R_b.toString(16));
            System.out.println(R_b.toString(16));

            String response = in.readLine();
            if (response.compareTo("ack") != 0) {
                System.out.println("Problem with exchanging R-values");
                System.exit(0);
            } else {
                System.out.println("R_b was ack'ed by the server");
            }

            R_ab = R_a.modPow(b_3, p);
            System.out.println(R_ab.toString(16));
            System.out.println((P_a.multiply(P_b.modInverse(p))).mod(p).toString(16));

            String authentication = in.readLine();
            System.out.println("Authentication " + authentication);


            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}