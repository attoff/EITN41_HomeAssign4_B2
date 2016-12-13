


public class Main {

    public static void main(String[] args) {


        Bob bob = new Bob();

        bob.setupConnection();
        bob.negotiateKey();
        bob.startNegotiatingGenerators();
        bob.startNegotiatingPandQ();
        bob.startComputingR();
        bob.sendMessage("1337");

    }
}
