import java.util.*;
@SuppressWarnings("unchecked")
public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static <T> T input(String input) {
        System.out.print(input);
        return (T) scanner.nextLine().trim();
    }
    public static void main(String[] args)  {
        String name = input("name the currency: ");
        double rewardAmount = -1;
        while (rewardAmount == -1) {
            try {
                rewardAmount = Double.valueOf(input("reward amount for mining: "));
            } catch (Exception e) {
                System.out.println("that is not an valid input (double needed)!");
            }
        }
        Wallet wallet = new Wallet();
        Wallet walletTwo = new Wallet();
        Wallet walletThree = new Wallet();
        wallet.transfer(0, walletTwo.getPublicKey());
        Miner miner = new Miner(wallet, rewardAmount);
        Miner miner2 = new Miner(walletTwo, miner);
        Miner miner3 = new Miner(walletThree, miner2);
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            miner.consoleOutPut(e);
        }
        miner.turnOn();
        miner2.turnOn();
        miner3.turnOn();
        Wallet[] wallets = new Wallet[]{wallet, walletTwo, walletThree};
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                miner.consoleOutPut(e);
            }
            Wallet sender, reciever;
            do {
                sender = wallets[new Random().nextInt(3)];
                reciever = wallets[new Random().nextInt(3)];
            } while (sender.equals(reciever));
            int amt = new Random().nextInt((int) miner.getBlockChain().getAmountYouHave(sender)+1);
            sender.transfer(amt, reciever.getPublicKey());
            System.out.printf("%s has started a transfer of %d %s to %s%n", sender, amt, name, reciever);
        }

    }
}