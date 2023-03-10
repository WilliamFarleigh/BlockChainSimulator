import java.security.*;
import java.util.*;
public final class Miner extends Thread {
    private Wallet wallet;
    private boolean isMining;
    private int identifier;
    private int lastHashCode; 
    // check if two or more miners get this block
    private SortedSet<Wallet.Transaction> memPool; // is <TransactionFee, Transaction>
    private List<Miner> knownMiners;
    private boolean done;
    private BlockChain blockChain;
    private boolean breakOut;
    public Miner(Wallet wallet) {
        this(wallet, 1);
    }
    public Miner(Wallet wallet, double reward) {
        identifier = wallet.getIdentifier();
        System.out.println("Blockchain starting up...");
        System.out.println("Miner " + identifier +  " started up on their own BlockChain");
        this.memPool = new TreeSet<>();
        breakOut = false;
        this.knownMiners = new ArrayList<>();
        this.wallet = wallet;
        this.blockChain = new BlockChain(reward);
        this.isMining = true;
    }

    // have to make it so there is talking between the miners
    // a wallet will have to tell a miner
    public Miner(Wallet wallet, Miner otherMiner) {
        identifier = wallet.getIdentifier();
        knownMiners = new ArrayList<>(otherMiner.knownMiners);
        knownMiners.add(otherMiner);
        otherMiner.knownMiners.add(this);
        this.wallet = wallet;
        this.blockChain = otherMiner.blockChain.clone();
        isMining = true;
        System.out.println("Miner " + identifier +  " started up on Miner " + otherMiner.identifier + "'s BlockChain");
    }
    
    public <T> void consoleOutPut(T msg) {
        System.out.println("Miner " + this.identifier + ": " + msg.toString());
    }
    private synchronized static boolean addBlock(Miner prospecter, Block block) {
        for (Miner miner : prospecter.knownMiners) {
            if (prospecter.blockChain.getStoredBlocks().keySet().contains(block.hashCode())) {
                prospecter.blockChain = miner.blockChain.clone();
                return false;
            }
            for (Block minerBlock : miner.getBlockChain().getStoredBlocks().values()) {
                for (Wallet.Transaction transaction : minerBlock.getTransactions()) {
                    if (block.getTransactions().contains(transaction)) {
                        return false;
                    }
                }
            }
            if (block.getLastBlockHash() == (miner.blockChain.getLastBlock() != null? miner.blockChain.getLastBlock().hashCode() : 0)) {
                miner.blockChain.addBlock(block);
            } else {
                prospecter.blockChain = miner.blockChain.clone();
                return false;
            }
        }
        prospecter.blockChain.removeFromPool(block);
        prospecter.blockChain.addBlock(block);
        try {
        Thread.sleep(100);
        } catch (Exception e) {
            System.out.println(e);
        }
        prospecter.consoleOutPut(prospecter.blockChain.getAmountYouHave(prospecter.wallet));
        return true;
    }
    
    public void turnOn() {
        isMining = true;
        this.start();
    }
    public void breakOut() {
        breakOut = true;
    }
    public void shutOff() {
        isMining = false;
    }
    public void loadTransactions() {
        this.memPool = new TreeSet<>();
        for (Wallet.Transaction transact : this.blockChain.getStaticMemPool()) {
            this.memPool.add(transact);
        }
    }
    public BlockChain getBlockChain() {
        return this.blockChain;
    }
    public void run() {
        while (isMining) {
            loadTransactions();
            try {
                this.sleep(new Random().nextInt(800));
                HashSet<Wallet.Transaction> root = new HashSet<>();
                for (Wallet.Transaction transaction : this.memPool) {
                    String unDecrypted = Wallet.decryptMessageWithKey(transaction.getSignedMessage(),  transaction.getSender(), KeyGenerator.EncodeType.RSA);
                    try {
                        double unEncryptedAmt = Double.valueOf(unDecrypted);
                        if (unEncryptedAmt != transaction.getAmountSent()) {
                            throw new Exception();
                        }
                        double walletAmount = 0; 
                        for (Block block : this.blockChain.getStoredBlocks().values()) {
                            for (Wallet.Transaction transact : block.getTransactions()) {
                                if (transact.getSender() != null && transact.getSender().equals(transaction.getSender())) {
                                    walletAmount -= transact.getAmountSent();
                                } if (transact.getReciever().equals(transaction.getSender())) {
                                    walletAmount += transact.getAmountSent();
                                }


                            }
                            if (0 < walletAmount && walletAmount >= transaction.getAmountSent()) {
                                root.add(transaction);
                            }
                        }
                    } catch (Exception e) {
                         System.out.println(e);
                         consoleOutPut("Invalid transaction!");
                     }
                     if (breakOut) {
                        break;
                    }
                }
                if (breakOut) {
                    breakOut = false;
                    this.consoleOutPut("Returned to the loop.");
                    continue;
                }
                root.add(this.wallet.rewardAmount(this.blockChain.reward));
                Block block = new Block(root,  this.blockChain.getLastBlock() != null? this.blockChain.getLastBlock().hashCode() : 0);
                if (addBlock(this, block)) {
                    Thread.sleep(new Random().nextInt(100));
                }
            } catch (Exception e) {
                 consoleOutPut(e);
            }
        }
    }

}