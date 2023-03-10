import java.util.*;

@SuppressWarnings("unchecked")
public class BlockChain implements Cloneable {
    public HashMap<Integer, Block> storedBlocks;
    public static List<Wallet.Transaction> staticMemPool = Collections.synchronizedList(new ArrayList());
    private Block lastBlock;
    private int amountInCirculation;
    static double reward = 1;

    public BlockChain() {
        this(1);
    }
    public BlockChain(double reward) {
        amountInCirculation = 0;
        this.reward = reward;
        lastBlock = null;
        storedBlocks = new HashMap<>();
    }

    public synchronized void removeFromPool(Block block) {
        for (Wallet.Transaction transact: block.getTransactions()) {
            if (staticMemPool.contains(transact)) {
                staticMemPool.remove(transact);
            }
        }
    }
    public static void addTransactions(Wallet.Transaction transaction) {
        staticMemPool.add(transaction);
    }
    public void addBlock(Block block) {
        storedBlocks.put(block.hashCode(), block);
        lastBlock = block;
    }
    public Block getLastBlock() {
        return lastBlock;
    }
    public static List<Wallet.Transaction> getStaticMemPool() {
        return staticMemPool;
    }
    public String toString() {
        return this.storedBlocks.toString();
    }
    public HashMap<Integer, Block> getStoredBlocks() {
        return this.storedBlocks;
    }
    public double getAmountYouHave(Wallet wallet) {
        double walletAmount = 0; 
        for (Block block : this.getStoredBlocks().values()) {
            for (Wallet.Transaction transact : block.getTransactions()) {
                if (transact.getSender() != null && transact.getSender().equals(wallet.getPublicKey())) {
                    walletAmount -= transact.getAmountSent();
                } else if (transact.getReciever().equals(wallet.getPublicKey())) {
                    walletAmount += transact.getAmountSent();
                }
            }
        }
        return walletAmount;

    }

    @Override
    public BlockChain clone() {
        BlockChain newBlockChain = new BlockChain(reward);
        newBlockChain.storedBlocks = Util.copyHashMap(this.storedBlocks);
        newBlockChain.lastBlock = this.lastBlock == null ? null : this.lastBlock.clone();
        newBlockChain.amountInCirculation = this.amountInCirculation;
        return newBlockChain;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash = 31 * hash + Util.toHashCode(storedBlocks);
        return hash;
    }
}