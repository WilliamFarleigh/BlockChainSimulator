import java.util.*;
public final class Block implements Cloneable {
    private final HashSet<Wallet.Transaction> transactions;
    //private long timeStamp;
    private final int lastBlockHash;

    public Block(HashSet<Wallet.Transaction> transactions,  int lastBlockHash) {
        // RESETS THE TRANSFERS
        this.lastBlockHash = lastBlockHash;
        this.transactions = transactions;
    }
    @Override
    public Block clone() {
        HashSet<Wallet.Transaction> copy = new HashSet<>(transactions);
        return new Block(copy, this.lastBlockHash);
    }
    public HashSet<Wallet.Transaction> getTransactions() {
        return this.transactions;
    }
    public int getLastBlockHash() {
        return this.lastBlockHash;
    }
    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + lastBlockHash;
        HashSet<Wallet.Transaction> transacts = new HashSet<Wallet.Transaction>();
        for (Wallet.Transaction transaction : transactions) {
            if (transaction.getSender() != null) {
                transacts.add(transaction);
            }
        }
        hash = 31 * hash + Util.toHashCode(transacts);
        return hash;
    }
}